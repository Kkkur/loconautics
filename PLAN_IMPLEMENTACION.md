# Loconautics — Especificación de Implementación (uso interno de Claude)

> Documento técnico maestro. Contiene TODOS los hallazgos de API validados contra los `.java` reales en `src/depend/`. Ningún detalle debe perderse. Cuando una firma no esté 100% confirmada, se marca `⚠️ VERIFICAR`.

---

## 0. Contexto y decisiones

- Mod ID `loconautics`, package raíz `com.lycoris.loconautics`.
- NeoForge 1.21.1, Java 21, Create 6.0.10-280, Sable 1.2.2, Aeronautics bundled 1.2.1.
- **Opción B (física aparente)**: Create calcula el movimiento por raíles; cada tick movemos el `ServerSubLevel` de Sable a la pose que Create calculó. Física real → diferida.
- **Botón** en `AssemblyScreen` (antes de ensamblar).
- **Un `ServerSubLevel` por carriage.**
- **Camino A**: interceptar antes de `removeBlocksFromWorld`, capturar bloques del mundo por grupo (carriage), crear sub-levels desde ahí.

---

## 1. Dependencias (build.gradle)

JARs en `run/mods/` (decisión de Lycoris; gitignored por copyright). NeoForge los carga solos
en runtime desde esa carpeta, y se referencian como `compileOnly` para compilar:
- `create-aeronautics-bundled-1.21.1-1.2.1.jar`
- `sable-neoforge-1.21.1-1.2.2.jar`

Estrategia Gradle (NeoForge ModDevGradle 2.0.140) — estado real en build.gradle:
```gradle
dependencies {
    // Create (template)
    implementation("com.simibubi.create:create-${minecraft_version}:${create_version}:slim") { transitive = false }
    implementation("net.createmod.ponder:ponder-neoforge:${ponder_version}+mc${minecraft_version}")
    compileOnly("dev.engine-room.flywheel:flywheel-neoforge-api-${minecraft_version}:${flywheel_version}")
    runtimeOnly("dev.engine-room.flywheel:flywheel-neoforge-${minecraft_version}:${flywheel_version}")
    implementation("com.tterrag.registrate:Registrate:${registrate_version}")

    // Sable + Aeronautics desde JAR locales en run/mods (cargados como mods en runtime)
    compileOnly files('run/mods/sable-neoforge-1.21.1-1.2.2.jar')
    compileOnly files('run/mods/create-aeronautics-bundled-1.21.1-1.2.1.jar')
}
```
`neoforge.mods.toml` (en `src/main/templates/META-INF/`) ya declara `create`, `sable`, `aeronautics` como `required`. **⚠️ VERIFICAR** modId real de Aeronautics — puede ser `aeronautics`, `simulated` o `create_aeronautics`. Revisar el `neoforge.mods.toml` dentro del JAR bundled.

---

## 2. HALLAZGOS DE API (validados contra src/depend)

### 2.1 Create — Estación y ensamblado

**`com.simibubi.create.content.trains.station.StationBlockEntity`** (993 líneas leídas)
- `public void assemble(UUID playerUUID)` — **EL método clave**. Flujo:
  - L688-776: calcula `TravellingPoint`s desde los bogeys detectados.
  - L781-826: bucle por bogey → crea `CarriageContraption contraption = new CarriageContraption(assemblyDirection)`, llama `contraption.assemble(this.level, bogeyPos)` (descubre bloques, **siguen en el mundo**), crea `CarriageBogey` y `Carriage`, los acumula en `contraptions` y `carriages`.
  - **L831-834: ⬅️ VENTANA CAMINO A**
    ```java
    for (CarriageContraption contraption : contraptions) {
        contraption.removeBlocksFromWorld(this.level, BlockPos.ZERO); // ← aquí saca del mundo
        contraption.expandBoundsAroundAxis(Direction.Axis.Y);
    }
    ```
  - L835: `new Train(UUID.randomUUID(), playerUUID, graph, carriages, spacing, ...)`.
  - L842-848: `carriage.setContraption(this.level, contraption)` → crea `CarriageContraptionEntity`.
  - L855-856: `Create.RAILWAYS.addTrain(train)` + `AddTrainPacket` a clientes.
