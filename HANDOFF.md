# Loconautics — HANDOFF completo (para continuar en una nueva conversación)

> Este documento contiene TODO el estado del proyecto: objetivo, arquitectura, hallazgos de API,
> problemas resueltos, qué se ha probado, limitaciones y próximos pasos. Está pensado para que un
> Claude (u otra persona) pueda continuar sin perder NADA de contexto.
>
> **Léelo entero antes de tocar código.** Complementa a `PLAN_IMPLEMENTACION.md` (spec técnica
> original) y `PLAN_EQUIPO.md` (reparto humano). Este HANDOFF es la fuente de verdad más reciente.
>
> **⚡ ORDEN DE LECTURA recomendado:** §14 (lo MÁS reciente: COLISIÓN del tren físico — **la SOLUCIÓN
> concreta está en §14.6**, encontrada en el fuente de Aeronautics) → §13 (RENDER, ya resuelto) → resto
> para contexto. El render del cuerpo y el bogey fantasma están RESUELTOS; lo abierto es la **colisión**:
> hay que cambiar el `teleport`-por-tick por un **constraint con motores en el physics tick** (§14.6).

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

- Rama `master` (historia lineal), **último commit: `cc781d8`**. Hitos recientes:
  - `cc781d8` — wip(collision): desactiva colisión del contraption de Create + documenta §14 (ESTE).
  - `1b57c1d` — fix(render): oculta el bogey viajero fantasma de Create (§13.5 resuelto).
  - `cf956b8` — lock del cuerpo físico al vagón (render clavado, sin jitter).
- SSH configurado, push a master autorizado. **Commit/push SOLO cuando el usuario lo pida.**
- `.gitignore`: `libs/`, `deps/`, `.claude/`, `run/`, `build/`, etc.

---

**Siguiente acción recomendada para el próximo Claude / el compa:** la tarea abierta es la **COLISIÓN**
del tren físico (no el render, ya resuelto). Leer **§14** entero: la interacción funciona, la física de
Sable ya está en el cuerpo visible, y el problema es el modelo de movimiento **teleport-por-tick**
(stutter + empuje lateral). Dirección: reemplazarlo por movimiento cinemático continuo (estudiar
`KinematicContraption` de Sable, el `transformProvider` de CI, y cómo Aeronautics mueve sus sub-levels).
Leer el `latest.log` directamente para diagnosticar.

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

### FIXES de esta sub-sesión (aplicados, PENDIENTES DE PROBAR in-game)
1. **Bogeys — fix real (cambio `invalidateChildren` → `resetRenderLevel`).** Diagnóstico definitivo (javap):
   `ContraptionVisual.setupChildren` lee `ClientContraption.renderedBlockEntityView` que pobló
   `setupRenderLevelAndRenderedBlockEntities` (privado) ANTES del sync. `invalidateChildren()` solo
   reconstruye los visuales desde esa lista YA poblada → los bogeys sobrevivían. **`resetRenderLevel()`**
   sí: `renderedBlockEntities.clear()` + re-llama `setupRenderLevelAndRenderedBlockEntities` (que nuestro
   `ClientContraptionRenderMixin` CANCELA para físicos → lista vacía) + invalida estructura/children.
   `PhysicsTrainRenderInvalidator` ahora llama `resetRenderLevel()`. (El `CarriageBogeyRenderMixin` sigue,
   solo cubre el caso sin-Flywheel.)
