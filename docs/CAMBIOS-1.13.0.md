# Cambios de 1.13.0

## Cuatro ataques especiales por fase

| Etapa | Ataques |
| --- | --- |
| I | Ascensión del Firmamento, Anillo de Ruptura, Corte del Umbral, Barrido del Guardián |
| II | Ascensión del Firmamento, Espiral de Asedio, Runas de Fractura, Vórtice del Ocaso |
| III | Ascensión del Firmamento, Ruptura del Horizonte, Lluvia del Cataclismo, definitivo propio del boss |

Se reutilizan los patrones de anillos, cruz, cono, espiral, runas, vórtice y meteoros. La vida, fuerza y daño por categoría se conservan. Las etapas aumentan el radio de los ataques de zona de seis a siete y ocho bloques, añaden pulsos y reducen los espacios entre especiales. Continúan los ataques básicos, proyectiles propios y súbditos; no cuentan como estos cuatro especiales.

## Desplazamientos

Ascensión del Firmamento lanza verticalmente a los jugadores visibles a doce bloques o menos. Está disponible desde la primera etapa, aumenta su impulso en cada fase y tiene diez segundos de recarga tras ejecutarse. Ruptura del Horizonte es el empuje fuerte de fase III, con ocho segundos de recarga. Ambos requieren una carga de 1,5 segundos. Si otro ataque está en curso, esperan su turno: no se superponen cargas. Por ello el tiempo entre ejecuciones puede ser mayor que la recarga indicada.

La carga, el nombre y las recargas aparecen sobre la hotbar; los ataques en preparación tienen prioridad sobre los contadores. Cada patrón tiene un sonido de ejecución. Se conserva la esfera blanca expansiva. Los jugadores con OP siguen excluidos de los desplazamientos de combate, como se solicitó en versiones anteriores. Se ven las advertencias y partículas aunque el jugador sea OP. Para probar el lanzamiento, usar supervivencia sin OP.

## Impacto de muerte

El impacto conserva radio de seis bloques, rayos visuales sin fuego y efectos negativos. Cambia el daño a ocho o nueve puntos (cuatro a cuatro corazones y medio), omitiendo armadura, Protección, resistencia y absorción; la manzana encantada no lo absorbe. Se usan eventos de daño normales: se respetan cancelaciones, bloqueo e inmunidad de impacto; no se asigna la salud directamente y los tótems siguen sus reglas. La regeneración puede recuperar vida después del golpe.

Tras un impacto aceptado se aplica un impulso radial pensado para desplazar aproximadamente ocho bloques en terreno libre. No teletransporta al jugador; paredes, suelo, líquidos y otros plugins pueden acortar o modificar el recorrido. El retroceso de muerte es independiente de la excepción OP de los ataques burst.

## Validación

Maven verify: 134 pruebas sin fallos. Se cubren rotación por fases, progresión, recargas, disponibilidad del definitivo, vectores y daño sin absorción. No se ha ejecutado un servidor Paper ni medido la altura o distancia reales con un cliente.
