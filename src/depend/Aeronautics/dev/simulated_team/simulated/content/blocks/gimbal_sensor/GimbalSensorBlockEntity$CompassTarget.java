/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.gimbal_sensor;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

static class GimbalSensorBlockEntity.CompassTarget {
    private final Vector3d target = new Vector3d(0.0, 0.0, 0.0);
    private final Vector3d randomTarget = new Vector3d(0.0, 0.0, 0.0);
    private int randomTargetTimer = 0;
    private double randomTargetLength = 3.0;
    private boolean isRandom = false;

    GimbalSensorBlockEntity.CompassTarget() {
    }

    public void update(Vec3 pos, Level level) {
        boolean bl = this.isRandom = !level.dimensionType().natural();
        if (!this.isRandom) {
            this.target.set(0.0, 0.0, -1.0);
        } else {
            RandomSource r = level.random;
            if (this.randomTargetTimer-- < 0) {
                float radius = 1.0f;
                this.randomTarget.set((double)((r.nextFloat() - 0.5f) * 2.0f * 1.0f), (double)((r.nextFloat() - 0.5f) * 2.0f * 1.0f), (double)((r.nextFloat() - 0.5f) * 2.0f * 1.0f));
                this.randomTargetTimer = level.random.nextInt(5, 15);
            }
            float nudge = 0.3f;
            this.randomTarget.add((double)((r.nextFloat() - 0.5f) * 2.0f * 0.3f), (double)((r.nextFloat() - 0.5f) * 2.0f * 0.3f), (double)((r.nextFloat() - 0.5f) * 2.0f * 0.3f));
            this.randomTarget.normalize();
            double step = 0.5;
            this.target.mul(0.5).fma(0.5, (Vector3dc)this.randomTarget);
            this.target.normalize();
        }
    }

    public boolean isRandom() {
        return this.isRandom;
    }

    public void setRandomTargetLength(double s) {
        this.randomTargetLength = s;
    }

    public Vector3d getTarget(Vector3d v) {
        return this.target.mul(this.isRandom() ? this.randomTargetLength : 1.0, v);
    }
}
