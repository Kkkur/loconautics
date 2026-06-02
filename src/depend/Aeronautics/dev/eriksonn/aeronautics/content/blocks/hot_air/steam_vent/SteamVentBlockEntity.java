/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
 *  com.simibubi.create.content.contraptions.DirectionalExtenderScrollOptionSlot
 *  com.simibubi.create.content.fluids.tank.FluidTankBlockEntity
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.DirectionalExtenderScrollOptionSlot;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.eriksonn.aeronautics.config.AeroConfig;
import dev.eriksonn.aeronautics.config.server.AeroBlockConfigs;
import dev.eriksonn.aeronautics.content.blocks.hot_air.BlockEntityLiftingGasProvider;
import dev.eriksonn.aeronautics.content.blocks.hot_air.GasEmitterRenderHandler;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.Balloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.ServerBalloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner.HotAirBurnerValueBehaviour;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasType;
import dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent.SteamVentBlock;
import dev.eriksonn.aeronautics.data.AeroLang;
import dev.eriksonn.aeronautics.index.AeroLiftingGasTypes;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
import dev.eriksonn.aeronautics.util.AeroSoundDistUtil;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SteamVentBlockEntity
extends SmartBlockEntity
implements BlockEntityLiftingGasProvider,
IHaveGoggleInformation {
    public static final Direction CHECKING_DIR = Direction.DOWN;
    private static final MutableComponent SCROLL_OPTION_TITLE = AeroLang.translate("scroll_option.hot_air_amount", new Object[0]).component();
    private static final String VALUE_FORMAT = "%s m\u00b3";
    public int signalStrength = 0;
    public int rawSignalStrength = 0;
    protected ScrollValueBehaviour steamAmountBehaviour;
    private GasEmitterRenderHandler renderHandler;
    private Balloon currentBalloon;
    private BlockEntityLiftingGasProvider.ClientBalloonInfo clientBalloonInfo;
    private WeakReference<FluidTankBlockEntity> source;
    private double efficiency = 0.0;
    private int ticksSinceSync;
    private int maxCapacity;
    protected LerpedFloat intensity = LerpedFloat.linear();
    @Nullable
    private BlockPos castPosition;

    public SteamVentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.source = new WeakReference<Object>(null);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        AeroBlockConfigs config = AeroConfig.server().blocks;
        this.setMaxCapacity((Integer)config.steamVentMaxHotAir.get());
        this.steamAmountBehaviour = new SteamVentValueBehaviour((Component)SCROLL_OPTION_TITLE, (SmartBlockEntity)this, new SteamVentValueBoxTransform()).between(() -> 50, () -> ((ConfigBase.ConfigInt)config.steamVentMaxHotAir).get()).withFormatter(arg_0 -> SteamVentBlockEntity.lambda$addBehaviours$1(VALUE_FORMAT, arg_0));
        this.steamAmountBehaviour.value = this.maxCapacity;
        behaviours.add((BlockEntityBehaviour)this.steamAmountBehaviour);
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void lazyTick() {
        super.lazyTick();
        this.getAndCacheTank();
        if (!this.isVirtual() && this.canOutputGas()) {
            this.tickBalloonLogic();
            this.notifyUpdate();
        }
    }

    public void tick() {
        super.tick();
        ++this.ticksSinceSync;
        FluidTankBlockEntity fluidTank = (FluidTankBlockEntity)this.source.get();
        if (fluidTank != null) {
            FluidTankBlockEntity controller = fluidTank.getControllerBE();
            if (controller != null) {
                this.efficiency = Mth.clamp((float)controller.boiler.getEngineEfficiency(controller.getTotalTankSize()), (float)0.0f, (float)1.0f);
            }
        } else {
            this.efficiency = 0.0;
        }
        double intensityGoal = Math.max(0.0, (double)this.signalStrength / 15.0);
        this.intensity.chase(intensityGoal, 0.1, LerpedFloat.Chaser.EXP);
        this.intensity.tickChaser();
        if (this.level.isClientSide) {
            GasEmitterRenderHandler renderHandler = this.getRenderHandler();
            if (this.isVirtual()) {
                renderHandler.targetFromRedstoneSignal(this.signalStrength);
            } else {
                renderHandler.targetFromRedstoneSignal(this.getGasOutput() > 0.0 ? this.signalStrength : 0);
            }
            renderHandler.tick();
            if (this.canOutputGas()) {
                AeroSoundDistUtil.addPosSteamVentSound(this.getBlockPos());
            } else {
                AeroSoundDistUtil.removePosSteamVentSound(this.getBlockPos());
            }
        }
    }

    public void initialize() {
        super.initialize();
        if (!this.isVirtual() && this.canOutputGas()) {
            this.tickBalloonLogic();
            this.notifyUpdate();
        }
    }

    public void invalidate() {
        super.invalidate();
        if (this.level.isClientSide) {
            AeroSoundDistUtil.removePosSteamVentSound(this.getBlockPos());
        } else {
            this.removeFromBalloon();
        }
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
        int range = (Integer)blocks.steamVentMaxRange.get();
        this.castPosition = this.getRaycastedPosition(this.level, Vec3.upFromBottomCenterOf((Vec3i)pos, (double)1.0), Vec3.upFromBottomCenterOf((Vec3i)pos, (double)(1.0 + (double)range)));
    }

    public boolean updateRawSignal() {
        int newStrength = this.level.getBestNeighborSignal(this.getBlockPos());
        if (newStrength != this.rawSignalStrength) {
            if (!this.level.isClientSide) {
                BlockState existentState = this.level.getBlockState(this.getBlockPos());
                if (newStrength > 0 && this.rawSignalStrength == 0) {
                    this.level.setBlockAndUpdate(this.worldPosition, (BlockState)existentState.setValue((Property)SteamVentBlock.POWERED, (Comparable)Boolean.valueOf(true)));
                } else if (newStrength == 0 && this.rawSignalStrength > 0) {
                    this.level.setBlockAndUpdate(this.worldPosition, (BlockState)existentState.setValue((Property)SteamVentBlock.POWERED, (Comparable)Boolean.valueOf(false)));
                }
            }
            this.rawSignalStrength = newStrength;
            this.signalSync();
            return true;
        }
        return false;
    }

    public void updateSignal(int signal) {
        if (signal != this.signalStrength) {
            if (this.signalStrength == 0 && signal != 0) {
                this.level.playSound(null, this.worldPosition, AeroSoundEvents.STEAM_VENT_OPEN.event(), SoundSource.BLOCKS, 0.25f, 1.1f - this.level.random.nextFloat() * 0.2f);
            } else if (signal == 0) {
                this.level.playSound(null, this.worldPosition, AeroSoundEvents.STEAM_VENT_CLOSE.event(), SoundSource.BLOCKS, 0.5f, 0.7f - this.level.random.nextFloat() * 0.2f);
            }
            this.signalStrength = signal;
            this.sendData();
        }
    }

    public static boolean inTankBounds(BlockPos pos, FluidTankBlockEntity controller) {
        int minX = controller.getBlockPos().getX();
        int minZ = controller.getBlockPos().getZ();
        int maxX = minX + controller.getWidth();
        int maxZ = minZ + controller.getWidth();
        return pos.getX() >= minX && pos.getX() < maxX && pos.getZ() >= minZ && pos.getZ() < maxZ;
    }

    public void signalSync() {
        FluidTankBlockEntity controller;
        FluidTankBlockEntity fluidTank = (FluidTankBlockEntity)this.source.get();
        if (fluidTank != null && (controller = fluidTank.getControllerBE()) != null) {
            ArrayList<SteamVentBlockEntity> adjacent = new ArrayList<SteamVentBlockEntity>();
            adjacent.add(this);
            int maxRaw = this.searchSignalSync(controller, new HashSet<BlockPos>(), adjacent);
            for (SteamVentBlockEntity steamVentBlockEntity : adjacent) {
                steamVentBlockEntity.updateSignal(maxRaw);
            }
        }
    }

    protected int searchSignalSync(FluidTankBlockEntity controller, Set<BlockPos> visited, List<SteamVentBlockEntity> vents) {
        int maxRaw = this.rawSignalStrength;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction dir : Iterate.horizontalDirections) {
            mutablePos.setWithOffset((Vec3i)this.getBlockPos(), dir);
            if (!SteamVentBlockEntity.inTankBounds((BlockPos)mutablePos, controller) || visited.contains(mutablePos)) continue;
            visited.add(mutablePos.immutable());
            BlockEntity blockEntity = this.level.getBlockEntity((BlockPos)mutablePos);
            if (!(blockEntity instanceof SteamVentBlockEntity)) continue;
            SteamVentBlockEntity vent = (SteamVentBlockEntity)blockEntity;
            vents.add(vent);
            maxRaw = Math.max(maxRaw, vent.searchSignalSync(controller, visited, vents));
        }
        return maxRaw;
    }

    public void getAndCacheTank() {
        BlockPos check;
        BlockEntity be;
        FluidTankBlockEntity ftbe = (FluidTankBlockEntity)this.source.get();
        if ((ftbe == null || ftbe.isRemoved()) && (be = this.level.getBlockEntity(check = this.getBlockPos().relative(CHECKING_DIR))) instanceof FluidTankBlockEntity) {
            FluidTankBlockEntity fluidTank = (FluidTankBlockEntity)be;
            this.source = new WeakReference<FluidTankBlockEntity>(fluidTank);
        }
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("SignalStrength", this.signalStrength);
        tag.putInt("RawSignalStrength", this.rawSignalStrength);
        if (clientPacket) {
            BlockEntityLiftingGasProvider.ClientBalloonInfo.writeToNBT(tag, (ServerBalloon)this.currentBalloon);
        }
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.signalStrength = tag.getInt("SignalStrength");
        this.rawSignalStrength = tag.getInt("RawSignalStrength");
        if (clientPacket) {
            this.ticksSinceSync = 0;
            this.clientBalloonInfo = BlockEntityLiftingGasProvider.ClientBalloonInfo.readFromNBT(tag);
        }
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

    @Override
    @Nullable
    public Balloon getBalloon() {
        return this.currentBalloon;
    }

    @Override
    public void setBalloon(Balloon balloon) {
        this.currentBalloon = balloon;
    }

    @Override
    public double getGasOutput() {
        return (double)this.steamAmountBehaviour.getValue() * this.efficiency * (double)((float)this.signalStrength / 15.0f);
    }

    @Override
    public LiftingGasType getLiftingGasType() {
        return (LiftingGasType)AeroLiftingGasTypes.STEAM.get();
    }

    @Override
    public boolean canOutputGas() {
        return this.efficiency > 0.0 && this.signalStrength > 0 && !this.isRemoved();
    }

    @Override
    public double getClientPredictedVolume() {
        if (this.clientBalloonInfo == null) {
            return 0.0;
        }
        return BlockEntityLiftingGasProvider.getPredictedVolume(this.clientBalloonInfo, this.ticksSinceSync);
    }

    public LerpedFloat getClientIntensity() {
        return this.intensity;
    }

    public GasEmitterRenderHandler getRenderHandler() {
        if (this.renderHandler == null) {
            this.renderHandler = new GasEmitterRenderHandler();
            return this.renderHandler;
        }
        return this.renderHandler;
    }

    private static /* synthetic */ String lambda$addBehaviours$1(String rec$, Object xva$0) {
        return VALUE_FORMAT.formatted(xva$0);
    }

    public static class SteamVentValueBehaviour
    extends HotAirBurnerValueBehaviour {
        public SteamVentValueBehaviour(Component label, SmartBlockEntity be, SteamVentValueBoxTransform slot) {
            super(label, be, (ValueBoxTransform)slot);
            slot.be = be;
        }
    }

    public static class SteamVentValueBoxTransform
    extends ValueBoxTransform.Sided {
        BlockEntity be;

        public ValueBoxTransform.Sided fromSide(Direction direction) {
            this.direction = direction;
            if (direction == Direction.UP) {
                Minecraft mc = Minecraft.getInstance();
                HitResult target = mc.hitResult;
                if (target instanceof BlockHitResult) {
                    Vec3 hit = target.getLocation();
                    Vec3 localHit = hit.subtract(Vec3.atCenterOf((Vec3i)this.be.getBlockPos()));
                    if (localHit.y < 0.4) {
                        this.direction = Direction.getNearest((double)localHit.x, (double)0.0, (double)localHit.z);
                    }
                }
            }
            return this;
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)12.0);
        }

        public float getScale() {
            return 0.45f;
        }

        protected ValueBoxTransform getMovementModeSlot() {
            return new DirectionalExtenderScrollOptionSlot((state, d) -> {
                Direction.Axis axis = d.getAxis();
                Direction.Axis shaftAxis = ((IRotate)state.getBlock()).getRotationAxis(state);
                return shaftAxis != axis;
            });
        }

        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            float yRot = AngleHelper.horizontalAngle((Direction)this.getSide()) + 180.0f;
            float xRot = this.getSide() == Direction.UP ? 90.0f : (this.getSide() == Direction.DOWN ? 270.0f : 0.0f);
            ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot)).rotateXDegrees(xRot += 22.5f);
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            if (direction == Direction.UP) {
                Minecraft mc = Minecraft.getInstance();
                HitResult target = mc.hitResult;
                if (target instanceof BlockHitResult) {
                    Vec3 hit = target.getLocation();
                    Vec3 localHit = hit.subtract(Vec3.atCenterOf((Vec3i)this.be.getBlockPos()));
                    return localHit.y < 0.4;
                }
            }
            return true;
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
