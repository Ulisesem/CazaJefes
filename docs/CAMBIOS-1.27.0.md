# CazaJefes 1.27.0 · Legado del Presidio

## 28 + 28

La Cámara de infusiones contiene **28 recetas de armadura y otras 28 de espadas/hachas**, 56 entre ambas, contando las existentes. Se añaden 11 de armadura y 17 de arma, no 28 por encima de las anteriores. Se mantienen las diez recetas de arcos, diez de ballestas y las categorías restantes.

Se conserva Hacha del Bosque Ancestral (TreeFeller), que ya formaba parte de Espadas y hachas y sirve solamente para hachas. Las demás compatibilidades anteriores no se amplían indiscriminadamente. Los efectos de equipo de boss siguen requiriendo Resonancia Primigenia.

## Nuevas rares

Todas tienen máximo III y costo de saturación 0; la recarga es por jugador y por rare y se comparte al cambiar de copia del objeto. Las probabilidades indicadas se evalúan solo en eventos de daño aceptados y positivos. Los efectos de armadura usan el mayor nivel entre las piezas, no cuatro activaciones por llevar cuatro copias. Las etiquetas entre paréntesis identifican el efecto.

### Armaduras · 11 nuevas

| Rare e ID | Función | Material de forja |
|---|---|---|
| **Último Aliento (LastStand)** · `ultimo_aliento` | Al recibir daño que te deja bajo 35% de vida: 25/35/45% de curarte 1/2/3 HP. Máximo III. Recarga 28 s. | `filamento_tormenta` + `GOLDEN_APPLE` |
| **Paso del Fugitivo (Escape)** · `paso_rescate` | Bajo 35% de vida: 25/35/45% al recibir daño de ganar Velocidad II durante 2/3/4 s. Máximo III. Recarga 18 s. | `perla_profundidades` + `RABBIT_FOOT` |
| **Piel Adamantina (Guard)** · `piel_adamantina` | 25/35/45% de reducir el impacto recibido un 6/9/12%. Máximo III. Recarga 8 s. | `cristal_escarcha` + `DIAMOND` |
| **Pulso del Presidio (Reprisal)** · `pulso_represalia` | 25/35/45% al recibir un golpe cuerpo a cuerpo de repeler al atacante y debilitarlo 2/3/4 s. Máximo III. Recarga 12 s. | `filamento_tormenta` + `BREEZE_ROD` |
| **Sangre Serena (Cleanse)** · `sangre_serena` | Si tienes Veneno o Wither: 25/35/45% al recibir daño de quitar uno de esos efectos. Máximo III. Recarga 16 s. | `perla_profundidades` + `HONEY_BOTTLE` |
| **Raíz Inquebrantable (Anchor)** · `ancla_firme` | 25/35/45% al recibir daño de reducir el próximo empuje horizontal 30/40/50% durante 0,75 s. Máximo III. Recarga 10 s. | `cristal_escarcha` + `IRON_CHAIN` |
| **Velo del Prófugo (Veil)** · `velo_niebla` | 25/35/45% al recibir daño de obtener Invisibilidad durante 2/3/4 s. La armadura sigue visible. Máximo III. Recarga 22 s. | `filamento_tormenta` + `INK_SAC` |
| **Antídoto Carmesí (Antidote)** · `antidoto_carmesi` | 25/35/45% de rechazar un efecto de Veneno entrante. No elimina otros efectos. Máximo III. Recarga 12 s. | `perla_profundidades` + `MILK_BUCKET` |
| **Reserva del Herrero (Mend)** · `reserva_forja` | 25/35/45% al recibir daño de reparar 8/12/16 puntos en la pieza equipada más desgastada. Máximo III. Recarga 10 s. | `cristal_escarcha` + `ANVIL` |
| **Guardia de Ceniza (EmberWard)** · `guardia_ceniza` | 25/35/45% de reducir 20/35/50% un impacto de fuego, lava o suelo caliente. Máximo III. Recarga 8 s. | `filamento_tormenta` + `MAGMA_BLOCK` |
| **Voto del Centinela (Focus)** · `voto_cazador` | 25/35/45% al recibir un proyectil de preparar 10/15/20% extra en tu próximo golpe cuerpo a cuerpo durante 4 s. Máximo III. Recarga 14 s. | `perla_profundidades` + `TARGET` |

### Espadas y hachas · 17 nuevas

