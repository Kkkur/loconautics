# Loconautics — Plan del Equipo (Lycoris & MDLOP)

> **Objetivo del mod:** Añadir un botón en la pantalla de ensamblaje de la estación de tren de Create que, en vez de ensamblar el tren como una contraption normal, lo convierta en un **objeto físico de Sable** (un "sub-level"), de forma que el tren **parezca un objeto físico real pero siga los raíles de Create de forma exacta**.

---

## 0. Datos del proyecto (no cambiar sin avisar)

| Cosa | Valor |
|------|-------|
| Mod ID | `loconautics` |
| Autores | Lycoris, MDLOP |
| Loader | NeoForge 1.21.1 |
| Java | 21 |
| Create | 6.0.10-280 (mc1.21.1) |
| Sable | 1.2.2 (NeoForge 1.21.1) |
| Aeronautics | create-aeronautics-bundled 1.21.1-1.2.1 |
| Repo | https://github.com/Kkkur/loconautics |

---

## 1. Decisiones cerradas (ya elegidas, no volver a discutir salvo que cambiemos de idea)

1. **Tipo de física → OPCIÓN B (física aparente).**
   El tren *parece* un objeto físico de Sable, pero su movimiento lo calcula Create (raíles, señales, curvas, acoplamiento). Cada tick movemos el sub-level de Sable a la pose que Create ya calculó.
   - La física *real* (que el tren rebote, sufra torque, descarrile de verdad) se deja **para más adelante**, una vez que la Opción B funcione.

2. **Dónde va el botón → en la `AssemblyScreen`** (la pantalla de ensamblaje), **antes** de ensamblar el tren. No en la pantalla del tren ya ensamblado.

3. **Un sub-level de Sable por cada carriage (vagón)**, no uno solo para todo el tren. Así cada vagón se articula en las curvas.

4. **Cómo capturamos los bloques → CAMINO A.**
   Interceptamos *antes* de que Create saque los bloques del mundo, capturamos los bloques por grupos (un grupo por futuro vagón) y creamos los sub-levels desde ahí.

---

## 2. Reglas de trabajo (acordadas)

- **Claude no escribe nada sin preguntar primero.**
- **Claude no se inventa nada**: si no encuentra una clase real, la pide descompilada o pregunta.
- **Código modular**, nada de "god files". Cada responsabilidad en su archivo.
- Antes de programar una fase que toque Create/Sable/Aeronautics, Claude lee los `.java` reales de la carpeta `src/depend/`.

---

## 3. Entregables — estado actual

| Entregable | Estado | Responsable |
|------------|--------|-------------|
| JAR de Aeronautics en `run/mods/` | ✅ Hecho (`create-aeronautics-bundled-1.21.1-1.2.1.jar`) | Lycoris |
| JAR de Sable en `run/mods/` | ✅ Hecho (`sable-neoforge-1.21.1-1.2.2.jar`) | Lycoris |
| Decompiles de Create (estación, tren, carriage…) | ✅ Hecho (`src/depend/Create/`) | Lycoris |
| Decompiles de Sable (sublevel, pipeline, eventos…) | ✅ Hecho (`src/depend/Sable/`) | Lycoris |
| Decompiles de Aeronautics (assembly helper…) | ✅ Hecho (`src/depend/Aeronautics/`) | Lycoris |

> **Todo lo que Claude necesitaba para empezar ya está entregado.** No hay bloqueos para arrancar.
>
> ⚠️ **Importante para compilar:** los JAR de Sable y Aeronautics viven en `run/mods/` (carpeta gitignored por copyright). Cada dev debe colocar ahí esos dos JAR antes de hacer `./gradlew build`, o la compilación fallará.

---

## 4. Reparto de tareas

### 🅰️ LYCORIS — Cliente / UI / Build

- [x] Conseguir los JAR de Aero y Sable → `libs/`
- [x] Descompilar archivos de Create UI (`AssemblyScreen`, `AbstractStationScreen`, `StationEditPacket`, `StationBlockEntity`)
- [ ] Verificar que el `build.gradle` **sincroniza bien en IntelliJ** después de que Claude añada los JAR de `libs/` (Gradle → Reload)
- [ ] Probar en juego que **aparece el botón nuevo** en la pantalla de ensamblaje de la estación → hacer captura y reportar posición/aspecto
- [ ] Crear la **textura PNG** del botón → `src/main/resources/assets/loconautics/textures/gui/` (Claude te dice el tamaño exacto y el nombre)

### 🅱️ MDLOP — Servidor / Sable / Trenes

- [ ] Probar el **ensamblado físico** en una partida y, si peta, **pegar el crash log / stacktrace completo** (no un resumen)
- [ ] Probar el **movimiento**: ¿el sub-level sigue al carriage? ¿se desincroniza en curvas? ¿el tren avanza?
- [ ] Revisar los **logs de Sable** para confirmar que crea los sub-levels (buscar líneas de `Sable` en el log)
- [ ] Probar **desensamblar** el tren físico: ¿los bloques vuelven al mundo bien?

### 🤝 Claude

- Escribe todo el código, modular, fase a fase.
- Antes de cada fase pide luz verde y explica qué archivos va a tocar.

---

## 5. Las 5 fases (qué esperar en cada una)

| Fase | Qué hace Claude | Qué probáis vosotros | Riesgo |
|------|-----------------|----------------------|--------|
| **0** | Limpia el template, configura `build.gradle` con los JAR, crea la estructura de paquetes | Que el proyecto **compile** (`./gradlew build`) | 🟢 |
| **1** | Modelo de datos + red (packets cliente↔servidor) | Que arranque sin errores | 🟢 |
| **2** | **El botón** en la pantalla de ensamblaje | Que **aparezca el botón** y al pulsarlo llegue al servidor (log) | 🟡 |
| **3** | **Bridge**: capturar bloques por vagón → crear sub-level de Sable | Que se cree el sub-level al ensamblar (logs de Sable) | 🔴 |
| **4** | **Driver**: cada tick mover el sub-level a la pose del vagón | Que el tren físico **se mueva por los raíles** | 🟡 |

**Hito importante:** al final de la Fase 2 ya hay algo visible en el juego (el botón). Al final de la Fase 4, el tren físico se mueve.

---

## 6. El reto técnico que Claude resolverá en la Fase 3 (para que lo sepáis)

Los bloques de un vagón solo pueden estar en **un** sitio: o en la contraption de Create, o en el sub-level de Sable — **no en los dos** (si no, se ven duplicados).

Camino A significa que **el sub-level de Sable manda**: la lógica del tren de Create sigue corriendo (para saber por dónde van los raíles), pero el render del vagón normal de Create se **suprime** y se ve el sub-level de Sable en su lugar.

Esto se prototipa en la Fase 3. No es un bloqueo, está identificado.

---

## 7. Cómo se prueba (protocolo rápido por fase)

1. `./gradlew build` → debe compilar sin errores.
2. `./gradlew runClient` → abre Minecraft con el mod.
3. Mundo creativo plano → colocar estación + raíles + bogey + bloques + bloque de controles.
4. Click derecho en la estación → modo ensamblaje → **buscar el botón nuevo**.
5. Pulsar el botón → mirar el log y el juego.

> Cuando algo falle, **copiad el log entero** (`run/logs/latest.log`), no solo la última línea.
