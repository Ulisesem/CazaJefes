# CazaJefes 1.31.0 · Habilidades por fase

El servidor crea `plugins/CazaJefes/combate.yml` al arrancar. El archivo incluye una rotación activa para Gorath, una rotación común para los demás jefes y cuatro habilidades editables. Los IDs y las fichas se siguen definiendo en `bosses.json` y `mobs/`.

## Crear y asignar habilidades

En `skills` puedes añadir habilidades con los siguientes campos.

| Campo | Función |
|---|---|
| `name`, `pattern`, `effect` | Nombre y patrón del ataque. Los patrones son `RINGS`, `CROSS`, `CONE`, `SPIRAL`, `METEORS`, `VORTEX`, `LANES`, `CHECKER`, `GEYSERS`, `NOVA`, `RUSH`, `CAGE` y `BEAM`. |
| `radius`, `damage`, `pulses` | Radio de 2 a 16 bloques, daño base de 0 a 180 HP por impacto y 1 a 6 pulsos. El daño final sigue las reglas de fase, armadura y eventos de Paper. |
| `cooldown` | Segundos de recarga. Se comparte si el mismo ataque aparece en dos fases. |
| `prepare-ticks`, `warning-ticks`, `interval-ticks`, `recovery-ticks` | Duraciones en múltiplos de 5 ticks. Veinte ticks equivalen a un segundo a 20 TPS. |
| `particles`, `sounds` | Activan las partículas y el audio del ataque principal. La barra y los marcadores mantienen la advertencia visual. |
| `dodgeable` | Con `true`, el daño sigue la geometría marcada. Con `false`, el golpe apunta al jugador fijado y requiere alcance y línea de visión. |
| `combo`, `summon` | Activa la combinación de seguimiento o la invocación de una horda en el primer pulso. |
| `ultimate` | Marca una habilidad YAML como definitivo. Solo se admite uno en la fase III. El ID reservado `definitivo` usa el ataque definido en el JSON de ese jefe. |

En `bosses`, añade el ID del jefe y asigna sus listas `phase-1`, `phase-2` y `phase-3`. Los IDs `nativo_1` a `nativo_4` permiten mezclar habilidades del juego anterior con las nuevas en las fases donde están disponibles. Las fases omitidas usan las listas comunes. `phase-2-health` y `phase-3-health` son porcentajes entre 1 y 99, en orden descendente. Los valores de base son 65 y 30. Las fases de un encuentro que ya avanzó se mantienen hasta que termine el encuentro.

Usa `/ri reload` para aplicar cambios del YAML con permisos de recarga. Reinicia para registrar un jefe nuevo en JSON. La consola indica las definiciones inválidas. La ficha del bestiario muestra los ataques asignados por fase.

## Amenaza y súbditos

- La amenaza suma daño efectivo, uso de libros en el encuentro y provocaciones. Pierde la mitad de su valor cada 12 segundos. Los atacantes válidos tienen prioridad y cada habilidad conserva sus reglas de alcance y cobertura.
- Para provocar, golpea primero al jefe y apunta a él a un máximo de 16 bloques. Usa `/ri provocar` para que te priorice cuatro segundos al seleccionar objetivo. La recarga es de 20 segundos por jefe. Los ataques ya disparados conservan su trayectoria.
- Cada horda configurada tiene seis entradas `ESPECIE:ROL`. `TANK` aumenta vida, armadura y resistencia al empuje. `HEALER` usa la cura existente. `ARCHER` dispara flechas que no se pueden recoger. `EXPLOSIVE` marca su zona antes de detonar sin romper bloques. `GUARD` usa el comportamiento habitual de combate. Los jefes sin una lista propia conservan sus súbditos por especie.

## Prueba rápida

Invoca a Gorath con `/ri boss gorath_quebrantador_de_mundos here`. En su primera fase alterna Corte del Presidio y Llamada del Presidio. La segunda empieza al 70 % y agrega el rayo. La tercera empieza al 30 % e incluye meteoritos y su definitivo.

Para probar también a [Azrak](examples/azrak_monarca_del_vacio.json), guarda su JSON en `plugins/CazaJefes/mobs/`. Añade este bloque bajo `bosses` en `combate.yml`, reinicia e invócalo con `/ri boss azrak_monarca_del_vacio here`.

```yaml
  azrak_monarca_del_vacio:
    phase-2-health: 70
    phase-3-health: 30
    phase-1: [corte_del_presidio, nativo_1]
    phase-2: [llamada_del_presidio, fractura_del_vacio]
    phase-3: [lluvia_de_obsidiana, definitivo]
    horde: [VINDICATOR:TANK, EVOKER:HEALER, STRAY:ARCHER, HUSK:EXPLOSIVE, VINDICATOR:GUARD, VINDICATOR:GUARD]
```

Las animaciones incluidas se ejecutan con Paper. Los modelos 3D importados siguen usando la integración opcional con Model Engine.
