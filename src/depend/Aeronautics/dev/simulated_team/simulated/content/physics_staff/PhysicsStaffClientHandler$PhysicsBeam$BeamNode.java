/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.physics_staff;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

private static class PhysicsStaffClientHandler.PhysicsBeam.BeamNode {
    Vec3 position = new Vec3(0.0, 0.0, 0.0);
    Vec3 previousPosition = new Vec3(0.0, 0.0, 0.0);

    private PhysicsStaffClientHandler.PhysicsBeam.BeamNode() {
    }

    void update() {
        RandomSource random = Minecraft.getInstance().level.random;
        this.previousPosition = this.position;
        this.position = this.position.offsetRandom(random, 3.0f).scale(0.5);
    }
}
