# CazaJefes 1.21.2

- Haste / Ímpetu del Relámpago: Velocidad I–VII para correr. Se actualizan máximo, amplificador real, descripción y configuración; la combinación de grimorios admite llegar a VII y rechaza excesos. Se conservan costo de cuatro puntos de saturación, recarga de diez segundos y duración de treinta segundos por nivel: VII dura 210 segundos.
- Los drops animados parten desde **16 bloques** sobre su punto de aterrizaje, antes ocho. Se limita la altura al techo del mundo, con margen de un bloque. Conservan 160 ticks, aproximadamente ocho segundos a 20 TPS, tres vueltas de espiral, interpolación y entrega de cada unidad después de aterrizar. No cambia el daño ni la altura de la animación del cadáver del boss.
- La actualización respalda el catálogo como rares-before-1.21.2.yml y conserva poderes desactivados mediante costos negativos. Las recetas siguen visibles en /rares.

Maven verify: 202 pruebas aprobadas. JAR compilado con Java 21 para Paper 1.21.11 e integridad ZIP/bytecode revisados. No se ha arrancado Paper ni probado la apariencia en un cliente en este entorno.

Detener el servidor, retirar el JAR anterior e instalar solo CazaJefes-1.21.2.jar conservando la carpeta del plugin.