| Rare e ID | Función | Material de forja |
|---|---|---|
| **Ruptura del Presidio (Combo)** · `combo_ruptura` | El tercer impacto al mismo rival en 5 s añade 1/2/3 HP. Se cuentan solo golpes con daño. Máximo III. Recarga 8 s. | `engranaje_umbrio` + `FLINT` |
| **Filo del Rastreador (Pursuit)** · `filo_persecucion` | 25/35/45% de añadir 5/10/15% de daño contra un objetivo que corre o se mueve rápidamente. Máximo III. Recarga 10 s. | `polvora_resonante` + `SUGAR` |
| **Colmillo de Ponzoña (VenomStrike)** · `colmillo_ponzona` | 25/35/45% de añadir 10/15/20% de daño contra un objetivo ya envenenado. Máximo III. Recarga 10 s. | `pluma_vendaval` + `SPIDER_EYE` |
| **Cazador Espectral (RevealStrike)** · `cazador_espectral` | 25/35/45% de añadir 10/15/20% de daño contra objetivos con Invisibilidad o Brillo. Máximo III. Recarga 10 s. | `engranaje_umbrio` + `GLOW_INK_SAC` |
| **Veto del Sepulturero (Hex)** · `veto_vital` | 25/35/45% de reducir las curas del rival 10/20/30% durante 4 s. La reducción combinada por estas rares no supera 50%. Máximo III. Recarga 14 s. | `polvora_resonante` + `WITHER_ROSE` |
| **Cadena del Carcelero (Pull)** · `cadena_gravitoria` | 25/35/45% de atraer brevemente al objetivo si está a más de 2 bloques. Máximo III. Recarga 12 s. | `pluma_vendaval` + `LEAD` |
| **Martillo del Carcelero (Launch)** · `martillo_carcelero` | 25/35/45% de aplicar un impulso vertical creciente al objetivo al golpear. Máximo III. Recarga 14 s. | `engranaje_umbrio` + `HEAVY_CORE` |
| **Sello del Desconcierto (Daze)** · `sello_desorientador` | 25/35/45% de aplicar Debilidad I 2/3/4 s y Náuseas 2 s. No bloquea comandos. Máximo III. Recarga 14 s. | `polvora_resonante` + `FERMENTED_SPIDER_EYE` |
| **Paso del Duelista (Strafe)** · `paso_duelista` | 25/35/45% al golpear de ganar Velocidad I durante 2/3/4 s. Máximo III. Recarga 12 s. | `pluma_vendaval` + `FEATHER` |
| **Cosecha del Verdugo (Leech)** · `cosecha_hierro` | 25/35/45% al golpear un rival bajo 40% de vida de recuperar 1/2/3 HP. Máximo III. Recarga 14 s. | `engranaje_umbrio` + `GHAST_TEAR` |
| **Vigía de la Fuga (Track)** · `vigia_fuga` | 25/35/45% de revelar con Brillo al objetivo durante 3/4/5 s. Máximo III. Recarga 12 s. | `polvora_resonante` + `GLOWSTONE_DUST` |
| **Filo del Firmamento (Skycut)** · `filo_ascenso` | 25/35/45% de añadir 8/12/16% de daño contra objetivos en el aire y por encima de ti. Máximo III. Recarga 10 s. | `pluma_vendaval` + `PHANTOM_MEMBRANE` |
| **Juramento del Herido (Fury)** · `furia_herido` | Bajo 35% de tu vida: 25/35/45% de añadir 8/12/16% de daño al golpear. Máximo III. Recarga 12 s. | `engranaje_umbrio` + `REDSTONE_BLOCK` |
| **Respiro del Minero (Sustain)** · `respiro_minero` | Con menos de 14 puntos de comida: 25/35/45% al golpear de recuperar 1/2/3 puntos de comida. Máximo III. Recarga 15 s. | `polvora_resonante` + `GOLDEN_CARROT` |
| **Forja Errante (Temper)** · `forja_errante` | 25/35/45% al golpear de reparar 4/8/12 puntos de durabilidad del arma usada, si sigue en tu mano. Máximo III. Recarga 12 s. | `pluma_vendaval` + `NETHERITE_SCRAP` |
| **Sentencia de Brasas (ScorchStrike)** · `llama_purificadora` | 25/35/45% de añadir 8/12/16% de daño contra un objetivo que ya está ardiendo. Máximo III. Recarga 10 s. | `engranaje_umbrio` + `FIRE_CHARGE` |
| **Rompecombos del Duelista (Breaker)** · `ruptura_combo` | Tras recibir tres golpes del mismo enemigo en 5 s, tu siguiente golpe contra él lo repele y aplica Debilidad I 2/3/4 s. Máximo III. Recarga 18 s. | `polvora_resonante` + `PISTON` |

