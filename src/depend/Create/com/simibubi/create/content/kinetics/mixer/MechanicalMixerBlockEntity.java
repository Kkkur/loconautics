/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.ShapedRecipe
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.content.kinetics.mixer;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.potion.PotionMixingRecipes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class MechanicalMixerBlockEntity
extends BasinOperatingBlockEntity {
    private static final Object shapelessOrMixingRecipesKey = new Object();
    public int runningTicks;
    public int processingTicks;
    public boolean running;

    public MechanicalMixerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public float getRenderedHeadOffset(float partialTicks) {
        float offset = 0.0f;
        if (this.running) {
            if (this.runningTicks < 20) {
                int localTick = this.runningTicks;
                float num = ((float)localTick + partialTicks) / 20.0f;
                num = (2.0f - Mth.cos((float)((float)((double)num * Math.PI)))) / 2.0f;
                offset = num - 0.5f;
            } else if (this.runningTicks <= 20) {
                offset = 1.0f;
            } else {
                int localTick = 40 - this.runningTicks;
                float num = ((float)localTick - partialTicks) / 20.0f;
                num = (2.0f - Mth.cos((float)((float)((double)num * Math.PI)))) / 2.0f;
                offset = num - 0.5f;
            }
        }
        return offset + 0.4375f;
    }

    public float getRenderedHeadRotationSpeed(float partialTicks) {
        float speed = this.getSpeed();
        if (this.running) {
            if (this.runningTicks < 15) {
                return speed;
            }
            if (this.runningTicks <= 20) {
                return speed * 2.0f;
            }
            return speed;
        }
        return speed / 2.0f;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.registerAwardables(behaviours, AllAdvancements.MIXER);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition).expandTowards(0.0, -1.5, 0.0);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.running = compound.getBoolean("Running");
        this.runningTicks = compound.getInt("Ticks");
        super.read(compound, registries, clientPacket);
        if (clientPacket && this.hasLevel()) {
            this.getBasin().ifPresent(bte -> bte.setAreFluidsMoving(this.running && this.runningTicks <= 20));
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putBoolean("Running", this.running);
        compound.putInt("Ticks", this.runningTicks);
        super.write(compound, registries, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.runningTicks >= 40) {
            this.running = false;
            this.runningTicks = 0;
            this.basinChecker.scheduleUpdate();
            return;
        }
        float speed = Math.abs(this.getSpeed());
        if (this.running && this.level != null) {
            if (this.level.isClientSide && this.runningTicks == 20) {
                this.renderParticles();
            }
            if (this.getSpeed() == 0.0f || !this.isSpeedRequirementFulfilled()) {
                if (this.runningTicks < 20) {
                    this.runningTicks = 40 - this.runningTicks;
                } else if (this.runningTicks == 20) {
                    ++this.runningTicks;
                }
            }
            if ((!this.level.isClientSide || this.isVirtual()) && this.runningTicks == 20) {
                if (this.processingTicks < 0) {
                    Couple<SmartFluidTankBehaviour> tanks;
                    int t;
                    float recipeSpeed = 1.0f;
                    if (this.currentRecipe instanceof StandardProcessingRecipe && (t = ((StandardProcessingRecipe)this.currentRecipe).getProcessingDuration()) != 0) {
                        recipeSpeed = (float)t / 100.0f;
                    }
                    this.processingTicks = Math.max(Mth.log2((int)((int)(512.0f / speed))) * Mth.ceil((float)(recipeSpeed * 15.0f)) + 1, 1);
                    Optional<BasinBlockEntity> basin = this.getBasin();
                    if (!(!basin.isPresent() || ((SmartFluidTankBehaviour)(tanks = basin.get().getTanks()).getFirst()).isEmpty() && ((SmartFluidTankBehaviour)tanks.getSecond()).isEmpty())) {
                        this.level.playSound(null, this.worldPosition, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.75f, speed < 65.0f ? 0.75f : 1.5f);
                    }
                } else {
                    --this.processingTicks;
                    if (this.processingTicks == 0) {
                        ++this.runningTicks;
                        this.processingTicks = -1;
                        this.applyBasinRecipe();
                        this.sendData();
                    }
                }
            }
            if (this.runningTicks != 20) {
                ++this.runningTicks;
            }
        }
    }

    public void renderParticles() {
        Optional<BasinBlockEntity> basin = this.getBasin();
        if (!basin.isPresent() || this.level == null) {
            return;
        }
        for (SmartInventory inv : basin.get().getInvs()) {
            for (int slot = 0; slot < inv.getSlots(); ++slot) {
                ItemStack stackInSlot = inv.getItem(slot);
                if (stackInSlot.isEmpty()) continue;
                ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
                this.spillParticle((ParticleOptions)data);
            }
        }
        for (SmartFluidTankBehaviour behaviour : basin.get().getTanks()) {
            if (behaviour == null) continue;
            for (SmartFluidTankBehaviour.TankSegment tankSegment : behaviour.getTanks()) {
                if (tankSegment.isEmpty(0.0f)) continue;
                this.spillParticle(FluidFX.getFluidParticle(tankSegment.getRenderedFluid()));
            }
        }
    }

    protected void spillParticle(ParticleOptions data) {
        float angle = this.level.random.nextFloat() * 360.0f;
        Vec3 offset = new Vec3(0.0, 0.0, 0.25);
        offset = VecHelper.rotate((Vec3)offset, (double)angle, (Direction.Axis)Direction.Axis.Y);
        Vec3 target = VecHelper.rotate((Vec3)offset, (double)(this.getSpeed() > 0.0f ? 25.0 : -25.0), (Direction.Axis)Direction.Axis.Y).add(0.0, 0.25, 0.0);
        Vec3 center = offset.add(VecHelper.getCenterOf((Vec3i)this.worldPosition));
        target = VecHelper.offsetRandomly((Vec3)target.subtract(offset), (RandomSource)this.level.random, (float)0.0078125f);
        this.level.addParticle(data, center.x, center.y - 1.75, center.z, target.x, target.y, target.z);
    }

    @Override
    protected List<Recipe<?>> getMatchingRecipes() {
        List<Recipe<?>> matchingRecipes = super.getMatchingRecipes();
        if (!((Boolean)AllConfigs.server().recipes.allowBrewingInMixer.get()).booleanValue()) {
            return matchingRecipes;
        }
        Optional<BasinBlockEntity> basin = this.getBasin();
        if (!basin.isPresent()) {
            return matchingRecipes;
        }
        BasinBlockEntity basinBlockEntity = basin.get();
        if (basin.isEmpty()) {
            return matchingRecipes;
        }
        IItemHandler availableItems = (IItemHandler)this.level.getCapability(Capabilities.ItemHandler.BLOCK, basinBlockEntity.getBlockPos(), null);
        if (availableItems == null) {
            return matchingRecipes;
        }
        for (int i = 0; i < availableItems.getSlots(); ++i) {
            List<MixingRecipe> list;
            ItemStack stack = availableItems.getStackInSlot(i);
            if (stack.isEmpty() || (list = PotionMixingRecipes.sortRecipesByItem(this.level).get(stack.getItem())) == null) continue;
            for (MixingRecipe mixingRecipe : list) {
                if (!this.matchBasinRecipe(mixingRecipe)) continue;
                matchingRecipes.add(mixingRecipe);
            }
        }
        return matchingRecipes;
    }

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        Recipe r = recipe.value();
        return r instanceof CraftingRecipe && !(r instanceof ShapedRecipe) && (Boolean)AllConfigs.server().recipes.allowShapelessInMixer.get() != false && r.getIngredients().size() > 1 && !MechanicalPressBlockEntity.canCompress(r) && !AllRecipeTypes.shouldIgnoreInAutomation(recipe) || r.getType() == AllRecipeTypes.MIXING.getType();
    }

    @Override
    public void startProcessingBasin() {
        if (this.running && this.runningTicks <= 20) {
            return;
        }
        super.startProcessingBasin();
        this.running = true;
        this.runningTicks = 0;
    }

    @Override
    public boolean continueWithPreviousRecipe() {
        this.runningTicks = 20;
        return true;
    }

    @Override
    protected void onBasinRemoved() {
        if (!this.running) {
            return;
        }
        this.runningTicks = 40;
        this.running = false;
    }

    @Override
    protected Object getRecipeCacheKey() {
        return shapelessOrMixingRecipesKey;
    }

    @Override
    protected boolean isRunning() {
        return this.running;
    }

    @Override
    protected Optional<CreateAdvancement> getProcessedRecipeTrigger() {
        return Optional.of(AllAdvancements.MIXER);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void tickAudio() {
        boolean slow;
        super.tickAudio();
        boolean bl = slow = Math.abs(this.getSpeed()) < 65.0f;
        if (slow && AnimationTickHolder.getTicks() % 2 == 0) {
            return;
        }
        if (this.runningTicks == 20) {
            AllSoundEvents.MIXING.playAt(this.level, (Vec3i)this.worldPosition, 0.75f, 1.0f, true);
        }
    }
}
