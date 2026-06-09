package com.lycoris.loconautics.allsable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joml.Vector3dc;

import net.minecraft.server.level.ServerLevel;

/**
 * A fully custom ("all-Sable", Option B) physics train: a set of carriages, each a Sable sub-level riding the
 * Create track graph through its own {@link RailCarriage} — with <b>no</b> {@code CarriageContraptionEntity}
 * and no Create {@code Train}. Create's rails are still reused (via {@link RailCarriage}); everything else
 * (motion, speed, coupling, propulsion) is ours.
 *
 * <p>Motion is advanced once per game tick ({@link #tickMotion()}); {@link SableTrainDriver} pins each car's
 * sub-level pose to its {@link RailCarriage} every physics substep (the same collision-correct pinning the
 * hybrid system uses, just sourced from our rail math instead of a Create entity).
 *
 * <p>Layer 1 keeps it deliberately small: cars advance at a shared speed with a simple acceleration ramp;
 * rigid multi-car coupling and navigation/signals come later.
 */
public final class SableTrain {

    /**
     * One carriage: its Sable sub-level id + the rail body (target) that says where it should be.
     * {@code localLead}/{@code localTrail} are the two bogey attachment points in the sub-level's LOCAL frame
     * (captured at spawn) — used only in physics mode, where springs pull these points toward the rail
     * targets. They are {@code null} for pin-mode cars.
     */
    public record Car(UUID subLevelId, RailCarriage carriage, Vector3dc localLead, Vector3dc localTrail) {}

    private final UUID id;
    private final ServerLevel level;
    private final List<Car> cars;
    /** When true, cars are free physics bodies held to the rail by bogey spring forces (and can derail);
     *  when false, cars are kinematically teleport-pinned to the rail (smooth, rigid). */
    private final boolean physics;

    /** Current speed (blocks/tick), ramped toward {@link #targetSpeed}. */
    private double speed = 0.0;
    /** Desired speed (blocks/tick). Set by propulsion (bearing axle) or debug. Signed = direction. */
    private double targetSpeed = 0.0;
    /** Acceleration magnitude (blocks/tick per tick). */
    private double accel = 0.01;

    public SableTrain(UUID id, ServerLevel level, List<Car> cars, boolean physics) {
        this.id = id;
        this.level = level;
        this.cars = new ArrayList<>(cars);
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

    public List<Car> cars() {
        return cars;
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

    public void setAccel(double accel) {
        this.accel = Math.abs(accel);
    }

    public double accel() {
        return accel;
    }

    /**
     * Advances the train one game tick: ramp {@link #speed} toward {@link #targetSpeed}, then move every
     * carriage along the rail by that speed. Each {@link RailCarriage} keeps its own two-bogey pose.
     *
     * <p>Layer-1 coupling is loose: all cars run at the same speed, so the spacing they were placed with is
     * preserved on straights (it breathes slightly on curves, like real bogeys). Rigid coupling is a later
     * layer.
     */
    public void tickMotion() {
        // Ramp toward the target speed.
        if (speed < targetSpeed) {
            speed = Math.min(targetSpeed, speed + accel);
        } else if (speed > targetSpeed) {
            speed = Math.max(targetSpeed, speed - accel);
        }

        for (Car car : cars) {
            car.carriage().setSpeed(speed);
            car.carriage().tick();
        }
    }
}