## Recetas, comandos y presentación

Las 28 nuevas esencias usan cuatro unidades del reactivo vanilla indicado, dos materiales de forja personalizados, un núcleo válido, una Rare Essence base y un cristal épico. Los materiales ya se consiguen de las familias de bosses y mediante las recetas de Materiales de forja. La cuadrícula de cada ficha muestra los ingredientes exactos.

`/ri book ultimo_aliento 3` entrega un libro con LastStand III. `/ri essence ruptura_combo 3` entrega la esencia de Breaker III. Las recetas producen esencias de magma y los comandos mantienen su separación. No se añaden drops de equipo con estas rares incorporadas ni drops de rares ya preparadas.

Las listas de 28 se muestran en la cuadrícula completa de cuatro filas de siete, ordenadas por nombre. La estrella **Volver a categorías pasa de 49 a 50**, una casilla a la derecha. En las listas de armaduras, Resonancia pasa a 51 para que no se superponga. El sello del inicio y los botones de volver de otras pantallas conservan su contexto. Se actualizan tanto el icono como su acción.

## Mecánicas y límites

- PvP estilo 1.8: no se requieren escudos ni recarga moderna de golpe. Los impactos cancelados, con daño cero o inválido no gastan recargas ni activan efectos.
- Los efectos ofensivos funcionan contra jugadores, mobs y bosses; se respetan equipos del marcador, mundos sin PvP en objetivos jugadores y las inmunidades de cada criatura.
- Los bonus porcentuales de daño de **estas nuevas rares** se limitan juntos a +50% por golpe; el extra plano de Combo se limita a 3 HP. Las reducciones propias de las nuevas armaduras se limitan al 50% por golpe. Las reglas anteriores de otras rares, armaduras y plugins siguen existiendo; no es una limitación global de todos los encantamientos del servidor.
- Combo necesita tres golpes del mismo atacante al mismo rival en cinco segundos; Breaker necesita que el portador haya recibido tres golpes del mismo rival en cinco segundos, con esa rare en el arma, antes de contraatacar. No cuentan ataques cancelados ni se conserva una carga indefinida.
- Anchor prepara la reducción del próximo empuje durante 0,75 s, incluyendo reducción del 20% en el componente vertical ascendente; no modifica caídas descendentes. Mantiene las comprobaciones de cancelación del evento de velocidad.
- Focus prepara un solo contraataque durante cuatro segundos. Requiere que siga equipada la rare de armadura para consumirlo.
- Las curas de LastStand/Leech pasan por EntityRegainHealthEvent, no reviven jugadores muertos y respetan reducción de curas y vida máxima. Veil no oculta la armadura. Cleanse quita solo un efecto: primero Veneno, o Wither si no hay Veneno.
- Hex reduce eventos de curación, no cambios directos de vida de otro plugin. No acumula niveles entre atacantes y limita su reducción combinada con Sello de Ruina a un máximo de 50% de la cura original. La limpieza de marcas se realiza al caducar o al salir.
- Temper comprueba que continúe en la mano el mismo tipo de arma con sus datos, tolerando el desgaste normal del golpe; no transfiere reparaciones a otro objeto diferente. Mend repara una sola pieza, la de mayor desgaste absoluto.
- Se agrupan los mensajes de nuevas activaciones de un mismo golpe. Las partículas usan CosmeticFX y sus presupuestos actuales; no se crean auras permanentes por cada rare.
- Sin búsquedas permanentes de todos los jugadores. Estados por UUID con límites (8192 recargas, 4096 combos/marcas, 512 tareas cortas), limpieza cada segundo y al desactivar el plugin. La lectura de poderes del equipo se agrupa para evitar clonar metadatos una vez por cada rare del catálogo.

## Validación y límites de la entrega

Maven verify y 248 pruebas de catálogo real, 28 recetas por grupo, exclusividad de equipo, ingredientes, etiquetas, niveles, límites de bonus y navegación, además de regresiones anteriores. JAR verificado como ZIP y bytecode Java 21. No se ejecutó Paper ni se comprobó el equilibrio real con OldCombatMechanics, cancelaciones de terceros o inventarios de jugadores; esos escenarios requieren prueba dentro del servidor.
