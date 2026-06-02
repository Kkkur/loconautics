/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CKinetics;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNonnullByDefault
public interface IAirCurrentSource {
    @Nullable
    public AirCurrent getAirCurrent();

    @Nullable
    public Level getAirCurrentWorld();

    public BlockPos getAirCurrentPos();

    public float getSpeed();

    public Direction getAirflowOriginSide();

    @Nullable
    public Direction getAirFlowDirection();

    default public float getMaxDistance() {
        float speed = Math.abs(this.getSpeed());
        CKinetics config = AllConfigs.server().kinetics;
        float distanceFactor = Math.min(speed / (float)((Integer)config.fanRotationArgmax.get()).intValue(), 1.0f);
        float pushDistance = Mth.lerp((float)distanceFactor, (float)3.0f, (float)((Integer)config.fanPushDistance.get()).intValue());
        float pullDistance = Mth.lerp((float)distanceFactor, (float)3.0f, (float)((Integer)config.fanPullDistance.get()).intValue());
        return this.getSpeed() > 0.0f ? pushDistance : pullDistance;
    }

    public boolean isSourceRemoved();
}
