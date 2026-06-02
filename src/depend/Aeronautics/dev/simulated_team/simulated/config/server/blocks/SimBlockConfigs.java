/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigFloat
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 */
package dev.simulated_team.simulated.config.server.blocks;

import net.createmod.catnip.config.ConfigBase;

public class SimBlockConfigs
extends ConfigBase {
    public final ConfigBase.ConfigInt opticalSensorRange = this.i(15, 0, Integer.MAX_VALUE, "optical_sensor_max_range", new String[]{"Maximum range for the Optical Sensor"});
    public final ConfigBase.ConfigInt laserPointerRange = this.i(100, 0, Integer.MAX_VALUE, "laser_pointer_max_range", new String[]{"Maximum range for the Laser Pointer"});
    public final ConfigBase.ConfigFloat maxRopeRange = this.f(40.0f, 0.0f, 1000.0f, "max_rope_range", new String[]{"Maximum range for rope connections"});
    public final ConfigBase.ConfigFloat maxRopeStretchAllowed = this.f(25.0f, 0.0f, 100.0f, "max_rope_winch_stretch_allowed", new String[]{"Maximum percent the rope mounted on a Rope Winch is allowed to stretch before not accepting input"});
    public final ConfigBase.ConfigFloat maxRopeZiplineAngle = this.f(85.0f, 0.0f, 90.0f, "max_rope_zipline_angle", new String[]{"Steepest angle at which a rope can be grabbed onto using a wrench in degrees"});
    public final ConfigBase.ConfigFloat maxSwivelBearingSpeed = this.f(96.0f, 0.0f, 256.0f, "max_swivel_bearing_speed", new String[]{"The maximum RPM a Swivel Bearing is allowed to rotate at"});

    public String getName() {
        return "blocks";
    }

    private static class Comments {
        private static final String opticalSensorRange = "Maximum range for the Optical Sensor";
        private static final String laserPointerRange = "Maximum range for the Laser Pointer";
        private static final String maxRopeRange = "Maximum range for rope connections";
        private static final String maxRopeWinchStretch = "Maximum percent the rope mounted on a Rope Winch is allowed to stretch before not accepting input";
        private static final String maxRopeZiplineAngle = "Steepest angle at which a rope can be grabbed onto using a wrench in degrees";
        private static final String maxSwivelBearingSpeed = "The maximum RPM a Swivel Bearing is allowed to rotate at";

        private Comments() {
        }
    }
}
