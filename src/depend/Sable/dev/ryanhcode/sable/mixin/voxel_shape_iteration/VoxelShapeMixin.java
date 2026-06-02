/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.phys.shapes.DiscreteVoxelShape
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.ryanhcode.sable.mixin.voxel_shape_iteration;

import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.mixinhelpers.voxel_shape_iteration.FastVoxelShapeIterator;
import dev.ryanhcode.sable.mixinterface.voxel_shape_iteration.FastVoxelShapeIterable;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Iterator;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={VoxelShape.class})
public abstract class VoxelShapeMixin
implements FastVoxelShapeIterable {
    @Unique
    private final Long2ObjectMap<FastVoxelShapeIterator> sable$boxIterator = new Long2ObjectArrayMap();
    @Shadow
    @Final
    protected DiscreteVoxelShape shape;

    @Shadow
    public abstract DoubleList getCoords(Direction.Axis var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterator<BoundingBox3dc> sable$allBoxes() {
        VoxelShapeMixin voxelShapeMixin = this;
        synchronized (voxelShapeMixin) {
            long id = Thread.currentThread().threadId();
            FastVoxelShapeIterator iterator = (FastVoxelShapeIterator)this.sable$boxIterator.get(id);
            if (iterator == null && (iterator = (FastVoxelShapeIterator)this.sable$boxIterator.get(id)) == null) {
                iterator = new FastVoxelShapeIterator(this.shape, this.getCoords(Direction.Axis.X).toDoubleArray(), this.getCoords(Direction.Axis.Y).toDoubleArray(), this.getCoords(Direction.Axis.Z).toDoubleArray());
                this.sable$boxIterator.put(id, (Object)iterator);
            }
            iterator.reset();
            return iterator;
        }
    }
}
