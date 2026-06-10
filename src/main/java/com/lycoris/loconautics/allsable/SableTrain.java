package com.lycoris.loconautics.allsable;

import java.util.List;
import java.util.UUID;

import org.joml.Vector3dc;

import net.minecraft.server.level.ServerLevel;

/**
 * A fully custom ("all-Sable", Option B) physics cart: a single carriage — a Sable sub-level riding the Create
 * track graph through its own {@link RailCarriage} — with <b>no</b> {@code CarriageContraptionEntity} and no
 * Create {@code Train}. Create's rails are still reused (via {@link RailCarriage}); everything else (motion,
 * speed, propulsion) is ours.
 *
 * <p>Each cart is fully independent: there is no multi-car coupling. Carts are joined to each other physically
 * with steel-cable ropes, so the physics engine handles any pulling between them — nothing here links them.
 *
 * <p>Motion is advanced once per game tick ({@link #tickMotion()}); {@link SableTrainDriver} holds the cart's
 * sub-level on its {@link RailCarriage} every physics substep.
 */
public final class SableTrain {

    /**
     * One LOOSE bogey: its own Sable sub-level (just the bogey block(s)) and a small {@link RailCarriage} that
     * rides the rail so the bogey sits at its rail point and pivots to the local tangent — independently of the
     * body. This is what makes bogeys turn on curves like Create (instead of being rigid with the body).
     */
    public record Bogey(UUID subLevelId, RailCarriage rail) {}

    /**
     * One carriage: the BODY sub-level id (the cart minus its bogeys) + the rail body (target) that says where
     * the body should be, plus the car's {@link Bogey loose bogeys} (each its own sub-level riding the rail).
     * {@code localLead}/{@code localTrail} are the two bogey attachment points in the sub-level's LOCAL frame
     * (captured at spawn) — used only in physics mode. They are {@code null} for pin-mode cars.
     */
    public record Car(UUID subLevelId, RailCarriage carriage, List<Bogey> bogeys,
                      Vector3dc localLead, Vector3dc localTrail) {}

    private final UUID id;
    private final ServerLevel level;
    private final Car car;
    /** When true, the cart is a free physics body held to the rail by bogey spring forces (legacy, can derail);
     *  when false, it is held to the rail by the constraint-glued / linear-bearing drive (default). */
    private final boolean physics;

    /** Current speed (blocks/tick), ramped toward {@link #targetSpeed}. */
    private double speed = 0.0;
    /** Desired speed (blocks/tick). Set by propulsion (bearing axle) or debug. Signed = direction. */
    private double targetSpeed = 0.0;
    /** Acceleration magnitude (blocks/tick per tick). */
    private double accel = 0.01;
    /** True when a Bearing Axle is driving the train this tick: the driver then servos the along-rail speed toward
     *  {@link #targetSpeed}. When false the train is purely force-driven (gravity, propellers, shoves move it). */
    private boolean powered = false;

    public SableTrain(UUID id, ServerLevel level, Car car, boolean physics) {
        this.id = id;
        this.level = level;
        this.car = car;
        this.physics = physics;
    }

    public boolean isPhysics() {
        return physics;
    }

    public UUID id() {
        return id;
    }

    public ServerLevel level() {
        return level;
    }

    public Car car() {
        return car;
    }

    public double speed() {
        return speed;
    }

    /** Restores the current speed (used by persistence so a reloaded train resumes at its previous speed
     *  instead of ramping back up from zero). */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setTargetSpeed(double targetSpeed) {
        this.targetSpeed = targetSpeed;
    }

    public double targetSpeed() {
        return targetSpeed;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    public boolean isPowered() {
        return powered;
    }

    public void setAccel(double accel) {
        this.accel = Math.abs(accel);
    }

    public double accel() {
        return accel;
    }

    /**
     * Advances the cart one game tick: ramp {@link #speed} toward {@link #targetSpeed}. For the default
     * constraint-glued drive the rail is advanced per physics substep by {@link SableTrainDriver} from the
     * cart's measured motion, so there is nothing to advance here; only the legacy spring / kinematic-pin
     * paths advance the rail per game tick.
     */
    public void tickMotion() {
        // Ramp the commanded speed toward the target (the propulsion servo / legacy paths read this).
        if (speed < targetSpeed) {
            speed = Math.min(targetSpeed, speed + accel);
        } else if (speed > targetSpeed) {
            speed = Math.max(targetSpeed, speed - accel);
        }

        // Constraint-glued cart (default: has captured bogey attachment points): advanced by the driver.
        if (!physics && car.localLead() != null && car.localTrail() != null) {
            return;
        }
        car.carriage().setSpeed(speed);
        car.carriage().tick();
        for (Bogey bogey : car.bogeys()) {
            bogey.rail().setSpeed(speed);
            bogey.rail().tick();
        }
    }
}
