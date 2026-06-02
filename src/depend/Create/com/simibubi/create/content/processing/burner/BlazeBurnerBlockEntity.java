/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.processing.burner;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.data.datamaps.BlazeBurnerFuel;
import com.simibubi.create.api.registry.CreateDataMaps;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class BlazeBurnerBlockEntity
extends SmartBlockEntity {
    public static final int MAX_HEAT_CAPACITY = 10000;
    public static final int INSERTION_THRESHOLD = 500;
    public LerpedFloat headAnimation;
    public boolean stockKeeper = false;
    public boolean isCreative = false;
    public boolean goggles = false;
    public boolean hat;
    protected FuelType activeFuel = FuelType.NONE;
    protected int remainingBurnTime = 0;
    protected LerpedFloat headAngle;

    public BlazeBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.headAnimation = LerpedFloat.linear();
        this.headAngle = LerpedFloat.angular();
        this.headAngle.startWithValue((double)((AngleHelper.horizontalAngle((Direction)state.getOptionalValue((Property)BlazeBurnerBlock.FACING).orElse(Direction.SOUTH)) + 180.0f) % 360.0f));
    }

    public FuelType getActiveFuel() {
        return this.activeFuel;
    }

    public int getRemainingBurnTime() {
        return this.remainingBurnTime;
    }

    public boolean isCreative() {
        return this.isCreative;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.shouldTickAnimation()) {
                this.tickAnimation();
            }
            if (!this.isVirtual()) {
                this.spawnParticles(this.getHeatLevelFromBlock(), 1.0);
            }
            return;
        }
        if (this.isCreative) {
            return;
        }
        if (this.remainingBurnTime > 0) {
            --this.remainingBurnTime;
        }
        if (this.activeFuel == FuelType.NORMAL) {
            this.updateBlockState();
        }
        if (this.remainingBurnTime > 0) {
            return;
        }
        if (this.activeFuel == FuelType.SPECIAL) {
            this.activeFuel = FuelType.NORMAL;
            this.remainingBurnTime = 5000;
        } else {
            this.activeFuel = FuelType.NONE;
        }
        this.updateBlockState();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        this.stockKeeper = BlazeBurnerBlockEntity.getStockTicker((LevelAccessor)this.level, this.worldPosition) != null;
    }

    @Nullable
    public static StockTickerBlockEntity getStockTicker(LevelAccessor level, BlockPos pos) {
        for (Direction direction : Iterate.horizontalDirections) {
            BlockEntity blockEntity;
            Level l;
            if (level instanceof Level && !(l = (Level)level).isLoaded(pos)) {
                return null;
            }
            BlockState blockState = level.getBlockState(pos.relative(direction));
            if (!AllBlocks.STOCK_TICKER.has(blockState) || !((blockEntity = level.getBlockEntity(pos.relative(direction))) instanceof StockTickerBlockEntity)) continue;
            StockTickerBlockEntity stbe = (StockTickerBlockEntity)blockEntity;
            return stbe;
        }
        return null;
    }

    @OnlyIn(value=Dist.CLIENT)
    private boolean shouldTickAnimation() {
        return !VisualizationManager.supportsVisualization((LevelAccessor)this.level);
    }

    @OnlyIn(value=Dist.CLIENT)
    void tickAnimation() {
        boolean active;
        boolean bl = active = this.getHeatLevelFromBlock().isAtLeast(BlazeBurnerBlock.HeatLevel.FADING) && this.isValidBlockAbove();
        if (!active) {
            float target = 0.0f;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && !player.isInvisible()) {
                double z;
                double x;
                if (this.isVirtual()) {
                    x = -4.0;
                    z = -10.0;
                } else {
                    x = player.getX();
                    z = player.getZ();
                }
                double dx = x - ((double)this.getBlockPos().getX() + 0.5);
                double dz = z - ((double)this.getBlockPos().getZ() + 0.5);
                target = AngleHelper.deg((double)(-Mth.atan2((double)dz, (double)dx))) - 90.0f;
            }
            target = this.headAngle.getValue() + AngleHelper.getShortestAngleDiff((double)this.headAngle.getValue(), (double)target);
            this.headAngle.chase((double)target, 0.25, LerpedFloat.Chaser.exp((double)5.0));
            this.headAngle.tickChaser();
        } else {
            this.headAngle.chase((double)((AngleHelper.horizontalAngle((Direction)this.getBlockState().getOptionalValue((Property)BlazeBurnerBlock.FACING).orElse(Direction.SOUTH)) + 180.0f) % 360.0f), 0.125, LerpedFloat.Chaser.EXP);
            this.headAngle.tickChaser();
        }
        this.headAnimation.chase(active ? 1.0 : 0.0, 0.25, LerpedFloat.Chaser.exp((double)0.25));
        this.headAnimation.tickChaser();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (!this.isCreative) {
            compound.putInt("fuelLevel", this.activeFuel.ordinal());
            compound.putInt("burnTimeRemaining", this.remainingBurnTime);
        } else {
            compound.putBoolean("isCreative", true);
        }
        if (this.goggles) {
            compound.putBoolean("Goggles", true);
        }
        if (this.hat) {
            compound.putBoolean("TrainHat", true);
        }
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.activeFuel = FuelType.values()[compound.getInt("fuelLevel")];
        this.remainingBurnTime = compound.getInt("burnTimeRemaining");
        this.isCreative = compound.getBoolean("isCreative");
        this.goggles = compound.contains("Goggles");
        this.hat = compound.contains("TrainHat");
        super.read(compound, registries, clientPacket);
    }

    public BlazeBurnerBlock.HeatLevel getHeatLevelFromBlock() {
        return BlazeBurnerBlock.getHeatLevelOf(this.getBlockState());
    }

    public BlazeBurnerBlock.HeatLevel getHeatLevelForRender() {
        BlazeBurnerBlock.HeatLevel heatLevel = this.getHeatLevelFromBlock();
        if (!heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING) && this.stockKeeper) {
            return BlazeBurnerBlock.HeatLevel.FADING;
        }
        return heatLevel;
    }

    public void updateBlockState() {
        this.setBlockHeat(this.getHeatLevel());
    }

    protected void setBlockHeat(BlazeBurnerBlock.HeatLevel heat) {
        BlazeBurnerBlock.HeatLevel inBlockState = this.getHeatLevelFromBlock();
        if (inBlockState == heat) {
            return;
        }
        this.level.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().setValue(BlazeBurnerBlock.HEAT_LEVEL, (Comparable)((Object)heat)));
        this.notifyUpdate();
    }

    protected boolean tryUpdateFuel(ItemStack itemStack, boolean forceOverflow, boolean simulate) {
        int newBurnTime;
        if (this.isCreative) {
            return false;
        }
        FuelType newFuel = FuelType.NONE;
        Holder.Reference holder = itemStack.getItem().builtInRegistryHolder();
        BlazeBurnerFuel superheatedFuel = (BlazeBurnerFuel)holder.getData(CreateDataMaps.SUPERHEATED_BLAZE_BURNER_FUELS);
        BlazeBurnerFuel normalFuel = (BlazeBurnerFuel)holder.getData(CreateDataMaps.REGULAR_BLAZE_BURNER_FUELS);
        if (superheatedFuel != null) {
            newBurnTime = superheatedFuel.burnTime();
            newFuel = FuelType.SPECIAL;
        } else if (normalFuel != null) {
            newBurnTime = normalFuel.burnTime();
            newFuel = FuelType.NORMAL;
        } else if (AllTags.AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.matches(itemStack)) {
            newBurnTime = 3200;
            newFuel = FuelType.SPECIAL;
        } else {
            newBurnTime = itemStack.getBurnTime(null);
            if (newBurnTime > 0) {
                newFuel = FuelType.NORMAL;
            } else if (AllTags.AllItemTags.BLAZE_BURNER_FUEL_REGULAR.matches(itemStack)) {
                newBurnTime = 1600;
                newFuel = FuelType.NORMAL;
            }
        }
        if (newFuel == FuelType.NONE) {
            return false;
        }
        if (newFuel.ordinal() < this.activeFuel.ordinal()) {
            return false;
        }
        if (newFuel == this.activeFuel) {
            if (this.remainingBurnTime <= 500) {
                newBurnTime += this.remainingBurnTime;
            } else if (forceOverflow && newFuel == FuelType.NORMAL) {
                newBurnTime = this.remainingBurnTime < 10000 ? Math.min(this.remainingBurnTime + newBurnTime, 10000) : this.remainingBurnTime;
            } else {
                return false;
            }
        }
        if (simulate) {
            return true;
        }
        this.activeFuel = newFuel;
        this.remainingBurnTime = newBurnTime;
        if (this.level.isClientSide) {
            this.spawnParticleBurst(this.activeFuel == FuelType.SPECIAL);
            return true;
        }
        BlazeBurnerBlock.HeatLevel prev = this.getHeatLevelFromBlock();
        this.playSound();
        this.updateBlockState();
        if (prev != this.getHeatLevelFromBlock()) {
            this.level.playSound(null, this.worldPosition, SoundEvents.BLAZE_AMBIENT, SoundSource.BLOCKS, 0.125f + this.level.random.nextFloat() * 0.125f, 1.15f - this.level.random.nextFloat() * 0.25f);
        }
        return true;
    }

    protected void applyCreativeFuel() {
        this.activeFuel = FuelType.NONE;
        this.remainingBurnTime = 0;
        this.isCreative = true;
        BlazeBurnerBlock.HeatLevel next = this.getHeatLevelFromBlock().nextActiveLevel();
        if (this.level.isClientSide) {
            this.spawnParticleBurst(next.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING));
            return;
        }
        this.playSound();
        if (next == BlazeBurnerBlock.HeatLevel.FADING) {
            next = next.nextActiveLevel();
        }
        this.setBlockHeat(next);
    }

    public boolean isCreativeFuel(ItemStack stack) {
        return AllItems.CREATIVE_BLAZE_CAKE.isIn(stack);
    }

    public boolean isValidBlockAbove() {
        if (this.isVirtual()) {
            return false;
        }
        BlockState blockState = this.level.getBlockState(this.worldPosition.above());
        return BasinBlock.isBasin((LevelReader)this.level, this.worldPosition.above()) || blockState.getBlock() instanceof FluidTankBlock;
    }

    protected void playSound() {
        this.level.playSound(null, this.worldPosition, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 0.125f + this.level.random.nextFloat() * 0.125f, 0.75f - this.level.random.nextFloat() * 0.25f);
    }

    protected BlazeBurnerBlock.HeatLevel getHeatLevel() {
        BlazeBurnerBlock.HeatLevel level = BlazeBurnerBlock.HeatLevel.SMOULDERING;
        switch (this.activeFuel.ordinal()) {
            case 2: {
                level = BlazeBurnerBlock.HeatLevel.SEETHING;
                break;
            }
            case 1: {
                boolean lowPercent = (double)this.remainingBurnTime / 10000.0 < 0.0125;
                level = lowPercent ? BlazeBurnerBlock.HeatLevel.FADING : BlazeBurnerBlock.HeatLevel.KINDLED;
            }
        }
        return level;
    }

    protected void spawnParticles(BlazeBurnerBlock.HeatLevel heatLevel, double burstMult) {
        if (this.level == null) {
            return;
        }
        if (heatLevel == BlazeBurnerBlock.HeatLevel.NONE) {
            return;
        }
        RandomSource r = this.level.getRandom();
        Vec3 c = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        Vec3 v = c.add(VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)r, (float)0.125f).multiply(1.0, 0.0, 1.0));
        if (r.nextInt(4) != 0) {
            return;
        }
        boolean empty = this.level.getBlockState(this.worldPosition.above()).getCollisionShape((BlockGetter)this.level, this.worldPosition.above()).isEmpty();
        if (empty || r.nextInt(8) == 0) {
            this.level.addParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, v.x, v.y, v.z, 0.0, 0.0, 0.0);
        }
        double yMotion = empty ? 0.0625 : r.nextDouble() * (double)0.0125f;
        Vec3 v2 = c.add(VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)r, (float)0.5f).multiply(1.0, 0.25, 1.0).normalize().scale((empty ? 0.25 : 0.5) + r.nextDouble() * 0.125)).add(0.0, 0.5, 0.0);
        if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)) {
            this.level.addParticle((ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, v2.x, v2.y, v2.z, 0.0, yMotion, 0.0);
        } else if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
            this.level.addParticle((ParticleOptions)ParticleTypes.FLAME, v2.x, v2.y, v2.z, 0.0, yMotion, 0.0);
        }
    }

    public void spawnParticleBurst(boolean soulFlame) {
        Vec3 c = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        RandomSource r = this.level.random;
        for (int i = 0; i < 20; ++i) {
            Vec3 offset = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)r, (float)0.5f).multiply(1.0, 0.25, 1.0).normalize();
            Vec3 v = c.add(offset.scale(0.5 + r.nextDouble() * 0.125)).add(0.0, 0.125, 0.0);
            Vec3 m = offset.scale(0.03125);
            this.level.addParticle((ParticleOptions)(soulFlame ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME), v.x, v.y, v.z, m.x, m.y, m.z);
        }
    }

    public static enum FuelType {
        NONE,
        NORMAL,
        SPECIAL;

    }
}
