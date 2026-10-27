# Cambios de 1.13.1

## Potencia X y ataques especiales

Los desplazamientos del boss usan potencia X: Ascensión tiene velocidad vertical de 2,45, 2,55 y 2,65 por etapa; Ruptura usa impulso horizontal 3,1. Se conservan nombres, cargas y recargas de 1.13.0. Potencia X es un ajuste de la habilidad, no un efecto vanilla ni un cambio en los libros del jugador. Los OP siguen exentos del burst de combate.

Los especiales ofensivos se calculan a partir del mayor valor entre el ataque real del boss y la referencia de su categoría. Los normales de zona usan un multiplicador de 1,7 a 1,9 según etapa; el definitivo usa 2,15. Se aplican las escalas de fase existentes. Los especiales tienen un límite de 200 puntos antes de la mitigación del jugador, separado del límite de 80 de los golpes normales. El daño final depende de armadura, encantamientos, bloqueo, inmunidad y otros plugins. Ascensión y Ruptura mantienen su naturaleza de desplazamiento, con daño de caída normal.

## Súbditos animales

Todos los bosses cuya criatura es un animal o bestia invocan súbditos de su propia especie. Un controlador común selecciona jugadores en supervivencia/aventura, los persigue, impulsa a animales pasivos y ataca en contacto cada 1,2 segundos si hay línea de visión. No depende solo de los objetivos nativos de Minecraft.

Los animales tienen Fuerza I, 120–220 puntos de vida y armadura adicional donde la criatura lo permita. Cada golpe directo de entidad puede quitar hasta el 40% de su vida máxima; un golpe normal no puede matarlos desde vida completa. El límite no aplica a /kill, daño ambiental o eliminaciones externas. Se conserva la probabilidad de fragmentos del sistema de súbditos. Desaparecen si su boss deja de estar activo o se desactiva el plugin.

## Lanzamiento al morir

El impacto tiene una componente vertical de 1,2 y horizontal de 0,85, con una segunda aplicación al siguiente tick para evitar que el impulso del daño lo sustituya. Se aplica aunque no haya pérdida de vida por absorción o inmunidad, salvo cancelación explícita del evento. Conserva el radio de seis bloques y el daño de cuatro a cuatro corazones y medio que ignora absorción.

La prueba de lanzamiento también admite creativo con permiso administrativo y pruebas creativas habilitadas; no causa daño a un creativo invulnerable. El retroceso de muerte no está exento por OP. El vuelo creativo, paredes, líquidos o plugins de movimiento pueden cambiar el recorrido. La distancia horizontal se aproxima a ocho bloques en espacio libre y no se garantiza una altura o distancia fija.

## Validación

Maven verify: 138 pruebas sin fallos. Incluye escalas de especiales, potencia X, vida de súbditos, límite por golpe y daño no negativo. No se ha ejecutado el servidor Paper ni medido el movimiento con un cliente real.
