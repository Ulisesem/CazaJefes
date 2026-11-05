# CazaJefes 1.32.0 · Guardia de Obsidiana

| Ataque | Alcance y mecánica |
|---|---|
| Lluvia de Obsidiana | Cuadrado de 20 × 20 alrededor del jefe. Dieciséis casillas de 5 × 5, con cuatro casillas amenazadas por oleada. Caen meteoritos compuestos de obsidiana y obsidiana llorosa. El daño se aplica en las casillas de la oleada con las comprobaciones de cobertura habituales. |
| Corte del Presidio | Radio base de 11 bloques. El ejecutor alza el hacha, avisa que vienen tres cortes y muestra el abanico peligroso. Cada corte gira hacia un lado distinto y dibuja una onda naranja y roja. |
| Fractura del Vacío | El rayo alcanza hasta 20 bloques. Sigue al jugador mientras apunta, fija la trayectoria y se detiene al encontrar cobertura. |
| Llamada del Presidio | Nova de radio base de 12 bloques y 16 HP de daño base. Invoca la horda desde los portales habituales. |
| Ataques comunes y definitivos de JSON | Radios base de 9, 11 y 12 bloques según etapa para la rotación común. Los definitivos que usan el perfil de `bosses.json` ganan 2 bloques de radio base. Los perfiles YAML personalizados conservan su configuración. |

El archivo anterior `combate.yml` sigue funcionando. Mientras tenga esquema 1 o carezca de versión, los valores originales de los cuatro ejemplos se amplían al cargarlos. Para usar valores exactos de tu propio archivo, establece `schema-version: 2` y ajusta los campos. La Lluvia de Obsidiana usa el patrón `OBSIDIAN` con área fija de 20 × 20.

## Modelos

El hacha está compuesta por cuatro displays de Paper que se interpolan juntos. El meteorito usa dos piezas. Esos modelos se ven directamente con el JAR y comparten el límite de displays por servidor.

[Gorath Guardia de Obsidiana](models/gorath_guardia_de_obsidiana.bbmodel) es un prototipo editable de cuerpo completo para Blockbench, con textura incluida y cinco animaciones. Para ensayarlo, impórtalo en Model Engine R4, distribuye el paquete de recursos generado y añade a `plugins/CazaJefes/cinematics.yml`:

```yaml
models:
  gorath_quebrantador_de_mundos:
    id: gorath_guardia_de_obsidiana
    prepare: prepare
    impact: attack
    recovery: recover
```

El modelo de cuerpo completo se validó como archivo, queda por verificar su importación y reproducción en Model Engine. [Importación de modelos en Model Engine](https://wiki.mythiccraft.io/modelengine/Modeling/Importing-a-Model)

## APIs para seguir ampliando el combate

- [Display Entities de Paper](https://docs.papermc.io/paper/dev/display-entities/) para proyectiles interpolados, piezas animadas y marcas de suelo.
- [Model Engine R4](https://wiki.mythiccraft.io/modelengine/API) para dar animaciones de cuerpo completo a jefes concretos.
- [Adventure Resource Packs](https://docs.papermc.io/adventure/resource-pack/) para distribuir audio e iconos propios, con un efecto base cuando el cliente no cargue el paquete.
- [PacketEvents](https://github.com/retrooper/packetevents) para efectos privados que necesiten paquetes, como indicadores de fijación que ve únicamente el objetivo.
