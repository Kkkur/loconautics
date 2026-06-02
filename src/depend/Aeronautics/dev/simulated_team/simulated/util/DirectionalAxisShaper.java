/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.Direction
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.simulated_team.simulated.util;

import java.util.Arrays;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DirectionalAxisShaper
extends VoxelShaper {
    private VoxelShaper axisFalse;
    private VoxelShaper axisTrue;

    public static DirectionalAxisShaper make(VoxelShape shape) {
        DirectionalAxisShaper shaper = new DirectionalAxisShaper();
        shaper.axisFalse = DirectionalAxisShaper.forDirectional((VoxelShape)shape, (Direction)Direction.UP);
        shaper.axisTrue = DirectionalAxisShaper.forDirectional((VoxelShape)DirectionalAxisShaper.rotatedCopy((VoxelShape)shape, (Vec3)new Vec3(0.0, 90.0, 0.0)), (Direction)Direction.UP);
        Arrays.asList(Direction.EAST, Direction.WEST).forEach(direction -> {
            VoxelShape mem = shaper.axisFalse.get(direction);
            shaper.axisFalse.withShape(shaper.axisTrue.get(direction), direction);
            shaper.axisTrue.withShape(mem, direction);
        });
        return shaper;
    }

    public VoxelShape get(Direction direction, boolean axisAlong) {
        return (axisAlong ? this.axisTrue : this.axisFalse).get(direction);
    }
}
