package com.lycoris.loconautics;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common config for Loconautics. Real options only; the example template config was removed.
 */
public final class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /** Master switch: when false, the addon behaves as if not installed (no physics assembly). */
    public static final ModConfigSpec.BooleanValue ENABLE_SABLE_MODE = BUILDER
            .comment("Master switch for assembling trains as physical Sable sub-levels.")
            .define("enableSableMode", true);

    // -------------------------------------------------------------------------
    // Train
    // -------------------------------------------------------------------------

    /** Hard cap (m/s) on the speed the Bearing Axle will drive an attached train to. -1 = defer to Create's
     *  train top speed; any other value overrides Create's limit for bearing-axle movement. */
    public static final ModConfigSpec.DoubleValue BEARING_AXLE_MAX_SPEED;
    /** Absolute cap (m/s) on a train sub-level's speed from ANY source (thrusters, propellers, physics wand,
     *  collisions, gravity on slopes, …). -1 = defer to Create's train top speed; any other value is the hard cap. */
    public static final ModConfigSpec.DoubleValue TRAIN_MAX_SPEED;

    static {
        BUILDER.push("train");

        BEARING_AXLE_MAX_SPEED = BUILDER
                .comment("Maximum speed (m/s) the Bearing Axle will drive an attached train to.",
                        "-1 defers to Create's train top speed (trainTopSpeed).",
                        "Any other value overrides Create's limit and becomes the hard cap for bearing-axle movement.")
                .defineInRange("bearingAxleMaxSpeed", -1.0, -1.0, 1000.0);

        TRAIN_MAX_SPEED = BUILDER
                .comment("Absolute maximum speed (m/s) a train sub-level may travel, regardless of what pushes it",
                        "(thrusters, propellers, physics wand, collisions, gravity on slopes, anything).",
                        "-1 defers to Create's train top speed (trainTopSpeed). Any other value is the hard cap.")
                .defineInRange("trainMaxSpeed", -1.0, -1.0, 1000.0);

        BUILDER.pop();
    }

    // -------------------------------------------------------------------------
    // Bearing Axle
    // -------------------------------------------------------------------------

    public static final ModConfigSpec.DoubleValue MASS_DIVISOR;
    public static final ModConfigSpec.DoubleValue BASE_IMPACT;

    static {
        BUILDER.push("bearing_axle");

        MASS_DIVISOR = BUILDER
                .comment("Divides total train mass (kg) to produce the stress impact per RPM.",
                        "Lower values = more stress per kg. Default: 50.0 (100 kg train → 2.0 impact).")
                .defineInRange("massDivisor", 10.0, 1.0, 1000.0);

        BASE_IMPACT = BUILDER
                .comment("Minimum stress impact regardless of train mass.",
                        "Ensures the Bearing Axle always costs something even on an empty train.")
                .defineInRange("baseImpact", 1.0, 0.0, 100.0);

        BUILDER.pop();
    }

    // ------------------------------------------------------------------ Transmission

    public static final ModConfigSpec.DoubleValue TRANSMISSION_SU_IMPACT;

    static {
        BUILDER.push("transmission");

        TRANSMISSION_SU_IMPACT = BUILDER
                .comment("Stress Units per RPM applied by the Transmission block's output shaft.",
                        "Full SU cost = TRANSMISSION_SU_IMPACT * abs(outputRPM). Default: 4.0.")
                .defineInRange("suImpact", 4.0, 0.0, 100.0);

        BUILDER.pop();
    }

    // ------------------------------------------------------------------ Analog Controller

    public static final ModConfigSpec.IntValue ANALOG_DECAY_TICKS;
    public static final ModConfigSpec.IntValue ANALOG_NEGATIVE_CAP;

    static {
        BUILDER.push("analog_controller");

        ANALOG_DECAY_TICKS = BUILDER
                .comment("Ticks between each automatic power decay step toward 0.",
                        "20 ticks = 1 second. Default: 20.")
                .defineInRange("decayTicks", 30, 1, 200);

        ANALOG_NEGATIVE_CAP = BUILDER
                .comment("Maximum negative power level the S key can reach.",
                        "E.g. 5 means S can step down to -5 at most.",
                        "Default: 5.")
                .defineInRange("negativeCap", 5, 0, 15);

        BUILDER.pop();
    }

    // ------------------------------------------------------------------ Steel Cable

    public static final ModConfigSpec.DoubleValue STEEL_CABLE_MAX_RANGE;

    static {
        BUILDER.push("steel_cable");

        STEEL_CABLE_MAX_RANGE = BUILDER
                .comment("Maximum connection range (in blocks) for the Steel Cable item.",
                        "Set to -1 (default) to automatically use 2x Simulated's current maxRopeRange.",
                        "Set to any positive value to override the range directly.")
                .defineInRange("maxRange", -1.0, -1.0, 10000.0);

        BUILDER.pop();
    }

    // ------------------------------------------------------------------ Wrench Relocation

    /** Master switch: when false, the Create wrench never relocates a Sable train sub-level (the feature is off). */
    public static final ModConfigSpec.BooleanValue WRENCH_RELOCATION_ENABLED;
    /** When true, only derailed train sub-levels can be wrench-relocated; when false, any train sub-level can. */
    public static final ModConfigSpec.BooleanValue WRENCH_RELOCATION_DERAILED_ONLY;

    static {
        BUILDER.push("wrench_relocation");

        WRENCH_RELOCATION_ENABLED = BUILDER
                .comment("Master switch for relocating Sable train sub-levels with the Create wrench.",
                        "Mirrors Create's own derailed-train relocation, but targets Sable train sub-levels.",
                        "When false the feature is completely disabled (the wrench does nothing to train sub-levels).")
                .define("enabled", true);

        WRENCH_RELOCATION_DERAILED_ONLY = BUILDER
                .comment("When true, only DERAILED train sub-levels may be relocated (matches Create's vanilla behaviour).",
                        "When false, ANY train sub-level may be relocated regardless of its derail state.")
                .define("derailedOnly", true);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}