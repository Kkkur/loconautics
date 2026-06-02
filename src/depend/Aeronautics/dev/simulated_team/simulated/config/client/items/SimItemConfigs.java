/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigEnum
 *  net.createmod.catnip.config.ConfigBase$ConfigFloat
 */
package dev.simulated_team.simulated.config.client.items;

import dev.simulated_team.simulated.client.BlockPropertiesTooltip;
import net.createmod.catnip.config.ConfigBase;

public class SimItemConfigs
extends ConfigBase {
    public final ConfigBase.ConfigEnum<BlockPropertiesTooltip.Condition> displayProperties = this.e(BlockPropertiesTooltip.Condition.GOGGLES, "displayProperties", new String[]{Comments.displayProperties});
    public final ConfigBase.ConfigFloat physicsStaffScrollSensitivity = this.f(0.6f, 0.0f, Float.MAX_VALUE, "physics_staff_scroll_sensitivity", new String[]{Comments.physicsStaffScrollSensitivity});
    public final ConfigBase.ConfigFloat physicsStaffRotateSensitivity = this.f(0.35f, 0.0f, Float.MAX_VALUE, "physics_staff_rotate_sensitivity", new String[]{Comments.physicsStaffRotateSensitivity});

    public String getName() {
        return "items";
    }

    public static class Comments {
        static String displayProperties = "When to display physics properties in block tooltips";
        static String physicsStaffScrollSensitivity = "The sensitivity of scrolling when holding a sub-level with the Creative Physics Staff";
        static String physicsStaffRotateSensitivity = "The sensitivity of rotation when holding a sub-level with the Creative Physics Staff";
    }
}
