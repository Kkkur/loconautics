/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.neoforged.neoforge.client.model.BakedModelWrapper
 */
package com.simibubi.create.content.equipment.goggles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

public class GogglesModel
extends BakedModelWrapper<BakedModel> {
    public GogglesModel(BakedModel template) {
        super(template);
    }

    public BakedModel applyTransform(ItemDisplayContext cameraItemDisplayContext, PoseStack mat, boolean leftHanded) {
        if (cameraItemDisplayContext == ItemDisplayContext.HEAD) {
            return AllPartialModels.GOGGLES.get().applyTransform(cameraItemDisplayContext, mat, leftHanded);
        }
        return super.applyTransform(cameraItemDisplayContext, mat, leftHanded);
    }
}
