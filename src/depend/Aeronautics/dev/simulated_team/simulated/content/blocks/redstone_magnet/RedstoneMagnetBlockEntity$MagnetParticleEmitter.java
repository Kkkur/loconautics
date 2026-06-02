/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  org.joml.Matrix3d
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.redstone_magnet;

import dev.simulated_team.simulated.content.particle.MagnetFieldParticleData;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3dc;

private static class RedstoneMagnetBlockEntity.MagnetParticleEmitter {
    protected final HashMap<Vector3d, Vector3d> nearbyMagnets;
    protected final Vector3d pos;
    protected final Vector3d oldNudge = new Vector3d();
    protected final Vector3d newNudge = new Vector3d();
    protected int time;
    protected final int startTime;
    protected final Level level;
    protected final boolean negative;
    static final Vector3d k1 = new Vector3d();
    static final Vector3d k2 = new Vector3d();
    static final Vector3d k3 = new Vector3d();
    static final Vector3d k4 = new Vector3d();
    static final Vector3d posTemp = new Vector3d();
    static final Vector3d currentField = new Vector3d();
    static final Vector3d relativePos = new Vector3d();
    static final Vector3d moment = new Vector3d();

    public RedstoneMagnetBlockEntity.MagnetParticleEmitter(Vector3d startPos, HashMap<Vector3d, Vector3d> nearbyMagnets, int maxTime, Level level, boolean negative) {
        this.pos = new Vector3d((Vector3dc)startPos);
        this.nearbyMagnets = nearbyMagnets;
        this.time = maxTime;
        this.startTime = maxTime;
        this.level = level;
        this.negative = negative;
        this.rk4(this.pos, this.newNudge);
    }

    public void update() {
        --this.time;
        if (this.time < 0) {
            return;
        }
        if (!this.level.getBlockState(BlockPos.containing((double)this.pos.x, (double)this.pos.y, (double)this.pos.z)).isAir()) {
            this.time = -1;
            return;
        }
        this.pos.add((Vector3dc)this.newNudge);
        this.oldNudge.set((Vector3dc)this.newNudge);
        this.rk4(this.pos, this.newNudge);
        ((ServerLevel)this.level).sendParticles((ParticleOptions)new MagnetFieldParticleData(this.negative), this.pos.x, this.pos.y, this.pos.z, 1, 0.01, 0.01, 0.01, 0.0);
    }

    void rk4(Vector3d pos, Vector3d nudgeOut) {
        double dt = 0.2;
        this.getField(pos, k1);
        this.getField(pos.fma(0.1, (Vector3dc)k1, posTemp), k2);
        this.getField(pos.fma(0.1, (Vector3dc)k2, posTemp), k3);
        this.getField(pos.fma(0.2, (Vector3dc)k3, posTemp), k4);
        nudgeOut.set((Vector3dc)k1).fma(2.0, (Vector3dc)k2).fma(2.0, (Vector3dc)k3).add((Vector3dc)k4).mul(0.03333333333333333);
    }

    void getField(Vector3d pos, Vector3d field) {
        field.zero();
        for (Map.Entry<Vector3d, Vector3d> entry : this.nearbyMagnets.entrySet()) {
            relativePos.set((Vector3dc)pos).sub((Vector3dc)entry.getKey());
            moment.set((Vector3dc)entry.getValue()).mul(this.negative ? -1.0 : 1.0);
            if (moment.lengthSquared() == 0.0) continue;
            double distanceSq = relativePos.lengthSquared();
            if (distanceSq < 0.2) {
                this.time = -1;
                return;
            }
            double d = moment.dot((Vector3dc)relativePos) / distanceSq;
            currentField.set((Vector3dc)relativePos).mul(3.0 * d);
            currentField.sub((Vector3dc)moment);
            currentField.div(distanceSq);
            field.add((Vector3dc)currentField);
        }
        field.normalize();
    }

    private Matrix3d generateOuterProduct(Vector3d v1, Vector3d v2) {
        return new Matrix3d(v1.x * v2.x, v1.x * v2.y, v1.x * v2.z, v1.y * v2.x, v1.y * v2.y, v1.y * v2.z, v1.z * v2.x, v1.z * v2.y, v1.z * v2.z);
    }
}
