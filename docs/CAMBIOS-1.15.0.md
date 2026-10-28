# CazaJefes 1.15.0

## Vida y espacio

Los slimes y magma cubes fijan el tamaño nativo antes de establecer sus atributos. Al restaurar encuentros antiguos, los cambios de tamaño conservan la vida que tenían; no se rellenan. Su división vanilla se cancela para mantener un único boss y una sola recompensa.

Un golpe positivo, autorizado y no cancelado remata cuando queda como máximo un punto de vida, evitando una cola de fracciones. No modifica la salud directamente, no atraviesa un bloqueo completo y conserva el renacimiento único de zombies. La fase no vuelve atrás al recuperar vida.

La recuperación comprueba el espacio real de la entidad, chunks cargados y borde del mundo. Los nuevos bosses sin espacio no consumen el huevo; los súbditos sin posición libre se retiran. La sofocación de entidades del encuentro se cancela y se intenta una recolocación cercana, sin curación ni destrucción de bloques. Los acuáticos no se sacan del agua para recuperarlos. Las búsquedas tienen límites; no sustituyen una arena con espacio suficiente para el tamaño del boss.

## Combate y coordinación

Burst usa una sola recarga de 25 segundos desde su lanzamiento. En fase III alterna sus dos variantes. La inmunidad de OP al burst se conserva. Los cambios de fase no reinician la recarga.

Cenit del Abismo solo pertenece a abismales en fase III. Empieza disponible 12,5 segundos después de entrar en esa etapa, carga 2 segundos y fuerza el ángulo vertical durante 5 segundos, dejando caminar al jugador. Su recarga es de 25 segundos desde el lanzamiento. Tiene máximo ocho objetivos por lanzamiento e inmunidad individual de 25 segundos frente a varias miradas; libera la cámara al acabar, morir o abandonar el encuentro. No usa teletransportes para modificar la cámara.

El controlador coordina mirada, burst, especiales de suelo y carga de ballesta. Los ataques pueden esperar si otro está en curso; los tiempos son mínimos de recarga y no una garantía de lanzamiento exacto.

Las familias tienen combinaciones: artrópodos venenosos, acuáticos, ígneos, congelantes, tiradores, no muertos, resonantes, magos y bestias. El golpe de continuación dibuja una cruz blanca y se puede esquivar. Son afinidades inspiradas en encantamientos; no añaden nuevas rares al jugador. Magos emplean trazos arcanos, nombres y sonidos de conjuración. Cada boss conserva su perfil definitivo.

Minijefes crecen un 30 % adicional, una sola vez, y aumentan su vida base mínima a 360 y su daño configurado mínimo a 26 antes de overrides explícitos. Su ataque asistido tiene referencia mínima 18 antes de modificadores; mantienen fases y combinaciones. Los encuentros existentes no recuperan vida por instalar la actualización.

Espadas y hachas NPC: Filo X e Irrompibilidad X. Golpes sin ese encantamiento reciben solo la diferencia hasta el bonus de Filo X (5,5 puntos antes de fases y mitigación). No se acumula dos veces. El equipo NPC no se convierte en botín.

Todos los bosses tienen botas con Agilidad Acuática X donde se pueden equipar; el movimiento en agua se refuerza por código también en especies sin equipo. Conservan respiración y eficiencia acuática máxima. El nivel vanilla superior a III no produce por sí solo ese movimiento adicional.

Los efectos negativos aplicados por ataques de bosses duran 600 ticks (30 segundos a 20 TPS). El refresco cercano al máximo se agrupa para reducir actualizaciones. Todos aplican ceguera, náuseas y fatiga en fase III cuando su daño es aceptado; los bosses son inmunes a esos tres efectos. El impulso de levitación del burst no se extiende a 30 segundos. Fuego visual y daño directo no son efectos de poción.

## Ballestas y shulkers

La ballesta conserva seis ráfagas de treinta flechas por carga. Poder X tiene multiplicador equivalente programado de 3,75, con topes existentes de daño. Cada ráfaga puede registrar un impacto positivo por jugador. Flechas no recogibles, limpieza a 3 segundos, máximo 192 por boss y 512 globales.

Los shulkers lanzan veinte bolitas con recarga de 10 segundos en fase III y 12 en las anteriores, vida máxima de proyectil de 8 segundos y techo global de 128. Un impacto aceptado pretende 8 puntos de salud (4 corazones), conservando bloqueo, reducción por inmunidad entre golpes y cancelaciones. No se suman veinte impactos simultáneos ignorando inmunidad. Veneno, wither y levitación duran 30 segundos. Cinco golpes recientes habilitan una teleportación defensiva tras el siguiente burst disponible, respetando la recarga compartida.

## Antifarmeo y presentación

Reclamo de la Grieta requiere golpes recientes repetidos, un atacante inalcanzable o elevado, al menos 6 segundos sin contacto dañino del boss y recarga de 25 segundos. Avisa 2 segundos antes y solo mueve al jugador si sigue refugiado y existe una ubicación despejada. Las cancelaciones de teleportación del servidor se respetan. No rompe bloques ni cubre todas las posibles trampas.

Abrir un huevo avisa a todos los jugadores de todos los mundos con nombre del jugador y del jefe. Las hordas anuncian al dueño sobre la hotbar al crear súbditos. Mensajes de etapa: títulos estilizados, adornos ofuscados y texto legible en negrita, solo para participantes registrados.

El impacto final aumenta su impulso para unos 15 bloques horizontales en terreno libre. La distancia depende de colisiones, terreno y otras reglas. Cuatro rayos visuales se sitúan a 9 bloques del centro, sin fuego ni daño nativo. Después aparece un cilindro de pequeñas luces; el botín conserva su descenso individual suavizado.

La brújula admite clic derecho en aire o suelo, anuncia que no hay huevos activos sin abrir y no vuelve a seguir al boss despierto. Las flechas son dibujos de polvo lima y pequeñas estrellas geométricas del mismo color: no requieren pack de recursos. Recarga visual de 10 segundos.

## Objetos y registro

Las Rare Essence conservan sus IDs y poderes, con el tipo y nivel romano en la descripción. El equipo muestra propiedades como Durability V, en naranja. Se actualizan inventario, armadura, mano secundaria y contenedores abiertos; los contenedores descargados se actualizan cuando se abren. No se cambian IDs de bosses ni se restauran alias antiguos.

Victorias por UUID: solo golpe final válido, persistencia JSON atómica, exportación CSV y API pública para una integración futura con hologramas. No se instala ni crea un holograma en esta versión.

## Rendimiento y validación

Cosméticos de combate: presupuesto compartido máximo de 384 partículas por jugador/segundo y 4096 globales por ventana de un segundo, alcance 32. Al agotarse se omiten adornos; el daño no depende de las partículas. Auras y botín conservan sus límites anteriores independientes. La recuperación inspecciona ocho entidades por pasada, hasta cuatro urgencias y como máximo 24 comprobaciones completas por búsqueda. Sin carga intencional de chunks nuevos para recolocar.

Maven verify y 169 pruebas automatizadas, inspección CRC del JAR y bytecode Java 21. No se ha arrancado un servidor Paper ni medido TPS/FPS. Hace falta una prueba de juego de cámara, colisiones, proyectiles, efectos largos y compatibilidad con otros plugins antes de desplegar a todos los jugadores.
