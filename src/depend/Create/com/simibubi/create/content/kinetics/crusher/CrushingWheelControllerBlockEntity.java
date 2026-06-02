/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.wrapper.RecipeWrapper
 */
package com.simibubi.create.content.kinetics.crusher;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.sound.SoundScapes;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class CrushingWheelControllerBlockEntity
extends SmartBlockEntity
implements Clearable {
    public Entity processingEntity;
    private UUID entityUUID;
    protected boolean searchForEntity;
    public ProcessingInventory inventory = new ProcessingInventory(this::itemInserted){

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return super.isItemValid(slot, stack) && CrushingWheelControllerBlockEntity.this.processingEntity == null;
        }
    };
    private RecipeWrapper wrapper = new RecipeWrapper((IItemHandler)this.inventory);
    public float crushingspeed;

    public CrushingWheelControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.CRUSHING_WHEEL_CONTROLLER.get(), (be, context) -> be.inventory);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen(this::supportsDirectBeltInput));
    }

    private boolean supportsDirectBeltInput(Direction side) {
        BlockState blockState = this.getBlockState();
        if (blockState == null) {
            return false;
        }
        Direction direction = (Direction)blockState.getValue((Property)CrushingWheelControllerBlock.FACING);
        return direction == Direction.DOWN || direction == side;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.searchForEntity) {
            this.searchForEntity = false;
            List search = this.level.getEntities((Entity)null, new AABB(this.getBlockPos()), e -> this.entityUUID.equals(e.getUUID()));
            if (search.isEmpty()) {
                this.clear();
            } else {
                this.processingEntity = (Entity)search.get(0);
            }
        }
        if (!this.isOccupied()) {
            return;
        }
        if (this.crushingspeed == 0.0f) {
            return;
        }
        if (this.level.isClientSide) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.tickAudio());
        }
        float speed = this.crushingspeed * 4.0f;
        Vec3 centerPos = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        Direction facing = (Direction)this.getBlockState().getValue((Property)CrushingWheelControllerBlock.FACING);
        int offset = facing.getAxisDirection().getStep();
        Vec3 outSpeed = new Vec3((facing.getAxis() == Direction.Axis.X ? 0.25 : 0.0) * (double)offset, offset == 1 ? (facing.getAxis() == Direction.Axis.Y ? 0.5 : 0.0) : 0.0, (facing.getAxis() == Direction.Axis.Z ? 0.25 : 0.0) * (double)offset);
        Vec3 outPos = centerPos.add((double)(facing.getAxis() == Direction.Axis.X ? 0.55f * (float)offset : 0.0f), (double)(facing.getAxis() == Direction.Axis.Y ? 0.55f * (float)offset : 0.0f), (double)(facing.getAxis() == Direction.Axis.Z ? 0.55f * (float)offset : 0.0f));
        if (!this.hasEntity()) {
            BlockPos nextPos;
            DirectBeltInputBehaviour behaviour;
            float processingSpeed = Mth.clamp((float)(speed / (!this.inventory.appliedRecipe ? (float)Math.log(this.inventory.getStackInSlot(0).getCount()) / (float)Math.log(2.0) : 1.0f)), (float)0.25f, (float)20.0f);
            this.inventory.remainingTime -= processingSpeed;
            this.spawnParticles(this.inventory.getStackInSlot(0));
            if (this.level.isClientSide) {
                return;
            }
            if (this.inventory.remainingTime < 20.0f && !this.inventory.appliedRecipe) {
                this.applyRecipe();
                this.inventory.appliedRecipe = true;
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 18);
                return;
            }
            if (this.inventory.remainingTime > 0.0f) {
                return;
            }
            this.inventory.remainingTime = 0.0f;
            if (facing != Direction.UP && (behaviour = BlockEntityBehaviour.get((BlockGetter)this.level, nextPos = this.worldPosition.below().relative(facing, facing.getAxis() == Direction.Axis.Y ? 0 : 1), DirectBeltInputBehaviour.TYPE)) != null) {
                boolean changed = false;
                if (!behaviour.canInsertFromSide(facing)) {
                    return;
                }
                for (int slot = 0; slot < this.inventory.getSlots(); ++slot) {
                    ItemStack remainder;
                    ItemStack stack = this.inventory.getStackInSlot(slot);
                    if (stack.isEmpty() || ItemStack.matches((ItemStack)(remainder = behaviour.handleInsertion(stack, facing, false)), (ItemStack)stack)) continue;
                    this.inventory.setStackInSlot(slot, remainder);
                    changed = true;
                }
                if (changed) {
                    this.setChanged();
                    this.sendData();
                }
                return;
            }
            for (int slot = 0; slot < this.inventory.getSlots(); ++slot) {
                ItemStack stack = this.inventory.getStackInSlot(slot);
                if (stack.isEmpty()) continue;
                ItemEntity entityIn = new ItemEntity(this.level, outPos.x, outPos.y, outPos.z, stack);
                entityIn.setDeltaMovement(outSpeed);
                entityIn.getPersistentData().put("BypassCrushingWheel", NbtUtils.writeBlockPos((BlockPos)this.worldPosition));
                this.level.addFreshEntity((Entity)entityIn);
            }
            this.inventory.clear();
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 18);
            return;
        }
        if (!this.processingEntity.isAlive() || !this.processingEntity.getBoundingBox().intersects(new AABB(this.worldPosition).inflate(0.5))) {
            this.clear();
            return;
        }
        double xMotion = ((double)((float)this.worldPosition.getX() + 0.5f) - this.processingEntity.getX()) / 2.0;
        double zMotion = ((double)((float)this.worldPosition.getZ() + 0.5f) - this.processingEntity.getZ()) / 2.0;
        if (this.processingEntity.isShiftKeyDown()) {
            zMotion = 0.0;
            xMotion = 0.0;
        }
        double movement = Math.max(-speed / 4.0f, -0.5f) * (float)(-offset);
        this.processingEntity.setDeltaMovement(new Vec3(facing.getAxis() == Direction.Axis.X ? movement : xMotion, facing.getAxis() == Direction.Axis.Y ? movement : 0.0, facing.getAxis() == Direction.Axis.Z ? movement : zMotion));
        if (this.level.isClientSide) {
            return;
        }
        Entity entity = this.processingEntity;
        if (!(entity instanceof ItemEntity)) {
            LivingEntity livingEntity;
            Vec3 entityOutPos = outPos.add(facing.getAxis() == Direction.Axis.X ? (double)(0.5f * (float)offset) : 0.0, facing.getAxis() == Direction.Axis.Y ? (double)(0.5f * (float)offset) : 0.0, facing.getAxis() == Direction.Axis.Z ? (double)(0.5f * (float)offset) : 0.0);
            int crusherDamage = (Integer)AllConfigs.server().kinetics.crushingDamage.get();
            Entity entity2 = this.processingEntity;
            if (entity2 instanceof LivingEntity && (livingEntity = (LivingEntity)entity2).getHealth() - (float)crusherDamage <= 0.0f && livingEntity.hurtTime <= 0) {
                this.processingEntity.setPos(entityOutPos.x, entityOutPos.y, entityOutPos.z);
            }
            this.processingEntity.hurt(CreateDamageSources.crush(this.level), (float)crusherDamage);
            if (!this.processingEntity.isAlive()) {
                this.processingEntity.setPos(entityOutPos.x, entityOutPos.y, entityOutPos.z);
            }
            return;
        }
        ItemEntity itemEntity = (ItemEntity)entity;
        itemEntity.setPickUpDelay(20);
        if (facing.getAxis() == Direction.Axis.Y) {
            if (this.processingEntity.getY() * (double)(-offset) < (centerPos.y - 0.25) * (double)(-offset)) {
                this.intakeItem(itemEntity);
            }
        } else if (facing.getAxis() == Direction.Axis.Z) {
            if (this.processingEntity.getZ() * (double)(-offset) < (centerPos.z - 0.25) * (double)(-offset)) {
                this.intakeItem(itemEntity);
            }
        } else if (this.processingEntity.getX() * (double)(-offset) < (centerPos.x - 0.25) * (double)(-offset)) {
            this.intakeItem(itemEntity);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public void tickAudio() {
        float pitch = Mth.clamp((float)(this.crushingspeed / 256.0f + 0.45f), (float)0.85f, (float)1.0f);
        if (this.entityUUID == null && this.inventory.getStackInSlot(0).isEmpty()) {
            return;
        }
        SoundScapes.play(SoundScapes.AmbienceGroup.CRUSHING, this.worldPosition, pitch);
    }

    private void intakeItem(ItemEntity itemEntity) {
        this.inventory.clear();
        this.inventory.setStackInSlot(0, itemEntity.getItem().copy());
        this.itemInserted(this.inventory.getStackInSlot(0));
        itemEntity.discard();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 18);
    }

    protected void spawnParticles(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        Object particleData = null;
        particleData = stack.getItem() instanceof BlockItem ? new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem)stack.getItem()).getBlock().defaultBlockState()) : new ItemParticleOption(ParticleTypes.ITEM, stack);
        RandomSource r = this.level.random;
        for (int i = 0; i < 4; ++i) {
            this.level.addParticle((ParticleOptions)particleData, (double)((float)this.worldPosition.getX() + r.nextFloat()), (double)((float)this.worldPosition.getY() + r.nextFloat()), (double)((float)this.worldPosition.getZ() + r.nextFloat()), 0.0, 0.0, 0.0);
        }
    }

    private void applyRecipe() {
        Optional<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>> recipe = this.findRecipe();
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        if (recipe.isPresent()) {
            ItemStack input = this.inventory.getStackInSlot(0);
            int rolls = input.getCount();
            this.inventory.clear();
            for (int roll = 0; roll < rolls; ++roll) {
                List<ItemStack> rolledResults = ((StandardProcessingRecipe)recipe.get().value()).rollResults(this.level.random);
                for (ItemStack stack : rolledResults) {
                    ItemHelper.addToList(stack, list);
                }
            }
            if (input.hasCraftingRemainingItem()) {
                ItemHelper.addToList(input.getCraftingRemainingItem(), list);
            }
            for (int slot = 0; slot < list.size() && slot + 1 < this.inventory.getSlots(); ++slot) {
                this.inventory.setStackInSlot(slot + 1, (ItemStack)list.get(slot));
            }
        } else {
            this.inventory.clear();
        }
    }

    public Optional<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>> findRecipe() {
        Optional<Object> crushingRecipe = AllRecipeTypes.CRUSHING.find(this.wrapper, this.level);
        if (!crushingRecipe.isPresent()) {
            crushingRecipe = AllRecipeTypes.MILLING.find(this.wrapper, this.level);
        }
        return crushingRecipe;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.hasEntity()) {
            compound.put("Entity", (Tag)NbtUtils.createUUID((UUID)this.entityUUID));
        }
        compound.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
        compound.putFloat("Speed", this.crushingspeed);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (compound.contains("Entity") && !this.isOccupied()) {
            this.entityUUID = NbtUtils.loadUUID((Tag)NBTHelper.getINBT((CompoundTag)compound, (String)"Entity"));
            this.searchForEntity = true;
        }
        this.crushingspeed = compound.getFloat("Speed");
        this.inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
    }

    public void clearContent() {
        this.inventory.clear();
    }

    public void startCrushing(Entity entity) {
        this.processingEntity = entity;
        this.entityUUID = entity.getUUID();
    }

    private void itemInserted(ItemStack stack) {
        Optional<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>> recipe = this.findRecipe();
        this.inventory.remainingTime = recipe.isPresent() ? (float)((StandardProcessingRecipe)recipe.get().value()).getProcessingDuration() : 100.0f;
        this.inventory.appliedRecipe = false;
    }

    public void clear() {
        this.processingEntity = null;
        this.entityUUID = null;
    }

    public boolean isOccupied() {
        return this.hasEntity() || !this.inventory.isEmpty();
    }

    public boolean hasEntity() {
        return this.processingEntity != null;
    }
}
