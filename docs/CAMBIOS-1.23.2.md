# CazaJefes 1.23.2 · PvP estilo 1.8 sin escudos

Quebranto deja de desactivar escudos. Un disparo que causa daño aplica una marca de cuatro segundos; el siguiente golpe cuerpo a cuerpo del mismo atacante causa +10/20/30% de daño según nivel I/II/III y consume la marca. Recarga de aplicación: 12 segundos, compartida entre copias de la rare. No reduce permanentemente la armadura ni modifica los encantamientos del rival.

La marca pertenece al atacante: los aliados no pueden consumirla. El golpe cancelado, bloqueado por completo o sin daño no consume la marca ni activa las rares nuevas. No se fuerza daño sobre los ticks de invulnerabilidad ni se impone una recarga de ataque de 1.9: OldCombatMechanics sigue controlando el combate base.

Nombre e ID `quebranto`, máximo III y receta se conservan para los libros, esencias y ballestas existentes. La descripción se actualiza al abrir inventarios mediante la actualización visual existente. No requiere fabricar de nuevo la rare.

Las demás rares de ballesta, arco y tridente mantienen sus controles por impactos y recargas propias: no dependen de escudos. La carga completa requerida por Marca del Cazador y Ojo de Tormenta corresponde a tensar el arco, no a la recarga de golpe cuerpo a cuerpo de 1.9. No se eliminan ballestas ni tridentes del servidor moderno.

Validación: Maven verify y regresiones. No se ha ejecutado Paper con OldCombatMechanics para esta versión.
