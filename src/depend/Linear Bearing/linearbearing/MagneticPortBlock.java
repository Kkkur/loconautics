/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.bearing.linearbearing;

import com.bearing.linearbearing.MagneticPortBlockEntity;
import com.bearing.linearbearing.ModBlocks;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MagneticPortBlock
extends DirectionalKineticBlock
implements IRotate,
IBE<MagneticPortBlockEntity> {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape SHAPE_NORTH = Shapes.box((double)0.0, (double)0.0, (double)0.0, (double)1.0, (double)1.0, (double)1.0);
    private static final VoxelShape SHAPE_SOUTH = Shapes.box((double)0.0, (double)0.0, (double)0.0, (double)1.0, (double)1.0, (double)1.0);
    private static final VoxelShape SHAPE_EAST = Shapes.box((double)0.0, (double)0.0, (double)0.0, (double)1.0, (double)1.0, (double)1.0);
    private static final VoxelShape SHAPE_WEST = Shapes.box((double)0.0, (double)0.0, (double)0.0, (double)1.0, (double)1.0, (double)1.0);
    private static final VoxelShape SHAPE_UP = Shapes.box((double)0.0, (double)0.0, (double)0.0, (double)1.0, (double)1.0, (double)1.0);
    private static final VoxelShape SHAPE_DOWN = Shapes.box((double)0.0, (double)0.0, (double)0.0, (double)1.0, (double)1.0, (double)1.0);

    public MagneticPortBlock(BlockBehaviour.Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)Direction.NORTH));
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        return switch (facing) {
            default -> throw new MatchException(null, null);
            case Direction.NORTH -> SHAPE_NORTH;
            case Direction.SOUTH -> SHAPE_SOUTH;
            case Direction.EAST -> SHAPE_EAST;
            case Direction.WEST -> SHAPE_WEST;
            case Direction.UP -> SHAPE_UP;
            case Direction.DOWN -> SHAPE_DOWN;
        };
    }

    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (!level.isClientSide && player != null) {
            if (!player.isCreative()) {
                ItemStack dropStack = new ItemStack((ItemLike)this.asItem());
                if (!player.getInventory().add(dropStack)) {
                    player.drop(dropStack, false);
                }
            }
            level.destroyBlock(pos, false, (Entity)player);
        }
        return InteractionResult.SUCCESS;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue((Property)FACING)).getAxis();
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.getBlockEntityType().create(pos, state);
    }

    public Class<MagneticPortBlockEntity> getBlockEntityClass() {
        return MagneticPortBlockEntity.class;
    }

    public BlockEntityType<? extends MagneticPortBlockEntity> getBlockEntityType() {
        return (BlockEntityType)ModBlocks.MAGNETIC_PORT_BE.get();
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == ((Direction)state.getValue((Property)FACING)).getOpposite();
    }
}
