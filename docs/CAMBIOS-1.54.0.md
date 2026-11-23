# CazaJefes 1.54.0

## Materiales y forja

Fragmento de Tributo pasa a PRISMARINE_SHARD. Quitina Resonante usa TURTLE_SCUTE (escama de tortuga). Ambos iconos se pueden cambiar en config.yml, items.tribute-fragment-material e items.resonant-chitin-material, con /ri reload. Los IDs permanecen iguales; las monedas y materiales antiguos se actualizan al abrir los menús, conservando cantidades. Una prismarina vanilla no sustituye al fragmento personalizado en recetas ExactChoice.

La estrella de regreso ocupa la casilla 49 (centro inferior). La pantalla principal presenta seis apartados separados y simétricos, sin los cinco accesos adicionales del pie. Los valores predeterminados de homeSlots pasan a 19/22/25/28/31/34; las posiciones personalizadas se conservan.

Materiales utiliza filas centradas de hasta cinco columnas y páginas de 20 objetos. Los 13 materiales actuales se distribuyen 4/5/4. Los libros se intercalan arriba/abajo y se paginan hasta 14, evitando casillas repetidas. El orden de materiales sigue siendo configurable mediante materialOrder en interfaces/recetas.json.

## Comando administrativo

`/ri materiales <id|todo> [cantidad 1-64] [jugador]`

Ejemplos:

- `/ri materiales todo` entrega una unidad de cada material que aparece en el apartado Materiales.
- `/ri materiales pvp_material_quitina 16` entrega 16 Quitinas Resonantes.
- `/ri materiales todo 1 Nombre` entrega el conjunto a otro jugador.

TAB muestra los IDs y jugadores disponibles. Usa el permiso administrativo de equipo existente. Si no cabe todo, no entrega ni consume nada. El catálogo se obtiene de las recetas activas, respetando recetas deshabilitadas.

## Combos de bosses

Tres golpes al mismo objetivo forman una secuencia con multiplicadores de daño bruto 1/1,12/1,24. Los golpes segundo y tercero mantienen el mínimo de 10 ticks entre liberaciones; al completar la secuencia se usa intervalTicks. Cambiar de objetivo, perder continuidad o aturdir al jefe reinicia la cadena. No se reinicia noDamageTicks, no se ignoran paredes, regiones o eventos cancelados, y los avisos especiales conservan su prioridad.

Se configura por jefe en ai.melee:

```json
{"comboHits":3,"comboIntervalTicks":10,"comboWindowTicks":60,"comboBonus":0.12}
```

comboHits admite 1–5 (1 desactiva cadenas), comboIntervalTicks 10–200, comboWindowTicks 20–400 y comboBonus 0–1. El daño real recibido depende de armadura, efectos y otros plugins. Reinicia tras editar JSON de bosses.

## Validación

370 pruebas automatizadas aprobadas (48 del motor y 322 del plugin). En Paper 1.21.11 se comprobó entrega de 13 materiales, Quitina con icono de escama y cantidad correcta, libros intercalados, regreso centrado, receta de brújula y combos. La sonda conectó 8 golpes en 85 ticks (4,25 s), con progresión de daño medida. La apariencia final requiere revisión en un cliente gráfico.

## Propuestas para ampliar el combate

- Contraataque perfecto: esquivar el tercer golpe abre 1,5 s de vulnerabilidad del boss, con aviso visual y una recarga por jugador.
- Contratos de cacería: objetivos por familia y hazañas (interrumpir una cura, salvar a otro jugador) que recompensen materiales, sin cambiar el contador de golpes finales.
- Resonancias de arena: zonas temporales telegrafiadas que el jugador puede usar para debilitar al boss o interrumpir un especial, respetando WorldGuard y sin romper bloques.

Estas tres propuestas son ideas para una siguiente actualización; no están activadas en 1.54.0.
