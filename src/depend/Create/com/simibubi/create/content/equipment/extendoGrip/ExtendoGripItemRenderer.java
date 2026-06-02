/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.equipment.extendoGrip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripRenderHandler;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ExtendoGripItemRenderer
extends CustomRenderedItemModelRenderer {
    protected static final PartialModel COG = PartialModel.of((ResourceLocation)Create.asResource("item/extendo_grip/cog"));
    protected static final PartialModel THIN_SHORT = PartialModel.of((ResourceLocation)Create.asResource("item/extendo_grip/thin_short"));
    protected static final PartialModel WIDE_SHORT = PartialModel.of((ResourceLocation)Create.asResource("item/extendo_grip/wide_short"));
    protected static final PartialModel THIN_LONG = PartialModel.of((ResourceLocation)Create.asResource("item/extendo_grip/thin_long"));
    protected static final PartialModel WIDE_LONG = PartialModel.of((ResourceLocation)Create.asResource("item/extendo_grip/wide_long"));
    private static final Vec3 ROTATION_OFFSET = new Vec3(0.0, 0.5, 0.5);
    private static final Vec3 COG_ROTATION_OFFSET = new Vec3(0.0, 0.0625, 0.0);

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        boolean rightHand;
        PoseTransformStack stacker = TransformStack.of((PoseStack)ms);
        float animation = 0.25f;
        boolean leftHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        boolean bl = rightHand = transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        if (leftHand || rightHand) {
            animation = Mth.lerp((float)AnimationTickHolder.getPartialTicks(), (float)ExtendoGripRenderHandler.lastMainHandAnimation, (float)ExtendoGripRenderHandler.mainHandAnimation);
        }
        animation = animation * animation * animation;
        float extensionAngle = Mth.lerp((float)animation, (float)24.0f, (float)156.0f);
        float halfAngle = extensionAngle / 2.0f;
        float oppositeAngle = 180.0f - extensionAngle;
        renderer.renderSolid(model.getOriginalModel(), light);
        ms.pushPose();
        ms.translate(0.0f, 0.0625f, -0.4375f);
        ms.scale(1.0f, 1.0f, 1.0f + animation);
        ms.pushPose();
        ((PoseTransformStack)stacker.rotateXDegrees(-halfAngle)).translate(ROTATION_OFFSET);
        renderer.renderSolid(THIN_SHORT.get(), light);
        stacker.translateBack(ROTATION_OFFSET);
        ms.translate(0.0f, 0.34375f, 0.0f);
        ((PoseTransformStack)stacker.rotateXDegrees(-oppositeAngle)).translate(ROTATION_OFFSET);
        renderer.renderSolid(WIDE_LONG.get(), light);
        stacker.translateBack(ROTATION_OFFSET);
        ms.translate(0.0f, 0.6875f, 0.0f);
        ((PoseTransformStack)stacker.rotateXDegrees(oppositeAngle)).translate(ROTATION_OFFSET);
        ms.translate(0.0f, 0.03125f, 0.0f);
        renderer.renderSolid(THIN_SHORT.get(), light);
        stacker.translateBack(ROTATION_OFFSET);
        ms.popPose();
        ms.pushPose();
        ((PoseTransformStack)stacker.rotateXDegrees(-180.0f + halfAngle)).translate(ROTATION_OFFSET);
        renderer.renderSolid(WIDE_SHORT.get(), light);
        stacker.translateBack(ROTATION_OFFSET);
        ms.translate(0.0f, 0.34375f, 0.0f);
        ((PoseTransformStack)stacker.rotateXDegrees(oppositeAngle)).translate(ROTATION_OFFSET);
        renderer.renderSolid(THIN_LONG.get(), light);
        stacker.translateBack(ROTATION_OFFSET);
        ms.translate(0.0f, 0.6875f, 0.0f);
        ((PoseTransformStack)stacker.rotateXDegrees(-oppositeAngle)).translate(ROTATION_OFFSET);
        ms.translate(0.0f, 0.03125f, 0.0f);
        renderer.renderSolid(WIDE_SHORT.get(), light);
        stacker.translateBack(ROTATION_OFFSET);
        ms.translate(0.0f, 0.34375f, 0.0f);
        ((PoseTransformStack)stacker.rotateXDegrees(180.0f - halfAngle)).rotateYDegrees(180.0f);
        ms.translate(0.0f, 0.0f, -0.25f);
        ms.scale(1.0f, 1.0f, 1.0f / (1.0f + animation));
        renderer.renderSolid(leftHand || rightHand ? ExtendoGripRenderHandler.pose.get() : AllPartialModels.DEPLOYER_HAND_POINTING.get(), light);
        ms.popPose();
        ms.popPose();
        ms.pushPose();
        float angle = AnimationTickHolder.getRenderTime() * -2.0f;
        if (leftHand || rightHand) {
            angle += 360.0f * animation;
        }
        ((PoseTransformStack)((PoseTransformStack)stacker.translate(COG_ROTATION_OFFSET)).rotateZDegrees(angle %= 360.0f)).translateBack(COG_ROTATION_OFFSET);
        renderer.renderSolid(COG.get(), light);
        ms.popPose();
    }
}
