# Análisis de Create-Interactive (Valkyrien Skies) → adaptación a Sable/Aeronautics

> Investigación del repo https://github.com/ValkyrienSkies/Create-Interactive (multi-loader, Java
> mixins + lógica Kotlin con patrón `mixin_logic`). Create-Interactive resuelve EXACTAMENTE nuestro
> problema (trenes de Create como objetos físicos) pero con barcos de VS. Este doc mapea cada técnica
> a su equivalente en Sable. **Léelo junto a HANDOFF.md.**

---

## 0. Idea central de Create-Interactive (CI)

Cada **carriage (vagón)** se convierte en un **ship de VS** (= nuestro **sub-level de Sable**). Modelo
**híbrido bidireccional** controlado por `isTrainDerailed`:

- **En vías (NO descarrilado):** Create calcula la posición del tren (vías, señales). El ship **sigue**
  al carriage de forma **cinemática** vía un `ServerShipTransformProvider` (el ship es `isStatic=true`).
  → **es nuestro modelo (Física tipo B).**
- **Descarrilado:** el `transformProvider` devuelve `null` → la física de VS toma el control del ship,
  y el **carriage sigue al ship** (se invierte el acoplamiento). Esto es el "modo físico real" que
  nosotros dejamos para el futuro.

Para v1 nosotros solo necesitamos el camino "en vías" (cinemático).

---

## 1. Cómo el ship SIGUE al carriage (cinemático) — resuelve nuestro "movimiento raro"

**Archivo:** `CreateInteractiveUtil.kt → updateShipShadow()` + `posRotToShipTransform()`.

```kotlin
serverShip.transformProvider = object : ServerShipTransformProvider {
    override fun provideNextTransformAndVelocity(prev, cur): NextTransformAndVelocityData? {
        if (isTrainDerailed(entity)) return null   // física toma el control
        // velocidad lineal = (posNueva - posAnterior) * 20  (20 TPS)
        val vel = transform.positionInWorld.sub(prevPos).mul(20.0)
        // omega (velocidad angular) = 2 * parte vectorial de (rotNueva * rotAnterior⁻¹) * 20
        val rotDiff = transform.shipToWorldRotation.difference(prev.shipToWorldRotation).normalize()
        val omega = Vector3d(rotDiff.x*2, rotDiff.y*2, rotDiff.z*2)...mul(20.0)
        return NextTransformAndVelocityData(transform, vel, omega)
    }
}
serverShip.isStatic = true   // no afectado por fuerzas físicas
```

**LO IMPORTANTE:** CI **provee velocidad lineal y angular (omega)** además de la pose. Eso permite que
el cliente **interpole/extrapole suave** entre ticks. **Nosotros hacemos `teleport` + `resetVelocity`
(velocidad 0) cada tick → el cuerpo NO interpola y "salta"/se ve raro.** ESTE es casi seguro el origen
del "movimiento raro depende de si va adelante/atrás".

### Equivalente en Sable — OJO: `KinematicContraption` NO es lo que parecía
Al investigar a fondo: `dev.ryanhcode.sable.api.sublevel.KinematicContraption` **NO** es el equivalente
del transform provider. Es para contraptions **DENTRO** de un sub-level (`plot.getContraptions()`
devuelve `ObjectCollection<KinematicContraption>`), y exige objetos internos (`MassTracker`,
`FloatingClusterContainer`, `BlockGetter`) imposibles de crear desde cero sin la fuente de Sable.
`ServerSubLevel` **no** lo implementa (implementa `PhysicsPipelineBody`). **Descartado.**

