package com.massivecraft.factions.util;

/**
 * Factions - Developed by FactionsUUID Team.
 */
public class Pair<Left, Right> {

    private final Left left;
    private final Right right;
    private Pair(Left left, Right right) {
        this.left = left;
        this.right = right;
    }

    public static <Left, Right> Pair<Left, Right> of(Left left, Right right) {
        return new Pair<>(left, right);
    }

    public Left getLeft() {
        return this.left;
    }

    public Right getRight() {
        return this.right;
    }
}
