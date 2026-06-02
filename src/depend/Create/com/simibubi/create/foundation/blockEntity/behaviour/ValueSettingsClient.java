/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.LayeredDraw$Layer
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsInputHandler;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;
import java.util.List;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class ValueSettingsClient
implements LayeredDraw.Layer {
    private Minecraft mc = Minecraft.getInstance();
    public int interactHeldTicks = -1;
    public BlockPos interactHeldPos = null;
    public BehaviourType<?> interactHeldBehaviour = null;
    public InteractionHand interactHeldHand = null;
    public Direction interactHeldFace = null;
    public List<MutableComponent> lastHoverTip;
    public int hoverTicks;
    public int hoverWarmup;

    public void cancelIfWarmupAlreadyStarted(PlayerInteractEvent.RightClickBlock event) {
        if (this.interactHeldTicks != -1 && event.getPos().equals((Object)this.interactHeldPos)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    public void startInteractionWith(BlockPos pos, BehaviourType<?> behaviourType, InteractionHand hand, Direction side) {
        this.interactHeldTicks = 0;
        this.interactHeldPos = pos;
        this.interactHeldBehaviour = behaviourType;
        this.interactHeldHand = hand;
        this.interactHeldFace = side;
    }

    public void cancelInteraction() {
        this.interactHeldTicks = -1;
    }

    public void tick() {
        ValueSettingsBehaviour valueSettingBehaviour;
        BlockHitResult blockHitResult;
        if (this.hoverWarmup > 0) {
            --this.hoverWarmup;
        }
        if (this.hoverTicks > 0) {
            --this.hoverTicks;
        }
        if (this.interactHeldTicks == -1) {
            return;
        }
        LocalPlayer player = this.mc.player;
        if (!ValueSettingsInputHandler.canInteract((Player)player) || AllBlocks.CLIPBOARD.isIn(player.getMainHandItem())) {
            this.cancelInteraction();
            return;
        }
        HitResult hitResult = this.mc.hitResult;
        if (!(hitResult instanceof BlockHitResult) || !(blockHitResult = (BlockHitResult)hitResult).getBlockPos().equals((Object)this.interactHeldPos)) {
            this.cancelInteraction();
            return;
        }
        Object behaviour = BlockEntityBehaviour.get((BlockGetter)this.mc.level, this.interactHeldPos, this.interactHeldBehaviour);
        if (!(behaviour instanceof ValueSettingsBehaviour) || (valueSettingBehaviour = (ValueSettingsBehaviour)behaviour).bypassesInput(player.getMainHandItem()) || !valueSettingBehaviour.testHit(blockHitResult.getLocation())) {
            this.cancelInteraction();
            return;
        }
        if (!this.mc.options.keyUse.isDown()) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ValueSettingsPacket(this.interactHeldPos, 0, 0, this.interactHeldHand, blockHitResult, this.interactHeldFace, false, valueSettingBehaviour.netId()));
            valueSettingBehaviour.onShortInteract((Player)player, this.interactHeldHand, this.interactHeldFace, blockHitResult);
            this.cancelInteraction();
            return;
        }
        if (this.interactHeldTicks > 3) {
            player.swinging = false;
        }
        if (this.interactHeldTicks++ < 5) {
            return;
        }
        ScreenOpener.open((Screen)new ValueSettingsScreen(this.interactHeldPos, valueSettingBehaviour.createBoard((Player)player, blockHitResult), valueSettingBehaviour.getValueSettings(), valueSettingBehaviour::newSettingHovered, valueSettingBehaviour.netId()));
        this.interactHeldTicks = -1;
    }

    public void showHoverTip(List<MutableComponent> tip) {
        if (this.mc.screen != null) {
            return;
        }
        if (this.hoverWarmup < 6) {
            this.hoverWarmup += 2;
            return;
        }
        ++this.hoverWarmup;
        this.hoverTicks = this.hoverTicks == 0 ? 11 : Math.max(this.hoverTicks, 6);
        this.lastHoverTip = tip;
    }

    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || !ValueSettingsInputHandler.canInteract((Player)mc.player)) {
            return;
        }
        if (this.hoverTicks == 0 || this.lastHoverTip == null) {
            return;
        }
        int x = guiGraphics.guiWidth() / 2;
        int y = guiGraphics.guiHeight() - 75 - this.lastHoverTip.size() * 12;
        float alpha = this.hoverTicks > 5 ? (float)(11 - this.hoverTicks) / 5.0f : Math.min(1.0f, (float)this.hoverTicks / 5.0f);
        Color color = new Color(0xFFFFFF);
        Color titleColor = new Color(16505981);
        color.setAlpha(alpha);
        titleColor.setAlpha(alpha);
        for (int i = 0; i < this.lastHoverTip.size(); ++i) {
            MutableComponent mutableComponent = this.lastHoverTip.get(i);
            guiGraphics.drawString(mc.font, (Component)mutableComponent, x - mc.font.width((FormattedText)mutableComponent) / 2, y, (i == 0 ? titleColor : color).getRGB());
            y += 12;
        }
    }
}
