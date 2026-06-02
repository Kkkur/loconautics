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
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.client.IItemDecorator
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

public class PotatoCannonItemRenderer
extends CustomRenderedItemModelRenderer {
    public static final IItemDecorator DECORATOR = (guiGraphics, font, stack, xOffset, yOffset) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        PotatoCannonItem.Ammo ammo = PotatoCannonItem.getAmmo((Player)player, stack);
        if (ammo == null || AllItems.POTATO_CANNON.is((Object)ammo.stack())) {
            return false;
        }
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate((float)xOffset, (float)(yOffset + 8), 100.0f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        guiGraphics.renderItem(ammo.stack(), 0, 0);
        poseStack.popPose();
        return false;
    };
    protected static final PartialModel COG = PartialModel.of((ResourceLocation)Create.asResource("item/potato_cannon/cog"));

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(model.getOriginalModel(), light);
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        float angle = AnimationTickHolder.getRenderTime() * -2.5f;
        if (player != null) {
            boolean inOffHand;
            boolean inMainHand = player.getMainHandItem() == stack;
            boolean bl = inOffHand = player.getOffhandItem() == stack;
            if (inMainHand || inOffHand) {
                boolean leftHanded = player.getMainArm() == HumanoidArm.LEFT;
                float speed = CreateClient.POTATO_CANNON_RENDER_HANDLER.getAnimation(inMainHand ^ leftHanded, AnimationTickHolder.getPartialTicks());
                angle += 360.0f * Mth.clamp((float)(speed * 5.0f), (float)0.0f, (float)1.0f);
            }
        }
        float offset = 0.03125f;
        ms.pushPose();
        ms.translate(0.0f, offset, 0.0f);
        ms.mulPose(Axis.ZP.rotationDegrees(angle %= 360.0f));
        ms.translate(0.0f, -offset, 0.0f);
        renderer.render(COG.get(), light);
        ms.popPose();
    }
}
