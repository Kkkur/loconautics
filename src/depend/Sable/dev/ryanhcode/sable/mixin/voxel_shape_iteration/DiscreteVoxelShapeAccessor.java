/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.shapes.DiscreteVoxelShape
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.ryanhcode.sable.mixin.voxel_shape_iteration;

import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={DiscreteVoxelShape.class})
public interface DiscreteVoxelShapeAccessor {
    @Accessor
    public int getXSize();

    @Accessor
    public int getYSize();

    @Accessor
    public int getZSize();
}
