# CazaJefes 1.7.3

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

Compila con `mvn verify` y Java 21. Resultado: `target/CazaJefes-1.7.3.jar`. 78 pruebas automáticas, configuración, ZIP/CRC y bytecode Java 21 verificados. **No se ejecutó Paper ni se verificaron interfaces dentro de Minecraft**. Prueba equilibrio y compatibilidad en un servidor de pruebas.

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
