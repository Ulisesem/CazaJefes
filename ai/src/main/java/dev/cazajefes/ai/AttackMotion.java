package dev.cazajefes.ai;

/** One cast is advanced by the same server loop as its owner. */
public interface AttackMotion extends AutoCloseable {
    boolean tick();
    @Override void close();

    static AttackMotion complete(){
        return new AttackMotion(){
            @Override public boolean tick(){return true;}
            @Override public void close(){}
        };
    }
}
