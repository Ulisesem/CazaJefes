# Auras y partículas de jefes · 1.37.1

Paper 1.21.11. El efecto ambiental por defecto combina un anillo con una espiral ajustada al cuerpo del jefe despierto. Usa la paleta de su criatura. La carga del ataque dibuja un anillo, la salida una columna y el impacto un pulso expansivo. Son efectos decorativos.

Edita `plugins/CazaJefes/bosses/<categoría>/<id>.json` y reinicia. Añade este bloque dentro de `combat` y conserva los otros campos que ya tengas:

```json
"particles": {
  "aura": {
    "shape": "CRATE",
    "color": "#F86E36",
    "accent": "#FFE08A",
    "speed": 1.3,
    "points": 24
  },
  "attacks": {
    "default": {
      "charge": { "shape": "RING" },
      "release": { "shape": "BEAM", "height": 2.5 },
      "impact": { "shape": "PULSE" }
    },
    "meteoro": {
      "impact": { "shape": "CLOUD", "particle": "ELECTRIC_SPARK", "radius": 4, "points": 20 }
    }
  }
}
```

El nombre dentro de `attacks` coincide con el `id` en `ai.attacks`. Las etapas omitidas usan `default` o el efecto incluido. Formas disponibles: `CRATE`, `RING`, `SPIRAL`, `PULSE`, `BEAM`, `CLOUD` y `OFF`. En el aura, `AUTO` recupera la geometría por criatura de 1.37.0. Sin `particle`, se usa polvo de dos colores. También puedes indicar partículas sin datos adicionales, como `END_ROD`, `FLAME` o `ELECTRIC_SPARK`.

El radio visual es independiente del daño. El aura conserva la distancia, la calidad personal y el cupo de envíos de CazaJefes. Los avisos conservan su propio cupo. Los pulsos de ataque usan como máximo tres fotogramas. No se crean entidades para dibujar estos efectos.

Diseño propio basado en los [patrones de cajas de SkiesCrates](https://docs.skiesdev.com/skiescrates/particles/particle_effects/) y la idea de [nubes alrededor de NPCs de Citizens](https://wiki.citizensnpcs.co/Commands).

En 1.37.2 el aura sigue la posición actual del jefe durante todo el encuentro. A menos de 16 bloques se actualiza cada 2 ticks en calidad alta, cada 4 en media y cada 6 en baja. Si el cupo es bajo se dibujan menos puntos. Durante la carga se reduce la densidad ambiental y se mantienen los avisos. Para comprobar que esté activada, usa `/ri particulas alta`, revisa `particles.auras.enabled` en `config.yml` y el ajuste `combat.aura` del jefe. `OFF` sigue disponible para quien prefiera ocultarla.

Desde 1.37.3, cada jefe recibe una forma de caja por defecto según su ID. La elección se conserva al reiniciar y es la misma para todos los jugadores. Formas: HELIX, SPIRAL, SPHERE, HEART, PULSAR, BEACON, TORNADO, VORTEX y SIMPLE. Configura combat.particles.aura.shape en el JSON del jefe para escoger una forma específica o RANDOM_CRATE para usar la asignación por ID.
