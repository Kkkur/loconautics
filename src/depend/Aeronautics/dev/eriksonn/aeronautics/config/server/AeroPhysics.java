/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigFloat
 */
package dev.eriksonn.aeronautics.config.server;

import net.createmod.catnip.config.ConfigBase;

public class AeroPhysics
extends ConfigBase {
    public final ConfigBase.ConfigFloat mountedPotatoCannonMagnitude = this.f(0.2f, 0.0f, Float.MAX_VALUE, "recoil_magnitude", new String[]{Comments.mountedPotatoCannonComment});
    public final ConfigBase.ConfigFloat propellerBearingThrust = this.f(0.2f, 0.0f, Float.MAX_VALUE, "propellerBearingThrust", new String[]{Comments.propellerBearingThrust});
    public final ConfigBase.ConfigFloat propellerBearingAirflowMult = this.f(0.05f, 0.0f, Float.MAX_VALUE, "propellerBearingAirflow", new String[]{Comments.propellerBearingAirflow});
    public final ConfigBase.ConfigFloat woodenPropellerThrust = this.f(1.0f, 0.0f, Float.MAX_VALUE, "woodenPropellerThrust", new String[]{Comments.woodenPropellerThrust});
    public final ConfigBase.ConfigFloat woodenPropellerAirflow = this.f(0.1f, 0.0f, Float.MAX_VALUE, "woodenPropellerAirflow", new String[]{Comments.woodenPropellerAirflow});
    public final ConfigBase.ConfigFloat andesitePropellerThrust = this.f(1.0f, 0.0f, Float.MAX_VALUE, "andesitePropellerThrust", new String[]{Comments.andesitePropellerThrust});
    public final ConfigBase.ConfigFloat andesitePropellerAirflow = this.f(0.1f, 0.0f, Float.MAX_VALUE, "andesitePropellerAirflow", new String[]{Comments.andesitePropellerAirflow});
    public final ConfigBase.ConfigFloat smartPropellerThrust = this.f(1.0f, 0.0f, Float.MAX_VALUE, "smartPropellerThrust", new String[]{Comments.smartPropellerThrust});
    public final ConfigBase.ConfigFloat smartPropellerAirflow = this.f(0.1f, 0.0f, Float.MAX_VALUE, "smartPropellerAirflow", new String[]{Comments.smartPropellerAirflow});
    public final ConfigBase.ConfigFloat hotAirStrength = this.f(1.5f, 0.0f, Float.MAX_VALUE, "hotAirStrength", new String[]{Comments.hotAirStrength});
    public final ConfigBase.ConfigFloat steamStrength = this.f(1.5f, 0.0f, Float.MAX_VALUE, "steamStrength", new String[]{Comments.steamStrength});

    public String getName() {
        return "physics";
    }

    private static class Comments {
        static String mountedPotatoCannonComment = "The recoil magnitude used whenever the Mounted Potato Cannon shoots";
        static String propellerBearingThrust = "Thrust scaling for Propeller Bearings";
        static String woodenPropellerThrust = "Thrust scaling for Wooden Propellers";
        static String woodenPropellerAirflow = "Airflow scaling for Wooden Propellers";
        static String andesitePropellerThrust = "Thrust scaling for Andesite Propellers";
        static String andesitePropellerAirflow = "Airflow scaling for Andesite Propellers";
        static String smartPropellerThrust = "Thrust scaling for Smart Propellers";
        static String smartPropellerAirflow = "Airflow scaling for Smart Propellers";
        static String propellerBearingAirflow = "Airflow scaling for Propeller Bearings";
        static String hotAirStrength = "kpg lifted per cubic meter of Hot Air";
        static String steamStrength = "kpg lifted per cubic meter of Steam";

        private Comments() {
        }
    }
}
