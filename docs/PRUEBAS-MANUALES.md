# Pruebas pendientes en Paper 1.21.11

Todos los casos siguientes están **pendientes**; no hay evidencia de servidor ejecutado en esta entrega.

| Caso | Resultado esperado |
|---|---|
| Arranque nuevo y con datos 0.9.7 copiados | Plugin habilitado, sin excepciones; revisar bedrock de huevos heredados |
| /rares varias páginas y detalles | Muestra las recetas del registro y sus resultados |
| Fabricar con stacks separados | Consume cada posición de la receta y entrega una unidad |
| Fabricar sin materiales / inventario lleno | No consume ni entrega nada |
| Click doble, shift, número, offhand, drag, drop | Ninguna extracción de las vistas previas ni duplicación |
| Cambio de recipes.yml y /ri reload | GUI nueva y mesa coinciden; receta desactivada deja de existir |
| Patrón vacío, símbolo desconocido, material inválido | Advertencia; receta omitida, nunca gratuita |
| Esencia falsa renombrada / núcleo con PDC incorrecto | No satisface ExactChoice |
| Fusionar equipo + esencia en mesa | Nivel limitado por maxLvl; no acepta mezclas de propiedades incompatibles |
| Permiso craft/hatch denegado y admin.compass solamente | No fabrica ni despierta; no recibe rare mediante /ri get |
| Combate 65% y 30% de vida | Transición una vez por umbral, refuerzos y especiales más frecuentes |
| Salir de marca antes de un segundo | Se evita el especial dirigido a ese jugador |
| Creativo, espectador, jugador lejano y pared | No se seleccionan como objetivos de combate válidos |
| Salir de radio de persecución | Regreso al ancla, vida restaurada, crédito de loot borrado |
| Muerte con golpe reciente de jugador | Un único reparto original de botín |
| Muerte sin crédito / súbditos | Sin botín de boss ni drops de súbdito |
| Alejar súbditos y repetir invocación | Límite del encuentro; no crece sin límite por alejarlos |
| Muerte de boss, chunk y apagado | Súbditos y tareas se limpian; bosses no quedan invulnerables |
| Huevo sobre cofre u otro contenedor | Soporte e inventario intactos tras eclosión |
| Huevo: pistón, agua, explosión, gravedad y golpe | No cambia de posición ni pierde su registro |
| Veinminer/treefeller dentro de una región protegida | Cada rotura respeta cancelación; no duplica drops |
| Combate prolongado con varios bosses | Revisar TPS, tareas, memoria, alcance y balance |

Registrar versión exacta de Paper, plugins instalados, pasos y logs en cualquier fallo.
