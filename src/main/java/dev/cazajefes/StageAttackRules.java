package dev.cazajefes;
import java.util.*;
final class StageAttackRules {
 static int groundCount(int phase){return phase>=3?2:3;}
 static long groundCooldown(int phase,int slot){return phase>=3&&slot==1?15000:phase==1?12000:10000;}
 static long gap(int phase){return phase==1?4000:phase==2?3000:2500;}
 static double lift(int phase){return CombatPower.lift(phase);}
 static final String LIFT="Ascensión del Firmamento",REPEL="Ruptura del Horizonte";
 static BossAbilityCatalog.Ultimate profile(BossCatalog.Definition boss,int phase,int slot){
  var base=BossAbilityCatalog.ultimate(boss);if(phase>=3&&slot==1)return base;
  String[] names=phase==1?new String[]{"Anillo de Ruptura","Corte del Umbral","Barrido del Guardián"}:phase==2?new String[]{"Espiral de Asedio","Runas de Fractura","Vórtice del Ocaso"}:new String[]{"Lluvia del Cataclismo"};
  var patterns=phase==1?new BossAbilityCatalog.Pattern[]{BossAbilityCatalog.Pattern.RINGS,BossAbilityCatalog.Pattern.CROSS,BossAbilityCatalog.Pattern.CONE}:phase==2?new BossAbilityCatalog.Pattern[]{BossAbilityCatalog.Pattern.SPIRAL,BossAbilityCatalog.Pattern.GEYSERS,BossAbilityCatalog.Pattern.VORTEX}:new BossAbilityCatalog.Pattern[]{BossAbilityCatalog.Pattern.METEORS};
  int i=Math.floorMod(slot,names.length);return new BossAbilityCatalog.Ultimate(names[i],patterns[i],base.effect(),phase==1?6:phase==2?7:8,phase==1?1:2,base.angle());
 }
 static final class Rotation {
  int phase,index;long next;long[] ready;
  int select(int stage,long now,boolean ultimateReady){if(stage!=phase){phase=stage;ready=new long[groundCount(stage)];Arrays.fill(ready,now+4500);next=now+4500;index=0;}if(now<next)return -1;
   for(int n=0;n<ready.length;n++){int slot=(index+n)%ready.length;if(now<ready[slot]||stage>=3&&slot==1&&!ultimateReady)continue;ready[slot]=now+groundCooldown(stage,slot);next=now+gap(stage);index=(slot+1)%ready.length;return slot;}return -1;
  }
 }
}
