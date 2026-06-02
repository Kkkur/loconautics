/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 */
package com.simibubi.create.foundation.blockEntity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.link.LinkRenderer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class SmartBlockEntityRenderer<T extends SmartBlockEntity>
extends SafeBlockEntityRenderer<T> {
    public SmartBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(T blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        FilteringRenderer.renderOnBlockEntity(blockEntity, partialTicks, ms, buffer, light, overlay);
        LinkRenderer.renderOnBlockEntity(blockEntity, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderNameplateOnHover(T blockEntity, Component tag, float yOffset, PoseStack ms, MultiBufferSource buffer, int light) {
        BlockHitResult bhr;
        Minecraft mc = Minecraft.getInstance();
        if (((SmartBlockEntity)blockEntity).isVirtual()) {
            return;
        }
        if (mc.player.distanceToSqr(Vec3.atCenterOf((Vec3i)blockEntity.getBlockPos())) > 4096.0) {
            return;
        }
        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult) || (bhr = (BlockHitResult)hitResult).getType() == HitResult.Type.MISS || !bhr.getBlockPos().equals((Object)blockEntity.getBlockPos())) {
            return;
        }
        float f = yOffset + 0.25f;
        ms.pushPose();
        ms.translate(0.5, (double)f, 0.5);
        ms.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        ms.scale(0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = ms.last().pose();
        float f2 = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        int j = (int)(f2 * 255.0f) << 24;
        Font font = mc.font;
        float f1 = -font.width((FormattedText)tag) / 2;
        font.drawInBatch(tag, f1, 0.0f, 0x20FFFFFF, false, matrix4f, buffer, Font.DisplayMode.SEE_THROUGH, j, light);
        font.drawInBatch(tag, f1, 0.0f, -1, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, light);
        ms.popPose();
    }
}
