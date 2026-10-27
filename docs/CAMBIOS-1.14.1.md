# Cambios de 1.14.1

Tempestad del Destierro pasa a una única carga de medio segundo seguida de seis ráfagas de treinta flechas: 180 proyectiles en total si la secuencia completa sus seis disparos. Se dispara una ráfaga cada 0,25 segundos a 20 TPS. La recarga de doce segundos empieza al terminar la última. Conserva el umbral de cinco golpes válidos recibidos en ocho segundos y sus encantamientos anteriores, incluido Multidisparo XV; la cantidad real de proyectiles se controla por código.

Cada ráfaga recalcula la puntería con la posición, velocidad y levitación del objetivo. Se compensa la caída de las flechas y se mantiene una flecha central exactamente apuntada a la solución calculada, incluso cuando el blanco está casi vertical. Esa flecha central de cada disparo también corrige suavemente su rumbo durante el primer segundo, siempre que haya objetivo válido y línea de visión. Las demás cubren el abanico. Los disparos normales usan la misma mejora de puntería.

El alcance de las flechas marcadas del saqueador pasa a 48 bloques, también en las reglas de validación de daño: un jugador que sale por los aires a más de 24 bloques ya no pierde automáticamente la posibilidad de recibir el impacto. Se conservan bloqueos por paredes, estado del jugador y eventos cancelados.

El daño de represalia aumenta hasta un 35% respecto a la referencia anterior, con límite de 200 puntos antes de mitigaciones y las escalas existentes. Una misma ráfaga no suma treinta impactos al mismo jugador: puede registrar un impacto aceptado por jugador y por ráfaga, sujeto a la inmunidad normal de Minecraft. Las seis ráfagas pueden dañar a lo largo de la secuencia. No se reinician los ticks de inmunidad.

Las flechas siguen siendo infinitas para el NPC y no recogibles. Caducan a los tres segundos y hay un límite de 192 proyectiles vivos por boss. Al morir o descargarse el saqueador se retiran los restantes. La secuencia se interrumpe si deja de existir un objetivo válido; no dispara a través de paredes para seguirlo.

No se puede garantizar que toda flecha impacte a un jugador que esquiva, cambia de trayectoria o se cubre. La mejora consiste en predicción y corrección de la flecha central, no en daño automático ni teletransporte de proyectiles.

Validación: Maven verify, pruebas de seis ráfagas, 180 proyectiles, trayectorias distintas con objetivo vertical y simulación de intercepción en espacio abierto. No se ha ejecutado un servidor Paper ni realizado una prueba visual o de carga con 180 flechas por boss.
