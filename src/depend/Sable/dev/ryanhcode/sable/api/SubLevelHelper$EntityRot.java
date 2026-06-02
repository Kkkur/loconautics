/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 */
package dev.ryanhcode.sable.api;

import net.minecraft.world.entity.Entity;

private static class SubLevelHelper.EntityRot {
    private float xRot;
    private float yRot;
    private float yHeadRot;

    private SubLevelHelper.EntityRot() {
    }

    public void apply(Entity entity) {
        entity.setXRot(this.xRot);
        entity.setYRot(this.yRot);
        entity.setYHeadRot(this.yHeadRot);
    }

    public void copy(Entity entity) {
        this.xRot = entity.getXRot();
        this.yRot = entity.getYRot();
        this.yHeadRot = entity.getYHeadRot();
    }
}
