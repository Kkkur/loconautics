/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.FenceBlock
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4fc
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;

public class ValueBoxRenderer {
    public static void renderItemIntoValueBox(ItemStack filter, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        BakedModel modelWithOverrides = itemRenderer.getModel(filter, null, null, 0);
        boolean blockItem = modelWithOverrides.isGui3d();
        float scale = (!blockItem ? 0.5f : 1.0f) + 0.015625f;
        float zOffset = (!blockItem ? -0.15f : 0.0f) + ValueBoxRenderer.customZOffset(filter.getItem());
        ms.scale(scale, scale, scale);
        ms.translate(0.0f, 0.0f, zOffset);
        itemRenderer.render(filter, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, modelWithOverrides);
    }

    public static void renderFlatItemIntoValueBox(ItemStack filter, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (filter.isEmpty()) {
            return;
        }
        int bl = light >> 4 & 0xF;
        int sl = light >> 20 & 0xF;
        int itemLight = Mth.floor((double)((double)sl + 0.5)) << 20 | (Mth.floor((double)((double)bl + 0.5)) & 0xF) << 4;
        ms.pushPose();
        TransformStack.of((PoseStack)ms).rotateXDegrees(230.0f);
        Matrix3f copy = new Matrix3f((Matrix3fc)ms.last().normal());
        ms.popPose();
        ms.pushPose();
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).translate(0.0f, 0.0f, -0.25f).translate(0.0, 0.0, 0.03225)).rotateYDegrees(180.0f);
        PoseStack squashedMS = new PoseStack();
        squashedMS.last().pose().mul((Matrix4fc)ms.last().pose());
        squashedMS.scale(0.5f, 0.5f, 9.765625E-4f);
        squashedMS.last().normal().set((Matrix3fc)copy);
        Minecraft mc = Minecraft.getInstance();
        mc.getItemRenderer().renderStatic(filter, ItemDisplayContext.GUI, itemLight, OverlayTexture.NO_OVERLAY, squashedMS, buffer, (Level)mc.level, 0);
        ms.popPose();
    }

    private static float customZOffset(Item item) {
        float nudge = -0.1f;
        if (item instanceof BlockItem) {
            Block block = ((BlockItem)item).getBlock();
            if (block instanceof AbstractSimpleShaftBlock) {
                return nudge;
            }
            if (block instanceof FenceBlock) {
                return nudge;
            }
            if (block.builtInRegistryHolder().is(BlockTags.BUTTONS)) {
                return nudge;
            }
            if (block == Blocks.END_ROD) {
                return nudge;
            }
        }
        return 0.0f;
    }
}
