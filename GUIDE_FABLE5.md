# GUIDE for Fable 5 — Loconautics work queue (2026-06-10)

> **Read order:** this file → `HANDOFF_NEXT_CLAUDE.md` §0a (all-Sable state) → `RESEARCH_sable_aeronautics.md`
> (Sable/Aeronautics API) → `RESEARCH_CreateTrainPhysics.md` (weight/physics formulas). Don't re-derive what's
> already in those — this guide is the task list + pointers. Keep token use low: read the specific file/section a
> task names, not everything.

## 0. CURRENT STATE (post-merge)
- Branch `feature/all-sable-physics-train`, HEAD = merge `9f9b85f` (merged Lycoris' `feature/controller-and-trans-fix`
  cleanly — no conflicts; his work and the persistence work touch disjoint files). **Builds OK** (`./gradlew build`).
- Merged jar **`c89df05`** deployed to the NEW test instance (see workflow).
- The merge brought in Lycoris' NEW code: **steel cable** (`content/steelcable/*`), **transmission** rework
  (`content/transmission/*`), **analog controller** rewrite (`content/analogcontroller/*`), bind-frequency GUI
  (`foundation/menu|screen/BindFrequency*`), and new **Rope\*** mixins (`mixin/Rope*`, `mixin/client/Rope*`) targeting
  Aeronautics' rope blocks (`dev.simulated_team.simulated.content.blocks.rope.*`). Build also gained an
  `extractSimulated` gradle task (new Aeronautics/Simulated source dep).
- **This merged build is reported to CRASH ON LAUNCH** — that's task #1.

## 1. WORKFLOW (CHANGED — new test instance)
- Build: `./gradlew build` → jar at `build/libs/loconautics-1.0.0.jar`. Verify md5.
- **Deploy to (NEW):** `C:\Users\User\curseforge\minecraft\Instances\Loconautics\mods\loconautics-1.0.0.jar`
  (NOT "Skybound SMP" anymore). That instance has create 6.0.10 + aeronautics-bundled 1.2.1 + sable 1.2.2 +
  "Who touched my train" + railways navigator. `md5sum` deployed == built before trusting a test.
- **Read logs from (NEW):** `C:\Users\User\curseforge\minecraft\Instances\Loconautics\logs\latest.log` and
  `.../Loconautics/crash-reports/`. The log/crash is now LOCAL → you can read it directly (no friend round-trip).
- No hot-reload (mixins). User restarts MC; you read the log. Instrument with `[tag]` logs when unsure — absence of
  a log line is also data.
