# Research — Sable & Aeronautics source dive (for Loconautics)

> Living catalogue of useful classes/APIs found in the decompiled reference sources under
> `src/depend/Sable` (802 files) and `src/depend/Aeronautics` (1105 files). Goal: note what we **use**, what
> we **could** use, and what we **should** use, for the all-Sable physics train (Option B) and the
> bearing-axle/transmission propulsion.
>
> Legend: ⭐ = directly useful to Loconautics now · 🔭 = useful later · 📝 = good to know.
> Status: DEEP PASS DONE (sections A–Q). Sable `api/` (B), Aeronautics blocks (E), and deep dives on:
> physics/forces/constraints (H), motion models ×4 (G + H.3 + N + P), propulsion (H.5b), controls (I + Q),
> passengers (J), `Sable.HELPER` (K), `ServerSubLevel` (L), engine + helpers (O), Create-compat (P), mixin
> catalogue (M). **To build the train, read §G first, then §N (force bogeys) / §H.3 (servo) / §P (kinematic)
> for the motion model, §H.5b for propulsion, §I+§Q for controls, §K for the helper, §Q.3 for lifecycle.**

---

## A. Sable — package map (where things live)

`dev.ryanhcode.sable.*`
- **`api/`** — the public extension suraface (the intended hooks). Most valuable section. See §B.
- `sublevel/` — the sub-level impl: `ServerSubLevel`, `SubLevel`, `plot/`, `system/` (physics system),
  `entity_collision/`, `render/`, `tracking_points/`, `storage/`.
- `physics/` — physics engine glue: `impl/rapier/` (Rapier pipeline), `chunk/`, `floating_block/`,
  `config/`, `callback/`.
- `platform/` + `neoforge/platform/` — service abstractions (event, assembly, plot, render, loader).
- `mixin/` + `mixinterface/` + `mixinhelpers/` — ~60 mixin packages (collision, clip, camera, interaction
  distance, particles, explosion, fluids, portals, tracking points…). Catalogue in §D.
- `network/` — TCP/UDP sub-level sync. `render/` — sublevel rendering, dynamic shading, water occlusion.
- `command/`, `debug/`, `config/`, `sound/`, `util/`.

---

## B. Sable — Public API (`dev.ryanhcode.sable.api`)  ⭐ the core toolbox

### B.1 Block / BlockEntity hooks — make a block participate in sub-level physics
Implement these on a `Block` or `BlockEntity` so it takes part in the physics body. **This is the idiomatic
way to drive a train from inside its own blocks**, instead of teleport-pinning from outside.

- ⭐ **`BlockEntitySubLevelActor`** — `sable$tick(ServerSubLevel)` and
  **`sable$physicsTick(ServerSubLevel sub, RigidBodyHandle handle, double timeStep)`**. A BE that runs logic
  every game/physics tick *inside a sub-level* (recall: normal `BlockEntity.tick()` does NOT fire in
  sub-levels). → The Bearing Axle should implement this to **apply propulsion force to the rigid body** each
  physics tick, the Sable-native way. Also solves "BEs don't tick in sub-levels" for mass/RPM logic.
- ⭐ **`BlockEntitySubLevelPropellerActor` + `BlockEntityPropeller`** — propeller pattern:
  `getThrust()/getAirflow()/getBlockDirection()/isActive()`, and `applyForces(sub, thrustDirection, timeStep)`
  pushing `THRUST_VECTOR` at `THRUST_POSITION`. **Direct template for bearing-axle propulsion**: thrust along
  the rail tangent instead of a block facing.
- 🔭 **`BlockEntitySubLevelReactionWheel`** — `sable$getAngularVelocity(Vector3d)` + `getBlockState()`.
  Applies angular velocity → orientation/steer control (e.g. keeping a car upright / banking).
- 🔭 **`BlockSubLevelLiftProvider`** — blocks contribute lift+drag (`sable$getNormal`, `sable$getLiftScalar`,
  `sable$contributeLiftAndDrag(ctx, sub, localPose, dt, linVel, angVel, linImpulse, angImpulse, group)`),
  with `groupLiftProviders(...)` and `LiftProviderGroup` (totalLift/liftCenter/totalDrag/dragCenter).
  Aero only, but it's the canonical "block applies force at a point each physics tick" reference.
- 📝 **`BlockSubLevelCollisionShape`** — `getSubLevelCollisionShape(getter, state)` custom collision voxel
  shape per block inside a sub-level.
- 📝 **`BlockSubLevelDynamicCollider`** — `buildBoxes(VoxelColliderData)` for dynamic/animated colliders.
- 📝 **`BlockSubLevelCustomCenterOfMass`** — `getCenterOfMass(getter, state)` per-block CoM override
  (affects how the train balances / where it pivots).
- 📝 **`BlockWithSubLevelCollisionCallback`** + `physics/callback/BlockSubLevelCollisionCallback`
  (`CollisionResult`) — react when something collides with this block in a sub-level.
- 🔭 **`BlockSubLevelAssemblyListener`** — `beforeMove/afterMove(originLevel, resultingLevel, state, oldPos,
  newPos)` — per-block hook fired during assembly when a block is moved world↔sub-level. Useful to fix up
  block state / re-link on (dis)assembly.

### B.2 Physics — constraints, forces, mass  ⭐ coupling & propulsion primitives
- ⭐ **`PhysicsPipeline`** (`api/physics`) — the physics façade we already use:
  `add(ServerSubLevel, Pose3dc)` / `remove`, `teleport(body, pos, quat)`, `applyImpulse`,
  `applyLinearAndAngularImpulse`, `addLinearAndAngularVelocity`, `resetVelocity`, `readPose`, `wakeUp`,
  `addBox`, `addRope`, and **`addConstraint(subA, subB, PhysicsConstraintConfiguration)`**.
  `PhysicsPipelineBody` = base type a `ServerSubLevel` implements.
- ⭐ **Constraints** (`api/physics/constraint`) — join two bodies. `PhysicsConstraintHandle`:
  `setMotor(axis, …)`, `setContactsEnabled`, `getJointImpulses`, `remove`, `isValid`.
  - **`FixedConstraintConfiguration(pos1, pos2, orientation)`** → rigidly weld two sub-levels.
    **Use for rigid multi-car coupling** (or weld a car to its bogeys).
  - **`RotaryConstraintConfiguration(pos1, pos2, normal1, normal2)`** + `setServoCoefficients(angle,
    stiffness, damping)` → hinge/bearing. Use for **articulated couplers** or the actual bearing pivot.
  - **`GenericConstraintConfiguration(pos1, pos2, ori1, ori2, lockedAxes:Set<ConstraintJointAxis>)`** +
    `setFrame1/2` → fully configurable joint (lock chosen axes). Most flexible coupler.
  - **`FreeConstraintConfiguration`** → loose link.
  - `ConstraintJointAxis` enum = the lockable/motorised axes.
- ⭐ **Forces** (`api/physics/force`):
  - **`ForceTotal`** — accumulate then `applyForces(handle)`: `applyImpulseAtPoint(MassData|ServerSubLevel|
    MassTracker, position, force)`, `applyLinearImpulse`, `applyTorqueImpulse`, etc. **This is how to push the
    train** (apply impulse along the rail tangent at the CoM).
  - **`QueuedForceGroup(sub)`** — `applyAndRecordPointForce(point, force)` / `getRecordedPointForces()`.
  - **`ForceGroups`** registry: predefined groups incl. **`PROPULSION`**, `LIFT`, `DRAG`, `GRAVITY`,
    `LEVITATION`, `MAGNETIC_FORCE` (for the force-debug HUD coloring).
