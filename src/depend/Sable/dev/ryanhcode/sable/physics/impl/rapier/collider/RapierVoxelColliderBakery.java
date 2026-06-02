/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.physics.impl.rapier.collider;

import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.collider.SableCollisionContext;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.physics.chunk.VoxelNeighborhoodState;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper;
import dev.ryanhcode.sable.physics.impl.rapier.Rapier3D;
import dev.ryanhcode.sable.physics.impl.rapier.collider.RapierVoxelColliderData;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RapierVoxelColliderBakery {
    @NotNull
    private final BlockGetter level;
    private final Function<BlockState, RapierVoxelColliderData> blockPhysicsDataBuilder = Util.memoize(this::buildPhysicsDataForBlock);

    public RapierVoxelColliderBakery(@NotNull BlockGetter blockGetter) {
        this.level = blockGetter;
    }

    @NotNull
    public BlockGetter getLevel() {
        return this.level;
    }

    @NotNull
    private RapierVoxelColliderData buildPhysicsDataForBlock(BlockState childState) {
        VoxelShape shape;
        boolean liquid = VoxelNeighborhoodState.isLiquid(childState);
        double friction = PhysicsBlockPropertyHelper.getFriction(childState);
        double volume = PhysicsBlockPropertyHelper.getVolume(childState);
        double restitution = PhysicsBlockPropertyHelper.getRestitution(childState);
        BlockSubLevelCollisionCallback callback = BlockWithSubLevelCollisionCallback.sable$getCallback(childState);
        RapierVoxelColliderData entry = Rapier3D.createVoxelColliderEntry(friction, volume, restitution, liquid, callback);
        if (liquid) {
            entry.addBox(JOMLConversion.ZERO, (Vector3dc)new Vector3d(1.0, 1.0, 1.0));
            return entry;
        }
        Block block = childState.getBlock();
        if (block instanceof BlockSubLevelCollisionShape) {
            BlockSubLevelCollisionShape extension = (BlockSubLevelCollisionShape)block;
            shape = extension.getSubLevelCollisionShape(this.level, childState);
        } else {
            shape = childState.getCollisionShape(this.level, BlockPos.ZERO, (CollisionContext)SableCollisionContext.get());
        }
        if (shape.isEmpty()) {
            return RapierVoxelColliderData.EMPTY;
        }
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> entry.addBox((Vector3dc)new Vector3d(Math.max(minX, 0.0), Math.max(minY, 0.0), Math.max(minZ, 0.0)), (Vector3dc)new Vector3d(Math.min(maxX, 1.0), Math.min(maxY, 1.0), Math.min(maxZ, 1.0))));
        return entry;
    }

    @Nullable
    public RapierVoxelColliderData getPhysicsDataForBlock(BlockState state) {
        RapierVoxelColliderData data = this.blockPhysicsDataBuilder.apply(state);
        return data == RapierVoxelColliderData.EMPTY ? null : data;
    }
}
