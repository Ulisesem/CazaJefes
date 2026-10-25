package dev.cazajefes;
final class EggPlacement {
 static boolean allowed(boolean eggAir,boolean aboveAir,boolean solid,boolean tile,boolean occupied){return eggAir&&aboveAir&&solid&&!tile&&!occupied;}
}