2. **Orientación "torcida" — fix replicando a Create EXACTO (javap de
   `OrientedContraptionEntity.applyLocalTransforms`).** Create rota el vagón así:
   `center → rotateY(viewYaw) → rotateZ(pitch) → rotateY(initialYaw) → uncenter`, con
   `viewYaw = -yaw` (getViewYRot **niega** el yaw), `pitch` **alrededor de Z** (no X), e
   `initialYaw = initialOrientation.toYRot()` (SOUTH=0/WEST=90/NORTH=180/EAST=270). El `orientationOf`
   viejo (`rotationYXZ(-yaw+180, pitch, 0)`) tenía un **+180 inventado**, pitch como X, e **ignoraba
   initialYaw** → de ahí el torcido. Ahora:
   `new Quaterniond().rotateY(toRad(-yaw)).rotateZ(toRad(pitch)).rotateY(toRad(initialYaw))`. En
   ensamblado `yaw==initialYaw` → identidad, que casa con el sub-level capturado world-aligned. El log
   `[drive]` ahora imprime también `initYaw`.
   - **Riesgo a verificar:** (a) handedness de `rotateYDegrees`/`rotateZDegrees` de Flywheel vs JOML
     (asumido igual, right-handed); si sale espejado, invertir signos. (b) El **pivote de posición**: la
     fórmula `targetPosition` rota el offset alrededor del `rotationPoint` (≈COM), pero Create pivota en el
     centro del vagón; si al **invertir marcha** (`currentlyBackwards`, yaw flipea 180) el vagón se
     desplaza, hay que alinear el pivote (usar el centro del vagón, no COM). Mirar `[drive]` en
     movimiento/curva.

**Funciona y confirmado in-game (antes de estos 2 fixes):** altura correcta ✅, cofres accesibles ✅,
disassembly conserva cofres+inventarios+bloques rotos ✅.

**Workflow de prueba:** el jar se despliega en `Skybound SMP/mods/` y el `latest.log` de esa instancia
SÍ lo puede leer Claude directamente (la prueba debe hacerse en ESA máquina, no en la del compañero).

---

## 13. SESIÓN DE RENDER (2026-06-01) — FUENTE DE VERDAD PARA RENDER (ya resuelto; lo MÁS reciente es §14) ⭐

> **Léela ANTES de tocar render.** Todo lo aprendido haciendo que el tren físico SE VEA bien.
> Complementa `ANALISIS_CREATE_INTERACTIVE.md`. Donde haya conflicto con secciones anteriores sobre
> render/orientación, MANDA ESTA.

### 13.0 Estado actual (qué funciona / qué falta)
- ✅ El **cuerpo del vagón (sub-level) va CLAVADO** a la posición del vagón de Create, sin jitter ni
  arrastre, recto y en curva (commit **`cf956b8`**).
- ✅ Altura correcta, cofres accesibles, disassembly conserva inventarios/bloques rotos.
- ✅ El sub-level lleva su **propio bogey físico** sincronizado con el cuerpo.
- ✅ **RESUELTO (2026-06-01):** el **bogey fantasma de Create** (el que giraba y seguía al tren con
  ~0.5s de lag) **ya NO se ve.** Era el **bogey VIAJERO** de `CarriageContraptionVisual.animate()`
  (un `BogeyVisual[]` propio de la subclase, ANIMADO siguiendo la vía) — una vía de Flywheel APARTE
  de los children y del `BlockEntityStorage`. Se oculta con `CarriageBogeyVisualMixin`. Ver §13.5.
- ⏳ **Pendiente (polish, opcional):** el bogey que queda (el del sub-level) va rígido pero **NO gira**
  (es bloque estático). Para ruedas girando habría que animarlo en fase con el cuerpo (CI Opción 2).

### 13.1 ARQUITECTURA DE RENDER (cómo se dibuja el tren físico)
Create maneja la LÓGICA y la POSICIÓN del tren (vías, señales). El sub-level de Sable es lo que SE VE y
lo FÍSICO (colisión, cofres). Para que el sub-level se vea exactamente donde está el vagón:

1. **Driver servidor** (`PhysicsTrainTickHandler`): cada tick teleporta el cuerpo físico del sub-level a
   la pose del vagón (`teleport` + `resetVelocity`). Es para la COLISIÓN.
   - OJO: en el SERVIDOR `entity.getRotationState()` devuelve TODO CERO → la orientación de colisión es
     identidad. Aceptable para colisión.
2. **Acoplamiento de render CLIENTE** = `ClientSubLevelRenderMixin` (mixin sobre
   `ClientSubLevel.renderPose(F)`). **EL TRUCO CLAVE.** Cada frame sobrescribe la pose con la que Sable
   dibuja el sub-level para que sea la pose interpolada EXACTA del vagón de Create:
   - posición = `carriage.getPosition(partialTick)` (= `lerp(xOld,x)`, lo MISMO que usa Create para el
     contraption, verificado en `ContraptionMatrices.translateToEntity`).
   - orientación = `PhysicsTrainPose.orientationOf(carriage)` (de `getRotationState()`, en CLIENTE sí
     está calculado → correcto en curvas).
   - anclaje: `renderPos = carriagePos + Q·(rotationPoint - plotAnchor - (0.5,0,0.5))`.
   - Esto eliminó el jitter/arrastre. **NO volver a conducir el render desde la pose del servidor.**

