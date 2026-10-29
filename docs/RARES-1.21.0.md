# Colección de rares · 1.21.0

El archivo recibido contiene 33 recetas detalladas, no 38. Cada receta base de esta colección consume una Rare Essence auténtica en el centro y ocho ingredientes vanilla. Fabricar una unidad de cada receta necesita 33 esencias; subir niveles requiere unidades adicionales. Los tiers del archivo no se usan como un sistema nuevo de raridad.

## Dónde están

`/rares` → **Cámara de infusiones** → Armaduras / Espadas y hachas / Arcos / Picos y herramientas. Los ocho poderes activables están en **Biblioteca de grimorios**.

| ID | Nombre visible | Apartado | Máximo | Costo por activación | Recarga explícita |
| --- | --- | --- | --- | --- | --- |
| poison | Colmillo de la Hidra | Espadas y hachas | 4 | 4 de saturación | Sin recarga adicional |
| backstab | Filo de la Traición | Espadas y hachas | 1 | 2 de saturación | Sin recarga adicional |
| blinding | Velo del Eclipse | Arcos, ballestas y tridentes | 2 | 4 de saturación | Sin recarga adicional |
| calllighting | Veredicto de la Tormenta | Arcos, ballestas y tridentes | 5 | 4 de saturación | Sin recarga adicional |
| confuse | Laberinto Mental | Espadas y hachas | 1 | 4 de saturación | Sin recarga adicional |
| disarm | Mano del Despojo | Espadas y hachas | 1 | 4 de saturación | Sin recarga adicional |
| slow | Grilletes de Escarcha | Arcos, ballestas y tridentes | 1 | 2 de saturación | Sin recarga adicional |
| vampiricregeneration | Pacto Carmesí | Espadas y hachas | 1 | 4 de saturación | Sin recarga adicional |
| weaken | Marca del Ocaso | Espadas y hachas | 1 | 4 de saturación | Sin recarga adicional |
| fertilize | Bendición del Brote | Picos y herramientas | 2 | 1 de saturación | Sin recarga adicional |
| meltobsidian | Forja de Obsidiana | Picos y herramientas | 1 | 1 de saturación | Sin recarga adicional |
| smelt | Toque de la Forja | Picos y herramientas | 1 | 1 de saturación | Sin recarga adicional |
| spore | Musgo Primigenio | Picos y herramientas | 1 | 1 de saturación | Sin recarga adicional |
| burst | Pulso del Destierro | Grimorios | 5 | 2 de saturación | 20 s |
| craftitem | Forja de Bolsillo | Grimorios | 1 | 1 de saturación | 5 s |
| fireresistance | Manto de la Caldera | Grimorios | 5 | 6 de saturación | 30 s |
| invisibility | Paso entre Sombras | Grimorios | 8 | 8 de saturación | 60 s |
| veinminer | Pulso del Filón | Picos y herramientas | 3 | 1 de saturación | Sin recarga adicional |
| treefeller | Hacha del Bosque Ancestral | Espadas y hachas | 3 | 1 de saturación | Sin recarga adicional |
| durability | Forja Viviente | Armaduras | 5 | Gratis | Sin recarga adicional |
| regeneration | Pulso de Aurora | Armaduras | 3 | Gratis | Sin recarga adicional |
| nofalldamage | Plumas del Firmamento | Armaduras | 4 | Gratis | Sin recarga adicional |
| execute | Sentencia del Umbral | Espadas y hachas | 4 | 5 de saturación | Sin recarga adicional |
| bleed | Herida del Crepúsculo | Espadas y hachas | 4 | 3 de saturación | Sin recarga adicional |
| haste | Fervor del Artífice | Grimorios | 3 | 4 de saturación | 10 s |
| magicbag | Bóveda del Vacío | Grimorios | 1 | 1 de saturación | 5 s |
| greaterburst | Nova del Destierro | Grimorios | 3 | 4 de saturación | 30 s |
| doublejump | Zancada Celestial | Armaduras | 3 | Gratis | Sin recarga adicional |
| repairitem | Restauración del Artífice | Grimorios | 5 | 20 de saturación | 3 s |
| fly | Alas del Firmamento | Armaduras | 5 | Gratis | Sin recarga adicional |
| hardy | Corazón Indómito | Armaduras | 3 | Gratis | Sin recarga adicional |
| clarity | Lucidez del Alba | Armaduras | 3 | Gratis | Sin recarga adicional |
| hungersteal | Hambre del Depredador | Armaduras | 3 | Gratis | 10 s |

