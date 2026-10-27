# Cambios de 1.14.0

## Saqueadores con ballesta

Los bosses de tipo PILLAGER llevan Ballesta del Destierro. No modifica vindicadores ni otros illagers que usan armas distintas. El arma NPC tiene:

| Encantamiento | Nivel |
| --- | --- |
| Infinidad | I |
| Carga rápida | X |
| Multidisparo | XV |
| Irrompibilidad | X |
| Reparación | I |
| Empuje | X |
| Perforación | X |

La combinación incluye encantamientos incompatibles con una ballesta vanilla y niveles superiores al límite normal. Se guardan como encantamientos del objeto, pero el comportamiento de munición, ráfaga y empuje se ejecuta por código. La ballesta NPC es irrompible y no forma parte del botín.

### Tempestad del Destierro

Cinco impactos positivos recibidos en una ventana de ocho segundos preparan una represalia contra el último atacante disponible. Se pueden combinar impactos de varios cazadores. Un golpe letal no inicia la habilidad. El umbral se configura con combat.pillager-retaliation-hits, entre tres y veinte, predeterminado cinco.

Cuando no hay otra carga en curso, prepara la ráfaga durante medio segundo, muestra su nombre sobre la hotbar y dispara quince flechas en un abanico de 49 grados. El daño usa la referencia de los ataques especiales y los impactos aceptados aplican un empuje fuerte hacia fuera. Recarga: doce segundos tras ejecutar la ráfaga. El daño de una misma ráfaga se acepta una sola vez por jugador, para evitar sumar quince impactos instantáneos. Las flechas pueden atravesar otras entidades conforme a Perforación X, pero no dañan mobs ni bosses aliados.

El saqueador también realiza disparos normales cada 2,5 segundos cuando está libre. Ambas modalidades requieren un objetivo válido y línea de visión, con alcance de 24 bloques. Usan munición creada por el NPC, sin consumir inventarios ni necesitar flechas. Se suprimen los disparos nativos para evitar problemas de la carga rápida de nivel X.

Todas las flechas están marcadas como no recogibles, incluido en creativo. Se eliminan al cumplir cinco segundos o al retirar el boss. Se limita a 32 proyectiles vivos por boss. No provocan fuego ni destrucción de bloques. Solo la ráfaga de represalia usa el empuje X programado; los disparos normales conservan su función de hostigamiento.

## Burst diagonal

Ascensión del Firmamento mantiene la velocidad vertical, la levitación X de veinte ticks y sus tiempos anteriores. La componente horizontal hacia fuera pasa de 0,35 a 1,25: el jugador sale en diagonal, alejándose del boss. No se cambia el anclaje del boss ni la inmunidad OP al burst. La altura real sigue dependiendo del terreno, efectos y plugins del servidor.

## Validación

Maven verify: 147 pruebas sin fallos. Se verifican los niveles configurados, las quince direcciones distintas del abanico, el umbral y ventana de golpes y la conservación del mecanismo vertical. No se ha ejecutado esta versión en Paper ni comprobado visualmente los proyectiles o el desplazamiento diagonal.
