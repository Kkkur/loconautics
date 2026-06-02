/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.decoration.slidingDoor;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.decoration.slidingDoor.DoorControl;
import com.simibubi.create.content.decoration.slidingDoor.DoorControlBehaviour;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.lang.ref.WeakReference;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

public class SlidingDoorMovementBehaviour
implements MovementBehaviour {
    @Override
    public boolean mustTickWhileDisabled() {
        return true;
    }

    @Override
    public void tick(MovementContext context) {
        BlockEntity blockEntity;
        StructureTemplate.StructureBlockInfo structureBlockInfo = context.contraption.getBlocks().get(context.localPos);
        if (structureBlockInfo == null) {
            return;
        }
        boolean open = SlidingDoorBlockEntity.isOpen(structureBlockInfo.state());
        if (!context.world.isClientSide()) {
            this.tickOpen(context, open);
        }
        if (!((blockEntity = context.contraption.getBlockEntityClientSide(context.localPos)) instanceof SlidingDoorBlockEntity)) {
            return;
        }
        SlidingDoorBlockEntity sdbe = (SlidingDoorBlockEntity)blockEntity;
        boolean wasSettled = sdbe.animation.settled();
        sdbe.animation.chase(open ? 1.0 : 0.0, (double)0.15f, LerpedFloat.Chaser.LINEAR);
        sdbe.animation.tickChaser();
        if (!wasSettled && sdbe.animation.settled() && !open) {
            context.world.playLocalSound(context.position.x, context.position.y, context.position.z, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.125f, 1.0f, false);
        }
    }

    protected void tickOpen(MovementContext context, boolean currentlyOpen) {
        boolean shouldOpen = this.shouldOpen(context);
        if (!this.shouldUpdate(context, shouldOpen)) {
            return;
        }
        if (currentlyOpen == shouldOpen) {
            return;
        }
        BlockPos pos = context.localPos;
        Contraption contraption = context.contraption;
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(pos);
        if (info == null || !info.state().hasProperty((Property)DoorBlock.OPEN)) {
            return;
        }
        this.toggleDoor(pos, contraption, info);
        Direction facing = this.getDoorFacing(context);
        BlockPos inWorldDoor = BlockPos.containing((Position)context.position).relative(facing);
        BlockState inWorldDoorState = context.world.getBlockState(inWorldDoor);
        Block block = inWorldDoorState.getBlock();
        if (block instanceof DoorBlock) {
            DoorBlock db = (DoorBlock)block;
            if (inWorldDoorState.hasProperty((Property)DoorBlock.OPEN) && inWorldDoorState.hasProperty((Property)DoorBlock.FACING) && inWorldDoorState.getOptionalValue((Property)DoorBlock.FACING).orElse(Direction.UP).getAxis() == facing.getAxis()) {
                db.setOpen(null, context.world, inWorldDoorState, inWorldDoor, shouldOpen);
            }
        }
        if (shouldOpen) {
            context.world.playSound(null, BlockPos.containing((Position)context.position), SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 0.125f, 1.0f);
        }
    }

    private void toggleDoor(BlockPos pos, Contraption contraption, StructureTemplate.StructureBlockInfo info) {
        BlockState newState = (BlockState)info.state().cycle((Property)DoorBlock.OPEN);
        contraption.entity.setBlock(pos, new StructureTemplate.StructureBlockInfo(info.pos(), newState, info.nbt()));
        BlockPos otherPos = newState.getValue((Property)DoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        info = contraption.getBlocks().get(otherPos);
        if (info != null && info.state().hasProperty((Property)DoorBlock.OPEN)) {
            newState = (BlockState)info.state().cycle((Property)DoorBlock.OPEN);
            contraption.entity.setBlock(otherPos, new StructureTemplate.StructureBlockInfo(info.pos(), newState, info.nbt()));
            contraption.invalidateColliders();
        }
    }

    protected boolean shouldUpdate(MovementContext context, boolean shouldOpen) {
        if (context.firstMovement && shouldOpen) {
            return false;
        }
        if (!context.data.contains("Open")) {
            context.data.putBoolean("Open", shouldOpen);
            return true;
        }
        boolean wasOpen = context.data.getBoolean("Open");
        context.data.putBoolean("Open", shouldOpen);
        return wasOpen != shouldOpen;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    protected boolean shouldOpen(MovementContext context) {
        if (context.disabled) {
            return false;
        }
        contraption = context.contraption;
        if (context.motion.length() < 0.0078125 && !contraption.entity.isStalled()) ** GOTO lbl-1000
        if (contraption instanceof ElevatorContraption) {
            ec = (ElevatorContraption)contraption;
            ** if (!ec.arrived) goto lbl-1000
        }
        ** GOTO lbl-1000
lbl-1000:
        // 2 sources

        {
            v0 = true;
            ** GOTO lbl12
        }
lbl-1000:
        // 2 sources

        {
            v0 = canOpen = false;
        }
lbl12:
        // 2 sources

        if (!canOpen) {
            context.temporaryData = null;
            return false;
        }
        var6_5 /* !! */  = context.temporaryData;
        if (var6_5 /* !! */  instanceof WeakReference && (var6_5 /* !! */  = (wr = (WeakReference)var6_5 /* !! */ ).get()) instanceof DoorControlBehaviour) {
            dcb = (DoorControlBehaviour)var6_5 /* !! */ ;
            if (dcb.blockEntity != null && !dcb.blockEntity.isRemoved()) {
                return this.shouldOpenAt(dcb, context);
            }
        }
        context.temporaryData = null;
        doorControls = null;
        if (contraption instanceof ElevatorContraption) {
            ec = (ElevatorContraption)contraption;
            doorControls = this.getElevatorDoorControl(ec, context);
        }
        if ((var6_5 /* !! */  = context.contraption.entity) instanceof CarriageContraptionEntity) {
            cce = (CarriageContraptionEntity)var6_5 /* !! */ ;
            doorControls = this.getTrainStationDoorControl(cce, context);
        }
        if (doorControls == null) {
            return false;
        }
        context.temporaryData = new WeakReference<DoorControlBehaviour>(doorControls);
        return this.shouldOpenAt(doorControls, context);
    }

    protected boolean shouldOpenAt(DoorControlBehaviour controller, MovementContext context) {
        if (controller.mode == DoorControl.ALL) {
            return true;
        }
        if (controller.mode == DoorControl.NONE) {
            return false;
        }
        return controller.mode.matches(this.getDoorFacing(context));
    }

    protected DoorControlBehaviour getElevatorDoorControl(ElevatorContraption ec, MovementContext context) {
        Integer currentTargetY = ec.getCurrentTargetY(context.world);
        if (currentTargetY == null) {
            return null;
        }
        ElevatorColumn.ColumnCoords columnCoords = ec.getGlobalColumn();
        if (columnCoords == null) {
            return null;
        }
        ElevatorColumn elevatorColumn = ElevatorColumn.get((LevelAccessor)context.world, columnCoords);
        if (elevatorColumn == null) {
            return null;
        }
        return BlockEntityBehaviour.get((BlockGetter)context.world, elevatorColumn.contactAt(currentTargetY), DoorControlBehaviour.TYPE);
    }

    protected DoorControlBehaviour getTrainStationDoorControl(CarriageContraptionEntity cce, MovementContext context) {
        Carriage carriage = cce.getCarriage();
        if (carriage == null || carriage.train == null) {
            return null;
        }
        GlobalStation currentStation = carriage.train.getCurrentStation();
        if (currentStation == null) {
            return null;
        }
        BlockPos stationPos = currentStation.getBlockEntityPos();
        ResourceKey<Level> stationDim = currentStation.getBlockEntityDimension();
        MinecraftServer server = context.world.getServer();
        if (server == null) {
            return null;
        }
        ServerLevel stationLevel = server.getLevel(stationDim);
        if (stationLevel == null || !stationLevel.isLoaded(stationPos)) {
            return null;
        }
        return BlockEntityBehaviour.get((BlockGetter)stationLevel, stationPos, DoorControlBehaviour.TYPE);
    }

    protected Direction getDoorFacing(MovementContext context) {
        Direction stateFacing = (Direction)context.state.getValue((Property)DoorBlock.FACING);
        Direction originalFacing = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)stateFacing.getAxis());
        Vec3 centerOfContraption = context.contraption.bounds.getCenter();
        Vec3 diff = Vec3.atCenterOf((Vec3i)context.localPos).add(Vec3.atLowerCornerOf((Vec3i)stateFacing.getNormal()).scale((double)-0.45f)).subtract(centerOfContraption);
        if (originalFacing.getAxis().choose(diff.x, diff.y, diff.z) < 0.0) {
            originalFacing = originalFacing.getOpposite();
        }
        Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)originalFacing.getNormal());
        directionVec = (Vec3)context.rotation.apply(directionVec);
        return Direction.getNearest((double)directionVec.x, (double)directionVec.y, (double)directionVec.z);
    }
}