## Reglas de aplicación

- Las infusiones van solo en equipo compatible; Doble salto solo en botas, Fertilizar en azadas, Musgo/Fundición/Obsidiana/Vetas en picos y Leñador en hachas. El equipo de boss sigue necesitando Resonancia Primigenia.
- Combinar equipo con esencias del mismo poder suma niveles. Si excede el máximo, la mesa rechaza el resultado sin consumir ingredientes.
- Los grimorios se activan con clic derecho en el aire. Para subirlos, colocar dos o más grimorios del mismo poder en casillas separadas de una mesa. Sus niveles se suman hasta el máximo; tipos mezclados, libros comunes o sumas excesivas se rechazan. Invisibility puede llegar a VIII.
- Los nombres de esencias comienzan con Rare Essence y muestran el poder; los libros se titulan Grimorio y su poder. El nombre visible cambia, el ID técnico se conserva para reconocer las mejoras existentes.
- Fuerza, Baluarte, Sello del Alba, aura venenosa y las anteriores reliquias de duelo se mantienen aparte; no se eliminaron recetas ajenas a la lista.
- Esporas de herramientas es **Musgo Primigenio** (`spore`). El poder de aura de armadura se mantiene como **Jardín de la Ruina** (`aura_esporas`). Al refrescar una armadura existente que llevaba Spore, se conserva su aura con el nuevo identificador.

## Decisiones donde el archivo no precisa cifras

- Veneno se intenta en cada golpe aceptado que pueda pagar su costo, con Veneno I–IV durante cuatro segundos. Sangrado utiliza Wither I por dos segundos por nivel.
- Puñalada añade 50% de daño por la espalda. Ejecutar duplica el daño bajo 10/15/20/25% de vida según nivel.
- Pacto Carmesí conserva la probabilidad indicada de 20%; restaura 25% del daño aceptado, limitado por la vida previa de la víctima y un máximo de cuatro HP. Respeta cancelaciones de curación.
- El rayo tiene la probabilidad indicada de 10%, un efecto visual sin fuego y daño adicional solicitado de dos HP por nivel. Respeta la reducción de daño e inmunidad entre golpes; no se garantiza daño de salud fijo.
- Ceguera de rare dura tres segundos; Confusión cinco. Claridad reduce solo la duración de ceguera/náuseas aplicadas por las rares de CazaJefes en 25/50/75%; no modifica pociones desconocidas ni los temporizadores propios de bosses.
- El archivo no describe el efecto de HungerSteal. **Hambre del Depredador** roba hasta un punto de comida por nivel a un jugador enemigo después de un golpe cuerpo a cuerpo aceptado; solo transfiere la cantidad que le falta al portador. Diez segundos de recarga, sin coste de uso. Un punto de comida equivale a medio icono de hambre.
- Desarmar tiene 25% de probabilidad contra jugadores con un arma en la mano y un hueco libre: guarda el arma en su inventario, sin borrarla ni soltarla al suelo. No despoja de equipo de exhibición a bosses.
- Las vetas y troncos están limitados a 16/32/48 bloques, contando el inicial, del mismo mineral o familia de madera. Se usan roturas reales del jugador para respetar cancelaciones y evitar minería sin límite.
- Forja Viviente repara tres puntos por nivel en cada pieza equipada cada cinco segundos. Pulso de Aurora cura un HP por nivel cada diez segundos, sin poción de regeneración permanente.
- Alas permite vuelo gratuito; los cinco niveles no inventan cinco velocidades de vuelo. Invisibilidad dura un minuto en cualquiera de sus ocho niveles, tal como indica el archivo.
- Manto de la Caldera dura 45 segundos por nivel; Fervor otorga Prisa minera I–III durante 30 segundos por nivel. Restauración repara 20% de la durabilidad máxima del objeto del primer slot por nivel, hasta reparar por completo; no cobra si no hay objeto dañado.

## Recetas base