- ⭐ **Mass** (`api/physics/mass`) — confirms the bearing fix:
  - `MassData` iface: `getMass/getInverseMass/getInertiaTensor/getInverseInertiaTensor/getCenterOfMass`,
    `isInvalid()` (mass<=0).
  - `MergedMassTracker` (the live object from `ServerSubLevel.getMassTracker()`): **`update(partialTick)`**
    (non-destructive — Sable calls it each physics tick) and `getSelfMassTracker()`. Do NOT rebuild on a live
    body (that was the train-explosion bug).
  - `MassTracker.build(blockGetter, bounds)` — the one-time builder (assembly only). `BLOCK_CENTER_OF_MASS`
    BiFunction, `addBlockMass(...)`, `moveCenterOfMass(...)`.
- 🔭 **Physics objects** (`api/physics/object`): `BoxPhysicsObject`(pose, halfExtents, mass)+`BoxHandle`,
  `RopePhysicsObject`(points, radius)+`RopeHandle` (`setAttachment(point, location, sub)`, `addPoint`,
  `setFirstSegmentLength`). Ropes/boxes attachable to sub-levels → **physical couplers / chains / tethers**.
  `ArbitraryPhysicsObject` = base for custom physics objects.
- 📝 `physics/handle/RigidBodyHandle` — the body handle passed to actors; `getAngularVelocity()` etc.
- 📝 `physics/collider/{SableCollisionContext, VoxelColliderData}`, `physics/force/ForceGroup`.

### B.3 Sub-level containers & kinematic driving  ⭐
- ⭐ **`SubLevelContainer`** (abstract; `ServerSubLevelContainer`/`ClientSubLevelContainer`):
  - `getContainer(Level|ServerLevel|ClientLevel)` (we use this).
  - `getSubLevel(int x,int z)` / `getSubLevel(UUID)` (via container), `allocateNewSubLevel(Pose3d)`,
    `allocateSubLevel(UUID, x, z, Pose3d)`, `addObserver(SubLevelObserver)`, `physicsSystem()`,
    `trackingSystem()`, `getAllSubLevels()`, `getPlot(...)`, `getChunk(...)`.
