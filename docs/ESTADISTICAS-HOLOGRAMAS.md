# Registro de victorias e integración futura

Solo recibe una victoria quien causa el golpe final directo o con su proyectil, en supervivencia o aventura. El boss debe tener crédito válido de jugador. Ayudantes, renacimiento, súbditos, muerte ambiental y pruebas en creativo no suman.

La mini scoreboard muestra el total al seguir un huevo. Consultas públicas: `/ri estadisticas`, `/ri estadisticas nombre` y `/ri ranking` (diez primeros).

## Datos

`plugins/CazaJefes/boss-statistics.json` es el registro persistente: esquema 1, jugadores por UUID con nombre observado, total, mapa de bosses por ID actual, mapa de categorías y fecha de última victoria en milisegundos Unix. Conserva los últimos 1000 IDs de encuentro como defensa adicional contra duplicación. La protección principal de entrega única del encuentro permanece activa.

`plugins/CazaJefes/boss-statistics.csv` exporta UUID, nombre, total y última victoria. Las escrituras se agrupan cada segundo y se hacen fuera del hilo de combate mediante sustitución atómica; al apagar se espera su finalización. Un apagado abrupto puede perder el último intervalo. No editar los archivos mientras el servidor está encendido. Empieza en cero: no existían registros fiables para reconstruir victorias antiguas.

## API para un adaptador de hologramas

Desde el hilo principal de Bukkit:

```java
BossStatistics stats = Bukkit.getServicesManager().load(BossStatistics.class);
long total = stats == null ? 0 : stats.total(player.getUniqueId());
List<BossStatistics.PlayerStats> top = stats == null ? List.of() : stats.ranking(10);
```

La API es pública en `dev.cazajefes.BossStatistics`; también se obtiene desde `CazaJefesPlugin.statistics()`. Los mapas entregados en PlayerStats son inmutables. Usar la API en el hilo principal; un lector externo puede consumir las exportaciones de disco.

Esta versión no registra placeholders de PlaceholderAPI ni crea hologramas. Un adaptador futuro debe escoger el plugin de hologramas instalado y traducir total/ranking a sus líneas o placeholders. No se prometen nombres de placeholders todavía inexistentes.
