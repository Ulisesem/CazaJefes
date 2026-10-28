# CazaJefes 1.19.0

## Combate y efectos

- Ceguera y náuseas: 200 ticks (10 segundos a 20 TPS); cada efecto tiene un límite de una aplicación aceptada cada 25 segundos por jugador, compartido entre bosses. No se reinicia su recarga al retirar el efecto.
- Se registra qué boss aplicó cada efecto negativo. Al comenzar su muerte se retiran los efectos registrados todavía coincidentes en jugadores conectados; no se borran indiscriminadamente pociones externas ni se retiran los efectos de otro boss que los haya sustituido. Los efectos anteriores a esta actualización no tienen atribución recuperable. La explosión final conserva daño, sonido y empuje, pero ya no aplica debuffs nuevos después de la victoria.
- Avisos sobre la hotbar: retención y refresco de tres segundos desde la última actualización; un aviso más reciente sustituye al anterior. Las cargas especiales de suelo, ballesta, arco, magos, burst y Cenit ofrecen ahora tres segundos. La combinación de suelo también tiene tres segundos de advertencia.
- Recuperación compartida de tres segundos después de especiales de suelo, burst, salva de ballesta, tríada de arco y hechizos de mago. Esto impide que otros controladores comiencen otra habilidad durante la recuperación. El burst conserva su recarga de 25 segundos; mirada abismal 25 segundos, arco 15, ballesta 12, hechizos 18 o 25. La recuperación puede retrasar una habilidad preparada.
- Persecución con actualización de ruta cada 0,5 segundos y flanqueo ante escudos para atacantes cuerpo a cuerpo. Se conservan selección del último agresor, predicción de proyectiles, movimiento por especie y protección de colisiones. No se afirma que ninguna inteligencia pueda acertar siempre ni resolver cualquier terreno.
- **Cadenas del Abismo:** después de al menos un golpe aceptado, si el jugador está inaccesible o por encima del cuerpo del boss y no ha recibido contacto durante tres segundos, el boss anuncia un reclamo de tres segundos. Reserva esa ventana para que no la ocupe otra habilidad. Busca suelo seguro cerca del encuentro y trata de trasladar al atacante; respeta cancelaciones de teleport, agua, límites del mundo y colisiones. Recarga 25 segundos. Si no hay sitio seguro, no fuerza el traslado ni destruye bloques.

## Burst visual

Cuatro alturas de anillos adaptadas a la caja corporal del jugador o boss, con pequeñas cruces blancas de polvo de tamaño 0,4. Las microestrellas giran y la onda se expande; ya no se utiliza el sprite grande END_ROD para este efecto. Máximo 32 emisiones por fotograma y ocho fotogramas por activación, con alcance de 32 bloques y el presupuesto cosmético existente. El presupuesto puede reducir detalle con muchos efectos simultáneos. Requiere evaluación visual en el cliente; no incluye un paquete de texturas nuevo.

## Legado de la armadura

| Nombre visible | ID de rare | Niveles | Poder |
| --- | --- | --- | --- |
| Corazón Indómito | hardy | I–III | +2 HP máximos por nivel: hasta tres corazones. Mayor nivel equipado; no se suma entre piezas ni cura al ponerse la armadura. |
| Alas del Firmamento | fly | I–V | Vuelo con consumo de saturación cada diez segundos, incluso flotando sin desplazarse; consumo dividido por el cuadrado del nivel. |
| Jardín de la Ruina | spore | I–III | Aura de veneno del nivel correspondiente, a cuatro bloques, con línea de visión, pulsos cada cinco segundos y coste de saturación. Solo jugadores con los que hubo un golpe PvP aceptado en los últimos diez segundos. Excluye equipos del scoreboard. |
| Forja Viviente | durability | I–V | Repara cada pieza equipada tres puntos por nivel cada cinco segundos: hasta quince. Conserva coste de saturación. |
| Pulso de Aurora | regeneration | I–III | Regeneración del nivel correspondiente, refrescada sin necesidad de caminar; mayor nivel equipado y coste de saturación. |
| Furia del Titán | strength | I–III | Fuerza del nivel correspondiente; separada de Hardy, con coste de saturación y sin sumar piezas. |
| Baluarte de Obsidiana | turtle | I–V | 10/20/30/40/50% de anular solo el daño adicional de Backstab del plugin; conserva el golpe base. |
| Sello del Alba | garlic | I–V | 10/20/30/40/50% de impedir la curación de Vampirismo/Vampiric Regeneration del plugin. |

Todas admiten únicamente armaduras. Hardy ahora admite todas las piezas. Las armaduras de colección de boss siguen necesitando Resonancia Primigenia antes de nuevas infusiones. Los máximos nuevos prevalecen sobre valores antiguos de configuración. Se conserva el equipo existente y no se borran mejoras guardadas; niveles superiores al nuevo máximo se limitan al activar el poder.

Las defensas Turtle/Garlic afectan a las rares de CazaJefes, no a habilidades desconocidas de otros plugins. La alianza comprobada es la del scoreboard: no existe integración específica con Factions. Spore deja de convertir bloques en musgo; ahora es una esencia exclusiva de armadura.

## Recetas e interfaz

`/rares` → Cámara de infusiones → Armaduras. Las ocho esencias tienen nombre de poder, descripción, nivel romano, máximo y compatibilidad. Nueva cabecera Legado de la Armadura. Las recetas se muestran con sus ingredientes reales y conservan la fabricación atómica sin consumir si faltan materiales o espacio.

El patrón alquímico es ` P /CKR/ FB`: poción P, núcleo válido C, cristal K, reactivo R, fragmento F y Rare Essence básica B.

| Poder | Poción | Reactivo | Cristal |
| --- | --- | --- | --- |
| Corazón Indómito | Curación II | Corazón del mar vanilla | Épico |
| Alas del Firmamento | Caída lenta | Élitros vanilla | Abismal |
| Jardín de la Ruina | Veneno | Flor de esporas | Raro |
| Forja Viviente | Regeneración | Membrana de fantasma | Épico |
| Pulso de Aurora | Regeneración | Rodaja de sandía reluciente | Legendario |
| Furia del Titán | Fuerza | Vara de blaze | Legendario |
| Baluarte de Obsidiana | Maestro tortuga | Escama de tortuga | Épico |
| Sello del Alba | Regeneración | Panal | Legendario |

Se crean esencias de nivel I y se conserva el mecanismo existente de infusión/niveles. Los drops de boss siguen siendo esencias base y equipo sin rares incorporadas. Se conservan anulaciones válidas de recetas del servidor; si un administrador configuró otra receta, puede prevalecer la configuración existente.

## Validación e instalación

Java 21, API Paper 1.21.11. Maven verify, 190 pruebas: compilación, recursos, límites de vida/defensa/reparación, categorías, recetas distintas, geometría corporal, tiempos, y regresiones anteriores. JAR revisado como ZIP y bytecode; no se ha arrancado Paper ni probado un cliente Minecraft en este entorno. La apariencia, compatibilidad con plugins del servidor y coste real bajo carga deben comprobarse en una copia del servidor.

Detener el servidor, respaldar plugins/CazaJefes, retirar el JAR anterior y colocar solo CazaJefes-1.19.0.jar. Conservar datos y configuración; reiniciar y probar encuentros antes de abrir al público.
