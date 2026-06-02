/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigBool
 *  net.createmod.catnip.config.ConfigBase$ConfigFloat
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 */
package dev.ryanhcode.offroad.config.server;

import net.createmod.catnip.config.ConfigBase;

public class OffroadBlockConfigs
extends ConfigBase {
    public final ConfigBase.ConfigFloat boreheadBearingSearchRadius = this.f(1.5f, 0.0f, 10.0f, "borehead_bearing_search_radius", new String[]{"The block gathering search radius of the borehead bearing"});
    public final ConfigBase.ConfigInt boreheadBearingStallRecoveryTicks = this.i(10, 0, "borehead_bearing_stall_recovery_ticks", new String[]{"The amount of ticks it takes for the borehead bearing to recover from an item stall"});
    public final ConfigBase.ConfigBool boreheadBearingStallingEnabled = this.b(true, "borehead_bearing_stalling_enabled", new String[]{"Whether the borehead bearing should stall when it doesn't have enough room to accept a mined block"});
    public final ConfigBase.ConfigFloat boreheadBearingRotationDivisor = this.f(4.0f, 1.0f, "borehead_bearing_rotation_divisor", new String[]{"The divisor used to determine the max speed of the attached borehead contraption contraption"});

    public String getName() {
        return "blocks";
    }
}
