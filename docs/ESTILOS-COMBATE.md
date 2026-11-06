# Estilos de combate en CazaJefes 1.38.1

Añade el objeto `tactics` dentro de `ai` en el JSON de cualquier jefe. Reinicia el servidor para cargar los cambios.

| Estilo | Distancia preferida | Comportamiento |
| --- | --- | --- |
| BRAWLER | 1.3 a 2.6 bloques | Persigue al objetivo para luchar de cerca. |
| SKIRMISHER | 3.5 a 7 bloques | Flanquea y puede moverse durante el aviso de ataque. |
| RANGED | 7 a 13 bloques | Mantiene espacio para disparar y retrocede si lo alcanzan. |
| ARTILLERY | 13 a 24 bloques | Busca distancia para usar sus habilidades de largo alcance. |
| AQUATIC | 2.2 a 5 bloques | Rodea al objetivo y puede moverse durante el aviso. |
| AUTO | Depende de la especie | Es el valor que se aplica si falta la configuración. |

Ejemplo del objeto `tactics` para un saqueador:

```json
{
  "style": "RANGED",
  "preferredMin": 7,
  "preferredMax": 13,
  "orbitSpeed": 0.65,
  "mobileWindup": false
}
```

La distancia preferida controla el movimiento. Los ataques conservan sus propios rangos, probabilidades y recargas. `orbitSpeed` es una fracción de la velocidad del jefe, entre 0 y 1. Si el objetivo queda oculto, el jefe vuelve a acercarse y mantiene las reglas de colisión, cuevas y arena. La detección sigue en `ai.detectionRange` y la permanencia en agua en `ai.aquatic`.
