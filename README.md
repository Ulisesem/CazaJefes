# CazaJefes 1.10.0 · Espiral amplia, botín unitario y protección por categoría

- Radio inicial de la espiral aumentado de 1,4 a 2,2 bloques; conserva tres vueltas, ocho segundos de descenso suavizado y una animación por boss.
- Cada unidad se separa de su stack antes de entrar en la cola: 10 fragmentos son 10 descensos, unos 82 segundos en total. Se preservan los datos, encantamientos y cantidades totales. El reporte sigue mostrando el total completo.
- Se impide la fusión en el suelo de objetos marcados como botín de boss; el inventario del jugador sí puede apilarlos normalmente.
- Al aterrizar Rare Essence, Primordial o Abismal: un rayo visual sin fuego ni daño, sonido de trueno y recompensa, partículas de tótem y fuegos artificiales.
- Protección: Minijefes V, Común X, Raro XV, Épico XX, Legendario XXV, Mítico XXX, Abismal XXXV. Se aplica a la armadura NPC de todos los bosses con equipamiento compatible; algunos mobs no muestran armadura visualmente. Las armas de zombies conservan Filo X y su tótem.
- Todos los bosses reciben además una defensa de categoría de 5%, 10%, 15%, 20%, 25%, 30% y 35%, respectivamente, para diferenciar categorías pese al límite vanilla del encantamiento. Los que carecen de equipamiento también reciben esta defensa. Se combina con las fases y el suelo mínimo de daño existente, sin anular ataques ni permitir daño negativo.
- No se modifica el equipo obtenible por los jugadores ni el botín de armadura, solo el equipo y defensa NPC.

Validación: compilación y pruebas unitarias de progresión, separación, cantidades, reconocimiento de esencias y trayectoria. No se ha ejecutado un servidor Paper ni comprobado la apariencia. Detener el servidor, reemplazar el JAR y arrancar conservando configuración. Los bosses creados tras la actualización reciben el equipo nuevo.

---
Documentación histórica:

# CazaJefes 1.9.4 · Descenso visual suavizado

El descenso usa un ItemDisplay con el mismo ItemStack, brillo e iluminación. Se actualiza cada dos ticks con teleportDuration=2 para que el cliente suavice la posición y orientación entre destinos de la espiral. No se crean objetos recogibles durante el vuelo. Se mantienen tres vueltas y 160 ticks de trayectoria; se esperan dos ticks al final para completar la interpolación antes de sustituir la copia por el objeto real. El siguiente stack conserva su turno cada 164 ticks.

Al aterrizar se elimina la copia visual y se crea una sola recompensa física con el identificador de encuentro, cantidad, datos y encantamientos originales. La entrega también se completa una sola vez si desaparece la copia o se desactiva el plugin; los stacks pendientes se vacían al suelo. Se mantienen los anuncios de rares al recoger y el reporte completo del botín.

No se incrementa la frecuencia de partículas. Una animación activa por boss. Puede seguir habiendo tirones si bajan los TPS o falla la conexión. Validación por compilación y pruebas unitarias; no se ha verificado la apariencia con un cliente Paper real.

Reemplazar el JAR con el servidor apagado y reiniciar; conservar configuración.

---
Documentación histórica:

# CazaJefes 1.9.3 · Botín de uno en uno

Cada recompensa del boss baja por separado. El primer stack aparece después del impacto, completa su espiral de 160 ticks (ocho segundos a 20 TPS), y el siguiente aparece cuatro ticks después. Los demás stacks esperan en una cola sin entidades ni partículas. Por ejemplo, tres stacks tardan unos 24 segundos en completar sus descensos. Se conserva la cantidad de cada stack; no se separan unidades de un mismo stack.

Cada boss tiene su propia cola. Al apagar el plugin se entregan los stacks pendientes sin animación y se liberan los que estén descendiendo. El reporte conserva la lista completa del botín; la recogida sucede conforme va terminando cada descenso.

Validación: Maven verify, pruebas FIFO, vaciado sin duplicación, trayectoria y duración. Sin comprobación visual en Paper. Reemplazar el JAR con el servidor apagado.

---
Documentación histórica:

# CazaJefes 1.9.2 · Botín en espiral durante ocho segundos

