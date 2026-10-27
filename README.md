# CazaJefes

Plugin de encuentros y progresión para **Paper 1.21.11**, con **Java 21**. Versión actual: **1.13.3**.

El bestiario contiene **168 bosses**, repartidos en seis categorías de 28: minijefes, raros, épicos, legendarios, míticos y abismales. Los encuentros comparten tres etapas de combate, habilidades especiales, recompensas por categoría y seguimiento del daño de los jugadores.

## Instalación

1. Detener el servidor y guardar una copia de plugins/CazaJefes.
2. Retirar el JAR anterior y colocar CazaJefes-1.13.3.jar en plugins.
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

Los libros activables usan clic derecho en el aire o sobre un bloque permitido. El burst no desplaza a jugadores con OP. Ascensión del Firmamento eleva jugadores en todas las etapas; Ruptura del Horizonte los repele en la tercera. Ambos usan potencia X y muestran carga y recarga sobre la hotbar. Los especiales ofensivos superan la referencia de daño de los golpes normales. Cada fase tiene cuatro ataques especiales y la dificultad aumenta durante el encuentro.

Los huevos registrados pueden despertarse sin OP en supervivencia o aventura con permiso de eclosión. La brújula deja de seguirlos en cuanto se activan; puede pasar a otro huevo disponible.

## Encuentros y recompensas

Los minijefes usan animales y bestias. Los bosses animales invocan súbditos de su especie con persecución propia, ataques de contacto y protección contra morir de un solo golpe directo. Sus drops son tributos, fragmentos y cristales comunes. Las categorías superiores añaden equipo, núcleos, esencias y materiales según sus tablas. El equipo que lleva el boss no es el equipo obtenido como recompensa.

Al morir definitivamente, el boss se eleva y cae con un impacto anunciado. Los rayos son visuales y no generan fuego. Después, cada unidad de botín desciende en una espiral suavizada durante unos ocho segundos; todas bajan simultáneamente a posiciones próximas. Las esencias tienen un efecto de aterrizaje propio. El impacto hace cuatro a cuatro corazones y medio incluso con absorción de manzana encantada y añade retroceso, respetando las cancelaciones y bloqueos descritos en el registro de cambios.

## Administración y desarrollo

- [Comandos, permisos y apariciones programadas](docs/ADMINISTRACION.md)
- [Cambios y compatibilidad de esta versión](docs/CAMBIOS-1.13.3.md)
- [Catálogo completo de IDs](docs/BOSSES-IDS-1.13.3.csv)
- [Organización del código](docs/ARQUITECTURA.md)
- [Notas de versiones anteriores y reconstrucción](docs/HISTORIAL-LEGADO.md)

Para compilar y validar:

```text
mvn verify
```

El JAR compilado queda en target. Las pruebas verifican catálogo, progresión, daño, recetas, permisos y reglas de animación. **No se ha ejecutado esta versión en un servidor Paper ni verificado visualmente con clientes reales.** Los 92 encuentros nuevos reutilizan las familias de combate existentes y tienen sus propios perfiles de ataque; deben ajustarse con pruebas de juego.
