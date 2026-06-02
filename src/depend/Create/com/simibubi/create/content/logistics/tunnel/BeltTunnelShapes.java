/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.BooleanOp
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.logistics.tunnel;

import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeltTunnelShapes {
    private static VoxelShape block = Block.box((double)0.0, (double)-5.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0);
    private static VoxelShaper opening = VoxelShaper.forHorizontal((VoxelShape)Block.box((double)2.0, (double)-5.0, (double)14.0, (double)14.0, (double)10.0, (double)16.0), (Direction)Direction.SOUTH);
    private static final VoxelShaper STRAIGHT = VoxelShaper.forHorizontalAxis((VoxelShape)Shapes.join((VoxelShape)block, (VoxelShape)Shapes.or((VoxelShape)opening.get(Direction.SOUTH), (VoxelShape)opening.get(Direction.NORTH)), (BooleanOp)BooleanOp.NOT_SAME), (Direction.Axis)Direction.Axis.Z);
    private static final VoxelShaper TEE = VoxelShaper.forHorizontal((VoxelShape)Shapes.join((VoxelShape)block, (VoxelShape)Shapes.or((VoxelShape)opening.get(Direction.NORTH), (VoxelShape[])new VoxelShape[]{opening.get(Direction.WEST), opening.get(Direction.EAST)}), (BooleanOp)BooleanOp.NOT_SAME), (Direction)Direction.SOUTH);
    private static final VoxelShape CROSS = Shapes.join((VoxelShape)block, (VoxelShape)Shapes.or((VoxelShape)opening.get(Direction.SOUTH), (VoxelShape[])new VoxelShape[]{opening.get(Direction.NORTH), opening.get(Direction.WEST), opening.get(Direction.EAST)}), (BooleanOp)BooleanOp.NOT_SAME);

    public static VoxelShape getShape(BlockState state) {
        BeltTunnelBlock.Shape shape = (BeltTunnelBlock.Shape)((Object)state.getValue(BeltTunnelBlock.SHAPE));
        Direction.Axis axis = (Direction.Axis)state.getValue(BeltTunnelBlock.HORIZONTAL_AXIS);
        if (shape == BeltTunnelBlock.Shape.CROSS) {
            return CROSS;
        }
        if (BeltTunnelBlock.isStraight(state)) {
            return STRAIGHT.get(axis);
        }
        if (shape == BeltTunnelBlock.Shape.T_LEFT) {
            return TEE.get(axis == Direction.Axis.Z ? Direction.EAST : Direction.NORTH);
        }
        if (shape == BeltTunnelBlock.Shape.T_RIGHT) {
            return TEE.get(axis == Direction.Axis.Z ? Direction.WEST : Direction.SOUTH);
        }
        return Shapes.block();
    }
}
