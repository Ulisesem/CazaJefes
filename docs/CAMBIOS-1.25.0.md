# CazaJefes 1.25.0 · Colecciones de rares

## Relevo Fantasma

Se elimina la exigencia de suelo seguro bajo el desplazamiento. Puede terminar en el aire y cruzar huecos o bordes; la caída posterior es normal. Si hay un escalón en la trayectoria busca una elevación libre de hasta 1,25 bloques, sin atravesar bloques, techos, bordes del mundo ni chunks descargados. Conserva distancia por nivel I–IV (1–2 / 2–3 / 3–4 / 4–5 bloques), probabilidad, recarga y activación agachándose. Se respeta la cancelación del teletransporte.

## Siete recetas para arcos y siete para ballestas

Arcos: Carga Inestable, Herida del Eclipse, Estela Boreal, Pulso de Retorno, Descarga de Tormenta, Grilletes de Escarcha y la nueva Flecha del Silencio.

Ballestas: Cerrojo del Vacío, Quebranto, Relevo Fantasma, Impacto del Ariete, Munición Necrótica, Destello del Umbral y Grillete de Hierro. Carga Inestable sigue siendo compatible también con ballestas, pero su única receta está en Arcos para no duplicarla.

| Rare nueva | ID | Efecto | Recarga |
|---|---|---|---|
| Flecha del Silencio · arco | `flecha_silencio` | Debilidad I durante 3/4/5 s; no bloquea comandos ni habilidades. | 10 s |
| Impacto del Ariete · ballesta | `impacto_ariete` | Empuja al objetivo alejándolo del tirador, con potencia creciente. | 12 s |
| Munición Necrótica · ballesta | `municion_necrotica` | Wither I durante 2/4/6 s. | 12 s |
| Destello del Umbral · ballesta | `destello_umbral` | Ceguera durante 1/2/3 s. | 14 s |
| Grillete de Hierro · ballesta | `grillete_hierro` | Lentitud I/II/III durante 2 s. | 10 s |

Las cinco llegan a III y tienen probabilidades 20/35/50%. Se activan en impactos con daño aceptado, tienen partículas y avisos, respetan inmunidades de criaturas y controles existentes de aliados/PvP. No añaden daño directo adicional; el Wither causa su daño periódico normal. Las recetas usan los seis materiales de forja existentes, núcleo, esencia y cristal épico. `/ri book` entrega libros; `/ri essence` entrega bolas de magma.

## Presentación de todas las rares

- Todas las listas de esencias y libros se ordenan alfabéticamente por nombre visible.
- Colecciones de hasta siete: fila central. Hasta catorce: dos filas centradas. Colecciones grandes conservan paginación de 28 posiciones.
- Cabecera propia del equipo, tamaño de colección y guía inferior de infusión.
- Las esencias conservan efecto, nivel máximo, compatibilidad y recarga; la ficha de receta muestra ingredientes exactos, resultado y confirmación de fabricación.
- No se altera la cantidad de ingredientes ni se consumen materiales al navegar.

Validación: Maven verify; pruebas de siete recetas por arma, posiciones sin solapamientos, habilidades nuevas y rutas con huecos/escalones. No se ha ejecutado el cliente Minecraft ni medido el aspecto dentro de Paper.
