/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.zapper.ZapperItemRenderer;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class WorldshaperItemRenderer
extends ZapperItemRenderer {
    protected static final PartialModel CORE = PartialModel.of((ResourceLocation)Create.asResource("item/handheld_worldshaper/core"));
    protected static final PartialModel CORE_GLOW = PartialModel.of((ResourceLocation)Create.asResource("item/handheld_worldshaper/core_glow"));
    protected static final PartialModel ACCELERATOR = PartialModel.of((ResourceLocation)Create.asResource("item/handheld_worldshaper/accelerator"));

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.render(stack, model, renderer, transformType, ms, buffer, light, overlay);
        float pt = AnimationTickHolder.getPartialTicks();
        float worldTime = AnimationTickHolder.getRenderTime() / 20.0f;
        renderer.renderSolid(model.getOriginalModel(), light);
        LocalPlayer player = Minecraft.getInstance().player;
        boolean leftHanded = player.getMainArm() == HumanoidArm.LEFT;
        boolean mainHand = player.getMainHandItem() == stack;
        boolean offHand = player.getOffhandItem() == stack;
        float animation = this.getAnimationProgress(pt, leftHanded, mainHand);
        float multiplier = mainHand || offHand ? animation : Mth.sin((float)(worldTime * 5.0f));
        int lightItensity = (int)(15.0f * Mth.clamp((float)multiplier, (float)0.0f, (float)1.0f));
        int glowLight = LightTexture.pack((int)lightItensity, (int)Math.max(lightItensity, 4));
        renderer.renderSolidGlowing(CORE.get(), glowLight);
        renderer.renderGlowing(CORE_GLOW.get(), glowLight);
        float angle = worldTime * -25.0f;
        if (mainHand || offHand) {
            angle += 360.0f * animation;
        }
        float offset = -0.155f;
        ms.translate(0.0f, offset, 0.0f);
        ms.mulPose(Axis.ZP.rotationDegrees(angle %= 360.0f));
        ms.translate(0.0f, -offset, 0.0f);
        renderer.render(ACCELERATOR.get(), light);
    }
}