- Commit/push only when the user says. End commits with `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.

## 2. TASK QUEUE (priority order, from Lycoris)

### #1 — FIX THE STARTUP CRASH ⛔ (do first)
The merged build compiles but crashes when the game opens. **Get the log first, don't guess** (HANDOFF rule).
- Launch the Loconautics instance → read `logs/latest.log` + newest `crash-reports/*.txt`. Look for
  `Mixin apply failed` / `was not found` / a registration NPE / `NoClassDefFound`.
- **Prime suspects** (new in the merge, `defaultRequire:1` so a bad one is fatal):
  `mixin/RopeStrandHolderDestroyMixin`, `mixin/RopeStrandHolderBehaviorAccessor`,
  `mixin/client/RopeConnectorRendererMixin`, `mixin/client/RopeWinchRendererMixin`,
  `mixin/client/RopeWinchBlockEntityAccessor` (in `loconautics.mixins.json`). They target Aeronautics rope classes —
  an injection point / signature that doesn't match the modpack's Aeronautics version will crash. Also check steel
  cable / transmission **registration** (`registry/LoconauticsRegistries`) and any missing model/texture.
- Fix the offending mixin selector / registration. Re-build, re-deploy, confirm clean start (`[sabletrain]
  persistence active` line should appear = our code loaded).

### #2 — Bogeys must each rotate on their OWN axis (Create-style), and support N>2 bogeys
**Requirement (Lycoris + screenshots):** like Create, **each bogey pivots at its own position to the rail tangent
under it** (NOT the whole car rotating about its centre), and with 2 bogeys both turn, with 3 all three turn.
**Root cause:** our car is ONE rigid Sable sub-level posed by a single orientation (`RailCarriage.orientation()` =
chord of the 2 bogeys). The bogey *blocks* are rigid inside it → they point along the chord, not their local tangent;
and `RailCarriage` only models 2 `TravellingPoint`s (3rd+ bogey ignored → the >2 "bug").
**Plan (pick with user):**
- (A) **Render-side articulation** (closest to Create, lowest physics risk): keep the body one sub-level posed by the
  chord, but at render time rotate the bogey blocks to their local rail tangent. Needs: identify bogey blocks
  (`AbstractBogeyBlock`) in the sub-level, give each a `TravellingPoint` for its tangent, and a CLIENT mixin into
  Sable's sub-level block render to apply a per-bogey transform around the bogey's pivot. Cosmetic only (collision
  unaffected). See `mixin/client/CarriageBogey*Mixin` (we already hide Create's ghost bogey) + Create's
  `BogeyRenderer` for the rotation it applies.
- (B) **Multi-body articulation** (physically real, bigger): each bogey = its own small sub-level pinned to its
  `TravellingPoint` (local tangent); body = sub-level linked to the bogeys by rotary constraints
  (`RESEARCH_sable_aeronautics.md` §H.4). True pivoting + derail; large rework, risk to the working train.
- **N bogeys:** generalise `RailCarriage` to a LIST of bogey `TravellingPoint`s (Create's `Carriage` has a bogey
  list) spaced along the car; body pose = best-fit (e.g. first↔last) and each bogey gets its own tangent (for A or B).
- Recommendation: do **(A)** first (gets the Create look with least risk), generalise to N bogeys at the same time.

### #3 — Trains "die" (stop being a Sable train) when you leave & re-enter the world = PERSISTENCE ✅ DONE
- **CONFIRMED WORKING in-game 2026-06-10** (Loconautics instance): spawn → exit to title → re-enter → log showed
  `persistence active — 1 saved train(s)` → `restored train <id> (1 car(s)) in minecraft:overworld`, and the train
  kept riding the rail (diag positions advancing). Mass also reaches the axle (`subMass==axleTrainMass`). Nothing left
  here unless multi-car/edge cases surface.
- **Already implemented & merged**: `allsable/SableTrainPersistence` + `SableTrainStore` (global SavedData
  `loconautics_sable_trains`). Saves on spawn/addcar/clear/periodic/ServerStopping; restores on ServerStarted+tick by
  retrying until the track graph (`Create.RAILWAYS.trackNetworks`) + sub-level (`container.getSubLevel(uuid)`) are
  loaded, then rebuilds the `RailCarriage` (`RailCarriage.restore`, re-injects `forward0` so orientation isn't reset).
- **UNTESTED in-game.** Self-diagnosing logs: `[sabletrain] persistence active — N saved train(s)`, `restored train
  …`, and `waiting to restore …: <dimension/graph/sub-level missing>`. Spawn a train → exit to title → re-enter →
  read the log; those lines tell you exactly if/where it fails. The `SubLevelObserver` now ignores `UNLOADED` (only
  `REMOVED` drops a car) so unload/shutdown no longer kills the train. The disk file is
  `saves/<world>/data/loconautics_sable_trains.dat`.

### #4 — Move the train with the Physics Wand, but RAIL-CONSTRAINED (not free)
- Aeronautics' physics staff grabs/moves a sub-level freely. We want it to push the train **along the rail** only.
- Approach: intercept the staff's force/move on our train sub-levels and project it onto the rail tangent
  (`RailCarriage.forward()`), feeding it as a Δspeed instead of a free translation. See `RESEARCH_sable_aeronautics.md`
  §E.4 (physics_staff / InteractCallback) and §N (force model). Ties into #6.

### #5 — Manual carriage coupling via the Steel Cable (no magic auto-connect)
Lycoris' design (verbatim intent): player builds the train, glues blocks with **honey glue**, and on "create Sable
train" we spawn **each carriage as a SEPARATE sub-level** — they are only coupled if the player connected them with
the **rope** or the new **steel cable** (a rope-like item with **double the rope's reach**, already added:
`content/steelcable/SteelCableItem|SteelCableTracker|SteelCableStrandRenderer`, packet `SteelCableStrandPacket`).
This lets you **detach wagons in real time**.
- Change `allsable/SableTrainSpawner.addCar` / assembly so cars are independent sub-levels by default; build coupling
  from the steel-cable/rope connections (constraints — `RESEARCH_sable_aeronautics.md` §H.4: Fixed/Generic/Rotary, or
  a rope `RopePhysicsObject`). Removing the cable decouples live.
- Steel cable is built on Aeronautics' rope strand system (the Rope\* mixins in #1) — verify those work post-crash-fix.

### #6 — Free movement on rail + external forces (propellers/thrusters), bearing axle still drives
- The train/cars should be free physics bodies **held to the rail** but pushable by external forces (propellers,
  thrusters), while the **Bearing Axle still sets drive speed**. This is motion model **#4 (force bogeys)** in
  `RESEARCH_sable_aeronautics.md` §N/§G — a stiff spring pulls each bogey to its rail point (high lateral stiffness so
  it can't slide off, but can derail under extreme force) + drive force along the tangent from axle RPM. A `physics`
  mode already exists (`SableTrain(physics=true)`, `SableTrainDriver.applyBogeySprings`, K=150/C=25) — extend/tune it,
  and let propeller/thruster forces add in naturally (they're already Sable forces on the body).

### #7 — Weight-based acceleration/deceleration (CONFIG feature)
- Replace `SableTrain`'s fixed `accel=0.01` with the mass-based model from **`RESEARCH_CreateTrainPhysics.md`**:
  `a = tractiveForce / mass / (20*20)`, tractive-effort cap `0.4*mass*9.81`, plus gravity-on-incline from the
  leading/trailing bogey Y delta. Mass already summed in `SableTrainDriver.updateAxleMass` (sub-level mass tracker).
- Expose as **config** in `Config.java` (toggle + friction/rolling/tractive constants). Lycoris wants it as a config
  feature: "more weight → longer to brake".

## 3. KEY FILES (our mod, `src/main/java/com/lycoris/loconautics/`)
- All-Sable train: `allsable/{SableTrain, SableTrainDriver, SableTrainSpawner, RailCarriage, RailFollower, RailPose,
  SableTrainRegistry, SableTrainPersistence, SableTrainStore, RailDebug}`. Commands `/loconautics sabletrain …`.
- Propulsion blocks: `content/{bearingaxle, transmission, analogcontroller, steelcable}/`.
- Mixins registered in `src/main/resources/loconautics.mixins.json` (`defaultRequire:1` → a failing required mixin
  crashes the game). Client/common split there.
- Setup: `Loconautics.java` (commonSetup registers drivers), `LoconauticsClient.java`, `Config.java`,
  `registry/LoconauticsRegistries.java`.
- Reference sources (decompiled, gitignored): `src/depend/{Sable, Aeronautics, Create}`. CreateTrainPhysics clone:
  `C:\Users\User\Desktop\CreateTrainPhysics-ref` (source, for formulas).

## 4. PROPULSION CHAIN (so you don't break it)
Generators → **Transmission** (gated by the **Analog Controller**'s redstone-link signal 1..15) → shaft → **Bearing
Axle** RPM → train speed (`target = rpm/256 * maxSpeed`, sign = direction). Only the Bearing Axle is read by
`SableTrainDriver.applyPropulsion`. Open question (still): do Create kinetic/redstone networks tick INSIDE a Sable
sub-level? The `[sabletrain] diag axleRPM=…/subMass=…/axleTrainMass=…` line answers it. See `RESEARCH_sable_aeronautics.md` §Y.
