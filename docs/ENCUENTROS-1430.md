# Encuentros de todos los jefes en 1.43.0

Los 168 jefes reciben un combo de dos oleadas, puntos débiles y adaptación al grupo. El ciclo central de IA decide cuándo ejecutarlo. Las rotaciones y ataques existentes siguen formando parte de la pelea.

| Familia | Combo | Oportunidad |
| --- | --- | --- |
| Asedio | Martillo del Asedio | Sal del primer abanico y cruza al otro lado para esquivar el segundo. |
| Cazadores | Emboscada de Colmillos | Las dos marcas aparecen en posiciones desplazadas. Rompe las huellas o cambia de posición. |
| Enjambres | Corona del Enjambre | Evita los focos sucesivos y rompe sus sellos. |
| Magos | Convergencia de Runas | Rompe dos runas para cortar la doble detonación. |
| Arqueros | Cruce de Saetas | Sal del corredor de tiro. Los puntos de mira pueden romperse. |
| Acuáticos | Resaca de la Fosa | Las oleadas atraen al jugador alcanzado y aplican fatiga. Rompe los nudos de corriente. |
| Caparazones | Bastión Quebradizo | La primera onda golpea el centro. La segunda deja un hueco interior seguro. |
| Vacío | Eclipse de Fracturas | Dos cruces de daño dejan espacios diagonales. Rompe dos anclajes para interrumpir. |

Las posiciones y orientación varían según el ID del jefe. Las fases aumentan el radio y acortan la carga, con un mínimo de dos segundos. Cada encuentro toma una instantánea de los objetivos antes de descargar.

## Interrupción y contraataque

Los sellos visibles pueden romperse al golpearlos o interactuar con ellos cerca y con línea de visión. Romper los sellos requeridos cancela ambas oleadas y deja dos segundos de vulnerabilidad con un 15 % más de daño recibido. Si ambas oleadas no dañan a nadie, se abre la misma ventana. El límite global de 48 sellos evita multiplicar entidades en peleas simultáneas. Si no hay espacio o cupo para crearlos, se puede obtener la ventana esquivando.

## Adaptación al grupo

Solo cuentan los UUID que han hecho daño válido al jefe, hasta seis por defecto. El máximo alcanzado se conserva durante el encuentro.

- Un participante mantiene la resistencia base.
- Dos o tres usan 1.5 veces la vida efectiva y hasta dos marcas.
- De cuatro a seis usan 2.2 veces la vida efectiva y hasta tres marcas.

Al aumentar la vida máxima se conserva el porcentaje restante. Si Paper limita el atributo, se compensa reduciendo proporcionalmente el daño entrante, sin regenerar vida. El daño de los especiales no crece con el número de jugadores. La vulnerabilidad se aplica también al equipo que ya está participando.

## Configuración por jefe

El inicio añade el objeto combat.encounter a los JSON existentes que aún no lo tengan. Modifica ese objeto y reinicia:

```json
{
  "enabled": true,
  "family": "AUTO",
  "groupScaling": true,
  "weakpoints": true,
  "cooldownTicks": 440,
  "chargeTicks": 60,
  "vulnerabilityTicks": 40,
  "vulnerabilityBonus": 0.15,
  "maximumPlayers": 6
}
```

family acepta AUTO, BRUISER, HUNTER, SWARM, CASTER, ARCHER, AQUATIC, SHELL y VOID. enabled desactiva estas tres mecánicas en ese jefe. El resto de ajustes de IA conserva sus propias opciones.

La API permite solicitar el combo mediante requestAttack(uuid, "encounter_combo", jugador), respetando la disponibilidad y las recargas. BossAttackEvent identifica este combo con encounter_combo. El bestiario muestra su nombre y cómo interrumpirlo.

WorldGuard se consulta antes de colocar sellos y antes de cada daño. La limpieza del encuentro retira sus sellos y cancela su secuencia.
