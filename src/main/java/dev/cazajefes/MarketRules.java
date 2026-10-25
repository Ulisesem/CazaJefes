package dev.cazajefes;
final class MarketRules {
 static int sigilFragments(int value){return Math.max(1,Math.min(4096,value));}
 static int amuletSigils(int value){return Math.max(1,Math.min(64,value));}
 static int amuletTributes(int value){return Math.max(0,Math.min(64,value));}
}
