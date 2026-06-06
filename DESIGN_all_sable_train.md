# Diseño — Tren "todo Sable" (physics object siguiendo vías)

> Rama `feature/all-sable-physics-train`, partiendo de `master` (fix de colisión bueno).
> Objetivo (Lycoris + usuario): que el tren sea un **objeto físico de Sable que sigue las vías**, en vez de
> una contraption de Create renderizada/colisionada como sub-level. Hacerlo AHORA como base, antes de añadir
> más cosas encima.

---

## 0. Por qué cambiar (qué problema resuelve)

Todos los dolores de cabeza (controles que no enganchan, doble collider cinemático, partículas de choque en
curvas, hitbox de interacción desfasada) **vienen de la `CarriageContraptionEntity`**: es una entidad de
Create que coexiste con nuestro sub-level, con su propia colisión/interacción/render, desfasada del cuerpo
visible. Mientras exista, peleamos contra ella.

La idea "todo Sable": **eliminar la `CarriageContraptionEntity`** y que el tren sea SOLO un sub-level de
Sable, movido directamente por la posición en el carril.

---

## 1. Hallazgo clave de viabilidad (la matemática de vías es reutilizable)

La parte difícil de un tren —seguir el carril por curvas, agujas, señales— está en clases **utilitarias** de
Create que NO dependen de la contraption:

- **`TravellingPoint`** (`content.trains.entity`): un punto que viaja por el grafo de vías.
  - `double travel(TrackGraph graph, double distance, ITrackSelector sel, …)` → avanza el punto `distance`
    metros por el carril (gestiona saltar de arista, curvas, señales, portales).
  - `Vec3 getPosition(TrackGraph graph)` → posición de mundo SOBRE el carril.
  - `getPositionWithOffset(graph, offset, flip)` = `edge.getPosition(graph, t)` + `edge.getNormal(graph,t)`.
- **`TrackEdge`** (`content.trains.graph`): `getPosition(graph, t)` (curva/bezier) y `getNormal(graph, t)`
  (vector "arriba"). Con dos posiciones (leading/trailing) sacas la **orientación** (forward = tangente).
- **`TrackGraph`** = la red de vías (es de Create/Steam'n'Rails; la seguimos usando tal cual).

→ Un cuerpo de Sable puede seguir el carril manteniendo 1–2 `TravellingPoint` y, cada tick, `travel()` +
`getPosition()` para sacar la pose. **Reutilizamos el motor de raíles de Create sin su contraption.**

**Importante:** "todo Sable" NO significa reimplementar las vías. Las vías y el grafo siguen siendo de
Create. Lo que quitamos es la **representación del tren** (contraption → sub-level físico).

---

## 2. Arquitectura propuesta

### 2.1 Componentes
- **`SableTrain`** (nuestro, server): un tren físico. Contiene, por vagón:
  - El `ServerSubLevel` (los bloques del vagón) — ya sabemos crearlo (`SubLevelBridge` /
    `SubLevelAssemblyHelper`, o la API limpia de Aeronautics `SimAssemblyHelper.assembleFromSingleBlock`).
  - Dos `TravellingPoint` (bogey leading/trailing) sobre el `TrackGraph`.
  - Velocidad / estado (acelerar, frenar, dirección).
- **Tick del tren** (server, cada tick): `travel(speed)` los puntos → calcular pose del vagón (pos + orient
  desde leading/trailing) → mover el sub-level a esa pose **con velocidad** (cinemático Sable, ver §2.2).
- **Render**: el sub-level se dibuja solo (Sable). Reutilizamos `ClientSubLevelRenderMixin` si hace falta
  acoplar al partialTick, o dejamos la interpolación de Sable.
- **Controles**: bloque de controls reacciona a su `useWithoutItem` (patrón swivel bearing, ya validado) y
  ajusta la velocidad del `SableTrain`. SIN `ContraptionInteractionPacket` ni handlePlayerInteraction de
  Create (no hay contraption). Mucho más limpio.

### 2.2 Cómo mover el sub-level (cinemático con velocidad)
La forma Sable-nativa: registrar el sub-level (o un wrapper) como **`KinematicContraption`** y exponer
`sable$getPosition(partialTick)` / `sable$getOrientation(partialTick)` desde los `TravellingPoint`
(interpolando con partialTick). Sable calcula la velocidad del delta de pose y la sube a Rapier → **arrastra
entidades suave, sin stutter, colisión exacta** (lo que ya vimos en `RapierPhysicsPipeline.updateContraptionPoses`).
Alternativa simple de arranque: el "pin en physics tick" que YA funciona (master), pero alimentado por
nuestros `TravellingPoint` en vez de por la entidad de Create.

### 2.3 Qué se elimina
- `CarriageContraptionEntity` (no se crea, o se descarta tras leer sus bloques).
- Toda la maraña de mixins de supresión (`MovingInteractionBehaviour`, `ContraptionCollider*`,
  `ContraptionHandlerClientSkip`, `canInteractWithBlock`…) — ya no hace falta suprimir una contraption que no
  existe.

