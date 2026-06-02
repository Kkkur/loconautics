/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.CrossCollisionBlock
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.equipment.zapper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ZapperItemRenderer
extends CustomRenderedItemModelRenderer {
    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (transformType == ItemDisplayContext.GUI && stack.has(AllDataComponents.SHAPER_BLOCK_USED)) {
            this.renderBlockUsed(stack, ms, buffer, light, overlay);
        }
    }

    private void renderBlockUsed(ItemStack stack, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = (BlockState)stack.get(AllDataComponents.SHAPER_BLOCK_USED);
        ms.pushPose();
        ms.translate(-0.3f, -0.45f, -0.0f);
        ms.scale(0.25f, 0.25f, 0.25f);
        BakedModel modelForState = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        if (state.getBlock() instanceof CrossCollisionBlock) {
            modelForState = Minecraft.getInstance().getItemRenderer().getModel(new ItemStack((ItemLike)state.getBlock()), null, null, 0);
        }
        Minecraft.getInstance().getItemRenderer().render(new ItemStack((ItemLike)state.getBlock()), ItemDisplayContext.NONE, false, ms, buffer, light, overlay, modelForState);
        ms.popPose();
    }

    protected float getAnimationProgress(float pt, boolean leftHanded, boolean mainHand) {
        float animation = CreateClient.ZAPPER_RENDER_HANDLER.getAnimation(mainHand ^ leftHanded, pt);
        return Mth.clamp((float)(animation * 5.0f), (float)0.0f, (float)1.0f);
    }
}
