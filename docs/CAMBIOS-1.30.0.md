# CazaJefes 1.30.0 · Combate por especie

## Efectos incluidos

- Los 168 jefes usan diez estilos por especie. Gorath levanta y abate un hacha espectral interpolada. Arqueros, magos, criaturas acuáticas, infernales y abismales tienen focos de ataque, paletas, formas y sonidos acordes a su familia.
- Los especiales de área aplican daño de Paper desde su zona o rayo de impacto. Mantienen los eventos de daño, la armadura y las comprobaciones de cobertura. El modelo y las partículas acompañan el impacto. El daño depende de la habilidad, categoría y fase.
- CazaJefes retira Ceguera y Oscuridad de los ataques de sus jefes. Los definitivos que tengan esos valores en un JSON anterior aplican Debilidad.
- Las hordas salen de portales que crecen en tres pasos. Los impactos de los súbditos usan partículas de su especie con un límite por criatura.
- El botín desciende por separado en espiral y deja un rastro de dos tonos. Las Rare Essence tienen destellos dorados, chispas, rayo visual y un anillo al aterrizar.
- Las rares activadas por libro tienen destellos y colores según el poder. Burst y Burst Mayor conservan su esfera blanca. Los efectos se envían cerca del combate y comparten límites de partículas. Si ya hay 96 objetos de botín animándose, el siguiente se entrega directamente.

## Paquete opcional de audio

El [ZIP de audio](https://raw.githubusercontent.com/Ulisesem/CazaJefes/main/dist/CazaJefes-Audio-1.30.0.zip) registra 30 eventos con sonidos de Minecraft afinados para las diez familias. Los identificadores quedan disponibles para reemplazar los sonidos con pistas propias en un paquete posterior. Las mejoras visuales y el audio base funcionan al instalar el JAR.

Para ofrecer el paquete a los jugadores, edita `plugins/CazaJefes/cinematics.yml` y reinicia.

```yaml
audio-pack:
  url: "https://raw.githubusercontent.com/Ulisesem/CazaJefes/main/dist/CazaJefes-Audio-1.30.0.zip"
  sha1: "4a6517d49cae6bab3cf93f2a7d73d66368e908b7"
```

CazaJefes usa el audio del paquete cuando recibe la confirmación de carga del cliente. Si el jugador lo rechaza, reproduce el audio base. El paquete usa el formato 75.0 de [Minecraft Java 1.21.11](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-11).

## Model Engine opcional

El puente utiliza la [API R4](https://wiki.mythiccraft.io/modelengine/API) y los IDs de animación de un modelo ya importado. El JAR usa el hacha espectral y los demás displays de Paper en una instalación normal. Para usar un modelo de cuerpo completo, instala Model Engine compatible con tu servidor, importa el modelo y añade sus IDs a `cinematics.yml`.

```yaml
models:
  gorath_quebrantador_de_mundos:
    id: gorath
    prepare: prepare
    impact: attack
    recovery: recover
```

Los nombres del ejemplo deben coincidir con los del modelo importado. También puedes definir un modelo por familia usando una clave como `style_arcane`. CazaJefes mantiene la caja de impacto original del jefe.
