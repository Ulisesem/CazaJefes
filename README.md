# CazaJefes

Plugin de encuentros y progresión para **Paper 1.21.11**, con **Java 21**. Versión actual: **1.19.0**.

El bestiario contiene **168 bosses**, repartidos en seis categorías de 28: minijefes, raros, épicos, legendarios, míticos y abismales. Los encuentros comparten tres etapas de combate, habilidades especiales, recompensas por categoría y seguimiento del daño de los jugadores.

## Instalación

1. Detener el servidor y guardar una copia de plugins/CazaJefes.
2. Retirar el JAR anterior y colocar CazaJefes-1.19.0.jar en plugins.
3. Arrancar el servidor conservando la configuración existente.
4. Consultar /bestiario y probar un encuentro antes de abrirlo a todos los jugadores.

Solo se admiten IDs actuales de bosses. Los datos con IDs antiguos ya no se convierten ni se reconocen. No instalar dos versiones del plugin a la vez.

## Para los jugadores

| Comando | Uso |
| --- | --- |
| /bestiario | Categorías, encuentros, defensas y ataques definitivos |
| /rares | Recetas y forja |
| /mercado | Intercambios de la Grieta |
| /ri compass | Brújula de huevos dormidos |
| /ri particulas <calidad> | Calidad personal de las auras permanentes |

Los libros activables usan clic derecho en el aire o sobre un bloque permitido. El burst no desplaza a jugadores con OP. Ascensión del Firmamento eleva jugadores en todas las etapas; Cataclismo del Destierro los repele en la tercera. Ambos usan potencia X y muestran carga y recarga sobre la hotbar. Los especiales ofensivos superan la referencia de daño de los golpes normales. Cada fase tiene cuatro ataques especiales y la dificultad aumenta durante el encuentro. Ascensión empuja en diagonal, conservando su elevación. Los saqueadores usan ballestas y responden a cinco golpes recientes con Tempestad del Destierro: seis ráfagas de treinta flechas no recogibles, puntería predictiva y empuje fuerte.

Los huevos registrados pueden despertarse sin OP en supervivencia o aventura con permiso de eclosión. La brújula deja de seguirlos en cuanto se activan; puede pasar a otro huevo disponible.

## Combate 1.19.0

- Armadillos: cobertura de 5 s cada 15 s, sin atacar hechos bola; cura pequeña al terminar.
- Shulkers: Refugio del Umbral cierra la caja 3 s y cura hasta 5 %, con recarga de 25 s.
- Abejas: giros y aproximación ondulante; persecución persistente del agresor dentro de límites de mundo y distancia.
- Hordas de seis, con colocación comprobada y límites globales. Los magos invocan sanadores que curan 3 % con recarga compartida de 15 s y aviso en el chat.
- Evocadores en III: Juicio de las Seis Tormentas; además, nuevos especiales de control para magos.
- Esqueletos arqueros: armadura de diamante/netherita con Protección X, arco reforzado, puntería predictiva y Tríada del Eclipse.
- Etapa III: +5 % de daño salvo renacidos. Ceguera 5 s; náuseas 10 s. Avisos dorado/aqua sin estrellas.
- Burst de estrellas blancas adaptado al cuerpo y cilindro final de 20 bloques.
- Las tiradas de rares/libros preparados se convierten en Rare Essence sin poder asignado. El equipo sigue cayendo sellado y sin rares incluidas. Resonancia Primigenia solo se fabrica.

Detalles, recargas, límites y pruebas pendientes en [cambios 1.19.0](docs/CAMBIOS-1.19.0.md). No se garantiza puntería infalible ni TPS con cualquier carga.

## Infusiones por equipo y desbloqueo

`/rares` → Cámara de infusiones o Rares PvP → escoger el equipo compatible. Armaduras, espadas/hachas, arcos y picos/herramientas se consultan por separado; las esencias muestran su destino y la mesa valida esa compatibilidad.

El equipo de boss no admite nuevas rares hasta aplicar **Resonancia Primigenia**. Fabricar el sello desde la Cámara de infusiones y combinar una unidad con una pieza sellada en la mesa. No borra sus datos ni cambia los niveles máximos. Los nuevos drops de armas/armaduras llegan sin rares personalizadas, pero mantienen los encantamientos vanilla. Las mejoras existentes de jugadores se conservan.

Todos los huevos registrados usan aura negra y morada. Ver [receta, drops, operaciones de reparación y compatibilidad](docs/INFUSIONES-Y-SELLOS.md).

## Forja de duelo · Rares PvP

Ocho rares nuevas para PvP: Juramento Carmesí, Desgarro del Bastión, Arco del Ocaso, Voz de la Tormenta, Vigía de Ónix, Coraza de Quitina, Grebas Rompecadenas y Paso Espectral. Cada pieza tiene un material exclusivo, una receta real, un disparador y recarga individual; no añade tareas periódicas de escaneo.

Abrir `/rares` y seleccionar **Rares PvP** o **Materiales de duelo**. Las fichas enlazan la receta de cada pieza con su material. La pieza base se consume y el resultado no transfiere mejoras previas: usar una base sin mejoras.

Ver [poderes, materiales y recetas](docs/DUELO-PVP.md). Son habilidades contra jugadores, con efectos breves, PvP habilitado y respeto por daño cancelado. Las ideas adicionales están separadas de lo implementado.

## Novedades de combate