### 13.2 ARCHIVOS NUEVOS/CLAVE de esta sesión
- `core/PhysicsTrainPose.java` — `orientationOf(carriage)` compartido. Usa `ContraptionRotationState`:
  `rotateZYX(zRot,yRot,xRot).rotateLocalY(yawOffset)`.
- `mixin/client/ClientSubLevelRenderMixin.java` — el acoplamiento de render (`renderPose(F)`).
- `client/ClientPhysicsTrainRegistry.java` — `findCarriage(subLevelId)`, `isPhysicsSubLevel(uuid)`,
  `physicsCarriages()`.
- `mixin/client/CarriageBogeyVisualMixin.java` — **(EL FIX del bogey, 2026-06-01)** `@Mixin` de
  `CarriageContraptionVisual` (subclase de `ContraptionVisual`), `@Inject HEAD cancellable` en
  `animate(float)`. Para trenes físicos: `visuals[i].hide()` en cada `BogeyVisual` + cancel. Detecta
  el tren por `contraption.entity` (campo propio `@Shadow`, fiable). Esto oculta el bogey viajero.
- `mixin/client/ContraptionVisualChildrenMixin.java` — `@Mixin(ContraptionVisual)`, `@Inject HEAD` en
  `setupChildren(Contraption,...)`: borra/limpia los `children` (otros BE como cofres) + cancela, para
  no doble-renderizarlos (el sub-level ya los dibuja). Detecta por el arg `Contraption`. NOTA: los
  bogeys NO son children (ver §13.3.5), de eso se encarga `CarriageBogeyVisualMixin`.
- `client/PhysicsTrainRenderInvalidator.java` — al recibir el sync, `resetRenderLevel()` del contraption
  para forzar el rebuild del visual (estructura/children) ahora que el tren se sabe físico (gana la race
  de "visual construido antes del sync"). El bogey viajero NO necesita rebuild (se oculta por frame).
- ~~`BlockEntityStorageMixin`~~ — **ELIMINADO.** Fue un callejón sin salida (ver §13.5).

### 13.3 LECCIONES / CALLEJONES SIN SALIDA (NO repetir) 🚫
1. **NO reconstruir la orientación a mano** desde yaw/pitch. Falla en curvas. Usar `getRotationState()`.
2. **El SERVIDOR no calcula `getRotationState()`** (devuelve 0). Solo en CLIENTE. Por eso el render se
   acopla en cliente, no desde la pose del servidor.
3. **NO usar velocidad/extrapolación** (`addLinearAndAngularVelocity`) para suavizar: el cuerpo se
   adelantaba/vibraba. Quitado. Solo `resetVelocity`.
4. **`getPosition(partialTick)` SÍ, `getAnchorVec()` NO.** Create dibuja el contraption con
   `getPosition` (lerp xOld/x). anchorVec hacía el cuerpo ir "rápido".
5. **El bogey VIAJERO de Create NO es un child ni un BE-visual del `BlockEntityStorage`.** ⭐ (CLAVE,
   resuelto 2026-06-01) Los bogeys de Create tienen `AbstractBogeyBlock.captureBlockEntityForTrain()
   == false` → `CarriageClientContraption.readBlockEntity` devuelve `null` → el bogey NUNCA entra en
   `renderedBlockEntityView` → `setupChildren`/`setupVisualizer` NUNCA crean un visual de bogey (por eso
   `[bogey-cancel]`/`[bogey-kill]` salían con 0 / no salían). El bogey viajero (animado, sigue la vía)
   lo dibuja **`CarriageContraptionVisual` (subclase de `ContraptionVisual`)** con su propio array
   `BogeyVisual[] visuals`, creado/animado en `animate(float)` (llamado desde `beginFrame`). **Se oculta
   ahí**: `visuals[i].hide()` + cancel `animate` → `CarriageBogeyVisualMixin`.
