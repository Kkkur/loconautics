/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Direction$Plane
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.BooleanOp
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.belt;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import java.util.HashMap;
import java.util.Map;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeltShapes {
    private static final VoxelShape SLOPE_DESC_PART = BeltShapes.makeSlopePart(false);
    private static final VoxelShape SLOPE_ASC_PART = BeltShapes.makeSlopePart(true);
    private static final VoxelShape SIDEWAYS_FULL_PART = BeltShapes.makeSidewaysFull();
    private static final VoxelShape SIDEWAYS_END_PART = BeltShapes.makeSidewaysEnding();
    private static final VoxelShape FLAT_FULL_PART = BeltShapes.makeFlatFull();
    private static final VoxelShape FLAT_END_PART = BeltShapes.makeFlatEnding();
    private static final VoxelShape SOUTH_MASK = Block.box((double)0.0, (double)-5.0, (double)8.0, (double)16.0, (double)21.0, (double)16.0);
    private static final VoxelShape NORTH_MASK = Block.box((double)0.0, (double)-5.0, (double)0.0, (double)16.0, (double)21.0, (double)8.0);
    private static final VoxelShaper VERTICAL_FULL = VerticalBeltShaper.make(FLAT_FULL_PART);
    private static final VoxelShaper VERTICAL_END = VerticalBeltShaper.make(BeltShapes.compose(FLAT_END_PART, FLAT_FULL_PART));
    private static final VoxelShaper VERTICAL_START = VerticalBeltShaper.make(BeltShapes.compose(FLAT_FULL_PART, FLAT_END_PART));
    private static final VoxelShaper FLAT_FULL = VoxelShaper.forHorizontalAxis((VoxelShape)FLAT_FULL_PART, (Direction.Axis)Direction.Axis.Z);
    private static final VoxelShaper FLAT_END = VoxelShaper.forHorizontal((VoxelShape)BeltShapes.compose(FLAT_END_PART, FLAT_FULL_PART), (Direction)Direction.SOUTH);
    private static final VoxelShaper FLAT_START = VoxelShaper.forHorizontal((VoxelShape)BeltShapes.compose(FLAT_FULL_PART, FLAT_END_PART), (Direction)Direction.SOUTH);
    private static final VoxelShaper SIDE_FULL = VoxelShaper.forHorizontalAxis((VoxelShape)SIDEWAYS_FULL_PART, (Direction.Axis)Direction.Axis.Z);
    private static final VoxelShaper SIDE_END = VoxelShaper.forHorizontal((VoxelShape)BeltShapes.compose(SIDEWAYS_END_PART, SIDEWAYS_FULL_PART), (Direction)Direction.SOUTH);
    private static final VoxelShaper SIDE_START = VoxelShaper.forHorizontal((VoxelShape)BeltShapes.compose(SIDEWAYS_FULL_PART, SIDEWAYS_END_PART), (Direction)Direction.SOUTH);
    private static final VoxelShaper SLOPE_DESC = VoxelShaper.forHorizontal((VoxelShape)SLOPE_DESC_PART, (Direction)Direction.SOUTH);
    private static final VoxelShaper SLOPE_ASC = VoxelShaper.forHorizontal((VoxelShape)SLOPE_ASC_PART, (Direction)Direction.SOUTH);
    private static final VoxelShaper SLOPE_DESC_END = VoxelShaper.forHorizontal((VoxelShape)BeltShapes.compose(FLAT_END_PART, SLOPE_DESC_PART), (Direction)Direction.SOUTH);
    private static final VoxelShaper SLOPE_DESC_START = VoxelShaper.forHorizontal((VoxelShape)BeltShapes.compose(SLOPE_DESC_PART, FLAT_END_PART), (Direction)Direction.SOUTH);
    private static final VoxelShaper SLOPE_ASC_END = VoxelShaper.forHorizontal((VoxelShape)BeltShapes.compose(FLAT_END_PART, SLOPE_ASC_PART), (Direction)Direction.SOUTH);
    private static final VoxelShaper SLOPE_ASC_START = VoxelShaper.forHorizontal((VoxelShape)BeltShapes.compose(SLOPE_ASC_PART, FLAT_END_PART), (Direction)Direction.SOUTH);
    private static final VoxelShaper PARTIAL_CASING = VoxelShaper.forHorizontal((VoxelShape)Block.box((double)0.0, (double)0.0, (double)5.0, (double)16.0, (double)11.0, (double)16.0), (Direction)Direction.SOUTH);
    static Map<BlockState, VoxelShape> cache = new HashMap<BlockState, VoxelShape>();
    static Map<BlockState, VoxelShape> collisionCache = new HashMap<BlockState, VoxelShape>();

    private static VoxelShape compose(VoxelShape southPart, VoxelShape northPart) {
        return Shapes.or((VoxelShape)Shapes.joinUnoptimized((VoxelShape)SOUTH_MASK, (VoxelShape)southPart, (BooleanOp)BooleanOp.AND), (VoxelShape)Shapes.joinUnoptimized((VoxelShape)NORTH_MASK, (VoxelShape)northPart, (BooleanOp)BooleanOp.AND));
    }

    private static VoxelShape makeSlopePart(boolean ascendingInstead) {
        VoxelShape slice = Block.box((double)1.0, (double)0.0, (double)15.0, (double)15.0, (double)11.0, (double)16.0);
        VoxelShape result = Shapes.empty();
        for (int i = 0; i < 16; ++i) {
            int yOffset = ascendingInstead ? 10 - i : i - 5;
            result = Shapes.or((VoxelShape)result, (VoxelShape)slice.move(0.0, (double)((float)yOffset / 16.0f), (double)((float)(-i) / 16.0f)));
        }
        return result;
    }

    private static VoxelShape makeFlatEnding() {
        return Shapes.or((VoxelShape)Block.box((double)1.0, (double)4.0, (double)0.0, (double)15.0, (double)12.0, (double)16.0), (VoxelShape)Block.box((double)1.0, (double)3.0, (double)1.0, (double)15.0, (double)13.0, (double)15.0));
    }

    private static VoxelShape makeFlatFull() {
        return Block.box((double)1.0, (double)3.0, (double)0.0, (double)15.0, (double)13.0, (double)16.0);
    }

    private static VoxelShape makeSidewaysEnding() {
        return Shapes.or((VoxelShape)Block.box((double)4.0, (double)1.0, (double)0.0, (double)12.0, (double)15.0, (double)16.0), (VoxelShape)Block.box((double)3.0, (double)1.0, (double)1.0, (double)13.0, (double)15.0, (double)15.0));
    }

    private static VoxelShape makeSidewaysFull() {
        return Block.box((double)3.0, (double)1.0, (double)0.0, (double)13.0, (double)15.0, (double)16.0);
    }

    public static VoxelShape getShape(BlockState state) {
        if (cache.containsKey(state)) {
            return cache.get(state);
        }
        VoxelShape createdShape = Shapes.or((VoxelShape)BeltShapes.getBeltShape(state), (VoxelShape)BeltShapes.getCasingShape(state));
        cache.put(state, createdShape);
        return createdShape;
    }

    public static VoxelShape getCollisionShape(BlockState state) {
        if (collisionCache.containsKey(state)) {
            return collisionCache.get(state);
        }
        VoxelShape createdShape = Shapes.joinUnoptimized((VoxelShape)AllShapes.BELT_COLLISION_MASK, (VoxelShape)BeltShapes.getShape(state), (BooleanOp)BooleanOp.AND);
        collisionCache.put(state, createdShape);
        return createdShape;
    }

    private static VoxelShape getBeltShape(BlockState state) {
        Direction facing = (Direction)state.getValue(BeltBlock.HORIZONTAL_FACING);
        Direction.Axis axis = facing.getAxis();
        BeltPart part = (BeltPart)((Object)state.getValue(BeltBlock.PART));
        BeltSlope slope = (BeltSlope)((Object)state.getValue(BeltBlock.SLOPE));
        if (slope == BeltSlope.VERTICAL) {
            if (part == BeltPart.MIDDLE || part == BeltPart.PULLEY) {
                return VERTICAL_FULL.get(axis);
            }
            return (part == BeltPart.START ? VERTICAL_START : VERTICAL_END).get(facing);
        }
        if (slope == BeltSlope.HORIZONTAL) {
            if (part == BeltPart.MIDDLE || part == BeltPart.PULLEY) {
                return FLAT_FULL.get(axis);
            }
            return (part == BeltPart.START ? FLAT_START : FLAT_END).get(facing);
        }
        if (slope == BeltSlope.SIDEWAYS) {
            if (part == BeltPart.MIDDLE || part == BeltPart.PULLEY) {
                return SIDE_FULL.get(axis);
            }
            return (part == BeltPart.START ? SIDE_START : SIDE_END).get(facing);
        }
        if (part == BeltPart.MIDDLE || part == BeltPart.PULLEY) {
            return (slope == BeltSlope.DOWNWARD ? SLOPE_DESC : SLOPE_ASC).get(facing);
        }
        if (part == BeltPart.START) {
            return (slope == BeltSlope.DOWNWARD ? SLOPE_DESC_START : SLOPE_ASC_START).get(facing);
        }
        if (part == BeltPart.END) {
            return (slope == BeltSlope.DOWNWARD ? SLOPE_DESC_END : SLOPE_ASC_END).get(facing);
        }
        return Shapes.empty();
    }

    private static VoxelShape getCasingShape(BlockState state) {
        if (!((Boolean)state.getValue((Property)BeltBlock.CASING)).booleanValue()) {
            return Shapes.empty();
        }
        Direction facing = (Direction)state.getValue(BeltBlock.HORIZONTAL_FACING);
        BeltPart part = (BeltPart)((Object)state.getValue(BeltBlock.PART));
        BeltSlope slope = (BeltSlope)((Object)state.getValue(BeltBlock.SLOPE));
        if (slope == BeltSlope.VERTICAL) {
            return Shapes.empty();
        }
        if (slope == BeltSlope.SIDEWAYS) {
            return Shapes.empty();
        }
        if (slope == BeltSlope.HORIZONTAL) {
            return AllShapes.CASING_11PX.get(Direction.UP);
        }
        if (part == BeltPart.MIDDLE || part == BeltPart.PULLEY) {
            return PARTIAL_CASING.get(slope == BeltSlope.UPWARD ? facing : facing.getOpposite());
        }
        if (part == BeltPart.START) {
            return slope == BeltSlope.UPWARD ? AllShapes.CASING_11PX.get(Direction.UP) : PARTIAL_CASING.get(facing.getOpposite());
        }
        if (part == BeltPart.END) {
            return slope == BeltSlope.DOWNWARD ? AllShapes.CASING_11PX.get(Direction.UP) : PARTIAL_CASING.get(facing);
        }
        return Shapes.block();
    }

    private static class VerticalBeltShaper
    extends VoxelShaper {
        private VerticalBeltShaper() {
        }

        public static VoxelShaper make(VoxelShape southBeltShape) {
            return VerticalBeltShaper.forDirectionsWithRotation((VoxelShape)VerticalBeltShaper.rotatedCopy((VoxelShape)southBeltShape, (Vec3)new Vec3(-90.0, 0.0, 0.0)), (Direction)Direction.SOUTH, (Iterable)Direction.Plane.HORIZONTAL, direction -> new Vec3(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 0.0 : 180.0, (double)(-direction.toYRot()), 0.0));
        }
    }
}
