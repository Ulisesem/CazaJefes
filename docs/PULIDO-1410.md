# Pulido de combate y zonas en 1.41.0

WorldGuard se consulta aunque la lista combat.worldguard-arenas esté vacía. En una región con pvp deny, el jefe no puede seleccionar objetivos ni mover su cuerpo hacia la región. Los ataques y el daño usan la misma comprobación. En regiones protegidas, el jefe puede combatir cuando el PvP está permitido, pero no retirar sus bloques. Fuera de regiones se conservan mobGriefing y los eventos de cambio de bloque de otros plugins.

Los golpes cercanos tienen un aviso máximo de cinco ticks y una recarga máxima de dieciocho ticks. La recuperación de ataques básicos por defecto baja a seis ticks. Los valores basicRecoveryTicks existentes se respetan.

Las interfaces comparten un marco simétrico, encabezados en negrita y navegación en las posiciones existentes. La ficha del jefe muestra rangos en español y recargas en segundos.

## Propuestas para la siguiente iteración

- Postura rompible. Encadenar golpes durante un punto débil deja al jefe vulnerable durante dos segundos.
- Especiales por parejas. Una marca en el suelo seguida de una carga obliga a elegir entre cubrirse o contraatacar.
- Adaptación al grupo. Ajustar vida y número de objetivos con límites definidos para uno, tres o seis jugadores.
- Contratos de cacería. Objetivos semanales con recompensas cosméticas y materiales, registrados por UUID.

Estas propuestas quedan documentadas para decidir su balance antes de incorporarlas al combate.
