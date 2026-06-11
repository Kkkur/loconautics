package com.lycoris.loconautics;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common config for Loconautics. Real options only; the example template config was removed.
 */
public final class Config {

    /** Train physics behaviour. Both modes scale acceleration and SU cost with consist mass; they differ only in
     *  whether a maximum pullable mass is enforced. */
    public enum PhysicsMode {
        /** No mass cap — any consist weight is always movable. */
        ARCADE,
        /** A configurable, multiplier-scaled maximum pullable mass; above it the axle produces no tractive effort. */
        REALISTIC
    }

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

    // --- train.physics: arcade/realistic mode + the realistic mass cap ---
    /** Physics mode: ARCADE (no mass cap) or REALISTIC (mass cap enforced). Mass-scaled acceleration and SU cost
     *  apply in BOTH modes. */
    public static final ModConfigSpec.EnumValue<PhysicsMode> PHYSICS_MODE;
    /** REALISTIC mode only: the x1 maximum pullable consist mass (kg). The Bearing Axle's gear multiplier
     *  (x1/x2/x4/x8) raises this cap proportionally. A consist heavier than the effective cap cannot be pulled. */
    public static final ModConfigSpec.DoubleValue BASE_MAX_PULLABLE_MASS;
    /** Floor (m/s²) on the weight-scaled traction limit, so an arbitrarily heavy consist still always creeps
     *  forward instead of stalling to a halt (the "train always moves" guarantee). */
    public static final ModConfigSpec.DoubleValue MIN_ACCELERATION;

    // --- train.dynamics: weight- and slope-based driving dynamics (always on in both modes) ---
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

        // ----- train.physics: arcade/realistic mode + the realistic mass cap -----
        BUILDER.push("physics");

        PHYSICS_MODE = BUILDER
                .comment("Train physics mode.",
                        "ARCADE: no mass cap — any consist weight is always movable.",
                        "REALISTIC: a consist heavier than the (multiplier-scaled) maximum pullable mass cannot move.",
                        "Mass-scaled acceleration and mass-scaled SU cost apply in BOTH modes; the cap is the only difference.")
                .defineEnum("mode", PhysicsMode.ARCADE);

        BASE_MAX_PULLABLE_MASS = BUILDER
                .comment("REALISTIC mode only: the x1 maximum pullable consist mass (kg).",
                        "The Bearing Axle's gear multiplier (x1/x2/x4/x8) raises this cap proportionally (and costs",
                        "proportionally more SU). A consist heavier than the effective cap cannot be pulled.")
                .defineInRange("baseMaxPullableMassKg", 10000.0, 1.0, 1.0e9);

        MIN_ACCELERATION = BUILDER
                .comment("Floor (m/s^2) on the weight-scaled traction limit, so an arbitrarily heavy consist still",
                        "always creeps forward instead of stalling to a halt. Applies in both modes.")
                .defineInRange("minAcceleration", 0.25, 0.0, 1000.0);

        BUILDER.pop(); // physics

        // ----- train.dynamics: weight- and slope-based driving dynamics (always on) -----
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
                        "Lower values = more stress per kg. Default: 500.0 (500 kg train -> 1.0 added impact).",
                        "The Bearing Axle's gear multiplier (x1/x2/x4/x8) multiplies the whole impact.")
                .defineInRange("massDivisor", 500.0, 1.0, 1.0e6);

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
