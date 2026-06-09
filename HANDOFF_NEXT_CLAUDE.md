# HANDOFF para la próxima Claude — Loconautics (sesión 2026-06-04 → 06)

> **Léeme ENTERO antes de tocar nada.** Comprime TODO lo de esta sesión: colisión (RESUELTA), controles
> (WIP medio-funcional), mecanismos internos de Sable, investigación web, el mod "bearing", y el **pivote
> en curso a "todo Sable"**. Complementa (y CORRIGE) a `HANDOFF.md` (§14.6 era un callejón) y
> `HANDOFF_2026-06-04_collision-controls.md`. Memoria persistente:
> `memory/loconautics-sable-collision-pathways.md`.

---

## 0a. ACTUALIZACIÓN 2026-06-09 noche (LÉEME PRIMERO — estado más reciente)
Rama `feature/all-sable-physics-train`, jar desplegado md5 **`2539d13`** (último cambio de código `d87f932`;
este doc por encima). Haz `git switch feature/all-sable-physics-train && git pull` y `git log --oneline -15`
para el estado exacto. Working tree limpio, todo pusheado.

> **PRIMER PASO sugerido para la nueva Claude:** el problema abierto nº1 es el **Analog Controller** (no
> conduce dentro del sub-level). Ya hay logs `[analog]` instrumentados — pide al usuario probarlo y **lee
> `…/Skybound SMP/logs/latest.log`** (busca `[analog]`) para ver si es montaje/rango o enrutado de teclas,
> antes de tocar nada. NO adivines.