- `protected int failedCarriageIndex`, `protected AssemblyException lastException` — para mostrar error en HUD.
- `Direction getAssemblyDirection()`, `boolean isAssembling()`, `int bogeyCount`, `int[] bogeyLocations`, `AbstractBogeyBlock<?>[] bogeyTypes`, `boolean[] upsideDownBogeys`.
- `private void exception(AssemblyException, int carriage)` — fija el error y `sendData()`.
- `@Nullable GlobalStation getStation()`.

**`AssemblyScreen extends AbstractStationScreen`** (UI de ensamblaje)
- `protected void init()` — crea los botones. Posiciones:
  - `toggleAssemblyButton = new WideIconButton(x+94, by, AllGuiTextures.I_ASSEMBLE_TRAIN)` con `by = guiTop + background.getHeight() - 24`.
  - `quitAssembly = new IconButton(x+73, by, AllIcons.I_DISABLE)`.
  - Callback de ensamblar: `CatnipServices.NETWORK.sendToServer(StationEditPacket.tryAssemble(blockEntity.getBlockPos()))`.
  - **Nuestro botón** → inyectar en `@Inject(method="init", at=@At("TAIL"))`, un `WideIconButton` nuevo en p.ej. `x+52, by` (a la izquierda de quitAssembly), con callback que envía nuestro `LoconauticsAssemblePacket(blockEntity.getBlockPos())`.
- `protected void init()` accede a `this.blockEntity` (heredado de `AbstractStationScreen`) y `this.station`.
- ⚠️ `tick()` y `tickTrainDisplay()` **reasignan el callback del `toggleAssemblyButton` cada tick** — nuestro botón es independiente, así que no le afecta. Pero OJO: cuando aparece un `Train`, `tick()` cambia a `StationScreen`. Nuestro botón solo importa en modo ensamblaje sin tren aún.

**`AbstractStationScreen`** — base. Campos `blockEntity` (StationBlockEntity), `station` (GlobalStation), `displayedTrain` (WeakReference<Train>). **Necesitamos `@Accessor`** si esos campos no son `protected`/accesibles. ⚠️ VERIFICAR visibilidad (en `AssemblyScreen` se usan como `this.blockEntity`, `this.station` directamente → son al menos `protected`, accesibles desde subclase, **pero un Mixin a `AssemblyScreen` los hereda**, así que probablemente no necesitamos accessor).

**`StationEditPacket extends BlockEntityConfigurationPacket<StationBlockEntity>`**
- Factories: `tryAssemble(BlockPos)`, `tryDisassemble(BlockPos)`, `configure(BlockPos, boolean, String, DoorControl)`.
- En `applySettings(player, be)`: si flag assemble → `be.assemble(player.getUUID())`.
- Modelo a imitar para nuestro packet, pero usaremos networking nativo NeoForge (no el de Create) para no acoplarnos.

### 2.2 Create — Tren y carriage (cómo siguen los raíles)

**`com.simibubi.create.content.trains.entity.Train`**
- Campos públicos clave: `double speed`, `double targetSpeed`, `UUID id`, `TrackGraph graph`, `Navigation navigation`, `List<Carriage> carriages`, `List<Integer> carriageSpacing`, `boolean derailed`, `UUID currentStation`.
- `public void tick(Level level)` — L289: mueve el tren. Por cada carriage llama `carriage.travel(level, graph, distance + stress, toFollowForward, toFollowBackward, carriageType)`. **Este es el movimiento por raíles que NO tocamos en Opción B.**
- `void earlyTick(Level)`, `boolean disassemble(Direction, BlockPos)`, `void detachFromTracks()`, `void reattachToTracks(Level)`.
- `Optional<BlockPos> getPositionInDimension(ResourceKey<Level>)`.
- Acceso al manager: `Create.RAILWAYS` (campo estático). `Create.RAILWAYS.trains` (Map<UUID,Train>), `Create.RAILWAYS.addTrain(train)`, `.removeTrain(id)`, `.sided(level).trains`.

