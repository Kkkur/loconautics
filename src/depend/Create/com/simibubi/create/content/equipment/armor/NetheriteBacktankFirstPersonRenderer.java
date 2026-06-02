/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.player.PlayerRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.RenderArmEvent
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderArmEvent;

@EventBusSubscriber(value={Dist.CLIENT})
public class NetheriteBacktankFirstPersonRenderer {
    private static final ResourceLocation BACKTANK_ARMOR_LOCATION = Create.asResource("textures/models/armor/netherite_diving_arm.png");
    private static boolean rendererActive = false;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        rendererActive = mc.player != null && AllItems.NETHERITE_BACKTANK.isIn(mc.player.getItemBySlot(EquipmentSlot.CHEST));
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onRenderPlayerHand(RenderArmEvent event) {
        if (!rendererActive) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        MultiBufferSource buffer = event.getMultiBufferSource();
        EntityRenderer entityRenderer = mc.getEntityRenderDispatcher().getRenderer((Entity)player);
        if (!(entityRenderer instanceof PlayerRenderer)) {
            return;
        }
        PlayerRenderer pr = (PlayerRenderer)entityRenderer;
        PlayerModel model = (PlayerModel)pr.getModel();
        model.attackTime = 0.0f;
        model.crouching = false;
        model.swimAmount = 0.0f;
        model.setupAnim((LivingEntity)player, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        ModelPart armPart = event.getArm() == HumanoidArm.LEFT ? model.leftSleeve : model.rightSleeve;
        armPart.xRot = 0.0f;
        armPart.render(event.getPoseStack(), buffer.getBuffer(RenderType.entitySolid((ResourceLocation)BACKTANK_ARMOR_LOCATION)), 0xF000F0, OverlayTexture.NO_OVERLAY);
        event.setCanceled(true);
    }
}
