# GUIDE for Fable 5 — Loconautics work queue (2026-06-10)

> **Read order:** this file → `HANDOFF_NEXT_CLAUDE.md` §0a (all-Sable state) → `RESEARCH_sable_aeronautics.md`
> (Sable/Aeronautics API) → `RESEARCH_CreateTrainPhysics.md` (weight/physics formulas). Don't re-derive what's
> already in those — this guide is the task list + pointers. Keep token use low: read the specific file/section a
> task names, not everything.

## 0. CURRENT STATE
- **2026-06-10 latest:** HEAD = `69855c3`. Lycoris/Lthiumm's branch (`controller-and-trans-fix`) is fully merged in
  (steel cable, transmission, analog, Rope mixins, + their fixes: startup crash, knot rendering, TransmissionRenderer).
  The `controller-and-trans-fix` and `transmission` branches were deleted after merging (cleanup). Bogeys pivot on
  curves ✅; persistence ✅; weight→axle ✅; analog→axle propulsion ✅; startup crash ✅ fixed.
  Active work = bogey/body **cohesion** fine-tune (#2, visual) and the **disconnected-block fall** feature (#2b).
- Branch `feature/all-sable-physics-train` (the ONLY active branch; `master` is the stable base, `backup/collision-fix-stable`
  is a safety backup). Built on the earlier merge `9f9b85f` (`controller-and-trans-fix`
  cleanly — no conflicts; his work and the persistence work touch disjoint files). **Builds OK** (`./gradlew build`).
- Merged jar **`c89df05`** deployed to the NEW test instance (see workflow).
- The merge brought in Lycoris' NEW code: **steel cable** (`content/steelcable/*`), **transmission** rework
  (`content/transmission/*`), **analog controller** rewrite (`content/analogcontroller/*`), bind-frequency GUI
  (`foundation/menu|screen/BindFrequency*`), and new **Rope\*** mixins (`mixin/Rope*`, `mixin/client/Rope*`) targeting
  Aeronautics' rope blocks (`dev.simulated_team.simulated.content.blocks.rope.*`). Build also gained an
  `extractSimulated` gradle task (new Aeronautics/Simulated source dep).

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

### #1 — Startup crash ✅ FIXED (Lycoris/Lthiumm, commit `a242df2`)
The "crash upon joining a world" was the new Rope mixins (`RopeStrandHolderBehaviorAccessor`,
`RopeStrandHolderDestroyMixin`); Lthiumm fixed their selectors. Merged into all-sable. The build starts clean now
(`[sabletrain] persistence active` confirms our code loaded). Nothing to do here.

### #2 — Loose pivoting bogeys ✅ DONE (pivoting); 🟡 cohesion fine-tuning remains (visual)
**DONE (commit `6013cc7`, confirmed in-game 2026-06-10):** Implemented the **multi-body** approach (option B, the
user picked it): at spawn each car is split into a **BODY** sub-level (cart minus the bogey blocks) + **one sub-level
per Create bogey block**. Each bogey rides its own point on the rail (`RailCarriage` spacing 1) and is pinned to the
local tangent → **bogeys pivot independently on curves, like Create** (✅ user-confirmed "ahora pivotan y giran solos").
Works for N bogeys automatically. The body is posed FROM the bogeys (midpoint of the two end bogeys, oriented to the
chord between them via `RailCarriage.orientationTo`) so it hangs on them — no independent drift. Code: `SableTrainSpawner
.buildCar` (split + sort bogeys front-to-back), `SableTrain.Bogey`/`Car.bogeys()`, `SableTrainDriver.pinCar`/`bodyPose`,
persistence handles per-bogey sub-levels, `clear()` removes them.

**🟡 REMAINING — cohesion (needs your eyes, cheap to iterate):** the user wants body+bogeys to look like ONE row of
blocks (the body a single rigid row that turns only "a little"; bogeys pivot under it), NOT separated. Two things to
tune **visually**:
1. **Vertical alignment / gaps.** The body has holes where the bogey blocks were removed, and the body vs bogeys may
   sit a touch off in Y. Tune so the body's bottom sits flush on the bogeys. Likely a small Y offset in
   `SableTrainDriver.pin`/`targetPosition` for the body, or pin each bogey at its captured height above the rail
   (store the bogey block's `Y - railY` at spawn and add it to the bogey's target Y) so it fills its hole.
2. **Orphan cleanup.** Old test spawns leave un-driven orphan sub-levels (the "floating chests"). `/loconautics
   sabletrain clear` now removes a train's bogeys too; tell the user to clear before re-testing.
Iterate with screenshots (straight + curve). The architecture is right; this is pure visual tuning.

### #2b — Disconnected blocks should become physical and FALL (NEW, from Lycoris 2026-06-10)
**Requirement:** if the player builds off the train and breaks the connecting block, any block(s) no longer in contact
(adjacent OR diagonal) with the train's core should drop out and fall to the ground, like a physics mod.
**Finding (researched):** **Sable does NOT auto-split on block break.** Its only split (`ClientboundRecentlySplitSubLevelPacket`
/ `setSplitFrom`) fires at ASSEMBLY time (kick-from-containing), never by connectivity on break. Its `FloatingBlockController`
is buoyancy (`FloatingBlockMaterial` lift), unrelated. So this must be IMPLEMENTED:
- Hook block-break/`onBlockChange` in our train BODY sub-levels.
- Flood-fill connectivity (26-neighbour, incl. diagonals) from a core anchor (e.g. the Bearing Axle, or the largest
  cluster). Any block NOT reached = disconnected.
- Eject the disconnected blocks: project to world (`Sable.HELPER.projectOutOfSubLevel(level, localPoint, dst)`),
  remove from the sub-level, and place them in the world / spawn a `FallingBlockEntity` so they fall. (Simplest first
  cut = drop as world blocks/items at the projected position.)
- Don't pin the ejected blocks (they're no longer train cars → they fall freely).

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
