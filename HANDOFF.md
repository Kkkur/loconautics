# Loconautics — HANDOFF completo (para continuar en una nueva conversación)

> Este documento contiene TODO el estado del proyecto: objetivo, arquitectura, hallazgos de API,
> problemas resueltos, qué se ha probado, limitaciones y próximos pasos. Está pensado para que un
> Claude (u otra persona) pueda continuar sin perder NADA de contexto.
>
> **Léelo entero antes de tocar código.** Complementa a `PLAN_IMPLEMENTACION.md` (spec técnica
> original) y `PLAN_EQUIPO.md` (reparto humano). Este HANDOFF es la fuente de verdad más reciente.

---

## 0. Qué es Loconautics

Addon de Minecraft (**NeoForge 1.21.1**) que convierte los **trenes de Create** en **objetos físicos
de Sable** (sub-levels) que **se mueven por las vías de Create**. El jugador pulsa un botón en la
pantalla de ensamblaje de la estación y, en vez de ensamblar el tren como una contraption normal de
Create, se ensambla como un sub-level físico de Sable que sigue los raíles.

- **Repo:** https://github.com/Kkkur/loconautics (rama `master`, historia lineal)
- **Autores:** Lycoris, MDLOP (MonkeyDLuffyOP). Trabajan en paralelo; merges sobre `master`.
- **Carpeta local (PC de Lycoris):** `C:\Users\User\Desktop\Mod Aeronautics`
- **Carpeta local (PC de MDLOP):** `C:\Users\WinterOS\Desktop\loconautics`

### Decisiones de diseño (cerradas)
1. **Física tipo B (cinemática/determinista):** el tren *parece* físico (es un sub-level real de
   Sable, con colisión propia) pero su movimiento lo calcula Create (raíles, señales, curvas). Cada
   tick movemos el sub-level a la pose que Create calcula. La física "real" (rebotes, descarrile) se
   deja para el futuro.
2. **Botón en `AssemblyScreen`** (pantalla de ensamblaje), antes de ensamblar.
3. **Un sub-level de Sable por cada carriage (vagón).**
4. **"Camino A":** interceptamos justo antes de que Create saque los bloques del mundo, capturamos
   los bloques por grupos (un grupo por vagón) y creamos los sub-levels desde ahí.

---

## 1. Entorno, dependencias y versiones EXACTAS

| Cosa | Valor |
|------|-------|
| Loader | NeoForge `21.1.219`, Minecraft `1.21.1`, Java 21 |
| Create | `6.0.10-280` (maven `com.simibubi.create:create-1.21.1:6.0.10-280:slim`, `transitive=false`) |
| Sable | `1.2.2` — **JAR local** en `run/mods/sable-neoforge-1.21.1-1.2.2.jar` |
| Aeronautics | `create-aeronautics-bundled-1.21.1-1.2.1.jar` — **JAR local** en `run/mods/` |
| sable-companion | `1.6.0` — va dentro del JAR de Sable vía **jarjar**, se extrae solo (ver build) |
| Flywheel | `1.0.6` (api + full jar como compileOnly/runtimeOnly) |
| Ponder | `1.0.82`, Registrate `MC1.21-1.3.0+67` |
| Parchment | `2024.11.17` |

**Repos maven:** `maven.createmod.net`, `maven.ithundxr.dev/snapshots`.

**JARs con copyright** (Sable, Aeronautics) → en `run/mods/`, **gitignored**. Cada dev los coloca
ahí manualmente (los saca de su instancia de CurseForge). Sin ellos el build falla.

**`deps/`** (gitignored): contiene `sable-companion-common-1.21.1-1.6.0.jar`, que la tarea Gradle
`extractSableCompanion` saca automáticamente del jarjar de Sable. `compileJava` depende de esa
tarea, así que basta tener el JAR de Sable en `run/mods/` y hacer `./gradlew build`.

### build.gradle — puntos clave (ya configurado)
```gradle
compileOnly("dev.engine-room.flywheel:flywheel-neoforge-api-1.21.1:1.0.6")
compileOnly("dev.engine-room.flywheel:flywheel-neoforge-1.21.1:1.0.6")   // full jar para mixin de render (no usado ahora, inofensivo)
runtimeOnly("dev.engine-room.flywheel:flywheel-neoforge-1.21.1:1.0.6")
compileOnly files('run/mods/sable-neoforge-1.21.1-1.2.2.jar')
compileOnly files('run/mods/create-aeronautics-bundled-1.21.1-1.2.1.jar')
compileOnly files('deps/sable-companion-common-1.21.1-1.6.0.jar')
// tarea extractSableCompanion (Copy) -> extrae el companion del jarjar de Sable a deps/
```

---

## 2. Workflow de desarrollo (IMPORTANTE)

- **Compilar:** `./gradlew build` (o `compileJava`). El build NO valida que los mixins se apliquen
  en runtime — solo compila. Los fallos de mixin salen al arrancar el juego.
