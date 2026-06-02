/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.LayeredDraw$Layer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.StringUtil
 *  net.minecraft.world.effect.MobEffectUtil
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.block.Blocks
 */
package com.simibubi.create.content.equipment.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import java.util.List;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

public class RemainingAirOverlay
implements LayeredDraw.Layer {
    public static final RemainingAirOverlay INSTANCE = new RemainingAirOverlay();

    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        boolean canBreathe;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        if (player.isCreative()) {
            return;
        }
        if (!player.getPersistentData().contains("VisualBacktankAir")) {
            return;
        }
        boolean isAir = player.getEyeInFluidType().isAir() || player.level().getBlockState(BlockPos.containing((double)player.getX(), (double)player.getEyeY(), (double)player.getZ())).is(Blocks.BUBBLE_COLUMN);
        boolean bl = canBreathe = !player.canDrownInFluidType(player.getEyeInFluidType()) || MobEffectUtil.hasWaterBreathing((LivingEntity)player) || player.getAbilities().invulnerable;
        if ((isAir || canBreathe) && !player.isInLava()) {
            return;
        }
        int timeLeft = player.getPersistentData().getInt("VisualBacktankAir");
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        ItemStack backtank = RemainingAirOverlay.getDisplayedBacktank(player);
        poseStack.translate((float)(guiGraphics.guiWidth() / 2 + 90), (float)(guiGraphics.guiHeight() - 53 + (backtank.has(DataComponents.FIRE_RESISTANT) ? 9 : 0)), 0.0f);
        MutableComponent text = Component.literal((String)StringUtil.formatTickDuration((int)(Math.max(0, timeLeft - 1) * 20), (float)mc.level.tickRateManager().tickrate()));
        GuiGameElement.of((ItemStack)backtank).at(0.0f, 0.0f).render(guiGraphics);
        int color = -1;
        if (timeLeft < 60 && timeLeft % 2 == 0) {
            color = Color.mixColors((int)-65536, (int)color, (float)Math.max((float)timeLeft / 60.0f, 0.25f));
        }
        guiGraphics.drawString(mc.font, (Component)text, 16, 5, color);
        poseStack.popPose();
    }

    public static ItemStack getDisplayedBacktank(LocalPlayer player) {
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir((LivingEntity)player);
        if (!backtanks.isEmpty()) {
            return backtanks.getFirst();
        }
        return AllItems.COPPER_BACKTANK.asStack();
    }
}
