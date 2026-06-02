/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Matrix4f
 */
package com.simibubi.create.compat.trainmap;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.compat.trainmap.TrainMapRenderer;
import com.simibubi.create.foundation.render.RenderTypes;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class TrainMapRenderer.TrainMapInstance
implements AutoCloseable {
    private DynamicTexture texture;
    private RenderType renderType;
    private boolean requiresUpload;
    private boolean linearFiltering;
    private Rect2i bounds;
    private boolean untouched;
    private Couple<Integer> sectionKey;
    public ResourceLocation location;

    public TrainMapRenderer.TrainMapInstance(TrainMapRenderer this$0, Couple<Integer> sectionKey) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        this.sectionKey = sectionKey;
        this.untouched = false;
        this.requiresUpload = true;
        this.texture = new DynamicTexture(128, 128, true);
        this.linearFiltering = false;
        this.location = textureManager.register("create_trainmap/" + String.valueOf(sectionKey.getFirst()) + "_" + String.valueOf(sectionKey.getSecond()), this.texture);
        this.renderType = RenderTypes.TRAIN_MAP.apply(this.location, this.linearFiltering);
        this.bounds = new Rect2i((Integer)sectionKey.getFirst() * 128, (Integer)sectionKey.getSecond() * 128, 128, 128);
    }

    public boolean canBeSkipped(Rect2i bounds) {
        return bounds.getX() + bounds.getWidth() < this.bounds.getX() || this.bounds.getX() + this.bounds.getWidth() < bounds.getX() || bounds.getY() + bounds.getHeight() < this.bounds.getY() || this.bounds.getY() + this.bounds.getHeight() < bounds.getY();
    }

    public NativeImage getImage() {
        this.untouched = false;
        this.requiresUpload = true;
        return this.texture.getPixels();
    }

    public void draw(PoseStack pPoseStack, MultiBufferSource pBufferSource, boolean linearFiltering) {
        if (this.texture.getPixels() == null) {
            return;
        }
        if (this.requiresUpload) {
            this.texture.upload();
            this.requiresUpload = false;
        }
        if (pPoseStack == null) {
            return;
        }
        if (linearFiltering != this.linearFiltering) {
            this.linearFiltering = linearFiltering;
            this.renderType = RenderTypes.TRAIN_MAP.apply(this.location, linearFiltering);
        }
        int pPackedLight = 0xF000F0;
        Matrix4f matrix4f = pPoseStack.last().pose();
        VertexConsumer vertexconsumer = pBufferSource.getBuffer(this.renderType);
        vertexconsumer.addVertex(matrix4f, 0.0f, 128.0f, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setLight(pPackedLight);
        vertexconsumer.addVertex(matrix4f, 128.0f, 128.0f, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setLight(pPackedLight);
        vertexconsumer.addVertex(matrix4f, 128.0f, 0.0f, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setLight(pPackedLight);
        vertexconsumer.addVertex(matrix4f, 0.0f, 0.0f, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setLight(pPackedLight);
    }

    @Override
    public void close() {
        this.texture.close();
    }
}
