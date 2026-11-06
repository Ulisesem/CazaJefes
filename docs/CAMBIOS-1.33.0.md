# CazaJefes 1.33.0 · Impacto de la Grieta

- Las zonas de peligro usan partículas. Los ataques comunes dibujan su área y las líneas de rayo muestran su trayectoria. Lluvia de Obsidiana conserva los 20 × 20 bloques y marca los bordes y el interior de las casillas de cada oleada. Los cristales rompibles de la barrera siguen formando parte del encuentro.
- Cuando un ejecutor prepara el último corte de cono, puede fijar el suelo bajo su objetivo y lanzar un hacha de netherita. El círculo indica el radio de tres bloques. El hacha permanece incrustada aproximadamente 1,25 segundos. La descarga visual, el sonido y las partículas de grieta acompañan un impacto que puede infligir daño y empujar. El suelo queda intacto.
- La rotación nativa de arcanos y criaturas del vacío incorpora Rayo de Rebote en fase II. Su trazado puede reflejarse una vez en una cara de bloque. Si cambia la cobertura tras el aviso, se cancela el pulso.
- Los arcanos reciben Anclas del Vacío en la fase III de la rotación nativa. Golpear o hacer clic en una runa abre un refugio verde temporal contra esa onda. Las runas son interacciones temporales y se limpian al terminar el ataque.
- Los flanqueos y retiradas terrestres elegidos por la táctica pueden usar un objetivo breve de navegación de Paper. La IA usa posiciones con espacio y suelo.

Las habilidades personalizadas pueden usar `REFLECTION` o `ANCHORS` en `combate.yml`. El bestiario refleja la rotación activa. Las integraciones de WorldGuard, WorldEdit, Mythic Dungeons, ItemsAdder, PacketEvents y Floodgate se mantienen como propuestas independientes.

## Prueba en el servidor

Invoca a Gorath en una arena despejada con `/ri boss gorath_quebrantador_de_mundos here` y prueba su tercer corte en supervivencia. Para probar las runas, invoca a Arcaneth con `/ri boss arcaneth_maestro_del_vacio_azul here` y llévalo a la fase III. Para el rayo de fase II, coloca cobertura lateral dentro de la arena y observa la trayectoria anunciada.

Referencias de implementación. [Partículas de Paper](https://docs.papermc.io/paper/dev/particles/) · [Mob Goals](https://docs.papermc.io/paper/dev/mob-goals/) · [Interaction](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/entity/Interaction.html)
