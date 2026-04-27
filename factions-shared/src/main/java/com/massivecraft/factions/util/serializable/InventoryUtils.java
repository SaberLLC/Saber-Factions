package com.massivecraft.factions.util.serializable;

public class InventoryUtils {
    private int x;
    private int y;
    private int rows;

    public InventoryUtils(int x, int y, int rows) {
        this.x = x;
        this.y = y;
        this.rows = rows;
    }

    public void increment() {
        if (this.x == 9) {
            this.x = 0;
            ++this.y;
        }
    }
}
