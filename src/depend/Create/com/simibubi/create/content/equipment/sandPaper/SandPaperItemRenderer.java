/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.sandPaper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItemComponent;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SandPaperItemRenderer
extends CustomRenderedItemModelRenderer {
    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        int itemInUseCount;
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        LocalPlayer player = mc.player;
        float partialTicks = AnimationTickHolder.getPartialTicks();
        boolean leftHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        boolean firstPerson = leftHand || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        boolean jeiMode = stack.has(AllDataComponents.SAND_PAPER_JEI);
        ms.pushPose();
        if (stack.has(AllDataComponents.SAND_PAPER_POLISHING)) {
            ms.pushPose();
            if (transformType == ItemDisplayContext.GUI) {
                ms.translate(0.0f, 0.2f, 1.0f);
                ms.scale(0.75f, 0.75f, 0.75f);
            } else {
                int modifier = leftHand ? -1 : 1;
                ms.mulPose(Axis.YP.rotationDegrees((float)(modifier * 40)));
            }
            float time = (float)(!jeiMode ? player.getUseItemRemainingTicks() : -AnimationTickHolder.getTicks() % stack.getUseDuration((LivingEntity)player)) - partialTicks + 1.0f;
            if (time / (float)stack.getUseDuration((LivingEntity)player) < 0.8f) {
                float bobbing = -Mth.abs((float)(Mth.cos((float)(time / 4.0f * (float)Math.PI)) * 0.1f));
                if (transformType == ItemDisplayContext.GUI) {
                    ms.translate(bobbing, bobbing, 0.0f);
                } else {
                    ms.translate(0.0f, bobbing, 0.0f);
                }
            }
            ItemStack toPolish = ((SandPaperItemComponent)stack.get(AllDataComponents.SAND_PAPER_POLISHING)).item();
            itemRenderer.renderStatic(toPolish, ItemDisplayContext.GUI, light, overlay, ms, buffer, player.level(), 0);
            ms.popPose();
        }
        if (firstPerson && (itemInUseCount = player.getUseItemRemainingTicks()) > 0) {
            int modifier = leftHand ? -1 : 1;
            ms.translate((float)modifier * 0.5f, 0.0f, -0.25f);
            ms.mulPose(Axis.ZP.rotationDegrees((float)(modifier * 40)));
            ms.mulPose(Axis.XP.rotationDegrees((float)(modifier * 10)));
            ms.mulPose(Axis.YP.rotationDegrees((float)(modifier * 90)));
        }
        itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel());
        ms.popPose();
    }
}