**`Carriage`**
- `public Train train`, `public int id`, `public Couple<CarriageBogey> bogeys`, `public int bogeySpacing`, `public TrainCargoManager storage`.
- `double travel(Level, TrackGraph, double distance, TravellingPoint toFollowForward, TravellingPoint toFollowBackward, int type)` — avanza los puntos por la vía, al final llama `updateContraptionAnchors()` + `manageEntities(level)`.
- `void setContraption(Level level, CarriageContraption contraption)` — **L122**: crea el `CarriageContraptionEntity`. En Camino A querremos **NO** llamar esto (o sustituirlo) para el modo físico.
- `void updateContraptionAnchors()` — **L322**: calcula `dce.positionAnchor` y `dce.rotationAnchors` desde los bogeys/puntos. **Esta es la fuente de la pose.**
- `CarriageContraptionEntity anyAvailableEntity()`, `void forEachPresentEntity(Consumer)`.
- `TravellingPoint getLeadingPoint()`, `getTrailingPoint()`; `CarriageBogey leadingBogey()`, `trailingBogey()`; `boolean isOnTwoBogeys()`.
- `DimensionalCarriageEntity getDimensional(ResourceKey<Level>)` / `getDimensionalIfPresent(...)`.

**`Carriage.DimensionalCarriageEntity`** (clase interna) — **LA POSE VIVE AQUÍ**
- `public Vec3 positionAnchor` → posición del vagón.
- `public Couple<Vec3> rotationAnchors` → dos anclas; de su diferencia salen yaw/pitch.
- `WeakReference<CarriageContraptionEntity> entity`.
- `void alignEntity(CarriageContraptionEntity entity)` — **L884**: calcula y aplica `entity.setPos(positionAnchor)`, `entity.yaw = atan2(diffZ,diffX)*180/PI + 180`, `entity.pitch = atan2(diffY, sqrt(diffX²+diffZ²))*180/PI * -1`. **Reproducimos esta fórmula** para derivar la pose del sub-level.
- `Vec3 leadingAnchor()` / `trailingAnchor()`.

**`CarriageContraptionEntity extends OrientedContraptionEntity`**
- `public UUID trainId`, `public int carriageIndex`, `private Carriage carriage`.
- `void tick()` / `protected void tickContraption()` — **L260**: server-side sincroniza datos, client-side hace `dce.alignEntity(this)` (L321). El render del vagón sale de aquí. **Para Camino A hay que suprimir/sustituir este render** o no spawnear esta entidad.
- `Carriage getCarriage()`, `void setCarriage(Carriage)`.
- `private void bindCarriage()` — resuelve el carriage desde `Create.RAILWAYS.sided(level).trains.get(trainId)`.

**`CarriageContraption extends Contraption`** (heredar de `Contraption`)
- `boolean assemble(Level, BlockPos)` (descubre estructura), `Direction getAssemblyDirection()`.
- `BlockPos anchor` (heredado de Contraption) — ancla en el mundo.
- `Map<BlockPos, StructureTemplate.StructureBlockInfo> getBlocks()` (heredado) — posiciones **locales** relativas al anchor.
- `BlockPos getSecondBogeyPos()`, `void removeBlocksFromWorld(Level, BlockPos)`, `void expandBoundsAroundAxis(Axis)`.

### 2.3 Sable — Sub-levels y física

**`dev.ryanhcode.sable.api.SubLevelAssemblyHelper`** (clase, 447 líneas leídas)
- `public static ServerSubLevel assembleBlocks(ServerLevel level, BlockPos anchor, Iterable<BlockPos> blocks, BoundingBox3ic bounds)` — **EL PUNTO DE ENTRADA**.
  - Internamente: `SubLevelContainer.getContainer(level)` → `container.allocateNewSubLevel(pose)` → mueve bloques del mundo al plot → calcula centro de masa → `pipeline.teleport(...)`.
  - **Importante: este método SACA los bloques del mundo** (los pone a AIR en el origen). En Camino A esto entra en conflicto con `contraption.removeBlocksFromWorld` de Create sobre los mismos bloques. Resolución → §4.
