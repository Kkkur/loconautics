/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Quaterniond
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.physics.floating_block;

import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockCluster;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockMaterial;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class FloatingClusterContainer {
    public List<FloatingBlockCluster> clusters = new ArrayList<FloatingBlockCluster>();
    private final Long2ObjectMap<BlockState> addedBlocks = new Long2ObjectOpenHashMap();
    private final Long2ObjectMap<BlockState> removedBlocks = new Long2ObjectOpenHashMap();
    public final Vector3d positionOffset = new Vector3d();
    public final Quaterniond rotationOffset = new Quaterniond();
    public final Vector3d velocity = new Vector3d();
    public final Vector3d angularVelocity = new Vector3d();

    public boolean needsTicking() {
        return !this.addedBlocks.isEmpty() || !this.clusters.isEmpty();
    }

    public void processBlockChanges(Vector3dc centerOfMass) {
        Vector3d pos;
        BlockPos blockPos;
        for (Map.Entry entry : this.removedBlocks.entrySet()) {
            blockPos = BlockPos.of((long)((Long)entry.getKey()));
            pos = new Vector3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5).sub(centerOfMass);
            this.removeFloatingBlock((BlockState)entry.getValue(), pos);
        }
        for (Map.Entry entry : this.addedBlocks.entrySet()) {
            blockPos = BlockPos.of((long)((Long)entry.getKey()));
            pos = new Vector3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5).sub(centerOfMass);
            this.addFloatingBlock((BlockState)entry.getValue(), pos);
        }
        this.addedBlocks.clear();
        this.removedBlocks.clear();
    }

    public void addFloatingBlock(BlockState state, Vector3d pos) {
        FloatingBlockMaterial material = PhysicsBlockPropertyHelper.getFloatingMaterial(state);
        assert (material != null) : "Floating Material desync on adding";
        FloatingBlockCluster foundCluster = null;
        for (FloatingBlockCluster cluster : this.clusters) {
            if (!cluster.getMaterial().equals(material)) continue;
            foundCluster = cluster;
            break;
        }
        if (foundCluster == null) {
            foundCluster = new FloatingBlockCluster(material);
            this.clusters.add(foundCluster);
        }
        double scale = PhysicsBlockPropertyHelper.getFloatingScale(state);
        foundCluster.getBlockData().addFloatingBlock((Vector3dc)pos, scale);
    }

    public void removeFloatingBlock(BlockState state, Vector3d pos) {
        FloatingBlockMaterial material = PhysicsBlockPropertyHelper.getFloatingMaterial(state);
        assert (material != null) : "Floating Material desync on removing";
        FloatingBlockCluster foundCluster = null;
        for (FloatingBlockCluster cluster : this.clusters) {
            if (!cluster.getMaterial().equals(material)) continue;
            foundCluster = cluster;
            break;
        }
        if (foundCluster != null) {
            double scale = PhysicsBlockPropertyHelper.getFloatingScale(state);
            foundCluster.getBlockData().removeFloatingBlock((Vector3dc)pos, scale);
            if (foundCluster.getBlockData().blockCount == 0) {
                this.clusters.remove(foundCluster);
            }
        }
    }

    public void queueAddFloatingBlock(BlockState state, BlockPos pos) {
        long longKey = pos.asLong();
        if (PhysicsBlockPropertyHelper.getFloatingMaterial(state) != null && !this.removedBlocks.remove(longKey, (Object)state)) {
            this.addedBlocks.put(longKey, (Object)state);
        }
    }

    public void queueRemoveFloatingBlock(BlockState state, BlockPos pos) {
        long longKey = pos.asLong();
        if (PhysicsBlockPropertyHelper.getFloatingMaterial(state) != null && !this.addedBlocks.remove(longKey, (Object)state)) {
            this.removedBlocks.put(longKey, (Object)state);
        }
    }
}
