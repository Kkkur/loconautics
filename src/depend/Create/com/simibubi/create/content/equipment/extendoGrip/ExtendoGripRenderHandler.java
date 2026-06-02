/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.ItemInHandRenderer
 *  net.minecraft.client.renderer.entity.player.PlayerRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.ClientHooks
 *  net.neoforged.neoforge.client.event.RenderHandEvent
 */
package com.simibubi.create.content.equipment.extendoGrip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderHandEvent;

@EventBusSubscriber(value={Dist.CLIENT})
public class ExtendoGripRenderHandler {
    public static float mainHandAnimation;
    public static float lastMainHandAnimation;
    public static PartialModel pose;

    public static void tick() {
        lastMainHandAnimation = mainHandAnimation;
        mainHandAnimation *= Mth.clamp((float)mainHandAnimation, (float)0.8f, (float)0.99f);
        pose = AllPartialModels.DEPLOYER_HAND_PUNCHING;
        if (!AllItems.EXTENDO_GRIP.isIn(ExtendoGripRenderHandler.getRenderedOffHandStack())) {
            return;
        }
        ItemStack main = ExtendoGripRenderHandler.getRenderedMainHandStack();
        if (main.isEmpty()) {
            return;
        }
        if (!(main.getItem() instanceof BlockItem)) {
            return;
        }
        if (!Minecraft.getInstance().getItemRenderer().getModel(main, null, null, 0).isGui3d()) {
            return;
        }
        pose = AllPartialModels.DEPLOYER_HAND_HOLDING;
    }

    @SubscribeEvent
    public static void onRenderPlayerHand(RenderHandEvent event) {
        boolean notInOffhand;
        ItemStack heldItem = event.getItemStack();
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        boolean rightHand = event.getHand() == InteractionHand.MAIN_HAND ^ player.getMainArm() == HumanoidArm.LEFT;
        ItemStack offhandItem = ExtendoGripRenderHandler.getRenderedOffHandStack();
        boolean bl = notInOffhand = !AllItems.EXTENDO_GRIP.isIn(offhandItem);
        if (notInOffhand && !AllItems.EXTENDO_GRIP.isIn(heldItem)) {
            return;
        }
        PoseStack ms = event.getPoseStack();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        LocalPlayer abstractclientplayerentity = mc.player;
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)abstractclientplayerentity.getSkin().texture());
        float flip = rightHand ? 1.0f : -1.0f;
        float swingProgress = event.getSwingProgress();
        boolean blockItem = heldItem.getItem() instanceof BlockItem;
        float equipProgress = blockItem ? 0.0f : event.getEquipProgress() / 4.0f;
        ms.pushPose();
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            if (1.0f - swingProgress > mainHandAnimation && swingProgress > 0.0f) {
                mainHandAnimation = 0.95f;
            }
            float animation = Mth.lerp((float)AnimationTickHolder.getPartialTicks(), (float)lastMainHandAnimation, (float)mainHandAnimation);
            animation = animation * animation * animation;
            ms.translate(flip * 0.54f, -0.4f + equipProgress * -0.6f, -0.41999996f);
            ms.pushPose();
            msr.rotateYDegrees(flip * 75.0f);
            ms.translate(flip * -1.0f, 3.6f, 3.5f);
            ((PoseTransformStack)((PoseTransformStack)msr.rotateZDegrees(flip * 120.0f)).rotateXDegrees(200.0f)).rotateYDegrees(flip * -135.0f);
            ms.translate(flip * 5.6f, 0.0f, 0.0f);
            msr.rotateYDegrees(flip * 40.0f);
            ms.translate(flip * 0.05f, -0.3f, -0.3f);
            PlayerRenderer playerrenderer = (PlayerRenderer)mc.getEntityRenderDispatcher().getRenderer((Entity)player);
            if (rightHand) {
                playerrenderer.renderRightHand(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), (AbstractClientPlayer)player);
            } else {
                playerrenderer.renderLeftHand(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), (AbstractClientPlayer)player);
            }
            ms.popPose();
            ms.pushPose();
            ms.translate(flip * -0.1f, 0.0f, -0.3f);
            ItemInHandRenderer firstPersonRenderer = mc.getEntityRenderDispatcher().getItemInHandRenderer();
            ItemDisplayContext transform = rightHand ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            firstPersonRenderer.renderItem((LivingEntity)mc.player, notInOffhand ? heldItem : offhandItem, transform, !rightHand, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
            if (!notInOffhand) {
                ClientHooks.handleCameraTransforms((PoseStack)ms, (BakedModel)mc.getItemRenderer().getModel(offhandItem, null, null, 0), (ItemDisplayContext)transform, (!rightHand ? 1 : 0) != 0);
                ms.translate(flip * -0.05f, 0.15f, -1.2f);
                ms.translate(0.0f, 0.0f, -animation * 2.25f);
                if (blockItem && mc.getItemRenderer().getModel(heldItem, null, null, 0).isGui3d()) {
                    msr.rotateYDegrees(flip * 45.0f);
                    ms.translate(flip * 0.15f, -0.15f, -0.05f);
                    ms.scale(1.25f, 1.25f, 1.25f);
                }
                firstPersonRenderer.renderItem((LivingEntity)mc.player, heldItem, transform, !rightHand, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
            }
            ms.popPose();
        }
        ms.popPose();
        event.setCanceled(true);
    }

    private static ItemStack getRenderedMainHandStack() {
        return Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().mainHandItem;
    }

    private static ItemStack getRenderedOffHandStack() {
        return Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().offHandItem;
    }

    static {
        pose = AllPartialModels.DEPLOYER_HAND_PUNCHING;
    }
}
