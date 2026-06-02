/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.content.contraptions.actors.seat;

import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public static class SeatEntity.Render
extends EntityRenderer<SeatEntity> {
    public SeatEntity.Render(EntityRendererProvider.Context context) {
        super(context);
    }

    public boolean shouldRender(SeatEntity seatEntity, Frustum frustum, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
        return false;
    }

    public ResourceLocation getTextureLocation(SeatEntity seatEntity) {
        return null;
    }
}
