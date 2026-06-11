package com.lycoris.loconautics.content.knuckle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public class KnuckleBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<KnuckleBlock> CODEC = simpleCodec(KnuckleBlock::new);

    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        // Boxes derived from knuckle.json elements (model units, 0-16).
        // Built for the "north" facing (the model's default orientation),
        // then rotated for the other horizontal directions.
        VoxelShape shape = Shapes.empty();
        shape = Shapes.or(shape, Block.box(1, 6, 11, 3, 11, 12));
        shape = Shapes.or(shape, Block.box(1, 10, 5, 3, 11, 11));
        shape = Shapes.or(shape, Block.box(1, 6, 5, 3, 7, 11));
        shape = Shapes.or(shape, Block.box(1, 6, 4, 3, 11, 5));
        shape = Shapes.or(shape, Block.box(0, 6, 3, 1, 11, 13));
        shape = Shapes.or(shape, Block.box(1, 9, 8, 4, 10, 10));
        shape = Shapes.or(shape, Block.box(1, 9, 6, 4, 10, 8));
        shape = Shapes.or(shape, Block.box(1, 8, 7, 4, 9, 9));
        shape = Shapes.or(shape, Block.box(1, 7, 8, 4, 8, 10));
        shape = Shapes.or(shape, Block.box(1, 7, 6, 4, 8, 8));
        shape = Shapes.or(shape, Block.box(4, 6, 5, 6, 11, 10));
        shape = Shapes.or(shape, Block.box(6, 6, 9, 8, 11, 11));
        shape = Shapes.or(shape, Block.box(8, 6.2, 9, 9, 10.8, 10));
        shape = Shapes.or(shape, Block.box(8.3, 6.5, 7, 9.8, 10.5, 9));
        shape = Shapes.or(shape, Block.box(5, 6, 10, 6, 11, 11));
        // Rotated element (22.5 deg around y, origin [6.5, 8.5, 4.5]) - approximated
        // with its axis-aligned bounding box to keep the shape valid/simple.
        shape = Shapes.or(shape, Block.box(3.71, 5.9, 3.66, 8.29, 11.1, 6.13));

        VoxelShape north = shape.optimize();
        SHAPES.put(Direction.NORTH, north);
        SHAPES.put(Direction.SOUTH, rotateY(north, 180));
        SHAPES.put(Direction.WEST, rotateY(north, 90));
        SHAPES.put(Direction.EAST, rotateY(north, 270));
    }

    /**
     * Rotates a voxel shape around the Y axis (centered on the block) by the given
     * number of degrees (must be a multiple of 90).
     */
    private static VoxelShape rotateY(VoxelShape shape, int degrees) {
        VoxelShape result = Shapes.empty();
        for (var box : shape.toAabbs()) {
            double minX = box.minX, minZ = box.minZ, maxX = box.maxX, maxZ = box.maxZ;
            double newMinX, newMinZ, newMaxX, newMaxZ;
            switch (((degrees % 360) + 360) % 360) {
                case 90 -> {
                    newMinX = minZ;
                    newMaxX = maxZ;
                    newMinZ = 1 - maxX;
                    newMaxZ = 1 - minX;
                }
                case 180 -> {
                    newMinX = 1 - maxX;
                    newMaxX = 1 - minX;
                    newMinZ = 1 - maxZ;
                    newMaxZ = 1 - minZ;
                }
                case 270 -> {
                    newMinX = 1 - maxZ;
                    newMaxX = 1 - minZ;
                    newMinZ = minX;
                    newMaxZ = maxX;
                }
                default -> {
                    newMinX = minX;
                    newMaxX = maxX;
                    newMinZ = minZ;
                    newMaxZ = maxZ;
                }
            }
            result = Shapes.or(result, Shapes.box(newMinX, box.minY, newMinZ, newMaxX, box.maxY, newMaxZ));
        }
        return result.optimize();
    }

    public KnuckleBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}