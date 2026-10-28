package dev.cazajefes;
final class HordeRules {static final int SIZE=6,PER_BOSS=12,GLOBAL=192;static boolean room(long boss,long global){return boss>=0&&global>=0&&boss+SIZE<=PER_BOSS&&global+SIZE<=GLOBAL;}}
