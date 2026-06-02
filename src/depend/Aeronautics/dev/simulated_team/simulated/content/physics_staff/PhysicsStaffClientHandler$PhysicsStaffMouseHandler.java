/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.joml.AxisAngle4d
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 */
package dev.simulated_team.simulated.content.physics_staff;

import dev.simulated_team.simulated.SimulatedClient;
import dev.simulated_team.simulated.config.client.items.SimItemConfigs;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffAction;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffClientHandler;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4d;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;

public static class PhysicsStaffClientHandler.PhysicsStaffMouseHandler
implements InteractCallback {
    @Override
    public InteractCallback.Result onAttack(int modifiers, int action, KeyMapping leftKey) {
        if (SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.holdingStaff && action == 1) {
            SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.onItemPunched();
            return new InteractCallback.Result(true);
        }
        return InteractCallback.super.onAttack(modifiers, action, leftKey);
    }

    @Override
    public InteractCallback.Result onUse(int modifiers, int action, KeyMapping rightKey) {
        if (SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.holdingStaff && action == 1) {
            SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.onItemUsed(PhysicsStaffAction.START_DRAG);
            return new InteractCallback.Result(true);
        }
        return InteractCallback.super.onUse(modifiers, action, rightKey);
    }

    @Override
    public InteractCallback.Result onMouseMove(double yaw, double pitch) {
        Minecraft mc = Minecraft.getInstance();
        PhysicsStaffClientHandler handler = SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER;
        if (handler.isRotating()) {
            assert (handler.dragSession != null);
            assert (mc.player != null);
            Vec3 axis = mc.player.calculateViewVector(0.0f, mc.player.getYRot() - 90.0f);
            Quaterniond orientation = handler.dragSession.dragOrientation();
            SimItemConfigs config = SimConfigService.INSTANCE.client().itemConfig;
            double rotationSensitivity = (Double)config.physicsStaffRotateSensitivity.get();
            double yawChange = Math.toRadians(yaw) * rotationSensitivity;
            orientation.rotateLocalY(yawChange);
            orientation.premul((Quaterniondc)new Quaterniond(new AxisAngle4d(Math.toRadians(-pitch) * rotationSensitivity, axis.x, axis.y, axis.z)));
            return new InteractCallback.Result(true);
        }
        return InteractCallback.super.onMouseMove(yaw, pitch);
    }

    @Override
    public InteractCallback.Result onScroll(double deltaX, double deltaY) {
        PhysicsStaffClientHandler handler = SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER;
        PhysicsStaffClientHandler.ClientDragSession dragSession = handler.dragSession;
        SimItemConfigs config = SimConfigService.INSTANCE.client().itemConfig;
        double scrollSensitivity = (Double)config.physicsStaffScrollSensitivity.get();
        if (handler.holdingStaff && dragSession != null) {
            double currentDistance = dragSession.distance;
            boolean sprint = Minecraft.getInstance().options.keySprint.isDown();
            double sensMultiplier = Mth.clamp((double)Math.pow(currentDistance / 10.0, 0.5), (double)1.0, (double)5.0) * (double)(sprint ? 4 : 1);
            dragSession.setDistance(handler.clampDistance(currentDistance + deltaY * scrollSensitivity * sensMultiplier));
            return new InteractCallback.Result(true);
        }
        return InteractCallback.super.onScroll(deltaX, deltaY);
    }
}
