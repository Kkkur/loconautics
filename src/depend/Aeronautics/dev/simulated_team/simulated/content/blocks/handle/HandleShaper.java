/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.Direction
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.simulated_team.simulated.content.blocks.handle;

import dev.simulated_team.simulated.index.SimBlockShapes;
import java.util.Arrays;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HandleShaper
extends VoxelShaper {
    private VoxelShaper axisFalse;
    private VoxelShaper axisTrue;

    public static HandleShaper make() {
        HandleShaper shaper = new HandleShaper();
        shaper.axisFalse = HandleShaper.forDirectional((VoxelShape)SimBlockShapes.HANDLE, (Direction)Direction.UP);
        shaper.axisTrue = HandleShaper.forDirectional((VoxelShape)HandleShaper.rotatedCopy((VoxelShape)SimBlockShapes.HANDLE, (Vec3)new Vec3(0.0, 90.0, 0.0)), (Direction)Direction.UP);
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
