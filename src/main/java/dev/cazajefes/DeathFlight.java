package dev.cazajefes;
final class DeathFlight {static double height(int tick,double rise){if(tick<0||tick>=48)return 0;return tick<=32?rise*tick/32.0:rise*(48-tick)/16.0;}}
