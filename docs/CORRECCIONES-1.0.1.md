# CazaJefes 1.0.1 — correcciones de pruebas en creativo e interfaces

## Causa identificada

El usuario confirmó que probaba en creativo. En 1.0.0 la misma regla destinada a impedir ataques contra creativo se aplicaba al público de la bossbar y a los atacantes que podían generar crédito de drops. Eso ocultaba la vida y bloqueaba el daño/crédito en las pruebas administrativas. No se atribuye esa causa a pruebas hechas en supervivencia.

## Cambios

- La bossbar se muestra también a espectadores/creativos dentro del alcance visual. La selección de objetivos hostiles sigue limitada a supervivencia/aventura.
- `combat.allow-creative-admin-tests: true` permite daño y crédito de drops a creativos con permiso `cazajefes.admin` (OP por defecto). No permite participar a espectadores ni a creativos sin ese permiso. Se conserva alcance, visión, crédito reciente y pago único. En configuraciones antiguas sin esta clave se usa `true`; puede deshabilitarse explícitamente con `false`.
- El mismo permiso permite despertar huevos en creativo para pruebas.
- `/rares` ya no compara por identidad las envolturas de inventario: usa la posición de la vista para detectar un clic en el menú superior.
- `/rares` y los menús heredados ejecutan navegación/compras en el siguiente tick, después de cancelar el evento, y descartan acciones si el jugador ya cerró o cambió de menú.
- Se acepta clic izquierdo/derecho normal; se rechazan shift, teclas numéricas, offhand, fuera del menú e inventario inferior. Una compuerta por menú evita que varios paquetes en el mismo tick encolen varias compras.

La navegación diferida sigue la [documentación de InventoryClickEvent de Paper 1.21.11](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/event/inventory/InventoryClickEvent.html).

## Validación

Maven verify con Java 21: BUILD SUCCESS. 13 pruebas unitarias, cero errores/fallos: seis conservadas, tres de elegibilidad de atacantes y cuatro de selección de clics/compuerta. ZIP/CRC, versión 1.0.1 y bytecode Java 21 comprobados.

No se ha iniciado Paper ni se ha probado la GUI con un cliente. Las pruebas nuevas validan reglas y límites; no sustituyen pruebas reales de eventos, botín y bossbar. Si persiste un fallo, hace falta latest.log del servidor.

## Prueba recomendada

Detener el servidor, retirar 1.0.0, instalar únicamente 1.0.1 y reiniciar. Con OP, invocar un boss nuevo con `/ri boss coloso here`, dañarlo y derrotarlo. En creativo debe mostrar bossbar y dar drops tras la secuencia final. Probar `/rares`, navegación, detalle y fabricar con materiales. Repetir en supervivencia y verificar que creativo sin permisos sigue sin generar crédito. Reiniciar de nuevo y comprobar persistencia.
