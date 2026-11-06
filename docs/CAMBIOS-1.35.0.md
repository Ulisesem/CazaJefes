# CazaJefes 1.35.0 · Auras de combate

Los 168 jefes del catálogo usan perfiles visuales por familia. Al despertar o entrar en la etapa III, el jefe emite un pulso con su paleta. Durante la carga de ataques, el aura ambiental baja de intensidad para que se lea el peligro. Al morir, los efectos se contraen hacia el cuerpo, seguidos por el impacto y la entrega animada de botín.

## Ajustes y rendimiento

- Usa `/ri particulas baja|media|alta|off` para ajustar el aura ambiental.
- Se muestran como máximo dos auras por jugador, dentro de un alcance de 32 bloques. Pasados 16 bloques se usa la densidad baja.
- Los avisos de área usan un cupo de partículas separado. Si hay 16 animaciones de muerte en curso, las nuevas entregan el botín sin otra secuencia de impacto.
- En un servidor de prueba, compara el tiempo por tick con spark y comprueba los FPS desde un cliente con varios jefes y jugadores. Estas mediciones y la revisión visual en juego quedan pendientes.

## Integraciones

Quedan por verificar WorldGuard, PacketEvents y Model Engine en un servidor con esos complementos. La restauración de arenas con WorldEdit, las incursiones con Mythic Dungeons y las integraciones con ItemsAdder y Floodgate siguen pendientes.