- `static GatherResult gatherConnectedBlocks(BlockPos origin, ServerLevel, int max, @Nullable FrontierPredicate)` — flood-fill (no lo usaremos; Create ya nos da los grupos).
- `static class AssemblyTransform(BlockPos anchorPos, BlockPos resultingAnchorPos, int angle, Rotation rotation, ServerLevel resultingLevel)` con `apply(Vec3/BlockPos/BlockState)`.
- `static void moveBlocks(ServerLevel, AssemblyTransform, Iterable<BlockPos>)` — primitiva de movimiento de bloques (la usa Aeronautics al desensamblar).
- `record GatherResult(Set<BlockPos> blocks, int checkedBlocks, BoundingBox3i boundingBox, State state)`.
- Tipos de `dev.ryanhcode.sable.companion.math`: `BoundingBox3i`, `BoundingBox3ic`, `Pose3d`, `Pose3dc`, `JOMLConversion`. JOML: `Vector3d/dc`, `Quaterniond/dc`.

**`dev.ryanhcode.sable.api.physics.PhysicsPipeline`** (interfaz)
- `void add(ServerSubLevel, Pose3dc)` / `void remove(ServerSubLevel)`.
- `void add(KinematicContraption)` / `void remove(KinematicContraption)` — **vía Opción B**.
- `void teleport(PhysicsPipelineBody body, Vector3dc pos, Quaterniondc orient)` — **mover el sub-level cada tick** (`ServerSubLevel implements PhysicsPipelineBody`).
- `void applyImpulse(...)`, `addLinearAndAngularVelocity(...)`, `getLinearVelocity/getAngularVelocity`, `void wakeUp(body)`.
- `<T extends PhysicsConstraintHandle> T addConstraint(ServerSubLevel a, ServerSubLevel b, PhysicsConstraintConfiguration<T>)` — para física real futura.
- `Pose3d readPose(ServerSubLevel, Pose3d)`.

**`dev.ryanhcode.sable.api.sublevel.KinematicContraption`** (interfaz) — **para Opción B**
- `void sable$getLocalBounds(BoundingBox3i)`
- `BlockGetter sable$blockGetter()`
- `MassTracker sable$getMassTracker()`
- `Vector3dc sable$getPosition(double partialTick)` ← **devolvemos la posición del carriage**
- `Quaterniond sable$getOrientation(double partialTick)` ← **devolvemos yaw/pitch como quaternion**
- `Map<BlockPos, BlockSubLevelLiftProvider.LiftProviderContext> sable$liftProviders()`
- `FloatingClusterContainer sable$getFloatingClusterContainer()`
- `boolean sable$shouldCollide()`
- `boolean sable$isValid()` ← `false` si el tren ya no existe
- default `sable$getLocalPose(Pose3d dest, double partialTick)` — usa los anteriores.

**`SubLevelContainer`** / **`ServerSubLevelContainer`**
- `static ServerSubLevelContainer getContainer(Level/ServerLevel)` ⚠️ VERIFICAR firma exacta (en el helper se llama `SubLevelContainer.getContainer(level)`).
- `ServerSubLevel allocateNewSubLevel(Pose3d)`, `allocateSubLevel(UUID,int,int,Pose3d)`, `getSubLevel(UUID)`, `getAllSubLevels()`, `addObserver(SubLevelObserver)`, `physicsSystem()`.

**`ServerSubLevel extends SubLevel implements PhysicsPipelineBody`**
- `Pose3d logicalPose()`, `Pose3dc lastPose()`, `ServerLevel getLevel()`, `ServerLevelPlot getPlot()`, `UUID getUniqueId()`, `MassTracker getMassTracker()`, `void updateLastPose()`.
- Plot: `plot.getCenterChunk()`, `plot.getCenterBlock()`, `plot.newEmptyChunk(chunkPos)`, `plot.getBoundingBox()`.

**`SubLevelObserver`** (interfaz) — callbacks de creación/eliminación de sub-levels. Implementar para limpiar el registro cuando un sub-level se destruye.

**Eventos** (`dev.ryanhcode.sable.api.event.*`)
- `SableSubLevelContainerReadyEvent` — registrar observers/plugins.
- `SablePrePhysicsTickEvent`, `SablePostPhysicsTickEvent` — ganchos de tick. ⚠️ VERIFICAR si son eventos del bus de NeoForge o un bus propio de Sable.

### 2.4 Aeronautics — patrón de referencia

