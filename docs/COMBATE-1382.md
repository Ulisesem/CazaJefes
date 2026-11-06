# Combate y ruptura de terreno en 1.38.2

- Los golpes de cuerpo a cuerpo miden la separación entre los cuerpos. El jefe mantiene el alcance durante el aviso y comprueba otra vez el alcance y la visibilidad al golpear.
- Los ataques a distancia mantienen su rango, recarga y aviso propios. El jefe vuelve a buscar al jugador cuando pierde la línea de visión.
- **Pulso de Fractura** es una onda de daño cercana con aviso. **Falla del Terreno** golpea a un objetivo elevado visible y lo impulsa hacia abajo y hacia el jefe. Los ataques se añaden a los perfiles existentes por defecto. Para personalizarlos, añade un ataque de tipo `SHOCKWAVE` o `FAULT_LINE` en `ai.attacks`. Puedes desactivar el valor automático con `ai.disableDefaultShockwave` o `ai.disableDefaultFaultLine`.
- La ruptura temporal busca el bloque que bloquea la persecución horizontal o vertical. Ante una plataforma elevada y cercana, también puede retirar el bloque que sostiene al jugador. Solo actúa sobre los materiales de terreno y construcción permitidos, sin drops, con los límites de arena y `mobGriefing`. Al terminar el encuentro restaura los huecos que estén libres.
