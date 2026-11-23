# CazaJefes 1.53.0

- Los huevos quedan dentro de una esfera negra y morada que gira e inclina su eje. Radio predeterminado: 3 bloques; 48 puntos por actualización, con los presupuestos de partículas existentes. Se configura en cada boss mediante combat.eggParticles.radius y points.
- Recetas y materiales conservan páginas centradas, marco azul uniforme y navegación común. Los materiales se ordenan por catalizadores, componentes defensivos y componentes de proyectiles.
- Se elimina Burst del catálogo, recetas, configuración incluida y ejecución de libros. Greater Burst conserva la habilidad y utiliza pequeñas estrellas blancas alrededor del cuerpo, ajustadas al tamaño de jugadores y bosses. Los libros Burst antiguos dejan de activarse; no se convierten automáticamente.
- El mercado contiene exclusivamente Sello del Renacido y Amuleto del Fénix Astral, colocados simétricamente. Se conservan los precios configurables y la validación de espacio/monedas antes de consumirlos.
- Fragmento de Tributo oculta la descripción vanilla adicional del disco mediante HIDE_ADDITIONAL_TOOLTIP. Los fragmentos existentes se normalizan al actualizar monedas en los menús.
- Invisibilidad y FireResistance permiten compartir con Shift + clic derecho apuntando a un jugador, aplicando el efecto también al usuario. Se conserva saturación, recarga y confirmación en chat.

Validación: compilación de los cuatro módulos y pruebas automatizadas. La prueba adicional en Paper comprueba el mercado, los libros compartidos y los metadatos del fragmento. La apariencia gráfica debe revisarse dentro de Minecraft.

## Configuración

- `bosses/<categoría>/<boss>.json` → `combat.eggParticles`: radio, puntos, velocidad de rotación, tamaño de partícula y colores primaryColor/accentColor (hexadecimal). Reiniciar tras editar JSON de bosses.
- `config.yml` → `presentation.greaterburst`: estrellas, fotogramas, intervalo, tamaño de estrella/partícula y expansión. Se mantiene el presupuesto global de partículas.
- `config.yml` → `books.sharing`: activar/desactivar, alcance, precisión al apuntar y mensajes con {effect}/{player}.
- `config.yml` → `books.invisibility` y `books.fireresistance`: duración. `books.greaterburst`: radio, fuerza horizontal y elevación, con progresión por nivel.
- `config.yml` → `presentation.sanctuary`: título, panel y posiciones de los dos canjes. Posiciones inválidas/repetidas usan21/23. `market`: precios de fragmentos/sellos/tributos.
- `interfaces/recetas.json`: título, etiquetas, color, posiciones iniciales, tamaño de página y materialOrder (catalysts, defense, projectiles, other).
- `rare-balance.json` y `rares.yml`: enfriamientos, multiplicadores, costos y activación de las rares existentes.

Usa `/ri reload` para configuración general y balance; el recetario actualiza su JSON al abrirlo. Los ajustes nuevos se añaden sin sobrescribir los valores personalizados.
