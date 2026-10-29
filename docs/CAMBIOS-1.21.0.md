# CazaJefes 1.21.0

## Aparición y minijefes

- Al despertar un huevo se retira temporalmente su bloque antes de crear el boss. Si la creación falla o lanza una excepción, se restaura el huevo y no se consume. Antes, el bloque podía impedir validar la caja corporal, sobre todo en criaturas grandes.
- Apariciones con motivo CUSTOM y marca de boss antes del evento de creación. Los plugins de protección pueden seguir cancelándolas; no se desactiva su protección. La consola distingue cancelación, falta de espacio y error de creación.
- Búsqueda segura ampliada hasta quince bloques y 96 comprobaciones de colisión, sin cargar chunks para buscar ni modificar el terreno. No fuerza una criatura dentro de bloques cuando no existe lugar adecuado. La dificultad Pacífico y restricciones externas siguen pudiendo impedir criaturas hostiles.
- El daño de contacto usa la parte visible del cuerpo, no solo la línea ojo a ojo ni la distancia al centro. El guardián de combate usa la misma comprobación para contacto, conservando distancia, modo de juego, paredes y cancelaciones.
- Los minijefes actualizan su controlador cada cinco ticks y tienen contacto independiente de la rotación de habilidades: 26/29/32 de daño base en etapas I/II/III, intervalo mínimo de 850 ms. El escalado de fase y las defensas normales siguen aplicándose; estas cifras no son daño de salud garantizado. No atacan hechos bola ni durante las cargas de burst/mirada que reservan el cuerpo.
- Persecución más enérgica en minijefes, conservando el límite del zorro. Las actualizaciones de ruta, colisiones, respuesta contra refugios y enfriamientos siguen limitadas; no se afirma que todos los terrenos sean resolubles.

## Haste, Burst y partículas

Haste se denomina **Fervor del Artífice** y ahora aplica la poción Prisa minera I–III durante treinta segundos por nivel. Se eligió Prisa como interpretación de Haste; la lista anterior lo había descrito como Velocidad. El costo de cuatro puntos de saturación y recarga de diez segundos se mantienen. Haste VI de los bosses no cambia.

Los libros propios del plugin habilitan el uso nativo del objeto en el aire al inicio del evento; las protecciones que posteriormente lo denieguen siguen respetándose. La activación real continúa comprobando saturación, recarga y objeto auténtico. No se promete compatibilidad con todas las prioridades de todos los plugins externos.

Los bursts de grimorio reutilizan la aplicación de velocidad del siguiente tick y un refuerzo limitado cuando corresponde. Conservan dirección lateral y elevación del libro, pero no reciben automáticamente la levitación X reservada al burst de boss. Los jugadores OP siguen excluidos del empuje y los compañeros del scoreboard no son objetivos.

Los anillos corporales expansivos ahora emiten **FIREWORK**, destellos blancos de estrella nativos, en lugar de cruces de polvo muy pequeñas. Son 32 emisiones por fotograma, ocho fotogramas, alcance de 32 bloques y presupuesto compartido. La forma se ajusta a la caja corporal de jugadores y bosses. El detalle puede reducirse con muchos efectos simultáneos.

## Rares y sello

- Solo una receta de **Colmillo de la Hidra** (`poison`): el antiguo `veneno` se reconoce como el mismo poder.
- Solo una receta de **Pacto Carmesí** (`vampiricregeneration`): el antiguo `vampirismo` se reconoce como el mismo poder.
- Las mejoras antiguas se leen con el máximo de sus niveles y se limitan al máximo actual; no se suman dos copias del mismo efecto. Las dos recetas duplicadas se retiran y se limpian los nombres viejos de esas líneas de lore al refrescar objetos.
- Los grimorios existentes se renuevan al refrescar inventarios para mostrar nombre, efecto y límites actuales. Esporas y aura venenosa de armadura siguen siendo poderes distintos con usos distintos.
- **Resonancia Primigenia** tiene acceso directo desde `/rares` y desde las categorías de armaduras: abre su receta real. Combinar una unidad con una pieza sellada de boss desbloquea esa pieza para nuevas infusiones y conserva sus datos. No es un poder de combate nuevo ni exige añadir otro sello a piezas ya desbloqueadas.
- El catálogo se respalda en `rares-before-1.21.0.yml`; se actualizan nombres y valores de la colección y se conservan los costos negativos usados para desactivar rares.

## Validación y límites

Maven verify: **202 pruebas aprobadas**. Incluye regresión de contacto desde un boss de cuerpo visible, deduplicación de poderes y progresión de contacto de minijefes, además de las pruebas previas. JAR comprobado como ZIP y bytecode Java 21.

No se ha iniciado Paper ni probado un cliente Minecraft en este entorno. No se puede asegurar que todos los bosses aparezcan en todos los terrenos o con cualquier combinación de plugins: se corrigieron los fallos identificados en el código y se añadieron motivos de fallo revisables. El daño sigue respetando supervivencia/aventura, escudos, armadura, inmunidad entre golpes y cancelaciones; los bursts conservan la excepción OP.

Detener el servidor, respaldar `plugins/CazaJefes`, sustituir el JAR anterior por solo CazaJefes-1.21.0.jar y reiniciar. No borrar datos ni configuraciones. Probar en una copia antes de abrir al público.