**`dev.simulated_team.simulated.util.SimAssemblyHelper`** (referencia, NO API pública)
- `assembleFromSingleBlock(...)` muestra el patrón: gather blocks → `BoundingBox3i bounds` → `SubLevelAssemblyHelper.assembleBlocks(level, anchor, blocks, bounds)` → `AssemblyResult(SubLevel, BlockPos offset)`.
- `disassembleSubLevel(level, subLevel, anchor, goal, rotation, playSound)` — patrón de desensamblado (usa `moveBlocks` + mueve entidades).
- `disassembleAndAddCreateContraptions(...)` — cómo integran con `ControlledContraptionEntity` de Create. Útil de referencia, pero los trenes usan `CarriageContraptionEntity` (caso distinto).
- **No hay puente público Contraption→SubLevel.** Lo construimos nosotros.

---

## 3. ARQUITECTURA — Estructura de paquetes

```
com.lycoris.loconautics
├── Loconautics.java                 (@Mod entry, reescribir)
├── LoconauticsClient.java           (@Mod CLIENT entry, reescribir)
├── Config.java                      (limpiar)
├── core/
│   ├── LoconauticsConstants.java
│   └── PhysicsTrainTag.java         (POJO: trainId, List<UUID> subLevelIds, ...)
├── registry/
│   └── LoconauticsRegistries.java   (stub para futuros bloques/items)
├── network/
│   ├── LoconauticsNetwork.java      (RegisterPayloadHandlersEvent)
│   ├── AssembleAsPhysicsTrainPacket.java   (C2S)
│   └── PhysicsTrainSyncPacket.java          (S2C)
├── server/
│   ├── PhysicsTrainRegistry.java    (Map<UUID,PhysicsTrainTag>, SavedData)
│   ├── assembly/
│   │   ├── PhysicsAssemblyOrchestrator.java
│   │   ├── SubLevelBridge.java
│   │   ├── PhysicsTrainDisassembler.java
│   │   └── AssemblyExceptionTranslator.java
│   ├── physics/
│   │   ├── TrainKinematicContraption.java   (implements KinematicContraption)
│   │   ├── SableEventBridge.java
│   │   └── PhysicsTrainObserver.java        (implements SubLevelObserver)
│   └── tick/
│       └── PhysicsTrainTickHandler.java
├── client/
│   ├── ClientPhysicsTrainRegistry.java
│   ├── LoconauticsClientEvents.java
│   └── screen/
│       └── SableModeButton.java
└── mixin/
    ├── StationBlockEntityAssembleMixin.java   (Camino A, server)
    └── client/
        ├── AssemblyScreenMixin.java           (el botón)
        └── AbstractStationScreenAccessor.java (si hace falta)
```

`loconautics.mixins.json`:
```json
{
  "mixins": ["StationBlockEntityAssembleMixin"],
  "client": ["client.AssemblyScreenMixin", "client.AbstractStationScreenAccessor"]
}
```

---

## 4. CAMINO A — Resolución del conflicto de bloques

**Problema:** `SubLevelAssemblyHelper.assembleBlocks` y `CarriageContraption.removeBlocksFromWorld` ambos sacan los mismos bloques del mundo. No pueden ejecutarse ambos sobre el mismo grupo.

**Estrategia elegida (a prototipar en Fase 3):**
1. Mixin en `StationBlockEntity.assemble`, **`@Inject` justo antes de `removeBlocksFromWorld`** (L831-832), `cancellable` o `@Redirect` del `removeBlocksFromWorld`.
2. Solo actuar si la estación está marcada como "modo físico" (flag puesto por nuestro packet antes de llamar a `assemble`).
3. Para cada `CarriageContraption`:
   - Reconstruir las **posiciones mundiales** de sus bloques: `worldPos = contraption.anchor + localPos` para cada `localPos` en `contraption.getBlocks().keySet()`. ⚠️ VERIFICAR cómo `CarriageContraption` mapea local↔world (revisar `Contraption` y `removeBlocksFromWorld`).
   - Calcular `BoundingBox3i bounds` desde esas posiciones (`BoundingBox3i.from(blocks)` existe en companion).
   - Llamar `SubLevelBridge.create(level, anchor, worldBlocks, bounds)` → `ServerSubLevel`.
