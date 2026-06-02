/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.actors.psi;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.psi.PSIActorVisual;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlock;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceRenderer;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import java.util.Optional;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class PortableStorageInterfaceMovement
implements MovementBehaviour {
    static final String _workingPos_ = "WorkingPos";
    static final String _clientPrevPos_ = "ClientPrevPos";

    @Override
    public Vec3 getActiveAreaOffset(MovementContext context) {
        return Vec3.atLowerCornerOf((Vec3i)((Direction)context.state.getValue((Property)PortableStorageInterfaceBlock.FACING)).getNormal()).scale((double)1.85f);
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    @Nullable
    public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        return new PSIActorVisual(visualizationContext, simulationWorld, movementContext);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!VisualizationManager.supportsVisualization((LevelAccessor)context.world)) {
            PortableStorageInterfaceRenderer.renderInContraption(context, renderWorld, matrices, buffer);
        }
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        boolean onCarriage = context.contraption instanceof CarriageContraption;
        if (onCarriage && context.motion.length() > 0.25) {
            return;
        }
        if (!this.findInterface(context, pos)) {
            context.data.remove(_workingPos_);
        }
    }

    @Override
    public void tick(MovementContext context) {
        Optional<Direction> currentFacingIfValid;
        boolean onCarriage;
        if (context.world.isClientSide) {
            PortableStorageInterfaceMovement.getAnimation(context).tickChaser();
        }
        if ((onCarriage = context.contraption instanceof CarriageContraption) && context.motion.length() > 0.25) {
            return;
        }
        if (context.world.isClientSide) {
            BlockPos pos = BlockPos.containing((Position)context.position);
            if (!this.findInterface(context, pos)) {
                this.reset(context);
            }
            return;
        }
        if (!context.data.contains(_workingPos_)) {
            if (context.stall) {
                this.cancelStall(context);
            }
            return;
        }
        BlockPos pos = NBTHelper.readBlockPos((CompoundTag)context.data, (String)_workingPos_);
        Vec3 target = VecHelper.getCenterOf((Vec3i)pos);
        if (!context.stall && !onCarriage && context.position.closerThan((Position)target, target.distanceTo(context.position.add(context.motion)))) {
            context.stall = true;
        }
        if (!(currentFacingIfValid = this.getCurrentFacingIfValid(context)).isPresent()) {
            this.reset(context);
            return;
        }
        PortableStorageInterfaceBlockEntity stationaryInterface = this.getStationaryInterfaceAt(context.world, pos, context.state, currentFacingIfValid.get());
        if (stationaryInterface == null) {
            this.reset(context);
            return;
        }
        if (stationaryInterface.connectedEntity == null) {
            stationaryInterface.startTransferringTo(context.contraption, stationaryInterface.distance);
        }
        boolean timerBelow = stationaryInterface.transferTimer <= 4;
        stationaryInterface.keepAlive = 2;
        if (context.stall && timerBelow) {
            context.stall = false;
        }
    }

    protected boolean findInterface(MovementContext context, BlockPos pos) {
        CarriageContraption cc;
        Contraption contraption = context.contraption;
        if (contraption instanceof CarriageContraption && !(cc = (CarriageContraption)contraption).notInPortal()) {
            return false;
        }
        Optional<Direction> currentFacingIfValid = this.getCurrentFacingIfValid(context);
        if (!currentFacingIfValid.isPresent()) {
            return false;
        }
        Direction currentFacing = currentFacingIfValid.get();
        PortableStorageInterfaceBlockEntity psi = this.findStationaryInterface(context.world, pos, context.state, currentFacing);
        if (psi == null) {
            return false;
        }
        if (psi.isPowered()) {
            return false;
        }
        context.data.put(_workingPos_, NbtUtils.writeBlockPos((BlockPos)psi.getBlockPos()));
        if (!context.world.isClientSide) {
            Vec3 diff = VecHelper.getCenterOf((Vec3i)psi.getBlockPos()).subtract(context.position);
            diff = VecHelper.project((Vec3)diff, (Vec3)Vec3.atLowerCornerOf((Vec3i)currentFacing.getNormal()));
            float distance = (float)(diff.length() + (double)1.85f - 1.0);
            psi.startTransferringTo(context.contraption, distance);
        } else {
            context.data.put(_clientPrevPos_, NbtUtils.writeBlockPos((BlockPos)pos));
            if (context.contraption instanceof CarriageContraption || context.contraption.entity.isStalled() || context.motion.lengthSqr() == 0.0) {
                PortableStorageInterfaceMovement.getAnimation(context).chase((double)(psi.getConnectionDistance() / 2.0f), 0.25, LerpedFloat.Chaser.LINEAR);
            }
        }
        return true;
    }

    @Override
    public void stopMoving(MovementContext context) {
    }

    @Override
    public void cancelStall(MovementContext context) {
        this.reset(context);
    }

    public void reset(MovementContext context) {
        context.data.remove(_clientPrevPos_);
        context.data.remove(_workingPos_);
        context.stall = false;
        PortableStorageInterfaceMovement.getAnimation(context).chase(0.0, 0.25, LerpedFloat.Chaser.LINEAR);
    }

    private PortableStorageInterfaceBlockEntity findStationaryInterface(Level world, BlockPos pos, BlockState state, Direction facing) {
        for (int i = 0; i < 2; ++i) {
            PortableStorageInterfaceBlockEntity interfaceAt = this.getStationaryInterfaceAt(world, pos.relative(facing, i), state, facing);
            if (interfaceAt == null) continue;
            return interfaceAt;
        }
        return null;
    }

    private PortableStorageInterfaceBlockEntity getStationaryInterfaceAt(Level world, BlockPos pos, BlockState state, Direction facing) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof PortableStorageInterfaceBlockEntity)) {
            return null;
        }
        PortableStorageInterfaceBlockEntity psi = (PortableStorageInterfaceBlockEntity)blockEntity;
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() != state.getBlock()) {
            return null;
        }
        if (blockState.getValue((Property)PortableStorageInterfaceBlock.FACING) != facing.getOpposite()) {
            return null;
        }
        if (psi.isPowered()) {
            return null;
        }
        return psi;
    }

    private Optional<Direction> getCurrentFacingIfValid(MovementContext context) {
        Direction facingFromVector;
        Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)((Direction)context.state.getValue((Property)PortableStorageInterfaceBlock.FACING)).getNormal());
        if ((directionVec = (Vec3)context.rotation.apply(directionVec)).distanceTo(Vec3.atLowerCornerOf((Vec3i)(facingFromVector = Direction.getNearest((double)directionVec.x, (double)directionVec.y, (double)directionVec.z)).getNormal())) > 0.5) {
            return Optional.empty();
        }
        return Optional.of(facingFromVector);
    }

    public static LerpedFloat getAnimation(MovementContext context) {
        Object object = context.temporaryData;
        if (!(object instanceof LerpedFloat)) {
            LerpedFloat nlf = LerpedFloat.linear();
            context.temporaryData = nlf;
            return nlf;
        }
        LerpedFloat lf = (LerpedFloat)object;
        return lf;
    }
}
