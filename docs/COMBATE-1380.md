# CazaJefes 1.38.0

El catálogo incluye 168 JSON en `src/main/resources/bosses/<categoría>/<id>.json`. Paper los exporta a `plugins/CazaJefes/bosses/<categoría>/<id>.json`. Edita el archivo individual y reinicia. El ID debe coincidir con el archivo para conservar huevos y puntos de aparición. Se puede editar la categoría dentro del archivo manteniendo su ruta de origen. Los archivos previos conservan sus ajustes y reciben una actualización de combate una sola vez.

- Raíz: nombre, descripción, especie, categoría, condición de minijefe, vida, daño, escala, retrato y perfil definitivo.
- `combat`: opciones como aura, botín, probabilidad de corazón y `caveBreach`. Los efectos personalizados se definen en `combat.particles`.
- `ai`: velocidad, gravedad, detección (60 bloques por defecto), movimiento acuático, equipo, enfriamiento general y ataques. Cada ataque define tipo, rango, peso, daño, alcance, aviso y recarga.
- `METEORITE_AOE`: `showerRadius: 20`, `durationTicks: 200` e `intervalTicks: 10` definen una lluvia de 10 segundos con un intento de caída cada medio segundo. `radius` es el radio de daño por meteorito. `showerRadius: 0` vuelve al meteorito individual.

Los jefes abren pasos temporales en roca y tierra bajo techo si no consiguen avanzar hacia su objetivo. Puedes desactivarlo con `combat.cave-breach: false` en `config.yml` o `combat.caveBreach: false` por jefe. Requiere `mobGriefing`. Con WorldGuard activo, requiere una arena configurada. Los bloques se abren sin drops, respetan la cancelación de eventos de cambio de bloque y se restauran cuando acaba el encuentro y el espacio queda libre. El registro permite recuperar los bloques al reiniciar y espera a que carguen sus chunks.
