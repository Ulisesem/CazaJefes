# CazaJefes 1.5.0

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

Compila con `mvn verify` y Java 21. Resultado: `target/CazaJefes-1.5.0.jar`. 58 pruebas automáticas, configuración, ZIP/CRC y bytecode Java 21 verificados. **No se ejecutó Paper ni se verificaron interfaces dentro de Minecraft**. Prueba equilibrio y compatibilidad en un servidor de pruebas.

Fuentes reconstruidas estáticamente desde 0.9.7: se preservó lo recuperable, sin recuperar comentarios ni estructura original exacta. El JAR original no se ejecutó.

## Cambios de combate en 1.5.0

Se elimina el regreso al origen; `combat.leash-radius` deja de utilizarse aunque figure en un archivo antiguo. No se rellena la vida ni se reinicia la etapa por desplazamiento. Los bosses permanecen en el agua: respiración acuática, gracia de delfín, eficiencia de movimiento acuático y persecución en tres dimensiones. No se fuerza la salida a superficie. Se cancelan teletransportes nativos que los expulsarían del agua; el Shulker combate allí con proyectiles sin saltar a tierra.

Shulkers: ocho proyectiles dirigidos por ráfaga, Veneno II durante cinco segundos si el impacto causa daño y se permite el combate. Ráfaga cada seis segundos, 4,5 en etapa final; máximo una ráfaga activa por boss y proyectiles con ocho segundos de duración. Sustituye disparos nativos adicionales. Al recibir golpes no letales, busca teletransporte cercano con espacio, suelo sólido y límites del mundo; pausa mínima 1,8 segundos. Bajo el agua no usa esos saltos.

Movimiento táctico común: anticipación corta, persecución con navegación, flanqueos alternados, retirada de atacantes a distancia, embestidas por fase y manejo de obstáculos. Los minijefes usan la misma lógica y golpe cercano; el zorro no abandona al jugador para cazar ovejas y tiene menor velocidad y embestida. No equivale a un sistema de aprendizaje automático ni reemplaza todos los comportamientos internos de Minecraft.

Avisos de etapa 1/3, 2/3 y 3/3 solo a jugadores que hayan causado daño permitido al boss. Se registran los golpes directos y proyectiles de jugador; siguen recibiendo avisos mientras estén conectados aunque dejen de golpear, se alejen o cambien de mundo. Mirar o acercarse no registra participación. Los UUID se conservan con el boss, incluso al recargar chunks o reiniciar. Al reconectarse se informa la etapa actual, sin repetir historial. Se conserva la etapa máxima alcanzada en los datos del boss. Cada boss tiene color estable y motivo de partículas en aura, ataques e invocaciones. La brújula en cualquiera de las manos muestra rastro y flecha hacia el objetivo más cercano del mismo mundo, sin el antiguo límite de 120 bloques; es una guía de dirección, no una ruta que evite obstáculos.

Los núcleos muestran únicamente su categoría; se conservan identificadores internos por compatibilidad. Protección VII/VII/VIII/VIII para Épico/Legendario/Mítico/Abismal. Filo sigue IV/V/VI/VII. La migración de inventario aplica el nuevo nombre y Protección a las piezas existentes.

API utilizada: [proyectiles Shulker de Paper 1.21.11](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/entity/ShulkerBullet.html). La compilación y pruebas de reglas no sustituyen una prueba dentro del servidor: comprueba especialmente agua, teletransportes, colisiones de mobs escalados, protección de terrenos y varios jugadores.


El anuncio único de derrota usa dorado y aqua con separadores, botín real y tabla de daño descendente de todos los jugadores registrados, incluido quien esté desconectado al morir el boss. Cuenta daño final permitido directo o de proyectiles de jugador, limitado a la vida restante para no sumar exceso del golpe final; daños ambientales y efectos sin autor atribuible no se asignan. Los totales y nombres se guardan en el boss. La tipografía sigue siendo la normal de Minecraft; no requiere paquete de recursos.
