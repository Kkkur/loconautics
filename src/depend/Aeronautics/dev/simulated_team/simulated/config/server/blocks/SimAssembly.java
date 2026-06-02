/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigBool
 *  net.createmod.catnip.config.ConfigBase$ConfigFloat
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 */
package dev.simulated_team.simulated.config.server.blocks;

import net.createmod.catnip.config.ConfigBase;

public class SimAssembly
extends ConfigBase {
    public final ConfigBase.ConfigInt maxBlocksMoved = this.i(128000, 1, "maxBlocksMoved", new String[]{Comments.maxBlocksMoved});
    public final ConfigBase.ConfigInt honeyGlueRange = this.i(48, 1, Integer.MAX_VALUE, "honeyGlueRange", new String[]{Comments.honeyGlueRange});
    public final ConfigBase.ConfigFloat mergingGlueRange = this.f(4.0f, 0.0f, Float.MAX_VALUE, "mergingGlueRange", new String[]{Comments.mergingGlueRange});
    public final ConfigBase.ConfigInt maxDisassemblyTicks = this.i(20, 5, "maxDisassemblyTicks", new String[]{Comments.maxDisassemblyTicks});
    public final ConfigBase.ConfigFloat disassemblyDegreeTolerance = this.f(4.0f, 0.0f, "disassemblyDegreeTolerance", new String[]{Comments.disassemblyDegreeTolerance});
    public final ConfigBase.ConfigFloat disassemblyMaxVelocity = this.f(5.0f, 0.0f, "disassemblyMaxVelocity", new String[]{Comments.disassemblyMaxVelocity});
    public final ConfigBase.ConfigFloat disassemblyMaxAngularVelocity = this.f(1.5707964f, 0.0f, "disassemblyMaxAngularVelocity", new String[]{Comments.disassemblyMaxAngularVelocity});
    public final ConfigBase.ConfigBool disallowMidAirDisassembly = this.b(true, "disallowMidAirDisassembly", new String[]{Comments.disallowMidAirDisassembly});
    public final ConfigBase.ConfigBool primaryDisassembly = this.b(false, "Primary Disassembly", new String[]{"Whether only the original Physics Assembler can disassemble the Sub-Level it assembled", "Disabling allows *ALL* Physics Assemblers to disassemble any Sub-Level"});

    public String getName() {
        return "assembly";
    }

    private static class Comments {
        static String honeyGlueRange = "Maximum range in blocks which honey glue may initially be placed";
        static String mergingGlueRange = "Maximum range in blocks which merging glue may be placed by items such as slime balls";
        static String maxBlocksMoved = "Maximum amount of blocks in a structure assemble-able by Physics Assemblers, Swivel Bearings, or other means.";
        static String maxDisassemblyTicks = "The amount of ticks that disassembly alignment is allowed to take before failing.";
        static String disassemblyDegreeTolerance = "The maximum amount of degrees a Simulated Contraption is allowed to be tilted to fully disassemble";
        static String disassemblyMaxVelocity = "The maximum velocity a Simulated Contraption is allowed to disassemble at in m/s";
        static String disassemblyMaxAngularVelocity = "The maximum angular velocity a Simulated Contraption is allowed to disassemble at in rad/s";
        static String disallowMidAirDisassembly = "Disallow disassembly of Simulated Contraptions in mid-air, requiring them to be within a few chunk sections of terrain";

        private Comments() {
        }
    }
}
