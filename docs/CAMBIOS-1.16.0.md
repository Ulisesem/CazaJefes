# CazaJefes 1.16.0

Añade la Forja de duelo: ocho rares PvP funcionales (cuatro armas y cuatro armaduras), ocho materiales propios y dieciséis recetas reales. Las recetas se pueden fabricar desde la interfaz y la mesa; los materiales derivan de núcleos, fragmentos y Rare Essence existentes más un reactivo vanilla distinto.

Dos categorías nuevas en /rares: Rares PvP y Materiales de duelo, con consulta cruzada entre pieza y material. La portada reorganiza sus nueve categorías sin superponer controles de navegación.

Los poderes tienen disparadores de combate y recargas individuales de 10–18 segundos: combo de hemorragia, hacha debilitante, arco de precisión, ballesta de control, casco antiproyección, pechera reactiva, pantalones de limpieza y botas de escape. Una sola defensa de armadura se activa por impacto. No se consumen recargas ante daño cancelado o totalmente bloqueado. No hay escaneo de jugadores ni tareas periódicas añadidas.

Los materiales tienen metadata propia y brillo; no requieren recursos externos ni son modelos nuevos. Las recetas previenen sustituir un ingrediente vanilla por otro objeto personalizado. La forja crea una pieza nueva y consume la base; no copia mejoras anteriores.

Los efectos PvP tienen duraciones breves e independientes de los debuffs de 30 segundos de bosses. Se mantienen el registro de golpe final, los bosses, sus IDs, sus drops y las mecánicas de 1.15.0.

Las ideas adicionales de materiales y futuras rares se documentan como propuestas, sin presentarlas como habilidades ya disponibles.

Validación: compilación Maven, 173 pruebas, CRC del JAR y bytecode Java 21. No se ha iniciado un servidor Paper ni medido un combate real; hace falta probar balance y compatibilidad de regiones, equipos, infusiones tradicionales y otros plugins de PvP.

Ver [colección y recetas](DUELO-PVP.md) y [propuestas futuras](IDEAS-MATERIALES.md).
