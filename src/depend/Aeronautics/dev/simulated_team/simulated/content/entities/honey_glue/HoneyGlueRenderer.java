/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 */
package dev.simulated_team.simulated.content.entities.honey_glue;

import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class HoneyGlueRenderer
extends EntityRenderer<HoneyGlueEntity> {
    public HoneyGlueRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public ResourceLocation getTextureLocation(HoneyGlueEntity entity) {
        return ResourceLocation.parse((String)"");
    }

    public boolean shouldRender(HoneyGlueEntity entity, Frustum frustum, double x, double y, double z) {
        return false;
    }
}
