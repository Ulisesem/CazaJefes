# CazaJefes 1.28.0 · Jefes personalizados

1. Copia [centinela_prisma.json](examples/centinela_prisma.json) a `plugins/CazaJefes/mobs/centinela_prisma.json`. CazaJefes crea la carpeta al arrancar.
2. Edita el archivo y reinicia el servidor. La carga de JSON se realiza al arranque, junto con el registro de recetas.
3. Con permisos de administrador, ejecuta `/ri boss centinela_prisma here` en una arena despejada. Usa `/ri egg centinela_prisma here` para colocar el huevo. También puedes buscarlo en `/bestiario` o usar su ID en un punto de aparición.

El ejemplo es opcional. El catálogo original sigue disponible si falta el archivo o algún JSON es inválido. Los errores salen en la consola con el nombre del archivo. Si dos archivos tienen el mismo ID, se acepta el primero válido por orden de archivo. La carpeta admite hasta 128 archivos JSON de 16 KiB cada uno.

## Campos

| Campo | Uso |
|---|---|
| `id` | Identificador único de 3–48 caracteres. Empieza con una letra minúscula y usa solo letras ASCII minúsculas, números y guion bajo. |
| `template` | ID de un jefe original. Define la especie, la categoría, si es minijefe y sus mecánicas por especie. |
| `name` | Nombre visible, hasta 48 caracteres. |
| `description` | Descripción opcional, hasta 140 caracteres. |
| `health` | Vida inicial y máxima, entre 1 y 1024 HP. Dos HP equivalen a un corazón. |
| `damage` | Daño base del atributo de ataque, entre 0 y 120. Los golpes y las habilidades pueden sumar bonificaciones o aplicar sus propios valores. |
| `scale` | Escala base, entre 0,25 y 4. Las reglas de tamaño para animales y minijefes se aplican encima. |
| `ultimate` | Opcional. Define el nombre, patrón, efecto, radio y pulsos del ataque final de etapa III. Los campos omitidos conservan los valores de la plantilla. |

En `ultimate`, el nombre admite 64 caracteres, `radius` va de 6 a 8 bloques y `pulses` de 2 a 4, en enteros.

- Patrones `RINGS`, `CROSS`, `CONE`, `SPIRAL`, `METEORS`, `VORTEX`, `LANES`, `CHECKER`, `GEYSERS`, `NOVA`, `RUSH` y `CAGE`.
- Efectos `SLOWNESS`, `POISON`, `WEAKNESS`, `WITHER`, `BLINDNESS`, `HUNGER`, `LEVITATION` y `DARKNESS`.

Cada jefe utiliza el botín de su categoría, las protecciones y el flujo de combate de CazaJefes. Los jefes personalizados participan en los puntos de aparición configurados con `random`. Los ajustes por ID de `config.yml` tienen prioridad para vida, daño y escala. Conserva el mismo ID si ya colocaste huevos, programaste puntos de aparición o repartiste núcleos de ese jefe. Cambia estadísticas con el jefe derrotado y vuelve a invocarlo para probarlas.

## Ideas para animaciones de combate

- **Fractura de Prisma.** Marca tres carriles en el suelo, deja un carril seguro y dispara los pulsos siguiendo las marcas. Con `BlockDisplay` interpolado puedes mostrar el avance de la fractura. La zona que aplica daño debería salir de la misma geometría que dibuja las marcas.
- **Mirada del Centinela.** Usa Adventure para avisar «Fija la mirada» y mostrar una barra de carga a quienes pelean. Un tono ascendente anuncia la descarga y un tono corto marca el inicio de la recuperación, cuando el jugador puede acercarse a castigar.
- **Embate de Obsidiana.** Con un modelo personalizado y una integración opcional de Model Engine, sincroniza tres poses con el combate: levantar el brazo, golpear y recuperarse. El servidor sigue determinando a quién alcanza el golpe. Verifica compatibilidad con tu versión exacta antes de integrar el modelo.
- **Rastro de la Grieta.** Usa partículas de Paper para el impacto y el borde de la zona, con un presupuesto compartido de partículas, un radio limitado y limpieza de los displays al terminar o cancelar el ataque.

Referencias de las APIs: [Displays de Paper](https://docs.papermc.io/paper/dev/display-entities/), [Adventure](https://docs.papermc.io/adventure/), [partículas de Paper](https://docs.papermc.io/paper/dev/particles/) y [wiki de Model Engine](https://git.lumine.io/mythiccraft/modelengine/-/wikis/home).
