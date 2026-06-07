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

    /** Optional cap on physics-train speed (blocks/tick). 0 = use Create's normal limits. */
    public static final ModConfigSpec.DoubleValue PHYSICS_TRAIN_MAX_SPEED = BUILDER
            .comment("Max speed (blocks/tick) for physics trains. 0 = use Create's default limits.")
            .defineInRange("physicsTrainMaxSpeed", 0.0, 0.0, 100.0);

    /** Render debug overlays (sub-level bounds, pose markers). */
    public static final ModConfigSpec.BooleanValue DEBUG_RENDER = BUILDER
            .comment("Render debug overlays for physics trains (sub-level bounds, pose markers).")
            .define("debugRender", false);

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

    public static final ModConfigSpec.DoubleValue TRANSMISSION_SU_IMPACT = BUILDER
            .comment("Stress Units per RPM applied by the Transmission block's output shaft.",
                    "Full SU cost = TRANSMISSION_SU_IMPACT * abs(outputRPM). Default: 4.0.")
            .defineInRange("transmissionSuImpact", 4.0, 0.0, 100.0);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}