Después del impacto y sus rayos, el botín aparece ocho bloques por encima del punto de muerte. Cada objeto completa tres vueltas en una espiral que se estrecha mientras desciende durante 160 ticks (ocho segundos a 20 TPS). Mantiene brillo y estelas; al llegar se activa la gravedad y la recogida se habilita dos ticks después. La duración de ascenso y caída del boss se conserva.

La posición se actualiza cada dos ticks, y las partículas cada cuatro. Al apagar el plugin se liberan los objetos pendientes sin dejarlos flotando. No se cambia el botín, el daño ni los rayos.

Validación: Maven verify y pruebas de duración, descenso continuo y llegada al punto de botín. Sin verificación visual en Paper. Sustituir el JAR con el servidor detenido.

---
Documentación histórica:

# CazaJefes 1.9.1 · Impacto, rayos y después botín

Secuencia: ascenso de 15 bloques durante 60 ticks, caída acelerada durante 12 ticks, impacto con sonido de explosión y cinco rayos visuales, pausa de seis ticks y aparición del botín. Los objetos orbitan 12 ticks y caen con brillo y estela; recogida desde 25 ticks después de aparecer.

Los rayos usan strikeLightningEffect: no generan fuego, no dañan mobs ni objetos y no multiplican el daño. El impacto conserva un único daño de 6–7 puntos (3–3½ corazones) a jugadores en supervivencia/aventura dentro de seis bloques, y Lentitud II, Debilidad I y Ceguera I. Respeta cancelaciones, absorción y bloqueo. No hay explosión física ni destrucción de bloques. El golpe y sus efectos ocurren antes de crear los ítems.

El crédito del jugador se guarda antes de iniciar la secuencia. Las recompensas y el reporte se entregan una sola vez, también si se interrumpe la copia visual o se desactiva el plugin; al detenerse se omite el ataque pendiente y se entrega el botín sin animación. No se cambia el renacimiento de los zombies ni el equilibrio de drops.

Validación: Maven verify y pruebas de trayectoria, daño y entrega única de recompensa. Sin ejecución visual en Paper; comprobar la secuencia con clientes reales. Conservar configuración y reemplazar el JAR con el servidor detenido.

---
Documentación histórica:

# CazaJefes 1.9.0 · Caída final y botín animado

Al morir definitivamente, el cuerpo real se oculta mediante hideEntity y se crea una copia visual sin IA, colisión, recompensas ni vulnerabilidad. La copia sube 15 bloques en 60 ticks y cae en 20 ticks. Cerca del límite superior del mundo se limita la altura al espacio disponible. El renacimiento previo de los zombies conserva su comportamiento: esta secuencia solo ocurre al morir de verdad.

Un círculo de seis bloques y avisos sobre la hotbar anuncian el impacto. Al caer se reproducen partículas y sonido de explosión, sin llamar a createExplosion: no rompe bloques, no inicia fuego y no destruye el botín.

- Jugadores en supervivencia o aventura a seis bloques o menos del punto de muerte: 6–7 puntos de daño (3–3½ corazones), omitiendo armadura y Protección para mantener esa potencia.
- Se respetan cancelaciones del evento, absorción, bloqueo y reducciones por inmunidad; pueden reducir o impedir la pérdida real. Otros plugins que modifiquen después el evento también pueden alterar el resultado.
- Tras un impacto aceptado con daño positivo: Lentitud II (6 s), Debilidad I (8 s) y Ceguera I (2 s). No afecta creativo ni espectador.
- El botín orbita suspendido a 2,7 bloques durante cuatro segundos, cae con brillo y estela, y se puede recoger a partir de 4,5 segundos. Al desactivar el plugin se restauran gravedad y recogida.
- Partículas de botín cada cuatro ticks; no se aumentan las auras permanentes optimizadas de 1.8.0.

Validación: Maven verify, 99 pruebas sin errores; geometría del ascenso y caída, radio, rango de daño y conservación de cancelación/absorción. No se ha probado visualmente en un servidor Paper; la ocultación de la animación vanilla debe comprobarse con clientes reales y otros plugins instalados.

Instalación: detener el servidor, sustituir el JAR anterior por CazaJefes-1.9.0.jar y arrancar. Conservar la configuración.

---
Documentación histórica:

