/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.kinetics.saw;

private static enum TreeCutter.SearchDirection {
    UP(0, 1),
    DOWN(-1, 0),
    BOTH(-1, 1);

    int minY;
    int maxY;

    private TreeCutter.SearchDirection(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }
}
