/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
 *  com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.eriksonn.aeronautics.config.AeroConfig;
import dev.eriksonn.aeronautics.config.server.AeroBlockConfigs;
import dev.eriksonn.aeronautics.content.blocks.hot_air.BlockEntityLiftingGasProvider;
import dev.eriksonn.aeronautics.content.blocks.hot_air.GasEmitterRenderHandler;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.Balloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.ServerBalloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner.HotAirBurnerBlock;
import dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner.HotAirBurnerValueBehaviour;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasType;
import dev.eriksonn.aeronautics.content.particle.HotAirEmberParticleData;
import dev.eriksonn.aeronautics.data.AeroLang;
import dev.eriksonn.aeronautics.index.AeroLiftingGasTypes;
import dev.eriksonn.aeronautics.util.AeroSoundDistUtil;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HotAirBurnerBlockEntity
extends SmartBlockEntity
implements BlockEntityLiftingGasProvider,
IHaveGoggleInformation,
IHaveHoveringInformation {
    private static final MutableComponent SCROLL_OPTION_TITLE = AeroLang.translate("scroll_option.hot_air_amount", new Object[0]).component();
    private static final String VALUE_FORMAT = "%s m\u00b3";
    public GasEmitterRenderHandler renderHandler = new GasEmitterRenderHandler();
    protected BlockEntityLiftingGasProvider.ClientBalloonInfo clientBalloonInfo;
    protected boolean powered;
    protected int signalStrength;
    protected ScrollValueBehaviour hotAirAmountBehaviour;
    protected double lastRenderTime;
    protected double renderTime;
    protected LerpedFloat intensity = LerpedFloat.linear();
    private int maxCapacity;
    private int ticksSinceSync;
    private Balloon currentBalloon;
    @Nullable
    private BlockPos castPosition;

    public HotAirBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(20);
    }

    public void initialize() {
        super.initialize();
        this.updateSignal();
        if (!this.isVirtual() && this.canOutputGas()) {
            this.tickBalloonLogic();
            this.notifyUpdate();
        }
    }

    public int getSignalStrength() {
        return this.signalStrength;
    }

    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.setMaxCapacity((Integer)AeroConfig.server().blocks.hotAirBurnerMaxHotAir.get());
        this.hotAirAmountBehaviour = new HotAirBurnerValueBehaviour((Component)SCROLL_OPTION_TITLE, this, (ValueBoxTransform)new HotAirBurnerValueBoxTransform()).between(() -> 5, () -> (Integer)AeroConfig.server().blocks.hotAirBurnerMaxHotAir.get()).withFormatter(arg_0 -> HotAirBurnerBlockEntity.lambda$addBehaviours$2(VALUE_FORMAT, arg_0));
        this.hotAirAmountBehaviour.value = this.maxCapacity;
        behaviours.add((BlockEntityBehaviour)this.hotAirAmountBehaviour);
    }

    public void updateSignal() {
        boolean shouldPower = this.level.hasNeighborSignal(this.worldPosition);
        int newSignalStrength = this.level.getBestNeighborSignal(this.worldPosition);
        if (newSignalStrength != this.signalStrength) {
            if (this.signalStrength == 0 && newSignalStrength != 0) {
                this.level.playSound(null, this.worldPosition, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.125f + this.level.random.nextFloat() * 0.125f, 0.75f - this.level.random.nextFloat() * 0.25f);
            } else if (newSignalStrength == 0) {
                this.level.playSound(null, this.worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.125f + this.level.random.nextFloat() * 0.125f, 1.1f - this.level.random.nextFloat() * 0.2f);
            }
            this.signalStrength = newSignalStrength;
            this.powered = shouldPower;
            this.sendData();
        }
    }

    public void lazyTick() {
        super.lazyTick();
        if (this.level == null) {
            return;
        }
        if (this.canOutputGas() && !this.isVirtual()) {
            this.tickBalloonLogic();
        }
        if (!this.level.isClientSide && !this.isVirtual()) {
            this.notifyUpdate();
        }
    }

    public AABB getRenderBoundingBox() {
        return AABB.encapsulatingFullBlocks((BlockPos)this.getBlockPos(), (BlockPos)this.getBlockPos().above());
    }

    public void tick() {
        super.tick();
        ++this.ticksSinceSync;
        if (!this.level.isClientSide) {
            return;
        }
        double intensityGoal = Math.max(0.0, (double)this.getSignalStrength() / 15.0);
        this.intensity.chase(intensityGoal, 0.1, LerpedFloat.Chaser.EXP);
        this.intensity.tickChaser();
        this.lastRenderTime = this.renderTime;
        this.renderTime += 0.05 * (1.0 + (double)(this.intensity.getValue() * this.intensity.getValue()) * 1.8);
        this.renderHandler.targetFromRedstoneSignal(this.signalStrength);
        this.renderHandler.tick();
        if (this.isVirtual()) {
            return;
        }
        double particleProbability = 0.4 * Math.sqrt(this.getGasOutput() / (double)this.maxCapacity);
        BlockPos pos = this.getBlockPos();
        RandomSource random = this.level.getRandom();
        double speed = (float)this.signalStrength / 15.0f;
        if (particleProbability > (double)random.nextFloat()) {
            this.level.addAlwaysVisibleParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, true, (double)pos.getX() + 0.5 + random.nextDouble() / 5.0 * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + (random.nextDouble() + random.nextDouble()) * 0.5 + 0.56, (double)pos.getZ() + 0.5 + random.nextDouble() / 5.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, speed * speed * 0.3, 0.0);
        }
        if (random.nextInt(20) == 0 && this.powered) {
            this.level.playLocalSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.25f + random.nextFloat() * 0.25f, random.nextFloat() * 0.7f + 0.6f, false);
        }
        if ((particleProbability /= 5.0) > (double)random.nextFloat()) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                this.level.addParticle((ParticleOptions)ParticleTypes.LAVA, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, (double)(random.nextFloat() / 2.0f), 5.0E-5, (double)(random.nextFloat() / 2.0f));
            }
        }
        if (random.nextFloat() < 0.5f * this.intensity.getValue()) {
            this.level.addParticle((ParticleOptions)new HotAirEmberParticleData(this.getBlockState().getValue(HotAirBurnerBlock.VARIANT) == HotAirBurnerBlock.Variant.SOUL_FIRE), (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5, (double)pos.getY() + 0.5 + 0.1, (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5, (double)this.intensity.getValue(), (double)this.intensity.getValue(), (double)this.intensity.getValue());
        }
        if (this.canOutputGas()) {
            AeroSoundDistUtil.addPosHotAirBurnerSound(this.getBlockPos());
        } else {
            AeroSoundDistUtil.removePosHotAirBurnerSound(this.getBlockPos());
        }
    }

    public void invalidate() {
        super.invalidate();
        if (this.level.isClientSide) {
            AeroSoundDistUtil.removePosHotAirBurnerSound(this.getBlockPos());
        } else {
            this.removeFromBalloon();
        }
    }

    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putBoolean("IsPowered", this.powered);
        compound.putInt("SignalStrength", this.signalStrength);
        if (clientPacket) {
            BlockEntityLiftingGasProvider.ClientBalloonInfo.writeToNBT(compound, (ServerBalloon)this.getBalloon());
        }
        super.write(compound, registries, clientPacket);
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        this.powered = tag.getBoolean("IsPowered");
        this.signalStrength = tag.getInt("SignalStrength");
        if (clientPacket) {
            this.ticksSinceSync = 0;
            this.clientBalloonInfo = BlockEntityLiftingGasProvider.ClientBalloonInfo.readFromNBT(tag);
        }
        super.read(tag, registries, clientPacket);
    }

    @Override
    public Balloon getBalloon() {
        return this.currentBalloon;
    }

    @Override
    public void setBalloon(Balloon balloon) {
        this.currentBalloon = balloon;
    }

    @Override
    @Nullable
    public BlockPos getCastPosition() {
        return this.castPosition;
    }

    @Override
    public void doRaycast() {
        BlockPos pos = this.getBlockPos();
        AeroBlockConfigs blocks = AeroConfig.server().blocks;
        int range = (Integer)blocks.hotAirBurnerMaxRange.get();
        this.castPosition = this.getRaycastedPosition(this.level, Vec3.upFromBottomCenterOf((Vec3i)pos, (double)1.0), Vec3.upFromBottomCenterOf((Vec3i)pos, (double)(1.0 + (double)range)));
    }

    @Override
    public double getGasOutput() {
        return (double)(this.hotAirAmountBehaviour.value * this.signalStrength) / 15.0;
    }

    @Override
    public LiftingGasType getLiftingGasType() {
        return (LiftingGasType)AeroLiftingGasTypes.DEFAULT_GAS.get();
    }

    @Override
    public boolean canOutputGas() {
        return this.signalStrength > 0 & !this.isRemoved();
    }

    @Override
    public double getClientPredictedVolume() {
        if (this.clientBalloonInfo == null) {
            return 0.0;
        }
        return BlockEntityLiftingGasProvider.getPredictedVolume(this.clientBalloonInfo, this.ticksSinceSync);
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!this.canOutputGas()) {
            return false;
        }
        AeroLang.blockName(this.getBlockState()).text(":").forGoggles(tooltip, 1);
        if (this.clientBalloonInfo != null) {
            this.addBalloonGoggleInformation(tooltip, this.clientBalloonInfo, this.ticksSinceSync, this.getAirPressure(this.clientBalloonInfo, this.level));
        }
        return true;
    }

    protected float getFlameIntensity(float partialTicks) {
        return this.intensity.getValue(partialTicks);
    }

    public float getTimeOffset() {
        return this.getBlockPos().hashCode() % 10;
    }

    public LerpedFloat getClientIntensity() {
        return this.intensity;
    }

    private static /* synthetic */ String lambda$addBehaviours$2(String rec$, Object xva$0) {
        return VALUE_FORMAT.formatted(xva$0);
    }

    private static class HotAirBurnerValueBoxTransform
    extends ValueBoxTransform.Sided {
        private HotAirBurnerValueBoxTransform() {
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)14.0);
        }

        public float getScale() {
            return 0.45f;
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis() != Direction.Axis.Y || direction.equals((Object)Direction.DOWN);
        }

        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            if (this.getSide() == Direction.DOWN) {
                return VecHelper.voxelSpace((double)8.0, (double)0.0, (double)8.0);
            }
            Vec3 location = this.getSouthLocation();
            location = location.add(VecHelper.voxelSpace((double)0.0, (double)-3.0, (double)1.75));
            location = VecHelper.rotateCentered((Vec3)location, (double)AngleHelper.horizontalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.Y);
            return location;
        }
    }
}