# CazaJefes 1.8.0 · Auras optimizadas

Las auras permanentes usan un único temporizador, geometría compartida entre espectadores, selección de bosses cercanos y presupuestos de envíos. Se conserva el combate de 1.7.5.

- Calidad media por defecto: 24 puntos y un acento, cada 10 ticks; alcance de 32 bloques y dos bosses por jugador.
- `/ri particulas baja|media|alta|off`: preferencia personal persistente. No necesita permiso administrativo. Solo afecta las auras permanentes; brújula, ataques y advertencias siguen independientes.
- Se respetan los ajustes de partículas del cliente; no se fuerza la visualización ambiental.
- Presupuesto por jugador: 160 envíos por segundo con capacidad de ráfaga de un segundo; presupuesto global: 1024 por actualización. Se reduce calidad o se omiten auras completas al agotarse. Se rota la prioridad entre jugadores.
- Configuración: `particles.auras` en config.yml. Los valores predeterminados también funcionan con configuraciones antiguas. Reiniciar tras cambiar el intervalo.

En calidad media, un boss visible pasa de aproximadamente 296 a 50 llamadas de envío por segundo por espectador (83% menos). Es una comparación del algoritmo, no una medición de FPS/TPS. Los ataques y otras partículas no están incluidos en esa cifra. Los límites controlan envíos, no bytes ni partículas visibles finales.

Validación: Maven verify y pruebas unitarias; no se ha ejecutado un servidor Paper ni un cliente. Antes de abrir el servidor, medir con jugadores y varios bosses usando el perfilador spark de Paper.

Instalación: detener el servidor, reemplazar el JAR anterior por CazaJefes-1.8.0.jar y arrancar. Conservar la configuración existente.

---
Documentación histórica:

# CazaJefes 1.7.5

Paper 1.21.11 y Java 21. Fuentes Maven y JAR de prueba.

## Equipo por categoría

| Categoría | Colección | Protección | Filo |
|---|---|---|---|
| Épica | Velo Astral | VII | IV |
| Legendaria | Juramento Solar | VII | V |
| Mítica | Corona del Vacío | VIII | VI |
| Abismal | Eclipse Eterno | VIII | VII |

24 piezas: cuatro armaduras, espada y hacha por categoría, con brillo y descripción gris. Épico/Legendario: diamante. Mítico/Abismal: netherita.

`/riequipo <epico|legendario|mitico|abismal> [armadura|espada|hacha|todo] [jugador]`

Ejemplo: `/riequipo abismal todo`. Sin pieza: todo. Desde consola indica pieza y jugador. Permiso `cazajefes.admin`, operadores por defecto. Entrega completa o nada si falta espacio.

## Bosses y drops

76 encuentros: 10 minijefes y 20 abismales. Quince nuevos abismales y un Warden duplicado retirado. Sus alias antiguos apuntan a Nharzul. Los nuevos bosses usan ataques por tipo de entidad y sistemas comunes; no todos tienen habilidades exclusivas.

Fuerza II minis, III raros, IV épicos, V legendarios, VI míticos, VII abismales. Prisa VI. Fases al 65% y 30% aumentan daño y resistencia. Fase final añade Fatiga minera II, Hambre II y Náusea, conservando los rayos según daño y la alteración infrecuente de casillas.

Arañitas: escala 0,55; 120 vida; 8 armadura; daño base 6 + 1,5 por rango. Todos los súbditos: Fuerza I. Partículas en especiales, invocaciones y huevos. Huevos colocables apuntando al aire con bedrock temporal y restauración del bloque original.

Minijefes: 1–2 tributos, 1–3 fragmentos y un cristal común. Solo los minis dejan cristales. Los superiores se fabrican con dos del rango anterior y un tributo; el común no tiene receta. Los bosses mayores conservan fragmentos garantizados y tiradas de otros materiales. Equipo de colección desde Épico. Corazones encantados: 1% predeterminado configurable.

Hardy en pechera da Fuerza I–V según nivel y saturación. Durability repara piezas equipadas con esa propiedad: 2 por nivel cada cinco segundos, incluso quieto, sujeto a saturación y daño real. No cura la vida del jugador.

## Interfaces y recetas

