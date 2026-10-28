# Infusiones, subcategorías y Resonancia Primigenia

## Organización de /rares

Cámara de infusiones y Rares PvP abren un selector por equipo: armaduras; espadas y hachas; arcos, ballestas y tridentes; picos y herramientas. Solo aparecen grupos que tienen recetas. La Cámara añade el apartado Resonancia Primigenia para el desbloqueo. Se conservan paginación, ficha de ingredientes y enlaces entre materiales de duelo y sus piezas.

La compatibilidad no es solo un filtro de interfaz: se comprueba al preparar la infusión en la mesa. Hardy permanece exclusivo de pecheras; Flame, de cascos; Treefeller, de hachas. La descripción de cada esencia identifica su equipo. Las piezas PvP tienen equipo fijo; no se convierten en infusiones intercambiables.

Las propiedades de armadura son Guardian, Paso del Viento, Hardy, Durability, Regeneration, NoFallDamage, DoubleJump, Fly, Saturación y Flame. Las de herramientas son Fertilize, MeltObsidian, Smelt, Spore, VeinMiner y Magneto. Treefeller se muestra junto a hachas. CallLighting, Slow y Blinding se asignan al grupo a distancia. El resto de propiedades ofensivas se aplican a espadas y hachas.

## Equipo de boss sellado

Las colecciones de equipo de boss y los nuevos drops de equipo llevan sus infusiones cerradas. No se pueden añadir otras rares hasta despertar esa pieza. La prohibición incluye las piezas existentes del catálogo, pero no borra las propiedades que ya tenían: siguen funcionando. El equipo PvP fabricado y el equipo vanilla no requieren este sello.

El desbloqueo es por pieza, no por jugador. Se guarda en sus datos y no depende del nombre o de una línea decorativa. Los niveles máximos de las rares no cambian; tampoco se inventa un nuevo máximo de propiedades distintas.

## Resonancia Primigenia

Una rare de desbloqueo de nivel I, consumida en una sola pieza:

```text
KPK
NSN
FTF
```

- K: cristal Épico, dos unidades.
- P: Rare Essence Primordial, una.
- N: núcleo de jefe de cualquiera de las alternativas válidas, dos.
- S: estrella del Nether, una.
- F: fragmento de tributo, dos.
- T: tributo del Cazador, uno.

Consultar y fabricar en `/rares` → Cámara de infusiones → Resonancia Primigenia.

Después, en la mesa de crafteo: **una pieza sellada + una Resonancia Primigenia**. Produce esa misma pieza despierta, conservando encantamientos, desgaste, nombre y propiedades anteriores. No añade un ataque ni regala una rare ofensiva. No permite gastar varias a la vez ni volver a despertar una pieza ya abierta.

Desde 1.18.0 el sello solo se obtiene por receta. Se retira su caída para que los bosses no entreguen rares preparadas; las claves antiguas de probabilidad ya no activan ese drop.

## Reparaciones y mejoras

Yunque, afiladora, herrería y reparación en mesa conservan la condición de pieza de boss. No convierten una pieza sellada en equipo libre por perder su ID en una receta vanilla. Una base despierta conserva el desbloqueo; un componente secundario despierto no abre gratis una base cerrada.

## Cambios en drops

Ninguna arma o armadura nueva del botín incorpora rares personalizadas. Conservan sus encantamientos vanilla de categoría, como Filo o Protección. La tirada que antes producía un arma rare preinfundida entrega ahora Rare Essence base sin poder asignado, para aplicar después del desbloqueo. Los libros, materiales y demás recompensas conservan sus rutas.

No se eliminan rares de objetos que los jugadores ya poseen. Las armas especiales que se fabrican por recetas antiguas tampoco se presentan como nuevos drops: la restricción solicitada se aplica al botín de bosses y a nuevas infusiones del equipo de su colección.

## Huevos

Todos los huevos registrados comparten dos espirales negras/moradas y un aro de polvo oscuro con acentos violetas. Se retiran el portal y encantamiento de su aura anterior, también el destello blanco al colocarlos. Las partículas usan el presupuesto compartido y el alcance de 32 bloques; al agotarse se omiten cosméticos sin afectar la activación del huevo.

## Validación

177 pruebas automatizadas y compilación para Java 21/Paper 1.21.11. Incluyen incompatibilidades entre familias, piezas de armadura específicas, reglas de desbloqueo y probabilidades por categoría. No se ha arrancado Paper ni comprobado visualmente el aura o las operaciones de inventario con clientes; hace falta verificar en juego crafteo, shift-clic, yunque, afiladora y herrería con los otros plugins instalados.