- **Probar:** se copia el jar a la instancia real de CurseForge que tiene TODO el modpack:
  ```
  cp build/libs/loconautics-1.0.0.jar "/c/Users/User/curseforge/minecraft/Instances/Skybound SMP/mods/"
  ```
  Luego el usuario reinicia el juego. (Es un modpack ENORME con Sinytra Connector, Flywheel, Sodium,
  Iris, Veil, Create Threaded Trains, Steam'n'Rails, etc.)
- **Logs:** `C:\Users\User\curseforge\minecraft\Instances\Skybound SMP\logs\latest.log`
  (servidor integrado = cliente y servidor en el mismo log). **Claude PUEDE leer este log
  directamente** (no hace falta que el usuario copie/pegue).
- **Crash reports:** `...\Skybound SMP\crash-reports\`.
- **Técnica clave — `javap` para verificar firmas/targets de mixin sin adivinar:**
  ```bash
  JAVAP="/c/Program Files/Java/jdk-21/bin/javap.exe"   # javap NO está en PATH
  # jar de Create en el cache de gradle:
  # ~/.gradle/caches/modules-2/files-2.1/com.simibubi.create/create-1.21.1/6.0.10-280/<hash>/create-1.21.1-6.0.10-280-slim.jar
  # jar de Sable/Flywheel: en run/mods o ~/.gradle/caches
  # unzip la .class a /tmp y: "$JAVAP" -p -c -classpath . <fqcn>
  ```
  Esto se usó para confirmar OWNERS exactos de invocaciones (p.ej. el `@Redirect` de
  `removeBlocksFromWorld` y `disassemble()`), firmas de `blit`, dimensiones de botones, etc.
- **Git:** SSH configurado (clave ed25519 en `~/.ssh/id_ed25519`, ya añadida a GitHub). Push directo
  a `master` autorizado. **El usuario pide hacer commit/push SOLO cuando lo diga explícitamente.**
  Flujo normal: editar → build → desplegar a la instancia → esperar feedback → cuando el usuario
  dice "sube", commit + push.
- **Archivos `.java` descompilados de referencia:** en `src/depend/Create/`, `src/depend/Sable/`,
  `src/depend/Aeronautics/` (los subió Lycoris). Son la mejor fuente para entender las APIs.

---

## 3. Arquitectura — todos los archivos y qué hacen

Paquete raíz: `com.lycoris.loconautics`.

### Núcleo / registro / red
- `Loconautics.java` — `@Mod` principal. Registra registries + config.
- `LoconauticsClient.java` — `@Mod(dist=CLIENT)`. Config screen + client setup.
- `Config.java` — `enableSableMode`, `physicsTrainMaxSpeed`, `debugRender`.
- `core/LoconauticsConstants.java` — MOD_ID="loconautics", LOGGER, `id(path)`, NETWORK_VERSION.
- `core/PhysicsTrainTag.java` — **(refactor del compañero)** datos de un tren físico:
  `record CarriageEntry(UUID subLevelId, BlockPos anchor)` + `List<CarriageEntry> carriages`.
  Guarda el **anchor** de cada carriage (el `contraption.anchor` del ensamblado). NBT (toNbt/fromNbt)
  + STREAM_CODEC (los clientes solo reciben los subLevelIds). Helper `subLevelIds()` para el driver.
- `registry/LoconauticsRegistries.java` — stub de DeferredRegisters (sin bloques/items aún).
- `network/LoconauticsNetwork.java` — registra packets en `RegisterPayloadHandlersEvent`. Helpers
  `sendTo(player, ...)`, `sendToAll(...)`.
- `network/AssembleAsPhysicsTrainPacket.java` — **C2S**. El botón lo envía con la `BlockPos` de la
  estación. Handler servidor → `PhysicsAssemblyOrchestrator.assemble(player, station)`.
- `network/PhysicsTrainSyncPacket.java` — **S2C**. Avisa a clientes de qué tren entra/sale de modo
  físico. Handler → `ClientPhysicsTrainRegistry.put/remove`.

### Servidor — ensamblado
- `server/PhysicsTrainRegistry.java` — `SavedData` global (almacenamiento del overworld). Mapea
  `trainId -> PhysicsTrainTag`. Persiste entre reinicios.
- `server/assembly/PhysicsAssemblyContext.java` — estado estático que cruza los dos puntos del
  ensamblado de Create (el redirect por-carriage + el orchestrator). `begin/isPending/addSubLevel(
  uuid, anchor)/drain()->List<CarriageEntry>/end`.
- `server/assembly/SubLevelBridge.java` — convierte un `CarriageContraption` en un `ServerSubLevel`:
  worldBlocks = `anchor.offset(local)` para cada local de `contraption.getBlocks()`, calcula bounds,
  llama `SubLevelAssemblyHelper.assembleBlocks(level, anchor, worldBlocks, bounds)`.
- `server/assembly/PhysicsAssemblyOrchestrator.java` — el flujo: si `enableSableMode`, `begin()`,
  snapshot de `Create.RAILWAYS.trains.keySet()`, llama `station.assemble(uuid)` (el mixin desvía los
  bloques a sub-levels), `drain()` las entries, encuentra el tren nuevo por diff, registra el tag,
  `sendToAll(PhysicsTrainSyncPacket)`.
- `server/assembly/PhysicsTrainDisassembler.java` — **solo limpieza de huérfanos** (red de
  seguridad del tick handler): borra sub-levels sobrantes + unregister + sync. El disassembly REAL
  del jugador lo hace `TrainDisassembleMixin` + `SubLevelDisassembler`.
- `server/assembly/SubLevelDisassembler.java` — **(nuevo)** desensambla un sub-level de vuelta al
  mundo replicando `SimAssemblyHelper.disassembleSubLevel` de Aeronautics: recoge los bloques
  ACTUALES del plot, `SubLevelAssemblyHelper.moveBlocks(level, transform, blocks)` (mueve estados +
  block-entities + inventarios), `moveTrackingPoints`, y borra el sub-level. `disassembleCarriage(
  server, subId, goal, rotation)`.

### Servidor — tick
- `server/tick/PhysicsTrainTickHandler.java` — `ServerTickEvent.Post`. Para cada `PhysicsTrainTag`:
  resuelve el `Train`, y por cada carriage lee la pose de su `CarriageContraptionEntity`
  (`entity.position()` + yaw/pitch) y `pipeline.teleport(serverSub, target, orientation)` +
  `resetVelocity`. Si el tren ya no existe → `PhysicsTrainDisassembler.disassemble` (limpieza
  huérfanos). Tiene logging `[drive]` cada 40 ticks. Métodos clave:
  - `targetPosition(...)`: `target = entityPos + rotate(rotationPoint - plotAnchor, Q)` donde
    `rotationPoint = sub.logicalPose().rotationPoint()` y `plotAnchor = sub.getPlot().getCenterBlock()`.
  - `orientationOf(...)`: `Quaterniond().rotationYXZ(toRadians(-yaw+180), toRadians(pitch), 0)`.

### Cliente
- `client/ClientPhysicsTrainRegistry.java` — espejo cliente (`ConcurrentHashMap`, se escribe en hilo
  de red y se lee en hilo de render). `isPhysicsTrain(uuid)`.
- `client/screen/SableModeButton.java` — crea un `WideIconButton` (26×18) de Create con el icono
  custom; al pulsar envía `AssembleAsPhysicsTrainPacket`.
- `client/screen/SableIcon.java` — `ScreenElement` que dibuja `assets/loconautics/textures/gui/
  sable_button.png` (24×16) con `graphics.blit(tex, x, y, 0,0, 24,16, 24,16)` (firma 1.21.1).

### Mixins (server, en `loconautics.mixins.json` → `mixins`)
- `mixin/StationBlockEntityAssembleMixin.java` — **Camino A**. `@Redirect` de
  `CarriageContraption.removeBlocksFromWorld(Level, BlockPos)` dentro de `StationBlockEntity.assemble`.
  Si pending → `SubLevelBridge.createFromContraption` + `PhysicsAssemblyContext.addSubLevel(id,
  anchor)` + salta el remove de Create (Sable ya quitó los bloques). Si no, comportamiento normal.
- `mixin/TrainDisassembleMixin.java` — `@Redirect` de `CarriageContraptionEntity.disassemble()`
  dentro de `Train.disassemble`. Para trenes físicos: `SubLevelDisassembler.disassembleCarriage(
  server, subId, goal=BlockPos.containing(entity.position()), Rotation.NONE)` + `entity.discard()`.
  Si no es físico o falla → `entity.disassemble()` normal.
- `mixin/ContraptionColliderMixin.java` — **(del compañero, MixinExtras)** `@Mixin(
  AbstractContraptionEntity)`: `canCollideWith` → false para trenes físicos; `@WrapOperation` de
  `MountedStorageManager.handlePlayerStorageInteraction` → false (bloquea acceso a cofres del vagón
  fantasma; el jugador usa el cofre del sub-level real). NO cancela todo, así los asientos de
  conductor siguen.
- `mixin/MovingInteractionBehaviourMixin.java` — **(del compañero)** `@Mixin({DeployerMovingInteraction,
  SeatInteractionBehaviour, SimpleBlockMovingInteraction})`: `@WrapMethod` de `handlePlayerInteraction`
  → suprime esas interacciones en trenes físicos.

### Mixins (client, en `loconautics.mixins.json` → `client`)
- `mixin/client/AssemblyScreenMixin.java` — extiende `AbstractStationScreen` (la superclase real),
  `@Inject` TAIL de `init()` añade el `WideIconButton` en `x+44, by` (a la izquierda de la X). Campo
  `@Unique` para el botón; `@Inject` en `tick()` actualiza `active = bogeyCount>0 || train!=null`
  (gris/activo como el botón de ensamblar de Create).
- `mixin/client/StationBlockEntityAccessor.java` — `@Accessor("bogeyCount")` (campo package-private).
- `mixin/client/ClientContraptionRenderMixin.java` — **(del compañero, AHORA REGISTRADO Y ARREGLADO)**
  `@Mixin(ClientContraption)`: `getRenderedBlocks` → `RenderedBlocks` vacío,
  `setupRenderLevelAndRenderedBlockEntities` → cancel, `getAndAdjustShouldRenderBlockEntities` →
  BitSet vacío, para trenes físicos. Esto oculta el vagón de Create en la FUENTE (bloques + block
  entities), funciona para Flywheel y modo inmediato. (Tenía un `_ ->` que no compilaba → cambiado a
  `pos ->`.)

### Recursos
- `resources/loconautics.mixins.json` — lista de mixins (server + client).
- `resources/assets/loconautics/lang/en_us.json` — strings (tooltip del botón, etc.).
- `resources/assets/loconautics/textures/gui/sable_button.png` — icono 24×16 (lo hizo Lycoris).
- `src/main/templates/META-INF/neoforge.mods.toml` — deps required: create, sable, aeronautics.

---

## 4. Hallazgos de API (verificados contra código real / javap)

### Create (`com.simibubi.create...`)
- `content.trains.station.StationBlockEntity`:
  - `public void assemble(UUID playerUUID)` — el ensamblado. Por cada bogey crea un
    `CarriageContraption`, lo `assemble()` (bloques aún en el mundo), y en el bucle llama
    `contraption.removeBlocksFromWorld(level, BlockPos.ZERO)` (← AQUÍ interceptamos) y luego crea el
    `Train` y `carriage.setContraption(...)`. `Create.RAILWAYS.addTrain(train)` + `AddTrainPacket`.
  - `bogeyCount` (package-private, accedido vía accessor), `getStation()`, `isAssembling()`.
  - **Owner exacto del call** (javap): `CarriageContraption.removeBlocksFromWorld(Level, BlockPos)`.
- `content.trains.station.AssemblyScreen extends AbstractStationScreen` — botones en `init()`:
  `toggleAssemblyButton = WideIconButton(x+94, by, AllGuiTextures.I_ASSEMBLE_TRAIN)`,
  `quitAssembly = IconButton(x+73, by, AllIcons.I_DISABLE)`, `by = guiTop + background.getHeight()-24`.
- `AbstractStationScreen extends AbstractSimiScreen` — campos `protected AllGuiTextures background;
  protected StationBlockEntity blockEntity; protected GlobalStation station; protected
  WeakReference<Train> displayedTrain;`. `guiLeft/guiTop` vienen de `AbstractSimiScreen`.
- `content.trains.entity.Train`: campos públicos `double speed; UUID id; TrackGraph graph;
  List<Carriage> carriages; boolean currentlyBackwards;`. `tick(Level)` mueve los carriages.
  `disassemble(Direction, BlockPos)` — posiciona cada `entity.setPos(...)` y llama
  `entity.disassemble()` (owner javap: `CarriageContraptionEntity.disassemble()V`), luego
  `Create.RAILWAYS.removeTrain(id)`. `Create.RAILWAYS.trains` (Map<UUID,Train>) accesible.
- `content.trains.entity.Carriage`: `anyAvailableEntity()`, `getDimensional(level)`,
  `updateContraptionAnchors()`, `setContraption(level, cc)`. `DimensionalCarriageEntity.alignEntity(
  entity)` calcula `entity.setPos(positionAnchor)`, `entity.yaw = atan2(diffZ,diffX)*180/PI + 180`,
  `entity.pitch = atan2(diffY, sqrt(...))*180/PI * -1`.
- `content.trains.entity.CarriageContraptionEntity`: `public UUID trainId; public int carriageIndex;`
  `getContraption()`, `getCarriage()`. yaw/pitch públicos.
- `content.contraptions.Contraption` (javap): `public BlockPos anchor; public Map<BlockPos,
  StructureBlockInfo> getBlocks()` (claves LOCALES), `protected BlockPos toLocalPos(BlockPos)` →
  confirma **worldPos = anchor + localPos**. `public void removeBlocksFromWorld(Level, BlockPos)`
  (declarado aquí, NO overridden en CarriageContraption). `addBlocksToWorld(Level, StructureTransform)`.
- Render: `content.contraptions.render.ClientContraption` (métodos `getRenderedBlocks`,
  `setupRenderLevelAndRenderedBlockEntities`, `getAndAdjustShouldRenderBlockEntities`),
  `ContraptionVisual` (Flywheel, `beginFrame`, campo `structure: TransformedInstance`),
  `ContraptionEntityRenderer` / `OrientedContraptionEntityRenderer` (modo inmediato, `shouldRender`).

### Sable (`dev.ryanhcode.sable...`)
- `api.SubLevelAssemblyHelper`:
  - `static ServerSubLevel assembleBlocks(ServerLevel, BlockPos anchor, Iterable<BlockPos> blocks,
    BoundingBox3ic bounds)` — MUEVE los bloques del mundo a un sub-level nuevo (pone AIR en el origen).
  - `static void moveBlocks(ServerLevel, AssemblyTransform, Iterable<BlockPos>)` — mueve bloques
    (estados + block entities con NBT/inventarios) de un sitio a otro según el transform.
  - `static void moveTrackingPoints(ServerLevel, BoundingBox3ic, ServerSubLevel|null, AssemblyTransform)`.
  - `static class AssemblyTransform(BlockPos anchorPos, BlockPos resultingAnchorPos, int angle,
    Rotation rotation, ServerLevel resultingLevel)`.
- `api.sublevel.SubLevelContainer`: `static ServerSubLevelContainer getContainer(ServerLevel)`,
  `getSubLevel(UUID)`, `removeSubLevel(SubLevel, SubLevelRemovalReason)`, `addObserver`.
- `api.sublevel.ServerSubLevelContainer`: `physicsSystem()` → `SubLevelPhysicsSystem` (→
  `getPipeline()` → `PhysicsPipeline`).
- `sublevel.ServerSubLevel extends SubLevel implements PhysicsPipelineBody`: `logicalPose()` →
  `Pose3d` (`.position()` Vector3d, `.orientation()` Quaterniond, `.rotationPoint()` Vector3d),
  `getPlot()` → `ServerLevelPlot`, `getUniqueId()`, `getMassTracker().getCenterOfMass()` (puede ser
  null justo tras ensamblar — por eso usamos `rotationPoint()`).
- `sublevel.plot.LevelPlot/ServerLevelPlot`: `getCenterBlock()`, `getBoundingBox()`,
  `getLoadedChunks()` → `PlotChunkHolder` (`getBoundingBox()` BoundingBox3ic, `getPos()` ChunkPos),
  `((ServerLevelPlot)plot).kickAllEntities()`.
- `api.physics.PhysicsPipeline`: `teleport(PhysicsPipelineBody, Vector3dc pos, Quaterniondc orient)`,
  `resetVelocity(body)`, `add/remove(ServerSubLevel|KinematicContraption)`.
- `companion` (jarjar): `companion.math.{BoundingBox3i, BoundingBox3ic, Pose3d, Pose3dc, JOMLConversion}`,
  `companion.SubLevelAccess`.

### Aeronautics (referencia, NO API pública)
- `dev.simulated_team.simulated.util.SimAssemblyHelper`:
  - `assembleFromSingleBlock(...)` — patrón de ensamblado (gather → assembleBlocks).
  - `disassembleSubLevel(Level, SubLevel, BlockPos subLevelAnchor, BlockPos disassemblyGoal, Rotation,
    boolean playSound)` — **la referencia que replicamos en `SubLevelDisassembler`**: recoge bloques
    del plot, `moveBlocks(level, transform, blocks)`, mueve entidades (glue), `moveTrackingPoints`.

### Flywheel (`dev.engine_room.flywheel...`)
- `lib.visual.AbstractEntityVisual`: `protected final T entity; isVisible(FrustumIntersection)`.
  **OJO:** `isVisible` solo hace CULLING, NO oculta la geometría (la instancia sigue subida a la GPU).
- `lib.instance.TransformedInstance`: `setZeroTransform()`, `setChanged()`. (Mi intento fallido.)

---

## 5. Historial de fases y estado

| Fase | Qué | Estado |
|------|-----|--------|
| 0 | Limpieza template + build.gradle + estructura | ✅ |
| 1 | Datos + red (PhysicsTrainTag, registries, packets) | ✅ |
| 2 | Botón en AssemblyScreen (gris/activo + icono 24×16) | ✅ (probado: aparece, dispara packet) |
| 3 | Bridge Camino A: capturar bloques → sub-levels de Sable | ✅ (probado: crea N sub-levels, logs) |
| 4a | Driver: sub-level sigue al tren por las vías | ✅ (probado: sigue; altura corregida con rotationPoint) |
| 4b | Quitar render del vagón de Create | ✅ código (merge del compa registrado); **pendiente de probar in-game** |
| — | Quitar colisión + interacción del vagón fantasma | ✅ (del compañero) |
| — | Disassembly vía Sable (mantiene cofres + bloques rotos) | ✅ código; **pendiente de probar in-game** |

---

## 6. Problemas RESUELTOS (y cómo) — historial de prueba-error

1. **El botón no se distinguía / chocaba con la X** → `WideIconButton` mide 26×18 (javap); movido a
   `x+44` (3px de hueco antes de la X en x+73). Icono custom 24×16 que encaja con 1px de borde.
2. **`blit` no compilaba** (`RenderType::guiTextured` no existe en 1.21.1) → firma correcta (javap):
   `blit(ResourceLocation, x, y, u, v, w, h, texW, texH)`.
3. **Error de build del compañero** (`package dev.ryanhcode.sable.companion.math does not exist`) →
   el companion va en el jarjar de Sable; tarea Gradle `extractSableCompanion` lo saca a `deps/`.
4. **Sub-level bajo el suelo / atravesando el suelo** → al principio usaba `getCenterOfMass()` que es
   `null` justo tras ensamblar → no aplicaba la subida. Fix: usar `logicalPose().rotationPoint()`
   (siempre presente). Fórmula: `target = entityPos + rotate(rotationPoint - plotAnchor, Q)`.
5. **Sub-level girado 180°** → `orientationOf` suma +180 al yaw.
6. **Spam de logs "not found"** → trenes físicos huérfanos de pruebas viejas en el registro. Fix:
   auto-limpieza en el tick handler (si el tren no existe → borra del registro + borra sub-levels).
7. **Al desensamblar, el sub-level se separaba/quedaba flotando** → no se borraba. Fix: limpieza al
   desensamblar (antes vía `PhysicsTrainDisassembler`, ahora vía el `@Redirect` de Sable disassembly).
8. **CRASH en carga** (`MixinApplyError ... ContraptionVisualMixin ... attachFields`, cascada a
   create/aeronautics/createbigcannons) → era MI `ContraptionVisualMixin` haciendo `@Shadow` de un
   campo HEREDADO (`entity` de `AbstractEntityVisual`, clase padre de Flywheel). El shadow de campos
   heredados no engancha → rompe la clase compartida. **DESCARTADO.** Lección: no hacer `@Shadow` de
   campos declarados en superclases ajenas.
9. **El vagón de Create seguía visible** (render) → diagnóstico con logs `[client]`/`[render]`:
   - `[client] ... registry knows it: true` → el cliente SÍ sabe que es físico.
   - `[render] hiding immediate-mode ...` salía, pero `[render] ... Flywheel` NO → el render es por
     Flywheel e `isVisible` no oculta. **El approach correcto (del compañero):** `ClientContraption.
     getRenderedBlocks` vacío (ataca la FUENTE: bloques + block entities, vale para ambos backends).
     Solo había que registrarlo y arreglar el `_ ->`.
10. **Disassembly revertía cofres/bloques rotos** → Create coloca su copia de ensamblado, no el
    estado actual. El approach NBT (del compa, `PendingNbtRestoreQueue`) se descartó (el storage de
    Create `returnStorageForDisassembly` lo sobrescribiría). **Solución final:** desensamblar el
    sub-level real con Sable (`moveBlocks`) → el estado actual ES el sub-level, así que cofres e
    inventarios y bloques rotos se mantienen solos.

---

## 7. Qué está PROBADO vs PENDIENTE

**Probado y confirmado in-game:**
- El mod carga (antes del crash del punto 8) sin romper el modpack.
- Botón aparece, gris/activo correcto, dispara el packet.
- Ensamblado crea los sub-levels (logs `Created sub-level ... / Assembled physics train ...`).
- El sub-level SIGUE al tren por las vías (driver), a la altura correcta tras el fix de rotationPoint.

**Pendiente de probar (último jar desplegado, commit `d1a78b0`):**
- Que **YA NO crashea** (quitado el ContraptionVisualMixin malo).
- Que el **vagón de Create desaparece** (ClientContraptionRenderMixin ahora registrado).
- Que al **desensamblar** se mantienen **cofres + bloques rotos** (Sable disassembly).
- Colisión/interacción del vagón fantasma suprimida (del compañero).

---

## 8. Limitaciones conocidas / próximos pasos

1. **Rotación del disassembly = `Rotation.NONE`** (hardcoded en `TrainDisassembleMixin`). Si el tren
   se dio la vuelta (reversed), los bloques pueden quedar orientados al revés. Hay que calcular la
   rotación real (carriage facing actual vs facing de ensamblado) y pasarla a
   `SubLevelDisassembler.disassembleCarriage`.
2. **Multi-dimensión:** asumimos 1 dimensión por tren (`anyAvailableEntity`). OK para v1.
3. **Sonidos:** el compañero notó que su render suppression "no quitaba los sonidos" — los sonidos
   del vagón de Create podrían seguir. A revisar si molesta.
4. **Race de timing del render:** el visual de Flywheel se construye cuando la entidad se renderiza;
   si el packet de sync llega después, `getRenderedBlocks` vacío podría no aplicarse hasta un rebuild.
   En la práctica el sync llega justo tras el AddTrainPacket, así que suele ir bien. Si se ve el
   vagón un instante o de forma persistente, forzar rebuild con `contraption.
   invalidateClientContraptionStructure()` al recibir el sync.
5. **Create Threaded Trains** (`createthreadedtrains`) está en el modpack — los trenes pueden tickear
   en otro hilo. No ha dado problemas observados, pero ojo con races al leer `Create.RAILWAYS.trains`
   y la pose del carriage desde el `ServerTickEvent`.
6. **Física real** (rebotes, descarrile) — futuro, requeriría constraints de Sable
   (`api.physics.constraint.*`) en vez del teleport cinemático.
7. **Física aparente:** "no se puede controlar con train controls" es ESPERADO — el jugador controla
   el tren de Create (con los controles) y el sub-level físico lo sigue. No se controla directamente.

---

## 9. Colaboración con el compañero (merge)

- Historia LINEAL en `master`. El compañero (MDLOP) commiteó encima de los commits de Claude/Lycoris.
- El compañero aportó: `ContraptionColliderMixin` (colisión + cofres), `MovingInteractionBehaviourMixin`
  (interacciones), refactor de `PhysicsTrainTag` a `CarriageEntry` (guardar anchor), y un intento de
  render (que dejó SIN registrar y NO compilaba — `_ ->`). Marcó su último commit "(BROKEN)".
- El merge (`d1a78b0`) integró todo eso, arregló el render del compa y lo registró, descartó mi
  ContraptionVisualMixin (crasheaba), y reimplementó el disassembly vía Sable (idea del compa).
- **MixinExtras** (`com.llamalad7.mixinextras`) está disponible (lo trae NeoForge) — el compañero usa
  `@WrapOperation` y `@WrapMethod`.

---

## 10. Flujo completo del modo físico (resumen mental)

**Ensamblado:** botón → `AssembleAsPhysicsTrainPacket` → `PhysicsAssemblyOrchestrator.assemble` →
`begin()` + `station.assemble(uuid)` → [mixin redirige `removeBlocksFromWorld` por carriage →
`SubLevelBridge` crea `ServerSubLevel` y guarda (subLevelId, anchor)] → Create crea el `Train` y los
`CarriageContraptionEntity` (fantasmas) → orchestrator encuentra el tren nuevo, registra el tag,
`PhysicsTrainSyncPacket` a clientes.

**Cada tick:** `PhysicsTrainTickHandler` lee la pose de cada carriage (Create la calcula con la
matemática de raíles) y `teleport` el sub-level ahí. El vagón fantasma de Create existe (para la
lógica del tren: control, sonidos) pero su RENDER está suprimido (`ClientContraptionRenderMixin`) y
su colisión/interacción también (`ContraptionColliderMixin`). Solo se ve/colisiona el sub-level.

**Desensamblado:** jugador desensambla → `Train.disassemble` posiciona cada vagón y llama
`entity.disassemble()` → [mixin redirige: si físico, `SubLevelDisassembler` mueve los bloques ACTUALES
del sub-level al mundo en esa posición y `entity.discard()`] → Create quita el tren de RAILWAYS → el
tick handler limpia el registro (huérfano).

---

## 11. Estado de git

- Rama `master`, último commit relevante: **`d1a78b0`** (merge + Sable disassembly + render fix).
- SSH configurado, push a master autorizado. Commit/push SOLO cuando el usuario lo pida.
- Jar desplegado en la instancia de pruebas con `d1a78b0`.
- `.gitignore`: `libs/`, `deps/`, `.claude/`, `run/`, `build/`, etc.

---

**Siguiente acción recomendada para el próximo Claude:** pedir al usuario que pruebe el jar actual
(commit `d1a78b0`) y reporte: (1) ¿no crashea?, (2) ¿desaparece el vagón de Create?, (3) ¿el
disassembly mantiene cofres + bloques rotos? Según el resultado, afinar (rotación del disassembly,
race del render, sonidos). Leer el `latest.log` directamente para diagnosticar.

---

## 12. Última sesión (2026-05-31, tarde) — qué se añadió y qué falta

**Probado in-game tras `d1a78b0`:** arranca sin crash ✅, ensambla ✅, **vagón (estructura)
desaparece pero los BOGEYS no** ❌, sub-level **hundido medio bloque** ❌, cofres no accesibles ❌
(consecuencia del hundido), disassembly mantiene cofres + bloques rotos ✅.

### Cambios de esta sesión (aún SIN commit hasta este push)
1. **FIX posición hundida (`PhysicsTrainTickHandler.targetPosition`)** ✅ CONFIRMADO in-game.
   - Con logging real (`[drive]`) se vio: el driver mandaba el sub-level a `Y = entityY + (rotationPoint.y
     - (plotAnchor.y + 0.5))`, medio bloque por debajo. El entity de Create ya está en el **fondo** del
     vagón (altura del raíl), así que en Y NO hay que aplicar el `+0.5` de centrado de bloque (sí en X/Z).
   - Cambio: el offset Y pasó a `rotationPoint.y() - plotAnchor.getY()` (sin `+0.5`). Ahora el vagón
     físico sube a la altura del contraption de Create. X/Z ya eran correctas.
2. **Logging de diagnóstico** (clave para depurar posición): `[drive]` ahora imprime `entityPos, yaw,
   pitch, plotAnchor, rotationPoint, com, orient(quat), target, poseAfter`; `[assemble]` (en
   `SubLevelBridge`) imprime `worldAnchor, plotAnchor, pose.pos, rotationPoint, bounds`. **Úsalo.**
3. **`CarriageBogeyRenderMixin` (nuevo, client)** — `@Inject HEAD cancellable` en
   `CarriageContraptionEntityRenderer.render(CarriageContraptionEntity,...)`, cancela para trenes
   físicos. **Solo sirve sin Flywheel** (ver abajo). Inofensivo con Flywheel.
4. **`PhysicsTrainRenderInvalidator` (nuevo, client) + llamada en `PhysicsTrainSyncPacket.handle`** —
   intento de ocultar bogeys: al recibir el sync, `ClientContraption.invalidateChildren()` de cada
   carriage (con reintentos en `ClientTickEvent.Post` por si las entidades aún no cargaron). **NO
   FUNCIONA todavía** — los bogeys siguen visibles.

### HALLAZGO IMPORTANTE sobre el render de bogeys (verificado en bytecode con javap)
- `CarriageContraptionEntityRenderer.render()` dibuja los bogeys en un bucle `carriage.bogeys.forEach`
  en **modo inmediato**, PERO en `lambda$render$1` el **offset 43** hace `if VisualizationManager.
  supportsVisualization(level) -> goto (skip)`. O sea, **con Flywheel el render inmediato de bogeys se
  SALTA** → los bogeys vienen de un **visual de Flywheel**, no del renderer inmediato.
- La estructura del vagón se oculta bien porque `ClientContraption.getRenderedBlocks` se reevalúa cada
  frame (mixin `ClientContraptionRenderMixin`). Pero los bogeys son **block-entities hijos** que
  `ContraptionVisual.setupChildren` (constructor) recopila **una sola vez** de
  `ClientContraption.renderedBlockEntityView` y crea sus `BogeyBlockEntityVisual`. Como se construye
  antes del sync → no se suprimen. Es la **race del render** (limitación #4).
- El invalidador intenta forzar el rebuild (`invalidateChildren` bumpea `childrenVersion` →
  `ContraptionVisual` re-ejecuta `setupChildren`), pero **aún se ven los bogeys**. Hipótesis a probar:
  (a) `invalidateChildren` no llega a las entidades a tiempo / no se encuentran en `entitiesForRendering`;
  (b) `setupRenderLevelAndRenderedBlockEntities` (que cancelamos) NO es lo que puebla
  `renderedBlockEntityView`, o se cachea aparte; (c) hay que vaciar `renderedBlockEntityView` /
  `shouldRenderBlockEntities` directamente o suprimir en `ContraptionVisual.setupChildren`/`setupVisualizer`
  vía mixin. **Investigar `ClientContraption.setupRenderLevelAndRenderedBlockEntities` (privado) y los
  campos `renderedBlockEntities`/`renderedBlockEntityView`/`shouldRenderBlockEntities`.** Posible mejor
  approach: mixin directo en `ContraptionVisual.setupChildren` o `setupVisualizer` que salte BEs (o solo
  los bogeys) si el tren es físico, en vez de depender del invalidate.

### PENDIENTE (lo que falta — el usuario lo confirmó así)
1. **Ocultar los bogeys del contraption de Create** (siguen visibles; ver hallazgo arriba). PRIORIDAD.
2. **Que el sub-level físico siga bien las vías:** ahora sube a la altura correcta pero queda **TORCIDO**
   y al ir **adelante/atrás cambia de posición y queda mal**. Sospecha: la **orientación**
   (`orientationOf` = `rotationYXZ(-yaw+180, pitch, 0)`) o el punto de referencia no es estable cuando el
   tren se mueve / invierte (`currentlyBackwards`). Mirar el `[drive]` log con el tren en movimiento y en
   curva: comparar `yaw/pitch` y `orient` con la pose real del vagón de Create. Probablemente haya que
   usar el yaw/pitch del `CarriageContraptionEntity` con partial-ticks o derivar la orientación de los dos
   bogeys (leading/trailing) como hace Create en `CarriageBogey.updateAngles`.

**Funciona y confirmado in-game:** altura correcta ✅, cofres accesibles ✅, disassembly conserva
cofres+inventarios+bloques rotos ✅.

**Workflow de prueba:** el jar se despliega en `Skybound SMP/mods/` y el `latest.log` de esa instancia
SÍ lo puede leer Claude directamente (la prueba debe hacerse en ESA máquina, no en la del compañero).
