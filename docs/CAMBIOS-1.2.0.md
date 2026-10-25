# CazaJefes 1.2.0 — huevos funcionales, apariencia y botín sin spam

## Correcciones y comportamiento

La ficha del bestiario mostraba un huevo como referencia de comando, pero no ejecutaba la colocación. Ahora el botón 40 coloca un huevo real con verificación de permisos y una superficie libre cercana. Se coloca bedrock debajo, se guarda el BlockData original y se restaura al eclosionar/romper el huevo si la base continúa siendo bedrock. No se reemplazan TileState (contenedores, etc.), soportes parciales ni otros huevos. El BlockData guardado sobrevive al reinicio mediante active-eggs.

Los Fragmentos de Tributo pasan de amatista a fragmentos del disco 5, conservando su PDC y cantidades mediante la migración de monedas. Los materiales vanilla no se convierten solo por tener el mismo nombre o apariencia.

Los objetos personalizados tienen glint visual mediante ItemMeta; no se usa un encantamiento artificial para generar el brillo. La brújula deja de llevar Unbreaking 10. Al abrir inventarios/entrar se actualizan las brújulas antiguas y se añade brillo a otros objetos del plugin sin borrar sus mejoras.

El Corazón de cada boss principal usa HEART_OF_THE_SEA, nombre del boss, categoría, brillo y marca de rare. Probabilidad base 1 %; configurable loot.boss-heart-chance, limitada a 0–1. Ningún minijefe lo deja, para conservar su tabla exclusiva de tributos/fragmentos. Es un trofeo, sin receta ni efecto activo nuevo. Galería con los 52 corazones posibles.

Los bosses pasivos y gigantes reciben asistencia de persecución/ataque. Se utiliza pathfinder hacia el objetivo, movimiento de apoyo también a corta distancia y ataque cercano manual con un segundo de espera y comprobación de visión. Los targets continúan limitados a supervivencia/aventura y al alcance de combate; probar en creativo no hace que te ataquen.

Se elimina el anuncio por cada objeto recogido y el contador de objetos restantes. Al morir el boss se emite un único mensaje con cantidades/nombres del botín real. Solo la adquisición efectiva de una rare del boss (arma especial, libro/esencia de propiedad o corazón) emite otro anuncio con el jugador. Las adquisiciones parciales no repiten el aviso para esa entidad; objetos que vuelve a soltar un jugador no llevan marca de procedencia del drop inicial.

## Forja

Siete secciones de recetas, más guía y galería: Tesorería, Cristales, Armería, Armaduras, Infusiones, Grimorios y Recetario completo. Los trofeos no se fabrican ni pueden extraerse del menú. Vistas de creación con pasos, ingredientes, resultado y botón de fabricación; se conserva cancelación de clics/drag y ejecución diferida para navegar/comprar.

## Validación

34 pruebas unitarias, Maven verify y ZIP/CRC/Java 21 correctos. Ocho regresiones nuevas: espacio/suelo del huevo, exclusión de contenedores/huevos existentes, corazón exclusivo de bosses principales y umbral del 1 %, silencio de pickups normales, adquisición parcial/real, no repetir avisos, asistencia a pasivos/gigantes y separación de recetas.

Estas pruebas no ejecutan un servidor ni simulan clics reales de Bukkit: no certifican colocación/restauración de bloques, apariencia, ruta de entidades o adquisición en el mundo. Falta probar 1.2.0 dentro de Paper 1.21.11.

## Casos manuales pendientes

1. OP: abrir bestiario, categoría, ficha y colocar huevo. Confirmar huevo + bedrock y que el clic derecho invoca al boss. Sin permisos no debe colocar.
2. Probar sobre cofre, soporte parcial, espacio ocupado y junto a otro huevo: no perder bloques/objetos. Probar reinicio con huevo colocado y restauración posterior.
3. Monedas y brújulas antiguas desde inventario/cofre: actualizar aspecto, mantener cantidades, usar en recetas; brújula sin Unbreaking 10, equipo con sus mejoras.
4. Inspeccionar brillo de monedas, cristales, equipo y corazones. Probar galería/paginación/guía y no extracción.
5. Boss pasivo en supervivencia: acercamiento y ataque dentro de rango, sin daño a través de pared; creativo no es objetivo.
6. Matar boss: exactamente un resumen de botín. Recoger objetos normales: silencio. Recoger rare/corazón: un aviso de quién lo recogió; inventario lleno no debe anunciar adquisición inexistente.
7. Confirmar que los 10 minijefes siguen dejando solo tributos y fragmentos. Probar con plugins reales de permisos/protección y aportar latest.log ante fallos.
