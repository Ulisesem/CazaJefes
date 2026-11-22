# 1.48.0 · Persecución y combate

Excavación cada tick bloqueado, sin depender por defecto de mobGriefing; se puede restaurar esa dependencia con combat.respect-mob-griefing=true. No se destruye bedrock ni se omiten protecciones. El límite de restauración sube a 1024 bloques por boss, 4096 globales. Cada bloque sigue registrado para restauración.

La IA mantiene el objetivo avisado durante cada ataque, revisa amenaza cada 3 segundos y alterna objetivos. Los barridos cercanos pueden afectar simultáneamente a varios jugadores válidos y visibles. Recuperación básica máxima 12 ticks y aviso 3 ticks; combo separado por 10 ticks para respetar la inmunidad nativa.

El mínimo de daño anterior limitado a 1 HP pasa al 20% del golpe disponible, conservando cancelaciones, absorción e inmunidad. Así, más daño base por Filo puede aumentar el daño efectivo.

Las rares leen niveles antiguos aunque falte la lista auxiliar de poderes, respetando equipo compatible, niveles y desactivaciones. Se elimina la entrada residual de desbloqueo del catálogo y su receta. Haste e Invisibilidad compartidos con Shift se aplican también al lanzador y muestran mensajes al receptor y al lanzador.

Huevos: corona ondulada simétrica y anillo inferior negros y morados, con partículas menores y más puntos. Se conecta el impacto de especiales a EffectLib opcional conservando los puentes actuales.

Validación: pruebas automatizadas y auditoría de carga/niveles de cada rare sobre Paper. Esta auditoría no equivale a verificar todas las condiciones y resultados de cada efecto en PvP real. Las APIs visuales externas deben probarse con sus plugins instalados.