**La adaptación FIEL de CI es proveer velocidad al `ServerSubLevel` que ya tenemos.** CI tampoco usa un
tipo de cuerpo aparte: mantiene su `ServerShip` y le da `(transform, velocidad lineal, omega)` cada tick
vía el transform provider. Nuestro equivalente es:
```java
// PhysicsPipeline (existe y la impl concreta de Sable funciona — resetVelocity se apoya en él):
default void addLinearAndAngularVelocity(PhysicsPipelineBody body, Vector3dc linVel, Vector3dc omega);
```
**IMPLEMENTADO (commit pendiente):** en `PhysicsTrainTickHandler`, en vez de `resetVelocity` tras el
`teleport`, ahora: `resetVelocity` (para SET, no acumular) + `addLinearAndAngularVelocity(body, linVel,
omega)` donde `linVel = (target - prevTarget) * 20` y `omega` = parte vectorial de la diferencia de
cuaterniones × 2, sign-corregida, × 20 (fórmula exacta de CI `updateShipShadow`). Se guarda la pose
anterior por sub-level en un `Map<UUID,double[7]>` (`PREV_POSE`), limpiado al desensamblar. Esto da al
cliente velocidad para **interpolar suave** entre ticks → debería arreglar el "movimiento raro".

---

## 2. Orientación del carriage — NO reconstruir el cuaternión a mano

**Nuestro bug actual:** reconstruimos `Q = RotY(-yaw)·RotZ(pitch)·RotY(initYaw)` a mano (frágil).

**Lo que hace CI** (`CreateInteractiveUtil.getContraptionPosRot`): usa el **`ContraptionRotationState`
interno de Create** (que YA encapsula la rotación del vagón) y lo convierte a cuaternión:

```kotlin
val rot = entity.rotationState.ci$getRotationQuaternion(Quaterniond())   // canónico
val pos = entity.anchorVec.toJOML().add(0.5, 0.5, 0.5)
```

La conversión canónica (`MixinContraptionRotationStateLogic`):
```kotlin
dest.identity()
   .rotateZYX(toRad(zRot), toRad(yRot), toRad(xRot))   // campos del ContraptionRotationState
   .rotateLocalY(toRad(rotState.yawOffset))
   .normalize()
```
…o más robusto aún: **`rotationState.asMatrix()`** (la `Matrix3d` que Create USA para rotar los
bloques) → convertir a cuaternión JOML (`Matrix3d → Matrix3d JOML → Quaterniond`). Eso es la verdad
absoluta, sin riesgo de signos.

**Para render interpolado** (`getContraptionPosRotForRender`): `slerp(prevRot, curRot, partialTick)` +
lerp de `anchorVec`/`prevAnchorVec`. Para nosotros, esto lo da gratis `KinematicContraption` con
partialTick.

**Acción:** reemplazar nuestro `orientationOf` manual por la lectura de `entity.getRotationState()`
(via accessor mixin al campo `rotationState`) → `asMatrix()` → quaternion. Elimina todo el riesgo de
yaw/pitch/initYaw/handedness.

---

## 3. Ocultar el render del vagón ORIGINAL de Create (estructura)

CI suprime el render original con VARIOS hooks coordinados, **todos condicionados a
`doesContraptionHaveShipLoaded(contraption)`**:

| Hook | Mixin/método | Qué hace |
|------|-------------|----------|
| Estructura (modelos de bloque, Flywheel) | `MixinClientContraption.getRenderedBlocks` → `wrapGetRenderedBlocks` | devuelve `RenderedBlocks(AIR, [])` vacío |
| Estructura (modo inmediato SBB) | `MixinContraptionEntityRenderer.buildStructureBuffer` (RETURN) → `postBuildBuffer` | devuelve buffer vacío |
| Setup estructura | `ClientContraption.setupRenderLevelAndRenderedBlockEntities`/`setupStructure` → cancel | — |
| Block-entities a renderizar | `ClientContraption.getAndAdjustShouldRenderBlockEntities` → BitSet vacío | — |
| **Visuales hijos de Flywheel** | **`MixinContraptionVisual.setupVisualizer` (HEAD) → cancel** | **no crea NINGÚN visual de BE hijo (incluye bogey BE)** |

