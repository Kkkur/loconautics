/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.BooleanOp
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.logistics.chute;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChuteShapes {
    static Map<BlockState, VoxelShape> cache = new HashMap<BlockState, VoxelShape>();
    static Map<BlockState, VoxelShape> collisionCache = new HashMap<BlockState, VoxelShape>();
    public static final VoxelShape INTERSECTION_MASK = Block.box((double)0.0, (double)-16.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0);
    public static final VoxelShape COLLISION_MASK = Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)24.0, (double)16.0);
    public static final VoxelShape PANEL = Block.box((double)1.0, (double)-15.0, (double)0.0, (double)15.0, (double)4.0, (double)1.0);

    public static VoxelShape createShape(BlockState state) {
        boolean intersection;
        if (AllBlocks.SMART_CHUTE.has(state)) {
            return Shapes.block();
        }
        Direction direction = (Direction)state.getValue((Property)ChuteBlock.FACING);
        ChuteBlock.Shape shape = (ChuteBlock.Shape)((Object)state.getValue(ChuteBlock.SHAPE));
        boolean bl = intersection = shape == ChuteBlock.Shape.INTERSECTION || shape == ChuteBlock.Shape.ENCASED;
        if (direction == Direction.DOWN) {
            return intersection ? Shapes.block() : AllShapes.CHUTE;
        }
        VoxelShape combineWith = intersection ? Shapes.block() : Shapes.empty();
        VoxelShape result = Shapes.or((VoxelShape)combineWith, (VoxelShape)AllShapes.CHUTE_SLOPE.get(direction));
        if (intersection) {
            result = Shapes.joinUnoptimized((VoxelShape)INTERSECTION_MASK, (VoxelShape)result, (BooleanOp)BooleanOp.AND);
        }
        return result;
    }

    public static VoxelShape getShape(BlockState state) {
        if (cache.containsKey(state)) {
            return cache.get(state);
        }
        VoxelShape createdShape = ChuteShapes.createShape(state);
        cache.put(state, createdShape);
        return createdShape;
    }

    public static VoxelShape getCollisionShape(BlockState state) {
        if (collisionCache.containsKey(state)) {
            return collisionCache.get(state);
        }
        VoxelShape createdShape = Shapes.joinUnoptimized((VoxelShape)COLLISION_MASK, (VoxelShape)ChuteShapes.getShape(state), (BooleanOp)BooleanOp.AND);
        collisionCache.put(state, createdShape);
        return createdShape;
    }

    public static VoxelShape createSlope() {
        VoxelShape shape = Shapes.empty();
        for (int i = 0; i < 16; ++i) {
            float offset = (float)i / 16.0f;
            shape = Shapes.join((VoxelShape)shape, (VoxelShape)PANEL.move(0.0, (double)offset, (double)offset), (BooleanOp)BooleanOp.OR);
        }
        return shape;
    }
}
