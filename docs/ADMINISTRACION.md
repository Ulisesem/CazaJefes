# Administración

Permiso completo: cazajefes.admin. También se aceptan cazajefes.admin.* y los permisos legacy rareitemhunter.admin.*. Los permisos granulares usan los sufijos spawnpoint, egg, boss, compass, essence, reload, equipment y diagnostico.

## Apariciones automáticas

Situarse en el lugar y ejecutar:

```text
/ri spawnpoint arena 0
/ri spawnpoint timer arena random 5m
```

El radio 0 conserva altura y posición. Un radio entre 1 y 128 elige una superficie aleatoria dentro de ese alcance. Sustituir random por un ID actual para fijar el boss. Se aceptan intervalos de 10 segundos a 24 horas, con sufijos s, m y h. La comprobación se realiza cada cinco segundos.

```text
/ri spawnpoint list
/ri spawnpoint off arena
/ri spawnpoint delete arena
```

La primera aparición espera el intervalo; las siguientes cuentan desde la muerte definitiva del boss. Solo se mantiene uno activo por punto. Un boss descargado no se duplica. Si otro plugin lo elimina sin evento de muerte, el punto puede quedar esperando; revisar su referencia auto.active con el servidor apagado. Desactivar o borrar el punto no elimina un boss que ya exista.

## Pruebas y objetos

```text
/ri egg <id> [here|punto]
/ri boss <id> [here|punto]
/ri get <compass|essence|fragment|tribute> [jugador]
/ri rare <id> [nivel] [jugador]
/ri book <id> <nivel> [jugador]
/riequipo <epico|legendario|mitico|abismal> [armadura|espada|hacha|todo] [jugador]
/ri diagnostico
/ri reload
```

Desde consola se debe indicar un punto guardado al invocar y un jugador al entregar objetos. Usar Tab para ver IDs actuales. La recarga actualiza configuración y recetas; sustituir el JAR requiere reinicio.

## Huevos y brújula

Los jugadores de supervivencia o aventura pueden despertar huevos con rareitemhunter.hunter.hatch, permitido por defecto. Una asignación explícita de cazajefes.hunter.hatch tiene prioridad. Se puede pulsar el huevo o su base. El modo creativo mantiene las reglas de prueba del plugin.

La brújula solo sigue huevos dormidos. Un boss despierto deja de ser un objetivo. Si no hay otros huevos, se retira la información de rastreo.
