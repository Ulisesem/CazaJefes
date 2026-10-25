# CazaJefes 1.3.0 — Fénix Astral, tormenta y mercado

## Nombres y economía

El usuario pidió retirar el nombre del Coloso. Los nombres visibles son Sello del Renacer y Amuleto del Fénix Astral. Se conservan las claves internas sello_coloso/amuleto_coloso para compatibilidad. El objeto final sigue siendo TOTEM_OF_UNDYING; necesita estar en una mano para funcionar como un tótem normal, no como un accesorio equipado en otro slot.

Por defecto, 64 Fragmentos de Tributo se canjean por un Sello del Renacer. Cuatro sellos y ocho Tributos del Cazador se canjean por un amuleto: coste equivalente 256 fragmentos + 8 tributos. No hay receta gratuita ni se conserva el antiguo canje directo por 32 fragmentos. Los administradores mantienen sus comandos de entrega.

```yaml
market:
  sigil-fragments: 64
  amulet-sigils: 4
  amulet-tributes: 8
combat:
  phase3-lightning:
    enabled: true
    threshold-damage: 40
    cooldown-seconds: 4
    max-damage: 30
```

Los ajustes funcionan también si no estaban escritos en un config antiguo. Recarga y reabre los menús para refrescar vistas; una confirmación con precio antiguo pide revisar el precio nuevo. Los costes de sello y cantidad de sellos nunca se permiten por debajo de uno.

## Mercado rediseñado

Entrada /mercado o /grieta, compatible con /ri trade y /ri canjear. Seis salas con iconos, bordes, brillo, bóveda de monedas y páginas. Una oferta lleva a una confirmación que separa costes, resultado y compra. Actualización del inventario solo tras verificar saldo y espacio; se conserva rechazo de shift, drag, números y vistas previas, y compuerta de una acción pendiente por vista. Enlaces entre mercado y forja.

Se reutiliza el catálogo completo de ofertas anterior. Solo cambia la vía de obtención del amuleto, además de su nombre visible y el nuevo sello. Los nombres del equipo y las demás categorías se conservan.

## Combate

Fuerza V usa amplificador 4 y Prisa VI amplificador 5. Se aplican al iniciar/reanudar sistemas y se renuevan mientras el boss combate. El normalizador antiguo sobrescribía el daño del evento; ahora incluye el bono de Fuerza de 15 puntos, antes de multiplicadores de fase. La transición de fase ya no reduce Fuerza a niveles I/II.

Control de ataques cada 5–10 ticks y melee manual para todos los bosses con espera de 500 ms, rango y visión. El daño sigue condicionado por inmunidad normal de Minecraft, armadura y otros plugins. Prisa por sí sola no acelera la IA de todos los mobs; la cadencia se implementa por separado.

La represalia solo cuenta daño efectivo de eventos aceptados, de jugadores en supervivencia/aventura contra bosses que ya están en fase 3. Los golpes letales interceptados por la secuencia final no generan un rayo después de la muerte. Proyectiles de jugador cuentan. Creativo/espectador no recibe represalias.

A 40 puntos acumulados por jugador/boss se prepara un rayo. La espera mínima es de cuatro segundos por jugador, compartida entre bosses. Con carga 40 causa 24 puntos base; carga 60 o superior llega al tope predeterminado de 30. Armadura, efectos y plugins pueden mitigar/cancelar. Se comprueban de nuevo vida, fase, mundo, distancia y visión antes de aplicarlo.

Se usa strikeLightningEffect y DamageSource LIGHTNING_BOLT dirigido al jugador. No se multiplica otra vez por Fuerza/fase ni genera una cadena de represalias por espinas. Se reinicia la inmunidad de golpe solo para este rayo, restaurándola si el daño no se aplica. Las cargas se limpian por muerte del boss, descarga de chunk y salida del jugador.

## Apariencia del fragmento

Nombre de objeto y displayName: Fragmento de Tributo. HIDE_ADDITIONAL_TOOLTIP oculta el texto nativo del disco; también se aplica a los iconos del mercado/forja con ese material. No se necesita un paquete de texturas. Su PDC y las cantidades se conservan al actualizar las monedas.

## Validación y pruebas pendientes

42 pruebas unitarias aprobadas y Maven verify correcto. Ocho nuevas: niveles/cadencia, acumulación de daño, fase/umbral/cooldown, escalado/tope de rayos, límites de costes, consumo entre stacks, compra fallida sin modificar monedas y seis departamentos distintos. Descriptores y Java 21/ZIP/CRC comprobados.

No se ejecutó Paper ni un cliente para esta versión. Faltan pruebas de IA real, tooltip del disco, renderizado/navegación del mercado, mitigación de rayos, interacción con inmunidad/otros plugins y resurrección del amuleto.

Prueba en una copia: actualizar el JAR con servidor detenido; abrir mercado, comprar sellos y confirmar amuleto con/sin saldo/espacio; sostenerlo en una mano y comprobar resurrección. En supervivencia atacar un boss en fase 3 para cargar rayos y revisar su cooldown. Confirmar Fuerza V/Prisa VI y ataques cercanos en bosses nuevos y reanudados. Verificar el tooltip de monedas antiguas y nuevas; aportar latest.log y versión exacta de Paper ante fallos.

Referencias: [DamageSource.Builder](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/damage/DamageSource.Builder.html) e [ItemMeta](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/inventory/meta/ItemMeta.html).
