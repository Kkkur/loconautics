# Research — Szedann/CreateTrainPhysics (for Loconautics weight/physics features)

> Source mod cloned at `C:\Users\User\Desktop\CreateTrainPhysics-ref` (GitHub: Szedann/CreateTrainPhysics, LGPL-2.1,
> Java/Gradle, NeoForge 1.21.1). **It is NOT a Sable mod** — it does *not* make sub-levels. It mixins into Create's
> own `Train`/`Carriage` to add realistic longitudinal physics to the vanilla Create train. We reuse its **formulas**
> for our all-Sable train's speed model (Lycoris' "accel/decel depend on weight" feature), not its architecture.
>
> Whole mod = **2 mixins** (`MixinTrain`, `MixinCarriage`) + 1 accessor iface (`IPhysicsCarriage`) + Config. Tiny.

## What it simulates (README)
Gravity (inclines), realistic fuel usage, tractive-effort limit (wheel adhesion), dynamic max turn speed, mass &
power. Engines = blocks tagged as motors (steam engine + Diesel Generators / Electro Energetics / Crafts&Additions).

## The formulas we want (all from `MixinTrain`) ⭐

All accelerations are **per tick** (Create speed is blocks/tick). `v` = `speed*20` (blocks/s). Constants are theirs.

- **Mass** (`railways$getCarriageMass`): `blockCount * 500 (+ cargo)` kg per carriage; train mass = Σ. Block count
  from the contraption. → For us: use the **sub-level mass tracker** (`ServerSubLevel.getMassTracker().getMass()`)
  we already sum in `SableTrainDriver.updateAxleMass`. That live mass IS our weight.
- **Force → acceleration** ⭐ (the core "heavier = slower"): `a = force / mass / (20*20)`. This single line is the
  weight-dependent accel/decel.
- **approachTargetSpeed** (replaces Create's accel ramp) ⭐⭐ — the model to copy into `SableTrain.tickMotion()`:
  ```
  maxPower = |target - speed| * mass * (20*20)             // power needed to reach target this tick
  if accelerating (|target|>|speed|, same sign):
      force = min( enginePowerW / v , maxTractiveEffort , maxPower )
  else (braking / coasting to target):
      force = min( maxTractiveEffort , maxPower )
  a = force / mass / (20*20)
  speed += clamp toward target by a
  ```
  → Replaces our fixed `accel = 0.01`. With no engine-power model we can keep a configured tractive force, but the
  **mass term is what makes heavy trains accelerate/brake slowly** — exactly Lycoris' request.
- **Max tractive effort** (adhesion limit, caps force before wheelslip): `Σ friction * carriageMass * 9.81`,
  `friction = 0.4` (0.8 for monorail in their commented code). Prevents instant stop/start of a heavy train.
- **Gravity on inclines** ⭐ (`railways$getGravityAcceleration`): from leading vs trailing bogey Y,
  `incline = atan2(Δy, Δhoriz)`; `a_grav = -(9.81/400) * sin(incline)`. Uphill decelerates, downhill accelerates.
  → We already have leading/trailing bogey world positions in `RailCarriage` (`leadingPos`/`trailingPos`), so this
  drops in directly.
- **Rolling friction**: `0.001 * mass * 9.81` (force, opposes motion). **Aero drag**: `0.5 * (1.204*0.35*9) * v²`.
  Both subtracted each tick in `tickPassiveSlowdown` (gravity + drag + rolling), only when not at a station.
- **Terminal max speed** from power vs drag (cubic): see `railways$getMaxSpeed` — optional, lower priority.
- **Dynamic max turn speed** ⭐ (`maxTurnSpeed`): for a curve of radius `r`,
  `vmax = sqrt(9.81 * r * (friction/(1-friction))) / 20`. Above it the train should derail. `r` from
  `BezierConnection.getRadius()` (their s-bend radius approximation included). → Pairs with our curve handling; lets
  fast trains derail on tight curves (ties into Lycoris' "free movement / can derail").
- **Train-vs-train collision** (`collideWithOtherTrains`): elastic 1-D collision, restitution `e=0.5`, momentum
  conservation; explodes + `crash()` if relative speed > 6 b/s or a sharp yaw mismatch. Reference for our coupling /
  impact later.

## Persistence pattern (`MixinCarriage`) 📝
Mass + engineCount are cached per carriage and **written/read in the carriage NBT** (`@Inject` on Carriage
`write`/`read`, keys `"mass"`, `"engineCount"`), recomputed lazily from the contraption block count / motor-tag count
if absent. Mirrors what we do, and confirms per-car physical attributes should live in saved NBT.

## How to apply to our `SableTrain` (concrete)
1. In `SableTrain` keep `mass` (already summed in `SableTrainDriver.updateAxleMass`). Expose it to `tickMotion`.
2. Replace the `accel = 0.01` ramp with the `approachTargetSpeed` model above: `a = tractiveForce/mass/(400)`,
   clamped by a max tractive effort `0.4*mass*9.81`. Add gravity-on-incline from the bogey Y delta each tick.
3. Make the constants a **config** block (Lycoris wants it as a config feature): friction, rolling coeff, a
   `weightAccelEnabled` toggle, base tractive force / engine-power-per-RPM. See our `Config.java`.
4. (Later) dynamic max-turn-speed → derail when exceeded (force mode / physics bogeys).

> License note: LGPL-2.1. We are reusing **formulas/approach**, re-implemented for our own sub-level train, not
> copying their files. Keep our implementation original; cite the approach.
