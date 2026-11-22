# CazaJefes 1.52.0

## Huevos e interfaz

Todos los huevos muestran tres circunferencias negras y moradas, de **1, 2 y 3 bloques de radio**, con rotación alterna. Los anillos comparten 48 puntos por actualización; el exterior recibe más puntos para conservar la forma. Se mantienen el alcance y los presupuestos globales de partículas.

La presentación se configura en `combat.eggParticles` de cada JSON:

```json
{"radii":[1,2,3],"points":48,"ringHeight":0.12,"ringSpacing":0.16,"removeGeneratedPedestal":true}
```

`points` es el total compartido entre los tres anillos (24–64), y `ringHeight`/`ringSpacing` controlan su elevación. Los ajustes instalados de 1.51.0 reciben los campos nuevos; sus 32 puntos predeterminados pasan a 48. Los radios nuevos sustituyen la hélice anterior.

El recetario usa paneles azules uniformes en todos los bordes. Inicio: **Esencias, Libros, Cristales, Materiales, Brújula y Todas las recetas**. El acceso a Defensas PvP conserva únicamente las cuatro piezas defensivas disponibles. Los nombres y colores personalizados de `interfaces/recetas.json` se conservan; se actualizan los valores predeterminados antiguos.

## Golpes y dificultad

Se comparó el diseño con dos encantamientos públicos de EcoEnchants, de Auxilor:

- [Shura](https://github.com/Auxilor/EcoEnchants/blob/master/eco-core/core-plugin/src/main/resources/enchants/shura.yml), de rareza Rare, multiplica el daño crítico del hacha por 1,25/1,50 en sus niveles I/II cuando la vida está por debajo del 51%.
- [Boss Hunter](https://github.com/Auxilor/EcoEnchants/blob/master/eco-core/core-plugin/src/main/resources/enchants/boss_hunter.yml) añade un 10% de daño por nivel al disparar contra bosses, hasta VIII. Su configuración dispara el efecto por impacto válido.

Son referencias de progresión y activación; no son una medición de DPS entre servidores. La frecuencia real también depende de distancia, armadura, invulnerabilidad y otros plugins.

En CazaJefes, los golpes básicos ya no reparten el daño en tres contactos débiles al avanzar de etapa. Se comprueba la distancia entre hitboxes, también para bosses grandes. Los especiales móviles ya liberados permiten continuar el combate cuerpo a cuerpo; sus avisos, animaciones ancladas y estados de aturdimiento siguen deteniendo esos golpes. Los golpes normales no ocupan la hotbar con avisos de especiales.

| Categoría | Intervalo mínimo básico | Multiplicador cuerpo a cuerpo | Multiplicador especial |
|---|---:|---:|---:|
| Minijefe / Común | 12 ticks | 1,00 | 1,00 |
| Raro | 12 ticks | 1,06 | 1,05 |
| Épico | 11 ticks | 1,12 | 1,10 |
| Legendario | 10 ticks | 1,18 | 1,15 |
| Mítico | 10 ticks | 1,25 | 1,20 |
| Abismal | 10 ticks | 1,33 | 1,25 |

Los multiplicadores se aplican una vez sobre los daños correspondientes, además de la progresión de etapas existente. Los ataques que ya tienen daño fijo en corazones conservan sus valores definidos. No se reinicia la inmunidad del jugador ni se ignoran eventos cancelados o límites de arenas.

En cada JSON, `ai.melee` controla `intervalTicks`, `warningTicks` y `duringMobileSpecials`. Para `MELEE_SWEEP`, esas opciones tienen prioridad sobre `cooldownTicks` y `warningTicks` escritos dentro de cada ataque. Daño, radio y peso de cada ataque se conservan. `combat.difficulty` permite cambiar `meleeDamageMultiplier` y `specialDamageMultiplier` por jefe.

El anuncio global al abrir el huevo muestra encabezado, nombre del boss y categoría con el color de su rareza, en todos los mundos.

## Brújula y recetas retiradas

La nueva receta de Brújula de Caza usa esta distribución:

| Fragmento de Tributo | Diamante | Fragmento de Tributo |
|---|---|---|
| Esmeralda | Brújula normal | Lingote de hierro |
| Fragmento de Tributo | Redstone | Fragmento de Tributo |

Los cuatro fragmentos son objetos de CazaJefes; un fragmento de disco vanilla no los sustituye. `recipes.yml` conserva la posibilidad de editar esta receta.

Se eliminaron los registros y definiciones de crafteo de armas de categoría, cinco armas especiales antiguas, cuatro armas PvP y tributos. También se retiraron las compras de armas y los cuatro materiales PvP ofensivos que ya no tienen receta asociada. Los restos de esas recetas se limpian al iniciar o recargar.

Los tributos continúan como botín. El equipo de categoría continúa como botín y con `/ri equipo <categoría> [armadura|espada|hacha|todo]`. La infusión de rares en armas sigue funcionando. Los libros, esencias, cristales, materiales útiles y cuatro defensas PvP conservan sus recetas.

## Armas especiales de botín

Algunos bosses Épicos, Legendarios, Míticos y Abismales tienen una tirada adicional de arma especial del **3/4/5/6%**, respectivamente. La elección predeterminada sigue su familia: hacha para zombies/husks/vindicadores, espada para magos, arco para tiradores, tridente para blaze/cubos de magma y mandoble para warden/enderman/esqueletos Wither.

Se configura por boss en `combat.specialWeaponLoot`:

```json
{"enabled":true,"item":"hacha_sismica","chance":0.03,"level":1}
```

Se admiten `espada_brezal`, `hacha_sismica`, `arco_ceniciento`, `lanza_magma`, `mandoble_vacio`, y opcionalmente `pvp_gear_hemorragia`, `pvp_gear_fractura`, `pvp_gear_ocaso`, `pvp_gear_resonancia`. `level` (I–V) mejora el encantamiento ofensivo vanilla del arma; las habilidades intrínsecas de equipo PvP conservan su funcionamiento. Las esencias aplicables no vienen instaladas en el botín. Un ajuste inválido desactiva esa tirada adicional.

## Verificación

Los cuatro módulos compilan y pasan **361 pruebas automatizadas** (46 del motor y 315 del plugin). La sonda en Paper 1.21.11 confirmó las nueve posiciones de la receta de brújula y sus fragmentos exactos, ausencia de crafteos de armas/tributos, armas disponibles con `/ri equipo` y paneles azules uniformes. Cuatro clientes de protocolo recibieron los anillos de radios 1/2/3, con los dos colores previstos.

En una prueba controlada de contacto básico, el boss conectó siete golpes aceptados durante 85 ticks (4,25 s), separados por 13 ticks con la preparación de un tick incluida. Esa medición comprueba regularidad en ese escenario; no representa todas las peleas ni un benchmark contra EcoEnchants. La revisión gráfica y el balance final requieren una sesión de juego con jugadores.
