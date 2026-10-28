# Forja de duelo · Rares PvP

Ocho reliquias funcionales y ocho materiales exclusivos. No necesitan pack de recursos: usan objetos vanilla con nombre, descripción, brillo e ID propio. Los materiales se refinan con drops existentes, no aparecen como ocho drops nuevos independientes.

## Colección

| Rare | Pieza | Material exclusivo | Poder | Recarga |
| --- | --- | --- | --- | --- |
| Juramento Carmesí | Espada de netherita | Sangre Cristalizada | Tercer golpe cargado al mismo jugador en 6 s: Wither I durante 2 s | 12 s |
| Desgarro del Bastión | Hacha de netherita | Fragmento del Bastión | Golpe cargado: +20 % de daño y Debilidad I durante 2 s | 10 s |
| Arco del Ocaso | Arco | Lente del Ocaso | Flecha suficientemente cargada desde al menos 12 bloques: ceguera 1 s y brillo 3 s | 14 s |
| Voz de la Tormenta | Ballesta | Resonador de Tormenta | Impacto: +15 % de daño y Lentitud I durante 2 s | 12 s |
| Vigía de Ónix | Casco de netherita | Ojo de Ónix | Impacto de proyectil: reduce 20 % el daño y revela al tirador 2 s | 12 s |
| Coraza de Quitina | Pechera de netherita | Quitina Resonante | Golpe cuerpo a cuerpo: reduce 20 % ese daño y concede Resistencia I durante 2 s | 14 s |
| Grebas Rompecadenas | Pantalones de netherita | Fibra Rompecadenas | Recibir un golpe con Lentitud: elimina Lentitud y da Velocidad I 2 s | 14 s |
| Paso Espectral | Botas de netherita | Pluma Espectral | Recibir un golpe con 40 % de vida o menos: Velocidad II durante 3 s | 18 s |

Estas son propiedades de nivel I fijo. No existe todavía una mejora de nivel para estos ocho poderes. Las infusiones tradicionales que el sistema permite en equipo compatible siguen siendo independientes.

## Recetas y navegación

`/rares` → **Materiales de duelo** → escoger material → ver ingredientes → Fabricar una unidad.

`/rares` → **Rares PvP · Forja de duelo** → escoger pieza → consultar su material exclusivo con el botón vinculado → Fabricar una unidad.

Ambos apartados tienen enlaces de ida y vuelta entre pieza y material. También se registran como recetas reales de mesa. El permiso de forja existente permanece en vigor.

### Refinado del material

```text
 R 
FCF
 E 
```

R = reactivo propio; F = fragmento de tributo (dos); C = un núcleo de jefe actual, cualquiera de las alternativas aceptadas; E = Rare Essence base. Produce una unidad del material exclusivo.

| Material | Representación | Reactivo R |
| --- | --- | --- |
| Sangre Cristalizada | Redstone encantada | Ojo de araña fermentado |
| Fragmento del Bastión | Cristales de prismarina encantados | Bloque de hierro |
| Lente del Ocaso | Fragmento de eco encantado | Ojo de ender |
| Resonador de Tormenta | Fragmento de amatista encantado | Pararrayos |
| Ojo de Ónix | Ojo de ender encantado | Obsidiana |
| Quitina Resonante | Fragmento de prismarina encantado | Ojo de araña |
| Fibra Rompecadenas | Membrana de fantasma encantada | Telaraña |
| Pluma Espectral | Pluma encantada | Pata de conejo |

### Reliquia final

```text
 M 
EKE
 B 
```

M = material exclusivo; E = Rare Essence base (dos); K = cristal Épico; B = pieza base vanilla del tipo exacto del resultado.

**La receta fabrica una pieza nueva y consume la base. No transfiere sus encantamientos, nombre, desgaste o infusiones.** Usar una base sin mejoras. Los objetos personalizados no sustituyen materiales vanilla; las recetas verifican los materiales originales por metadata exacta.

## Encantamientos y límites

Armas cuerpo a cuerpo: Filo V. Arco: Poder V. Ballesta: Carga rápida III y Multidisparo I. Armaduras: Protección VII. Todas: Irrompibilidad V y Reparación I. El aspecto naranja y las descripciones grises identifican la colección; no son nuevas texturas ni modelos 3D.

Las habilidades solo funcionan en PvP válido entre supervivencia/aventura, con PvP habilitado en el mundo. No ignoran regiones, cancelaciones de daño, inmunidad entre golpes ni reglas de equipos. Golpes totalmente bloqueados no activan habilidades ni gastan recarga. Solo se activa una defensa de armadura por impacto; la revisión va de botas a casco, dando prioridad a la salida de emergencia cuando se cumplen varias condiciones. No se reanima al jugador con estas piezas.

Los efectos de estas rares duran 1–3 segundos, según la tabla. Los 30 segundos de efectos de bosses no se trasladan al PvP. No hay efectos de inmovilidad prolongada ni daño directo que evite armadura. Un hacha contra una defensa de Quitina combina los modificadores multiplicando: 1,20 × 0,80 = 0,96 antes de otros plugins.

Las recargas pertenecen al jugador y al poder: cambiar de pieza o reconectarse no las reinicia. Se limpian las entradas expiradas tras salir; un reinicio del servidor no conserva recargas de combate.

Sin tareas periódicas nuevas: efectos y cosméticos se procesan al disparar o aceptar un impacto. Las partículas usan el presupuesto compartido existente y el sonido solo lo recibe quien activa la rare.

Las 16 recetas aceptan overrides y desactivación en recipes.yml con claves `pvp_material_<id>` y `pvp_gear_<id>`. IDs: hemorragia, fractura, ocaso, resonancia, vigia, quitina, cadenas, espectral. `/ri reload` vuelve a registrarlas.

## Validación

Maven verify y 173 pruebas automatizadas, incluidos catálogo, disparadores, recargas, condiciones del combo y rutas de categorías. JAR revisado para Java 21. No se ha ejecutado un duelo real ni revisado visualmente la interfaz en Paper; el balance competitivo necesita pruebas con jugadores y los demás plugins instalados.