---

## 3. Los retos reales (con honestidad)

1. **Ensamblaje:** interceptar el ensamblaje de la estación de Create para, en vez de spawnear
   `CarriageContraptionEntity`, crear nuestro `SableTrain` (sub-level + `TravellingPoint` colocados en el
   carril donde estaba el tren). Hay que inicializar los `TravellingPoint` en la arista/posición correcta
   (Create lo hace en `Carriage`/`DimensionalCarriageEntity`; replicar esa colocación).
2. **El tick del tren:** reimplementar el avance (lo que hace `Train.tick` + `Carriage.travel`): `travel()`
   los puntos, mantener el espaciado entre bogeys (rígido), parar en topes/señales. Create tiene MUCHO aquí
   (acoplamiento entre vagones, navegación, señales, estaciones). Decidir qué reusar:
   - **Opción A (reusar `Train`):** dejar que Create gestione `Train`/`Carriage`/`TravellingPoint`/navegación
     y SOLO sustituir la representación (entidad → sub-level). Menos reescritura; el `Train` sigue siendo de
     Create pero sin entidad de contraption visible. **Recomendada para empezar.**
   - **Opción B (tren propio):** `SableTrain` gestiona sus propios `TravellingPoint` y velocidad, ignorando
     `Train` de Create. Máximo control, pero hay que reimplementar navegación/señales/acoplamiento (enorme).
3. **Controles/conducción:** ajustar velocidad desde el bloque de controls (fácil con Opción A: llamar a la
   API de control del `Train` de Create; o con B: setear la velocidad de nuestro tren).
4. **Multi-vagón:** acoplamiento rígido entre vagones (distancia constante). Create lo resuelve; con Opción A
   lo heredamos.
5. **Desensamblar:** devolver los bloques del sub-level al mundo (ya tenemos `SubLevelDisassembler`).
6. **CCD** (`setCcdMotionThreshold`) si va rápido, para no atravesar suelo/vías (visto en el mod "bearing").

---

## 4. Plan por fases (incremental, cada fase testeable)

> Filosofía: que CADA fase arranque y se pueda probar; no un big-bang.

- **Fase 0 (este doc + esqueleto).** Crear paquete `allsable/` con las clases vacías y el plan. ✅ HECHO.
- **Fase 1 — Prueba de concepto del seguimiento de carril.** ✅ HECHO (sin confirmar in-game aún). Un solo
  `TravellingPoint` (`allsable/RailFollower`) que avanza a velocidad constante; marcador de partículas END_ROD
  que se mueve a `point.getPosition()`, rebotando en topes. Comando `/loconautics railtest [speed]`
  (`allsable/RailDebug`). Objetivo: **ver un cuerpo recorrer la vía** (recta y curva).
- **Fase 2a — Pose completa del vagón (sin sub-level).** ✅ HECHO (sin confirmar in-game). Dos
  `TravellingPoint` (bogeys) con espaciado rígido (`allsable/RailCarriage`) → pos + orientación vía
  `RailPose`. Comando `/loconautics railtest2 [spacing] [speed]`: dibuja bogey delantero (FLAME), trasero
  (SOUL_FIRE_FLAME), centro (END_ROD) y una flecha de orientación (CRIT). Valida la matemática de pose en
  aislamiento.
- **Fase 2b — Mover el sub-level real con esa pose** (cinemático con velocidad). Caminar encima en
  movimiento. Depende de tener un sub-level (integración con el ensamblaje, Fase 3).
- **Fase 3 — Integración con el ensamblaje (Opción A).** Al ensamblar en la estación: crear el sub-level
  (ya lo hacemos) PERO alimentar su pose desde los `TravellingPoint`/`Carriage` de Create, y ocultar/neutralizar
  la `CarriageContraptionEntity` (o no spawnearla). Reutilizar el `Train` de Create para velocidad/navegación.
- **Fase 4 — Controles + multi-vagón + desensamblar.** Conducir vía bloque de controls; acoplar vagones;
  cerrar el ciclo de desensamblado.

---

## 5. Decisión pendiente para Lycoris/usuario

**RESUELTO (2026-06-06): el usuario eligió la Opción B (tren 100% propio).**
- **B (tren 100% propio sobre `TravellingPoint`):** independencia total de Create (sin reusar su `Train`).
  Implica reimplementar navegación/señales/estaciones/acoplamiento (Fases 3-4 = mucho trabajo), pero el
  usuario prioriza no depender de las internals de Create.
- (Descartada) A (reusar el `Train` de Create): era mi recomendación por menor trabajo, pero el usuario
  optó por la independencia total.

Las Fases 1 y 2a (carril + pose) son **iguales** en A y B y ya están construidas.

---

## 6. Estado de ramas
- `master`: fix de colisión (bueno). Intacto.
- `feature/sable-train-controls`: enfoque híbrido (controles WIP) — guardado, por si volvemos.
- `feature/all-sable-physics-train`: **esta**. Experimento del nuevo sistema.