Workflow sin cambios (build `./gradlew build` → copiar jar a `Skybound SMP/mods/` y verificar md5 → el usuario
reinicia, no hay hot-reload → tú lees `latest.log`). Commit/push solo cuando el usuario lo diga (en esta
sesión autorizó commit+push tras cada cambio probado). Acabar commits con
`Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.
**Referencia clave nueva: `RESEARCH_sable_aeronautics.md`** (catálogo a fondo de TODA la API de Sable/Aeronautics
— motion models, fuerzas, constraints, controles, `Sable.HELPER`, etc. Léelo antes de tocar física).

**EL TREN ALL-SABLE FUNCIONA IN-GAME** (modo "pin" por defecto): `/loconautics sabletrain [speed]` levanta un
cart construido sobre una vía a un sub-level físico que **recorre la vía, gira en curvas y te lleva encima
fluido**, sin `CarriageContraptionEntity` ni Create `Train`. Código en `allsable/`:
- `RailFollower`/`RailCarriage`/`RailPose` (matemática de vías), `SableTrain`/`SableTrainRegistry`/
  `SableTrainDriver` (modelo + driver), `SableTrainSpawner` (comando), `RailDebug` (comandos).
- Comandos: `/loconautics sabletrain [speed]` (spawn), `... speed <v>` / `... stop` (throttle en vivo),
  `... addcar` (multi-vagón: cada vagón su sub-level+2 bogeys, hereda dirección del tren), `... clear`,
  `... physics [speed]` (ver abajo). Debug de raíl: `railtest`/`railtest2`.
- **Motion model = teleport-pin** (cinemático): el driver fija el `logicalPose` de cada vagón a la pose de su
  `RailCarriage` cada physics tick. Orientación = **delta de bases** (arregló el roll en rampas; era Euler
  rotateX=roll al ir por eje X). Giro estilo Create = bogeys en los extremos, centrados.
- **Captura del cart**: flood-fill SOLO de bloques por ENCIMA del raíl (da igual que toque el suelo).
- **Bearing axle weight EN all-sable**: `SableTrainDriver.updateAxleMass` cada 30 ticks suma la masa de los
  sub-levels y la pone en el axle (antes salía 0; el cableado solo existía en el híbrido).
- **Controles en sub-levels (patrón clave)**: el alcance/rango se mide con `Sable.HELPER.projectOutOfSubLevel`
  (NO con coords crudas del plot, que están lejísimos → desmontaba al instante). Ver `AnalogControllerBlockEntity.playerInRange`.

**MODO FÍSICO (force-bogeys) — v1, comando aparte, SIN tunear:** `/loconautics sabletrain physics`. El vagón
es cuerpo libre, cada bogey lo sujeta a la vía con un **muelle** (receta de `WheelMount`, §N del RESEARCH);
puede DESCARRILAR. Constantes K=150/C=25 a calibrar. El usuario dijo "mejor seguimos con el pin, deja el
físico aparte" → es secundario.

**⚠️ MIXINS DE ESTACIÓN HÍBRIDOS = FAIL-SAFE/efectivamente OFF:** `PhysicsAssemblyContext` lanzaba
`NoClassDefFoundError` desde el mixin en el modpack → envolvimos los redirects en try/catch para no romper el
ensamblaje normal de Create. Efecto: el **ensamblaje físico híbrido** ("Assemble as Physics Train") puede no
crear sub-levels. NO es bloqueante porque el all-sable lo reemplaza. (Si se quiere el híbrido, hay que
resolver ese classloader.)

**PROBLEMAS ABIERTOS (prioridad):**
1. **Analog Controller no conduce dentro del sub-level.** Monta (tras el fix de rango) pero subir power con
   teclas no mueve el tren. Sospecha: los BE no tickean en sub-levels (la decay/serverTick no corre) y/o el
   enrutado de teclas no llega. **HAY LOGS `[analog]` puestos** (sanity-disconnect, key fromCurrentUser) —
   NECESITAS leer `latest.log` mientras el usuario lo prueba para diagnosticar. (La cadena real:
   Analog Controller → redstone-link → Transmission → shaft → Bearing Axle RPM → velocidad; solo el bearing
   axle fija la velocidad del tren, ver §0a y RESEARCH §Y.)
2. ~~"Solo ensamblar si hay un bogey"~~ HECHO (`d87f932`): `/loconautics sabletrain` exige un
   `AbstractBogeyBlock` de Create en el cart (escaneo hasta el nivel del raíl). Interpretado como "el cart
   necesita un bloque bogey/ruedas"; si el usuario quería otra cosa, ajustar.
3. **"Se parte en trozos" al romper un bloque** = splitting de bloques flotantes de Sable
   (`FloatingBlockController`). Investigar desactivarlo para sub-levels de tren. (Romper un vagón ya NO tira el
   tren entero — el `SubLevelObserver` quita solo ese vagón.)
4. **Acople multi-vagón con constraint** (Fixed/Generic, RESEARCH §H.4) — pendiente; ahora los vagones se
   mantienen juntos solo por "misma velocidad" (loose).
5. ~~**Persistencia**~~ **IMPLEMENTADO 2026-06-09 (sin probar in-game todavía).** Nuevo `allsable/
   SableTrainStore` (SavedData global en el overworld, `loconautics_sable_trains`) + `allsable/
   SableTrainPersistence` (@EventBusSubscriber): serializa cada tren (id, dimensión, motion, y por coche el
   `subLevelId` + grafo + 2 `TravellingPoint` vía `write/read` + spacing + upNormal + **frame de referencia
   forward0** + localLead/localTrail). Persiste en spawn/addcar/clear, periódicamente (cada 200 ticks) y en
   `ServerStoppingEvent` (captura la posición final). Restaura en `ServerStarted`+tick: reintenta cada tick
   (ventana 600t) hasta que el grafo (`Create.RAILWAYS.trackNetworks.get(id)`) y el sub-level
   (`container.getSubLevel(uuid)`) estén cargados, reconstruye el `RailCarriage` (`RailCarriage.restore`, que
   re-inyecta forward0 para NO resetear la orientación a axis-aligned) y re-registra el `SableTrain`.
   **FIX clave de paso**: el `SubLevelObserver` de `SableTrainSpawner` ahora ignora `UNLOADED` y solo borra el
   coche en `REMOVED` (antes un unload de chunk/apagado tiraba el tren — y habría borrado el record en disco
   cada apagado). Jar `f65c5e5`. **Huérfanos previos** (sub-levels de antes de este cambio) NO tienen record →
   no se adoptan; probar con trenes nuevos. PENDIENTE: confirmar in-game (spawn → reiniciar → ¿sigue el tren
   recorriendo la vía?).
6. Tuning del modo físico (K/C).

---

## 0b. ACTUALIZACIÓN 2026-06-07 (contexto previo)
Cambios de esta sesión (todo pusheado a origin):
- **Gate A/B RESUELTO: el usuario eligió la Opción B (tren 100% propio)**, NO reusar el `Train` de Create.
  Solo se reutiliza el grafo de vías (`TrackGraph`/`TravellingPoint`). Fases 3-4 implican reimplementar
  navegación/señales/acoplamiento (ver §7).
- **Fase 1 + 2a construidas** (rama all-sable, commit `c32c869`; sin confirmar in-game aún):
  `allsable/RailFollower` (1 `TravellingPoint` recorriendo la vía, rebota en topes) y `allsable/RailCarriage`
  (2 bogeys rígidos → pose completa vía `RailPose`). Comandos en `allsable/RailDebug`:
  `/loconautics railtest [speed]` (Fase 1), `/loconautics railtest2 [spacing] [speed]` (Fase 2a: dibuja
  bogeys + centro + flecha de orientación), `/loconautics railtest clear`. Marcadores de partículas, sin
  sub-level/entidad todavía. Jar desplegado en `Skybound SMP/mods/` (md5 `6d3636…`, solo Fase 1/2a).
- **`feature/analog-controller` (bloque analog controller de Lycoris/MDLOP: GUI/red/modelos) integrado**:
  mergeado en `master` (fast-forward → `4da2b29`) y en `feature/all-sable-physics-train` (merge `61c0afa`,
  archivos disjuntos, compila). Se hizo con **merge directo + push** (no PRs: `gh` no instalado, sin token;
  además el repo NUNCA ha usado PRs — siempre commit directo). La rama all-sable fusionada compila.
- **Siguiente:** confirmar Fase 1/2a in-game; luego Fase 2b (mover un sub-level real con la pose), que necesita
  integración con el ensamblaje (Fase 3).

## 0. Qué es el proyecto (recordatorio de 1 párrafo)
Addon NeoForge 1.21.1 que convierte trenes de Create en **sub-levels físicos de Sable** que siguen las vías
de Create. Autores: Lycoris (usuario) + MDLOP. Repo `github.com:Kkkur/loconautics`. Carpeta local
`C:\Users\User\Desktop\Mod Aeronautics`. Fuente de referencia descompilado en `src/depend/{Sable,Aeronautics,Create}`.

## 1. WORKFLOW (idéntico, sin hot-reload)
- Build: `./gradlew build` (o `compileJava`). Jar en `build/libs/loconautics-1.0.0.jar`.
- Deploy: `cp build/libs/loconautics-1.0.0.jar "/c/Users/User/curseforge/minecraft/Instances/Skybound SMP/mods/"`
  y **verificar md5** (compilado == desplegado).
- Test: el usuario **reinicia Minecraft** (no hay hot-reload; los mixins se aplican al cargar; **pregunté y
  confirmé que NO se puede hot-reload** lo nuestro porque casi todo son mixins). Claude lee el log directo:
  `…/Skybound SMP/logs/latest.log` (servidor integrado = cliente+servidor en el mismo log).
- Commit/push: SOLO cuando el usuario lo diga. Acabar mensajes de commit con
  `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`. SSH ya configurado, push directo autorizado.
- El usuario está MUY cansado de reiniciar → **acertar a la primera**, batch de cambios, e instrumentar con
  logs (`[poses]`, `[ctrl]`, …) cuando no estés seguro: la AUSENCIA de un log también es dato.

## 2. ESTADO DE RAMAS (todo en origin)
| Rama | Commit | Qué es |
|---|---|---|
| `master` | `4da2b29` | Fix de colisión **+ analog-controller** (mergeado 2026-06-07). Base estable. |
| `backup/collision-fix-stable` | `e7696e5` | Copia de seguridad del fix de colisión puro (pre-analog). |
| `feature/sable-train-controls` | `2430d2a` | Enfoque **híbrido** de controles (WIP, medio-funciona — ver §4). |
| `feature/analog-controller` | `4da2b29` | Bloque "analog controller" (GUI/red/modelos) de Lycoris/MDLOP. Ya mergeado en master y all-sable. |
| `feature/all-sable-physics-train` | `61c0afa` | **AQUÍ AHORA.** Pivote "todo Sable" (§7) + Fase 1/2a + analog-controller. |
| `new-main` | — | preexistente del compa. |

---

## 3. ✅ LA COLISIÓN (RESUELTA — master `e7696e5`) — corrige §14.6 de HANDOFF.md

**§14.6 (constraint-motores) era FALSO/callejón.** La solución real:

**Mecanismo (leído del fuente de Sable, no teoría):**
- Colisión jugador↔sub-level = `SubLevelEntityCollision.collide` (CPU) → transforma los **bloques del plot**
  por **`subLevel.logicalPose()`**, interpolando `lastPose → logicalPose` por substeps
  (`mixin/entity/entity_sublevel_collision/EntityMixin` redirige `Entity.collide`).
- Raytrace (`mixin/clip_overwrite/BlockGetterMixin.clip`) **también** usa `logicalPose()`.
- → **`logicalPose` es la pose autoritativa de colisión Y raytrace.** El render usa `renderPose` (acoplado
  al vagón en `ClientSubLevelRenderMixin`). Las desacoplamos.

**Causa raíz del desfase ("caja gris ~0.64 abajo"):** el sub-level es cuerpo **dinámico**. El driver viejo
hacía `teleport` del logicalPose en `ServerTickEvent.Post` (DESPUÉS del physics tick) → la **gravedad lo
hundía** y la colisión leía la pose hundida.

**Fix (`server/tick/PhysicsTrainTickHandler`, registrado en `Loconautics.commonSetup`):** fijar logicalPose
a la pose del vagón **DENTRO del physics tick**: `SableEventPlatform.onPhysicsTick` (teleport + resetVelocity)
y `onPostPhysicsTick` (re-fija, quita el hundimiento del substep). Game tick = solo lifecycle. Log `[poses]`
con `err=(dx,dy,dz)` debe salir ≈(0,0,0). **Confirmado in-game: caminas EXACTO sobre el cuerpo visible.**

**Limitación pendiente (curvas):** `PhysicsTrainPose.orientationOf` usa `getRotationState()`, que es
**identidad en servidor** → en curvas el cuerpo de colisión queda axis-aligned (recto perfecto). FIX
documentado en §6.

---

## 4. 🟡 LOS CONTROLES (híbrido, rama `feature/sable-train-controls`) — medio-funciona, SIN cerrar

**Objetivo:** conducir clicando el bloque de **Train Controls que se VE** (cuerpo Sable), no la hitbox
invisible de la contraption de Create (que está dentro/desfasada).

**Por qué es difícil (confirmado por issues de VS, ver §5):** la `CarriageContraptionEntity` coexiste con
nuestro sub-level, con su propia interacción/colisión desfasada. Create conduce vía un **raycast cliente
propio** (`ContraptionHandlerClient.rightClickingOnContraptionsGetsHandledLocally`) contra los bloques de la
contraption (en el raíl), que `event.setCanceled(true)` al acertar cualquier bloque.

**Intentos (en orden, todos en la rama de controles):**
1. **Ray-shift** del raycast de Create por `-bodyOffset` → funcionó para apuntar, PERO Create cancela TODA
   interacción al acertar → **rompió cofres/colocar bloques** (= VS Create-Interactive issue #2). Revertido.
2. **Handler de `InteractionKeyMappingTriggered`** → no se registraba con `@EventBusSubscriber` → no disparaba.
3. **Handler de `PlayerInteractEvent.RightClickBlock`** (patrón swivel bearing, registrado explícito en
   `LoconauticsClient`): detecta el `ControlsBlock` en la pos del plot y reenvía a Create
   (`carriage.handlePlayerInteraction` + `ContraptionInteractionPacket`). Hallazgos al depurar con logs `[ctrl]`:
   - `getContaining(level, x, z)` (búsqueda por chunk del plot) **devuelve null en CLIENTE** → cambiado a
     buscar el sub-level físico cuyo **plot más cercano** a la pos (`physicsSubLevelIds()` +
     `container.getSubLevel(uuid)`). **Funcionó** (`sub=… dist2=9`, `carriage=idx0`, `handled=true`).
   - `RightClickBlock` dispara **dos veces** (mano principal + secundaria) → `handlePlayerInteraction`
     **alterna** (start/stop) → arrancaba y paraba. Filtrado a `InteractionHand.MAIN_HAND`.
   - Tras eso: arranca ("Now controlling") pero **se para al instante de nuevo**. Causa encontrada:
     `CarriageContraptionEntity.control()` (servidor) hace `if (!canInteractWithBlock(player, controles, 8)) return false`
     → `ControlsServerHandler` llama `stopControlling`. Ese chequeo mide distancia en **espacio-contraption**
     (raíl) y el jugador está en el cuerpo Sable. **Último intento: `ContraptionInteractRangeMixin` bypasea
     `canInteractWithBlock` para trenes físicos.** **EL USUARIO DIJO "no funciona" — SIN CONFIRMAR si ese
     bypass arregla o no (no llegó a pasar log tras ese build antes del pivote).**
4. **Mixins de apoyo creados en la rama de controles:**
   - `CarriageNoKinematicCollisionMixin`: `sable$shouldCollide()=false` para trenes físicos → **mata el
     collider cinemático REDUNDANTE de la contraption** (el "cuerpo invisible" / **partículas de choque en
     curvas** que reportó el usuario; venían de que el sub-level y el collider cinemático de la contraption
     ocupan el mismo espacio y Rapier los choca — visto en `RapierPhysicsPipeline.processCollisionEffects`).
   - `ContraptionHandlerClientSkipMixin`: el raycast de Create devuelve `null` para trenes físicos (no cancela
     → deja correr nuestro handler / interacción normal de cofres).

**Resumen del estado de controles:** muy cerca pero NO cerrado; el último corte conocido es
`canInteractWithBlock` (bypass aplicado, sin confirmar). **Si se retoma el híbrido**, el siguiente paso es
pasar log `[ctrl]` tras el bypass y, si sigue, buscar otro `return false` en `control()` o un doble-toggle
servidor. PERO ojo: **el usuario y Lycoris decidieron PIVOTAR a "todo Sable" (§7)**, que elimina la
contraption y por tanto TODO este problema de raíz.

---

## 5. MECANISMOS DE SABLE / CREATE (lo aprendido leyendo fuente + web)

- **Sable convierte TODA contraption de Create en physics body**: mixin
  `compatibility.create.contraptions.AbstractContraptionEntityMixin` la hace `KinematicContraption` y la
  auto-añade al pipeline (`sable$addToPipeline`). `sable$getPosition(partialTick)` = `anchor + rotate(centerOfMass)`.
  Sube sus bloques como collider cinemático SOLO si `sable$shouldCollide()` (de ahí el §4.4a).
  `ContraptionColliderMixin` (de Sable) redirige `collideEntities` por `logicalPose`.
- **`getContaining(level, x, z)` es por CHUNK del plot** (los bloques se almacenan en ~20481032). La
  contraption está en el chunk del raíl → `getContaining(contraption)` = null → los redirects de Sable son
  no-op para nuestra contraption-en-el-raíl. La colisión/clip usan `getAllIntersecting` (por bounds), no
  getContaining; por eso funcionan y getContaining no.
- **Interacción idiomática de bloques en sub-levels (Aeronautics):** NO usan la interacción de contraption de
  Create. Cada bloque interactivo (steering wheel, throttle, **swivel bearing**) responde a su `useItemOn`/
  `useWithoutItem` **normal**; Sable entrega el click al bloque del plot (cliente+servidor, red vanilla).
  También tienen `SimClickInteractions`/`InteractCallback` (handler de input cliente) que mira el bloque en
  `mc.hitResult.getBlockPos()` (coords del plot). Nuestro handler `RightClickBlock` es ese mismo patrón.
- **Conducir un tren de Create (flujo):** `ControlsInteractionBehaviour.handlePlayerInteraction` →
  `startControlling(localPos, player)` (TOGGLE: si ya controla, para) + cliente `ControlsHandler` (solo
  reenvía teclas con `ControlsInputPacket`; NO fija posición). El jugador conduce de pie (va montado por la
  colisión Sable). Servidor `ControlsServerHandler.tick` → `entity.control()`; si false → `stopControlling`.
- **Mapeo plot→contraption-local:** `localPos = plotPos - plot.getCenterBlock()` (el plotAnchor ↔ anchor de
  la contraption; verificado en `SubLevelBridge`: `worldBlock = anchor.offset(localPos)`).
- **Pasajeros/montar:** Sable arrastra al jugador que está encima vía `trackingSubLevel`/`inheritedMotion`
  (mixins `entity/entities_stick_sublevels` y `entity/entity_sublevel_collision`). Ya funciona (gracias al fix
  de colisión).
- **Desensamblar:** `TrainDisassembleMixin` redirige `entity.disassemble()` → `SubLevelDisassembler` mueve
  los bloques vivos del sub-level al mundo (aterriza el plotAnchor en `entity.position()`). Independiente del
  offset de render.
- **CURVAS (orientación) — método canónico:** para el RENDER usar `entity.getRotationState().asMatrix()` (la
  `Matrix3d` que Create usa para rotar bloques) → cuaternión JOML (la "verdad absoluta", sin riesgo de signos).
  Es **solo cliente** (identidad en servidor); para la COLISIÓN server-side, construir desde `entity.yaw`/`pitch`,
  que SÍ se fijan en servidor en `Carriage$DimensionalCarriageEntity.alignEntity` (Carriage.java ~L804):
  `yaw=atan2(diffZ,diffX)*180/π+180`, `pitch=-atan2(diffY,√(diffX²+diffZ²))*180/π`.

**Issues que confirman que esto es difícil hasta en VS maduro:** VS-2 #1500 (raycast desalineado en
estructuras físicas, sin resolver), Create-Interactive-Issues #2 (la bounding box de la contraption bloquea
la interacción de bloques — = nuestra rotura de cofres), Create #5868 (controles del tren pierden prioridad
vs el bloque detrás). DeepWiki útil: `deepwiki.com/ryanhcode/sable`,
`deepwiki.com/Creators-of-Aeronautics/Simulated-Project`.

## 5b. EL MOD "bearing" (linearbearing) que descargó el usuario — qué es y qué sirve
RAR extraído (era `C:\Users\User\Downloads\bearing.rar`; paquete `com.bearing.linearbearing`). **NO es un
tren siguiendo vías.** Es un addon Create+Sable+Aeronautics para **transmitir energía cinética (rotación)
entre el mundo y sub-levels físicos**: `MagneticPort` (raycast `level.clip` que atraviesa sub-levels y
transmite velocidad cinética a otro puerto), `TorsionalAnchor` (transmite rotación a través de un
`SpringBlockEntity` de Aeronautics, leído por **reflexión**), `LinearBearing` (al clicarlo con `useWithoutItem`
ensambla un bloque en un sub-level vía **reflexión** a `SimAssemblyHelper.assembleFromSingleBlock`, y activa
**CCD** `setCcdMotionThreshold`). Todo con reflexión porque no compilan contra Aeronautics; **nosotros SÍ
tenemos el source**, no necesitamos esos hacks. **Útil de ahí:** (1) `SimAssemblyHelper.assembleFromSingleBlock(level, pos, frontPos, …)`
= API limpia de Aeronautics para hacer un sub-level físico (devuelve `.subLevel()`/`.offset()`); (2) **CCD**
si el tren va rápido (no atravesar suelo/vías); (3) reconfirma el patrón `useWithoutItem`; (4) `SpringBlockEntity`
de Aeronautics = primitivo de constraint por fuerzas.

---

## 6. PENDIENTES del enfoque híbrido (por si se vuelve a él)
1. Cerrar controles (confirmar el bypass de `canInteractWithBlock`; ver §4).
2. Curvas: aplicar orientación real (§5, último bullet) en el pin del physics tick (servidor) y/o render.
3. Asientos de pasajeros (mismo patrón `RightClickBlock`/`useItemOn`).
4. Optimización (de Create-Interactive): guardar `subLevelId` como duck en `CarriageContraptionEntity` en vez
   de lookups por registro.
5. Verificar desensamblado tras todos los mixins.
6. Actualizar `HANDOFF.md` §14.6 (es FALSO).

---

## 7. 🚧 EL PIVOTE EN CURSO: "TODO SABLE" (rama `feature/all-sable-physics-train`, AQUÍ)

**Decisión de Lycoris+usuario (literal):** cambiar el sistema a "todo Sable" para que el tren **sea un
physics object que sigue las vías**, eliminando la `CarriageContraptionEntity` (origen de TODOS los
conflictos). Hacerlo AHORA como base, antes de añadir más cosas (luego costaría reconstruir). En rama aparte
para no perder nada.

**Lo ya hecho en esta rama:**
- **`DESIGN_all_sable_train.md`** (commit `6b0e58b`) — diseño completo. LÉELO (actualizado con el gate B y el
  estado de fases).
- **`allsable/RailPose.java`** (`6b0e58b`) — núcleo: pose del vagón (pos + orientación) desde dos
  `TravellingPoint`, espejando `Carriage.alignEntity`. Compila → viabilidad validada.
- **`allsable/RailFollower.java` + `RailCarriage.java` + `RailDebug.java`** (commit `c32c869`, 2026-06-07) —
  **Fase 1 + 2a** (ver §0b). Recorren la vía y derivan la pose; comandos `/loconautics railtest[2]`. Sin
  confirmar in-game todavía.

**Hallazgo de viabilidad (clave):** la matemática de seguir vías está en utilidades de Create **independientes
de la contraption**: `TravellingPoint.travel(graph, distancia, ITrackSelector, …)` avanza por el carril
(curvas/agujas/señales/portales) y `getPosition(graph)` da la pose. `TrackGraphHelper.getGraphLocationAt(level,
pos, dir, axis)` localiza un punto en el grafo. `TrackEdge.getPosition(graph,t)`/`getNormal(graph,t)` para
pose+banking. **Reutilizamos el motor de raíles de Create; solo cambia la REPRESENTACIÓN (contraption →
sub-level físico).** ("Todo Sable" NO es reinventar las vías — siguen siendo de Create.)

**DECISIÓN (gate) — RESUELTA 2026-06-07: el usuario eligió la Opción B.**
- **Opción B (ELEGIDA):** tren 100% propio sobre `TravellingPoint` (independencia total de Create; hay que
  reimplementar navegación/señales/estaciones/acoplamiento = enorme, en Fases 3-4). Solo se reutiliza el
  grafo de vías (`TrackGraph`/`TrackEdge`), NO el `Train` de Create.
- (Descartada) Opción A: reusar `Train`/`Carriage`/navegación de Create cambiando solo la representación.
  Era la recomendada por menos trabajo, pero el usuario priorizó la independencia total.

**Plan por fases (en el DESIGN):** Fase 0 (doc+esqueleto, HECHO) → **Fase 1 = mover un cuerpo de debug por
una vía a velocidad constante** (recta+curva; **igual en A y B**, así que se puede construir ya) → Fase 2
(pose completa de dos bogeys → mover el sub-level real, cinemático con velocidad) → Fase 3 (integrar con el
ensamblaje, ocultar/no-spawnear la contraption) → Fase 4 (controles + multi-vagón + desensamblar).

**Para mover el sub-level (Fase 2+):** lo Sable-nativo es registrarlo como `KinematicContraption` y exponer
`sable$getPosition/Orientation(partialTick)` desde los `TravellingPoint` → Sable calcula velocidad y arrastra
suave (visto en `RapierPhysicsPipeline.updateContraptionPoses`). Alternativa de arranque: el pin del physics
tick que YA funciona (master), alimentado por nuestros `TravellingPoint`.

**SIGUIENTE ACCIÓN sugerida:** confirmar in-game las Fases 1/2a (`/loconautics railtest` y `railtest2`); si la
pose va bien, atacar la **Fase 2b** (mover un sub-level real con la pose), que requiere integración con el
ensamblaje (Fase 3). Recordar: Opción B → reimplementar navegación/acoplamiento, sin apoyarse en el `Train`.

---

## 8. MAPA DE ARCHIVOS (nuestro mod, `src/main/java/com/lycoris/loconautics/`)
- `Loconautics.java` (@Mod; registra el driver de colisión en commonSetup), `LoconauticsClient.java`
  (@Mod CLIENT; en la rama de controles registra el handler de controles).
- `server/tick/PhysicsTrainTickHandler.java` — **el fix de colisión** (pin en physics tick). En master.
- `core/PhysicsTrainPose.java` (`orientationOf`), `core/PhysicsTrainTag.java`, `core/LoconauticsConstants.java`.
- `server/PhysicsTrainRegistry.java` (SavedData global), `client/ClientPhysicsTrainRegistry.java` (mirror).
- `server/assembly/`: `StationBlockEntityAssembleMixin`(redirect removeBlocksFromWorld→captura),
  `SubLevelBridge`(crea el sub-level), `PhysicsAssemblyOrchestrator/Context`, `SubLevelDisassembler`.
- `mixin/`: `ContraptionColliderMixin`, `ContraptionColliderSkipMixin`, `EntityNoContraptionCollisionMixin`,
  `MovingInteractionBehaviourMixin`, `TrainDisassembleMixin`, `StationBlockEntityAssembleMixin`.
  CLIENT: `ClientSubLevelRenderMixin`(acopla render al vagón), `CarriageBogey*Mixin`(oculta bogey fantasma §13),
  `ClientContraptionRenderMixin`, `AssemblyScreenMixin`(botón).
- **Solo en rama controles:** `client/PhysicsTrainControlsInteraction`, `mixin/CarriageNoKinematicCollisionMixin`,
  `mixin/ContraptionInteractRangeMixin`, `mixin/client/ContraptionHandlerClientSkipMixin`.
- **Solo en rama all-sable:** `allsable/RailPose`, `DESIGN_all_sable_train.md`.
- Mixins se registran en `src/main/resources/loconautics.mixins.json` (`required:true`, `defaultRequire:1`).

## 9. ENTORNO (de HANDOFF.md §1, sin cambios)
NeoForge 21.1.x / MC 1.21.1 / Java 21. Create 6.0.10-280 (slim, maven). Sable 1.2.2 (JAR local en run/mods).
Aeronautics bundled 1.2.1 (JAR local). Flywheel 1.0.6. JARs con copyright en `run/mods/` (gitignored). El
modpack real de test (Skybound SMP) tiene Sinytra Connector + 200 mods; por eso se prueba ahí y no en runClient.
`javap` para verificar firmas sin adivinar (`/c/Program Files/Java/jdk-21/bin/javap.exe`, no está en PATH).
Extraer RARs: `/c/Program Files/WinRAR/UnRAR.exe x`.

---
## 10. TL;DR para arrancar rápido
1. Colisión: ✅ resuelta (master). 2. Controles híbridos: 🟡 casi, en su rama, bloqueados por
`canInteractWithBlock` (bypass sin confirmar). 3. **Pivote a "todo Sable"** (rama
`feature/all-sable-physics-train`): tren = sub-level físico movido por `TravellingPoint` de Create, sin
`CarriageContraptionEntity`. 4. **Gate RESUELTO: Opción B (tren 100% propio)** — solo se reusa el grafo de
vías, no el `Train` de Create. 5. **Fase 1 + 2a construidas** (`allsable/RailFollower`/`RailCarriage`/
`RailDebug`, comandos `/loconautics railtest[2]`) — sin confirmar in-game. 6. **analog-controller mergeado**
en master (`4da2b29`) y all-sable (`61c0afa`). 7. Siguiente: confirmar Fase 1/2a in-game → Fase 2b (mover
sub-level real con la pose). Ver §0b.
