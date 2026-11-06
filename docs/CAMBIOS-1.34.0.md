# CazaJefes 1.34.0 · Contraataque

## Interrumpe el ataque

- Gorath prepara una segunda descarga dos segundos después de clavar el hacha en su último corte. Golpea el hacha o haz clic derecho sobre ella para cancelar la descarga. Si se completa, inflige daño dentro de tres bloques, sujeto a cobertura y protecciones.
- Los arcanos permiten cancelar la onda de Anclas del Vacío al romper dos runas. Una sola runa todavía abre el refugio local.
- Si un arquero pierde la línea de tiro al jugador que fijó para su Tríada, cancela la carga y busca un ángulo lateral cuando hay suelo libre.
- Interrumpir la descarga de Gorath o las Anclas abre cuatro segundos de recuperación. La barra anuncia la interrupción a los jugadores cercanos.

## Avisos privados

El jugador fijado ve **TE ESTÁ APUNTANDO** en su barra de acción. En las cargas comunes también recibe partículas privadas sobre el jefe. Los avisos compartidos de peligro siguen disponibles para el grupo. El plugin usa el envío de [PacketEvents](https://docs.packetevents.com/sending-and-simulating-packets/) si está instalado y disponible. El mismo aviso se puede mostrar con Paper.

## Límites de WorldGuard

Con WorldGuard instalado y una región de arena ya creada, agrega sus IDs a tu `plugins/CazaJefes/config.yml`.

```yaml
combat:
  worldguard-arenas: [arena_gorath, arena_arcaneth]
```

Añade la línea bajo tu sección `combat` existente y usa `/ri reload`. El combate cruza un límite solo cuando ambas posiciones están fuera de todas las arenas listadas o comparten alguna arena. Los jefes y los súbditos guardan su origen, además de comprobar su posición actual. Las invocaciones que quedarían fuera se cancelan. Se comprueban el daño de combate, la Caída Final y los efectos de desplazamiento de los jefes cubiertos por la integración. La lista vacía deja el modo habitual. Si configuras arenas y WorldGuard no está disponible o falla la consulta, se bloquea el combate afectado y se indica el problema en consola. [Consultas espaciales de WorldGuard](https://worldguard.enginehub.org/en/latest/developer/regions/spatial-queries/)

## Prueba

Invoca a Gorath con `/ri boss gorath_quebrantador_de_mundos here` y espera el último corte. Invoca a Arcaneth con `/ri boss arcaneth_maestro_del_vacio_azul here` y rompe dos anclas en la fase III. Para verificar la protección, prueba con dos jugadores a lados opuestos del límite de una región configurada.
