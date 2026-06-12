package com.lycoris.loconautics.allsable;

import java.util.List;
import java.util.UUID;

import org.joml.Vector3dc;

import com.simibubi.create.content.trains.station.GlobalStation;

import net.minecraft.core.BlockPos;
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
     * One bogey block that stays part of the car's BODY sub-level (never detached/separated).
     *
     * <p>{@code localPos} is the bogey block's position within the body sub-level (used to find its
     * block entity each tick so {@link SableTrainDriver} can push a yaw value into it).
     * {@code rail} is a small 1-block-spacing {@link RailCarriage} seated on the rail directly under
     * the bogey's spawn position — it is never advanced/ticked; it exists only so the driver can read
     * the local rail tangent/orientation at that point on the track for the yaw calculation.
     */
    public record Bogey(BlockPos localPos, RailCarriage rail) {}

    /**
     * One carriage: the BODY sub-level id (the cart minus its bogeys) + the rail body (target) that says where
     * the body should be, plus the car's {@link Bogey loose bogeys} (each its own sub-level riding the rail).
     * {@code localLead}/{@code localTrail} are the two bogey attachment points in the sub-level's LOCAL frame
     * (captured at spawn) — used only in physics mode. They are {@code null} for pin-mode cars.
     */
    public record Car(UUID subLevelId, RailCarriage carriage, List<Bogey> bogeys,
                      Vector3dc localLead, Vector3dc localTrail) {}

    /**
     * Station-stop lifecycle, driven by {@link SableTrainDriver#tickStation}:
     * <ul>
     *   <li>{@code RUNNING}  — normal propulsion; the driver scans the rail ahead for a station.</li>
     *   <li>{@code STOPPING} — a station was detected; distance-based braking owns {@code targetSpeed} until stopped.</li>
     *   <li>{@code STOPPED}  — parked at the marker, holding {@code targetSpeed} at zero for the dwell.</li>
     *   <li>{@code DEPARTING}— dwell elapsed; normal propulsion resumes until the train is visibly moving again.</li>
     * </ul>
     */
    public enum StationState {
        RUNNING, STOPPING, STOPPED, DEPARTING
    }

    private final UUID id;
    private final ServerLevel level;
    /** The car. Mutable: wrench relocation re-seats the car on a new rail location (see {@link #relocate}). */
    private Car car;
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
    /**
     * When true the car has lost all its bogeys (its wheels were destroyed) and is no longer held to the rail:
     * the driver releases its rail constraint and the sub-level becomes a free physics body that gravity and
     * collisions act on normally. This is the single hook future derailment mechanics build on — flip it true
     * (for any reason: lost wheels, excessive speed on a curve, a collision) and the car leaves the rail next tick.
     */
    private boolean derailed = false;
    /** Latest total weight (kg) of the whole hauled consist (this car + everything coupled to it), refreshed by
     *  {@link SableTrainDriver}. Drives weight-scaled acceleration/braking. 0 until first computed. */
    private double haulMass = 0.0;
    /** REALISTIC mode: true when the hauled consist exceeds the bearing axle's (multiplier-scaled) maximum pullable
     *  mass. While set, {@link SableTrainDriver} applies no tractive effort (the train can't pull but gravity still
     *  acts, so it may still roll downhill). Always false in ARCADE mode. Set each game tick by applyPropulsion. */
    private boolean overloaded = false;

    // ---------------------------------------------------------------------------------------------
    // Station stopping (driven entirely by SableTrainDriver.tickStation).
    // ---------------------------------------------------------------------------------------------

    /** Current station-stop phase. */
    private StationState stationState = StationState.RUNNING;
    /** Along-rail distance (blocks) remaining to the station edge point: measured once when the station is first
     *  detected, decremented each tick by {@code abs(speed)}. The braking formula reads from it. */
    private double distanceToStation = Double.MAX_VALUE;
    /** Remaining dwell ticks while {@code STOPPED}. */
    private int dwellTicks = 0;
    /** The station currently being approached/served, or {@code null} when {@code RUNNING}/{@code DEPARTING}. */
    private GlobalStation currentStation = null;
    /**
     * The station this car is physically sitting at while stationary, or {@code null} when it is not at one. Unlike
     * {@link #currentStation} (driven by the transient, powered-only station-stop state machine), this is the durable
     * "present at a station" marker: set when the car parks ({@code STOPPED}) or is assembled at a station, and
     * cleared only once the car actually starts moving again. It is what tells a Create station block a Sable train
     * is present (so its flag raises and its disassemble button works), independent of whether anyone is driving.
     */
    private GlobalStation atStation = null;
    /** Body world position captured when {@link #atStation} was set, so the driver can clear it once the car has
     *  moved a real distance away (instantaneous speed is an unreliable "moving" signal — a parked car still reports
     *  small residual speed from the physics servo). {@code null} when not at a station. */
    private Vector3dc atStationPos = null;
    /** Along-rail travel direction toward the station being served: {@code +1} = node1→node2, {@code -1} = reverse.
     *  Captured when the station is detected so the braking curve drives the train the right way (carriages have
     *  no inherent front, so a station can be on either side). */
    private double stationDirection = 1.0;

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

    /**
     * Re-seats the car on a freshly built {@link RailCarriage} (and its rebuilt bogey reference rails) after a
     * wrench relocation. The body sub-level id and the local bogey attachment points ({@code localLead}/
     * {@code localTrail}) are unchanged — only the rail the car rides moves — so a new {@link Car} is built that
     * keeps those and swaps in the new carriage/bogeys. Clears speed so the moved car starts at rest.
     */
    public void relocate(RailCarriage newCarriage, List<Bogey> newBogeys) {
        this.car = new Car(car.subLevelId(), newCarriage, newBogeys, car.localLead(), car.localTrail());
        this.speed = 0.0;
        this.targetSpeed = 0.0;
        this.overloaded = false;
        this.stationState = StationState.RUNNING;
        this.distanceToStation = Double.MAX_VALUE;
        this.dwellTicks = 0;
        this.currentStation = null;
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

    /** Whether the car has left the rail and is now a free physics body (see {@link #derailed}). */
    public boolean isDerailed() {
        return derailed;
    }

    /**
     * Derails the car (true) so the driver releases its rail constraint next tick and it becomes a free
     * sub-level, or clears the flag (false). The single entry point for derailment mechanics.
     */
    public void setDerailed(boolean derailed) {
        this.derailed = derailed;
    }

    /** Total weight (kg) of the whole hauled consist, or 0 if not yet computed. */
    public double haulMass() {
        return haulMass;
    }

    /** Updates the cached hauled-consist weight (called by {@link SableTrainDriver} from the game tick). */
    public void setHaulMass(double haulMass) {
        this.haulMass = haulMass;
    }

    /** REALISTIC mode: true when the consist exceeds the axle's (multiplier-scaled) maximum pullable mass. */
    public boolean isOverloaded() {
        return overloaded;
    }

    /** Sets the overloaded state (see {@link #overloaded}); set each game tick by {@link SableTrainDriver}. */
    public void setOverloaded(boolean overloaded) {
        this.overloaded = overloaded;
    }

    // ---------------------------------------------------------------------------------------------
    // Station-stop state.
    // ---------------------------------------------------------------------------------------------

    public StationState stationState() {
        return stationState;
    }

    public void setStationState(StationState state) {
        this.stationState = state;
    }

    /** True while the train is braking for, or parked at, a station (operator controls are locked out). */
    public boolean isAtStation() {
        return stationState == StationState.STOPPING || stationState == StationState.STOPPED;
    }

    public double distanceToStation() {
        return distanceToStation;
    }

    public void setDistanceToStation(double distance) {
        this.distanceToStation = distance;
    }

    public int dwellTicks() {
        return dwellTicks;
    }

    public void setDwellTicks(int ticks) {
        this.dwellTicks = ticks;
    }

    public GlobalStation currentStation() {
        return currentStation;
    }

    public void setCurrentStation(GlobalStation station) {
        this.currentStation = station;
    }

    /** The station this car is parked/present at while stationary, or {@code null}. See {@link #atStation}. */
    public GlobalStation atStation() {
        return atStation;
    }

    public void setAtStation(GlobalStation station) {
        this.atStation = station;
        if (station == null) {
            this.atStationPos = null;
        }
    }

    /** Body position recorded when the car came to rest at its station (for distance-based clearing), or null. */
    public Vector3dc atStationPos() {
        return atStationPos;
    }

    public void setAtStationPos(Vector3dc pos) {
        this.atStationPos = pos;
    }

    /** Along-rail direction toward the station being served (+1 = node1→node2, -1 = reverse). */
    public double stationDirection() {
        return stationDirection;
    }

    public void setStationDirection(double direction) {
        this.stationDirection = direction;
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
    }
}