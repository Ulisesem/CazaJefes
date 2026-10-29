# CazaJefes 1.20.0

Colección de 33 rares basada en las recetas entregadas por el propietario. Se implementan las matrices de nueve ingredientes, máximos, costos de uso, recargas explícitas y nombres nuevos; se añaden Claridad y Robo de Hambre y se separa Esporas de herramientas del aura venenosa de armadura.

Consultar [nombres, categorías, poderes y las 33 matrices](RARES-1.20.0.md). La nota del archivo que afirma 38 esencias no coincide con sus 33 recetas detalladas. Cada receta base usa una esencia; no se inventan cinco recetas faltantes.

## Cambios de comportamiento

- Procesamiento de rares ofensivas dividido en preparación y confirmación: un golpe cancelado o con daño final no positivo no consume saturación ni activa sus poderes. Se conserva la protección de daño de bosses y la normalización de daño no negativo.
- Flechas y tridentes guardan las infusiones al dispararse; cambiar de arma después no cambia la rare del proyectil. Las mejoras de armadura no se activan por llevarlas en la mano.
- Pacto Carmesí cura después del golpe aceptado, con daño efectivo limitado por la vida previa de la víctima, respetando cancelaciones y límites de curación. Sello del Alba y Baluarte siguen contrarrestando las habilidades propias del plugin.
- Desarmar guarda el arma en un hueco libre del rival; no la elimina ni la tira, y no cobra si no hay candidato viable.
- Estallido afecta a un enemigo cercano; Gran Estallido afecta a varios, con un límite de dieciséis. Se respeta la excepción de jugadores OP y se excluyen compañeros del scoreboard. No hay integración específica con Factions.
- Regeneración ahora cura un HP por nivel cada diez segundos, sin costo. Reparación pasiva, vuelo, caída, saltos, vida extra, claridad y robo de comida tienen costo cero según la lista.
- Caída reduce 25% por nivel y alcanza inmunidad en IV. No acumula cuatro reducciones entre piezas. Doble salto permite uno, dos o tres saltos extra; vuelo equipado toma prioridad y el burst de boss mantiene su control temporal.
- Herramientas transforman drops reales. Obsidiana y Fertilizar verifican permiso de construcción mediante evento de colocación antes de cambiar el bloque. La minería enlazada usa roturas del jugador y límites por nivel; no carga chunks para buscar vetas.
- Los grimorios iguales se pueden combinar en la mesa para alcanzar su máximo, incluido Invisibilidad VIII. Infusiones que exceden el máximo se rechazan en lugar de desperdiciar esencias.

## Configuración y equipo existente

Se respalda `rares.yml` como `rares-before-1.20.0.yml` y se actualizan una vez los máximos, costos y recargas de esta colección. Las especificaciones de la colección prevalecen en ejecución; los costos negativos permiten desactivar un poder. Se preservan otras rares y anulaciones de recetas. No borrar la carpeta del plugin.

Las armaduras existentes con aura Spore conservan el poder al refrescarse su metadata, con ID `aura_esporas`. Las esencias sueltas antiguas `spore` adoptan el uso actual de Musgo Primigenio; el aura de armadura tiene su receta separada. Las mejoras guardadas por encima de máximos nuevos no se destruyen, pero se limita su nivel activo y mostrado. No se reintroducen IDs antiguos de bosses.

Los drops no incluyen armas o armaduras con rares incorporadas; continúan entregando esencias básicas y materiales según las tablas existentes. Las recetas de la lista usan Rare Essence auténtica y materiales vanilla, sin añadir cristales o núcleos al patrón suministrado.

## Validación

Java 21 y API Paper 1.21.11. **199 pruebas pasaron con Maven verify**: catálogo y recursos, matrices distintas con una esencia, ingredientes de recetas similares, máximos/costos/recargas, categorías, separación de Esporas, caída, claridad, umbrales, reparación y rechazo de excesos. Se mantienen las pruebas previas de daño, bosses, botín y animaciones.

Se revisa integridad ZIP del JAR y bytecode Java 21. No se ha arrancado Paper ni probado un cliente Minecraft en este entorno. Las pruebas unitarias no garantizan ausencia total de errores, compatibilidad con todos los plugins ni equilibrio PvP. Probar en una copia antes de abrir el servidor al público.

## Instalar

Detener el servidor, respaldar `plugins/CazaJefes`, retirar el JAR previo e instalar solo `CazaJefes-1.20.0.jar`. Conservar configuraciones y datos, reiniciar y abrir `/rares`. Las recetas no cambian solo por consultar un documento: están registradas en el código y se muestran desde las mismas recetas que utiliza la mesa.
