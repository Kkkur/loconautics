/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.redstone.link.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LinkedControllerItemRenderer
extends CustomRenderedItemModelRenderer {
    protected static final PartialModel POWERED = PartialModel.of((ResourceLocation)Create.asResource("item/linked_controller/powered"));
    protected static final PartialModel BUTTON = PartialModel.of((ResourceLocation)Create.asResource("item/linked_controller/button"));
    static LerpedFloat equipProgress = LerpedFloat.linear().startWithValue(0.0);
    static List<LerpedFloat> buttons = new ArrayList<LerpedFloat>(6);

    static void tick() {
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        boolean active = LinkedControllerClientHandler.MODE != LinkedControllerClientHandler.Mode.IDLE;
        equipProgress.chase(active ? 1.0 : 0.0, (double)0.2f, LerpedFloat.Chaser.EXP);
        equipProgress.tickChaser();
        if (!active) {
            return;
        }
        for (int i = 0; i < buttons.size(); ++i) {
            LerpedFloat lerpedFloat = buttons.get(i);
            lerpedFloat.chase(LinkedControllerClientHandler.currentlyPressed.contains(i) ? 1.0 : 0.0, (double)0.4f, LerpedFloat.Chaser.EXP);
            lerpedFloat.tickChaser();
        }
    }

    static void resetButtons() {
        for (LerpedFloat button : buttons) {
            button.startWithValue(0.0);
        }
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        LinkedControllerItemRenderer.renderNormal(stack, model, renderer, transformType, ms, light);
    }

    protected static void renderNormal(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, int light) {
        LinkedControllerItemRenderer.render(stack, model, renderer, transformType, ms, light, RenderType.NORMAL, false, false);
    }

    public static void renderInLectern(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, int light, boolean active, boolean renderDepression) {
        LinkedControllerItemRenderer.render(stack, model, renderer, transformType, ms, light, RenderType.LECTERN, active, renderDepression);
    }

    protected static void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, int light, RenderType renderType, boolean active, boolean renderDepression) {
        float pt = AnimationTickHolder.getPartialTicks();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        ms.pushPose();
        if (renderType == RenderType.NORMAL) {
            boolean noControllerInMain;
            Minecraft mc = Minecraft.getInstance();
            boolean rightHanded = mc.options.mainHand().get() == HumanoidArm.RIGHT;
            ItemDisplayContext mainHand = rightHanded ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            ItemDisplayContext offHand = rightHanded ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
            active = false;
            boolean bl = noControllerInMain = !AllItems.LINKED_CONTROLLER.isIn(mc.player.getMainHandItem());
            if (transformType == mainHand || transformType == offHand && noControllerInMain) {
                float equip = equipProgress.getValue(pt);
                int handModifier = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1;
                msr.translate(0.0f, equip / 4.0f, equip / 4.0f * (float)handModifier);
                msr.rotateYDegrees(equip * -30.0f * (float)handModifier);
                msr.rotateZDegrees(equip * -30.0f);
                active = true;
            }
            if (transformType == ItemDisplayContext.GUI) {
                if (stack == mc.player.getMainHandItem()) {
                    active = true;
                }
                if (stack == mc.player.getOffhandItem() && noControllerInMain) {
                    active = true;
                }
            }
            active &= LinkedControllerClientHandler.MODE != LinkedControllerClientHandler.Mode.IDLE;
            renderDepression = true;
        }
        renderer.render(active ? POWERED.get() : model.getOriginalModel(), light);
        if (!active) {
            ms.popPose();
            return;
        }
        BakedModel button = BUTTON.get();
        float s = 0.0625f;
        float b = s * -0.75f;
        int index = 0;
        if (renderType == RenderType.NORMAL && LinkedControllerClientHandler.MODE == LinkedControllerClientHandler.Mode.BIND) {
            int i = (int)Mth.lerp((float)((Mth.sin((float)(AnimationTickHolder.getRenderTime() / 4.0f)) + 1.0f) / 2.0f), (float)5.0f, (float)15.0f);
            light = i << 20;
        }
        ms.pushPose();
        msr.translate(2.0f * s, 0.0f, 8.0f * s);
        LinkedControllerItemRenderer.renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        msr.translate(4.0f * s, 0.0f, 0.0f);
        LinkedControllerItemRenderer.renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        msr.translate(-2.0f * s, 0.0f, 2.0f * s);
        LinkedControllerItemRenderer.renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        msr.translate(0.0f, 0.0f, -4.0f * s);
        LinkedControllerItemRenderer.renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        ms.popPose();
        msr.translate(3.0f * s, 0.0f, 3.0f * s);
        LinkedControllerItemRenderer.renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        msr.translate(2.0f * s, 0.0f, 0.0f);
        LinkedControllerItemRenderer.renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
        ms.popPose();
    }

    protected static void renderButton(PartialItemModelRenderer renderer, PoseStack ms, int light, float pt, BakedModel button, float b, int index, boolean renderDepression) {
        ms.pushPose();
        if (renderDepression) {
            float depression = b * buttons.get(index).getValue(pt);
            ms.translate(0.0f, depression, 0.0f);
        }
        renderer.renderSolid(button, light);
        ms.popPose();
    }

    static {
        for (int i = 0; i < 6; ++i) {
            buttons.add(LerpedFloat.linear().startWithValue(0.0));
        }
    }

    protected static enum RenderType {
        NORMAL,
        LECTERN;

    }
}
