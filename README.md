# CazaJefes 1.1.0 · La Grieta

Plugin de prueba para Paper 1.21.11 / Java 21. [Descargar JAR](dist/CazaJefes-1.1.0.jar?raw=true).

## Interfaces

`/bestiario`, `/bestirio`, `/bestiary`, `/ri bosses`, `/ri jefes` y `/ri bestiario`: seis capítulos exclusivos (Minijefes, Raros, Épicos, Legendarios, Míticos y Abismales), con los 62 bosses, bordes de categoría, sellos luminosos, iconos de criaturas y fichas de fases/botín.

`/rares` y el recetario anterior abren la forja por categorías: Tributos y cristales, Armas de élite, Armaduras, Esencias, Hechizos y Todo el arsenal. Muestra recetas registradas y fabrica una unidad con comprobación de materiales/espacio. La infusión equipo + esencias sigue en mesa shapeless.

## Progresión

- Todos los minijefes: solo 1–2 Tributos del Cazador y 1–3 Fragmentos de Tributo. Sin equipo, manzanas, cristales ni libros.
- Bosses principales: fragmentos y un Cristal Encantado de su categoría garantizados; el resto puede incluir equipo, rares, núcleos, esencias, libros y materiales.
- Mobs hostiles ordinarios matados por jugador: 1 % de fragmento. Se excluyen bosses/súbditos y spawns marcados como spawner, huevo, comando o CUSTOM. El marcado empieza con esta versión; la procedencia de mobs antiguos sin marca no está garantizada.
- Súbditos matados por jugador: 0,5 % de fragmento; eliminarlos por tiempo de vida no recompensa.
- Equipo nuevo con nombre de categoría y origen. Cristales de seis rangos con brillo y PDC propio.

Cristal Común: cuatro fragmentos + un tributo. Cada rango siguiente: dos cristales del anterior + un tributo. Nueve fragmentos se convierten en un tributo. Las armas y armaduras de forja requieren un cristal del rango del resultado, dos núcleos, una pieza base del mismo material y un fragmento.

## Fases

| Fase | Vida | Daño causado | Daño recibido |
|---|---|---|---|
| I | Más del 65 % | ×1,00 | ×1,00 |
| II | 30–65 % | ×1,35 | ×0,82 |
| III | Hasta 30 % | ×1,80 | ×0,65 |

Desde la segunda fase: debilidad y lentitud al golpear. En la última: marchitar u oscuridad para wardens; espera de seis segundos por jugador entre aplicaciones.

Solo en la última fase: 2 % por tirada para rotar hasta cuatro posiciones ocupadas del inventario principal. Máximo una tirada cada diez segundos de golpes y un éxito por minuto. No toca armadura/offhand ni actúa con otra interfaz/cursor ocupado, en creativo o espectador. No crea ni elimina objetos.

Nharzul, Soberano del Abismo (`nharzul_soberano_del_abismo`) y Azrath, Devorador de Ecos son Abismales. Salud base nominal mínima de 14000/10000 (sujeta al límite de atributos del servidor), reducción adicional de daño del 15 %, daño adicional ×1,35 e ira mantenida hacia objetivos válidos.

La barra es visible en creativo. OP o `cazajefes.admin` permite probar daño/drops en creativo con `combat.allow-creative-admin-tests: true`. Los bosses siguen atacando solo a supervivencia/aventura.

## Instalar y migrar

1. Copia la carpeta del plugin y el mundo. Detén el servidor y sustituye el JAR anterior por 1.1.0; no mantengas dos versiones.
2. Inicia normalmente. Se respaldan config.yml y recipes.yml antiguos como `*-before-1.1.0.yml` antes de migrar.
3. Los IDs nuevos corresponden al nombre visible sin acentos y con guiones bajos. Todos los IDs antiguos siguen como alias. Los atributos guardados por ID anterior se copian al ID nuevo conservando tus valores; los de una instalación antigua pueden ser menores que los valores nuevos.
4. Los cinco patrones de armas v1 y sus ingredientes personalizados se retiran para adoptar K/C/B/F (cristal/núcleo/base/fragmento); permanecen en el respaldo para trasladarlos manualmente. Se conservan enabled y las demás recetas.
5. Tributos, fragmentos y núcleos del inventario principal se actualizan al entrar o abrir inventarios. Se conservan sus IDs. No se recrea equipo ya existente ni se borran sus mejoras.

Las entidades cargadas actualizan su boss_type; huevos guardados aceptan ambos formatos. Para volver a una versión anterior restaura también mundo/datos: esa versión no reconoce los IDs nuevos. No se reconstruye contenido de contenedores perdido por 0.9.7.

## Configuración y compilación

En config.yml: `loot.mob-fragment-chance: 0.01`, `loot.minion-fragment-chance: 0.005`, `combat.final-inventory-chance: 0.02`. Probabilidades limitadas a 0–1; valores no finitos equivalen a cero.

recipes.yml v2 permite enabled, shape e ingredientes de recetas shaped existentes; símbolos no redefinidos conservan su ingrediente exacto. rares.yml conserva costes y niveles; cooldownSeconds controla los hechizos activables, no todos los pasivos. `/ri reload` recarga archivos/recetas. Reinicia para cambiar intervalos de encuentros existentes.

JDK 21 + Maven 3.9+: `mvn verify`; resultado `target/CazaJefes-1.1.0.jar`.

26 pruebas unitarias aprobadas, más ZIP/CRC y bytecode Java 21 comprobados. Cubren catálogo/alias, categorías completas, botín simulado, granjas, fases, probabilidad/cooldown, consumo, clics, compuertas y YAML. No se ejecutó Paper ni cliente Minecraft; falta validar interfaces, eventos, botín real, balance y plugins externos en juego.

Detalles: [cambios](docs/CAMBIOS-1.1.0.md), [IDs completos](docs/BOSSES-IDS-1.1.0.csv) y [pruebas pendientes](docs/PRUEBAS-1.1.0.md). Los informes 1.0.x son históricos. No se asignó licencia sin indicación del titular.

[Paper InventoryClickEvent](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/event/inventory/InventoryClickEvent.html) · [Paper Warden](https://javadocs.papermc.io/paper/1.21.11/org/bukkit/entity/Warden.html).
