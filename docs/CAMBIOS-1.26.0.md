# CazaJefes 1.26.0 · Diez rares por arma

## Colecciones completas

Arcos (10): Carga Inestable, Descarga de Tormenta, Estela Boreal, Flecha del Silencio, Grilletes de Escarcha, Herida del Eclipse, Pulso de Retorno, Fulgor del Alba, Ceniza Solar y Sello de Ruina.

Ballestas (10): Carga Inestable, Cerrojo del Vacío, Destello del Umbral, Detonación de la Grieta, Grillete de Hierro, Impacto del Ariete, Munición Necrótica, Quebranto, Relevo Fantasma e Impacto Sísmico.

Carga Inestable se muestra en ambos apartados mediante una única receta y la misma esencia; no se duplica el objeto registrado. Se conservan los niveles y funcionamiento de las rares anteriores.

## Cuatro rares nuevas

Todas llegan a III, con probabilidad 20/35/50% por nivel, en impactos aceptados, sin depender de escudos. Costo de saturación 0 y recarga por jugador y por rare.

| Rare / ID | Arma | Efecto por nivel I/II/III | Recarga |
|---|---|---|---|
| Fulgor del Alba · `fulgor_alba` | Arco | Brillo 3/4/5 s para revelar la silueta del objetivo. No retira el efecto de invisibilidad. | 10 s |
| Ceniza Solar · `ceniza_solar` | Arco | Fuego 3/4/5 s; evento de combustión cancelable. No incendia bloques; respeta inmunidades al fuego. | 12 s |
| Sello de Ruina · `sello_ruina` | Arco | Reduce 20/35/50% las curas por eventos de regeneración durante 4 s. No acumula porcentajes entre atacantes; prevalece el nivel mayor. | 14 s |
| Impacto Sísmico · `impacto_sismico` | Ballesta | Levitation I 0,5/1/1,5 s e impulso vertical creciente. | 14 s |

Las cuatro tienen partículas, aviso de activación, receta y compatibilidad exclusiva. Jugadores, mobs y bosses son objetivos válidos, con sus inmunidades y controles existentes de aliados/PvP. Un plugin que cure mediante `setHealth` sin evento no puede ser reducido por Sello de Ruina. Los efectos de fuego/levitación no fuerzan el daño ni el movimiento sobre inmunidades propias o cancelaciones de otros plugins.

Las recetas conservan el patrón de forja: cuatro reactivos vanilla, dos materiales personalizados de la familia, núcleo, Rare Essence base y cristal épico. El recetario muestra coincidencias exactas. `/ri book <id> <nivel>` entrega libro; `/ri essence <id> <nivel>` entrega esencia de magma. Se mantiene el desbloqueo del equipo de boss.

## Recetario reorganizado

- Inicio con las nueve categorías en cuadrícula 3×3 y accesos a materiales, corazones, mercado e infusión separados de la colección.
- Paleta propia: tributos naranja, cristales/esencias morado, armas/PvP rojo, armaduras azul, libros cian, materiales marrón y recetario completo gris.
- Todas las categorías usan listas centradas cuando tienen pocos resultados; las grandes conservan paginación de 28 objetos. Diez esencias: dos filas de cinco en posiciones 20–24 y 29–33.
- Orden alfabético por nombre visible, cabeceras cortas, número de recetas y guía de infusión.
- Ficha de fabricación con ingredientes a la izquierda, flecha de resultado, objeto a la derecha, información y confirmación central. La guía explica cuándo se consumen materiales y cuándo se necesita espacio.
- Navegación actualizada con los mismos índices que los iconos; sin consumir ingredientes al abrir una receta.

## Validación

Maven verify y 242 pruebas: diez recetas por arma sin IDs duplicados, posiciones de menú sin solapamientos, reglas de las cuatro rares, progresión de reducción de curas y regresiones anteriores. JAR verificado como ZIP y Java 21. No se ha ejecutado Paper ni revisado la interfaz en el cliente Minecraft; faltan las pruebas visuales y de interacción real con OldCombatMechanics y otros plugins.
