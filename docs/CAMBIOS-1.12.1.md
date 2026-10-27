# Cambios de 1.12.1

Se eliminan todos los alias de IDs de bosses y el campo migrationId de Definition. El catálogo, los comandos, ataques y drops solo aceptan los IDs actuales. Se retira la conversión de claves de bosses, selecciones de spawnpoints y huevos guardados. Los núcleos y corazones tampoco tienen una ruta de conversión por ID anterior.

Los datos antiguos no se convierten automáticamente. Hay que actualizar los puntos y volver a crear huevos con IDs actuales cuando proceda. No se borran automáticamente los datos guardados por el usuario.

Se conservan las migraciones de recetas, sellos y otras monedas independientes de los IDs de bosses. El catálogo actual solo documenta IDs actuales. Las notas y catálogos históricos describen versiones anteriores y no se cargan como alias.
