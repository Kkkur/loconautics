/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.ponder.api.element.ParrotPose
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.createmod.ponder.foundation.element.ParrotElementImpl
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.infrastructure.ponder.scenes.highLogistics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllItems;
import java.util.function.Supplier;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.element.ParrotElementImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public static class FrogAndConveyorScenes.ChainConveyorParrotElement
extends ParrotElementImpl {
    private ItemEntity wrench;

    public FrogAndConveyorScenes.ChainConveyorParrotElement(Vec3 location, Supplier<? extends ParrotPose> pose) {
        super(location, pose);
    }

    protected void renderLast(PonderLevel world, MultiBufferSource buffer, GuiGraphics graphics, float fade, float pt) {
        PoseStack poseStack = graphics.pose();
        EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        if (this.entity == null) {
            this.entity = this.pose.create(world);
            this.entity.yRotO = 180.0f;
            this.entity.setYRot(180.0f);
        }
        if (this.wrench == null) {
            this.wrench = new ItemEntity((Level)world, 0.0, 0.0, 0.0, AllItems.WRENCH.asStack());
            this.wrench.yRotO = 180.0f;
            this.wrench.setYRot(180.0f);
        }
        double lx = Mth.lerp((double)pt, (double)this.entity.xo, (double)this.entity.getX());
        double ly = Mth.lerp((double)pt, (double)this.entity.yo, (double)this.entity.getY());
        double lz = Mth.lerp((double)pt, (double)this.entity.zo, (double)this.entity.getZ());
        float angle = AngleHelper.angleLerp((double)pt, (double)this.entity.yRotO, (double)this.entity.getYRot());
        poseStack.pushPose();
        poseStack.translate(this.location.x, this.location.y, this.location.z);
        poseStack.translate(lx, ly, lz);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.translate(0.0f, 1.5f, 0.0f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin((float)(((float)world.scene.getCurrentTime() + pt) * 0.2f)) * 10.0f));
        poseStack.translate(0.0f, -1.5f, 0.0f);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0f));
        poseStack.scale(1.5f, 1.5f, 1.5f);
        poseStack.translate(-0.1, 0.2, -0.6);
        BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(this.wrench.getItem(), (Level)world, null, 0);
        Minecraft.getInstance().getItemRenderer().render(this.wrench.getItem(), ItemDisplayContext.GROUND, false, poseStack, buffer, this.lightCoordsFromFade(fade), OverlayTexture.NO_OVERLAY, bakedmodel);
        poseStack.popPose();
        this.entity.flapSpeed = 2.0f;
        entityrenderermanager.render((Entity)this.entity, 0.0, 0.0, 0.0, 0.0f, pt, poseStack, buffer, this.lightCoordsFromFade(fade));
        poseStack.popPose();
    }
}