- Burst compartido cada 25 segundos; no se acumulan Ascensión y Ruptura.
- Combos por familia de mob, hechizos anunciados y Cenit del Abismo en etapa III: carga de 2 segundos, mirada hacia arriba durante 5 segundos y recarga de 25 segundos.
- Minijefes 30 % mayores respecto a su tamaño anterior, daño asistido reforzado y vida base mínima de 360 para nuevas apariciones, salvo configuración explícita.
- Agilidad Acuática X para todos los bosses; inmunidad al fuego, lava, ceguera, náuseas y fatiga. Las entidades sin botas usan movimiento acuático controlado.
- Efectos negativos de los ataques de bosses: ceguera 5 s, náuseas 10 s y los demás 30 s. La levitación breve que sostiene el burst conserva su temporizador independiente.
- Ballestas con Poder X; armas NPC con Filo X e Irrompibilidad X; Filo X equivalente en golpes sin arma, sin duplicar el bonus de armas encantadas.
- Shulkers: 20 bolitas, 4 corazones por impacto aceptado, veneno, wither y levitación. Se respetan bloqueos, inmunidad entre golpes y cancelaciones.
- Recuperación de bosses y súbditos atrapados sin recargar vida. Reclamo de la Grieta advierte antes de sacar de su refugio a atacantes inalcanzables.
- Brújula con clic derecho en el suelo, flechas de estrellas lima y avisos de ausencia de huevos dormidos. No rastrea bosses ya despiertos.
- Esencias tituladas Rare Essence; tipo de rare en gris y propiedades del equipo en naranja con números romanos, por ejemplo Durability V.

## Registro de victorias

La mini scoreboard muestra el total personal. Solo cuenta el golpe final de un jugador válido en supervivencia o aventura, incluidos sus proyectiles. Las ayudas aparecen en el informe de daño, pero no reciben la victoria. Los renacimientos, súbditos y muertes ambientales no suman.

Usar `/ri estadisticas [jugador]` y `/ri ranking`. El registro empieza desde esta versión; no inventa victorias de versiones que no las guardaban. Se conserva por UUID en `plugins/CazaJefes/boss-statistics.json` y se exporta a CSV. Ver [integración futura con hologramas](docs/ESTADISTICAS-HOLOGRAMAS.md).

## Encuentros y recompensas

Los minijefes usan animales y bestias. Los bosses animales invocan súbditos de su especie con persecución propia, ataques de contacto y protección contra morir de un solo golpe directo. Sus drops son tributos, fragmentos y cristales comunes. Las categorías superiores añaden equipo, núcleos, esencias y materiales según sus tablas. El equipo que lleva el boss no es el equipo obtenido como recompensa.

Al morir definitivamente, el boss se eleva y cae con un impacto anunciado. Los rayos son visuales y no generan fuego. Después, cada unidad de botín desciende en una espiral suavizada durante unos ocho segundos; todas bajan simultáneamente a posiciones próximas. Las esencias tienen un efecto de aterrizaje propio. El impacto hace cuatro a cuatro corazones y medio incluso con absorción de manzana encantada y añade retroceso, respetando las cancelaciones y bloqueos descritos en el registro de cambios.

Los especiales y adornos de combate usan un presupuesto compartido de partículas, alcance de 32 bloques y límites de proyectiles. Esto reduce trabajo y envíos; no garantiza TPS ni FPS con cualquier cantidad de bosses. Ver los límites y pruebas pendientes en el registro de cambios.

## Administración y desarrollo

- [Comandos, permisos y apariciones programadas](docs/ADMINISTRACION.md)
- [Cambios y compatibilidad de esta versión](docs/CAMBIOS-1.19.0.md)
- [Catálogo completo de IDs](docs/BOSSES-IDS-1.19.0.csv)
- [Ideas de materiales y recetas, todavía propuestas](docs/IDEAS-MATERIALES.md)
- [Organización del código](docs/ARQUITECTURA.md)
- [Notas de versiones anteriores y reconstrucción](docs/HISTORIAL-LEGADO.md)

Para compilar y validar:

```text
mvn verify
```

El JAR compilado queda en target. Las pruebas verifican catálogo, progresión, daño, recetas, permisos y reglas de animación. **No se ha ejecutado esta versión en un servidor Paper ni verificado visualmente con clientes reales.** Los 92 encuentros nuevos reutilizan las familias de combate existentes y tienen sus propios perfiles de ataque; deben ajustarse con pruebas de juego.

## Cambios de 1.19.0

Consulta [los cambios de combate y rares](docs/CAMBIOS-1.19.0.md). Ceguera y náuseas de bosses duran diez segundos y tienen recargas independientes de 25 segundos por jugador. Los efectos registrados de un encuentro se retiran al morir su jefe. Los avisos sobre la hotbar se mantienen tres segundos; la nueva advertencia puede sustituir la anterior.

En `/rares` → Cámara de infusiones → Armaduras encontrarás Corazón Indómito, Alas del Firmamento, Jardín de la Ruina, Forja Viviente, Pulso de Aurora, Furia del Titán, Baluarte de Obsidiana y Sello del Alba. Son nombres visibles nuevos para Hardy, Fly, Spore, Durability, Regeneration, Strength, Turtle y Garlic. Los identificadores internos de rares existentes se conservan para reconocer el equipo; los IDs actuales de bosses siguen sin alias antiguos.
