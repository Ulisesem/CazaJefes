# Cambios de CazaJefes 1.1.0

Bestiario y forja por categorías, nuevos IDs basados en nombres, cristales de progresión y tres fases de dificultad creciente. Conserva los 62 bosses y los 10 minijefes del catálogo.

## Botín de bosses principales

Además de fragmentos y un cristal de su rango garantizados, se hacen tiradas independientes:

| Categoría | Tributo | Núcleo | Armadura | Arma | Rare | Esencia | Libro | Material |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
| Raro | 45 % | 55 % | 11,5 % | 8,5 % | 11,5 % | 17,5 % | 11 % | 55 % |
| Épico | 50 % | 60 % | 15 % | 11 % | 15 % | 23 % | 14 % | 55 % |
| Legendario | 55 % | 65 % | 18,5 % | 13,5 % | 18,5 % | 28,5 % | 17 % | 55 % |
| Mítico | 60 % | 70 % | 22 % | 16 % | 22 % | 34 % | 20 % | 55 % |
| Abismal | 65 % | 75 % | 25,5 % | 18,5 % | 25,5 % | 39,5 % | 23 % | 55 % |

Míticos/Abismales dan esencia primordial al cumplirse su roll de esencia. Abismales tienen otro roll del 15 % de esencia abisal. Los minijefes NO usan esta tabla: exclusivamente 1–2 tributos y 1–3 fragmentos.

## Correcciones adicionales

- CombatGuard leía `boss` mientras BossManager escribía `is_boss`: se unifica la marca mediante BossKeys. Esto corrige crédito de botín y protecciones que no reconocían al boss real.
- Los sistemas de entidades ya cargadas se reanudan al arrancar. Antes el inicio solo normalizaba atributos; ahora también recupera encuentros/barras. Al descargar chunks se cancelan tareas y al cargar se reanudan.
- La migración comprueba el YAML guardado, sin confundir defaults nuevos con valores ya escritos. Respaldos antes de migrar.
- Se retira el antiguo desorden de hotbar de cualquier fase: el nuevo efecto es exclusivo de fase final y tiene límites de tiradas y éxitos.
- Las recetas de equipo utilizan bases distintas por pieza/arma, evitando formas idénticas que produjeran resultados distintos.

## Validación

Maven verify: 26 pruebas, cero fallos/errores/omitidas. Las nuevas simulaciones hacen 1000 tiradas por minijefe y 100 por boss principal con semillas; no son muertes reales de Bukkit. Cubren categorías/alias completos, tabla de loot, exclusiones, fases, probabilidades/cooldowns, categorías de recetas y recursos YAML.

ZIP/CRC y Java 21 comprobados. No se ejecutó Paper ni un cliente. Faltan prueba visual/funcional de las interfaces y eventos, balance y compatibilidad con otros plugins. Evidencia actual en validation; las notas 1.0.x son históricas.

Migración, configuración y límites en README. La tabla completa de IDs antiguos/nuevos está en BOSSES-IDS-1.1.0.csv.
