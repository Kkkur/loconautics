/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.redstone;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DirectedDirectionalBlock
extends HorizontalDirectionalBlock
implements IWrenchable,
TransformableBlock {
    public static final EnumProperty<AttachFace> TARGET = EnumProperty.create((String)"target", AttachFace.class);
    public static final MapCodec<DirectedDirectionalBlock> CODEC = DirectedDirectionalBlock.simpleCodec(DirectedDirectionalBlock::new);

    public DirectedDirectionalBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(TARGET, (Comparable)AttachFace.WALL));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{TARGET, FACING}));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        int n = 0;
        Direction[] directionArray = pContext.getNearestLookingDirections();
        int n2 = directionArray.length;
        if (n < n2) {
            Direction direction = directionArray[n];
            BlockState blockstate = direction.getAxis() == Direction.Axis.Y ? (BlockState)((BlockState)this.defaultBlockState().setValue(TARGET, (Comparable)(direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR))).setValue((Property)FACING, (Comparable)pContext.getHorizontalDirection()) : (BlockState)((BlockState)this.defaultBlockState().setValue(TARGET, (Comparable)AttachFace.WALL)).setValue((Property)FACING, (Comparable)direction.getOpposite());
            return blockstate;
        }
        return null;
    }

    public static Direction getTargetDirection(BlockState pState) {
        switch ((AttachFace)pState.getValue(TARGET)) {
            case CEILING: {
                return Direction.UP;
            }
            case FLOOR: {
                return Direction.DOWN;
            }
        }
        return (Direction)pState.getValue((Property)FACING);
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        if (targetedFace.getAxis() == Direction.Axis.Y) {
            return IWrenchable.super.getRotatedBlockState(originalState, targetedFace);
        }
        Direction targetDirection = DirectedDirectionalBlock.getTargetDirection(originalState);
        Direction newFacing = targetDirection.getClockWise(targetedFace.getAxis());
        if (targetedFace.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
            newFacing = newFacing.getOpposite();
        }
        if (newFacing.getAxis() == Direction.Axis.Y) {
            return (BlockState)originalState.setValue(TARGET, (Comparable)(newFacing == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR));
        }
        return (BlockState)((BlockState)originalState.setValue(TARGET, (Comparable)AttachFace.WALL)).setValue((Property)FACING, (Comparable)newFacing);
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.mirror != null) {
            state = this.mirror(state, transform.mirror);
        }
        if (transform.rotationAxis == Direction.Axis.Y) {
            return this.rotate(state, transform.rotation);
        }
        Direction targetDirection = DirectedDirectionalBlock.getTargetDirection(state);
        Direction newFacing = transform.rotateFacing(targetDirection);
        if (newFacing.getAxis() == Direction.Axis.Y) {
            return (BlockState)state.setValue(TARGET, (Comparable)(newFacing == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR));
        }
        return (BlockState)((BlockState)state.setValue(TARGET, (Comparable)AttachFace.WALL)).setValue((Property)FACING, (Comparable)newFacing);
    }

    @NotNull
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