Las matrices siguientes se usan tanto en la mesa como en el recetario. Se conservan las anulaciones explícitas de administrador en `recipes.yml`, si existen. Esencia Rara significa Rare Essence básica auténtica del plugin; no magma renombrado. Los ingredientes vanilla no aceptan piezas custom de boss como sustituto. Usar herramientas/armas sin mejoras como ingredientes, pues se consumen. La fabricación por menú comprueba espacio y materiales antes de consumir.

### Colmillo de la Hidra · `poison`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Ojo de araña | Ojo de araña fermentado | Ojo de araña |
| Ojo de araña fermentado | Esencia Rara | Ojo de araña fermentado |
| Ojo de araña | Bola de slime | Ojo de araña |

### Filo de la Traición · `backstab`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Pedernal | Fragmento de eco | Pedernal |
| Espada de hierro | Esencia Rara | Espada de hierro |
| Piedra negra | Obsidiana | Piedra negra |

### Velo del Eclipse · `blinding`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Saco de tinta luminosa | Fragmento de amatista | Saco de tinta luminosa |
| Fragmento de amatista | Esencia Rara | Fragmento de amatista |
| Arena de almas | Polvo de piedra luminosa | Arena de almas |

### Veredicto de la Tormenta · `calllighting`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Pararrayos | Bloque de cobre | Pararrayos |
| Faro | Esencia Rara | Faro |
| Pararrayos | Bloque de cobre | Pararrayos |

### Laberinto Mental · `confuse`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Ojo de araña fermentado | Fragmento de amatista | Ojo de araña fermentado |
| Fragmento de amatista | Esencia Rara | Fragmento de amatista |
| Bola de slime | Saco de tinta luminosa | Bola de slime |

### Mano del Despojo · `disarm`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Gancho de cuerda | Cadena | Gancho de cuerda |
| Pistón pegajoso | Esencia Rara | Pistón pegajoso |
| Bloque de redstone | Lingote de hierro | Bloque de redstone |

### Grilletes de Escarcha · `slow`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Telaraña | Bola de slime | Telaraña |
| Bloque de slime | Esencia Rara | Bloque de slime |
| Telaraña | Hielo | Telaraña |

### Pacto Carmesí · `vampiricregeneration`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Rosa del wither | Lágrima de ghast | Rosa del wither |
| Lágrima de ghast | Esencia Rara | Lágrima de ghast |
| Carne podrida | Bloque de verruga del Nether | Carne podrida |

### Marca del Ocaso · `weaken`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Rosa del wither | Arena de almas | Rosa del wither |
| Tierra de almas | Esencia Rara | Tierra de almas |
| Rosa del wither | Bloque de carbón | Rosa del wither |

### Bendición del Brote · `fertilize`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Polvo de hueso | Semillas de trigo | Polvo de hueso |
| Bloque de musgo | Esencia Rara | Bloque de musgo |
| Polvo de hueso | Semillas de trigo | Polvo de hueso |

### Forja de Obsidiana · `meltobsidian`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Vara de blaze | Cubo de lava | Vara de blaze |
| Bloque de magma | Esencia Rara | Bloque de magma |
| Vara de blaze | Obsidiana | Vara de blaze |

### Toque de la Forja · `smelt`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Polvo de blaze | Alto horno | Polvo de blaze |
| Bloque de carbón | Esencia Rara | Bloque de carbón |
| Polvo de blaze | Horno | Polvo de blaze |

### Musgo Primigenio · `spore`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Bloque de musgo | Polvo de hueso | Bloque de musgo |
| Roca musgosa | Esencia Rara | Roca musgosa |
| Bloque de musgo | Polvo de hueso | Bloque de musgo |

### Pulso del Destierro · `burst`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Dinamita | Carga ígnea | Dinamita |
| Carga ígnea | Esencia Rara | Carga ígnea |
| Dinamita | Redstone | Dinamita |

### Forja de Bolsillo · `craftitem`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Polvo de piedra luminosa | Mesa de crafteo | Polvo de piedra luminosa |
| Tablones de roble | Esencia Rara | Tablones de roble |
| Libro | Tablones de roble | Libro |

### Manto de la Caldera · `fireresistance`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Crema de magma | Vara de blaze | Crema de magma |
| Obsidiana | Esencia Rara | Obsidiana |
| Crema de magma | Ladrillos del Nether | Crema de magma |

