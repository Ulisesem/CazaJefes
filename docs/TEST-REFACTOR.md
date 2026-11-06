# CazaJefes 1.36.2 · testRefactor

## Compilar y usar la API

Usa Java 21 y Gradle 8.14.3. Ejecuta `gradle clean build`. El build crea tres JAR independientes en los directorios `build/libs`. La fecha del nombre usa UTC y puede fijarse con `-PbuildDate=20261105`.

- `CazaJefes` es el plugin principal. Incluye el contrato de la API para que los consumidores compartan la misma clase.
- `api` es la dependencia de compilación para otros plugins. Declara `depend: [CazaJefes]` en tu plugin y obtén `CazaJefesApi` con Bukkit ServicesManager en el hilo del servidor. El SDK se usa como compileOnly. Deja su JAR fuera de la carpeta plugins.
- `pluginExample` muestra el catálogo indirectamente por ID, la invocación, la brújula y estadísticas. Instálalo junto al principal para probar `/cazajefesejemplo`, `/cazajefesejemplo item` y `/cazajefesejemplo boss sapo_de_fuego`.

Gradle expande las propiedades del descriptor al empaquetar para que Paper pueda leerlas antes de cargar el plugin. El workflow construye y guarda un artefacto por módulo en GitHub Actions. Los JAR no se versionan en el repositorio.

## Servidor

- El hacha sigue una secuencia de arma normal, hacha en la mano, mano vacía durante el lanzamiento y restauración del arma. El hacha arrojada usa una entidad pública que pueden ver los jugadores que la estén siguiendo.
- Los avisos cambian al iniciar la preparación y antes del impacto. La recuperación aparece después.
- Los saqueadores llevan Carga rápida VI, tienen una recarga normal de 850 ms y apuntan al cuerpo a corta distancia. El ataque Bombardeo de la Grieta alterna con la ráfaga de flechas. `combat.pillager-rocket-charges` acepta 1 a 9 y vale 6 por defecto. Los cohetes causan daño de área con caída por distancia y comprueban paredes.
- `combat.death-break-cave-roof` controla la apertura durante el ascenso. Quita permanentemente solo los materiales rocosos permitidos en la columna, sin soltar bloques. Exige un jugador acreditado, usa BlockBreakEvent y respeta límites de arena.
- Los acuáticos comprueban que quede agua alrededor del destino, se apartan de la orilla y pueden regresar a su último punto seguro si los sacan del agua. Los súbditos acuáticos persiguen y atacan por contacto dentro del agua, incluso cuando su especie normalmente es pasiva.
- La primera carga crea `plugins/CazaJefes/bosses/<categoría>/<id>.json` a partir de los datos existentes. Los archivos separados tienen prioridad y se validan uno a uno. Los jefes personalizados conservan `mobs/`. Agrega `meteorTexture` con un Base64 de skin de textures.minecraft.net al JSON del jefe para personalizar las cabezas de sus meteoritos.
- `recipes.yml` se completa con los IDs al registrar recetas. Las recetas con forma admiten `enabled`, `shape` e `ingredients`. Las infusiones y mejoras de libros permiten activarse o desactivarse por ID, porque su resultado depende de los ítems colocados.
- `messages.attack` acepta MiniMessage con `<state>` y `<attack>`. Si se instala PlaceholderAPI, la plantilla también puede usar sus variables. El servicio público expone las victorias de jugadores por UUID.
- NBTAPI se usa para exportar e importar SNBT desde la API. PacketEvents envía partículas simples y actualizaciones de mano a los espectadores del jefe. Paper conserva el inventario real, el seguimiento de entidades y las partículas con datos, como polvo y trazos. El puente de regiones de WorldGuard sigue disponible.

Esta rama es de pruebas. El controlador base y los adaptadores nuevos están separados por paquetes. El núcleo anterior se conserva en su paquete para limitar cambios de compatibilidad en esta primera pasada.


## Cambios de combate en 1.36.1

