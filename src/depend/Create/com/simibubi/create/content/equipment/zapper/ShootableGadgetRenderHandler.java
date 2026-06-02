/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.ItemInHandRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.player.PlayerRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.client.event.RenderHandEvent
 */
package com.simibubi.create.content.equipment.zapper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderHandEvent;

public abstract class ShootableGadgetRenderHandler {
    protected float leftHandAnimation;
    protected float rightHandAnimation;
    protected float lastLeftHandAnimation;
    protected float lastRightHandAnimation;
    protected boolean dontReequipLeft;
    protected boolean dontReequipRight;

    public void tick() {
        this.lastLeftHandAnimation = this.leftHandAnimation;
        this.lastRightHandAnimation = this.rightHandAnimation;
        this.leftHandAnimation *= this.animationDecay();
        this.rightHandAnimation *= this.animationDecay();
    }

    public float getAnimation(boolean rightHand, float partialTicks) {
        return Mth.lerp((float)partialTicks, (float)(rightHand ? this.lastRightHandAnimation : this.lastLeftHandAnimation), (float)(rightHand ? this.rightHandAnimation : this.leftHandAnimation));
    }

    protected float animationDecay() {
        return 0.8f;
    }

    public void shoot(InteractionHand hand, Vec3 location) {
        LocalPlayer player;
        boolean rightHand = hand == InteractionHand.MAIN_HAND ^ (player = Minecraft.getInstance().player).getMainArm() == HumanoidArm.LEFT;
        if (rightHand) {
            this.rightHandAnimation = 0.2f;
            this.dontReequipRight = false;
        } else {
            this.leftHandAnimation = 0.2f;
            this.dontReequipLeft = false;
        }
        this.playSound(hand, location);
    }

    protected abstract void playSound(InteractionHand var1, Vec3 var2);

    protected abstract boolean appliesTo(ItemStack var1);

    protected abstract void transformTool(PoseStack var1, float var2, float var3, float var4, float var5);

    protected abstract void transformHand(PoseStack var1, float var2, float var3, float var4, float var5);

    public void registerListeners(IEventBus bus) {
        bus.addListener(this::onRenderPlayerHand);
    }

    protected void onRenderPlayerHand(RenderHandEvent event) {
        ItemStack heldItem = event.getItemStack();
        if (!this.appliesTo(heldItem)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        PlayerRenderer playerrenderer = (PlayerRenderer)mc.getEntityRenderDispatcher().getRenderer((Entity)player);
        ItemInHandRenderer firstPersonRenderer = mc.getEntityRenderDispatcher().getItemInHandRenderer();
        PoseStack ms = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();
        int light = event.getPackedLight();
        float pt = event.getPartialTick();
        boolean rightHand = event.getHand() == InteractionHand.MAIN_HAND ^ mc.player.getMainArm() == HumanoidArm.LEFT;
        float recoil = rightHand ? Mth.lerp((float)pt, (float)this.lastRightHandAnimation, (float)this.rightHandAnimation) : Mth.lerp((float)pt, (float)this.lastLeftHandAnimation, (float)this.leftHandAnimation);
        float equipProgress = event.getEquipProgress();
        if (rightHand && (this.rightHandAnimation > 0.01f || this.dontReequipRight)) {
            equipProgress = 0.0f;
        }
        if (!rightHand && (this.leftHandAnimation > 0.01f || this.dontReequipLeft)) {
            equipProgress = 0.0f;
        }
        ms.pushPose();
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)player.getSkin().texture());
        float flip = rightHand ? 1.0f : -1.0f;
        float f1 = Mth.sqrt((float)event.getSwingProgress());
        float f2 = -0.3f * Mth.sin((float)(f1 * (float)Math.PI));
        float f3 = 0.4f * Mth.sin((float)(f1 * ((float)Math.PI * 2)));
        float f4 = -0.4f * Mth.sin((float)(event.getSwingProgress() * (float)Math.PI));
        float f5 = Mth.sin((float)(event.getSwingProgress() * event.getSwingProgress() * (float)Math.PI));
        float f6 = Mth.sin((float)(f1 * (float)Math.PI));
        ms.translate(flip * (f2 + 0.64f - 0.1f), f3 + -0.4f + equipProgress * -0.6f, f4 + -0.72f + 0.3f + recoil);
        ms.mulPose(Axis.YP.rotationDegrees(flip * 75.0f));
        ms.mulPose(Axis.YP.rotationDegrees(flip * f6 * 70.0f));
        ms.mulPose(Axis.ZP.rotationDegrees(flip * f5 * -20.0f));
        ms.translate(flip * -1.0f, 3.6f, 3.5f);
        ms.mulPose(Axis.ZP.rotationDegrees(flip * 120.0f));
        ms.mulPose(Axis.XP.rotationDegrees(200.0f));
        ms.mulPose(Axis.YP.rotationDegrees(flip * -135.0f));
        ms.translate(flip * 5.6f, 0.0f, 0.0f);
        ms.mulPose(Axis.YP.rotationDegrees(flip * 40.0f));
        this.transformHand(ms, flip, equipProgress, recoil, pt);
        if (rightHand) {
            playerrenderer.renderRightHand(ms, buffer, light, (AbstractClientPlayer)player);
        } else {
            playerrenderer.renderLeftHand(ms, buffer, light, (AbstractClientPlayer)player);
        }
        ms.popPose();
        ms.pushPose();
        ms.translate(flip * (f2 + 0.64f - 0.1f), f3 + -0.4f + equipProgress * -0.6f, f4 + -0.72f - 0.1f + recoil);
        ms.mulPose(Axis.YP.rotationDegrees(flip * f6 * 70.0f));
        ms.mulPose(Axis.ZP.rotationDegrees(flip * f5 * -20.0f));
        this.transformTool(ms, flip, equipProgress, recoil, pt);
        firstPersonRenderer.renderItem((LivingEntity)mc.player, heldItem, rightHand ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !rightHand, ms, buffer, light);
        ms.popPose();
        event.setCanceled(true);
    }

    public void dontAnimateItem(InteractionHand hand) {
        LocalPlayer player = Minecraft.getInstance().player;
        boolean rightHand = hand == InteractionHand.MAIN_HAND ^ player.getMainArm() == HumanoidArm.LEFT;
        this.dontReequipRight |= rightHand;
        this.dontReequipLeft |= !rightHand;
    }
}