### Paso entre Sombras · `invisibility`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Ojo de araña fermentado | Fragmento de eco | Ojo de araña fermentado |
| Fragmento de amatista | Esencia Rara | Fragmento de amatista |
| Ojo de araña fermentado | Ojo de ender | Ojo de araña fermentado |

### Pulso del Filón · `veinminer`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Lingote de hierro | Dinamita | Lingote de hierro |
| Bloque de redstone | Esencia Rara | Bloque de redstone |
| Pico de hierro | Dinamita | Pico de hierro |

### Hacha del Bosque Ancestral · `treefeller`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Pedernal | Tronco de roble | Pedernal |
| Hacha de hierro | Esencia Rara | Hacha de hierro |
| Pedernal | Tronco de roble sin corteza | Pedernal |

### Forja Viviente · `durability`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Fragmento de netherita | Yunque | Fragmento de netherita |
| Bloque de hierro | Esencia Rara | Bloque de hierro |
| Fragmento de netherita | Diamante | Fragmento de netherita |

### Pulso de Aurora · `regeneration`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Lágrima de ghast | Manzana dorada | Lágrima de ghast |
| Rodaja de sandía reluciente | Esencia Rara | Rodaja de sandía reluciente |
| Lágrima de ghast | Zanahoria dorada | Lágrima de ghast |

### Plumas del Firmamento · `nofalldamage`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Pluma | Membrana de phantom | Pluma |
| Fardo de heno | Esencia Rara | Fardo de heno |
| Pluma | Bloque de slime | Pluma |

### Sentencia del Umbral · `execute`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Calavera de esqueleto wither | Estrella del Nether | Calavera de esqueleto wither |
| Fragmento de netherita | Esencia Rara | Fragmento de netherita |
| Calavera de esqueleto wither | Espada de diamante | Calavera de esqueleto wither |

### Herida del Crepúsculo · `bleed`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Rosa del wither | Bloque de redstone | Rosa del wither |
| Bloque de hueso | Esencia Rara | Bloque de hueso |
| Rosa del wither | Espada de hierro | Rosa del wither |

### Fervor del Artífice · `haste`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Azúcar | Pata de conejo | Azúcar |
| Bloque de redstone | Esencia Rara | Bloque de redstone |
| Azúcar | Zanahoria dorada | Azúcar |

### Bóveda del Vacío · `magicbag`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Caparazón de shulker | Cofre de ender | Caparazón de shulker |
| Ojo de ender | Esencia Rara | Ojo de ender |
| Caparazón de shulker | Cofre | Caparazón de shulker |

### Nova del Destierro · `greaterburst`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Caparazón de shulker | Dinamita | Caparazón de shulker |
| Bloque de redstone | Esencia Rara | Bloque de redstone |
| Caparazón de shulker | Dinamita | Caparazón de shulker |

### Zancada Celestial · `doublejump`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Membrana de phantom | Cohete | Membrana de phantom |
| Pata de conejo | Esencia Rara | Pata de conejo |
| Membrana de phantom | Bloque de slime | Membrana de phantom |

### Restauración del Artífice · `repairitem`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Fragmento de netherita | Yunque | Fragmento de netherita |
| Bloque de hierro | Esencia Rara | Bloque de hierro |
| Fragmento de netherita | Mesa de herrería | Fragmento de netherita |

### Alas del Firmamento · `fly`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Aliento de dragón | Huevo de dragón | Aliento de dragón |
| Estrella del Nether | Esencia Rara | Estrella del Nether |
| Membrana de phantom | Élitros | Membrana de phantom |

### Corazón Indómito · `hardy`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Manzana dorada | Caparazón de tortuga | Manzana dorada |
| Bloque de hierro | Esencia Rara | Bloque de hierro |
| Zanahoria dorada | Tótem de la inmortalidad | Zanahoria dorada |

### Lucidez del Alba · `clarity`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Lingote de hierro | Fragmento de amatista | Lingote de hierro |
| Fragmento de amatista | Esencia Rara | Fragmento de amatista |
| Lingote de hierro | Fragmento de amatista | Lingote de hierro |

### Hambre del Depredador · `hungersteal`

| Izquierda | Centro | Derecha |
| --- | --- | --- |
| Carne podrida | Zanahoria dorada | Carne podrida |
| Ojo de araña fermentado | Esencia Rara | Ojo de araña fermentado |
| Carne podrida | Filete cocido | Carne podrida |
