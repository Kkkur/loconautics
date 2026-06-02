/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.gui.element.BoxElement
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.outliner.Outline
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.outliner.Outliner$OutlineEntry
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.MouseHandler
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.LayeredDraw$Layer
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 */
package com.simibubi.create.content.equipment.goggles;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.equipment.goggles.IHaveCustomOverlayIcon;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.api.equipment.goggles.IProxyHoveringInformation;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import com.simibubi.create.foundation.mixin.accessor.MouseHandlerAccessor;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CClient;
import java.util.ArrayList;
import java.util.Map;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.element.BoxElement;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class GoggleOverlayRenderer {
    public static final LayeredDraw.Layer OVERLAY = GoggleOverlayRenderer::renderOverlay;
    private static final Map<Object, Outliner.OutlineEntry> outlines = Outliner.getInstance().getOutlines();
    public static int hoverTicks = 0;
    public static BlockPos lastHovered = null;

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Color colorBorderBot;
        boolean exceptionAdded;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        HitResult objectMouseOver = mc.hitResult;
        if (!(objectMouseOver instanceof BlockHitResult)) {
            lastHovered = null;
            hoverTicks = 0;
            return;
        }
        BlockHitResult result = (BlockHitResult)objectMouseOver;
        for (Outliner.OutlineEntry entry : outlines.values()) {
            Outline outline;
            if (!entry.isAlive() || !((outline = entry.getOutline()) instanceof ValueBox) || ((ValueBox)outline).isPassive) continue;
            return;
        }
        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        int prevHoverTicks = hoverTicks++;
        lastHovered = pos;
        pos = GoggleOverlayRenderer.proxiedOverlayPosition((Level)world, pos);
        BlockEntity be = world.getBlockEntity(pos);
        boolean wearingGoggles = GogglesItem.isWearingGoggles((Player)mc.player);
        boolean isShifting = mc.player.isShiftKeyDown();
        boolean hasGoggleInformation = be instanceof IHaveGoggleInformation;
        boolean hasHoveringInformation = be instanceof IHaveHoveringInformation;
        boolean goggleAddedInformation = false;
        boolean hoverAddedInformation = false;
        ItemStack item = AllItems.GOGGLES.asStack();
        ArrayList<Component> tooltip = new ArrayList<Component>();
        if (be instanceof IHaveCustomOverlayIcon) {
            IHaveCustomOverlayIcon customOverlayIcon = (IHaveCustomOverlayIcon)be;
            item = customOverlayIcon.getIcon(isShifting);
        }
        if (hasGoggleInformation && wearingGoggles) {
            IHaveGoggleInformation gte = (IHaveGoggleInformation)be;
            goggleAddedInformation = gte.addToGoggleTooltip(tooltip, isShifting);
        }
        if (hasHoveringInformation) {
            if (!tooltip.isEmpty()) {
                tooltip.add(CommonComponents.EMPTY);
            }
            IHaveHoveringInformation hte = (IHaveHoveringInformation)be;
            hoverAddedInformation = hte.addToTooltip(tooltip, isShifting);
            if (goggleAddedInformation && !hoverAddedInformation) {
                tooltip.remove(tooltip.size() - 1);
            }
        }
        if (be instanceof IDisplayAssemblyExceptions && (exceptionAdded = ((IDisplayAssemblyExceptions)be).addExceptionToTooltip(tooltip))) {
            hasHoveringInformation = true;
            hoverAddedInformation = true;
        }
        if (!hasHoveringInformation) {
            hasHoveringInformation = hoverAddedInformation = TrainRelocator.addToTooltip(tooltip, isShifting);
            if (hoverAddedInformation) {
                hoverTicks = prevHoverTicks + 1;
            }
        }
        if (hasGoggleInformation && !goggleAddedInformation && hasHoveringInformation && !hoverAddedInformation) {
            hoverTicks = 0;
            return;
        }
        BlockState state = world.getBlockState(pos);
        if (wearingGoggles && AllBlocks.PISTON_EXTENSION_POLE.has(state)) {
            Direction[] directions = Iterate.directionsInAxis((Direction.Axis)((Direction)state.getValue((Property)PistonExtensionPoleBlock.FACING)).getAxis());
            int poles = 1;
            boolean pistonFound = false;
            for (Direction dir : directions) {
                int attachedPoles = PistonExtensionPoleBlock.PlacementHelper.get().attachedPoles((Level)world, pos, dir);
                poles += attachedPoles;
                pistonFound |= world.getBlockState(pos.relative(dir, attachedPoles + 1)).getBlock() instanceof MechanicalPistonBlock;
            }
            if (!pistonFound) {
                hoverTicks = 0;
                return;
            }
            if (!tooltip.isEmpty()) {
                tooltip.add(CommonComponents.EMPTY);
            }
            CreateLang.translate("gui.goggles.pole_length", new Object[0]).text(" " + poles).forGoggles(tooltip);
        }
        if (tooltip.isEmpty()) {
            hoverTicks = 0;
            return;
        }
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        int tooltipTextWidth = 0;
        for (FormattedText formattedText : tooltip) {
            int textLineWidth = mc.font.width(formattedText);
            if (textLineWidth <= tooltipTextWidth) continue;
            tooltipTextWidth = textLineWidth;
        }
        int tooltipHeight = 8;
        if (tooltip.size() > 1) {
            tooltipHeight += 2;
            tooltipHeight += (tooltip.size() - 1) * 10;
        }
        int n = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        CClient cfg = AllConfigs.client();
        int posX = n / 2 + (Integer)cfg.overlayOffsetX.get();
        int posY = height / 2 + (Integer)cfg.overlayOffsetY.get();
        posX = Math.min(posX, n - tooltipTextWidth - 20);
        posY = Math.min(posY, height - tooltipHeight - 20);
        float fade = Mth.clamp((float)(((float)hoverTicks + deltaTracker.getGameTimeDeltaPartialTick(false)) / 24.0f), (float)0.0f, (float)1.0f);
        Boolean useCustom = (Boolean)cfg.overlayCustomColor.get();
        Color colorBackground = useCustom != false ? new Color(((Integer)cfg.overlayBackgroundColor.get()).intValue()) : BoxElement.COLOR_VANILLA_BACKGROUND.scaleAlpha(0.75f);
        Color colorBorderTop = useCustom != false ? new Color(((Integer)cfg.overlayBorderColorTop.get()).intValue()) : ((Color)BoxElement.COLOR_VANILLA_BORDER.getFirst()).copy();
        Color color = colorBorderBot = useCustom != false ? new Color(((Integer)cfg.overlayBorderColorBot.get()).intValue()) : ((Color)BoxElement.COLOR_VANILLA_BORDER.getSecond()).copy();
        if (fade < 1.0f) {
            poseStack.translate(Math.pow(1.0f - fade, 3.0) * (double)Math.signum((float)((Integer)cfg.overlayOffsetX.get()).intValue() + 0.5f) * 8.0, 0.0, 0.0);
            colorBackground.scaleAlpha(fade);
            colorBorderTop.scaleAlpha(fade);
            colorBorderBot.scaleAlpha(fade);
        }
        GuiGameElement.of((ItemStack)item).at((float)(posX + 10), (float)(posY - 16), 450.0f).render(guiGraphics);
        if (!Mods.MODERNUI.isLoaded()) {
            RemovedGuiUtils.drawHoveringText(guiGraphics, tooltip, posX, posY, n, height, -1, colorBackground.getRGB(), colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);
            poseStack.popPose();
            return;
        }
        MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
        Window window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
        double cursorX = mouseHandler.xpos();
        double cursorY = mouseHandler.ypos();
        ((MouseHandlerAccessor)mouseHandler).create$setXPos((double)Math.round(cursorX / guiScale) * guiScale);
        ((MouseHandlerAccessor)mouseHandler).create$setYPos((double)Math.round(cursorY / guiScale) * guiScale);
        RemovedGuiUtils.drawHoveringText(guiGraphics, tooltip, posX, posY, n, height, -1, colorBackground.getRGB(), colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);
        ((MouseHandlerAccessor)mouseHandler).create$setXPos(cursorX);
        ((MouseHandlerAccessor)mouseHandler).create$setYPos(cursorY);
        poseStack.popPose();
    }

    public static BlockPos proxiedOverlayPosition(Level level, BlockPos pos) {
        BlockState targetedState = level.getBlockState(pos);
        Block block = targetedState.getBlock();
        if (block instanceof IProxyHoveringInformation) {
            IProxyHoveringInformation proxy = (IProxyHoveringInformation)block;
            return proxy.getInformationSource(level, pos, targetedState);
        }
        return pos;
    }
}
