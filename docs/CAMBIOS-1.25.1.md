# CazaJefes 1.25.1

Incluye la presentación y las cinco rares añadidas en 1.25.0, además de los siguientes cambios solicitados después:

- Descarga de Tormenta muestra únicamente su función; se retira la frase «Sustituye Ojo de Tormenta» de su descripción visible.
- Carga Inestable también se consulta y fabrica desde Ballestas. Se usa una sola receta registrada y la misma esencia compatible con arco y ballesta, sin duplicar el objeto. Conserva nivel IV y daño directo.
- Nueva Detonación de la Grieta (`detonacion_grieta`), exclusiva de ballesta, nivel máximo III. Recupera la carga por radio: dispara a un bloque, carga visible con sonidos durante 2 s y explosión cosmética sin romper bloques ni incendiar. Radio 3/4/5 bloques y daño base 6/9/12 HP (3/4,5/6 corazones), antes de armadura/absorción/otros plugins. Recarga 14 s. Respeta daño cancelado, ticks de invulnerabilidad, línea de visión, equipos del marcador y mundos sin PvP; máximo 8 objetivos por explosión. No se garantiza ese daño final a través de protecciones.
- Quedan siete recetas de arcos y nueve de ballestas: las siete anteriores más la visualización compartida de Carga y la nueva Detonación, sin retirar otras rares.
- Relevo permite terminar sobre el aire y desniveles, y salvar una subida libre de hasta 1,25 bloques. No atraviesa sólidos.
- Todas las listas de rares conservan orden alfabético, filas centradas, cabeceras y guía de infusión. Las nueve ballestas se muestran en dos filas.

`/ri book detonacion_grieta 3` entrega un libro. `/ri essence detonacion_grieta 3` entrega la esencia de magma.

Validación: Maven verify, pruebas de progresión radial y presencia de Carga en ambas secciones. Sin ejecución del servidor ni revisión visual en Minecraft.
