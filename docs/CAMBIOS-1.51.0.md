# CazaJefes 1.51.0

Esta actualización parte de `testRefactor` 1.50.0 y conserva sus cambios de IA, restauración de terreno a los 20 minutos y protección de bedrock natural. Paper 1.21.11 y Java 21; PacketEvents sigue siendo una dependencia necesaria.

## Combate y huevos

- Los bosses terrestres ya no ascienden artificialmente para perseguir a un jugador que está volando con Fly. Conservan su respuesta contra jugadores subidos a plataformas y su desplazamiento sobre agua. Los acuáticos conservan su movimiento de inmersión.
- El pedestal temporal del huevo vuelve al bloque original al abrirlo o retirarlo. Una base que ya era bedrock natural se conserva. Un huevo antiguo sin datos de procedencia conserva su base por seguridad.
- Todos los huevos usan una hélice negra y morada de dos bloques de altura y dos de diámetro. Mantiene 32 puntos por actualización, sin duplicar el presupuesto de partículas.
- El hacha arrojada conserva el material, nombre y encantamientos del hacha real. Solo cambia la mano principal: preparación, mano vacía al lanzar, espada de respaldo al clavarse y recuperación del arma original después de 15 segundos. La espada lleva Filo X e Irrompibilidad X y aporta el daño de Filo X a los golpes cuerpo a cuerpo de la IA.
- El equipo completo de seis ranuras se sincroniza con los observadores del boss; los cambios de mano no envían una armadura vacía. El controlador de cambio de armas respeta un lanzamiento en curso.
- Los saqueadores con ballesta pueden pedir una escolta de seis: cuatro saqueadores a distancia y dos defensores cuerpo a cuerpo. Respeta su aviso, enfriamiento y presupuesto de súbditos.

## Rares

- Las 28 rares de armadura y las 28 de espada/hacha alcanzan IV. Los efectos con probabilidad general pasan a 30/45/60/75%; las mecánicas de combo conservan sus requisitos de golpes.
- VampiricRegeneration alcanza III: 25/35/45% de activación, curación de 25/30/35% del daño y tope de 4/5/6 HP. Slow y Weaken llegan a III con 30/45/60% y 4/5/6 segundos.
- Se conservan los daños específicos solicitados para Carga Inestable y Pulso de Retorno. Las utilidades y el control a distancia usan progresión propia, sin añadir niveles vacíos.
- Garlic puede impedir la curación por robo de vida; Cleanse elimina veneno, Wither, lentitud, debilidad y los bloqueos de curación Hex/Ruin, con una protección breve para que no se reapliquen inmediatamente. Anchor contrarresta impulsos y Clarity reduce ceguera y náuseas de rares compatibles.
- Los comandos aceptan el nombre corto entre paréntesis sin escribir los paréntesis. Por ejemplo: `/ri book poison 3`, `/ri essence unstable 4`, `/ri essence voidlock 5`. Los IDs técnicos existentes siguen aceptándose y los objetos conservan sus datos.

## Recetas

Inicio con seis apartados: Esencias, Libros, Armas forjadas, Cristales, Materiales y Tributos. Los accesos secundarios quedan en el pie; las páginas centran sus recetas y usan una navegación uniforme. Cada detalle muestra ingredientes, resultado y una única acción de fabricación.

Se retiraron los crafteos de armaduras de boss del registro y de los menús, incluidas las recetas antiguas. Las esencias para armaduras permanecen. Las armaduras de boss siguen saliendo como botín y con `/ri equipo abismal armadura`; `/riequipo` conserva compatibilidad.

## Configuración

Los 168 JSON incluidos y los archivos instalados reciben solo las opciones ausentes. No se reemplazan ataques ni ajustes del administrador.

En `bosses/<categoría>/<id>.json`, dentro de `combat`:

```json
{
  "mobility": {"pursueFlyingPlayers": false, "climbSpeed": 0.35, "climbRange": 6, "waterWalking": true},
  "eggParticles": {"radius": 1, "height": 2, "points": 32, "removeGeneratedPedestal": true},
  "axeThrow": {"enabled": true, "lodgedTicks": 300, "flightTicks": 10, "visualScale": 2.1, "swordSharpness": 10, "swordUnbreaking": 10},
  "crossbowSupport": {"enabled": true, "count": 6, "cooldownTicks": 500, "firstDelayTicks": 100, "lifetimeTicks": 800, "minimumPhase": 1}
}
```

Los tiempos de estos controles se expresan en ticks (20 ticks = 1 segundo); reinicia para cargar cambios de los JSON de bosses. `crossbowSupport` se aplica a saqueadores o entidades que porten ballesta; `axeThrow` a sus ataques de hacha.

`rare-balance.json` contiene las probabilidades por nivel, enfriamiento y multiplicadores de daño, curación, duración e impulso para los efectos compatibles. `/ri reload` recarga el balance. Los valores inválidos usan el valor predeterminado; un JSON inválido conserva el último balance válido.

`interfaces/recetas.json` permite cambiar título, nombres, color de panel, seis posiciones del inicio y cantidad de recetas por página (1–28). Se recarga al abrir el menú; las posiciones repetidas o inválidas conservan el último diseño válido. `recipes.yml` conserva los ajustes de ingredientes de las recetas restantes.

## Validación

Los cuatro módulos compilan y las 338 pruebas automatizadas pasan (41 del motor y 297 del plugin). La verificación adicional en Paper confirma vuelo, pedestales, comandos, recetas, una escolta de cuatro saqueadores y dos defensores, y recuperación del arma a los 15 segundos sin perder armadura ni tótem. Cuatro clientes de protocolo recibieron paquetes de equipamiento completo. La apariencia final requiere verla desde un cliente de Minecraft; las pruebas de protocolo no sustituyen esa revisión visual ni una sesión de balance PvP con jugadores.
