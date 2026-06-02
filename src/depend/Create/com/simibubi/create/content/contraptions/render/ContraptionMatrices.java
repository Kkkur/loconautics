/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.simibubi.create.content.contraptions.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class ContraptionMatrices {
    private final PoseStack modelViewProjection = new PoseStack();
    private final PoseStack viewProjection = new PoseStack();
    private final PoseStack model = new PoseStack();
    private final Matrix4f world = new Matrix4f();
    private final Matrix4f light = new Matrix4f();

    void setup(PoseStack viewProjection, AbstractContraptionEntity entity) {
        float partialTicks = AnimationTickHolder.getPartialTicks();
        this.viewProjection.pushPose();
        ContraptionMatrices.transform(this.viewProjection, viewProjection);
        this.model.pushPose();
        entity.applyLocalTransforms(this.model, partialTicks);
        this.modelViewProjection.pushPose();
        ContraptionMatrices.transform(this.modelViewProjection, viewProjection);
        ContraptionMatrices.transform(this.modelViewProjection, this.model);
        ContraptionMatrices.translateToEntity(this.world, entity, partialTicks);
        this.light.set((Matrix4fc)this.world);
        this.light.mul((Matrix4fc)this.model.last().pose());
    }

    void clear() {
        ContraptionMatrices.clearStack(this.modelViewProjection);
        ContraptionMatrices.clearStack(this.viewProjection);
        ContraptionMatrices.clearStack(this.model);
        this.world.identity();
        this.light.identity();
    }

    public PoseStack getModelViewProjection() {
        return this.modelViewProjection;
    }

    public PoseStack getViewProjection() {
        return this.viewProjection;
    }

    public PoseStack getModel() {
        return this.model;
    }

    public Matrix4f getWorld() {
        return this.world;
    }

    public Matrix4f getLight() {
        return this.light;
    }

    public static void transform(PoseStack ms, PoseStack transform) {
        ms.last().pose().mul((Matrix4fc)transform.last().pose());
        ms.last().normal().mul((Matrix3fc)transform.last().normal());
    }

    public static void translateToEntity(Matrix4f matrix, Entity entity, float partialTicks) {
        double x = Mth.lerp((double)partialTicks, (double)entity.xOld, (double)entity.getX());
        double y = Mth.lerp((double)partialTicks, (double)entity.yOld, (double)entity.getY());
        double z = Mth.lerp((double)partialTicks, (double)entity.zOld, (double)entity.getZ());
        matrix.setTranslation((float)x, (float)y, (float)z);
    }

    public static void clearStack(PoseStack ms) {
        while (!ms.clear()) {
            ms.popPose();
        }
    }
}
