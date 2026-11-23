package dev.cazajefes.ai;
import java.util.UUID;
/** One combo belongs to one target and shares the boss melee clock. */
public final class MeleeCombo {
 private UUID target;private long last=Long.MIN_VALUE;private int step;
 public void strike(UUID next,long tick,MeleeSettings settings){if(!next.equals(target)||last==Long.MIN_VALUE||tick-last>settings.comboWindowTicks()||step>=settings.comboHits())step=0;target=next;last=tick;step++;}
 public double multiplier(MeleeSettings settings){return 1+Math.max(0,step-1)*settings.comboBonus();}
 public int interval(MeleeSettings settings){return step<settings.comboHits()?settings.comboIntervalTicks():settings.intervalTicks();}
 public void clear(){target=null;last=Long.MIN_VALUE;step=0;}
}
