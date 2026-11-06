# CazaJefes 1.35.1 · Auras individuales y daño de jefes

- Los 168 jefes tienen una variación de forma y color derivada de su identidad y de su familia. Despertar y llegar a etapa III usan su propia paleta.
- Sapo de Fuego tiene una caldera de brasas. Magmoryx lleva dos anillos de basalto y ceniza. Pyroth tiene cuatro chorros volcánicos. Calderyx gira con una doble espiral de lava.
- Las auras conservan el cupo por jugador y se adaptan al tamaño de cada jefe.
- Los cambios de fase permiten recibir daño. Los cubos conservan su protección por categoría y usan comprobación de visibilidad en el punto al que apunta el jugador, además de los puntos habituales de su cuerpo.
- La mitigación por armadura y Protección deja pasar una fracción mínima del daño válido, hasta 1 HP de mínimo. Los bloqueos, la absorción y la inmunidad breve de Paper siguen aplicándose.
- El diagnóstico deja de atribuir los golpes de daño cero a falta de Fuerza. Los mensajes distinguen absorción de mitigación.

Para probar, invoca `/ri boss sapo_de_fuego` en terreno abierto. Compara golpes normales y con Fuerza 100, dejando aproximadamente un segundo entre golpes. Si falla, captura la línea de consola de `/ri diagnostico` y el aviso de daño.
