# Cambios de 1.13.3

Ascensión del Firmamento utiliza impulso vertical y levitación X durante veinte ticks. La elevación deja de depender exclusivamente de un paquete de velocidad. El boss permanece anclado y solo se afecta a jugadores no OP en supervivencia o aventura. No se descancelan eventos de velocidad. La levitación usa su propio evento de poción, que otros plugins pueden denegar; no se teletransporta al jugador.

El temporizador del burst es independiente de la persecución y ataques básicos, con una comprobación común cada cinco ticks para todos los bosses registrados. Sigue coordinándose con cargas en curso y se limpia al morir, descargar o desactivar el boss.

Durante la elevación se suspenden el vuelo y doble salto de las rares de CazaJefes para que no sustituyan la trayectoria. Al terminar se recuperan los permisos de vuelo previos cuando corresponde. La levitación expira normalmente; no se borra una poción ajena al limpiar el temporizador. Ruptura del Horizonte conserva el empuje horizontal y no aplica levitación.

/ri diagnostico informa si se aceptó la levitación, además de los datos de velocidad. No se ha ejecutado esta versión en un servidor Paper. Las pruebas de geometría, catálogo, recargas y selección del mecanismo no sustituyen una comprobación del movimiento con clientes.
