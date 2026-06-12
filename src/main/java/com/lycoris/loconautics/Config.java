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
    /** Maximum number of Create bogeys allowed in a single carriage (one glued cluster). Default 2, matching
     *  Create's structural limit of two bogeys per carriage. */
    public static final ModConfigSpec.IntValue MAX_BOGEYS_PER_CARRIAGE;

    // --- train.physics: physics mode + mass-cap (the realistic/arcade toggle) ---
    /**
     * Physics mode. ARCADE: any consist is always movable (no mass cap). REALISTIC: a consist heavier than the
     * (multiplier-scaled) {@link #BASE_MAX_PULLABLE_MASS} produces no tractive effort. Mass-scaled acceleration and
     * SU scaling apply identically in BOTH modes.
     */
    public enum PhysicsMode { ARCADE, REALISTIC }

    /** Selected {@link PhysicsMode}. */
    public static final ModConfigSpec.EnumValue<PhysicsMode> PHYSICS_MODE;
    /** REALISTIC mode only: the x1 maximum pullable consist mass (kg). The bearing axle's gear multiplier scales
     *  this cap proportionally (x2/x4/x8). A consist above the scaled cap cannot be driven. */
    public static final ModConfigSpec.DoubleValue BASE_MAX_PULLABLE_MASS;
    /** Floor (m/s²) on the weight-scaled traction limit, so an arbitrarily heavy consist still always creeps forward
     *  (the "train always moves" guarantee). Applies in both modes. */
    public static final ModConfigSpec.DoubleValue MIN_ACCELERATION;

    // --- train.dynamics: weight- and slope-based driving dynamics (always-on in both modes) ---
    /** The consist mass (kg) at which {@code baseAcceleration}/{@code baseDeceleration} apply directly. */
    public static final ModConfigSpec.DoubleValue DYNAMICS_REFERENCE_MASS;
    /** Acceleration (m/s²) at {@code referenceMass}; scales as referenceMass/mass for heavier/lighter consists. */
    public static final ModConfigSpec.DoubleValue DYNAMICS_BASE_ACCELERATION;
    /** Braking deceleration (m/s²) at {@code referenceMass}; scales the same way. */
    public static final ModConfigSpec.DoubleValue DYNAMICS_BASE_DECELERATION;
    /** Absolute ceiling (m/s²) on accel/decel, so a near-empty consist can't change speed unrealistically fast. */
    public static final ModConfigSpec.DoubleValue DYNAMICS_MAX_ACCELERATION;
    /** Scale braking grip with weight: lighter trains slip (brake weaker, slide further), heavier ones grip. */
    public static final ModConfigSpec.BooleanValue WEIGHT_SCALES_BRAKING_SLIP;
    /** Braking adhesion (0..1) of a near-zero-mass train — the fraction of braking force it can actually apply. */
    public static final ModConfigSpec.DoubleValue DYNAMICS_MIN_BRAKING_ADHESION;
    /** Consist mass (kg) at/above which braking adhesion is full (1.0 — no slip). */
    public static final ModConfigSpec.DoubleValue DYNAMICS_FULL_ADHESION_MASS;
    /** Add stress impact from the rail incline (steeper = more). Motion on slopes is left to Sable's gravity. */
    public static final ModConfigSpec.BooleanValue SLOPE_EFFECTS_ENABLED;
    /** Extra stress impact (SU) added per degree of rail incline — the steeper the angle, the more stress. */
    public static final ModConfigSpec.DoubleValue SLOPE_STRESS_FACTOR;

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

        MAX_BOGEYS_PER_CARRIAGE = BUILDER
                .comment("Maximum number of Create bogeys allowed in a single carriage (one glued cluster).",
                        "Create caps a carriage at 2 bogeys; assembly is refused if any carriage exceeds this. Default: 2.")
                .defineInRange("maxBogeysPerCarriage", 2, 1, 16);

        // ----- train.physics: physics mode + mass cap (realistic/arcade toggle) -----
        BUILDER.push("physics");

        PHYSICS_MODE = BUILDER
                .comment("Physics mode.",
                        "ARCADE: any consist is always movable (no maximum pullable mass).",
                        "REALISTIC: a consist heavier than baseMaxPullableMassKg (scaled by the bearing axle's gear",
                        "multiplier) cannot be driven at all.",
                        "Mass-scaled acceleration and SU cost apply identically in BOTH modes.")
                .defineEnum("mode", PhysicsMode.ARCADE);

        BASE_MAX_PULLABLE_MASS = BUILDER
                .comment("REALISTIC mode only: the x1 maximum pullable consist mass (kg).",
                        "The bearing axle's gear multiplier (x1/x2/x4/x8) raises this cap proportionally (and costs",
                        "proportionally more SU). A consist above the scaled cap produces no tractive effort.")
                .defineInRange("baseMaxPullableMassKg", 10000.0, 1.0, 1.0e9);

        MIN_ACCELERATION = BUILDER
                .comment("Floor (m/s^2) on the weight-scaled traction limit, so an arbitrarily heavy consist still",
                        "always creeps forward (the 'train always moves' guarantee). Applies in both modes.")
                .defineInRange("minAcceleration", 0.25, 0.0, 1000.0);

        BUILDER.pop(); // physics

        // ----- train.dynamics: weight- and slope-based driving dynamics (always-on in both modes) -----
        BUILDER.push("dynamics");

        DYNAMICS_REFERENCE_MASS = BUILDER
                .comment("Consist mass (kg) at which baseAcceleration/baseDeceleration apply directly.",
                        "Lighter consists accelerate/brake faster, heavier ones slower.")
                .defineInRange("referenceMass", 1000.0, 1.0, 1000000.0);

        DYNAMICS_BASE_ACCELERATION = BUILDER
                .comment("Acceleration (m/s^2) of a referenceMass consist. Scales with referenceMass/consistMass.")
                .defineInRange("baseAcceleration", 4.0, 0.0, 1000.0);

        DYNAMICS_BASE_DECELERATION = BUILDER
                .comment("Braking deceleration (m/s^2) of a referenceMass consist. Scales with referenceMass/consistMass.")
                .defineInRange("baseDeceleration", 6.0, 0.0, 1000.0);

        DYNAMICS_MAX_ACCELERATION = BUILDER
                .comment("Absolute ceiling (m/s^2) on acceleration and braking, so a near-empty consist can't",
                        "change speed unrealistically fast.")
                .defineInRange("maxAcceleration", 20.0, 0.0, 1000.0);

        WEIGHT_SCALES_BRAKING_SLIP = BUILDER
                .comment("Lighter trains have more wheel slip when braking (brake weaker, slide further);",
                        "heavier trains grip and stop more predictably.")
                .define("weightScalesBrakingSlip", true);

        DYNAMICS_MIN_BRAKING_ADHESION = BUILDER
                .comment("Braking grip (0..1) of a near-zero-mass train: the fraction of braking force it can apply",
                        "before its wheels slip. Lower = more sliding for light trains.")
                .defineInRange("minBrakingAdhesion", 0.35, 0.0, 1.0);

        DYNAMICS_FULL_ADHESION_MASS = BUILDER
                .comment("Consist mass (kg) at/above which braking grip is full (1.0, no slip).")
                .defineInRange("fullAdhesionMass", 2000.0, 1.0, 1000000.0);

        SLOPE_EFFECTS_ENABLED = BUILDER
                .comment("The Bearing Axle adds stress impact from the rail incline (steeper = more stress).",
                        "Motion on slopes is NOT altered here — it is left to Sable's gravity acting on the consist",
                        "mass (uphill naturally slows, downhill naturally speeds up, capped by trainMaxSpeed).")
                .define("slopeEffects", true);

        SLOPE_STRESS_FACTOR = BUILDER
                .comment("Extra stress impact (SU) added per DEGREE of rail incline (steeper angle = more stress).",
                        "E.g. 0.5 on a 30-degree slope adds 0.5 * 30 = 15 SU on top of the base + weight impact.")
                .defineInRange("slopeStressPerDegree", 0.5, 0.0, 100.0);

        BUILDER.pop(); // dynamics

        BUILDER.pop(); // train
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
                        "Lower values = more stress per kg. Default: 500.0 (500 kg train → +1.0 impact, so",
                        "baseImpact 1.0 + 1.0 = 2.0 SU at 1 RPM). The gear multiplier scales the whole impact.")
                .defineInRange("massDivisor", 500.0, 1.0, 100000.0);

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

    // ------------------------------------------------------------------ Derailment

    /** Whether a train derails when it corners too hard (lateral force = mass × speed × turn-rate). */
    public static final ModConfigSpec.BooleanValue DERAIL_ON_CURVE;
    /** Lateral force (mass × speed × turn-rate) above which a cornering train flies off the rail. */
    public static final ModConfigSpec.DoubleValue DERAIL_MAX_LATERAL_FORCE;
    /** Whether a train derails when it runs off the end of a track (with no buffer) above an impact. */
    public static final ModConfigSpec.BooleanValue DERAIL_AT_TRACK_END;
    /** Head-on impact (mass × speed) at a dead end above which the train derails instead of parking. */
    public static final ModConfigSpec.DoubleValue DERAIL_MAX_END_IMPACT;

    static {
        BUILDER.push("derailment");

        DERAIL_ON_CURVE = BUILDER
                .comment("Whether a train derails when it takes a curve too hard.",
                        "The cornering load is mass * speed(m/s) * turn-rate(rad/s) — i.e. heavier, faster, and",
                        "sharper-turning trains push harder on the rails. Above maxLateralForce it leaves the rail.")
                .define("derailOnCurve", true);

        DERAIL_MAX_LATERAL_FORCE = BUILDER
                .comment("Cornering load (mass * speed * turn-rate) above which a train derails on a curve.",
                        "Higher = harder to derail. TUNE IN-GAME: lower it until reckless cornering derails but",
                        "normal running does not. Scales with train mass, so heavy trains derail at lower speeds.",
                        "Default 60.0.")
                .defineInRange("maxLateralForce", 60.0, 0.0, 1.0e9);

        DERAIL_AT_TRACK_END = BUILDER
                .comment("Whether a train derails when it runs off the END of a track (no buffer present).",
                        "Below maxEndImpact it parks at the dead end; above it, it flies off the rail.")
                .define("derailAtTrackEnd", true);

        DERAIL_MAX_END_IMPACT = BUILDER
                .comment("Head-on impact (mass * speed in m/s) at a dead end above which the train derails.",
                        "Higher = harder to derail. Scales with mass. Default 50.0.")
                .defineInRange("maxEndImpact", 50.0, 0.0, 1.0e9);

        BUILDER.pop();
    }

    // ------------------------------------------------------------------ Diagnostics

    /** Verbose logging of the carriage collision "bounce" loop (per-car velocity/energy across the physics solve,
     *  rail slide vs speed, speed-cap activations, rope coupling extension, consist-mass staleness). Heavy log
     *  spam — enable only while diagnosing. All such lines carry the {@code [bounce]} tag. */
    public static final ModConfigSpec.BooleanValue BOUNCE_DEBUG;

    static {
        BUILDER.push("diagnostics");

        BOUNCE_DEBUG = BUILDER
                .comment("Verbose per-substep/per-tick logging of the carriage collision 'bounce' loop:",
                        "per-car velocity and kinetic energy across the physics solve (PRE/POST), rail slide vs",
                        "speed, speed-cap activations, rope coupling extension, and consist-mass staleness.",
                        "Every line is tagged [bounce]. Spams the log heavily — enable only while diagnosing.",
                        "Default: false.")
                .define("bounceDebug", false);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}