/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.logistics.stockTicker;

public static class StockKeeperRequestScreen.CategoryEntry {
    boolean hidden;
    String name;
    int y;
    int targetBECategory;

    public StockKeeperRequestScreen.CategoryEntry(int targetBECategory, String name, int y) {
        this.targetBECategory = targetBECategory;
        this.name = name;
        this.hidden = false;
        this.y = y;
    }
}
