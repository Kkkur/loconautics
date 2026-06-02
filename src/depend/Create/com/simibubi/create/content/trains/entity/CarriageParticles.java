/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Position
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CarriageParticles {
    CarriageContraptionEntity entity;
    boolean arrived;
    int depressurise;
    double prevMotion;
    LerpedFloat brakes;

    public CarriageParticles(CarriageContraptionEntity entity) {
        this.entity = entity;
        this.arrived = true;
        this.depressurise = 0;
        this.prevMotion = 0.0;
        this.brakes = LerpedFloat.linear();
    }

    public void tick(Carriage.DimensionalCarriageEntity dce) {
        boolean stopped;
        Minecraft mc = Minecraft.getInstance();
        Entity camEntity = mc.cameraEntity;
        if (camEntity == null) {
            return;
        }
        Vec3 leadingAnchor = dce.leadingAnchor();
        if (leadingAnchor == null || !leadingAnchor.closerThan((Position)camEntity.position(), 64.0)) {
            return;
        }
        RandomSource r = this.entity.level().random;
        Vec3 contraptionMotion = this.entity.position().subtract(this.entity.getPrevPositionVec());
        double length = contraptionMotion.length();
        if (this.arrived && length > (double)0.01f) {
            this.arrived = false;
        }
        this.arrived |= this.entity.isStalled();
        boolean bl = stopped = length < (double)0.002f;
        if (stopped) {
            if (!this.arrived) {
                this.arrived = true;
                this.depressurise = (int)(20.0f * this.entity.getCarriage().train.accumulatedSteamRelease / 10.0f);
            }
        } else {
            this.depressurise = 0;
        }
        if (this.depressurise > 0) {
            --this.depressurise;
        }
        this.brakes.chase(this.prevMotion > length + length / 512.0 ? 1.0 : 0.0, 0.25, LerpedFloat.Chaser.exp((double)0.625));
        this.brakes.tickChaser();
        this.prevMotion = length;
        Level level = this.entity.level();
        Vec3 position = this.entity.getPosition(0.0f);
        float viewYRot = this.entity.getViewYRot(0.0f);
        float viewXRot = this.entity.getViewXRot(0.0f);
        int bogeySpacing = this.entity.getCarriage().bogeySpacing;
        for (CarriageBogey bogey : this.entity.getCarriage().bogeys) {
            float cutoff;
            if (bogey == null) continue;
            boolean spark = this.depressurise == 0 || this.depressurise > 10;
            float f = cutoff = length < 0.125 ? 0.0f : 0.125f;
            if (length > 0.1666666716337204) {
                cutoff = Math.max(cutoff, this.brakes.getValue() * 1.15f);
            }
            for (int j : Iterate.positiveAndNegative) {
                if (r.nextFloat() > cutoff && (spark || r.nextInt(4) == 0)) continue;
                for (int i : Iterate.positiveAndNegative) {
                    if (r.nextFloat() > cutoff && (spark || r.nextInt(4) == 0)) continue;
                    Vec3 v = Vec3.ZERO.add((double)j * 1.15, spark ? (double)-0.6f : 0.32, (double)i);
                    Vec3 m = Vec3.ZERO.add((double)j * (spark ? 0.5 : 0.25), spark ? 0.49 : -0.29, 0.0);
                    m = VecHelper.rotate((Vec3)m, (double)bogey.pitch.getValue(0.0f), (Direction.Axis)Direction.Axis.X);
                    m = VecHelper.rotate((Vec3)m, (double)bogey.yaw.getValue(0.0f), (Direction.Axis)Direction.Axis.Y);
                    v = VecHelper.rotate((Vec3)v, (double)bogey.pitch.getValue(0.0f), (Direction.Axis)Direction.Axis.X);
                    v = VecHelper.rotate((Vec3)v, (double)bogey.yaw.getValue(0.0f), (Direction.Axis)Direction.Axis.Y);
                    v = VecHelper.rotate((Vec3)v, (double)(-viewYRot - 90.0f), (Direction.Axis)Direction.Axis.Y);
                    v = VecHelper.rotate((Vec3)v, (double)viewXRot, (Direction.Axis)Direction.Axis.X);
                    v = VecHelper.rotate((Vec3)v, (double)-180.0, (Direction.Axis)Direction.Axis.Y);
                    v = v.add(0.0, 0.0, bogey.isLeading ? 0.0 : (double)(-bogeySpacing));
                    v = VecHelper.rotate((Vec3)v, (double)180.0, (Direction.Axis)Direction.Axis.Y);
                    v = VecHelper.rotate((Vec3)v, (double)(-viewXRot), (Direction.Axis)Direction.Axis.X);
                    v = VecHelper.rotate((Vec3)v, (double)(viewYRot + 90.0f), (Direction.Axis)Direction.Axis.Y);
                    v = v.add(position);
                    m = m.add(contraptionMotion.scale(0.75));
                    level.addParticle(spark ? bogey.getStyle().contactParticle : bogey.getStyle().smokeParticle, v.x, v.y, v.z, m.x, m.y, m.z);
                }
            }
        }
    }
}
