/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.MapDecorationTextureManager
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.saveddata.maps.MapDecoration
 *  net.minecraft.world.level.saveddata.maps.MapItemSavedData
 *  net.neoforged.neoforge.client.gui.map.IMapDecorationRenderer
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Matrix4f
 */
package com.simibubi.create.foundation.map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MapDecorationTextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.neoforged.neoforge.client.gui.map.IMapDecorationRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class StationMapDecorationRenderer
implements IMapDecorationRenderer {
    public boolean render(MapDecoration decoration, PoseStack poseStack, MultiBufferSource bufferSource, @NotNull MapItemSavedData mapData, MapDecorationTextureManager decorationTextures, boolean inItemFrame, int packedLight, int index) {
        poseStack.pushPose();
        poseStack.translate((double)decoration.x() / 2.0 + 64.0, (double)decoration.y() / 2.0 + 64.0, -0.02);
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.0f, 0.0f);
        poseStack.scale(4.5f, 4.5f, 3.0f);
        TextureAtlasSprite sprite = decorationTextures.get(decoration);
        float U0 = sprite.getU0();
        float V0 = sprite.getV0();
        float U1 = sprite.getU1();
        float V1 = sprite.getV1();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.text((ResourceLocation)sprite.atlasLocation()));
        Matrix4f mat = poseStack.last().pose();
        float zOffset = -0.001f;
        buffer.addVertex(mat, -1.0f, 1.0f, (float)index * zOffset).setColor(-1).setUv(U0, V0).setLight(packedLight);
        buffer.addVertex(mat, 1.0f, 1.0f, (float)index * zOffset).setColor(-1).setUv(U1, V0).setLight(packedLight);
        buffer.addVertex(mat, 1.0f, -1.0f, (float)index * zOffset).setColor(-1).setUv(U1, V1).setLight(packedLight);
        buffer.addVertex(mat, -1.0f, -1.0f, (float)index * zOffset).setColor(-1).setUv(U0, V1).setLight(packedLight);
        poseStack.popPose();
        if (decoration.name().isPresent()) {
            Font font = Minecraft.getInstance().font;
            Component component = (Component)decoration.name().get();
            float f6 = font.width((FormattedText)component);
            poseStack.pushPose();
            poseStack.translate(0.0, 6.0, (double)-0.005f);
            poseStack.scale(0.8f, 0.8f, 1.0f);
            poseStack.translate(-f6 / 2.0f + 0.5f, 0.0f, 0.0f);
            font.drawInBatch(component, 0.0f, 0.0f, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, Integer.MIN_VALUE, packedLight);
            poseStack.popPose();
        }
        poseStack.popPose();
        return true;
    }
}