**→ NOS FALTA el último (`ContraptionVisual.setupVisualizer` cancel).** Nosotros solo cancelamos
`setupRenderLevelAndRenderedBlockEntities` en `ClientContraption`, que NO impide que `setupVisualizer`
cree los visuales hijos. Por eso el bogey BE (y posibles otros) sigue renderizándose bajo Flywheel.

---

## 4. Bogeys — la solución correcta (NUESTRO bloqueo actual)

CI NO oculta los bogeys: los **re-renderiza pegados al ship**. Dos partes:

### (a) Suprimir el visual de Flywheel del bogey BE
- **`MixinVisualManager` → `@Mixin(BlockEntityStorage.class)` (Flywheel)**, inject en
  `willAccept(BlockEntity)` HEAD cancellable → **`false`** si el BE es un `AbstractBogeyBlockEntity`
  (o actor) que pertenece a un ship. Esto impide que Flywheel cree el `BogeyBlockEntityVisual`.
  `BlockEntityStorage` es `dev.engine_room.flywheel.lib.visual...` (storage de visuales de BE).
- **+ `MixinContraptionVisual.setupVisualizer` cancel** (§3) cubre los BE hijos del contraption.

### (b) Re-renderizar el bogey animado siguiendo al ship
- **`MixinCarriageContraptionEntityRenderer` → inject `translateBogey` HEAD cancellable**
  (`remap=false`). Si el carriage es un ship: recalcula la transform del bogey con la rotación/escala
  del ship (`translateBogeyWithShip`) y `ci.cancel()`. El bogey **sigue dibujándose** (BogeyStyle.render
  corre después) pero pegado a la física.
  - **OJO Flywheel:** en Create 6.0.10-280 el render inmediato de bogeys se SALTA bajo Flywheel
    (`lambda$render$1` offset 43: `if supportsVisualization goto skip`). CI funciona porque su versión
    de Create/Flywheel llama a `translateBogey` igualmente, o porque desactivan visualización del BE.
    **Verificar en nuestra versión:** si `translateBogey` NO se llama bajo Flywheel, hay que forzar el
    bogey por otro camino (p.ej. un visual propio, o desactivar Flywheel para el carriage).
- **`MixinCarriageBogey.updateCouplingAnchor`** → redirige los anchors de acople (las cadenas entre
  vagones) a coords del ship. Análogo.

### Decisión para Sable
Nuestro sub-level **NO contiene los bloques de bogey** (Create los consume como `CarriageBogey`, no van
en `contraption.getBlocks()`). Así que:
- **Opción 1 (simple):** ocultar bogeys del todo (cancel `setupVisualizer` + nuestro
  `CarriageBogeyRenderMixin` que cancela el render inmediato). El tren queda **sin ruedas visibles**.
- **Opción 2 (como CI, recomendado):** re-renderizar el bogey siguiendo la pose del sub-level
  (equivalente a `translateBogeyWithShip` pero con `subLevel.logicalPose()` en vez de `ship.transform`).
  Así hay ruedas que giran y siguen el tren. Es lo que da el acabado bueno.

---

## 5. Ensamblado (crear el "ship"/sub-level)

**`CreateInteractiveUtil.createShipForContraption`:**
1. Ordena bloques: no-frágiles primero, frágiles después (como `Contraption.addBlocksToWorld`).
2. `createNewShipAtBlock` → reserva un chunk-claim (centro del ship), análogo a nuestro **plot** de
   Sable (`getCenterBlock`).
3. Coloca cada bloque en `shipCenter + localPos` con `setBlock` + copia el BlockEntity (NBT, ajustando
   x/y/z y `LastKnownPos`). → es lo que hace `SubLevelAssemblyHelper.assembleBlocks` por nosotros.
4. `attemptTrainRelocation` (reubica el tren sobre el ship) + `serverShip.isStatic = true`.