4. **Saltar** el `removeBlocksFromWorld` de Create para ese contraption (Sable ya los quitó), pero **dejar** que Create cree el `Carriage`/`Train` lógicos (necesarios para la matemática de raíles).
5. El `CarriageContraptionEntity` que crea `carriage.setContraption`:
   - Opción 5a: dejar que se cree pero **suprimir su render** (mixin client que lo oculta si su train está en modo físico).
   - Opción 5b: no llamar `setContraption` y gestionar nosotros la entidad. Más invasivo.
   - **Decisión preliminar: 5a** (menos invasivo). Verificar en runtime que no se ven bloques duplicados.
6. Registrar `PhysicsTrainTag(trainId, [subLevelId por carriage])` en `PhysicsTrainRegistry`.
7. Enviar `PhysicsTrainSyncPacket` a clientes en la dimensión.

**Driver de movimiento (Fase 4):**
- `PhysicsTrainTickHandler` escucha `ServerTickEvent.Pre`.
- Por cada `PhysicsTrainTag` → por cada carriage:
  - Leer pose: `dce.positionAnchor` + derivar quaternion de `dce.rotationAnchors` (fórmula de `alignEntity`).
  - `pipeline.teleport(subLevel, toVector3d(positionAnchor), toQuaterniond(yaw,pitch))`.
  - Alternativa más limpia: registrar `TrainKinematicContraption` como `KinematicContraption` y que Sable lea la pose vía `sable$getPosition/sable$getOrientation`. **Preferir esta si funciona.**

---

## 5. ORDEN DE IMPLEMENTACIÓN (5 fases)

### Fase 0 — Limpieza + andamio `🟢`
1. `LoconauticsConstants` (MODID, logger, IDs).
2. `Config` — limpiar; opciones: `enableSableMode`, `physicsTrainMaxSpeed`, `debugRender`.
3. `Loconautics` — borrar EXAMPLE_*, registrar network + handlers.
4. `LoconauticsClient` — limpiar.
5. `LoconauticsRegistries` — stub.
6. `build.gradle` — añadir `compileOnly(files(...))` de los JAR; resolver runtime classpath.
7. `loconautics.mixins.json` — secciones `mixins`/`client` vacías por ahora.
8. `en_us.json` — strings base.
9. **Hito: `./gradlew build` compila.**

### Fase 1 — Datos + red `🟢`
10. `PhysicsTrainTag`.
11. `PhysicsTrainRegistry` (server, SavedData por dimensión; limpiar huérfanos en `LevelEvent.Load`).
12. `ClientPhysicsTrainRegistry`.
13. `LoconauticsNetwork` (canal `loconautics:main` v1, `RegisterPayloadHandlersEvent`).
14. `AssembleAsPhysicsTrainPacket` (C2S, lleva `BlockPos stationPos`).
15. `PhysicsTrainSyncPacket` (S2C, lleva `UUID trainId` + lista de `UUID subLevelId`).

### Fase 2 — El botón `🟡`
16. `SableModeButton` (extiende `WideIconButton` de Create o `Button` vanilla con textura propia).
17. `AbstractStationScreenAccessor` (solo si los campos no son accesibles; probablemente innecesario al mixinear `AssemblyScreen`).
18. `AssemblyScreenMixin` (`@Inject` TAIL de `init()`, añadir botón en `x+52, by`; `onPress` → enviar `AssembleAsPhysicsTrainPacket`).
19. **Hito: el botón aparece y el packet llega al servidor (log).**

### Fase 3 — Bridge Create→Sable `🔴`
20. `SubLevelBridge.create(ServerLevel, BlockPos anchor, Iterable<BlockPos> worldBlocks, BoundingBox3ic bounds) → ServerSubLevel` (envuelve `SubLevelAssemblyHelper.assembleBlocks`).
21. `StationBlockEntityAssembleMixin` — Camino A (§4): capturar grupos, crear sub-levels, saltar `removeBlocksFromWorld`, solo si flag físico.
22. `TrainKinematicContraption` (implements `KinematicContraption`).
23. `SableEventBridge` (registra observer en `SableSubLevelContainerReadyEvent`).
24. `PhysicsTrainObserver` (implements `SubLevelObserver`, limpia registro).
25. `PhysicsAssemblyOrchestrator` (coordina el flujo, fija el flag físico antes de `assemble`).
26. `AssemblyExceptionTranslator` (errores → `AssemblyException` de Create para el HUD).
27. **Hito: ensamblar crea N sub-levels (logs de Sable).**

