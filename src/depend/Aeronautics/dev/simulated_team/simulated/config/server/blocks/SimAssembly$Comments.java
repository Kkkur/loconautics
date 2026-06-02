/*
 * Decompiled with CFR 0.152.
 */
package dev.simulated_team.simulated.config.server.blocks;

private static class SimAssembly.Comments {
    static String honeyGlueRange = "Maximum range in blocks which honey glue may initially be placed";
    static String mergingGlueRange = "Maximum range in blocks which merging glue may be placed by items such as slime balls";
    static String maxBlocksMoved = "Maximum amount of blocks in a structure assemble-able by Physics Assemblers, Swivel Bearings, or other means.";
    static String maxDisassemblyTicks = "The amount of ticks that disassembly alignment is allowed to take before failing.";
    static String disassemblyDegreeTolerance = "The maximum amount of degrees a Simulated Contraption is allowed to be tilted to fully disassemble";
    static String disassemblyMaxVelocity = "The maximum velocity a Simulated Contraption is allowed to disassemble at in m/s";
    static String disassemblyMaxAngularVelocity = "The maximum angular velocity a Simulated Contraption is allowed to disassemble at in rad/s";
    static String disallowMidAirDisassembly = "Disallow disassembly of Simulated Contraptions in mid-air, requiring them to be within a few chunk sections of terrain";

    private SimAssembly.Comments() {
    }
}
