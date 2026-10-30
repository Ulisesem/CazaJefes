# CazaJefes 1.23.1

Corregido el fallo de arranque `IllegalArgumentException: Rare desconocida: cerrojo_vacio`. Las recetas con definición propia ya no consultan el registro antiguo de alquimia antes de construir sus ingredientes. Se aplica a las 45 rares definidas, incluidas las doce nuevas; se mantienen las recetas antiguas de alquimia.

Los errores posteriores `plugin is disabled` y `zip file closed` del registro ocurren tras el fallo de activación. Sustituir el JAR con el servidor detenido y arrancar de nuevo, sin recarga en caliente. No se necesita borrar configuraciones, inventarios ni mundos.

El aviso inicial `Código no se reconoce` es independiente: eliminar la línea de texto suelta del BAT o convertirla en un comentario con `rem`.

Validación: Maven verify y regresiones de selección de recetas, incluidas todas las nuevas. No se ejecutó el servidor Paper.
