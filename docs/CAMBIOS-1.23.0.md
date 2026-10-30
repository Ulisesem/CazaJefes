# CazaJefes 1.23.0 · Rares de proyectiles

Doce rares exclusivas, aplicables en la mesa mediante esencia o `/ri book <id> <nivel>`. Mantienen el sellado obligatorio del equipo de boss. No caen como rares preparadas ni incorporadas en equipo.

| Arma | ID y nombre | Máximo | Recarga | Mecánica |
|---|---|---|---|---|
| Ballesta | `cerrojo_vacio` · Cerrojo del Vacío | III | 10 s | Tres impactos al mismo enemigo en 8 s preparan atracción en el cuarto. |
| Ballesta | `carga_inestable` · Carga Inestable | III | 12 s | Impactar un bloque deja trampa con avisos visuales/sonoros; explota tras 2 s, radio 3 y 2 HP por nivel antes de defensas. |
| Ballesta | `quebranto` · Quebranto | III | 12 s | Impactar un escudo en uso aplica recarga de escudo 1 s por nivel. Los impactos bloqueados no reciben daño añadido. |
| Ballesta | `relevo_fantasma` · Relevo Fantasma | II | 10 s | Acertar habilita 4 s para agacharse: paso lateral derecho de 1/2 bloques si hay suelo y camino libre. Respeta la cancelación del teletransporte. |
| Arco | `marca_cazador` · Marca del Cazador | IV | 8 s | Dos disparos totalmente cargados al mismo enemigo en 6 s: segundo +15% de daño por nivel. |
| Arco | `estela_boreal` · Estela Boreal | III | 10 s | Tercer impacto al mismo rival en 8 s aplica Lentitud I–III durante 3 s y partículas de nieve. |
| Arco | `pulso_retorno` · Pulso de Retorno | III | 12 s | Dos impactos al mismo rival a distancia mínima de 12 bloques en 6 s: cura 1 HP por nivel. |
| Arco | `ojo_tormenta` · Ojo de Tormenta | III | 8 s | Tras 1,5 s inmóvil, disparo cargado gana 10% de velocidad por nivel y 10 ticks sin gravedad. No es un proyectil teledirigido. |
| Tridente | `ancla_abisal` · Ancla Abisal | III | 10 s | Lanzamiento y posterior golpe cuerpo a cuerpo con esta rare al mismo enemigo en 6 s: +15% de daño por nivel y atracción. |
| Tridente | `marea_ascendente` · Marea Ascendente | III | 12 s | Alternar lanzamiento/cuerpo a cuerpo al mismo rival en 6 s libera empuje en radio 4, hasta 8 objetivos visibles. |
| Tridente | `resaca` · Resaca | III | 12 s | Impactar bloque crea remolino 3 s con pulsos de Lentitud I–III de 2 s en radio 3. |
| Tridente | `conductor_tempestades` · Conductor de Tempestades | III | 12 s | Tercer lanzamiento al mismo rival en 8 s descarga sobre un segundo enemigo visible a 4 bloques: 2 HP por nivel antes de defensas. |

Las doce son gratuitas en saturación y usan recargas. Los porcentajes de daño, la velocidad y el empuje crecen con nivel; los límites anteriores son los implementados. La recarga se comparte entre copias de la misma rare del jugador, no entre jugadores. Los impactos cancelados o sin daño no progresan combos; Quebranto admite el impacto bloqueado sin añadir daño. El daño secundario no progresa estas rares ni activa las rares de combate anteriores.

## Recetario y materiales

`/rares` → Cámara de infusiones → Ballestas / Arcos / Tridentes. Los efectos antiguos válidos para todas las armas a distancia conservan su sección compartida. En Materiales de forja se incluyen también los seis nuevos materiales de proyectiles.

Cada rare requiere cuatro unidades de su reactivo vanilla, dos unidades del material personalizado de su familia, un núcleo de boss válido, una Rare Essence base y un cristal épico. La cuadrícula de ingredientes de la interfaz contiene las coincidencias exactas.

| Material | Familias que pueden soltarlo |
|---|---|
| Engranaje Umbrío | Saqueadores y vindicadores |
| Pólvora Resonante | Creepers, blazes, cubos de magma |
| Pluma del Vendaval | Abejas, fantasmas, ghasts, loros, allays, murciélagos, esqueletos y bogged |
| Cristal de Escarcha | Strays, osos polares, gólems de nieve |
| Perla de las Profundidades | Guardianes, ahogados, delfines, calamares, ajolotes, tortugas y peces |
| Filamento de Tormenta | Evocadores, brujas, ilusionistas, shulkers, wardens y endermen |

Una unidad con probabilidad 12% en minijefes o 25% en bosses de la familia, sin convertir todos los bosses en fuentes de todos los materiales. También se fabrica una unidad con dos núcleos, una esencia base, un fragmento y su representación vanilla; la interfaz muestra la receta.

## Rendimiento y validación

Máximo 1024 proyectiles registrados durante 60 s, 8192 progresiones de combo, 256 tareas cortas y 8 objetivos por área. Partículas mediante CosmeticFX, alcance 32 bloques y presupuesto compartido; no se añaden búsquedas permanentes de todos los jugadores. Limpieza de estados caducados cada segundo y restauración de gravedad al desactivar el plugin. Las trampas no usan explosiones destructivas ni rayos incendiarios. El daño pasa por eventos normales y los aliados del marcador / mundos sin PvP se excluyen.

Maven verify y pruebas unitarias de catálogo, exclusividad, progresión y regresiones. No se ejecutó Paper ni se comprobó el aspecto visual o el equilibrio con jugadores: deben probarse especialmente protecciones regionales, escudos, movimiento lateral y convivencia con otros plugins antes de producción.