- ⭐ **`KinematicContraption`** — the interface to expose a body Sable drives **kinematically with velocity**
  (smooth drag, no stutter): `sable$getPosition(double partialTick)`, `sable$getOrientation(double)`,
  `sable$getLocalBounds`, `sable$blockGetter`, `sable$getMassTracker`, `sable$shouldCollide`, `sable$isValid`,
  `sable$liftProviders`, `sable$getLocalPose(dest, partialTick)`. **Strong candidate to replace our
  teleport-pin**: implement this fed by the `RailCarriage` pose so Sable computes velocity and drags riders
  smoothly (the design doc's "Sable-native" option). Create contraptions are auto-wrapped as this.
- 🔭 **`SubLevelObserver`** — lifecycle callbacks for sub-level add/remove → clean train teardown.
- 📝 `SubLevelTrackingPlugin`, `sublevel/storage`, `ClientSubLevelContainer.getInterpolation()`
  (`ClientSableInterpolationState` — client render interpolation).

### B.4 Events & platform
- ⭐ **`api/event/SablePrePhysicsTickEvent` / `SablePostPhysicsTickEvent`** — the pre/post physics-substep
  callbacks we hook via `SableEventPlatform`. `getPhysicsSystem()` + `getTimeStep()`.
  `SableSubLevelContainerReadyEvent` — fired when a level's container is ready (good init hook).
- ⭐ **`platform/SableEventPlatform`** (`.INSTANCE`) — `onPhysicsTick`, `onPostPhysicsTick`. Impl does
  `NeoForge.EVENT_BUS.addListener` per call → **listeners accumulate** (multiple drivers coexist; that's why
  our custom-train driver doesn't disturb the hybrid one).
- 🔭 **`platform/SableAssemblyPlatform`**, `SablePlotPlatform`, `SableSubLevelRenderPlatform`,
  `SableChunkEventPlatform`, `SableLoaderPlatform`, `SablePlatform` — other service hooks (assembly, plot,
  render, chunk events). Worth a look when wiring assembly/render for the custom train.

### B.5 Assembly & helpers  ⭐ (we already use these)
- ⭐ **`SubLevelAssemblyHelper`**:
  - `assembleBlocks(level, anchor, Iterable<BlockPos> blocks, BoundingBox3ic bounds)` → `ServerSubLevel`
    (MOVES blocks world→sub-level; builds mass tracker; teleports body to pose). We use it in `SubLevelBridge`
    and `SableTrainSpawner`.
  - `gatherConnectedBlocks(origin, level, maxBlocks, FrontierPredicate)` → `GatherResult(blocks, checkedBlocks,
    boundingBox, State{SUCCESS,NO_BLOCKS,TOO_MANY_BLOCKS})`. Flood-fill capture (we use it, excluding tracks).
  - `AssemblyTransform(anchor, plotAnchor, …, Rotation, level)`, `moveBlocks/moveOtherStuff/moveTrackingPoints`,
    `kickFromContainingSubLevel`, `FrontierPredicate.isValidConnection(...)`.
- 🔭 **`SubLevelHelper`** (`EntityRot`) — general sub-level helpers (transform points/entities, rotations).
  TODO: catalogue methods.
- 🔭 **`entity/EntitySubLevelUtil`** — entity↔sub-level transforms (where to look for mounting/dragging the
  player, seats, passenger handling). TODO: catalogue.
- 📝 `math/OrientedBoundingBox3d`, `math/LevelReusedVectors`, `particle/ParticleSubLevelKickable`,
  `schematic/SubLevelSchematicSerializationContext` (save/load sub-levels as schematics),
  `command/SubLevelArgumentType` (a sub-level command-arg type — handy for debug commands),
  `block/BlockSubLevelLiftProvider$LiftProviderContext(pos,state,dir)`.

### B.6 Companion math (`dev.ryanhcode.sable.companion.math`)  📝
`Pose3d`(position/orientation/rotationPoint, transformPosition, …), `BoundingBox3i`/`BoundingBox3ic`
(minX..maxZ, toAABB), used throughout. The pose transform (`position + Q·(local - rotationPoint)`) is the
key to aligning logicalPose with rendered blocks (our `targetPosition`).

---

## C. Sable — internals worth knowing (impl)  📝 — TODO expand
- `sublevel/ServerSubLevel` — `logicalPose()/renderPose()/lastPose`, `buildMassTracker()/getMassTracker()`,
  `updateMergedMassData(partialTick)`, `getPlot()`, `getLevel()`, `getUniqueId()`, `isRemoved()`,
  `updateLastPose()`, `setSplitFrom(...)`.
- `physics/impl/rapier/RapierPhysicsPipeline` — builds mass tracker once on add (line ~282), updates it each
  tick; `updateContraptionPoses` (kinematic velocity drag). `sublevel/system/SubLevelPhysicsSystem`
  (`getLevel/getPipeline/getPartialPhysicsTick`, calls `updateMergedMassData` each tick).
- `sublevel/entity_collision/*` — player↔sub-level collision (reads `logicalPose`). `mixin/clip_overwrite/*`
  — raytrace through sub-levels (reads `logicalPose`). These were the keys to the collision fix.
- TODO: `sublevel/tracking_points/*` (entities/points that ride sub-levels — relevant to passengers/seats),
  `sublevel/plot/ServerLevelPlot`, `floating_block/*`.

---

## D. Sable — mixin catalogue  📝 — TODO
~60 mixin packages. Notable by name (to investigate): `interaction_distance` (reach inside sub-levels —
relevant to the controls problem), `clip_overwrite`, `entity` (entities sticking to sub-levels), `physics`,
`impact`, `explosion`, `fluids_on_sub_levels`, `portal`, `tracking_points`, `voxel_shape_iteration`,
`sign_interaction`, `punching`, `recoil`, `world_border`.

---

## E. Aeronautics (`dev.{simulated_team.simulated, eriksonn.aeronautics, ryanhcode.offroad}`)
Three mods bundled. `simulated` = the physics/sub-level layer built on Sable (most relevant); `eriksonn` =
aircraft/propellers/levitite; `offroad` = vehicles/borehead. **All compile against Sable's API, so they are
worked examples of every hook in §B.**

### E.1 Assembly / disassembly  ⭐ (clean APIs — use for our station integration & disassembly)
- ⭐ **`simulated.util.SimAssemblyHelper`**:
  - **`assembleFromSingleBlock(level, selfPos, toAssemble, includeStart, includeEncasingGlue)`** →
    `AssemblyResult(SubLevel subLevel, BlockPos offset)` (throws `AssemblyException`). One-call sub-level
    creation from a seed block (flood-fills connected blocks). Cleaner than wiring `SubLevelAssemblyHelper`
    ourselves; the "bearing" mod called this by reflection.
  - **`disassembleSubLevel(level, subLevel, subLevelAnchor, disassemblyGoal, Rotation, playSound)`** — clean
    teardown back to the world. **Use this for the custom-train disassembly layer** (vs our hand-rolled one).
  - `rotationFrom90DegRots(int)`, `register()`.
- ⭐ **`simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity`** — Aeronautics' "assembler"
  block (≈ our station). **Crucial motion-model reference**: holds a **`FreeConstraintHandle
  alignmentConstraint`** and in `tick()` servos the sub-level to a goal with
  `setMotor(ConstraintJointAxis.LINEAR_X/Y/Z, localGoal.{x,y,z}, stiffness=1000, damping=50, false, 0)`.
  i.e. it **drags a sub-level to a target position via a motorised constraint** rather than teleporting it.
  See §G.

### E.2 Force / constraint block entities  ⭐ (templates for our actors)
- ⭐ **`simulated.content.blocks.spring.SpringBlockEntity implements BlockEntitySubLevelActor`** — the cleanest
  example of `sable$physicsTick(sub, handle, dt)`: it finds its paired spring and applies a spring impulse
  between the two anchor points (works across sub-levels). **Direct template for the Bearing Axle applying
  propulsion force**, and for spring/buffer couplers. `getCenter()`, `tryChangeLengthOrError`, `getPairedSpring`.
- ⭐ **`simulated.content.blocks.swivel_bearing.SwivelBearingBlockEntity extends KineticBlockEntity`** — a
  kinetic block that owns a **`RotaryConstraintHandle`** and servos it:
  `setMotor(RotaryConstraintHandle.DEFAULT_AXIS, goal, kP, kD, false, 0)`, `attachConstraints(subLevel, pos)`,
  `assemble()/disassemble()`, `updateServoCoefficients()` (reads a friction config). **Template for: the
  bearing-axle pivot, articulated couplers, and "kinetic RPM → constraint motor".** Also has
  `SwivelBearingPeripheral` (ComputerCraft) and a goggle `addToTooltip`.
- 📝 `simulated` also has `auger_shaft`, `physics_staff` (grab/move sub-levels), `nameplate`, ziplines,
  honey-glue, handles — all `InteractCallback`s (see E.4).

### E.3 Propellers / thrust (propulsion reference)  ⭐
- ⭐ **`eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity extends KineticBlockEntity
  implements BlockEntitySubLevelPropellerActor`** — kinetic block that produces thrust:
  `getThrust()/getAirflow()/getBlockDirection()/isActive()`, `getConfigThrust/Airflow/Radius`, a
  `PropellerActorBehaviour`, `onSpeedChanged`. Variants: Andesite/Wooden/Smart propeller. **This is the
  closest existing thing to "kinetic RPM → physics force on the body" — the model for bearing-axle propulsion
  along the rail tangent.** Note it ties thrust to RPM (`onSpeedChanged(previousSpeed)`), confirming kinetic
  speed DOES reach a BE inside a sub-level via the actor path.
- 🔭 `eriksonn` `propeller_bearing/PropellerBearingBlockEntity`, air-pressure system, `mixin/sable_hooks`,
  `mixin/propeller_collision`, `mixin/balloon` (`SimAssemblyHelperMixin`, `SubLevelAssemblyHelperMixin`).
- 🔭 `ryanhcode.offroad` — `content/contraptions/borehead_contraption/BoreheadBearingContraption`,
  borehead bearing block/BE, mounted storage services. Vehicle/contraption reference.

### E.4 Interaction / controls  ⭐ (the controls pattern we struggled with)
- ⭐ **`simulated.index.SimClickInteractions`** — static registry `CLICK_INTERACTION_ENTRIES` of
  `InteractCallback` client handlers, incl. **`SteeringWheelHandler`, `ThrottleLeverHandler`**,
  `PhysicsStaffMouseHandler`, `ClientHandleHandler`, `PhysicsAssemblerGUIHandler`, `SpringItemHandler`,
  `ZiplineClientManager`, `HoneyGlueClientHandler`. **This is the idiomatic way to drive controls on a
  sub-level block** (client reads `mc.hitResult` block pos, sends a packet) — the pattern to copy for
  train controls (throttle/brake/reverser) instead of fighting Create's contraption interaction.
- 📝 `simulated.api`: `CustomStressImpactTooltipProvider` (custom SU tooltip — **our BearingAxleBlockEntity
  could implement this** for a proper stress bar), `IDirectionalAnalogOutput` (analog signal per face —
  relevant to the Analog Controller), `BearingSlowdownController` (clockwork bearing easing/countdown),
  `ConditionalDisplayTarget`, `SimpleResourceManager<T>` (codec-backed datapack registry).
- 📝 `simulated.content.blocks.steering_wheel` / `throttle_lever` (the actual control blocks behind E.4),
  `display_sources`, `navigation_targets`.

---

## F. Config references  📝
- `simulated.config.server.physics.SimPhysics` and `.blocks.SimAssembly` — Aeronautics' physics/assembly
  config knobs (mass scaling, substeps, limits). Good values to mirror/tune for trains.
- Sable `physics/config/*` and `config/*` — engine config (timestep, iterations).

---

## G. ⭐ Cross-cutting conclusion — train MOTION MODEL options (ranked)
We now have FOUR concrete ways to make the sub-level follow the `RailCarriage` pose (see also §N for #4 and
§H.3 for #2). Quick ranking: **#4 (force bogeys) or #2 (servo constraint) for the real train; #1 (teleport-pin)
only for the current debug keystone.**
1. **Teleport-pin (current, `SableTrainDriver`)** — `pipeline.teleport` + `resetVelocity` each substep.
   Pros: simple, reuses the proven collision fix. Cons: teleport every tick can stutter rider drag / fight
   the solver; not "physical".
2. **Motorised constraint to a kinematic anchor (RECOMMENDED)** — exactly what `PhysicsAssemblerBlockEntity`
   does: create a `Free`/`Generic` constraint between the car sub-level and a virtual anchor, and each tick
   `setMotor(LINEAR_X/Y/Z [+ angular], goalFromRailCarriage, stiffness, damping)`. Sable then moves the body
   *physically* toward the rail pose → smooth rider drag, real collisions, no teleport hacks. Strongest
   candidate; copy `PhysicsAssemblerBlockEntity.tick()`.
3. **`KinematicContraption`** (§B.3) — implement the interface so Sable drives the body kinematically from
   `sable$getPosition/Orientation(partialTick)`. Cleanest conceptually; Sable computes velocity for drag.
   More integration (registering as a kinematic body) but the most "native".
4. **Force bogeys (most authentic — §N)** — the car is a FREE rigid body; each bogey applies a stiff spring
   toward its rail point (vertical+lateral) + a drive force along the rail tangent (from axle RPM), exactly
   like `WheelMountBlockEntity`. Orientation emerges from physics. Best realism (suspension, derailing,
   coupling all "just work" as forces); needs tuning. **This is how Aeronautics actually does free vehicles.**
- **Propulsion**: model the Bearing Axle as a `BlockEntitySubLevelActor`/propeller-actor (§B.1, §E.2/E.3):
  apply force along the rail tangent in `sable$physicsTick`, magnitude from kinetic RPM. Couples naturally
  with model 2/3 (a free-rolling train pushed by axle force + braking) instead of forcing a target speed.
- **Coupling (multi-car)**: `FixedConstraint` (rigid) or `Generic`/`Rotary` (articulated) between adjacent
  car sub-levels; or `SpringBlockEntity`-style buffers; or a `RopePhysicsObject` drawbar.

---

## H. DEEP DIVE — physics building blocks (verbatim recipes)  ⭐⭐ read before coding the train

### H.1 `RigidBodyHandle` (`api/physics/handle`) — the per-body actuator
Get one with `RigidBodyHandle.of(ServerSubLevel)` (returns null if no container) — internally
`physicsSystem.getPhysicsHandle(subLevel)`. The actor callback `sable$physicsTick(sub, handle, dt)` hands you
one directly. Methods:
- `applyImpulseAtPoint(Vector3dc position, Vector3dc force)` — impulse at a WORLD point (pipeline.applyImpulse).
- `applyLinearAndAngularImpulse(impulse, torque[, wakeUp])`, `applyLinearImpulse`, `applyAngularImpulse`,
  `applyTorqueImpulse`.
- `getLinearVelocity(Vector3d dest)`, `getAngularVelocity(Vector3d dest)`.
- `addLinearAndAngularVelocity(lin, ang)`, `teleport(pos, quat)`, `isValid()`.
- `applyForcesAndReset(ForceTotal)` → calls `forceTotal.applyForces(this)`.
> Impulse = force × dt. All the worked examples multiply their force by `timeStep` before applying.

### H.2 Applying force from a block — the `SpringBlockEntity.sable$physicsTick` recipe ⭐
The cleanest worked example of a block pushing its own body. Pattern (per physics tick):
```
ServerSubLevelContainer container = SubLevelContainer.getContainer(sub.getLevel());
SubLevelPhysicsSystem system = container.physicsSystem();
system.updatePose(sub);                                  // refresh pose before reading positions
Vector3d worldPoint = sub.logicalPose().transformPosition(localCenter, new Vector3d());  // local→world
Vector3d velAtPoint = Sable.HELPER.getVelocity(level, worldPoint, new Vector3d());       // body velocity @point
// build a force in WORLD space (e.g. Hooke: alignment*stiffness; damping: -k*relativeVel), then:
ForceTotal ft = new ForceTotal();
ft.applyImpulseAtPoint(sub, localCenter, sub.logicalPose().transformNormalInverse(worldForce, tmp).mul(dt));
ft.applyLinearAndAngularImpulse(ZERO, sub.logicalPose().transformNormalInverse(worldTorque, tmp));
handle.applyForcesAndReset(ft);                          // <-- pushes the body
```
Key helpers: `Pose3d.transformPosition(local,dest)` (local→world point), `transformNormal(vec)` /
`transformNormalInverse(vec,dest)` (rotate vectors world↔local), `Sable.HELPER.getVelocity(level, worldPt,
dest)`. Spring constants seen: stiffness ≈145, linear damping ≈−4.5·relVel, align-torque ≈20·(nA×−nB),
ang-damping ≈−2·(relAngVel·n), all ·sizeScale·dt. **For bearing-axle propulsion**: in `sable$physicsTick`,
`ft.applyImpulseAtPoint(sub, localCoM, localRailTangent · thrust · dt)` then `applyForcesAndReset`; thrust
from kinetic RPM. (Confirms a kinetic BE CAN run physics each tick inside a sub-level via this actor path.)

### H.3 Servo a body to a target pose — the `PhysicsAssemblerBlockEntity` constraint recipe ⭐⭐ (motion model #2)
How Aeronautics drags a sub-level to a goal WITHOUT teleporting (smooth, carries riders):
```
PhysicsPipeline pipeline = container.physicsSystem().getPipeline();
// anchor body to the WORLD (sublevelA = null): pos1 = world anchor, pos2 = local point on body, + ref orientation
FreeConstraintConfiguration cfg = new FreeConstraintConfiguration(worldAnchor, localPointOnBody, refOrientation);
FreeConstraintHandle c = pipeline.addConstraint(null, serverSubLevel, cfg);   // either A or B may be null = world
// lock orientation hard, position soft initially:
c.setMotor(ConstraintJointAxis.ANGULAR_X, 0, 13000, 1000, false, 0);   // (axis, target, stiffness, damping, ?, ?)
c.setMotor(ConstraintJointAxis.ANGULAR_Y, 0, 13000, 1000, false, 0);
c.setMotor(ConstraintJointAxis.ANGULAR_Z, 0, 13000, 1000, false, 0);
c.setMotor(ConstraintJointAxis.LINEAR_X, 0, 1e-6, 50, false, 0);       // soft until a goal is set
// each tick, drive the linear axes toward the goal in the constraint's LOCAL frame:
Vector3d localGoal = refOrientation.transformInverse(worldGoal, new Vector3d());
c.setMotor(ConstraintJointAxis.LINEAR_X, localGoal.x, 1000, 50, false, 0);   // stiffness 1000, damping 50
c.setMotor(ConstraintJointAxis.LINEAR_Y, localGoal.y, 1000, 50, false, 0);
c.setMotor(ConstraintJointAxis.LINEAR_Z, localGoal.z, 1000, 50, false, 0);
// when done: if (c.isValid()) c.remove();
```
Constants used: LINEAR stiffness 1000 / damping 50; ANGULAR stiffness 13000 / damping 1000. Goal is expressed
in the constraint LOCAL frame (`refOrientation.transformInverse(worldGoal)`). Readiness check: pose orientation
within tolerance AND `current.distance(goal) < 0.2` for >5 ticks. **For our train**: create one Free/Generic
constraint per car anchored to world, and each tick set the LINEAR goal = `RailCarriage.center()` and the
ANGULAR target = `RailCarriage.orientation()`. This is the recommended replacement for the teleport-pin.
`setMotor` signature (`PhysicsConstraintHandle`): `(ConstraintJointAxis axis, double target, double stiffness,
double damping, boolean ?, double ?)` — last two flags unverified (likely velocity-mode + maxForce).

### H.4 Constraint configs (`api/physics/constraint/*`)
- `FreeConstraintConfiguration(Vector3dc pos1, Vector3dc pos2, Quaterniondc orientation)` — 6-DoF joint you
  motorise per-axis (used above).
- `FixedConstraintConfiguration(pos1, pos2, orientation)` — rigid weld (multi-car rigid coupling).
- `GenericConstraintConfiguration(pos1, pos2, ori1, ori2, Set<ConstraintJointAxis> lockedAxes)` — lock chosen
  axes, free the rest; `setFrame1/2`. Most flexible (e.g. lock all but yaw for a bogie pivot).
- `RotaryConstraintConfiguration(pos1, pos2, normal1, normal2)` — hinge about an axis;
  `RotaryConstraintHandle.setServoCoefficients(angle, stiffness, damping)`, `DEFAULT_AXIS`.
- `pipeline.addConstraint(@Nullable subA, @Nullable subB, config)` → typed handle; null body = anchor to world.
  Handle: `setMotor(...)`, `setContactsEnabled(bool)`, `getJointImpulses(lin,ang)`, `isValid()`, `remove()`.
- `ConstraintJointAxis` = { LINEAR_X/Y/Z, ANGULAR_X/Y/Z } (the motorised DoFs).

### H.5b Propulsion — the propeller-actor recipe (definitive, cleaner than ForceTotal) ⭐⭐
`BlockEntitySubLevelPropellerActor` (extends `BlockEntitySubLevelActor`) default impl:
```
default void sable$physicsTick(sub, handle, dt) {
    if (prop.isActive()) applyForces(sub, Vec3.of(prop.getBlockDirection().getNormal()), dt);
}
default void applyForces(sub, thrustDirection, dt) {
    Vec3 thrust = thrustDirection.scale(prop.getScaledThrust() * dt);          // force × dt
    QueuedForceGroup fg = sub.getOrCreateQueuedForceGroup(ForceGroups.PROPULSION.get());
    fg.applyAndRecordPointForce(centerOf(prop.getBlockPos()), thrust);          // apply + record for HUD
}
```
**Bearing-axle propulsion = copy this**: implement `BlockEntitySubLevelActor.sable$physicsTick`, set
`thrustDirection = RailCarriage.forward()` (rail tangent, world space), magnitude from kinetic `getSpeed()`
(RPM), and `sub.getOrCreateQueuedForceGroup(ForceGroups.PROPULSION.get()).applyAndRecordPointForce(axleWorldPos,
dir·thrust·dt)`. Cleanest path: no `ForceTotal`/`handle` bookkeeping, and it shows up in Sable's force HUD.

### H.5 `SwivelBearingBlockEntity` — kinetic RPM → rotary servo (bearing/coupler) ⭐
`extends KineticBlockEntity`; owns a `RotaryConstraintHandle handle`. On `assemble()` it builds the assembled
sub-level (`SimAssemblyHelper`) and `attachConstraints(subLevel, getConstraintPos(...))`; each tick servos it:
`handle.setMotor(RotaryConstraintHandle.DEFAULT_AXIS, goalAngle, kP, kD, false, 0)` (or friction-only when
idle: `setMotor(DEFAULT_AXIS, 0, 0, friction, false, 0)` from config `swivelBearingFriction`).
`disassemble()` tears down. Template for: the bearing-axle pivot, and articulated/rotary couplers between cars.

---

## I. DEEP DIVE — controls / interaction on sub-level blocks  ⭐⭐ (solves our old controls problem)
Aeronautics drives steering wheels / throttle levers WITHOUT Create's contraption interaction. The whole
machinery lives in `simulated.util.{click_interactions, hold_interaction}` + per-control handlers.

### I.1 The reach fix — `Sable.HELPER.projectOutOfSubLevel` ⭐⭐
`BlockHoldInteraction.inInteractionRange(player, target, reachBuffer)`:
```
double distance = getInteractionRange(player) + reachBuffer;     // player reach
Vec3 eye = player eye;
return Sable.HELPER.projectOutOfSubLevel(player.level(), target, new Vector3d())   // sub-level pt -> WORLD pt
        .distanceSquared(eye) < distance*distance;
```
**This is exactly what the hybrid controls were missing.** Our blocker was Create's `canInteractWithBlock`
measuring distance in *contraption* space. The right test: take the control block's point (inside the
sub-level), `Sable.HELPER.projectOutOfSubLevel(level, point, dest)` to get its real WORLD position, then a
normal eye-distance reach check. No contraption, no bypass mixin.

### I.2 The interaction flow (client)
- **`InteractCallback`** (`util/click_interactions`) — client hooks: `onUse/onAttack/onPick(modifiers,
  action, KeyMapping)`, `onScroll(dx,dy)`, `onMouseMove(yaw,pitch)`, `clientTick(level, player)`, each
  returning `Result(cancelled)`. `filterInteract(cb, Input, modifiers, action, KeyMappings)` gates by key.
  `Input.mouse(key)` / `Input.key(key, scan)`; `KeyMappings(use, attack, middle)`.
- Registered in **`SimClickInteractions`** (`SimClickInteractions.register(handler)`); the mod routes raw
  mouse/key input through every registered callback.
- **`BlockHoldInteraction extends InteractCallback`** — "press-and-hold a block" base:
  `startHold(level, player, pos)` (stores `interactionPos`, registers with `HoldInteractionManager`),
  `onUse` toggles release, `onMouseMove`→`activeOnMouseMove(yaw,pitch)`, `activeTick`, `renderOverlay` (HUD),
  `getInteractionPos()`, static reach helpers in §I.1.
- **`ThrottleLeverHandler`** (worked example): `startHold` reads `ThrottleLeverBlockEntity.state` (0–15) and
  INVERTED prop; `activeOnMouseMove(yaw,pitch)` → `value -= pitch/180`, clamp 0..1, `signal = round(value*15)`;
  on change → `changed()` → `VeilPacketManager.server().sendPacket(new ThrottleLeverSignalPacket(pos, signal))`.
  `activeTick` re-checks `inInteractionRange(player, pos.getCenter(), 0)` and bails when out of range.
  `renderOverlay` draws the brass value bar. Server `ThrottleLeverBlockEntity.state` holds the 0–15 signal.
- The hold is started from the control block's **normal `useWithoutItem`** (Sable delivers vanilla
  use/click to the sub-level block, client+server) — no Create contraption packet needed.

### I.3 → Train controls recipe
Mirror this for throttle / reverser / brake blocks: control block `useWithoutItem` starts a
`BlockHoldInteraction`; mouse/scroll sets a value; reach via `projectOutOfSubLevel`; a packet carries
`(blockPos, value)` to the server; the BE sets the `SableTrain`'s target speed / direction / brake. Sibling
files to copy: `steering_wheel/SteeringWheelHandler` + `SteeringWheelBlockEntity`,
`throttle_lever/ThrottleLeverBlockEntity`, packets in `simulated.network.packets`, `HoldInteractionManager`.

### I.4 `Sable.HELPER` (the `SableHelper`) — central util to catalogue ⭐ (TODO full method list)
Seen used everywhere: `getContaining(Level, Vec3i)` / `getContaining(BlockEntity)` (which sub-level holds a
pos/BE — null for the main world), `getVelocity(Level, worldPoint, dest)` (velocity of whatever sub-level is
at that world point), **`projectOutOfSubLevel(Level, point, dest)`** (sub-level-local → world). This is the
go-to for any "where is this sub-level thing in the real world / how fast is it moving" question.

---

## J. DEEP DIVE — passengers / entities riding sub-levels  ⭐ (riders on the train)
- **`api/entity/EntitySubLevelUtil`**:
  - `Sable.HELPER.getTrackingSubLevel(entity)` → the sub-level an entity is currently riding (null = world).
    **The query for "is this player standing on a train car?"** and which one.
  - `setOldPosNoMovement(entity)` — rewrites `entity.xo/yo/zo` through `lastPose`/`logicalPose` so a rider's
    interpolation stays glued to a moving sub-level (no rubber-banding). Call when teleporting riders.
  - `kickEntity(subLevel, entity)` — eject an entity to WORLD coords with correct position/velocity/look
    (`logicalPose.transformPosition/Normal`). **Use at disassembly / when a rider leaves the car.**
  - `shouldKick(entity)` = entity type NOT in tag `sable:retain_in_sub_level`.
  - `getCustomEntityOrientation(entity, pt)` / `hasCustomEntityOrientation` — overridable hook to give riders
    a custom orientation (banking / upside-down). Base = none.
- **`mixin/entity/entities_stick_sublevels/*`** (large package, client+server+packets+players+effects) — the
  machinery that makes entities ride and move with a sub-level. **This is why a rider standing on a moving car
  is dragged automatically** (it already works thanks to the collision fix). Nothing to build for basic
  riders; just don't fight it.
- **`mixin/entity/entities_turn_with_sub_levels/*`** — entities rotate (yaw) with the sub-level (banking
  turns). **`mixin/entity/entity_rotations_and_riding/*`** — riding/rotation glue.
- **`sublevel/tracking_points/`** — `TrackingPoint` + `SubLevelTrackingPointSavedData` (`TakenLoginPoint`):
  persistent points that ride a sub-level across save/quit (e.g. where a player logs back in on a moving
  train). `SubLevelTrackingPointObserver`. Relevant if riders must persist on a parked train through a restart.
- `Sable.HELPER` methods seen so far (the `SableHelper`): `getContaining(Level,Vec3i)`,
  `getContaining(BlockEntity)`, `getVelocity(Level, worldPt, dest)`, `projectOutOfSubLevel(Level, pt, dest)`,
  `getTrackingSubLevel(Entity)`. (Full list still TODO — it's the single most useful util class.)

---

## K. DEEP DIVE — `Sable.HELPER` = `ActiveSableCompanion`  ⭐⭐ (the central util, full surface)
`Sable.HELPER` (static, `= SableCompanion.INSTANCE`) — 451-line util, the answer to almost every
"sub-level ↔ world" question. Grouped:
- **Which sub-level contains X**: `getContaining(level, …)` overloads for `Vec3i / Position / Vector3dc /
  ChunkPos / SectionPos / (chunkX,chunkZ) / (blockX,blockZ) / Entity / BlockEntity` → `SubLevel` (null = main
  world). Client variants `getContainingClient(…)` → `ClientSubLevel`. `getAllIntersecting(level, BoundingBox3dc)`
  → all sub-levels overlapping a box. `isInPlotGrid(level, chunkX, chunkZ)`.
- **Coordinate projection**: `projectOutOfSubLevel(level, pos, dest)` (sub-level-local → world; the controls
  reach fix). (Inverse is `subLevel.logicalPose().transformPositionInverse(...)`.)
- **Raytrace / iterate through sub-levels**: `runIncludingSubLevels(level, origin, checkOrigin, subLevel,
  BiFunction<S,BlockPos,T>)` and `findIncludingSubLevels(...)` — run/scan a function over blocks across the
  world AND any sub-levels along the way (the proper way to pick a block that may be inside a moving body).
- **Distance honouring sub-levels**: `distanceSquaredWithSubLevels(level, a, b)` and
  `rectilinearDistanceWithSubLevels(level, a, b)` (many overloads). **The reach/interaction metric** — use
  these instead of raw world distance when either point may be inside a sub-level.
- **Velocity**: `getVelocity(level, worldPos, dest)`, `getVelocity(level, subLevel, pos, dest)`,
  `getVelocityRelativeToAir(level, pos, dest)` — body velocity at a world point (for damping / wind / drag).
- **Rider queries**: `getTrackingSubLevel(entity)` (standing on), `getLastTrackingSubLevel(entity)`,
  `getVehicleSubLevel(entity)` (mounted in), `getTrackingOrVehicleSubLevel(entity)`,
  `getEyePositionInterpolated(entity, pt)`, `getFeetPos(entity, distanceDown)`.
- Misc: `getClientLevel()`.
> For the train this single class covers: is-the-player-on-a-car (`getTrackingSubLevel`), where-is-the-car
> -block-in-world (`projectOutOfSubLevel`), control reach (`distanceSquaredWithSubLevels`), and rider/wind
> velocity (`getVelocity`).

---

## L. `ServerSubLevel` / `SubLevel` — the body we drive (full surface)  ⭐
`SubLevel` (base): `logicalPose()` (authoritative pose for collision/clip), `lastPose()` (previous, for
interpolation/velocity), `boundingBox()`, `getPlot()`, `getLevel()`, `getUniqueId()/setUniqueId(UUID)`,
`isRemoved()/markRemoved()/onRemove()`, `updateLastPose()`, `updateBoundingBox()`, `getName()/setName()`.

`ServerSubLevel` (server) adds:
- ⭐ **`getUserDataTag()` / `setUserDataTag(CompoundTag)`** — arbitrary PERSISTENT NBT on the sub-level. Store
  our train metadata here (trainId, car index, bogey rail position) so a parked train survives restart
  without a separate SavedData.
- ⭐ **`latestLinearVelocity` / `latestAngularVelocity`** (public `Vector3d` fields) — current body velocity,
  read directly (cheaper than a handle call).
- Forces: `getOrCreateQueuedForceGroup(ForceGroup)`, `applyQueuedForces(system, handle, dt)`,
  `enableIndividualQueuedForcesTracking(bool)`, `getQueuedForceGroups()` — categorised force accumulation
  (feeds the force-debug HUD). `prePhysicsTick(system, handle, dt)` / `prePhysicsTickBegin()` — where actors run.
- Mass: `getMassTracker()` (live `MergedMassTracker`), `getSelfMassTracker()`, `buildMassTracker()` (assembly
  only — see the bearing-explosion fix), `updateMergedMassData(partialTick)`.
- Managers: `getReactionWheelManager()`, `getFloatingBlockController()`, `getHeatMapManager()`.
- Splitting: `setSplitFrom(containing, originalPose)`, `getSplitFromSubLevel()/Pose()`, `clearSplitFrom()`
  (a sub-level breaking off a containing one — relevant to coupling/decoupling cars).
- Networking: `getTrackingPlayers()`, `playerSink()`, `lastNetworkedPose()`, `getLastNetworkedStopped()`,
  `deleteAllEntities()`.
- `getRuntimeId()`, `getLevel()` (ServerLevel), `getPlot()` (ServerLevelPlot — `getCenterBlock()`,
  `getBoundingBox()`, `getCenterChunk()`, `newEmptyChunk(...)`).

---

## M. Sable mixin catalogue (breadth — ~60 packages, one-liners)  📝
What each `mixin/<pkg>` adapts vanilla for (investigate the named ones if that feature misbehaves on a train):
- **`clip_overwrite`** — raytrace/clip routed through sub-levels (reads `logicalPose`). Core to collision/pick.
- **`entity/entities_stick_sublevels`** — entities ride & move with sub-levels (riders on the car). Big pkg.
- **`entity/entities_turn_with_sub_levels`**, **`entity/entity_rotations_and_riding`** — riders rotate/bank.
- **`entity/entities_in_blocks`**, **`entity/entity_aabb_lookup`**, **`entity/arrows_hit_blocks`**,
  **`entity/entity_leashing`**, **`entity/entity_pathfinding`** — suffocation, AABB queries, projectiles,
  leads, mob pathing across sub-levels.
- **`interaction_distance`** — reach/interaction range inside sub-levels (pairs with `projectOutOfSubLevel`).
- **`physics`** — engine hooks. **`impact`** — collision impact damage. **`explosion`** — explosions on/through.
- **`plot`** — the plot grid region. **`chunk_container_replacement`** — sub-level chunk storage.
- **`sublevel_render`**, **`block_decal_render`**, **`dynamic_directional_shading`**, **`sky_light_shadow`**,
  **`water_occlusion`** — rendering of sub-levels. **`camera`** — camera inside sub-levels.
- **`fluids_on_sub_levels`**, **`sculk_vibrations`**, **`enchanting_table`**, **`sign_interaction`**,
  **`respawn_point`** (beds on sub-levels), **`portal`**, **`world_border`**, **`stop_rain`**,
  **`prevent_freezing`/`player_freezing`**, **`prevent_overgrowth`**, **`recoil`**, **`punching`**,
  **`death_message`**, **`toast`**, **`voxel_shape_iteration`**, **`level_accelerator`** (fast block access),
  **`tracking_points`**, **`block_placement`/`block_properties`**, **`udp`** (sub-level net), **`options`**,
  **`game_test`**, **`debug_render`**, **`command`**, **`config`**, **`compatibility`** (jade/exposure/vista…).
> `mixinterface/*` = duck-interfaces these mixins add; `mixinhelpers/*` = shared helper logic.

---

## N. DEEP DIVE — `WheelMountBlockEntity` = a full physics VEHICLE wheel  ⭐⭐⭐ (best bogey model)
`offroad ... wheel_mount.WheelMountBlockEntity extends KineticBlockEntity implements BlockEntitySubLevelActor`.
This is Aeronautics' free-rolling physics vehicle wheel — **the closest existing thing to a train bogey**, and
it shows the fully force-based approach (no teleport, no servo-constraint; the body is a free rigid body pushed
by wheel forces). Its `sable$physicsTick(sub, handle, dt)` recipe:
1. **Suspension spring (vertical)** — keep the body at rest height above the surface:
   - `TerrainCastResult cast = computeMaxExtensionToTerrain(wheelDownAxis, pose)` (raycast down through
     sub-levels; returns `maxExtension`, hit `normal`, hit `subLevel`, `minInteractingBlock`).
   - `springLength = clamp(distance - radius, 0, restDist=0.65)`;
     `dampingForce = -localVelocity.y * dampingStrength`;
     `springForce = ((restDist - springLength)*springStrength + dampingForce) * dt` along the hit normal.
   - Mass-normalised by `massData.getInverseNormalMass(wheelPos, UP)` so heavy bodies don't sink.
2. **Drive + brake (rolling axis `normalD`)** — `kineticSpeed = getSpeed()` (RPM, signed by facing):
   `queuedForce.fma( vel·normalD * -rollFriction + kineticSpeed*(1-brake)*surfaceBraking*1.75, normalD) * dt`.
   i.e. **kinetic RPM → forward force along the rolling direction**, minus rolling resistance; redstone above
   the block = brake.
3. **Lateral grip (side axis `sideD`)** — `queuedForce.fma( vel·sideD * -0.6*friction, sideD) * dt`. Stops the
   wheel sliding sideways.
4. `forceTotal.applyImpulseAtPoint(sub, wheelPos, queuedForce)`, batched, then `handle.applyForcesAndReset`.
- Surface friction from `PhysicsBlockPropertyHelper.getFriction(blockState)`; brake from
  `level.getSignal(pos.above(), DOWN)/15`. Wheel-spin visual in `tick()` from `translation/circumference`.

### → TRAIN BOGEY = a rail wheel (motion model #4, RECOMMENDED for authenticity)
Replace "raycast to terrain" with "the rail point under the bogey" (we already have it from the
`TravellingPoint`/`RailCarriage`). Each bogey, in `sable$physicsTick`, applies to the free car body:
- a **strong spring** pulling the bogey toward its rail world-point (vertical + LATERAL — a train can't slide
  off, so lateral stiffness is high, unlike a car tyre's soft grip);
- a **drive force along the rail tangent** (`RailCarriage.forward()`), magnitude from bearing-axle RPM;
- velocity damping for stability.
Two bogeys → the car's orientation emerges from physics (no manual yaw/pitch). This is the most authentic and
reuses this exact proven recipe. Trade-off vs the servo-constraint (§H.3): forces let the car wobble/derail
under extreme inputs (realistic but needs tuning); the constraint pins harder. Prototype both.

---

## O. Engine (`RapierPhysicsPipeline`) + `SubLevelHelper`  📝/⭐
### O.1 `physics/impl/rapier/RapierPhysicsPipeline` (the `PhysicsPipeline` impl, 523 lines)
- `add(ServerSubLevel, Pose3dc)` / `remove(ServerSubLevel)` — register a sub-level rigid body.
- **`add(KinematicContraption)` / `remove(KinematicContraption)`** — the **motion model #3 path**: keeps an
  `activeContraptions` map; each `physicsTick` → `updateContraptionPoses()` reads `sable$getPosition(pt)` /
  `sable$getOrientation(pt)` and calls native `Rapier3D.setKinematicContraptionTransform(scene, id, centerOf
  Mass, pose, velocity)` — **Sable computes the velocity from the pose delta and feeds Rapier**, so riders get
  dragged smoothly. Create contraptions are added here (that's how the hybrid already moves). To use #3 for our
  train: implement `KinematicContraption` over the `RailCarriage` pose and `pipeline.add(it)`.
- `addConstraint(@Nullable subA, @Nullable subB, config)` — real impl (constraints between two bodies, or one
  body ↔ world when a side is null). `readPose(body, dest)`, `teleport`, `applyImpulse(body, pos, force)`,
  `applyLinearAndAngularImpulse`, `addLinearAndAngularVelocity`, `getLinear/AngularVelocity`, `wakeUp`,
  `addRope/addBox`, chunk/block-change handlers, `init(gravity, universalDrag)`.
### O.2 `api/SubLevelHelper`  ⭐ (entity local-frame + chains)
- **`pushEntityLocal(subLevel, entity[, anchor])` / `popEntityLocal(...)`** — move an entity INTO / OUT OF a
  sub-level's local frame so you can run vanilla logic (interaction, seating, look math) in local space, then
  restore. The idiomatic way to handle a passenger/seat or a click on a moving car correctly.
- **`getConnectedChain(SubLevel)`** / `getLoadingDependencyChain(ServerSubLevel)` — the set of sub-levels
  connected to / depended-on by one. **Relevant to multi-car**: a coupled train is a chain of connected
  sub-levels (load/unload and iterate them together).
- `getVelocityRelativeToAir(...)`, `registerWindProvider(BiFunction<Vector3dc,Level,Vector3dc>)` — wind/air.
- nested `EntityRot` (`apply(entity)` / `copy(entity)`) — capture/restore entity rotation across transforms.

---

## P. `KinematicContraption` recipe (motion model #3) + the Create-compat layer
### P.1 How Create contraptions implement `KinematicContraption` (the reference impl)
`neoforge/mixin/compatibility/create/contraptions/AbstractContraptionEntityMixin` makes every Create
`AbstractContraptionEntity implements KinematicContraption`:
- `sable$addToPipeline(level)` = `SubLevelPhysicsSystem.require(level).getPipeline().add(this)`;
  `sable$removeFromPipeline` = `.remove(this)`.
- **`sable$getPosition(partialTick)`** = take CoM (local) − rotationOffset, `applyRotation(·, pt)`,
  + rotationOffset + `prevAnchor.lerp(anchor, pt)` → world CoM. (Interpolated for smooth render.)
- **`sable$getOrientation(partialTick)`** = build a `Matrix3d`, rotate each column by `applyRotation(·, pt)`,
  `getNormalizedRotation(Quaterniond)`.
- `sable$getMassTracker()`, `sable$blockGetter()` (= `contraption.getContraptionWorld()`),
  `sable$getLocalBounds(bounds)`, `sable$shouldCollide()` (=true), `sable$getFloatingClusterContainer()`.
> **For our train model #3**: a `SableTrain` car implements `KinematicContraption` with `sable$getPosition/
> Orientation(pt)` = the `RailCarriage` pose interpolated prev→current by `partialTick` (keep last + current
> pose each tick), `sable$getMassTracker()` = the sub-level's tracker, `sable$blockGetter()` = the sub-level,
> register with `pipeline.add(it)`. Sable then drives it kinematically with computed velocity (smooth rider
> drag). **The interpolation (prev/current pose) is the bit our teleport-pin lacks.**

### P.2 The Create-compat breadth (`neoforge/mixin/compatibility/create/*`)  📝 — big deal for trains
Sable makes a LOT of Create machinery work INSIDE sub-levels: `belt/*`, `harvester_*`, `block_breakers`,
`basin_interactions`, `airflow` (fans/AirCurrent), `blaze_burner`, `chain_conveyor`, `blueprint`, and the
`contraptions/*` set (`ContraptionColliderMixin`, `ContraptionHandlerClientMixin`,
`ContraptionControlsRendererMixin`, `ContraptionVisualMixin`, `Matrix3dAccessor`). **Implication: a physics
train car can contain working Create contraptions/kinetics** (belts moving items, fans, etc.) — not just
static blocks. Also the place to look when a specific Create block misbehaves on a moving car. (Our hybrid
already rides on `AbstractContraptionEntityMixin` making the carriage a KinematicContraption; the all-Sable
pivot replaces THAT entity but keeps all this block-level compat.)

---

## Q. Controls plumbing + sub-level lifecycle (completing §I)  ⭐
### Q.1 Client routing — `HoldInteractionManager`
Single active hold at a time: static `active`; `start(handler)` (stops the previous, `active.start()`),
`stop()`, `tick(level, player)` each client tick → `active.activeTick(...)`; if it returns true, auto-stop.
`isActive([handler])`, `getCrouchBlockingTicks()`. The mod feeds raw mouse/scroll/keys into `active`'s
`onMouseMove/onScroll/onUse`. → control block's `useWithoutItem` → `handler.startHold(level, player, pos)` →
`HoldInteractionManager.start(handler)`.
### Q.2 Server apply — packet pattern
`ThrottleLeverSignalPacket(BlockPos pos, int signal) implements CustomPacketPayload` — `Type` =
`Simulated.path("throttle_lever_signal")`, `CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ::pos,
ByteBufCodecs.INT, ::signal, ::new)`, `handle(ServerPacketContext)` → `level.getBlockEntity(pos)` → set the
BE's value. **Copy verbatim for train throttle/reverser/brake** (pos + value → BE → `SableTrain.setTargetSpeed`
/ direction / brake). (Aeronautics uses Veil's `VeilPacketManager`; we use NeoForge payloads — same shape.)
### Q.3 Lifecycle — `SubLevelObserver` ⭐
`container.addObserver(observer)`; callbacks `onSubLevelAdded(subLevel)`,
`onSubLevelRemoved(subLevel, SubLevelRemovalReason)`, `tick(SubLevelContainer)`. **Use this to tear down a
`SableTrain`** when one of its car sub-levels is removed (destroyed/unloaded/disassembled), instead of polling.
Cleaner than the ad-hoc lifecycle in the hybrid `PhysicsTrainTickHandler`.

---

## Y. PROPULSION CHAIN (per Lycoris — authoritative) ⭐⭐
The three control blocks form ONE Create-kinetics chain; only the Bearing Axle touches the train:
1. **Analog Controller** — player-mounted cab throttle; emits a **redstone signal 1..15** (redstone-link).
2. **Transmission** — a **redstone link sits on top**; the transmission **lets through more/less rotation**
   (rotation comes from **separate Create generators**) depending on that 1..15 signal. = a kinetic throttle/valve.
3. **shaft** → **Bearing Axle** — reads the resulting **RPM** (`getSpeed()`, signed) and **that RPM is the
   train's velocity** (`target = rpm/256 · maxSpeed`). The axle also computes stress from train mass.
So: generators → (transmission gated by Analog signal) → shaft → bearing axle RPM → train speed. The Analog
Controller / Transmission are NOT read directly by the train — they shape the RPM the axle sees, via Create's
networks. `SableTrainDriver.applyPropulsion` therefore reads ONLY the bearing axle.
> OPEN QUESTION being tested: do Create's kinetic/redstone networks actually run INSIDE a Sable sub-level?
> BEs don't tick there (bearing handoff §4). The `[sabletrain] diag axleRPM=…` log answers this: if a powered
> chain on board still shows axleRPM=0, the network isn't ticking in-sub-level → that's the next thing to solve
> (manually tick the sub-level's kinetic graph, or drive the chain from the game tick). Bearing-axle handoff
> with the full RPM→bps spec lives at `Downloads/HANDOFF_BEARING_AXLE_PROPULSION.md` (not in the repo).

## Z. TODO (remaining, lower value)
- [x] Sable `api` deep dive (B, H, J, K, L). [x] Aeronautics force/constraint/controls (E, H, I).
      [x] mixin catalogue (M).
- [ ] `SubLevelHelper` (EntityRot) + `schematic/SubLevelSchematicSerializationContext` full method lists.
- [ ] `eriksonn` air-pressure system + `PropellerActorBehaviour` internals (thrust curve vs RPM).
- [ ] `offroad` borehead contraption tick (a full custom vehicle worked example) — likely the closest analog
      to a moving multi-block physics vehicle; worth a dedicated read before Phase 3/4.
- [ ] `simulated` networking packets (ThrottleLeverSignalPacket etc.) + `HoldInteractionManager` (client routing).
- [ ] `ServerLevelPlot` full surface; `sublevel/render` client interpolation (`ClientSableInterpolationState`).
- [ ] Prototype motion model #2 (motorised Free constraint, §H.3) vs current teleport-pin once the keystone
      ride-test passes; and bearing-axle as a propeller-actor (§H.5b).