6. **Los bloques de bogey SÍ se capturan en el sub-level** (`CarriageContraption.capture` →
   `super.capture` los mete en `getBlocks()` → `SubLevelBridge` los lleva al sub-level). Ese bogey del
   sub-level es el que se MUESTRA (rígido con el cuerpo, pero estático: sin ruedas girando). El bogey
   de Create que se OCULTA es el **viajero** (otra cosa, ver #5).
7. **`@Shadow` de campo HEREDADO de superclase ajena = CRASH** (problema #8). Para `entity` de
   `AbstractEntityVisual` se EXTIENDE la clase (no `@Shadow`). Para campos del PROPIO target (p.ej.
   `renderPose` de `ClientSubLevel`) sí vale `@Shadow`.
8. **Bug de edición recurrente:** poner un campo `private static int loconautics$logged` JUSTO antes
   del método con `@Inject` lo mete ENTRE la anotación y el método → error "annotation interface not
   applicable". Poner el campo ANTES de la anotación.
9. **El render inmediato de bogeys (`CarriageContraptionEntityRenderer.render`) se SALTA bajo Flywheel**
   (offset 43). `CarriageBogeyRenderMixin` solo sirve sin Flywheel.

### 13.4 EL DESAFÍO DE FONDO (cuerpo Sable vs bogey Create) — RESUELTO
El cuerpo lo dibuja Sable (chunk render del sub-level, acoplado por `ClientSubLevelRenderMixin`), y el
bogey **viajero** lo dibujaba Create vía Flywheel (`CarriageContraptionVisual.animate`) interpolando en
la fase del entity de Create → de ahí el lag de ~0.5s y la sensación de "fantasma que sigue al tren".
**Solución aplicada:** ocultar el bogey viajero de Create (`CarriageBogeyVisualMixin`) y dejar que el
bogey lo aporte el SUB-LEVEL (sus bloques de bogey ya van rígidos con el cuerpo). Resultado: un único
bogey, clavado, sin lag. (Queda estático/sin girar — ver §13.5 polish.)

### 13.5 RESUELTO: el bogey fantasma de Create (cómo se llegó, qué NO funcionó)
**Qué era:** el bogey **viajero** de `CarriageContraptionVisual` (§13.3.5). Se oculta con
`CarriageBogeyVisualMixin` (`@Inject HEAD` en `animate(float)`, `visuals[i].hide()` + cancel). ✅

**Callejones sin salida de esta caza (NO repetir):**
1. **`BlockEntityStorage.willAccept` por "render world fantasma"** (idea de la sesión anterior):
   el log demostró que el bogey BE **nunca** está en el `VirtualRenderWorld` rastreado — siempre en el
   `ClientLevel` principal (`ClientSubLevel.getLevel()` devuelve el ClientLevel; los sub-levels viven en
   el mundo principal a coords de plot lejanas, p.ej. x=20481032). Detección por nivel = imposible.
   `BlockEntityStorageMixin` ELIMINADO.
2. **Suprimir en `willAccept` los bogeys DENTRO del sub-level físico** (invertir lo anterior): MAL —
   eso quitaba el bogey BUENO del sub-level y dejaba el fantasma. Confirmó que el `willAccept` NO es el
   camino del fantasma. (La nota vieja "getContainingClient != null → dejar pasar" era correcta.)
3. **`ContraptionVisual.setupVisualizer` / `setupChildren` / `resetRenderLevel`:** NO afectan al bogey
   (no es child, ver §13.3.5). `ContraptionVisualChildrenMixin` sí sirve para OTROS BE (cofres), no para
   bogeys.
4. **El bloque de bogey en el mundo (x=-125, la pos de ensamblado)** existe (`subLevel=none`) pero está
   FIJO ahí, NO es el fantasma que sigue al tren. Pista decisiva del usuario: "el bogey fantasma SIGUE
   al tren con lag" → descartó el fijo y apuntó al viajero animado.

**Cómo diagnosticar vías de render de un bogey (receta que funcionó):** `javap` de
`CarriageContraptionEntityRenderer.lambda$render$1` (inmediato, se salta bajo Flywheel),
`ContraptionVisual.setupChildren/setupVisualizer` (children), y **`CarriageContraptionVisual`** (el
viajero: campos `BogeyVisual[] visuals`, `CarriageBogey[] bogeys`, métodos `animate`/`checkCarriage`).

**Polish pendiente (opcional):** el bogey del sub-level va rígido pero NO gira (bloque estático). Para
ruedas girando: re-render del bogey en fase con el cuerpo (estilo CI Opción 2, `translateBogeyWithShip`
→ con `subLevel.renderPose`), o dejar el bogey viajero de Create pero forzar su pose a la del sub-level
en `animate` en vez de ocultarlo.

### 13.6 RECORDATORIOS DE WORKFLOW
- **Build+deploy:** `./gradlew build` → `cp build/libs/loconautics-1.0.0.jar "/c/Users/User/curseforge/
  minecraft/Instances/Skybound SMP/mods/"`. **Hay que REINICIAR Minecraft** (no hay hot-reload). El
  usuario está MUY cansado de reiniciar → batch de cambios, acertar a la primera, verificar md5 del jar
  desplegado vs compilado.
- **Log:** `...\Skybound SMP\logs\latest.log` (Claude lo lee directo). Verificar que el run arrancó
  DESPUÉS del deploy y que el md5 del jar coincide.
- Para "ver" de verdad, pedir al usuario una CAPTURA (las suyas fueron clarísimas).
- **Create-Interactive** (`ValkyrienSkies/Create-Interactive`, licencia *All Rights Reserved* →
  REIMPLEMENTAR, NO copiar). El mapa VS→Sable está en `ANALISIS_CREATE_INTERACTIVE.md`.
- **Commit/push solo cuando el usuario lo pida.** Usar `-m` múltiples de bash (NO here-string de
  PowerShell `@'...'@`, mete un `@` en el asunto). Acabar con `Co-Authored-By: Claude Opus 4.8`.

---

## 14. SESIÓN COLISIÓN/INTERACCIÓN (2026-06-02) — TAREA ABIERTA PARA EL COMPA ⭐

> El render ya está clavado y el bogey fantasma oculto (§13, commit `1b57c1d`). Esta sesión atacó la
> **colisión y la interacción** del tren físico. El bogey quedó RESUELTO. La colisión NO — se deja
> documentada aquí con todo lo probado, los hallazgos DEFINITIVOS y la dirección recomendada.

### 14.0 Qué se commiteó esta sesión (estado del repo)
- `mixin/ContraptionColliderSkipMixin` — `@Inject HEAD cancellable` en `ContraptionCollider.collideEntities`
  → cancela para trenes físicos. Desactiva el **empuje + anti-clip** del contraption de Create. Es el
  enfoque de CI (`MixinContraptionCollider`: *"Disable contraption collision entirely! We'll get it from
  VS2"*). **Verificado en log** (`[collide-skip] physics=true -> CANCELLED` en CLIENT y SERVER).
- `mixin/EntityNoContraptionCollisionMixin` — `@Inject HEAD cancellable` en `Entity.canBeCollidedWith`
  → `false` para `CarriageContraptionEntity` física. Belt-and-suspenders: la entidad contraption no es
  sólida. (Probablemente no-op porque `canBeCollidedWith` ya es `false` por defecto, pero deja explícito
  el estado deseado: **colisión de Create totalmente OFF**.)
- Resto SIN cambios respecto a `1b57c1d` (se revirtieron los experimentos fallidos, ver §14.3).

### 14.1 HALLAZGOS DEFINITIVOS (medidos in-game, no teoría) ✅
1. **La INTERACCIÓN/RAYTRACE FUNCIONA.** Con F3, al apuntar al cuerpo visible el **"Targeted Block" sale
   en coords del PLOT** (`20481032, 12X, 20481032`) con el bloque correcto (`create:controls`,
   `railway_casing`, `small_bogey`). **Sable transforma el rayo del jugador al sub-level** → apuntas a los
   bloques del cuerpo visible. **NO hay desfase de apuntado.** → NO parchear `getAnchorVec`/`toLocalVector`
   de Create; de la interacción se encarga Sable.
2. **La FÍSICA de Sable ESTÁ en el cuerpo visible.** Log `[poses]` (cliente, tren parado):
   `entityPos=(-7.5, 64.0, 22.5)`, `renderPose=(-7.5, 64.643, 22.098)`, `logicalPose=(-7.5, 64.636,
   22.098)`. → **`logicalPose` (física Sable) ≈ `renderPose` (visible), IDÉNTICOS.** La pose física del
   sub-level YA está donde se ve. No hay nada que "sincronizar" entre física y render.
3. **La colisión que el jugador siente ("caja gris" en F3+B) está en `entityPos`** (la del contraption de
   Create: `y -0.64`, `z -0.40` respecto al cuerpo visible). El usuario quiere chocar con la roja (visible),
   no con la gris.
4. **PERO la colisión de Create está TOTALMENTE desactivada** (`collideEntities` cancelado, entidad no
   sólida) **y la gris PERSISTE** → por eliminación, la "gris" NO es de Create: es el **cuerpo cinemático
   de Sable comportándose mal** (la pose física = visible, pero la *resolución* de colisión empuja al
   jugador de lado al aterrizar, y la posición efectiva se siente a nivel de raíl). Es un problema del
   **modelo cinemático teleport-por-tick**, no de Create.

### 14.2 SÍNTOMAS que reporta el usuario
- Al volar/caer encima del tren parado, **al tocar suelo te teletransporta al bloque de al lado** (empuje
  lateral en la resolución de colisión).
- Al **conducir**, el cuerpo **no te arrastra** (te empuja) — el body se teletransporta por debajo de ti.
- Probé darle velocidad al body (como CI) → **STUTTERING** notable.

### 14.3 CALLEJONES SIN SALIDA de esta sesión (NO repetir) 🚫
1. **Parchear `getAnchorVec`/`toLocalVector` de Create** para alinear interacción/colisión: INNECESARIO.
   Sable ya maneja el raytrace (§14.1.1). Probé varias fórmulas de offset (render-offset, delta de
   interpolación + `(Q-I)·(0,0.5,0)`); **todas dan 0 estando parado/plano** y no cambian nada → el modelo
   de mapeo plot→mundo que asumí era incorrecto. Revertido.
2. **`addLinearAndAngularVelocity` (velocidad CI) en el driver** para que el body arrastre al jugador:
   causó **stutter**. Revertido a `resetVelocity`. (CI da velocidad pero su ship es `isStatic=true` con un
   `transformProvider`, no `teleport`+velocidad como nosotros.)
3. **Desactivar más colisión de Create**: no queda más. `collideEntities` es la única vía jugador↔contraption
   y está off; la entidad no es sólida (`canBeCollidedWith` default false); `EntityContraptionInteractionMixin.
   create$onMove` solo pone `onGround`, no bloquea. → la gris NO es de Create.

### 14.4 CAUSA RAÍZ (hipótesis fuerte) y DIRECCIÓN RECOMENDADA para el compa 🎯
**El driver `PhysicsTrainTickHandler` hace `pipeline.teleport(serverSub, target, orient)` + `resetVelocity`
cada tick.** El teletransporte discreto choca con el modelo de interpolación/colisión de Sable:
- F3 de Sable muestra `Interpolation running / Delay: 0.50t` al moverse → Sable **interpola** las poses del
  servidor con un buffer. El teleport-por-tick produce saltos que la interpolación no suaviza para la
  COLISIÓN (el render sí, porque lo desacoplamos en `ClientSubLevelRenderMixin`).
- Sable tiene un modelo kinemático pensado para esto: **`dev.ryanhcode.sable.api.sublevel.KinematicContraption`**
  expone `sable$getPosition(partialTick)` / `sable$getOrientation(partialTick)` → una transform **continua
  e interpolada por partialTick**, no un teleport. (El §1 del ANALISIS lo descartó por necesitar
  `MassTracker`/`FloatingClusterContainer`/`BlockGetter`; reconsiderarlo — puede que sí sea el camino, o que
  haya forma de registrar el `ServerSubLevel` como kinemático.)
- **Create-Interactive lo resuelve con un `ServerShipTransformProvider`** (ANALISIS §1): el ship es
  `isStatic=true` y el provider DICTA `(transform, velocidad lineal, omega)` cada tick. El cliente
  **extrapola suave** con la velocidad → sin stutter, y las entidades encima se mueven con el body.

**Tareas concretas sugeridas:**
1. Buscar en Sable el equivalente al `transformProvider` de VS: ¿cómo se mueve un `ServerSubLevel` de forma
   continua (no teleport)? Mirar `PhysicsPipeline` completo (no solo `teleport`/`resetVelocity`/
   `addLinearAndAngularVelocity`), `ServerSubLevelContainer.physicsSystem()`, y si hay un modo "kinemático"
   o "static body con target".
2. Estudiar **cómo Aeronautics mueve sus sub-levels** (aviones con pasajeros, mismo motor Sable, SIN stutter)
   — es el mejor referente. El jar es `run/mods/create-aeronautics-bundled-1.21.1-1.2.1.jar` (paquete
   `dev.simulated_team...`). Probablemente usa el pipeline de física con fuerzas/velocidad continua, no
   teleport. Replicar ese patrón para un body que sigue un path determinista.
3. Si el body se mueve continuo (con velocidad real) en vez de teleport: (a) la colisión dejará de empujar
   de lado, (b) arrastrará al jugador, (c) sin stutter. El reto: que siga EXACTO el path de Create. CI lo
   logra dictando transform+velocidad cada tick con `isStatic=true`.

### 14.5 HERRAMIENTAS / DATOS para depurar
- Log `[drive]` (servidor, cada 40 ticks) en `PhysicsTrainTickHandler`: imprime `entityPos, yaw, pitch,
  initYaw, rotState, plotAnchor, rotationPoint, com, orient, target, poseAfter`. **Clave:** en SERVIDOR
  `rotState` siempre 0 → `orient` identidad. En este tren `yaw=initYaw=270` → orientación correcta = identidad
  (el cuerpo se capturó orientado así); el desfase de colisión NO es de orientación, es del modelo de movimiento.
- El log `[poses]` (cliente) que probó §14.1.2 fue revertido, pero se puede re-añadir en
  `ClientSubLevelRenderMixin` (loguear `entityPos / renderPose / self.logicalPose()`).
- F3+B in-game muestra: caja **blanca** = AABB de la entidad contraption; **roja** = cuerpo visible (lo que
  el usuario quiere); **gris** = lo que choca (cinemático Sable, a nivel raíl). El usuario hace capturas
  excelentes con F3 — pedírselas.
- `getRotationState()` de `OrientedContraptionEntity` (verificado javap): `zRotation=pitch`,
  `yRotation=-yaw+getYawOffset()` (yawOffset=`getInitialYaw()`), y el campo `yawOffset` del state se queda en
  0 (no se setea). En servidor `yaw` (animado) ya = facing real, así que `yRotation≈0` cuando `yaw=initYaw`.

### 14.6 ⭐ SOLUCIÓN ENCONTRADA (2026-06-02, tras subir el FUENTE COMPLETO de Aeronautics) ⭐
El compa subió el código fuente completo de Aeronautics/Sable a `src/depend/Aeronautics/`. **Ahí está
cómo se DEBE mover un sub-level: NO con `teleport`, sino con un CONSTRAINT con MOTORES (spring-damper)
en el PHYSICS tick.** Referencia exacta: `dev.simulated_team.simulated.content.physics_staff.
PhysicsStaffServerHandler` (la "physics staff" arrastra un sub-level a donde apunta el jugador =
seguimiento de objetivo móvil, NUESTRO caso).

**Receta (copiar el patrón, NO el código literal):**
1. **Engancha el PHYSICS tick** (no el game tick): `SableEventPlatform.INSTANCE.onPhysicsTick((physicsSystem,
   timeStep) -> ...)`. Corre a la frecuencia de substeps de física (suave) y da `physicsSystem.
   getPartialPhysicsTick()` para interpolar. (Aeronautics lo registra en `Simulated.java:87` →
   `SimulatedCommonEvents.onPhysicsTick`.)
2. **Crea UN constraint** entre el mundo y el sub-level (una vez, al ensamblar o lazy):
   `PhysicsConstraintHandle c = pipeline.addConstraint(null /*bodyA=mundo*/, serverSub /*bodyB*/,
   new FreeConstraintConfiguration(JOMLConversion.ZERO /*anchor en mundo*/, plotAnchorLocal /*anchor en
   sub-level*/, orientationRef));` (`PhysicsPipeline pipeline = container.physicsSystem().getPipeline()`).
   - Alternativa rígida: `FixedConstraintConfiguration(pos, rotationPoint, orientation)` (es lo que usa el
     "lock" del staff — pin rígido a una pose; ver `PhysicsStaffServerHandler.addConstraint`).
3. **Cada physics tick, mueve los motores hacia la pose del vagón** (en vez de teleport):
   ```
   for (ConstraintJointAxis ax : ConstraintJointAxis.ANGULAR)
       c.setMotor(ax, targetAngle, angularStiffness, angularDamping, false, 0.0);
   c.setMotor(ConstraintJointAxis.LINEAR_X, goalLocal.x, linStiffness, linDamping, false, 0.0);
   c.setMotor(ConstraintJointAxis.LINEAR_Y, goalLocal.y, ...);
   c.setMotor(ConstraintJointAxis.LINEAR_Z, goalLocal.z, ...);
   ```
   donde `goalLocal` = la pose objetivo del vagón (posición + orientación que YA calculamos en
   `PhysicsTrainTickHandler.targetPosition`/`PhysicsTrainPose.orientationOf`) transformada al marco del
   constraint (ver `DragSession.physicsTick`: `orientation.transformInverse(localGoal)`).
   - Con **stiffness alto + damping** el cuerpo sigue el objetivo MUY pegado pero de forma **física y
     continua** → arrastra al jugador, **sin stutter**, y la colisión se resuelve natural (es un cuerpo
     físico tirado por un muelle, no teletransportado).
4. **Limpieza:** `c.remove()` al desensamblar / cuando el tren desaparece (como `DragSession.onRemoved`).

**Por qué esto arregla TODO lo de §14.2:**
- No hay teleport discreto → no hay stutter (el cuerpo se integra continuo entre substeps).
- El cuerpo es un rigid body de verdad tirado hacia el objetivo → **arrastra entidades** encima.
- La colisión la resuelve la física de Sable normalmente → no más empuje lateral raro.

**Archivos del fuente a estudiar (todos en `src/depend/Aeronautics/dev/simulated_team/...`):**
- `content/physics_staff/PhysicsStaffServerHandler(.java + $DragSession)` — EL patrón (drag por motores + lock fijo).
- `events/SimulatedCommonEvents.onPhysicsTick` + `Simulated.java:87` — cómo engancha el physics tick.
- `content/blocks/handle/HandleBlockEntity`, `docking_connector/DockingConnectorBlockEntity` — más ejemplos
  de constraints/motores en `sable$physicsTick(subLevel, RigidBodyHandle, timeStep)`.
- `content/entities/launched_plunger/LaunchedPlungerEntity.physicsTick(...)` — patrón alternativo:
  `RigidBodyHandle` + aplicar fuerzas/impulsos directos (por si se prefiere control por fuerzas).
- API Sable (en `src/depend/Sable/`): `FreeConstraintConfiguration`, `FixedConstraintConfiguration`,
  `PhysicsConstraintHandle`, `ConstraintJointAxis`, `PhysicsPipeline.addConstraint(...)`, `RigidBodyHandle`.

**Plan de implementación sugerido:** reescribir `PhysicsTrainTickHandler` para: (a) crear el constraint
por carriage al registrar el tren físico, (b) suscribirse a `SableEventPlatform.onPhysicsTick` y ahí
poner los motores hacia la pose del vagón (reutilizando `targetPosition` + `orientationOf`), (c) quitar el
`teleport`+`resetVelocity`, (d) remover el constraint al desensamblar. Empezar con stiffness/damping altos
y tunear. El render (ClientSubLevelRenderMixin) y el bogey (§13) se quedan IGUAL — solo cambia el motor del
cuerpo físico.
