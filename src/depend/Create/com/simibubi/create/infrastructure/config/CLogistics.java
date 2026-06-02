/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigBool
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 */
package com.simibubi.create.infrastructure.config;

import net.createmod.catnip.config.ConfigBase;

public class CLogistics
extends ConfigBase {
    public final ConfigBase.ConfigInt defaultExtractionTimer = this.i(8, 1, "defaultExtractionTimer", new String[]{Comments.defaultExtractionTimer});
    public final ConfigBase.ConfigInt psiTimeout = this.i(60, 1, "psiTimeout", new String[]{Comments.psiTimeout});
    public final ConfigBase.ConfigInt mechanicalArmRange = this.i(5, 1, "mechanicalArmRange", new String[]{Comments.mechanicalArmRange});
    public final ConfigBase.ConfigInt packagePortRange = this.i(5, 1, "packagePortRange", new String[]{Comments.packagePortRange});
    public final ConfigBase.ConfigInt linkRange = this.i(256, 1, "linkRange", new String[]{Comments.linkRange});
    public final ConfigBase.ConfigInt displayLinkRange = this.i(64, 1, "displayLinkRange", new String[]{Comments.displayLinkRange});
    public final ConfigBase.ConfigInt vaultCapacity = this.i(20, 1, 2048, "vaultCapacity", new String[]{Comments.vaultCapacity});
    public final ConfigBase.ConfigInt chainConveyorCapacity = this.i(20, 1, "chainConveyorCapacity", new String[]{Comments.chainConveyorCapacity});
    public final ConfigBase.ConfigInt brassTunnelTimer = this.i(10, 1, 10, "brassTunnelTimer", new String[]{Comments.brassTunnelTimer});
    public final ConfigBase.ConfigInt factoryGaugeTimer = this.i(100, 5, "factoryGaugeTimer", new String[]{Comments.factoryGaugeTimer});
    public final ConfigBase.ConfigBool seatHostileMobs = this.b(true, "seatHostileMobs", new String[]{Comments.seatHostileMobs});

    public String getName() {
        return "logistics";
    }

    private static class Comments {
        static String defaultExtractionTimer = "The amount of ticks a funnel waits between item transferrals, when it is not re-activated by redstone.";
        static String linkRange = "Maximum possible range in blocks of redstone link connections.";
        static String displayLinkRange = "Maximum possible distance in blocks between display links and their target.";
        static String psiTimeout = "The amount of ticks a portable storage interface waits for transfers until letting contraptions move along.";
        static String mechanicalArmRange = "Maximum distance in blocks a Mechanical Arm can reach across.";
        static String packagePortRange = "Maximum distance in blocks a Package Port can be placed at from its target.";
        static String vaultCapacity = "The total amount of stacks a vault can hold per block in size.";
        static String chainConveyorCapacity = "The amount of packages a chain conveyor can carry at a time.";
        static String brassTunnelTimer = "The amount of ticks a brass tunnel waits between distributions.";
        static String factoryGaugeTimer = "The amount of ticks a factory gauge waits between requests.";
        static String seatHostileMobs = "Whether hostile mobs walking near a seat will start riding it.";

        private Comments() {
        }
    }
}
