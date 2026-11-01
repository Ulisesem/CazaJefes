# CazaJefes 1.24.0 · Rares visibles y persecución

## Cambios de rares

Las ocho rares revisadas se activan con un impacto de proyectil que cause daño, sin exigir aciertos previos, inmovilidad ni distancia mínima. Las recargas siguen siendo por jugador y por rare; las probabilidades se evalúan en impactos aceptados. Jugadores, mobs y bosses son objetivos válidos. Los aliados del marcador y mundos sin PvP se respetan en objetivos jugadores.

| Rare | Nivel máximo | Probabilidad por nivel | Efecto / recarga |
|---|---|---|---|
| Carga Inestable | IV | 15/25/35/45% | Daño adicional 3/6/9/12 HP (1,5/3/4,5/6 corazones). Arcos y ballestas. Recarga 12 s. Ya no es trampa en el suelo. |
| Cerrojo del Vacío | V | 20/30/40/50/60% | Atracción directa al tirador, fuerza creciente. Recarga 10 s. |
| Quebranto | III | 25/40/55% | Lentitud I y Debilidad I durante 4/5/6 s, con partículas y aviso. Ya no exige disparar y después golpear cuerpo a cuerpo. Recarga 12 s. |
| Relevo Fantasma | IV | 20/35/50/65% | Al acertar prepara 6 s para agacharse y desplazarse a la derecha: 1–2 / 2–3 / 3–4 / 4–5 bloques aleatorios. Si ya estabas agachado, lo intenta al activar. No atraviesa bloques ni huecos sin suelo. Recarga 10 s. |
| Herida del Eclipse | IV | 20/30/40/50% | Sustituye Marca del Cazador: +1/2/3/4 HP al impacto y Wither I durante 2/4/6/8 s si la criatura admite ese efecto. Recarga 8 s. |
| Estela Boreal | III | 25/40/55% | Lentitud I–III durante 3 s. Corona y copos visibles alrededor del objetivo en varios pulsos. Recarga 10 s. |
| Pulso de Retorno | III | 20/35/50% | +1/2/3 HP al impacto; intenta curar hasta esa cantidad, limitado a la vida realmente perdida por el rival y tu vida máxima. Funciona cerca o lejos. Recarga 12 s. |
| Descarga de Tormenta | III | 20/35/50% | Sustituye Ojo de Tormenta: +1/2/3 HP al impacto, Lentitud I 3 s, destello eléctrico y sonido. Recarga 8 s. |

Los avisos indican al atacante el objetivo y la rare activada; el jugador afectado recibe aviso también. Pulso informa la cantidad de vida recuperada, que puede ser cero si ya tienes la vida máxima o se cancela la cura. Relevo avisa cómo activarlo y si no hay lugar seguro.

Los IDs técnicos `marca_cazador` y `ojo_tormenta` se conservan para que los objetos existentes reciban el nuevo nombre y comportamiento. No aparecen como recetas duplicadas. `/ri book` sigue dando libros con nivel; las recetas conservan esencias de magma. Los máximos y descripciones se actualizan al cargar el catálogo, con copia de seguridad de la configuración.

El daño extra se suma al mismo evento de impacto, no se intenta un segundo golpe dentro de los ticks de invulnerabilidad. No se fuerza daño en impactos cancelados, escudos/bloqueos completos ni inmunidad. Otros plugins, mitigaciones especiales de bosses o absorción pueden alterar el daño y la curación efectivos; los valores de la tabla son el daño adicional solicitado por cada rare. Ningún efecto periódico anula las inmunidades propias de una criatura.

## Recetas sin sección compartida

Se elimina Proyectiles compartidos del menú y se reparten sus rares:

- Velo del Eclipse (Blinding): solo cascos. Represalia contra atacante cuerpo a cuerpo, 25%, ceguera 3 s; recarga 8 s y costo 4 de saturación.
- Veredicto de la Tormenta (CallLighting): espadas y hachas. 10% de descarga con 2 HP extra por nivel en el impacto; rayo visual sin fuego.
- Grilletes de Escarcha (Slow): solo arcos. 25% de Lentitud I durante 4 s.

El arco PvP Ocaso queda en Arcos, no desaparece al eliminar el apartado compartido. El equipo que antes tenía una rare ahora incompatible deja de activar esa propiedad; puede aplicarse una esencia o libro nuevo al equipo compatible. No se transfieren propiedades automáticamente entre objetos.

## Armadillos y agua

- Armadillo: mantiene IA y física activas fuera de su defensa de 5 s. Se suprime el comportamiento pasivo nativo de miedo en Paper 1.21.11 y se fuerza la salida de la bola al acabar, sin reiniciar indefinidamente la animación. La regeneración mínima y recarga de 15 s se conservan. Los súbditos usan la misma corrección de movimiento, sin la defensa del jefe.
- El adaptador de armadillo inspecciona la API interna presente en Paper 1.21.11, sin compilar contra NMS. En una implementación distinta usa la apertura de Bukkit como alternativa. Esta parte debe comprobarse especialmente con el servidor real.
- Bosses y súbditos en el agua persiguen la altura real de los ojos del jugador, con velocidad vertical descendente, navegación activa y comprobación de colisiones; no se les obliga a flotar a la superficie ni se les regenera la vida por moverse.

Partículas bajo el alcance y presupuesto compartido existentes, sin efectos permanentes globales. Validación: Maven verify y pruebas unitarias de progresión, compatibilidad y regresiones. No se ejecutó Paper ni se midió rendimiento multijugador; faltan las pruebas de interacción real con OldCombatMechanics, armadillos, protección de regiones y persecución acuática.