- Los jefes eliminan las metas de huida y distracción y limpian memorias de pánico o pacificación. Priorizan un jugador frente a ellos al elegir objetivo y conservan un objetivo válido. La provocación sigue siendo una excepción explícita.
- Los piglins mantienen equipo irrompible, son inmunes a la zombificación y no se distraen admirando oro. La restauración de equipo respeta la mano vacía durante el lanzamiento del hacha.
- El ataque de orbes del shulker y el rayo de represalia avisan 25 ticks antes de salir por defecto. Comparten el bloqueo de ataque con el resto de especiales del mismo jefe y conservan sus propias recargas.
- El valor anterior de nueve cohetes se migra a seis una sola vez. Los demás valores configurados se conservan.


### Ajustes por jefe en JSON

Edita `plugins/CazaJefes/bosses/<categoría>/<id>.json` y reinicia. Los valores raíz `health`, `damage` y `scale` del JSON tienen prioridad para nuevas apariciones. Si antes usabas valores individuales en `config.yml`, cópialos al JSON. Conserva el ID, especie y categoría de los jefes incluidos. Para otra criatura usa el sistema de plantillas en `mobs/`.

Este es un ejemplo del bloque opcional `combat`. Los campos omitidos heredan la configuración común y `combate.yml`. `rocketCharges` aplica a jefes saqueadores.

```json
"combat": {
  "fearless": true,
  "aura": true,
  "loot": true,
  "heartChance": 0.015,
  "speed": 0.31,
  "attackIntervalTicks": 10,
  "warningTicks": 30,
  "recoveryTicks": 40,
  "rocketCharges": 6,
  "phase-2-health": 65,
  "phase-3-health": 30,
  "phase-1": ["embestida_personal"],
  "phase-2": ["nativo_1", "nativo_2"],
  "phase-3": ["nativo_1", "definitivo"],
  "skills": {
    "embestida_personal": {
      "name": "Ruptura del Bastión",
      "pattern": "NOVA",
      "effect": "WEAKNESS",
      "damage": 18,
      "radius": 7,
      "cooldown": 12,
      "warning-ticks": 30,
      "recovery-ticks": 40
    }
  },
  "horde": ["ZOMBIE:GUARD", "ZOMBIE:GUARD", "HUSK:TANK", "SKELETON:ARCHER", "WITCH:HEALER", "CREEPER:EXPLOSIVE"]
}
```

`warningTicks` y `recoveryTicks` fijan mínimos para las habilidades de la rotación. Los enfriamientos de ataques personalizados se configuran con `skills.<id>.cooldown` en segundos. Las técnicas nativas de especie y las tablas completas de drops aún tienen reglas compartidas.


## Idiomas en 1.36.2

Usa `/settings` para elegir Español, English o Português. El menú muestra tres cabezas. La selección se guarda por jugador, y en la primera visita se usa el idioma del cliente si coincide con los incluidos.

En `plugins/CazaJefes/lenguajes/{es,en,pt}.json` cada archivo tiene `meta.id`, `meta.name` y `meta.texture` en Base64. `messages` contiene textos por clave. Para personalizar los nombres, agrega `"sapo_de_fuego": {"name": "Fire Toad", "description": "..."}` dentro de `bosses` del idioma deseado. Para cambiar nombres de habilidades en la barra, agrega una clave `id_del_boss.id_de_la_habilidad` dentro de `attacks`. Los valores que falten usan el texto incluido o el nombre original. Reinicia para cargar los cambios del archivo.

La selección se aplica al menú de idioma, al aviso de etapas, a títulos y categorías del bestiario y a los avisos principales de ataques. Los textos antiguos de recetas, botín y algunos ataques de especie conservan sus traducciones originales.

Texturas de las cabezas de [México](https://cdn.minecraft-heads.com/custom-heads/head/70015-mexico), [Reino Unido](https://minecraft-heads.com/custom-heads/head/70085-united-kingdom-flag) y [Portugal](https://minecraft-heads.com/custom-heads/head/77846-portugal-flag).