### Fase 4 — Driver cinemático `🟡`
28. `PhysicsTrainTickHandler` (`ServerTickEvent.Pre` → `teleport`/KinematicContraption por carriage).
29. Mixin client para suprimir render del `CarriageContraptionEntity` físico (Opción 5a).
30. `PhysicsTrainSyncPacket` handler en cliente → `ClientPhysicsTrainRegistry`.
31. `PhysicsTrainDisassembler` (deshacer: `moveBlocks` de vuelta + limpiar registro + `Train.disassemble`).
32. **Hito: el tren físico se mueve por los raíles.**

### Fase 5 (diferida) — Física real
- `addConstraint` de la pipeline, fuerzas, descarrile real. Solo cuando Opción B sea estable.

---

## 6. RIESGOS Y PUNTOS A VERIFICAR EN RUNTIME

1. **Render duplicado** (Opción 5a): confirmar que ocultar el `CarriageContraptionEntity` no rompe el HUD/asientos/acoplamiento. Plan B: 5b.
2. **local↔world en CarriageContraption**: confirmar el mapeo exacto al reconstruir posiciones mundiales (revisar `Contraption.removeBlocksFromWorld` y `anchor`).
3. **`SableSubLevelContainerReadyEvent`**: ¿bus NeoForge o bus propio de Sable? Afecta `@SubscribeEvent` vs registro manual.
4. **`SubLevelContainer.getContainer` firma exacta** (Level vs ServerLevel; null-safety).
5. **Runtime classpath de los JAR**: ModDevGradle puede requerir copiarlos a `run/mods` o usar `additionalRuntimeClasspath`. Verificar que cargan en `runClient`.
6. **modId de Aeronautics** en `neoforge.mods.toml` (mirar dentro del JAR bundled).
7. **Convivencia con `Train.tick()`**: en Opción B NO cancelamos el tick (Create mueve por raíles). Solo leemos la pose. Confirmar que `manageEntities`/`alignEntity` no pelean con nuestro `teleport`.
8. **Multi-dimensión**: el carriage puede tener varias `DimensionalCarriageEntity`. Para v1 asumir 1 dimensión; manejar `getDimensionalIfPresent(level.dimension())`.

---

## 7. Archivos de referencia en src/depend (mapa rápido)

- Ensamblado: `Create/StationBlockEntity.java` (assemble L688), `Create/AssemblyScreen.java`, `Create/AbstractStationScreen.java`, `Create/StationEditPacket.java`, `Create/CarriageContraption.java`.
- Tren: `Create/Train.java`, `Create/Carriage.java`, `Create/CarriageContraptionEntity.java`, `Create/CarriageBogey.java`, `Create/TravellingPoint.java`, `Create/Navigation.java`.
- Vías: `Create/TrackGraph.java`, `Create/TrackEdge.java`, `Create/TrackNode.java`, `Create/TrackNodeLocation.java`.
- Sable: `Sable/SubLevelAssemblyHelper.java`, `Sable/PhysicsPipeline.java`, `Sable/KinematicContraption.java`, `Sable/ServerSubLevel.java`, `Sable/SubLevelContainer.java`, `Sable/ServerSubLevelContainer.java`, `Sable/SubLevelObserver.java`, `Sable/event/Sable*PhysicsTickEvent.java`, `Sable/event/SableSubLevelContainerReadyEvent.java`, `Sable/MassTracker.java`, `Sable/*ConstraintConfiguration/Handle.java`.
- Aeronautics: `Aeronautics/SimAssemblyHelper.java`, `Aeronautics/SimAssemblyContraption.java`, `Aeronautics/SimAssemblyService.java`, `Aeronautics/SimMovementContext.java`.

---

## 8. Estado / próximo paso

- Investigación: ✅ completa para Fases 0-4.
- JARs: ✅ en `libs/`.
- **Próximo:** Fase 0 (esperando luz verde del usuario para escribir).
