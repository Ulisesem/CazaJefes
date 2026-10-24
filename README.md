# CazaJefes 1.0.0 — compilación de prueba

Proyecto Maven para **Paper 1.21.11 / Java 21**, reconstruido a partir del adjunto `CazaJefes-0.9.7(1).jar`. No requiere MythicMobs, Vault ni otro plugin. No es compatible con Minecraft 1.8.8 ni está preparado para Folia.

## Qué contiene

- Se conservan las seis clases principales recuperadas, 62 definiciones de bosses (10 minis), seis rangos, IDs/PDC, huevos, brújula de rastreo, bestiario, mercado, equipo, monedas, botín y catálogo de propiedades del original.
- `/rares`: catálogo paginado de las recetas shaped realmente registradas, detalle 3×3 y botón para fabricar **una unidad** con los materiales del inventario. Primero calcula el consumo y el espacio; solo después actualiza el inventario. No tira el resultado al suelo si falta espacio. Bloquea arrastrar, shift, teclas numéricas y extracción de las vistas previas.
- `/ri rares`, `/ri recipes`, `/ri recetas` y `/ri crafts` abren el mismo menú actualizado. El bestiario, mercado y catálogo de propiedades anteriores siguen disponibles; sus vistas históricas de recetas no deben usarse como referencia tras personalizar recetas: `/rares` es la referencia del registro real.
- `recipes.yml`: permite deshabilitar recetas shaped, cambiar su patrón y sustituir símbolos por materiales Bukkit. Los símbolos no redefinidos conservan las alternativas exactas originales, incluidos materiales personalizados de bosses. Hay ejemplos de las cinco armas especiales. Una receta con patrón o material inválido se omite y deja una advertencia en el registro.
- Se renombran 12 bosses iniciales, incluidos Gorath, Nyxara, Varkhos y los minis. Los demás nombres se conservan. Se actualizan y hacen visibles los nombres de los súbditos. Los IDs de comandos y PDC no cambian.
- Tres fases por vida: inicial, ruptura al 65%, furia al 30%. Las transiciones invocan refuerzos y dan Fuerza temporal; el intervalo de especiales disminuye por fase. Se mantiene la variedad de ataques recuperada.
- Los especiales tienen un aviso visual y un segundo de anticipación. El objetivo puede esquivar desplazándose más de tres bloques desde la marca; se vuelve a comprobar estado, alcance y visión antes de ejecutar el ataque. Los especiales con efectos secundarios conservan parte de su lógica original.
- Adquisición de objetivo a un máximo de 24 bloques y con línea de visión. Solo jugadores en supervivencia/aventura. Al salir del radio de persecución, el boss vuelve al ancla y recupera vida; se borra el crédito de botín.
- Botín original conservado. Requiere un golpe válido de jugador en los últimos 120 segundos por defecto y marca persistente de pago para impedir repetir el reparto. Los súbditos no sueltan botín, tienen límite por encuentro, tiempo de vida y limpieza al morir el boss, descargar/recargar chunks o deshabilitar el plugin.
- Se corrige el permiso de `/ri get`, la comprobación para despertar huevos y el permiso de fabricación. Los huevos se protegen frente a física, desplazamiento, explosiones y pistones. Se deja intacto el bloque de soporte, incluidos sus inventarios.
- Treefeller/veinminer rompen cada bloque mediante `Player.breakBlock`, con eventos y un límite de 64; no eliminan los bloques vecinos directamente. No se afirma integración completa con plugins de regiones.

## Compilar

Instala JDK 21 y Maven 3.9+. Desde esta carpeta:

```text
mvn verify
```

Resultado: `target/CazaJefes-1.0.0.jar`. La API de Paper tiene alcance `provided`: no se incorpora al plugin. Las dependencias se descargan del repositorio de Paper y Maven Central. No se necesita el JAR antiguo para compilar.

Se ejecutaron seis pruebas automatizadas: conservación del catálogo y aliases, unicidad de IDs y planificación del consumo (repeticiones, varios stacks, material incorrecto y fallo sin modificación del inventario). Esto no sustituye una prueba dentro del juego.

## Instalar y probar

