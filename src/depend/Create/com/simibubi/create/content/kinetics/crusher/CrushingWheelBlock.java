/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.crusher;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrushingWheelBlock
extends RotatedPillarKineticBlock
implements IBE<CrushingWheelBlockEntity> {
    public CrushingWheelBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return (Direction.Axis)state.getValue((Property)AXIS);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.CRUSHING_WHEEL_COLLISION_SHAPE;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        for (Direction d : Iterate.directions) {
            if (d.getAxis() == state.getValue((Property)AXIS) || !AllBlocks.CRUSHING_WHEEL_CONTROLLER.has(worldIn.getBlockState(pos.relative(d)))) continue;
            worldIn.removeBlock(pos.relative(d), isMoving);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public void updateControllers(BlockState state, Level world, BlockPos pos, Direction side) {
        if (side.getAxis() == state.getValue((Property)AXIS)) {
            return;
        }
        if (world == null) {
            return;
        }
        BlockPos controllerPos = pos.relative(side);
        BlockPos otherWheelPos = pos.relative(side, 2);
        boolean controllerExists = AllBlocks.CRUSHING_WHEEL_CONTROLLER.has(world.getBlockState(controllerPos));
        boolean controllerIsValid = controllerExists && (Boolean)world.getBlockState(controllerPos).getValue((Property)CrushingWheelControllerBlock.VALID) != false;
        Direction controllerOldDirection = controllerExists ? (Direction)world.getBlockState(controllerPos).getValue((Property)CrushingWheelControllerBlock.FACING) : null;
        boolean controllerShouldExist = false;
        boolean controllerShouldBeValid = false;
        Direction controllerNewDirection = Direction.DOWN;
        BlockState otherState = world.getBlockState(otherWheelPos);
        if (AllBlocks.CRUSHING_WHEEL.has(otherState)) {
            controllerShouldExist = true;
            CrushingWheelBlockEntity be = (CrushingWheelBlockEntity)this.getBlockEntity((BlockGetter)world, pos);
            CrushingWheelBlockEntity otherBE = (CrushingWheelBlockEntity)this.getBlockEntity((BlockGetter)world, otherWheelPos);
            if (be != null && otherBE != null && be.getSpeed() > 0.0f != otherBE.getSpeed() > 0.0f && be.getSpeed() != 0.0f && otherBE.getSpeed() != 0.0f) {
                Direction.Axis wheelAxis = (Direction.Axis)state.getValue((Property)AXIS);
                Direction.Axis sideAxis = side.getAxis();
                int controllerADO = Math.round(Math.signum(be.getSpeed())) * side.getAxisDirection().getStep();
                Vec3 controllerDirVec = new Vec3(wheelAxis == Direction.Axis.X ? 1.0 : 0.0, wheelAxis == Direction.Axis.Y ? 1.0 : 0.0, wheelAxis == Direction.Axis.Z ? 1.0 : 0.0).cross(new Vec3(sideAxis == Direction.Axis.X ? 1.0 : 0.0, sideAxis == Direction.Axis.Y ? 1.0 : 0.0, sideAxis == Direction.Axis.Z ? 1.0 : 0.0));
                controllerNewDirection = Direction.getNearest((double)(controllerDirVec.x * (double)controllerADO), (double)(controllerDirVec.y * (double)controllerADO), (double)(controllerDirVec.z * (double)controllerADO));
                controllerShouldBeValid = true;
            }
            if (otherState.getValue((Property)AXIS) != state.getValue((Property)AXIS)) {
                controllerShouldExist = false;
            }
        }
        if (!controllerShouldExist) {
            if (controllerExists) {
                world.setBlockAndUpdate(controllerPos, Blocks.AIR.defaultBlockState());
            }
            return;
        }
        if (!controllerExists) {
            if (!world.getBlockState(controllerPos).canBeReplaced()) {
                return;
            }
            world.setBlockAndUpdate(controllerPos, (BlockState)((BlockState)AllBlocks.CRUSHING_WHEEL_CONTROLLER.getDefaultState().setValue((Property)CrushingWheelControllerBlock.VALID, (Comparable)Boolean.valueOf(controllerShouldBeValid))).setValue((Property)CrushingWheelControllerBlock.FACING, (Comparable)controllerNewDirection));
        } else if (controllerIsValid != controllerShouldBeValid || controllerOldDirection != controllerNewDirection) {
            world.setBlockAndUpdate(controllerPos, (BlockState)((BlockState)world.getBlockState(controllerPos).setValue((Property)CrushingWheelControllerBlock.VALID, (Comparable)Boolean.valueOf(controllerShouldBeValid))).setValue((Property)CrushingWheelControllerBlock.FACING, (Comparable)controllerNewDirection));
        }
        ((CrushingWheelControllerBlock)AllBlocks.CRUSHING_WHEEL_CONTROLLER.get()).updateSpeed(world.getBlockState(controllerPos), (LevelAccessor)world, controllerPos);
    }

    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn.getY() < (double)((float)pos.getY() + 1.25f) || !entityIn.onGround()) {
            return;
        }
        float speed = this.getBlockEntityOptional((BlockGetter)worldIn, pos).map(KineticBlockEntity::getSpeed).orElse(Float.valueOf(0.0f)).floatValue();
        double x = 0.0;
        double z = 0.0;
        if (state.getValue((Property)AXIS) == Direction.Axis.X) {
            z = speed / 20.0f;
            x += ((double)((float)pos.getX() + 0.5f) - entityIn.getX()) * (double)0.1f;
        }
        if (state.getValue((Property)AXIS) == Direction.Axis.Z) {
            x = speed / -20.0f;
            z += ((double)((float)pos.getZ() + 0.5f) - entityIn.getZ()) * (double)0.1f;
        }
        entityIn.setDeltaMovement(entityIn.getDeltaMovement().add(x, 0.0, z));
    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        for (Direction direction : Iterate.directions) {
            BlockPos neighbourPos = pos.relative(direction);
            BlockState neighbourState = worldIn.getBlockState(neighbourPos);
            Direction.Axis stateAxis = (Direction.Axis)state.getValue((Property)AXIS);
            if (AllBlocks.CRUSHING_WHEEL_CONTROLLER.has(neighbourState) && direction.getAxis() != stateAxis) {
                return false;
            }
            if (!AllBlocks.CRUSHING_WHEEL.has(neighbourState) || neighbourState.getValue((Property)AXIS) == stateAxis && stateAxis == direction.getAxis()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue((Property)AXIS);
    }

    @Override
    public float getParticleTargetRadius() {
        return 1.125f;
    }

    @Override
    public float getParticleInitialRadius() {
        return 1.0f;
    }

    @Override
    public Class<CrushingWheelBlockEntity> getBlockEntityClass() {
        return CrushingWheelBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CrushingWheelBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.CRUSHING_WHEEL.get();
    }
}
