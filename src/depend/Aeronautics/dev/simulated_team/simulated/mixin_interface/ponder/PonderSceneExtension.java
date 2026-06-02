/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.mixin_interface.ponder;

import net.minecraft.world.phys.Vec3;

public interface PonderSceneExtension {
    public float simulated$getBasePlateAnimationTimer(float var1);

    public void simulated$toggleRenderBasePlateShadow();

    public Vec3 simulated$getShadowOffset(float var1);

    public void simulated$setShadowOffset(Vec3 var1);

    public void simulated$setOldShadowOffset(Vec3 var1);

    public void simulated$moveShadowOffset(Vec3 var1);

    public void simulated$setScaleFactor(float var1);

    public float simulated$getScale(float var1);

    public void simulated$setYOffset(float var1);

    public float simulated$getYOffset(float var1);
}
