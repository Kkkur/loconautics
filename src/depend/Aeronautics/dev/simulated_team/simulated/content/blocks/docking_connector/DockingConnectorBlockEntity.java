/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor
 *  dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintConfiguration
 *  dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintHandle
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.entity.monster.Shulker
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.ShulkerBoxBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.docking_connector;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintHandle;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.compat.computercraft.wired.DockingConnectorWiredElement;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlock;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorInventory;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorPair;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorTank;
import dev.simulated_team.simulated.content.blocks.docking_connector.PairedDockingConnectorBlock;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetBehaviour;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetMap;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetPair;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetPairIdentifier;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.RedstoneMagnetBlock;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.SimMagnet;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimMathUtils;
import dev.simulated_team.simulated.util.SimMovementContext;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class DockingConnectorBlockEntity
extends SmartBlockEntity
implements SimMagnet,
BlockEntitySubLevelActor,
Clearable {
    public static MagnetMap<DockingConnectorBlockEntity> MAGNET_CONTROLLER = new MagnetMap();
    public boolean powered;
    public LerpedFloat extension = LerpedFloat.linear().chase(0.0, 0.1, LerpedFloat.Chaser.LINEAR);
    public LerpedFloat feet = LerpedFloat.linear().chase(0.0, 0.15, LerpedFloat.Chaser.LINEAR);
    public DockingConnectorInventory inventory;
    public DockingConnectorTank tank;
    public BlockPos otherConnectorPosition = null;
    public UUID otherConnectorSubLevelId = null;
    protected DockingConnectorState state = DockingConnectorState.UNPOWERED;
    protected boolean virtualLock = false;
    protected double closestPairDistance = 0.0;
    private MagnetBehaviour magnetBehaviour;
    private FixedConstraintHandle constraintHandle;
    public final DockingConnectorWiredElement ccWiredElement;
    private ConstraintSmoother constraintSmoother = null;

    public DockingConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new DockingConnectorInventory(this);
        this.tank = new DockingConnectorTank(this);
        this.ccWiredElement = DockingConnectorWiredElement.create(this);
    }

    @Nullable
    public DockingConnectorBlockEntity getOtherConnector() {
        DockingConnectorBlockEntity be;
        if (this.otherConnectorPosition == null) {
            return null;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(this.otherConnectorPosition);
        return blockEntity instanceof DockingConnectorBlockEntity ? (be = (DockingConnectorBlockEntity)blockEntity) : null;
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.magnetBehaviour = new MagnetBehaviour(this, MAGNET_CONTROLLER);
        behaviours.add(this.magnetBehaviour);
    }

    public void initialize() {
        MagnetMap<DockingConnectorBlockEntity> controller;
        super.initialize();
        DockingConnectorBlockEntity otherConnector = this.getOtherConnector();
        if (otherConnector != null && this.constraintHandle == null && otherConnector.constraintHandle == null && (controller = MAGNET_CONTROLLER).getPair(this.level, this.getBlockPos(), this.otherConnectorPosition) == null) {
            controller.tryAddPair(this.level, this.getBlockPos(), this.otherConnectorPosition, DockingConnectorPair::new);
            DockingConnectorPair pair = (DockingConnectorPair)controller.getPair(this.level, this.getBlockPos(), this.otherConnectorPosition);
            if (pair != null) {
                pair.dock(true);
                this.notifyUpdate();
            }
        }
    }

    public void tick() {
        BlockState frontState;
        BlockPos frontPos;
        BlockPos pos;
        block11: {
            block12: {
                super.tick();
                BlockState state = this.getBlockState();
                Direction direction = (Direction)state.getValue((Property)BlockStateProperties.FACING);
                pos = this.getBlockPos();
                frontPos = pos.relative(direction);
                frontState = this.level.getBlockState(frontPos);
                this.powered = (Boolean)state.getValue((Property)BlockStateProperties.POWERED);
                if (!(frontState.isAir() || frontState.is((Block)SimBlocks.PAIRED_DOCKING_CONNECTOR.get()) && ((Direction)frontState.getValue((Property)BlockStateProperties.FACING)).getOpposite() == direction)) {
                    this.powered = false;
                }
                if (!this.level.isClientSide() && this.isExtended()) {
                    this.searchForPairs();
                    if (!frontState.is((Block)SimBlocks.PAIRED_DOCKING_CONNECTOR.get())) {
                        this.level.setBlock(frontPos, (BlockState)((PairedDockingConnectorBlock)((Object)SimBlocks.PAIRED_DOCKING_CONNECTOR.get())).defaultBlockState().setValue((Property)BlockStateProperties.FACING, (Comparable)direction.getOpposite()), 3);
                    }
                }
                if (this.otherConnectorPosition == null || this.level.isClientSide) break block11;
                BlockEntity blockEntity = this.level.getBlockEntity(this.otherConnectorPosition);
                if (!(blockEntity instanceof DockingConnectorBlockEntity)) break block12;
                DockingConnectorBlockEntity be = (DockingConnectorBlockEntity)blockEntity;
                if (Objects.equals(be.otherConnectorPosition, this.getBlockPos())) break block11;
            }
            this.unDock();
            this.state = DockingConnectorState.EXTENDED;
            this.sendData();
        }
        float previousExtensionTarget = this.extension.getChaseTarget();
        this.extension.updateChaseTarget(this.powered ? 1.0f : 0.0f);
        if (this.extension.getChaseTarget() != previousExtensionTarget) {
            if (this.level.isClientSide()) {
                this.level.playLocalSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, this.powered ? SimSoundEvents.DOCKING_CONNECTOR_EXTENDS.event() : SimSoundEvents.DOCKING_CONNECTOR_RETRACTS.event(), SoundSource.BLOCKS, 0.75f, 1.0f, false);
            } else if (!this.powered && frontState.is((Block)SimBlocks.PAIRED_DOCKING_CONNECTOR.get())) {
                this.level.setBlock(frontPos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        boolean previousExtended = this.isExtended();
        this.extension.tickChaser();
        if (previousExtended != this.isExtended()) {
            this.level.setBlock(this.getBlockPos(), (BlockState)this.getBlockState().setValue((Property)DockingConnectorBlock.EXTENDED, (Comparable)Boolean.valueOf(this.isExtended())), 6);
        }
        float previousFeetValue = this.feet.getValue();
        this.feet.updateChaseTarget(this.hasOtherConnector() || this.virtualLock ? 1.0f : 0.0f);
        this.feet.tickChaser();
        this.updateState();
    }

    public void lazyTick() {
        super.lazyTick();
        if (this.state == DockingConnectorState.EXTENDED || this.state == DockingConnectorState.LOCKING) {
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        }
    }

    public void setVirtualLock(boolean lock) {
        this.virtualLock = lock;
    }

    private void removeConstraint() {
        if (this.constraintHandle != null) {
            this.constraintHandle.remove();
            this.constraintHandle = null;
        }
        this.constraintSmoother = null;
    }

    private void attachConstraints(DockingConnectorBlockEntity other, Quaterniondc targetOrientation, Vector3dc relativePos, Quaterniondc relativeOrientation, boolean isLocked) {
        BlockPos pos = this.getBlockPos();
        BlockPos otherPos = other.getBlockPos();
        ServerSubLevel thisSubLevel = (ServerSubLevel)Sable.HELPER.getContaining(this.level, (Vec3i)pos);
        ServerSubLevel otherSubLevel = (ServerSubLevel)Sable.HELPER.getContaining(this.level, (Vec3i)otherPos);
        assert (thisSubLevel != null);
        Vector3d anchorPos = JOMLConversion.toJOML((Position)this.getTipPosition());
        Vector3d otherAnchorPos = JOMLConversion.toJOML((Position)other.getTipPosition());
        ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)((ServerLevel)this.level));
        SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
        double partialPhysicsTick = physicsSystem.getPartialPhysicsTick();
        double physicsTime = this.feet.getValue((float)partialPhysicsTick);
        double lerpFactor = Mth.clamp((double)(physicsTime * physicsTime), (double)0.0, (double)1.0);
        if (isLocked) {
            lerpFactor = 1.0;
        }
        double rotationLerpFactor = Mth.clamp((double)(lerpFactor * 2.0), (double)0.0, (double)1.0);
        if (this.constraintHandle != null) {
            this.constraintHandle.remove();
        }
        otherAnchorPos.fma(1.0 - lerpFactor, relativePos);
        FixedConstraintConfiguration constraint = new FixedConstraintConfiguration((Vector3dc)anchorPos, (Vector3dc)otherAnchorPos, (Quaterniondc)relativeOrientation.slerp(targetOrientation, rotationLerpFactor, new Quaterniond()));
        this.constraintHandle = (FixedConstraintHandle)container.physicsSystem().getPipeline().addConstraint(thisSubLevel, otherSubLevel, (PhysicsConstraintConfiguration)constraint);
    }

    private void searchForPairs() {
        DockingConnectorBlockEntity other;
        BlockEntity blockEntity;
        Direction direction = (Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING);
        MagnetMap<DockingConnectorBlockEntity> controller = MAGNET_CONTROLLER;
        if (this.hasOtherConnector()) {
            controller.tryAddPair(this.level, this.getBlockPos(), this.otherConnectorPosition, DockingConnectorPair::new);
            return;
        }
        Vector3d tempRelativePos = new Vector3d();
        this.closestPairDistance = Double.MAX_VALUE;
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
        SimMovementContext context = SimMovementContext.getMovementContext(this.level, this.getBlockPos().getCenter());
        List<SimMovementContext> contexts = controller.findNearby(context);
        for (SimMovementContext movementContext : contexts) {
            DockingConnectorBlockEntity other2;
            BlockEntity blockEntity2;
            if (movementContext.subLevel() == subLevel || !((blockEntity2 = this.level.getBlockEntity(movementContext.localBlockPos())) instanceof DockingConnectorBlockEntity) || (other2 = (DockingConnectorBlockEntity)blockEntity2).hasOtherConnector() || !other2.magnetActive()) continue;
            controller.tryAddPair(this.level, this.getBlockPos(), movementContext.localBlockPos(), DockingConnectorPair::new);
            DockingConnectorPair.getRelativePosition(this, other2, tempRelativePos);
            this.closestPairDistance = Math.min(tempRelativePos.length(), this.closestPairDistance);
        }
        BlockPos sameGridConnection = this.getBlockPos().offset(direction.getNormal().multiply(3));
        if (this.isExtended() && (blockEntity = this.level.getBlockEntity(sameGridConnection)) instanceof DockingConnectorBlockEntity && ((Direction)(other = (DockingConnectorBlockEntity)blockEntity).getBlockState().getValue((Property)BlockStateProperties.FACING)).getOpposite() == direction && other.isExtended()) {
            controller.tryAddPair(this.level, this.getBlockPos(), other.getBlockPos(), DockingConnectorPair::new);
        }
    }

    private void updateState() {
        if (this.powered) {
            if (this.state != DockingConnectorState.LOCKED && this.isExtended()) {
                this.state = this.hasOtherConnector() ? DockingConnectorState.LOCKING : DockingConnectorState.EXTENDED;
            }
        } else {
            if (this.state != DockingConnectorState.UNPOWERED && this.hasOtherConnector()) {
                MagnetPair magnetPair;
                MagnetMap<DockingConnectorBlockEntity> controller = MAGNET_CONTROLLER;
                Map map = controller.pairMap.get(this.level);
                if (map != null && (magnetPair = map.get(new MagnetPairIdentifier(this.otherConnectorPosition, this.getBlockPos()))) instanceof DockingConnectorPair) {
                    DockingConnectorPair pair = (DockingConnectorPair)magnetPair;
                    pair.unDock();
                }
            }
            this.state = DockingConnectorState.UNPOWERED;
        }
    }

    public void pairTo(DockingConnectorBlockEntity other) {
        if (other.getBlockPos().equals((Object)this.otherConnectorPosition)) {
            return;
        }
        DockingConnectorBlockEntity otherConnector = this.getOtherConnector();
        if (otherConnector != null && this.getBlockPos().equals((Object)otherConnector.otherConnectorPosition)) {
            otherConnector.unDock();
        }
        this.unDock();
        MagnetMap<DockingConnectorBlockEntity> controller = MAGNET_CONTROLLER;
        controller.tryAddPair(this.level, this.getBlockPos(), other.getBlockPos(), DockingConnectorPair::new);
        DockingConnectorPair pair = (DockingConnectorPair)controller.getPair(this.level, this.getBlockPos(), other.getBlockPos());
        if (pair != null) {
            pair.dock(true);
            this.notifyUpdate();
        }
    }

    public boolean isLocked() {
        return this.state == DockingConnectorState.LOCKED;
    }

    public float getPlateOffset() {
        return 0.5f + this.getExtensionDistance(0.0f);
    }

    public boolean isExtended() {
        return this.extension.getValue() == 1.0f && this.powered;
    }

    public boolean isRetracted() {
        return this.extension.getValue() == 0.0f;
    }

    public boolean isFeetExtended() {
        return this.otherConnectorPosition != null && this.feet.getValue() == 1.0f;
    }

    public boolean hasOtherConnector() {
        return this.otherConnectorPosition != null;
    }

    public float getExtensionDistance(float partialTick) {
        return SimMathUtils.smoothStep(this.extension.getValue(partialTick));
    }

    public float getFeetRotation(float partialTick) {
        float rotation = this.feet.getValue(partialTick);
        float rotationTarget = this.feet.getChaseTarget();
        if (rotationTarget == 1.0f) {
            rotation *= rotation;
        }
        return rotation;
    }

    public void setDock(DockingConnectorBlockEntity otherConnector, boolean isLocked, @Nullable Quaterniondc targetOrientation, Vector3dc relativePos, Quaterniondc relativeOrientation) {
        BlockPos previous = this.otherConnectorPosition;
        SubLevel otherSubLevel = Sable.HELPER.getContaining((BlockEntity)otherConnector);
        this.otherConnectorPosition = otherConnector.getBlockPos();
        this.otherConnectorSubLevelId = otherSubLevel != null ? otherSubLevel.getUniqueId() : null;
        this.updateState();
        if (this.state == DockingConnectorState.LOCKING) {
            if (targetOrientation != null && this.constraintSmoother == null) {
                this.constraintSmoother = new ConstraintSmoother(otherConnector, targetOrientation, relativePos, relativeOrientation);
            }
            if (isLocked) {
                this.state = DockingConnectorState.LOCKED;
                this.inventory.connect(this.otherConnectorPosition, otherConnector.inventory);
                this.tank.connect(this.otherConnectorPosition, otherConnector.tank);
                this.ccWiredElement.connect(otherConnector.ccWiredElement);
                this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
                if (this.constraintSmoother != null) {
                    ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)((ServerLevel)this.level));
                    this.constraintSmoother.step(container, this, 1.0);
                }
                this.constraintSmoother = null;
            }
        }
        if (previous != this.otherConnectorPosition) {
            if (targetOrientation == null) {
                this.removeConstraint();
            }
            this.sendData();
        }
    }

    public void unDock() {
        DockingConnectorBlockEntity otherConnector = this.getOtherConnector();
        if (otherConnector != null) {
            this.ccWiredElement.disconnect(otherConnector.ccWiredElement);
        }
        this.closestPairDistance = Double.MAX_VALUE;
        this.otherConnectorSubLevelId = null;
        this.otherConnectorPosition = null;
        this.state = this.isExtended() ? DockingConnectorState.EXTENDED : DockingConnectorState.UNPOWERED;
        this.inventory.disconnect();
        this.tank.disconnect();
        this.removeConstraint();
        this.sendData();
        this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
    }

    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        if (this.constraintSmoother != null) {
            this.constraintSmoother.partialStep(this);
        }
    }

    public void updateSignal() {
        boolean shouldPower = this.level.hasNeighborSignal(this.worldPosition);
        if (this.powered != shouldPower) {
            this.powered = shouldPower;
            this.sendData();
        }
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.putBoolean("IsPowered", this.powered);
        tag.putFloat("Extension", this.extension.getValue());
        tag.putFloat("Target", this.extension.getChaseTarget());
        tag.putFloat("Feet", this.feet.getValue());
        if (this.otherConnectorPosition != null) {
            tag.put("OtherConnector", NbtUtils.writeBlockPos((BlockPos)this.otherConnectorPosition));
        }
        if (this.otherConnectorSubLevelId != null) {
            tag.putUUID("OtherConnectorSubLevelId", this.otherConnectorSubLevelId);
        }
        tag.put("Inventory", (Tag)this.inventory.write(registries));
        tag.put("Tank", (Tag)this.tank.write());
        super.write(tag, registries, clientPacket);
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        this.powered = tag.getBoolean("IsPowered");
        this.extension.setValue((double)tag.getFloat("Extension"));
        this.extension.updateChaseTarget(tag.getFloat("Target"));
        this.feet.setValue((double)tag.getFloat("Feet"));
        this.extension.setValue((double)this.extension.getValue());
        this.feet.setValue((double)this.feet.getValue());
        this.otherConnectorPosition = tag.contains("OtherConnector") ? (BlockPos)NbtUtils.readBlockPos((CompoundTag)tag, (String)"OtherConnector").orElse(null) : null;
        if (tag.contains("OtherConnectorSubLevelId")) {
            this.otherConnectorSubLevelId = tag.getUUID("OtherConnectorSubLevelId");
        }
        this.inventory.read(registries, tag.getCompound("Inventory"));
        this.tank.read(tag.getCompound("Tank"));
        super.read(tag, registries, clientPacket);
    }

    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.extension.updateChaseTarget(this.powered ? 1.0f : 0.0f);
            this.feet.updateChaseTarget(this.hasOtherConnector() ? 1.0f : 0.0f);
            return true;
        }
        return super.triggerEvent(id, type);
    }

    public void remove() {
        super.remove();
        this.removeConstraint();
        if (this.level == null || !this.level.isClientSide) {
            this.ccWiredElement.remove();
        }
    }

    @Override
    public Quaternionfc getOrientation() {
        return ((Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING)).getRotation();
    }

    @Override
    public SubLevel getLatestSubLevel() {
        return Sable.HELPER.getContaining((BlockEntity)this);
    }

    @Override
    public Vector3d setMagneticMoment(Vector3d v) {
        v.set((Vector3dc)JOMLConversion.toJOML((Position)Vec3.atLowerCornerOf((Vec3i)((Direction)this.getBlockState().getValue((Property)DockingConnectorBlock.FACING)).getNormal())));
        v.mul(Math.sqrt((Double)SimConfigService.INSTANCE.server().physics.dockingConnectorStrength.get()));
        return v;
    }

    @Override
    public Vec3 getMagnetPosition() {
        return Vec3.atCenterOf((Vec3i)this.getBlockPos()).add(Vec3.atLowerCornerOf((Vec3i)((Direction)this.getBlockState().getValue((Property)RedstoneMagnetBlock.FACING)).getNormal()).scale(1.4));
    }

    public Vec3 getTipPosition() {
        return Vec3.atCenterOf((Vec3i)this.getBlockPos()).add(Vec3.atLowerCornerOf((Vec3i)((Direction)this.getBlockState().getValue((Property)RedstoneMagnetBlock.FACING)).getNormal()).scale(1.5));
    }

    @Override
    public boolean magnetActive() {
        return this.isExtended() && this.constraintHandle == null;
    }

    public AABB getBoundingBox(BlockState state) {
        return Shulker.getProgressAabb((float)1.0f, (Direction)((Direction)state.getValue((Property)ShulkerBoxBlock.FACING)), (float)this.getExtensionDistance(1.0f));
    }

    public AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(1.0);
    }

    public @Nullable Iterable<@NotNull SubLevel> sable$getConnectionDependencies() {
        if (this.otherConnectorSubLevelId == null) {
            return null;
        }
        SubLevelContainer container = SubLevelContainer.getContainer((Level)this.level);
        SubLevel otherSubLevel = container.getSubLevel(this.otherConnectorSubLevelId);
        if (otherSubLevel == null) {
            return null;
        }
        return List.of(otherSubLevel);
    }

    public void clearContent() {
        this.inventory.clearContent();
    }

    public static enum DockingConnectorState {
        UNPOWERED,
        EXTENDED,
        LOCKING,
        LOCKED;

    }

    private record ConstraintSmoother(BlockPos otherConnectorPos, Quaterniond targetRelativeOrientation, Vector3d initialRelativePosition, Quaterniond initialRelativeOrientation) {
        private ConstraintSmoother(DockingConnectorBlockEntity otherConnectorPos, Quaterniondc targetRelativeOrientation, Vector3dc initialRelativePosition, Quaterniondc initialRelativeOrientation) {
            this(otherConnectorPos.getBlockPos(), new Quaterniond(targetRelativeOrientation), new Vector3d(initialRelativePosition), new Quaterniond(initialRelativeOrientation));
        }

        public void partialStep(DockingConnectorBlockEntity connector) {
            ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel)((ServerLevel)connector.level));
            SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
            double partialPhysicsTick = physicsSystem.getPartialPhysicsTick();
            double physicsTime = connector.feet.getValue((float)partialPhysicsTick);
            double lerpFactor = Mth.clamp((double)(physicsTime * physicsTime), (double)0.0, (double)1.0);
            this.step(container, connector, lerpFactor);
        }

        public void step(ServerSubLevelContainer container, DockingConnectorBlockEntity connector, double lerpFactor) {
            BlockPos pos = connector.getBlockPos();
            BlockEntity blockEntity = connector.level.getBlockEntity(this.otherConnectorPos);
            if (blockEntity instanceof DockingConnectorBlockEntity) {
                DockingConnectorBlockEntity other = (DockingConnectorBlockEntity)blockEntity;
                ServerSubLevel thisSubLevel = (ServerSubLevel)Sable.HELPER.getContaining(connector.level, (Vec3i)pos);
                ServerSubLevel otherSubLevel = (ServerSubLevel)Sable.HELPER.getContaining(connector.level, (Vec3i)this.otherConnectorPos);
                assert (thisSubLevel != null);
                Vector3d anchorPos = JOMLConversion.toJOML((Position)connector.getTipPosition());
                Vector3d otherAnchorPos = JOMLConversion.toJOML((Position)other.getTipPosition());
                double rotationLerpFactor = Mth.clamp((double)(lerpFactor * 2.0), (double)0.0, (double)1.0);
                if (connector.constraintHandle != null) {
                    connector.constraintHandle.remove();
                }
                otherAnchorPos.fma(1.0 - lerpFactor, (Vector3dc)this.initialRelativePosition);
                FixedConstraintConfiguration constraint = new FixedConstraintConfiguration((Vector3dc)anchorPos, (Vector3dc)otherAnchorPos, (Quaterniondc)this.initialRelativeOrientation.slerp((Quaterniondc)this.targetRelativeOrientation, rotationLerpFactor, new Quaterniond()));
                connector.constraintHandle = (FixedConstraintHandle)container.physicsSystem().getPipeline().addConstraint(thisSubLevel, otherSubLevel, (PhysicsConstraintConfiguration)constraint);
            }
        }
    }
}
