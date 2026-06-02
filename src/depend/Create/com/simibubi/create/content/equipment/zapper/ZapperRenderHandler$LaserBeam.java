/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.equipment.zapper;

import net.minecraft.world.phys.Vec3;

public static class ZapperRenderHandler.LaserBeam {
    float itensity;
    Vec3 start;
    Vec3 end;

    public ZapperRenderHandler.LaserBeam(Vec3 start, Vec3 end) {
        this.start = start;
        this.end = end;
        this.itensity = 1.0f;
    }
}
