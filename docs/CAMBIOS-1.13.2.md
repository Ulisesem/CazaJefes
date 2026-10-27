# Cambios de 1.13.2

## Lanzamiento del burst

El ataque reserva todo el ciclo en el que se libera; no ejecuta un golpe normal ni empieza otro especial inmediatamente después. El impulso se envía al siguiente tick, después del ciclo que podía sustituirlo con el retroceso de un golpe. Si dos ticks después la componente vertical es mucho menor que la solicitada, se refuerza una sola vez. No se reintenta si se detecta una cancelación del evento de velocidad. No se teletransporta ni se atraviesan techos.

La selección usa la distancia al cuerpo visible del boss, no solo a su origen y ojos. Mantiene doce bloques de alcance, supervivencia/aventura e inmunidad OP al burst de combate. No se modifican daño, cargas ni recargas.

/ri diagnostico incluye el último lanzamiento del jugador: antigüedad, velocidad solicitada y observada, refuerzo y cancelación del evento. No identifica qué plugin canceló el movimiento. Sirve para distinguir un impulso sustituido o cancelado de un ataque que nunca seleccionó al jugador. Para consultar tras una prueba sin OP, un administrador puede devolver el permiso antes de ejecutar el comando.

## Boss inmóvil durante el burst

Desde que empieza a cargar, el boss queda anclado durante 48 ticks: la carga y la expansión visual. Se suspende su IA, gravedad y colisión; se conserva su posición sin cambiar salud ni invulnerabilidad. Al terminar se restauran exactamente los valores previos. Si muere, se descarga o se detiene el plugin se limpia el anclaje. El impulso del burst solo se envía a jugadores, nunca al propio boss. Una nueva etapa interrumpe el anclaje anterior y no deja estados acumulados.

## Tamaño de animales

Los bosses animales aumentan un 25% respecto de su escala anterior, hasta el límite de diez. Se aplica al aparecer o al restaurar sus sistemas, con una marca persistente para evitar aumentos acumulativos al recargar. No aumenta el tamaño de los súbditos. Se conserva el punto de aparición y la vida del boss.

## Validación

Maven verify y pruebas de refuerzo condicionado, cancelación e idempotencia del tamaño. Sin ejecución en Paper ni validación visual del lanzamiento. Los bloqueos de movimiento de otros plugins, vuelo, techos o desconexiones pueden impedir el resultado.
