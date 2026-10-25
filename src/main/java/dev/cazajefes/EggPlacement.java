package dev.cazajefes;
final class EggPlacement {
 static boolean support(boolean air,boolean full,boolean tile){return !tile&&(air||full);}
 static boolean allowed(boolean eggAir,boolean aboveAir,boolean solid,boolean tile,boolean occupied){return eggAir&&aboveAir&&solid&&!tile&&!occupied;}
}
