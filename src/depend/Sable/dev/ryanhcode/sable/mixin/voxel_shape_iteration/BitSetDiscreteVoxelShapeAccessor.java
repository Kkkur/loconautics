/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package dev.ryanhcode.sable.mixin.voxel_shape_iteration;

import dev.ryanhcode.sable.mixin.voxel_shape_iteration.DiscreteVoxelShapeAccessor;
import java.util.BitSet;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={BitSetDiscreteVoxelShape.class})
public interface BitSetDiscreteVoxelShapeAccessor
extends DiscreteVoxelShapeAccessor {
    @Accessor
    public BitSet getStorage();

    @Invoker
    public boolean invokeIsZStripFull(int var1, int var2, int var3, int var4);

    @Invoker
    public boolean invokeIsXZRectangleFull(int var1, int var2, int var3, int var4, int var5);

    @Invoker
    public void invokeClearZStrip(int var1, int var2, int var3, int var4);
}
