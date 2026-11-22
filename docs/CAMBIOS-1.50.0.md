# CazaJefes 1.50.0

## Movimiento y combate

Los jefes terrestres se sostienen sobre el agua mediante soporte virtual sin colocar hielo ni modificar el líquido. Los acuáticos conservan su persecución sumergida. La persecución vertical despeja obstáculos con más frecuencia y permite ascender de forma controlada hacia jugadores elevados. Los golpes básicos reducen preparación y recuperación; se conserva la inmunidad nativa entre impactos.

Haste equivale a Velocidad VI, no a Prisa minera: dura 20 segundos y después espera 30 segundos. Los tiempos se pueden ajustar por boss en combat.boons.hasteDurationTicks (400) y hasteCooldownTicks (600); la recarga comienza al terminar el efecto. La velocidad también afecta al motor propio de movimiento.

## Terreno

Se restaura el terreno 20 minutos después de la derrota del boss, incluyendo el hueco abierto al nacer. Un registro append-only terrain-restoration.log conserva bloques, datos y plazos al reiniciar. Descargar un chunk no cuenta como derrota. Si varios bosses alteran una misma posición, se espera el plazo de todos sus propietarios.

La restauración procesa hasta 128 bloques por segundo en chunks cargados. Espera si un ser vivo ocupa el espacio y respeta regiones protegidas. No sustituye construcciones nuevas. Nunca guarda ni reemplaza bedrock. Los contenedores se respaldan con NBTAPI y no sueltan copias de su contenido al romperse; si no es posible guardar sus datos, se conserva el bloque.

La bedrock, incluida la base del huevo, no se elimina. Se bloquea su rotura normal por jugadores, cambios de entidades y su inclusión en listas de explosión. Las operaciones directas de administración o de otros plugins que omiten eventos Bukkit no se interceptan.

## Idioma

language.force-spanish=true fuerza español en el sistema de mensajes y evita que el idioma del cliente o una preferencia anterior lo cambien. Los nombres propios de rares y los identificadores técnicos se conservan. Los estados de IA visibles se muestran en español.

## Validación

Pruebas automatizadas y sonda local Paper 1.21.11. El plazo de restauración se verifica con un reloj de prueba antes/después de 20 minutos y recargando el registro; no se ha esperado ese tiempo real. No se ha hecho inspección gráfica de las partículas.
