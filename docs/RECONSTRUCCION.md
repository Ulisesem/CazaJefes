> Informe histórico de la base 1.0.0. Los cambios y la validación de 1.0.1 están en CORRECCIONES-1.0.1.md y validation/. Evidencia anterior: validation-1.0.0/.

# Reconstrucción y validación

Fecha: 24 de octubre de 2026. Destino: Paper 1.21.11, Java 21.

## Evidencia del adjunto

Archivo recuperado de los adjuntos de la conversación «Estado plugin bosses»: `CazaJefes-0.9.7(1).jar`, 123807 bytes. SHA-256:

```text
73652939e0029b7fca64b30540bcde01569f4854b79316af392cb18500e95a29
```

La ruta `/mnt/data/…` no existe en el entorno Windows actual; se utilizó el adjunto exacto suministrado por el historial. Su huella coincide con la auditoría local del 14 de octubre. Esa auditoría se usó como orientación, y el adjunto se volvió a abrir y descompilar en esta tarea. Las respuestas históricas del asistente solo aparecían como referencias truncadas; no se inventó su contenido.

Inspección ZIP/CRC, recursos, metadatos y firmas con `javap -p`. CFR 0.152 se probó inicialmente; la recuperación finalmente usada es de **Vineflower 1.11.2**. Ninguna herramienta llamó a métodos del plugin ni inició su clase principal. Se ejecutaron el descompilador, herramientas de compilación y pruebas del proyecto nuevo, no el plugin original.

El adjunto tiene 24 clases, todas con bytecode major 65. Incluye descriptores YAML y un POM con versión 0.9.4, mientras `plugin.yml` declara 0.9.7. No contiene fuentes Java ni pruebas originales. La versión de ambos descriptores del proyecto nuevo es 1.0.0.

## Funciones identificadas y conservadas

| Clase | Responsabilidad |
|---|---|
| CazaJefesPlugin | Ciclo de vida, listeners, comando y recetas |
| BossCatalog | 62 bosses, diez minis, seis rangos y aliases históricos |
| BossManager | Huevos persistentes, entidades, combate, barras, súbditos y muerte |
| HuntTracker | Brújula, objetivos del mismo mundo, partículas y scoreboard |
| RelicItems | Monedas, núcleos, equipo, cinco armas especiales, libros/esencias, propiedades, recetas y drops |
| CazaJefesCommand | Comandos administrativos, spawnpoints, bestiario, mercado y menús |

Se recuperan también records y clases internas: Definition, Tier, Encounter, TrackedEncounter, BossAttackTask, Session, DropGroup, MenuHolder, TradeOffer, GearTierSpec, VisualRecipe y tareas anónimas. El listado íntegro de clases y firmas está en `CLASES-Y-METODOS-ORIGINAL.txt`; los metadatos en `INSPECCION-ORIGINAL.json`.

Nuevas clases: RecipeSettings (overrides), RaresMenu (GUI y fabricación), CraftPlan (consumo verificable) y CombatGuard (protecciones). No se empaqueta bytecode original: el JAR nuevo se genera compilando todas las fuentes del proyecto.

## Reparación de fuentes

Vineflower produjo 33 errores de compilación inicialmente. Se repararon principalmente colecciones sin parámetros de tipo, variables capturadas y una referencia de un switch a una variable inexistente en la selección de armadura. Se contrastaron tipos y estructura con el desensamblado y la segunda descompilación. Las correcciones hacen compilable la recuperación; no demuestran equivalencia semántica perfecta con el binario anterior.

El diff `CAMBIOS-DESDE-RECUPERACION.diff` compara los archivos finales contra la salida inicial de Vineflower. Los recursos originales se conservan en `original-resources/`, fuera de los recursos compilados. No se recuperaron comentarios, nombres locales originales, historial Git, archivos de desarrollo ni licencia. No se promete una recuperación textual del fuente original.

## Verificación efectuada

- Maven 3.9.11, JDK 21.0.12: `mvn verify`, **BUILD SUCCESS**. La comprobación final se repitió sin conexión, con las dependencias ya descargadas.
- Seis pruebas JUnit: dos del catálogo/aliases y cuatro de planificación de consumo. Cero fallos, errores u omisiones.
- Inspección del JAR de salida: ZIP/CRC correctos, bytecode major 65, cuatro YAML y clases de entrada/GUI/protecciones presentes. `validation/JAR.json` registra huella y entradas.
- Los logs y resultados de pruebas están en `validation/`.

No se ejecutó Paper, no se conectó un jugador y no se probó el plugin nuevo en juego. No se ha certificado el balance, la compatibilidad con plugins externos, todos los efectos de reliquias ni todas las combinaciones de crafteo del servidor. El resultado es un **JAR de prueba compilado**, no una release validada en producción. Las pruebas unitarias cubren la planificación del descuento; no cubren los eventos de inventario reales ni la entrega de resultados en Bukkit.

## Decisiones y límites

- Se preserva gran parte del código recuperado y se añaden mejoras concretas. No se sustituye el catálogo por una demostración reducida.
- Las recetas configurables son modificaciones de recetas shaped existentes. Las alternativas de ingredientes personalizados se heredan del registro original. La fusión shapeless sigue separada.
- Los ataques originales continúan; se añaden fases, telegráficos, límites y protecciones. Algunos daños siguen siendo constantes o dependen del rango. Algunas propiedades conservan temporizadores incrustados.
- Se conserva el sistema original de drops y se añade control de crédito y pago único; no se introduce una tabla de botín totalmente configurable.
- No hay migrador general de metadatos de objetos antiguos. El cambio de nombres puede afectar coincidencias exactas. No se reconstruye contenido de contenedores perdido por la versión anterior.
- No hay integración específica con WorldGuard ni comprobación exhaustiva de todos los exploits. Las capacidades de manipulación de bloques y las interacciones con protecciones externas requieren la prueba manual indicada.
- Repositorio confirmado por el usuario: `Ulisesem/CazaJefes`. Se incluyen fuentes, evidencia y JAR de prueba.
