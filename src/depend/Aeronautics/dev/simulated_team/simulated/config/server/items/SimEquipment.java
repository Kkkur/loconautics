/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 */
package dev.simulated_team.simulated.config.server.items;

import net.createmod.catnip.config.ConfigBase;

public class SimEquipment
extends ConfigBase {
    public final ConfigBase.ConfigInt maxPlungerLauncherShots = this.i(100, 0, "maxPlungerLauncherShots", new String[]{Comments.maxPlungerLauncherShots});
    public final ConfigBase.ConfigInt maxPlungerLauncherRange = this.i(64, 0, "maxPlungerLauncherRange", new String[]{Comments.maxPlungerLauncherRange});

    public String getName() {
        return "equipment";
    }

    private static class Comments {
        static String maxPlungerLauncherShots = "Amount of free Plunger Launcher shots provided by one filled Backtank. Set to 0 makes Plunger Launchers unbreakable";
        static String maxPlungerLauncherRange = "The max range that launched plungers can be from each other";

        private Comments() {
        }
    }
}
