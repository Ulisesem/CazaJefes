# Ataques e IA central en CazaJefes 1.39.0

Los jefes combinan las rotaciones de fase de `combate.yml` (también configurables en `combat` por JSON) con los ataques de `ai.attacks`. Las rotaciones conservan sus avisos, patrones, combinaciones, habilidades de horda y definitivos. El mismo ciclo de IA controla cuándo puede comenzar un ataque. La defensa del armadillo y el refugio del Shulker también pasan por ese control.

Las ráfagas se simulan en el servidor y se muestran con PacketEvents. La andanada del saqueador usa seis tandas de cinco flechas. La batería dispara seis cohetes con daño de área. La órbita del Shulker tiene veinte orbes guiados, con veneno, Wither y levitación al impactar. Los arqueros reciben una tríada, y los magos tienen proyectiles de hechizo. El evocador puede invocar seis descargas en fase III.

Para ajustar los ataques de familia añade su ID a `ai.attacks` con el tipo `FAMILY_VOLLEY` o `LIGHTNING_CHAIN`. Una entrada con el mismo ID sustituye el valor predeterminado. `combat.legacyAttacks: false` desactiva las rotaciones de fase, las defensas especiales y los ataques de familia automáticos. Conserva cualquier habilidad que se configure explícitamente en `ai.attacks`.

- `FAMILY_VOLLEY`: `projectile` acepta `ARROW`, `FIREWORK`, `SHULKER` o `SPELL`. También puedes ajustar `salvos`, `shots`, `intervalTicks`, `guided`, `speed` y `area`.
- `LIGHTNING_CHAIN`: admite `strikes`, `intervalTicks` y `minimumPhase`. Los rayos visuales se dibujan con partículas y sonido sin prender fuego.
- `combat.compactBody`: activado por defecto. Reduce los cuerpos que sobrepasan 1.7 bloques de ancho o 2.85 de alto, manteniendo sus proporciones. Desactívalo por jefe si la arena tiene espacio.
- La comprobación de obstáculos se hace a los cuatro ticks horizontales o cinco verticales. Se conservan el registro de restauración, las listas de materiales y los permisos de arena.

## API pública

```java
var api = Bukkit.getServicesManager().load(CazaJefesApi.class);
api.combat(boss.getUniqueId()).ifPresent(info -> {
    // Estado, fase, estilo y ataques de la IA activa.
    getLogger().info(info.attacks().toString());
});
boolean queued = api.requestAttack(boss.getUniqueId(), "bateria_de_cohetes", player);
```

Llama a ambos métodos desde el hilo de Paper. `requestAttack` admite ataques de la IA expuestos en `attacks()` y comprueba rango, visión, fase aplicable y enfriamientos. También admite los IDs de `signatureAttacks()` cuando la rotación de la fase está disponible.
