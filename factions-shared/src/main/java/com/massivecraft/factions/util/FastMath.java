package com.massivecraft.factions.util;

public final class FastMath {

    private FastMath() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    private static final int BIG_ENOUGH_INT   = 16 * 1024;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5;

    public static int floor(float x) {
        return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    public static int round(float x) {
        return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    public static int ceil(float x) {
        return BIG_ENOUGH_INT - (int) (BIG_ENOUGH_FLOOR - x);
    }

    public static int floor(double x) {
        return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    public static int round(double x) {
        return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    public static int ceil(double x) {
        return BIG_ENOUGH_INT - (int) (BIG_ENOUGH_FLOOR - x);
    }
}