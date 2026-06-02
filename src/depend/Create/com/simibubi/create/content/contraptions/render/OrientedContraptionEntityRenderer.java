/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 */
package com.simibubi.create.content.contraptions.render;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class OrientedContraptionEntityRenderer
extends ContraptionEntityRenderer<OrientedContraptionEntity> {
    public OrientedContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(OrientedContraptionEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        if (!super.shouldRender(entity, frustum, cameraX, cameraY, cameraZ)) {
            return false;
        }
        return entity.getVehicle() != null || !AllTags.AllContraptionTypeTags.REQUIRES_VEHICLE_FOR_RENDER.matches(entity.getContraption().getType());
    }
}