`/rares`: cristales, armas y armaduras por rango, paginación e ingredientes reales. `/mercado`: departamentos y confirmaciones. `/bestiario` o `/bestirio`: todos los bosses.

44 recetas temáticas: 39 propiedades/hechizos y cinco armas Rare. Combinan poción normal exacta, reactivo vanilla, núcleo de boss, cristal, fragmento y esencia/equipo base. Las pociones extendidas, potenciadas, arrojadizas y persistentes no sustituyen a las normales. Más 24 recetas de conjuntos.

Son diseños propios, inspirados en referencias oficiales: [membrana de phantom y reparación](https://www.minecraft.net/nl-nl/article/taking-inventory--phantom-membrane), [polvo de blaze y alquimia](https://www.minecraft.net/nl-nl/article/taking-inventory--blaze-powder).

## Actualizar y compilar

Detén el servidor, respalda mundo y carpeta del plugin, sustituye el JAR y reinicia. Conserva un solo JAR; no uses recarga en caliente. `recipes.yml` regula recetas, `rares.yml` propiedades y `config.yml` combate/drops. Las recetas antiguas afectadas se respaldan en `recipes-before-1.4.0.yml`, restableciendo patrones y conservando enabled. Equipo antiguo se normaliza conservando propiedades Rare; categorías inferiores pasan a Épico.

Compila con `mvn verify` y Java 21. Resultado: `target/CazaJefes-1.7.5.jar`. 78 pruebas automáticas, configuración, ZIP/CRC y bytecode Java 21 verificados. **No se ejecutó Paper ni se verificaron interfaces dentro de Minecraft**. Prueba equilibrio y compatibilidad en un servidor de pruebas.

Fuentes reconstruidas estáticamente desde 0.9.7: se preservó lo recuperable, sin recuperar comentarios ni estructura original exacta. El JAR original no se ejecutó.

## Cambios de combate en 1.6.0

Se elimina el regreso al origen; `combat.leash-radius` deja de utilizarse aunque figure en un archivo antiguo. No se rellena la vida ni se reinicia la etapa por desplazamiento. Los bosses permanecen en el agua: respiración acuática, gracia de delfín, eficiencia de movimiento acuático y persecución en tres dimensiones. No se fuerza la salida a superficie. Se cancelan teletransportes nativos que los expulsarían del agua; el Shulker combate allí con proyectiles sin saltar a tierra.

Shulkers: ocho proyectiles dirigidos por ráfaga, Veneno II durante cinco segundos si el impacto causa daño y se permite el combate. Ráfaga cada seis segundos, 4,5 en etapa final; máximo una ráfaga activa por boss y proyectiles con ocho segundos de duración. Sustituye disparos nativos adicionales. Al recibir golpes no letales, busca teletransporte cercano con espacio, suelo sólido y límites del mundo; pausa mínima 1,8 segundos. Bajo el agua no usa esos saltos.

Movimiento táctico común: anticipación corta, persecución con navegación, flanqueos alternados, retirada de atacantes a distancia, embestidas por fase y manejo de obstáculos. Los minijefes usan la misma lógica y golpe cercano; el zorro no abandona al jugador para cazar ovejas y tiene menor velocidad y embestida. No equivale a un sistema de aprendizaje automático ni reemplaza todos los comportamientos internos de Minecraft.

Avisos de etapa 1/3, 2/3 y 3/3 solo a jugadores que hayan causado daño permitido al boss. Se registran los golpes directos y proyectiles de jugador; siguen recibiendo avisos mientras estén conectados aunque dejen de golpear, se alejen o cambien de mundo. Mirar o acercarse no registra participación. Los UUID se conservan con el boss, incluso al recargar chunks o reiniciar. Al reconectarse se informa la etapa actual, sin repetir historial. Se conserva la etapa máxima alcanzada en los datos del boss. Cada boss tiene color estable y motivo de partículas en aura, ataques e invocaciones. La brújula en cualquiera de las manos muestra rastro y flecha hacia el objetivo más cercano del mismo mundo, sin el antiguo límite de 120 bloques; es una guía de dirección, no una ruta que evite obstáculos.

Los núcleos muestran únicamente su categoría; se conservan identificadores internos por compatibilidad. Protección VII/VII/VIII/VIII para Épico/Legendario/Mítico/Abismal. Filo sigue IV/V/VI/VII. La migración de inventario aplica el nuevo nombre y Protección a las piezas existentes.

API utilizada: [proyectiles Shulker de Paper 1.21.11](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/entity/ShulkerBullet.html). La compilación y pruebas de reglas no sustituyen una prueba dentro del servidor: comprueba especialmente agua, teletransportes, colisiones de mobs escalados, protección de terrenos y varios jugadores.


El anuncio único de derrota usa dorado y aqua con separadores, botín real y tabla de daño descendente de todos los jugadores registrados, incluido quien esté desconectado al morir el boss. Cuenta daño final permitido directo o de proyectiles de jugador, limitado a la vida restante para no sumar exceso del golpe final; daños ambientales y efectos sin autor atribuible no se asignan. Los totales y nombres se guardan en el boss. La tipografía sigue siendo la normal de Minecraft; no requiere paquete de recursos.

## Combate y presentación de 1.6.0

La brújula sustituye el rastro continuo por tres flechas de partículas orientadas al objetivo, emitidas en una sola tanda cada diez segundos por jugador. Cambiar de casilla no reinicia el plazo. Su dirección se calcula en tres dimensiones y sigue siendo una guía directa, sin buscar una ruta entre paredes.

Los bosses activos conservan aura orbital, color propio y motivo de partículas. La presentación del botín usa negrita en dorado y aqua, con una línea por objeto y cantidad; conserva la tabla de daño por jugador.

Los especiales comunes se amplían de cinco a ocho variantes y usan diez segundos entre activaciones: ondas, embestidas, explosiones marcadas, invocaciones, efectos de área y los nuevos cortes cruzados, barridos en cono y runas ascendentes. Shulkers reciben esas tres variantes nuevas, además de sus proyectiles y teletransportes. Los disparos nativos o ataques cuerpo a cuerpo no son especiales y conservan su frecuencia propia.

Cada uno de los 76 bosses tiene un definitivo definido en `BossAbilityCatalog`, con nombre exclusivo y combinación propia de patrón, efecto, radio, pulsos y orientación. Comparten doce familias de ejecución: anillos expansivos, cruz, cono, espiral, meteoros marcados, vórtice, carriles, tablero alternado, géiseres, nova, corredor de embestida y jaula que se cierra. Son perfiles personalizados sobre un motor común; no son 76 motores de ataque independientes. Consulta `docs/DEFINITIVOS-1.6.0.csv` para revisar cada uno.

Solo se activan en etapa 3. La carga dura tres segundos y muestra PRECAUCIÓN / CARGANDO ATAQUE FINAL, nombre y cuenta regresiva sobre la hotbar, junto con zonas de partículas. Se avisa a jugadores cercanos expuestos al peligro; los mensajes de etapa continúan limitados a participantes. La brújula y las represalias de rayos ceden prioridad a esa advertencia.

Los definitivos tienen dos a cuatro pulsos, separados por un segundo, y recargan quince segundos desde el fin de la ejecución. No se superponen dos cargas del mismo boss. El plazo del definitivo se guarda en los datos del boss; sus tareas se cancelan al morir, descargar el chunk o apagar el plugin. Los patrones dejan espacios para esquivar y se fijan al comenzar la carga. El daño requiere jugador válido, distancia y línea de visión; no rompe bloques ni elimina la inmunidad normal a golpes consecutivos. Los efectos secundarios se aplican cuando se pierde salud o absorción.

La selección de objetivo conserva foco durante dos segundos y prioriza jugadores participantes cercanos. Se mantienen persecución táctica, movimiento acuático, especiales y daño de contacto de minijefes.

Validación: 78 pruebas automáticas y compilación Java 21. No se ejecutó un servidor Paper ni un cliente Minecraft en esta sesión. Revisa presentación, esquiva y equilibrio en tu servidor de pruebas antes de usarlo con jugadores.


## Corrección 1.6.1

Corrige NullPointerException de EntityPickupItemEvent en rareLoot al recoger objetos sin relic_id. Los objetos normales se descartan antes del aviso de botín; la clasificación admite identificadores ausentes sin consultar contains(null) en listas inmutables. Se conservan avisos únicos para armas Rare, libros y corazones del boss. Validación: 78 pruebas, incluidas tres regresiones de recogida y clasificación. No se ejecutó Paper en esta sesión.


## Auras y guardianes zombis en 1.6.2

Las auras de bosses despiertos usan un controlador visual independiente del ataque, cada cinco ticks: anillo inferior de 16 puntos, doble hélice de 16 y corona superior de ocho, más chispas END_ROD y el motivo propio del boss. Se calcula el contorno desde su caja real, evitando el antiguo límite de radio que podía ocultar el efecto dentro de entidades grandes. Colores por boss y categoría. Se envían a jugadores que puedan verlo dentro de 64 bloques con force=true de Paper; no se dibujan auras de mobs normales ni de huevos mediante este controlador. El cliente o sus mods aún pueden limitar las partículas. El ciclo se limpia al morir, descargar la entidad o apagar el plugin y se registra de nuevo al cargar el boss.

Los bosses zombis (Zombie, Husk, Drowned, Zombie Villager, Zombified Piglin y Giant) reciben conjunto completo encantado: hierro en Común/Raro, diamante en Épico/Legendario y netherita en Mítico/Abismal. Espada y hacha visibles en ambas manos, alternando la mano principal según boss. La mano secundaria es visual y no añade un segundo golpe independiente. Protección/Filo II–VII e Irrompibilidad III para este equipo de NPC; los conjuntos de jugadores conservan sus niveles actuales. Se equipa al aparecer y al recuperar bosses existentes tras reiniciar/cargar su chunk.

El equipo visible no forma parte del botín normal: probabilidad de caída cero, sin recoger objetos del suelo y protegido contra extracción por Disarm. Disarm retira temporalmente el arma principal y la devuelve después de tres segundos si la mano sigue vacía. Se conservan las tablas de drops existentes.

Validación: 78 pruebas automáticas y compilación Java 21. No se ejecutó Paper ni se inspeccionó el efecto dentro de Minecraft. Referencias: [partículas de Paper](https://docs.papermc.io/paper/dev/particles/) y [equipo de entidades 1.21.11](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/inventory/EntityEquipment.html).


## Muerte, renacimiento y vampirismo en 1.7.0

Se elimina la antigua secuencia de muerte que cancelaba el golpe letal, dejaba al boss en 1 HP, desactivaba su IA y lo hacía invulnerable hasta terminar una tarea de animación. La muerte vuelve al flujo normal del servidor y conserva el botín y la tabla de daño. No depende de teletransportes ni temporizadores para morir. Reiniciar con esta versión también recupera IA y vulnerabilidad de los bosses cargados, incluidos los que quedaron bloqueados antes.

Los zombis mantienen la armadura encantada, pero ahora escogen aleatoriamente espada o hacha para la mano principal una vez por aparición. La elección persiste al recargar el chunk. En la otra mano llevan Suspiro del Renacido, un tótem de renacimiento de NPC. En el primer golpe letal se consume, reproduce el efecto y sonido de protección de muerte y restaura exactamente el 65% de la vida máxima. No se concede invulnerabilidad ni se reinicia la etapa. El aviso se envía a participantes conectados y jugadores cercanos. El golpe letal inicial se registra en la tabla de daño.

Después del renacimiento, todo daño del boss a jugadores se duplica respecto al resultado de las reglas de combate actuales, incluidos proyectiles y habilidades. La marca de renacimiento se conserva en el boss y el tótem no vuelve al reiniciar o cargar su chunk. El siguiente golpe letal permite la muerte normal. El equipo y el tótem de NPC no se añaden como drops.

Todos los bosses, incluidos minijefes, obtienen vampirismo en etapa 3. Al causar daño permitido a un jugador, recuperan el 50% de ese daño efectivo, limitado a la vida del jugador antes del golpe, la vida que le falta al boss y el 3% de su vida máxima. Recarga de diez segundos por boss, persistente al recargar. Los golpes cancelados o sin daño no curan. Un rayo de partículas rojas hacia el boss y corazones señalan la extracción; el aviso sobre la hotbar respeta la prioridad de los definitivos.

Las auras se amplían a 72 puntos: anillo exterior, doble hélice, corona y un segundo anillo a media altura. Más grosor y alcance exterior; acentos dorados al renacer. Continúan adaptándose al tamaño real del mob.

Validación: 78 pruebas y compilación Java 21. No se ejecutó Paper ni se validaron las mecánicas en un cliente Minecraft. Referencia: [efecto de protección de muerte de Paper 1.21.11](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/EntityEffect.html).

## Daño bajo y animación de muerte en 1.7.1

Se elimina Debilidad II de los golpes de etapa 3: al combinarse con armadura encantada y resistencia de fase podía anular ataques débiles o reducirlos a cantidades difíciles de ver. Los demás efectos finales permanecen. Los golpes de jugador permitidos con daño positivo en etapa 3 tienen un suelo de daño final de hasta 1 HP (15% del daño base si es menor). Se recalcula mediante los modificadores normales de Bukkit; no se resta vida directamente ni se deshacen eventos cancelados. No se inventa daño cuando el golpe original es cero. La barra muestra un decimal en lugar de redondear hacia arriba.

El vampirismo conserva su recarga y extracción, con un presupuesto adicional: puede devolver como máximo el 35% del daño que los jugadores hayan causado recientemente en etapa 3. El presupuesto caduca después de diez segundos sin daño entrante y se descarta al curarse, impidiendo que anule todo el progreso. El renacimiento del tótem sigue siendo único y separado.

La animación de elevación vuelve como una representación temporal del mob después de su muerte real: sube hasta cuatro bloques en 32 ticks, baja en 16 y desaparece con partículas. Copia tamaño y equipo, sin IA, daño, XP ni botín. Su invulnerabilidad solo afecta a la representación, que no tiene los identificadores de boss. El boss real ya ha muerto y entregado el botín antes de empezar; un fallo visual no lo deja bloqueado en 1 HP. Las tareas se limpian al terminar o desactivar el plugin.

Validación: 78 pruebas y compilación. No se ejecutó Paper: confirma la pérdida de vida y la elevación en tu servidor. Las cancelaciones de daño por otros plugins se siguen respetando. API: [Paper RegionAccessor](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/RegionAccessor.html).


## Cambios actuales de 1.7.2

Se elimina por completo el vampirismo de bosses: no hay extracción ni curación al golpear jugadores. Se mantiene el renacimiento único del tótem al 65% de vida y daño doble. Las propiedades vampíricas de objetos de jugadores no cambian.

El equipo de NPC zombis tiene Protección X en las cuatro armaduras y Filo X en su espada o hacha. Irrompibilidad III y elección aleatoria persistente se mantienen. Los conjuntos para jugadores no cambian.

Al consumir el tótem, el boss deja exactamente un Sello del Renacido en el suelo y lo anuncia junto a su renacimiento. La marca persistente de tótem consumido impide repetir la entrega al recargar el chunk. Se conserva el identificador interno de los sellos anteriores: siguen sirviendo y actualizan su nombre al migrar el inventario. El mercado conserva la opción de comprarlos con fragmentos. En /mercado → Santuario del Renacer se canjean, por defecto, cuatro sellos y ocho tributos por el Amuleto del Fénix Astral (tótem). Los precios configurados se respetan.

Validación: 75 pruebas automáticas, compilación y estructura del JAR verificadas. Las pruebas específicas del vampirismo de bosses se retiran junto a esa mecánica. No se ejecutó Paper ni un cliente Minecraft en esta sesión.


## Corrección de partículas 1.7.3

El registro de Paper 1.21.11 muestra 192 excepciones iguales: missing required data class java.lang.Float. DRAGON_BREATH requiere un Float que representa la potencia del aliento y los efectos abismales lo estaban omitiendo. Se envía ahora 1.0f en el aura del botín, onda de choque, ráfaga dirigida y onda de efectos; también se valida el resto de partículas seleccionadas dinámicamente. No se sustituye la partícula por otro efecto.

La tarea de partículas del botín restaura gravedad y se cancela si un efecto inválido genera IllegalArgumentException, con un aviso único en lugar de repetir la excepción cada dos ticks. Los objetos ya entregados no se eliminan. La muerte registrada de Zyrakk es un mensaje normal del servidor; el fallo mostrado sucedía en las partículas posteriores.

Validación: 79 pruebas automáticas, incluida comprobación del payload real enviado a la interfaz World, compilación y estructura del JAR. No se ejecutó un servidor Paper en esta sesión. Referencia oficial: [Particle de Paper 1.21.11](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/Particle.html).


## Correcciones de golpes y brújula en 1.7.4

Se corrige el filtro de golpes contra bosses escalados: antes exigía la línea de visión de ojos a ojos y medía alcance hasta la posición central. Un cuerpo visible podía rechazarse cuando la cabeza quedaba por encima de un techo o el centro estaba lejos. Ahora se mide la distancia a la caja real del mob y se trazan comprobaciones hacia puntos visibles de su cuerpo. Se siguen respetando paredes, permisos, modo de juego y cancelaciones externas. Las pruebas ejercitan EntityDamageByEntityEvent y el propio CombatGuard, incluyendo cuerpo visible, paredes, alcance, permisos y cancelación previa.

La inmunidad acuática a daño ambiental CUSTOM ya no cancela eventos atribuidos a un jugador o proyectil. El suelo de daño positivo contra armadura se aplica en todas las etapas; no se inventa daño para golpes originales de cero, no se resta vida directamente y no se elimina Protección X.

Diagnóstico: los golpes cancelados, con cero daño o sin pérdida de vida después de un evento aceptado generan un aviso de consola limitado a uno por boss cada cinco segundos. El administrador recibe una indicación breve. /ri diagnostico, junto al boss y con permiso cazajefes.admin, muestra versión, entidad, UUID, vida, invulnerabilidad, ticks de inmunidad, absorción, armadura, modo, permiso de combate y visibilidad del cuerpo. La opción combat.damage-diagnostics: false permite desactivar avisos automáticos. El renacimiento válido del tótem no se reporta como fallo. Este diagnóstico informa sin saltarse protecciones de otros plugins.

Las flechas de la brújula son verdes y conservan diez segundos entre tandas. Al llevarla se anuncia el objetivo detectado o que no hay bosses ni huevos en ese mundo. Solo se repite al cambiar la señal, el mundo o volver a activar la brújula, no cada actualización.

Validación: 85 pruebas automáticas y compilación Java 21. No se ejecutó Paper ni se reprodujo el mundo del usuario; si persiste el bloqueo, /ri diagnostico y el aviso de consola permiten distinguir una regla de acceso, daño nulo, invulnerabilidad o interferencia externa. API: [rayTraceBlocks de Paper 1.21.11](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/World.html).


## Corrección del daño negativo en 1.7.5

El registro aportado muestra base=875.063 y final=-30.221724121093757: el golpe terminaba con daño negativo y podía aumentar la vida del mob hasta su máximo, sin activar el renacimiento. También aparecían residuos negativos de redondeo al calcular daño cero. No era una cura del tótem.

Se retira la búsqueda binaria que llamaba repetidamente a setDamage(double). Los ajustes de fases, bonificaciones Rare, defensa y daño de bosses pasan por SafeDamage: escala una instantánea de los modificadores aplicables sin ejecutar de nuevo sus funciones acumulativas. Cada reducción se limita al daño restante; la absorción no supera los corazones de absorción disponibles. Los resultados no finitos se anulan y un golpe cero no puede quedar negativo. Las cancelaciones existentes se respetan. No se cambia directamente la vida para infligir daño ni se eliminan los intervalos nativos de inmunidad.

La validación final de daño del boss se ejecuta antes de decidir si consume su tótem. Se conserva Protección X, Filo X, renacimiento único al 65% y daño doble después. El diagnóstico muestra también si el renacimiento ya fue usado y qué sostiene la mano secundaria. Los eventos cancelados de daño cero por redondeo ya no generan avisos repetidos ni se atribuyen automáticamente a otro plugin.

Validación: 93 pruebas automáticas. Una regresión con EntityDamageEvent reproduce el resultado -30.221724 del registro al reducir una base de 1067.15 a 875.063 con reducciones fijas; la nueva escala conserva un daño positivo sin invocar los callbacks. Se prueban 1000 instantáneas con reducciones extremas, rangos de daño alto y todas las fases, absorción, inmunidad, cancelaciones y el residuo -1.7881393432617188E-7. No se ejecutó Paper ni se reprodujo el mundo del usuario; falta confirmar el comportamiento en su servidor. Referencia: [EntityDamageEvent de Paper 1.21.11](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/event/entity/EntityDamageEvent.html).
