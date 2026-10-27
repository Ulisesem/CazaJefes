# Cambios de 1.12.0

- Bestiario ampliado de 76 a 168 bosses: 28 minijefes, 28 raros, 28 épicos, 28 legendarios, 28 míticos y 28 abismales.
- 92 nuevos perfiles definitivos dentro de las familias de ataques existentes; minijefes basados en animales.
- Ferrun, Colmillo de Hierro sustituye al minijefe humanoide Cazador Oxidado. Su nuevo ID es ferrun_colmillo_de_hierro.
- IDs actuales en IA, habilidades, núcleos, corazones y entidades. Los alias solo se conservan para compatibilidad y migración; ya no se muestran en las fichas.
- Nova blanca con expansión completa alrededor del lanzador y avance frontal suave.
- Activación de huevos propios sin OP, incluso cuando el uso vanilla del bloque viene cancelado. Se conservan restricciones de modo y permiso. Los fallos de creación no consumen el huevo.
- Brújula limitada a huevos dormidos. Al quedarse sin objetivos restaura el destino y la tabla anteriores; si hay otros huevos puede cambiar al más cercano.
- Fichas del bestiario con notas de combate, defensas y el definitivo de cada encuentro.

## Compatibilidad

La migración convierte las claves antiguas de bosses, la selección de spawnpoints y los huevos guardados. Los objetos antiguos se actualizan al pasar por la migración de inventario existente. Un boss ya vivo conserva su tipo de criatura hasta la siguiente aparición.

La eclosión personalizada solo reconoce huevos registrados de CazaJefes. No utiliza la cancelación del clic como control de zona para esos huevos; utiliza el permiso de eclosión. Quien necesite prohibirla debe configurar cazajefes.hunter.hatch o el permiso legacy compatible.
