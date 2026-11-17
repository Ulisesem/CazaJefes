# Visuales e integración en 1.42.0

Incluye las reglas de zonas y los ajustes de interfaz de 1.41.0.

PacketEvents transmite la animación del brazo al liberar los ataques de la IA configurados en ai.attacks. Paper dibuja una trayectoria de aviso y un círculo de peligro para los especiales. Los avisos usan el presupuesto compartido de partículas y se envían a jugadores cercanos en zonas de combate permitidas. Cada familia tiene un sonido de liberación acorde con su arma o magia.

La API publica BossAttackEvent en el hilo de Paper, con etapas CHARGE, RELEASE e IMPACT. Permite a un plugin externo consultar el UUID del jefe, el objetivo y el ID de habilidad para integrar objetivos, sonidos o recompensas. Son eventos informativos, y corresponden a los ataques configurados en ai.attacks. Las rotaciones heredadas conservan sus propias presentaciones.
