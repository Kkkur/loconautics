/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.shapes.BooleanOp
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.trains.track;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrackVoxelShapes {
    public static VoxelShape orthogonal() {
        return Block.box((double)-14.0, (double)0.0, (double)0.0, (double)30.0, (double)4.0, (double)16.0);
    }

    public static VoxelShape longOrthogonalX() {
        return Block.box((double)-3.3, (double)0.0, (double)-14.0, (double)19.3, (double)4.0, (double)30.0);
    }

    public static VoxelShape longOrthogonalZ() {
        return Block.box((double)-14.0, (double)0.0, (double)-3.3, (double)30.0, (double)4.0, (double)19.3);
    }

    public static VoxelShape longOrthogonalZOffset() {
        return Block.box((double)-14.0, (double)0.0, (double)0.0, (double)30.0, (double)4.0, (double)24.0);
    }

    public static VoxelShape ascending() {
        VoxelShape shape = Block.box((double)-14.0, (double)0.0, (double)0.0, (double)30.0, (double)4.0, (double)4.0);
        VoxelShape[] shapes = new VoxelShape[6];
        for (int i = 0; i < 6; ++i) {
            int off = (i + 1) * 2;
            shapes[i] = Block.box((double)-14.0, (double)off, (double)off, (double)30.0, (double)(4 + off), (double)(4 + off));
        }
        return Shapes.or((VoxelShape)shape, (VoxelShape[])shapes);
    }

    public static VoxelShape diagonal() {
        VoxelShape shape = Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)4.0, (double)16.0);
        VoxelShape[] shapes = new VoxelShape[12];
        int off = 0;
        for (int i = 0; i < 6; ++i) {
            off = (i + 1) * 2;
            shapes[i * 2] = Block.box((double)off, (double)0.0, (double)off, (double)(16 + off), (double)4.0, (double)(16 + off));
            shapes[i * 2 + 1] = Block.box((double)(-off), (double)0.0, (double)(-off), (double)(16 - off), (double)4.0, (double)(16 - off));
        }
        shape = Shapes.or((VoxelShape)shape, (VoxelShape[])shapes);
        off = 20;
        shape = Shapes.join((VoxelShape)shape, (VoxelShape)Block.box((double)off, (double)0.0, (double)off, (double)(16 + off), (double)4.0, (double)(16 + off)), (BooleanOp)BooleanOp.ONLY_FIRST);
        shape = Shapes.join((VoxelShape)shape, (VoxelShape)Block.box((double)(-off), (double)0.0, (double)(-off), (double)(16 - off), (double)4.0, (double)(16 - off)), (BooleanOp)BooleanOp.ONLY_FIRST);
        off = 8;
        shape = Shapes.or((VoxelShape)shape, (VoxelShape)Block.box((double)off, (double)0.0, (double)off, (double)(16 + off), (double)4.0, (double)(16 + off)));
        shape = Shapes.or((VoxelShape)shape, (VoxelShape)Block.box((double)(-off), (double)0.0, (double)(-off), (double)(16 - off), (double)4.0, (double)(16 - off)));
        return shape.optimize();
    }
}
