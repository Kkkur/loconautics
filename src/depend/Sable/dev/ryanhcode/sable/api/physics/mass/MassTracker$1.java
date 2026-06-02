/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics.mass;

import dev.ryanhcode.sable.api.block.BlockSubLevelCustomCenterOfMass;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3dc;

class MassTracker.1
implements BiFunction<BlockGetter, BlockState, Vector3dc> {
    private final Int2ObjectOpenHashMap<Vector3dc> cache = new Int2ObjectOpenHashMap();

    MassTracker.1() {
    }

    @Override
    public Vector3dc apply(BlockGetter blockGetter, BlockState state) {
        return (Vector3dc)this.cache.computeIfAbsent(state.hashCode(), x -> {
            if (state.isAir()) {
                return JOMLConversion.HALF;
            }
            Block patt0$temp = state.getBlock();
            if (patt0$temp instanceof BlockSubLevelCustomCenterOfMass) {
                BlockSubLevelCustomCenterOfMass customCenterOfMass = (BlockSubLevelCustomCenterOfMass)patt0$temp;
                return customCenterOfMass.getCenterOfMass(blockGetter, state);
            }
            VoxelShape shape = state.getCollisionShape(blockGetter, BlockPos.ZERO);
            if (shape.isEmpty()) {
                return JOMLConversion.HALF;
            }
            if (state.isCollisionShapeFullBlock(blockGetter, BlockPos.ZERO)) {
                return JOMLConversion.HALF;
            }
            AABB bounds = shape.bounds().intersect(UNIT_BOUNDS);
            return JOMLConversion.toJOML((Position)bounds.getCenter());
        });
    }
}
