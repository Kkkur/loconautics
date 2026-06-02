/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.SpawnPlacementTypes
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.equipment.bell;

import com.google.common.collect.Streams;
import com.simibubi.create.content.equipment.bell.SoulBaseParticle;
import com.simibubi.create.content.equipment.bell.SoulParticle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulPulseEffect {
    public static final int MAX_DISTANCE = 11;
    private static final List<List<BlockPos>> LAYERS = SoulPulseEffect.genLayers();
    private static final int WAITING_TICKS = 100;
    public static final int TICKS_PER_LAYER = 6;
    private int ticks;
    public final BlockPos pos;
    public final int distance;
    public final List<BlockPos> added;

    public SoulPulseEffect(BlockPos pos, int distance, boolean canOverlap) {
        this.ticks = 6 * distance;
        this.pos = pos;
        this.distance = distance;
        this.added = canOverlap ? null : new ArrayList();
    }

    public boolean finished() {
        return this.ticks <= -100;
    }

    public boolean canOverlap() {
        return this.added == null;
    }

    public List<BlockPos> tick(Level world) {
        if (this.finished()) {
            return null;
        }
        --this.ticks;
        if (this.ticks < 0 || this.ticks % 6 != 0) {
            return null;
        }
        List<BlockPos> spawns = this.getPotentialSoulSpawns(world);
        while (spawns.isEmpty() && this.ticks > 0) {
            this.ticks -= 6;
            spawns.addAll(this.getPotentialSoulSpawns(world));
        }
        return spawns;
    }

    public int currentLayerIdx() {
        return this.distance - this.ticks / 6 - 1;
    }

    public List<BlockPos> getPotentialSoulSpawns(Level world) {
        if (world == null) {
            return new ArrayList<BlockPos>();
        }
        return SoulPulseEffect.getLayer(this.currentLayerIdx()).map(p -> p.offset((Vec3i)this.pos)).filter(p -> SoulPulseEffect.canSpawnSoulAt(world, p, true)).collect(Collectors.toList());
    }

    public static boolean isDark(Level world, BlockPos at) {
        return world.getBrightness(LightLayer.BLOCK, at) < 1;
    }

    public static boolean canSpawnSoulAt(Level world, BlockPos at, boolean ignoreLight) {
        EntityType dummy = EntityType.ZOMBIE;
        double dummyWidth = 0.2;
        double dummyHeight = 0.75;
        double w2 = dummyWidth / 2.0;
        return world != null && SpawnPlacementTypes.ON_GROUND.isSpawnPositionOk((LevelReader)world, at, dummy) && (ignoreLight || SoulPulseEffect.isDark(world, at)) && Streams.stream((Iterable)world.getBlockCollisions(null, new AABB((double)at.getX() + 0.5 - w2, (double)at.getY(), (double)at.getZ() + 0.5 - w2, (double)at.getX() + 0.5 + w2, (double)at.getY() + dummyHeight, (double)at.getZ() + 0.5 + w2))).allMatch(VoxelShape::isEmpty);
    }

    public void spawnParticles(Level world, BlockPos at) {
        if (world == null || !world.isClientSide) {
            return;
        }
        Vec3 p = Vec3.atLowerCornerOf((Vec3i)at);
        if (this.canOverlap()) {
            world.addAlwaysVisibleParticle((ParticleOptions)((int)Math.round(VecHelper.getCenterOf((Vec3i)this.pos).distanceTo(VecHelper.getCenterOf((Vec3i)at))) >= this.distance ? new SoulParticle.PerimeterData() : new SoulParticle.ExpandingPerimeterData()), p.x + 0.5, p.y + 0.5, p.z + 0.5, 0.0, 0.0, 0.0);
        }
        if (SoulPulseEffect.isDark(world, at)) {
            world.addAlwaysVisibleParticle((ParticleOptions)new SoulParticle.Data(), p.x + 0.5, p.y + 0.5, p.z + 0.5, 0.0, 0.0, 0.0);
            world.addParticle((ParticleOptions)new SoulBaseParticle.Data(), p.x + 0.5, p.y + 0.01, p.z + 0.5, 0.0, 0.0, 0.0);
        }
    }

    private static List<List<BlockPos>> genLayers() {
        ArrayList<List<BlockPos>> layers = new ArrayList<List<BlockPos>>();
        for (int i = 0; i < 11; ++i) {
            layers.add(new ArrayList());
        }
        for (int x = 0; x < 11; ++x) {
            for (int y = 0; y < 11; ++y) {
                for (int z = 0; z < 11; ++z) {
                    BlockPos prev;
                    int i;
                    BlockPos candidate = new BlockPos(x, y, z);
                    int dist = (int)Math.round(Math.sqrt(candidate.distSqr((Vec3i)BlockPos.ZERO)));
                    if (dist > 11) continue;
                    if (dist <= 0) {
                        dist = 1;
                    }
                    List layer = (List)layers.get(dist - 1);
                    int start = layer.size();
                    int end = start + 1;
                    layer.add(candidate);
                    if (candidate.getX() != 0) {
                        layer.add(new BlockPos(-candidate.getX(), candidate.getY(), candidate.getZ()));
                        ++end;
                    }
                    if (candidate.getY() != 0) {
                        for (i = start; i < end; ++i) {
                            prev = (BlockPos)layer.get(i);
                            layer.add(new BlockPos(prev.getX(), -prev.getY(), prev.getZ()));
                        }
                        end += end - start;
                    }
                    if (candidate.getZ() == 0) continue;
                    for (i = start; i < end; ++i) {
                        prev = (BlockPos)layer.get(i);
                        layer.add(new BlockPos(prev.getX(), prev.getY(), -prev.getZ()));
                    }
                }
            }
        }
        return layers;
    }

    public static Stream<BlockPos> getLayer(int idx) {
        if (idx < 0 || idx >= 11) {
            return Stream.empty();
        }
        return LAYERS.get(idx).stream();
    }
}
