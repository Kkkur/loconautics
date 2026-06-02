/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Direction$Plane
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.belt;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

private static class BeltShapes.VerticalBeltShaper
extends VoxelShaper {
    private BeltShapes.VerticalBeltShaper() {
    }

    public static VoxelShaper make(VoxelShape southBeltShape) {
        return BeltShapes.VerticalBeltShaper.forDirectionsWithRotation((VoxelShape)BeltShapes.VerticalBeltShaper.rotatedCopy((VoxelShape)southBeltShape, (Vec3)new Vec3(-90.0, 0.0, 0.0)), (Direction)Direction.SOUTH, (Iterable)Direction.Plane.HORIZONTAL, direction -> new Vec3(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 0.0 : 180.0, (double)(-direction.toYRot()), 0.0));
    }
}
