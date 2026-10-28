# CazaJefes 1.18.0

## Comportamiento y curas

**Armadillo · Bastión Viviente:** se cubre durante 5 segundos y no puede causar daño mientras está cubierto. Los inicios de cobertura están separados como mínimo 15 segundos y esperan si otro especial está en curso. Al terminar cura un 0,5 % de vida máxima, con techo de 8 puntos de salud; no ataca continuamente hecho bola. Fuera de la cobertura se fuerza la salida de la bola y usa la persecución/ataque asistido del plugin. Su IA nativa de miedo se sustituye por ese control para no encadenar cierres vanilla.

**Shulker · Refugio del Umbral:** carga defensiva de 3 segundos con la caja cerrada; al terminar recupera hasta 5 % de vida máxima y avisa sobre la hotbar si se aplicó la cura. Recarga mínima de 25 segundos entre inicios. Espera a otros ataques y no dispara nuevas bolitas durante el cierre. Las teleportaciones defensivas pendientes se omiten si sigue cubierto.

Las curas no reviven entidades ni sobrepasan su vida máxima. Publican un EntityRegainHealthEvent y respetan su cancelación; no reinician etapas ni regeneran toda la vida por un golpe fuerte.

**Abejas:** vuelo de aproximación ondulante con giros laterales y pequeñas subidas/picados, también en sus súbditos. Se evita la muerte vanilla posterior a perder el aguijón. Es una trayectoria de vuelo, no un modelo con animación de voltereta añadido mediante recursos externos.

## Persecución y encierros

Se guarda por UUID al último jugador que hizo daño válido. Se prioriza persistentemente mientras esté conectado, vivo, en un modo atacable, en el mismo mundo y a un máximo de 64 bloques del cuerpo. Se persigue aun sin línea de visión, mediante rutas nativas; no se cargan mundos o chunks lejanos para perseguir indefinidamente. Los arqueros priorizan ese agresor cuando tienen alcance y visión. Los ataques siguen sus límites de alcance y obstáculos.

La recuperación existente de sofocación se amplía a encierros con tres o más lados bloqueados. Tras al menos 5 segundos, y sin otro especial en curso, se busca una salida libre cercana. No destruye bloques ni cura por recolocarse; conserva las comprobaciones de chunks, agua, borde y tamaño. No es una demostración de que todas las trampas posibles estén cubiertas.

La etapa III usa **Cataclismo del Destierro · Empuje X**, el nuevo nombre explícito de la repulsión coordinada. Comparte la recarga de burst de 25 segundos y alterna con Ascensión: no se añade otro empuje paralelo que choque con las habilidades existentes. La inmunidad de OP al burst se mantiene.

## Hordas

Cada horda propia se crea como un lote de seis súbditos, repartidos alrededor del tamaño real del boss. Si alguna aparición no puede colocarse o se cancela, se retira el lote incompleto y no se anuncia una horda falsa. Límites: 12 súbditos activos por boss y 192 gestionados globalmente. Una invocación se pospone si no caben los seis; no se fuerza a aparecer dentro de bloques para cumplir el número.

Se mantiene Fuerza I, la resistencia de animales y arañas, y la eliminación de hijos al morir el boss. Un temporizador de duración por lote sustituye temporizadores de movimiento individuales antiguos; los animales usan su controlador compartido. Los magos de boss invocan seis evocadores sanadores. Sus invocaciones vanilla de vex se sustituyen por esa horda controlada y los súbditos no generan cadenas de vex adicionales.

## Magos

**Conjuradores de Vida:** cada súbdito mago tiene capacidad de curar a su dueño hasta un 3 % de vida máxima. Comparten una recarga de 15 segundos por boss, evitando seis curas simultáneas. Deben estar vivos, a un máximo de 24 bloques y con visión del boss. Un haz verde conecta la cura, se reproduce sonido y el chat informa del jefe y los puntos realmente recuperados. Matar a los sanadores interrumpe esta ruta de curación. No es vampirismo y no cura cada golpe.

**Grilletes del Conjurador / Runas de Entropía:** nuevos especiales de magos con carga de 2 segundos y tres pulsos sobre una zona marcada. Se pueden esquivar alejándose de esa zona; aplican debilidad y control según etapa. Recarga de 18 segundos tras finalizar.

