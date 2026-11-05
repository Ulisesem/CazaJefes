# CazaJefes 1.29.0 · Combate y bestiario

## Catálogo

Al iniciar, el plugin exporta los 168 jefes originales a `plugins/CazaJefes/bosses.json`. Puedes editar el nombre visible, la descripción, la vida configurada, el daño base, la escala, el retrato y el ataque definitivo. Reinicia el servidor para aplicar el catálogo. Las estadísticas base se aplican al invocar al jefe. Los valores por ID de `config.yml` siguen teniendo prioridad para vida, daño y escala.

Conserva los IDs, las especies y las categorías del catálogo original para que los huevos, los trofeos y los puntos de aparición sigan referenciando al mismo jefe. Si el catálogo tiene errores, la consola indica el problema y el servidor usa la copia incluida en el JAR. Los jefes adicionales siguen en `plugins/CazaJefes/mobs/`, uno por archivo. El [Centinela de Prisma](examples/centinela_prisma.json) sirve de ejemplo.

El campo `portrait` acepta el hash hexadecimal de 60 a 64 caracteres de una textura alojada en `textures.minecraft.net`. Los 168 jefes originales llevan un retrato por especie. Dos jefes de la misma especie comparten su retrato predeterminado. Las tarjetas y fichas muestran el nombre del jefe sobre su retrato. Las referencias de textura proceden de [MoreMobHeads](https://github.com/JoelGodOfwar/MoreMobHeads/tree/ffe43f546d3ccdfbadc8aee2f1b9af25bb0966c8/src/main/resources/heads/entity), con su [licencia incluida](../src/main/resources/MORE-MOB-HEADS-LICENSE.txt). Se aplican mediante la [API de perfiles de Paper](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/inventory/meta/SkullMeta.html).

## Combate

- Los ataques de la rotación común muestran preparación, una barra de carga, zona de peligro e impacto. Tras la combinación, el jefe entra tres segundos en recuperación. Los avisos y sonidos se envían a jugadores aptos cercanos.
- La primera etapa incluye un barrido de tres cortes con ángulos diferentes. La segunda añade un rayo que sigue al objetivo mientras apunta y fija la trayectoria antes de disparar. El rayo se detiene en los bloques y detecta la caja de impacto del jugador.
- En la tercera etapa, la lluvia de meteoritos marca la zona y muestra el meteorito descendiendo mediante un display interpolado. El definitivo usa el perfil individual del JSON, con marcas moradas.
- Al cambiar de etapa, un sonido y un anillo señalan la transformación. Durante un segundo el jefe se prepara y el daño entrante se cancela. Las mecánicas por especie siguen activas en su rotación.
- Al llegar al 50 % o menos, el jefe puede invocar cuatro cristales rompibles si tiene espacio alrededor. Reducen hasta un 35 % del daño entrante. Cada cristal roto reduce esa protección y la barrera caduca en 15 segundos. Los cristales se retiran sin explosión.
- Antes de invocar la horda aparecen portales. Los súbditos entran unos 0,75 segundos después, con el aviso y el sonido de la invocación. Siguen las reglas de espacio, aforo y comportamiento de su especie.
- Los displays son temporales y tienen un tope de 144 simultáneos. Las partículas siguen el presupuesto compartido del plugin.

La animación usa Paper y Adventure. Las transformaciones se representan con efectos del propio juego. Los modelos 3D personalizados quedan fuera de esta versión.