1. Utiliza un servidor de pruebas Paper 1.21.11 con Java 21 y una copia del mundo y de la carpeta de datos.
2. Retira la versión anterior de `plugins/`; no mantengas ambas versiones a la vez. Copia el nuevo JAR e inicia el servidor normalmente. No uses el comando global `/reload`.
3. Prueba `/rares`, `/ri bosses`, `/ri catalog`, `/ri trade` y `/ri compass`. Usa `/ri boss coloso here` como administrador para el combate de prueba. Usa `/ri egg coloso here` en un bloque libre para comprobar los huevos.
4. Verifica fabricación con y sin ingredientes, inventario lleno, shift/drag/teclas numéricas, fases 65% y 30%, esquiva, pérdida de visión, persecución, muerte y limpieza de súbditos.
5. Comprueba permisos con un jugador sin OP y repite las pruebas con tus plugins de protección. La lista detallada está en `docs/PRUEBAS-MANUALES.md`.

**No se ha iniciado Paper ni se ha ejecutado el JAR original o el plugin nuevo en un servidor durante este trabajo.** La entrega incluye compilación, pruebas unitarias e inspección estática; la jugabilidad, el balance y las interacciones con otros plugins están pendientes.

## Configuración

`config.yml`: vida, daño base y escala por ID en `bosses.<id>`, intervalo del controlador en ticks y ajustes de `combat`. El daño de algunos especiales heredados sigue definido por rango, no por el atributo base del boss. Los cambios de atributos afectan a nuevas invocaciones; reinicia el servidor para aplicar de forma consistente cambios de intervalo a encuentros existentes.

`rares.yml`: `useCost` conserva el sistema de saturación original; `maxLvl` limita libros y fusiones entre 1 y 6. `cooldownSeconds` se aplica a los ocho hechizos activables. Las propiedades pasivas y los efectos al golpear conservan sus temporizadores originales; no se promete que este campo controle todos los efectos. El campo textual antiguo `recipe` es descriptivo y **no define recetas**; para eso se utiliza `recipes.yml`. Se añade la entrada `magneto` que faltaba.

`recipes.yml`: claves del registro, por ejemplo `hacha_sismica`, `brujula_caza`, `crear_libro_rare_burst` o `crear_esencia_rare_poison`. Solo las recetas shaped aceptan estas modificaciones; la fusión de equipo con esencia sigue siendo shapeless y conserva su validación propia. No permite inventar un nuevo tipo de rare ni un efecto nuevo desde YAML.

Ejemplo para cambiar únicamente el lingote del hacha, conservando todos sus ingredientes de bosses:

```yaml
recipes:
  hacha_sismica:
    enabled: true
    shape: ['GMP', ' NB', ' S ']
    ingredients:
      N: DIAMOND
      S: STICK
```

`/ri reload` recarga los tres archivos y reconstruye recetas. Los menús abiertos usan el registro actual al fabricar; ciérralos y ábrelos para refrescar la vista. Si una receta es inválida, corrígela y recarga. Revisa las advertencias del servidor.

## Migración y límites de reconstrucción

El JAR no contenía fuentes Java. Se recuperaron mediante Vineflower y se repararon tipos genéricos y una expresión de selección que no compilaban. Se conservan muchos nombres locales `varN`; no se recuperan comentarios, nombres originales de todas las variables, pruebas originales ni historia Git. Véase `docs/RECONSTRUCCION.md` y el diff con la recuperación inicial.

No se convierte automáticamente el inventario existente. Aunque los IDs se conservan, `ExactChoice` compara también metadatos: núcleos/equipo antiguos con nombres o lore diferentes pueden no servir en recetas nuevas. Ensaya la migración con una copia. Los huevos antiguos cuyo soporte se había sustituido por bedrock requieren revisar/restaurar ese bloque manualmente; no se inventa el contenido perdido de contenedores. No se altera ahora el soporte automáticamente.

No se ha añadido programación automática de spawns ni integración WorldGuard, economía o anticheat externo. Las protecciones implementadas son específicas, no una garantía de ausencia de exploits. Continúan los efectos y sistemas recuperados que no se modificaron; requieren pruebas funcionales. No se han investigado recetas externas: las recetas son las del JAR, ahora configurables.

## Referencias y publicación

[Configuración de proyecto Paper](https://docs.papermc.io/paper/dev/project-setup/), [Java para Paper](https://docs.papermc.io/paper/getting-started/) y [API 1.21.11](https://javadocs.papermc.io/paper/1.21.11/index.html).

Repositorio confirmado: [Ulisesem/CazaJefes](https://github.com/Ulisesem/CazaJefes). El JAR de prueba se incluye en [dist/CazaJefes-1.0.0.jar](dist/CazaJefes-1.0.0.jar?raw=true), con su huella SHA-256. No es una release validada en un servidor. No se asigna una licencia al código recuperado sin indicación del titular.
