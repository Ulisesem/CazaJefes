# CazaJefes 1.36.0 · testRefactor

## Compilar y usar la API

Usa Java 21 y Gradle 8.14.3. Ejecuta `gradle clean build`. El build crea tres JAR independientes en los directorios `build/libs`. La fecha del nombre usa UTC y puede fijarse con `-PbuildDate=20261105`.

- `CazaJefes` es el plugin principal. Incluye el contrato de la API para que los consumidores compartan la misma clase.
- `api` es la dependencia de compilación para otros plugins. Declara `depend: [CazaJefes]` en tu plugin y obtén `CazaJefesApi` con Bukkit ServicesManager en el hilo del servidor. El SDK se usa como compileOnly. Deja su JAR fuera de la carpeta plugins.
- `pluginExample` muestra el catálogo indirectamente por ID, la invocación, la brújula y estadísticas. Instálalo junto al principal para probar `/cazajefesejemplo`, `/cazajefesejemplo item` y `/cazajefesejemplo boss sapo_de_fuego`.

Gradle expande las propiedades del descriptor al empaquetar para que Paper pueda leerlas antes de cargar el plugin. El workflow construye y guarda un artefacto por módulo en GitHub Actions. Los JAR no se versionan en el repositorio.

## Servidor

- El hacha sigue una secuencia de arma normal, hacha en la mano, mano vacía durante el lanzamiento y restauración del arma. El hacha arrojada usa una entidad pública que pueden ver los jugadores que la estén siguiendo.
- Los avisos cambian al iniciar la preparación y antes del impacto. La recuperación aparece después.
- Los saqueadores llevan Carga rápida VI, tienen una recarga normal de 850 ms y apuntan al cuerpo a corta distancia. El ataque Bombardeo de la Grieta alterna con la ráfaga de flechas. `combat.pillager-rocket-charges` acepta 1 a 9 y vale 9 por defecto. Los cohetes causan daño de área con caída por distancia y comprueban paredes.
- `combat.death-break-cave-roof` controla la apertura durante el ascenso. Quita permanentemente solo los materiales rocosos permitidos en la columna, sin soltar bloques. Exige un jugador acreditado, usa BlockBreakEvent y respeta límites de arena.
- Los acuáticos comprueban que quede agua alrededor del destino, se apartan de la orilla y pueden regresar a su último punto seguro si los sacan del agua. Los súbditos acuáticos persiguen y atacan por contacto dentro del agua, incluso cuando su especie normalmente es pasiva.
- La primera carga crea `plugins/CazaJefes/bosses/<categoría>/<id>.json` a partir de los datos existentes. Los archivos separados tienen prioridad y se validan uno a uno. Los jefes personalizados conservan `mobs/`. Agrega `meteorTexture` con un Base64 de skin de textures.minecraft.net al JSON del jefe para personalizar las cabezas de sus meteoritos.
- `recipes.yml` se completa con los IDs al registrar recetas. Las recetas con forma admiten `enabled`, `shape` e `ingredients`. Las infusiones y mejoras de libros permiten activarse o desactivarse por ID, porque su resultado depende de los ítems colocados.
- `messages.attack` acepta MiniMessage con `<state>` y `<attack>`. Si se instala PlaceholderAPI, la plantilla también puede usar sus variables. El servicio público expone las victorias de jugadores por UUID.
- NBTAPI se usa para exportar e importar SNBT desde la API. PacketEvents envía partículas simples y actualizaciones de mano a los espectadores del jefe. Paper conserva el inventario real, el seguimiento de entidades y las partículas con datos, como polvo y trazos. El puente de regiones de WorldGuard sigue disponible.

Esta rama es de pruebas. El controlador base y los adaptadores nuevos están separados por paquetes. El núcleo anterior se conserva en su paquete para limitar cambios de compatibilidad en esta primera pasada.