**Juicio de las Seis Tormentas:** evocadores en etapa III cargan durante 2 segundos y dirigen seis rayos al objetivo. Se espacian 20 ticks para no borrar la inmunidad de daño nativa. Cada impacto aceptado tiene referencia de 6 puntos de vida (3 corazones), más el bonus de etapa III cuando corresponda: 6,3 puntos/3,15 corazones antes de bloqueos y reducciones por invulnerabilidad. Se respetan cancelaciones y cobertura; salir del alcance o perder visión evita el impacto. Los rayos son visuales, sin fuego ni destrucción de bloques. Recarga de 25 segundos tras finalizar.

Todos estos especiales consultan la ocupación del boss para esperar a burst, mirada abismal, defensas, ballesta y ataques de suelo. Se suprimen hechizos/proyectiles nativos del mago durante su propio especial para no superponerlos.

## Esqueletos arqueros

SKELETON, STRAY y BOGGED llevan diamante o netherita según categoría. Protección X e Irrompibilidad X; se conserva el refuerzo por categoría del encuentro. Arco NPC con Poder X, Retroceso X, Llama X, Irrompibilidad X, Infinidad I y Reparación I. Este equipo no cae como botín.

Flechas propias con referencia de Poder X, velocidad 3,2, puntería predictiva y corrección inicial durante 20 ticks. Alcance máximo 48 bloques, comprobación de visión, limpieza a 4 segundos y límite global de 128 flechas de estos arqueros. No son recogibles. Disparo normal cada 2 segundos; **Tríada del Eclipse** carga 1 segundo y lanza tres trayectorias convergentes, con recarga de 15 segundos. Los stray añaden lentitud y los bogged veneno.

La trayectoria compensa movimiento, gravedad y levitación mediante el modelo compartido. No se garantiza acertar todos los tiros: hay paredes, escudos, desplazamientos, latencia y otros plugins. No se atraviesan bloques ni se resetea la inmunidad entre golpes para simular puntería perfecta.

## Daño, efectos y presentación

En etapa III se añade un 5 % al daño de bosses que NO hayan renacido, dentro de los topes de daño existentes. Los renacidos conservan su multiplicador anterior sin apilar ese bonus. El daño fijo de bolitas y rayos también incorpora el bonus cuando corresponde; el impacto de muerte conserva su regla independiente.

Ceguera de ataques de boss: 100 ticks (5 s). Náuseas: 200 ticks (10 s). Los demás debuffs conservan su duración anterior. Estos cambios no alargan las rares PvP de 1–3 segundos.

El burst toma el ancho y alto reales de la entidad: empieza envolviendo el cuerpo y se expande como una esfera/elipsoide de pequeñas estrellas blancas END_ROD. 32 puntos por fotograma, ocho fotogramas y el presupuesto compartido existente. El cilindro de muerte alcanza 20 bloques, con luces distribuidas verticalmente; no añade entidades ni daño extra.

Los avisos de etapa combinan dorado y aqua, texto en negrita y títulos estilizados. Se retiran estrellas y adornos ofuscados. Solo los reciben los participantes registrados.

## Drops

Las tiradas que daban rares de poder o libros preparados entregan ahora Rare Essence base sin tipo de poder, nivel de infusión ni encantamiento especial. Los grados genéricos Primordial/Abismal siguen siendo catalizadores, no poderes preparados. Se mantienen materiales, fragmentos, tributos, corazones y equipo sellado sin rares incluidas. Se agrupan entradas iguales en el mensaje y se conserva la caída de unidades individuales.

Resonancia Primigenia pasa a ser exclusivamente de receta; deja de caer como rare preparada. Las recetas de poderes y grimorios siguen disponibles para fabricarlos. No se borran libros, rares o equipo que ya tengan los jugadores.

## Validación

Maven verify: 186 pruebas aprobadas; inspección CRC del JAR y bytecode Java 21. Las pruebas nuevas cubren ciclos defensivos, límites de hordas, duración de efectos, geometría escalada, bonus y reglas de curación/rayos. No se ha ejecutado un servidor Paper ni probado animaciones, IA, colisiones, tiro o balance multijugador. Se necesita una prueba en juego y una medición de carga con la cantidad de bosses prevista; los límites no equivalen a garantizar ausencia de lag.