Nuestro `SubLevelBridge` ya hace el equivalente vía Sable. **Diferencia clave:** CI guarda
`shadowShipId` en el carriage entity (`AbstractContraptionEntityDuck.ci$getShadowShipId`) — un **puente
bidireccional carriage↔ship**. Nosotros guardamos `subLevelId` en el `PhysicsTrainTag` (servidor) y
`ClientPhysicsTrainRegistry` (cliente). Equivalente, aunque CI lo tiene directo en la entidad (más
cómodo para los hooks de render). **Considerar añadir el subLevelId como campo/duck en el
CarriageContraptionEntity** para que los mixins de render lo resuelvan sin buscar en un registro.

---

## 6. Acoplamiento entre vagones (solo modo físico/descarrilado)

`MixinCarriageContraptionEntityLogic.preTick`: añade un **`VSDistanceJoint`** entre el ship de cada
vagón y el del vagón delantero, anclado en las posiciones de los bogeys (con offset de "car hook").
Es física real para mantener el tren unido al descarrilar. **Equivalente Sable:**
`api.physics.constraint.*` (FixedConstraint/GenericConstraint/RotaryConstraint — están en
`src/depend/Sable/`). **Futuro**, no para v1 cinemático.

---

## 7. Otros hooks útiles de CI (referencia)

- **Colisión/interacción:** CI hace que el carriage entity no colisione (lo hace el ship). Nosotros ya
  lo hacemos con `ContraptionColliderMixin` (del compañero).
- **Wrench/relocación de tren:** `MixinMinecraftLogic.wrapUseItemOn` reordena la interacción para que el
  wrench reubique el tren DESPUÉS de la interacción con bloques, transformando el rayo al espacio del
  ship. Útil cuando queramos que el wrench funcione sobre el tren físico.
- **getViewXRot/getViewYRot → 0** cuando hay ship (`MixinOrientedContraptionEntityLogic`): anulan la
  rotación de Create en la entidad para que SOLO la física rote. (En modo descarrilado.)
- **`applyRotation`/`reverseRotation`** redirigidos a la rotación del ship: para passengers/cámara.

---

## 8. PLAN DE ACCIÓN concreto para Sable (orden sugerido)

1. **Movimiento suave (la queja principal):** ✅ HECHO — proveer velocidad+omega al `ServerSubLevel`
   (§1). (`KinematicContraption` se investigó y se descartó: no es el equivalente.) Pendiente de probar.
2. **Orientación robusta:** reemplazar `orientationOf` manual por `entity.getRotationState().asMatrix()`
   → quaternion (§2). Necesita un `@Accessor`/`@Invoker` para `rotationState`/`getRotationState()`.
3. **Bogeys:** (a) cancelar `ContraptionVisual.setupVisualizer` para trenes físicos (§3, nos falta);
   (b) suprimir el visual del bogey BE vía `BlockEntityStorage.willAccept` si hace falta (§4a);
   (c) decidir Opción 1 (ocultar) u Opción 2 (re-render siguiendo el sub-level, como `translateBogey`).
4. **Puente carriage→subLevelId** como duck en el entity (§5) para simplificar los mixins de render.
5. (Futuro) Descarrile + constraints Sable (§6) para física real.

### Mapa rápido VS → Sable
| Create-Interactive (VS) | Loconautics (Sable) |
|---|---|
| `ServerShip` / `ClientShip` | `ServerSubLevel` (o `KinematicContraption`) |
| `ship.getChunkClaimCenterPos` | `subLevel.getPlot().getCenterBlock()` |
| `ship.transform.shipToWorldRotation` | `subLevel.logicalPose().orientation()` |
| `ServerShipTransformProvider` (provee pose+vel) | `KinematicContraption.sable$getPosition/Orientation(partialTick)` |
| `serverShip.isStatic = true` | cuerpo cinemático (no simulado) |
| `shadowShipId` en el entity | `subLevelId` (en tag/registry; considerar duck en entity) |
| `BlockEntityStorage.willAccept` (Flywheel) | mismo (Flywheel es común) |
| `ContraptionVisual.setupVisualizer` cancel | mismo |
| `VSDistanceJoint` | `api.physics.constraint.*` de Sable |

---

