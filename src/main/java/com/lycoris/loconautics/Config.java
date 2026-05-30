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

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
