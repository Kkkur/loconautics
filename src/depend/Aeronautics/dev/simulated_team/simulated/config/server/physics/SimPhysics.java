/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigFloat
 */
package dev.simulated_team.simulated.config.server.physics;

import net.createmod.catnip.config.ConfigBase;

public class SimPhysics
extends ConfigBase {
    public final ConfigBase.ConfigFloat redstoneMagnetStrength = this.f(1000.0f, 0.0f, Float.MAX_VALUE, "redstoneMagnetStrength", new String[]{"The maximum force two magnets will apply towards each other"});
    public final ConfigBase.ConfigFloat dockingConnectorStrength = this.f(1000.0f, 0.0f, Float.MAX_VALUE, "dockingConnectorStrength", new String[]{"The maximum force two docking connectors will apply towards each other"});
    public final ConfigBase.ConfigFloat redstoneMagnetLinearAccelerationClamping = this.f(500.0f, 0.0f, Float.MAX_VALUE, "redstoneMagnetLinearAccelerationClamping", new String[]{"Limit for linear acceleration for a magnet pair"});
    public final ConfigBase.ConfigFloat redstoneMagnetAngularAccelerationClamping = this.f(50.0f, 0.0f, Float.MAX_VALUE, "redstoneMagnetAngularAccelerationClamping", new String[]{"Limit for angular acceleration for a magnet pair"});
    public final ConfigBase.ConfigFloat dockingConnectorLinearAccelerationClamping = this.f(500.0f, 0.0f, Float.MAX_VALUE, "dockingConnectorLinearAccelerationClamping", new String[]{"Limit for linear acceleration for a docking connector pair"});
    public final ConfigBase.ConfigFloat dockingConnectorAngularAccelerationClamping = this.f(50.0f, 0.0f, Float.MAX_VALUE, "dockingConnectorAngularAccelerationClamping", new String[]{"Limit for angular acceleration for a docking connector pair"});
    public final ConfigBase.ConfigFloat swivelBearingStiffness = this.f(1600.0f, 0.0f, Float.MAX_VALUE, "swivel_stiffness", new String[]{"The stiffness of locked swivel bearing joints"});
    public final ConfigBase.ConfigFloat swivelBearingFriction = this.f(0.3f, 0.0f, Float.MAX_VALUE, "swivel_friction", new String[]{"The friction / damping of unlocked swivel bearing joints"});
    public final ConfigBase.ConfigFloat swivelBearingDamping = this.f(40.0f, 0.0f, Float.MAX_VALUE, "swivel_damping", new String[]{"The damping of locked swivel bearing joints"});
    public final ConfigBase.ConfigFloat dockingConnectorAngleTolerance = this.f(20.0f, 0.0f, 365.0f, "docking_connector_angle", new String[]{"The angle tolerance in degrees for docking connectors to link"});
    public final ConfigBase.ConfigFloat dockingConnectorDistanceTolerance = this.f(0.5f, 0.0f, 4.0f, "docking_connector_distance", new String[]{"The distance tolerance in blocks for docking connectors to link"});
    public final ConfigBase.ConfigFloat handleMaxForce = this.f(120.0f, 0.0f, Float.MAX_VALUE, "handleMaxForce", new String[]{"The maximum force handles are allowed to apply to the contraption they are attached to"});
    public final ConfigBase.ConfigFloat physicsStaffLinearStiffness = this.f(2650.0f, 0.0f, Float.MAX_VALUE, "physics_staff_linear_stiffness", new String[]{Comments.physicsStaffLinearStiffness});
    public final ConfigBase.ConfigFloat physicsStaffLinearDamping = this.f(125.0f, 0.0f, Float.MAX_VALUE, "physics_staff_linear_damping", new String[]{Comments.physicsStaffLinearDamping});
    public final ConfigBase.ConfigFloat physicsStaffAngularStiffness = this.f(10000.0f, 0.0f, Float.MAX_VALUE, "physics_staff_angular_stiffness", new String[]{Comments.physicsStaffAngularStiffness});
    public final ConfigBase.ConfigFloat physicsStaffAngularDamping = this.f(850.0f, 0.0f, Float.MAX_VALUE, "physics_staff_angular_damping", new String[]{Comments.physicsStaffAngularDamping});

    public String getName() {
        return "physics";
    }

    private static class Comments {
        private static final String redstoneMagnetStrength = "The maximum force two magnets will apply towards each other";
        private static final String dockingConnectorStrength = "The maximum force two docking connectors will apply towards each other";
        private static final String redstoneMagnetLinearAccelerationClamping = "Limit for linear acceleration for a magnet pair";
        private static final String redstoneMagnetAngularAccelerationClamping = "Limit for angular acceleration for a magnet pair";
        private static final String dockingConnectorLinearAccelerationClamping = "Limit for linear acceleration for a docking connector pair";
        private static final String dockingConnectorAngularAccelerationClamping = "Limit for angular acceleration for a docking connector pair";
        private static final String swivelBearingStiffness = "The stiffness of locked swivel bearing joints";
        private static final String swivelBearingDamping = "The damping of locked swivel bearing joints";
        private static final String swivelBearingFriction = "The friction / damping of unlocked swivel bearing joints";
        private static final String dockingConnectorAngleTolerance = "The angle tolerance in degrees for docking connectors to link";
        private static final String dockingConnectorDistanceTolerance = "The distance tolerance in blocks for docking connectors to link";
        private static final String handleMaxForce = "The maximum force handles are allowed to apply to the contraption they are attached to";
        public static String physicsStaffLinearStiffness = "The linear stiffness of the joint motors used to hold sub-levels by the Creative Physics Staff";
        public static String physicsStaffLinearDamping = "The linear damping of the joint motors used to hold sub-levels by the Creative Physics Staff";
        public static String physicsStaffAngularStiffness = "The angular stiffness of the joint motors used to hold sub-levels by the Creative Physics Staff";
        public static String physicsStaffAngularDamping = "The angular damping of the joint motors used to hold sub-levels by the Creative Physics Staff";

        private Comments() {
        }
    }
}
