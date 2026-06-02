/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FastColor$ABGR32
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  org.joml.Matrix4f
 */
package com.simibubi.create.compat.trainmap;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.infrastructure.config.CClient;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashSet;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

public class TrainMapRenderer
implements AutoCloseable {
    public static final TrainMapRenderer INSTANCE = new TrainMapRenderer();
    public static final int WIDTH = 128;
    public static final int HEIGHT = 128;
    private Object2ObjectMap<Couple<Integer>, TrainMapInstance> maps = new Object2ObjectOpenHashMap();
    public int trackingVersion;
    public ResourceKey<Level> trackingDim;
    public CClient.TrainMapTheme trackingTheme;
    private TrainMapInstance previouslyAccessed;

    public void startDrawing() {
        this.previouslyAccessed = null;
        this.maps.values().forEach(tmi -> {
            tmi.getImage().fillRect(0, 0, 128, 128, 0);
            tmi.untouched = true;
        });
    }

    public Object2ObjectMap<Couple<Integer>, TrainMapInstance> getMaps() {
        return this.maps;
    }

    public void setPixel(int xCoord, int zCoord, int color) {
        TrainMapInstance instance = this.getOrCreateAt(xCoord, zCoord);
        xCoord = Mth.positiveModulo((int)xCoord, (int)128);
        zCoord = Mth.positiveModulo((int)zCoord, (int)128);
        instance.getImage().setPixelRGBA(xCoord, zCoord, color);
    }

    public int getPixel(int xCoord, int zCoord) {
        Couple<Integer> sectionKey = this.toSectionKey(xCoord, zCoord);
        if (!this.maps.containsKey(sectionKey)) {
            return 0;
        }
        TrainMapInstance instance = this.getOrCreateAt(xCoord, zCoord);
        xCoord = Mth.positiveModulo((int)xCoord, (int)128);
        zCoord = Mth.positiveModulo((int)zCoord, (int)128);
        return instance.getImage().getPixelRGBA(xCoord, zCoord);
    }

    public void setPixels(int xCoordFrom, int zCoordFrom, int xCoordTo, int zCoordTo, int color) {
        for (int x = Math.min(xCoordFrom, xCoordTo); x <= Math.max(xCoordFrom, xCoordTo); ++x) {
            for (int z = Math.min(zCoordFrom, zCoordTo); z <= Math.max(zCoordFrom, zCoordTo); ++z) {
                this.setPixel(x, z, color);
            }
        }
    }

    public void blendPixel(int xCoord, int zCoord, int color, int alpha) {
        TrainMapInstance instance = this.getOrCreateAt(xCoord, zCoord);
        xCoord = Mth.positiveModulo((int)xCoord, (int)128);
        zCoord = Mth.positiveModulo((int)zCoord, (int)128);
        instance.getImage().blendPixel(xCoord, zCoord, FastColor.ABGR32.color((int)alpha, (int)color));
    }

    public void blendPixels(int xCoordFrom, int zCoordFrom, int xCoordTo, int zCoordTo, int color, int alpha) {
        for (int x = Math.min(xCoordFrom, xCoordTo); x <= Math.max(xCoordFrom, xCoordTo); ++x) {
            for (int z = Math.min(zCoordFrom, zCoordTo); z <= Math.max(zCoordFrom, zCoordTo); ++z) {
                this.blendPixel(x, z, color, alpha);
            }
        }
    }

    public void finishDrawing() {
        this.previouslyAccessed = null;
        HashSet stale = new HashSet();
        this.maps.forEach((key, tmi) -> {
            if (!tmi.untouched) {
                return;
            }
            tmi.close();
            stale.add(key);
        });
        stale.forEach(key -> {
            TrainMapInstance tmi = (TrainMapInstance)this.maps.remove(key);
            if (tmi != null) {
                tmi.close();
            }
        });
    }

    public boolean is(int x, int z, int color) {
        return (this.getPixel(x, z) & 0xFFFFFF) == (color & 0xFFFFFF);
    }

    public boolean isEmpty(int x, int z) {
        return this.getPixel(x, z) == 0;
    }

    public int alphaAt(int x, int z) {
        int pixel = this.getPixel(x, z);
        return (pixel & 0xFFFFFF) != 0 ? pixel >>> 24 & 0xFF : 0;
    }

    public void render(GuiGraphics graphics, boolean linearFiltering, Rect2i bounds) {
        MultiBufferSource.BufferSource bufferSource = graphics.bufferSource();
        PoseStack pose = graphics.pose();
        this.maps.forEach((key, tmi) -> {
            if (tmi.canBeSkipped(bounds)) {
                return;
            }
            int x = (Integer)key.getFirst();
            int y = (Integer)key.getSecond();
            pose.pushPose();
            pose.translate((float)(x * 128), (float)(y * 128), 0.0f);
            tmi.draw(pose, (MultiBufferSource)bufferSource, linearFiltering);
            pose.popPose();
        });
    }

    public TrainMapInstance getOrCreateAt(int xCoord, int zCoord) {
        Couple<Integer> sectionKey = this.toSectionKey(xCoord, zCoord);
        if (this.previouslyAccessed != null && this.previouslyAccessed.sectionKey.equals(sectionKey)) {
            return this.previouslyAccessed;
        }
        return (TrainMapInstance)this.maps.compute(sectionKey, (key, instance) -> instance == null ? new TrainMapInstance(this, (Couple<Integer>)key) : instance);
    }

    public Couple<Integer> toSectionKey(int xCoord, int zCoord) {
        return Couple.create((Object)Mth.floor((float)((float)xCoord / 128.0f)), (Object)Mth.floor((float)((float)zCoord / 128.0f)));
    }

    public void resetData() {
        for (TrainMapInstance instance : this.maps.values()) {
            instance.close();
        }
        this.maps.clear();
    }

    @Override
    public void close() {
        this.resetData();
    }

    public class TrainMapInstance
    implements AutoCloseable {
        private DynamicTexture texture;
        private RenderType renderType;
        private boolean requiresUpload;
        private boolean linearFiltering;
        private Rect2i bounds;
        private boolean untouched;
        private Couple<Integer> sectionKey;
        public ResourceLocation location;

        public TrainMapInstance(TrainMapRenderer this$0, Couple<Integer> sectionKey) {
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
}
