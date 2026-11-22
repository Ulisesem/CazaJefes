# CazaJefes 1.49.0

- Excavación permanente hacia el objetivo en los seis ejes, sin registro ni restauración posterior. Respeta bedrock, regiones y cancelaciones de EntityChangeBlockEvent. Los registros antiguos de restauración quedan sin ejecutar.
- Objetivo principal más cercano; respuesta temporal de 1,5 segundos al jugador que acaba de generar amenaza al golpear. El objetivo de una carga se conserva para no cambiar el destinatario después del aviso. Los barridos siguen afectando a varios jugadores cercanos válidos.
- Magnetismo: especial de todos los perfiles, radio esférico de 30 bloques, aviso de 2 segundos, atracción durante 1 segundo y cooldown predeterminado de 20 segundos. Respeta validación de objetivos y protecciones. Se configura en ai.attacks con id magnetismo, type MAGNETISM, radius, warningTicks, cooldownTicks y weight. Un perfil explícito con ese id conserva sus parámetros.
- Durability: una pieza con la rare repara toda la armadura equipada según el mayor nivel, 3 puntos por nivel cada 5 segundos, sin sumar niveles entre piezas. La descripción de recetas se actualiza.
- Huevos en cuevas: antes de activar el boss se calcula el volumen de su escala real, se comprueban todas las obstrucciones y, si son rompibles, se despeja con partículas y sonido de explosión sin fuego ni daño de explosión. La rotura es permanente. Ante bedrock o protección se conserva el huevo.

Validación: pruebas unitarias y sonda local en Paper 1.21.11. No se ha realizado inspección gráfica con un cliente Minecraft.
