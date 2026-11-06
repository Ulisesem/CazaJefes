package dev.cazajefes.ai;

public enum AttackCategory {
    SHORT_RANGE, MID_RANGE, LONG_RANGE, GLOBAL, MIXED;

    public boolean includes(double distance) {
        if (!Double.isFinite(distance) || distance < 0) return false;
        return switch (this) {
            case SHORT_RANGE -> distance < 3;
            case MID_RANGE -> distance >= 3 && distance < 10;
            case LONG_RANGE -> distance >= 10 && distance <= 30;
            case GLOBAL, MIXED -> true;
        };
    }
}
