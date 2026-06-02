/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.pipes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

public class SmartFluidPipeBlockEntity
extends SmartBlockEntity
implements Clearable {
    private FilteringBehaviour filter;

    public SmartFluidPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new SmartPipeBehaviour(this));
        this.filter = new FilteringBehaviour(this, new SmartPipeFilterSlot()).forFluids().withCallback(this::onFilterChanged);
        behaviours.add(this.filter);
        this.registerAwardables(behaviours, FluidPropagator.getSharedTriggers());
    }

    public void clearContent() {
        this.filter.setFilter(ItemStack.EMPTY);
    }

    private void onFilterChanged(ItemStack newFilter) {
        if (!this.level.isClientSide) {
            FluidPropagator.propagateChangedPipe((LevelAccessor)this.level, this.worldPosition, this.getBlockState());
        }
    }

    class SmartPipeBehaviour
    extends StraightPipeBlockEntity.StraightPipeFluidTransportBehaviour {
        public SmartPipeBehaviour(SmartBlockEntity be) {
            super(be);
        }

        @Override
        public boolean canPullFluidFrom(FluidStack fluid, BlockState state, Direction direction) {
            if (fluid.isEmpty() || SmartFluidPipeBlockEntity.this.filter != null && SmartFluidPipeBlockEntity.this.filter.test(fluid)) {
                return super.canPullFluidFrom(fluid, state, direction);
            }
            return false;
        }

        @Override
        public boolean canHaveFlowToward(BlockState state, Direction direction) {
            return state.getBlock() instanceof SmartFluidPipeBlock && SmartFluidPipeBlock.getPipeAxis(state) == direction.getAxis();
        }
    }

    static class SmartPipeFilterSlot
    extends ValueBoxTransform {
        SmartPipeFilterSlot() {
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            float y;
            AttachFace face = (AttachFace)state.getValue((Property)SmartFluidPipeBlock.FACE);
            float f = face == AttachFace.CEILING ? 0.55f : (y = face == AttachFace.WALL ? 11.4f : 15.45f);
            float z = face == AttachFace.CEILING ? 4.6f : (face == AttachFace.WALL ? 0.55f : 4.625f);
            return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)8.0, (double)y, (double)z), (double)this.angleY(state), (Direction.Axis)Direction.Axis.Y);
        }

        @Override
        public float getScale() {
            return super.getScale() * 1.02f;
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            AttachFace face = (AttachFace)state.getValue((Property)SmartFluidPipeBlock.FACE);
            ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(this.angleY(state))).rotateXDegrees(face == AttachFace.CEILING ? -45.0f : 45.0f);
        }

        protected float angleY(BlockState state) {
            AttachFace face = (AttachFace)state.getValue((Property)SmartFluidPipeBlock.FACE);
            float horizontalAngle = AngleHelper.horizontalAngle((Direction)((Direction)state.getValue((Property)SmartFluidPipeBlock.FACING)));
            if (face == AttachFace.WALL) {
                horizontalAngle += 180.0f;
            }
            return horizontalAngle;
        }
    }
}
