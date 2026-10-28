package dev.cazajefes;
import java.util.*;
import java.util.random.RandomGenerator;
final class LootRules {
 enum Kind { FRAGMENT,TRIBUTE,CRYSTAL,CORE,ARMOR,WEAPON,BASE_ESSENCE,ESSENCE,ABYSSAL_ESSENCE,MATERIAL }
 record Reward(Kind kind,int amount,int variant) {}
 static double probability(double value) { return Double.isFinite(value) ? Math.max(0,Math.min(1,value)) : 0; }
 static List<Reward> roll(BossCatalog.Definition boss,RandomGenerator random) {
  List<Reward> out=new ArrayList<>(); int rank=boss.tier().ordinal();
  if (boss.mini()) {
   out.add(new Reward(Kind.TRIBUTE,1+random.nextInt(2),0));out.add(new Reward(Kind.FRAGMENT,1+random.nextInt(3),0));out.add(new Reward(Kind.CRYSTAL,1,0));return out;
  }
  out.add(new Reward(Kind.FRAGMENT,2+random.nextInt(2+rank),0));
  add(out,random,Kind.TRIBUTE,0.40+rank*0.05,1,0);
  add(out,random,Kind.CORE,0.50+rank*0.05,1,0);
  if(rank>=2)add(out,random,Kind.ARMOR,0.08+rank*0.035,1,random.nextInt(4));
  if(rank>=2)add(out,random,Kind.WEAPON,0.06+rank*0.025,1,random.nextInt(2));
  add(out,random,Kind.BASE_ESSENCE,0.08+rank*0.035,1,0);
  add(out,random,Kind.ESSENCE,0.12+rank*0.055,1,0);
  if(rank==5) add(out,random,Kind.ABYSSAL_ESSENCE,0.15,1,0);
  add(out,random,Kind.BASE_ESSENCE,0.08+rank*0.03,1,random.nextInt(39));
  add(out,random,Kind.MATERIAL,0.55,1+random.nextInt(2),random.nextInt(4));
  return List.copyOf(out);
 }
 private static void add(List<Reward> out,RandomGenerator random,Kind kind,double chance,int amount,int variant) {
  if(random.nextDouble()<probability(chance))out.add(new Reward(kind,amount,variant));
 }
 static boolean heartEligible(boolean mini,double roll,double chance){return !mini&&roll<probability(chance);}
 static boolean eligibleMob(boolean playerKill,boolean hostile,boolean farmSpawn,boolean boss,boolean minion) {
  return playerKill&&hostile&&!farmSpawn&&!boss&&!minion;
 }
}
