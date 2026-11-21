# 1.45.0 · Resonancia visual

EffectLib opcional ejecuta Sphere en ataques especiales, con partículas e iteraciones configurables y sin cooldown adicional por defecto. Se limpian los gestores propios al apagar. Configuración visual-providers.effectlib: enabled, particles (24), iterations (8), cooldown-ticks (0).

ExcellentCrates opcional conecta directamente sus nueve implementaciones mediante su API CrateEffect y NightCore UniParticle, sin copiar código. Dibuja un efecto por boss siguiendo su posición, visible para los jugadores seleccionados. Ante API incompatible vuelve a las formas propias. visual-providers.excellentcrates.enabled controla este añadido.

Las auras propias respiran mediante escalado sinusoidal. El presupuesto previo de partículas permanece para el motor propio. Los motores externos pueden emitir partículas fuera de ese presupuesto y deben ajustarse según el servidor. No se promete impacto nulo en TPS.

Verificación: compilación y pruebas unitarias. Los puentes externos requieren probar las versiones instaladas en el servidor final; no se ha realizado validación visual con esos plugins instalados.
