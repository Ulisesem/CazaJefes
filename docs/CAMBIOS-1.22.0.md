# CazaJefes 1.22.0

## Armadillo

El control anterior desactivaba la IA del armadillo y repetía `rollOut()` durante cada actualización. Se introduce un control de apertura que habilita su IA mientras está desenrollándose y no vuelve a ordenar la apertura si ya está en UNROLLING. Al llegar a IDLE vuelve al movimiento asistido y se limpia la memoria de peligro que puede provocar miedo nativo.

Se mantienen cinco segundos de defensa, recarga mínima de quince segundos entre comienzos y la cura pequeña existente al terminar la defensa. No ataca mientras está enrollado o terminando de abrirse. La transición visual de apertura puede durar algo más que los cinco segundos de defensa. Se usa el mismo control para armadillos súbditos, que deben permanecer abiertos para atacar.

## Minijefes y súbditos

- Los minijefes priorizan acercarse o embestir a su objetivo. Se retiran metas vanilla de pánico, evasión, tentación, reproducción y seguimiento parental cuando corresponden, manteniendo el resto de su navegación y los golpes de contacto asistidos.
- El controlador compartido antes reservado a animales ahora registra todos los súbditos. Los de cuerpo a cuerpo persiguen al jugador; los animales conservan daño de contacto. Los hostiles normales siguen utilizando su ataque nativo.
- Arqueros, saqueadores, magos, blazes, ghasts y otros tiradores intentan mantenerse entre seis y doce bloques del cuerpo del objetivo: retroceden si está demasiado cerca, flanquean en el rango útil y se acercan si están lejos o sin línea de visión. Shulkers conservan su posición fija. Los ahogados con tridente se consideran tiradores.
- Llamas y gólems de nieve tienen apoyo de proyectiles dirigidos cada 2,5 segundos, con daño base de ocho más dos por rango, hasta 64 proyectiles propios y duración de tres segundos. No son homing ni aciertos garantizados. Otros tiradores conservan los disparos y recargas nativos.
- La dirección asistida comprueba colisiones y evita precipicios; permite un salto ante obstáculos. Los objetivos siguen limitados a supervivencia/aventura, mismo mundo y cercanía. Se conservan los límites de hordas, el máximo global y la retirada cuando muere el padre.

No se elimina el riesgo de atascarse en cualquier construcción: estas reglas necesitan pruebas con el terreno y los plugins reales del servidor. No se ignoran paredes ni cancelaciones de daño.

## Burst

El libro dibuja una esfera completa centrada en el cuerpo del jugador y lo sigue durante el efecto. Las ondas de bosses usan los semiejes horizontal y vertical de su caja corporal y un margen, para ajustar el efecto a tamaños distintos.

Cada fotograma contiene 24 microestrellas de cuatro puntos, hechas con polvo blanco de tamaño 0,35, distribuidas sobre una esfera o elipsoide. Son 96 emisiones por fotograma, frente a las 32 anteriores; cuatro fotogramas con expansión progresiva, para un máximo de 384 emisiones por efecto y espectador antes del presupuesto cosmético. Alcance de 32 bloques. No se amplía el presupuesto global existente, por lo que otros efectos simultáneos pueden reducir el detalle. No se pretende escalar el sprite FIREWORK: se usan pequeñas estrellas geométricas para poder controlar su tamaño.

## Nombres y descripciones

Se conserva el nombre personalizado y se añade el original entre paréntesis: **Ímpetu del Relámpago (Haste)**, **Pulso del Destierro (Burst)**, **Corazón Indómito (Hardy)**, **Alas del Firmamento (Fly)** y **Forja Viviente (Durability)**, entre otros.

Los libros no llevan el prefijo «Grimorio» y el apartado se denomina **Libros de habilidades**. Las descripciones en español, nivel, máximo, costo y recarga se conservan. Las esencias con poder muestran Rare Essence y el nombre correspondiente. Los libros existentes se refrescan con la presentación actual; no se cambian IDs ni poderes. La configuración se respalda en `rares-before-1.22.0.yml`.

Haste sigue dando **Velocidad hasta VII**. Se conservan el botín desde dieciséis bloques, su espiral de ocho segundos, los objetos individuales, la deduplicación de poderes y el acceso al sello Resonancia Primigenia.

## Validación

Maven verify: **207 pruebas aprobadas**. Incluyen apertura sin reiniciar UNROLLING, IA activa durante esa transición, ciclo de defensa existente, decisiones de distancia/cuerpo a cuerpo, nombres y geometría/presupuesto de esfera. JAR verificado como ZIP y bytecode Java 21, compilado contra Paper 1.21.11.

No se ha ejecutado Paper ni un cliente Minecraft. La prueba de armadillo simula los cambios de estado de la API; no comprueba la animación nativa en un servidor. La apariencia, agresividad efectiva y compatibilidad con otros plugins necesitan validación en juego.

Detener el servidor, respaldar la carpeta `plugins/CazaJefes`, retirar el JAR anterior e instalar solo CazaJefes-1.22.0.jar. Conservar configuraciones y datos.
