# Sincronización de animaciones en 1.42.1

El equipo de los jefes mostrados por PacketEvents se actualiza ante todos los jugadores cercanos que reciben el chunk. Preparar el hacha muestra el hacha en mano, lanzarla vacía la mano y terminar restaura el arma original. El seguimiento de equipo de la entidad visual comprueba los cambios cada dos ticks.

Los avisos de trayectoria comprueban la pared antes de dibujarse. Los mensajes diferidos de recuperación no se muestran cuando ya hay otro ataque activo en la IA. Las presentaciones antiguas y los ataques configurados siguen sujetos al mismo turno y a la pausa compartida de especiales.

La comprobación final incluye un cliente que cuenta paquetes de animación y cambios de equipo. La estética final debe comprobarse dentro del cliente gráfico de Minecraft.
