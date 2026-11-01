# CazaJefes 1.25.2 · Relevo Fantasma

Relevo ya no comprueba cada bloque del camino: funciona como salto instantáneo y puede cruzar obstáculos entre la salida y el destino. Busca primero a la derecha, después direcciones cercanas y finalmente alrededor, con destinos hasta 3 bloques por encima o por debajo. No exige suelo bajo el jugador, por lo que funciona en bordes, huecos y desniveles.

Conserva la distancia horizontal aleatoria por nivel (1–2 / 2–3 / 3–4 / 4–5 bloques), probabilidad y recarga. El destino final debe tener espacio para el cuerpo y permanecer dentro de la altura del mundo, borde y chunks cargados. Respeta la cancelación del evento de teletransporte. Si todos los destinos están ocupados no consume la preparación y muestra el aviso sobre la hotbar, sin repetir el mensaje largo en el chat.

Validación: Maven verify y pruebas de salto sobre pared, destinos alternativos, distancia y espacio completamente bloqueado. Sin prueba dentro de Paper.
