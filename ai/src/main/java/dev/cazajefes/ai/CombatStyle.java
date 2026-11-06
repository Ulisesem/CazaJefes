package dev.cazajefes.ai;

/** Preferred engagement band, independent of each attack's actual range. */
public enum CombatStyle {
    BRAWLER(1.3, 2.6, .45, false),
    SKIRMISHER(3.5, 7, .8, true),
    RANGED(7, 13, .65, false),
    ARTILLERY(13, 24, .42, false),
    AQUATIC(2.2, 5, .72, true);

    final double near, far, orbit;
    final boolean mobileWindup;

    CombatStyle(double near,double far,double orbit,boolean mobileWindup){
        this.near=near;
        this.far=far;
        this.orbit=orbit;
        this.mobileWindup=mobileWindup;
    }

    public record Tactics(CombatStyle style,double near,double far,double orbit,boolean mobileWindup) {
        public enum Move { APPROACH, RETREAT, ORBIT }

        public Move move(double distance,boolean visible){
            if(!visible||distance>far)return Move.APPROACH;
            if(distance<near)return Move.RETREAT;
            return Move.ORBIT;
        }
    }
}
