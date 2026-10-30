# CazaJefes 1.23.4

- `/ri book <rare> <nivel> [jugador]` vuelve a entregar exclusivamente libros para todas las rares, incluidas las doce de proyectiles. Conservan sus poderes, límites de nivel, descripción y compatibilidad. El marcador del libro se respeta al actualizar la apariencia, de modo que no se transforma en esencia al abrir un inventario.
- Las esencias fabricadas en recetas conservan la bola de magma solicitada; no se confunden con la entrega administrativa de libros. Los objetos convertidos a esencias por 1.23.3 no se convierten todos indiscriminadamente en libros: se pueden volver a entregar con el comando.
- El segundo icono de arco era la sección de efectos compartidos por arcos, ballestas y tridentes. Ahora usa una flecha espectral y se llama Proyectiles · compartidas. La sección Arcos sigue usando el único icono de arco y contiene sus rares exclusivas. No se eliminan recetas.

Validación: Maven verify y pruebas de regresión. Sin ejecución del servidor Paper.
