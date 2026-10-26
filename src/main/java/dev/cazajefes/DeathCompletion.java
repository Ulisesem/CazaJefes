package dev.cazajefes;
/** Releases an encounter reward once, including interrupted visual sequences. */
final class DeathCompletion {
 private Runnable reward;
 DeathCompletion(Runnable reward){this.reward=java.util.Objects.requireNonNull(reward);}
 void finish(){Runnable action=reward;if(action==null)return;reward=null;action.run();}
}
