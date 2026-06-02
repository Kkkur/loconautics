/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import java.lang.ref.WeakReference;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class ChainConveyorPackage.ChainConveyorPackagePhysicsData {
    public Vec3 targetPos = null;
    public Vec3 prevTargetPos = null;
    public Vec3 prevPos = null;
    public Vec3 pos = null;
    public Vec3 motion = Vec3.ZERO;
    public int lastTick = AnimationTickHolder.getTicks();
    public float yaw;
    public float prevYaw;
    public boolean flipped;
    public ResourceLocation modelKey;
    public WeakReference<ChainConveyorBlockEntity> beReference;

    public ChainConveyorPackage.ChainConveyorPackagePhysicsData(ChainConveyorPackage this$0, Vec3 serverPosition) {
    }

    public boolean shouldTick() {
        if (this.lastTick == AnimationTickHolder.getTicks()) {
            return false;
        }
        this.lastTick = AnimationTickHolder.getTicks();
        return true;
    }

    public void setBE(ChainConveyorBlockEntity ccbe) {
        if (this.beReference == null || this.beReference.get() != ccbe) {
            this.beReference = new WeakReference<ChainConveyorBlockEntity>(ccbe);
        }
    }
}
