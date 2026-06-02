/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.CardboardArmorHandler;
import com.simibubi.create.foundation.mixin.accessor.GuiAccessor;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class CardboardArmorStealthOverlay
extends Gui
implements IClientItemExtensions {
    private static final ResourceLocation PACKAGE_BLUR_LOCATION = Create.asResource("textures/misc/package_blur.png");
    private static LerpedFloat opacity = LerpedFloat.linear().startWithValue(0.0).chase(0.0, 0.25, LerpedFloat.Chaser.EXP);

    public CardboardArmorStealthOverlay() {
        super(Minecraft.getInstance());
    }

    public static void clientTick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        opacity.tickChaser();
        opacity.updateChaseTarget(CardboardArmorHandler.testForStealth((Entity)player) ? 1.0f : 0.0f);
    }

    public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        float value = opacity.getValue(partialTick);
        if (value == 0.0f) {
            return;
        }
        ((GuiAccessor)((Object)this)).create$renderTextureOverlay(new GuiGraphics(mc, mc.renderBuffers().bufferSource()), PACKAGE_BLUR_LOCATION, value);
    }
}