## 8.bis ESTADO ACTUAL (2026-05-31 noche) — diagnóstico en vivo

Implementado y probado in-game (commit de este push):
- **Velocidad para movimiento suave** (`PhysicsTrainTickHandler`): `addLinearAndAngularVelocity` con
  `vel = Δpos*20`, `omega` = fórmula CI. Pendiente confirmar si suaviza (el usuario aún no nota cambio).
- **`ContraptionVisualSetupMixin`**: cancela `ContraptionVisual.setupVisualizer` para trenes físicos
  (patrón seguro de CI, extiende `AbstractEntityVisual`, sin `@Shadow`). **Aplica sin crashear.**
- Logs de diagnóstico `[bogey-inv]` (invalidador) y `[bogey-cancel]` (mixin).

**HALLAZGO CLAVE del diagnóstico (importante para seguir):**
- `[bogey-inv] matchedEntities=1 invalidated=true` → el invalidador SÍ encuentra el vagón y llama
  `resetRenderLevel`. **PERO cero `[bogey-cancel]`** → `setupVisualizer` NUNCA se llama tras invalidar.
- Causa: `resetRenderLevel` limpia `renderedBlockEntities` y re-llama
  `setupRenderLevelAndRenderedBlockEntities`, que NUESTRO `ClientContraptionRenderMixin` CANCELA →
  lista vacía → `setupChildren` no itera nada → `setupVisualizer` no se llama. Conclusión:
  **los bogeys visibles NO son hijos del contraption fantasma.**
- Verificado en `CarriageContraption.capture` (línea 185): Create **mete los bloques de bogey en el
  contraption** (`instanceof AbstractBogeyBlock → ++bogeys` y se capturan). Así que **van en nuestro
  sub-level**. Los bogeys que se ven son **los bloques de bogey del PROPIO sub-level**, renderizados
  por Flywheel vía `BlockEntityStorage` (la MISMA vía que CI suprime con `willAccept`), NO vía
  `ContraptionVisual.setupVisualizer`.

**PRÓXIMO PASO para bogeys (lo que falta):** portar el hook de CI
`MixinVisualManager → @Mixin(BlockEntityStorage) willAccept(BlockEntity)→false` para los
`AbstractBogeyBlockEntity` que pertenezcan a un sub-level físico (CI lo decide con
`getShipManagingPos(pos)`; nuestro equivalente: comprobar si el BE está en el plot de un sub-level
nuestro). Opcionalmente re-renderizar el bogey animado siguiendo `subLevel.logicalPose()` (como
`translateBogeyWithShip`). `BlockEntityStorage` es de Flywheel (común), así que el mixin es casi
copiable. **OJO LICENCIA:** Create-Interactive es `Copyright All Rights Reserved` → NO copiar código
literal; reimplementar a partir de la idea.

## 9. Archivos de CI más relevantes (para releer)

- `CreateInteractiveUtil.kt` — núcleo: createShip, updateShipShadow, posRotToShipTransform,
  getContraptionPosRot(ForRender), moveContraptionToTransform, isTrainDerailed.
- `mixin_logic/MixinOrientedContraptionEntityLogic.kt` — orientación desde el ship.
- `mixin_logic/MixinContraptionRotationStateLogic.kt` + `CreateInteractiveContraptionRotationState.kt`
  — conversión rotationState→quaternion / asMatrix.
- `mixin_logic/client/MixinClientContraptionLogic.kt` — supresión de estructura.
- `mixin_logic/client/MixinVisualManagerLogic.kt` (+ `mixin/client/MixinVisualManager.java`
  `@Mixin(BlockEntityStorage)`) — supresión del visual de Flywheel del bogey BE.
- `mixin_logic/client/MixinCarriageContraptionEntityRendererLogic.kt` — re-render del bogey sobre el ship.
- `mixin_logic/client/MixinCarriageBogeyLogic.kt` — anchors de acople sobre el ship.
- `mixin_logic/MixinCarriageContraptionEntityLogic.kt` — joints entre vagones (físico).
