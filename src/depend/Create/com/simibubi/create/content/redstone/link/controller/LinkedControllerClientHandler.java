/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.LayeredDraw$Layer
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.redstone.link.controller;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlock;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerBindPacket;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerInputPacket;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItemRenderer;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerStopLecternPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.ControlsUtil;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LinkedControllerClientHandler {
    public static final LayeredDraw.Layer OVERLAY = LinkedControllerClientHandler::renderOverlay;
    public static Mode MODE = Mode.IDLE;
    public static int PACKET_RATE = 5;
    public static Collection<Integer> currentlyPressed = new HashSet<Integer>();
    private static BlockPos lecternPos;
    private static BlockPos selectedLocation;
    private static int packetCooldown;

    public static void toggleBindMode(BlockPos location) {
        if (MODE == Mode.IDLE) {
            MODE = Mode.BIND;
            selectedLocation = location;
        } else {
            MODE = Mode.IDLE;
            LinkedControllerClientHandler.onReset();
        }
    }

    public static void toggle() {
        if (MODE == Mode.IDLE) {
            MODE = Mode.ACTIVE;
            lecternPos = null;
        } else {
            MODE = Mode.IDLE;
            LinkedControllerClientHandler.onReset();
        }
    }

    public static void activateInLectern(BlockPos lecternAt) {
        if (MODE == Mode.IDLE) {
            MODE = Mode.ACTIVE;
            lecternPos = lecternAt;
        }
    }

    public static void deactivateInLectern() {
        if (MODE == Mode.ACTIVE && LinkedControllerClientHandler.inLectern()) {
            MODE = Mode.IDLE;
            LinkedControllerClientHandler.onReset();
        }
    }

    public static boolean inLectern() {
        return lecternPos != null;
    }

    protected static void onReset() {
        ControlsUtil.getControls().forEach(kb -> kb.setDown(ControlsUtil.isActuallyPressed(kb)));
        packetCooldown = 0;
        selectedLocation = BlockPos.ZERO;
        if (LinkedControllerClientHandler.inLectern()) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new LinkedControllerStopLecternPacket(lecternPos));
        }
        lecternPos = null;
        if (!currentlyPressed.isEmpty()) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new LinkedControllerInputPacket(currentlyPressed, false));
        }
        currentlyPressed.clear();
        LinkedControllerItemRenderer.resetButtons();
    }

    public static void tick() {
        LinkedControllerItemRenderer.tick();
        if (MODE == Mode.IDLE) {
            return;
        }
        if (packetCooldown > 0) {
            --packetCooldown;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ItemStack heldItem = player.getMainHandItem();
        if (player.isSpectator()) {
            MODE = Mode.IDLE;
            LinkedControllerClientHandler.onReset();
            return;
        }
        if (!(LinkedControllerClientHandler.inLectern() || AllItems.LINKED_CONTROLLER.isIn(heldItem) || AllItems.LINKED_CONTROLLER.isIn(heldItem = player.getOffhandItem()))) {
            MODE = Mode.IDLE;
            LinkedControllerClientHandler.onReset();
            return;
        }
        if (LinkedControllerClientHandler.inLectern() && ((LecternControllerBlock)AllBlocks.LECTERN_CONTROLLER.get()).getBlockEntityOptional((BlockGetter)mc.level, lecternPos).map(be -> !be.isUsedBy((Player)mc.player)).orElse(true).booleanValue()) {
            LinkedControllerClientHandler.deactivateInLectern();
            return;
        }
        if (mc.screen != null) {
            MODE = Mode.IDLE;
            LinkedControllerClientHandler.onReset();
            return;
        }
        if (InputConstants.isKeyDown((long)mc.getWindow().getWindow(), (int)256)) {
            MODE = Mode.IDLE;
            LinkedControllerClientHandler.onReset();
            return;
        }
        List<KeyMapping> controls = ControlsUtil.getControls();
        HashSet<Integer> pressedKeys = new HashSet<Integer>();
        for (int i = 0; i < controls.size(); ++i) {
            if (!ControlsUtil.isActuallyPressed(controls.get(i))) continue;
            pressedKeys.add(i);
        }
        HashSet<Integer> newKeys = new HashSet<Integer>(pressedKeys);
        Collection<Integer> releasedKeys = currentlyPressed;
        newKeys.removeAll(releasedKeys);
        releasedKeys.removeAll(pressedKeys);
        if (MODE == Mode.ACTIVE) {
            if (!releasedKeys.isEmpty()) {
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new LinkedControllerInputPacket(releasedKeys, false, lecternPos));
                AllSoundEvents.CONTROLLER_CLICK.playAt(player.level(), (Vec3i)player.blockPosition(), 1.0f, 0.5f, true);
            }
            if (!newKeys.isEmpty()) {
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new LinkedControllerInputPacket(newKeys, true, lecternPos));
                packetCooldown = PACKET_RATE;
                AllSoundEvents.CONTROLLER_CLICK.playAt(player.level(), (Vec3i)player.blockPosition(), 1.0f, 0.75f, true);
            }
            if (packetCooldown == 0 && !pressedKeys.isEmpty()) {
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new LinkedControllerInputPacket(pressedKeys, true, lecternPos));
                packetCooldown = PACKET_RATE;
            }
        }
        if (MODE == Mode.BIND) {
            Iterator iterator;
            VoxelShape shape = mc.level.getBlockState(selectedLocation).getShape((BlockGetter)mc.level, selectedLocation);
            if (!shape.isEmpty()) {
                Outliner.getInstance().showAABB((Object)"controller", shape.bounds().move(selectedLocation)).colored(12008493).lineWidth(0.0625f);
            }
            if ((iterator = newKeys.iterator()).hasNext()) {
                Integer integer = (Integer)iterator.next();
                LinkBehaviour linkBehaviour = BlockEntityBehaviour.get((BlockGetter)mc.level, selectedLocation, LinkBehaviour.TYPE);
                if (linkBehaviour != null) {
                    CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new LinkedControllerBindPacket(integer, selectedLocation));
                    CreateLang.translate("linked_controller.key_bound", controls.get(integer).getTranslatedKeyMessage().getString()).sendStatus((Player)mc.player);
                }
                MODE = Mode.IDLE;
            }
        }
        currentlyPressed = pressedKeys;
        controls.forEach(kb -> kb.setDown(false));
    }

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        int width1 = guiGraphics.guiWidth();
        int height1 = guiGraphics.guiHeight();
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) {
            return;
        }
        if (MODE != Mode.BIND) {
            return;
        }
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        Screen tooltipScreen = new Screen(CommonComponents.EMPTY){};
        tooltipScreen.init(mc, width1, height1);
        Object[] keys = new Object[6];
        List<KeyMapping> controls = ControlsUtil.getControls();
        for (int i = 0; i < controls.size(); ++i) {
            KeyMapping keyBinding = controls.get(i);
            keys[i] = keyBinding.getTranslatedKeyMessage().getString();
        }
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(CreateLang.translateDirect("linked_controller.bind_mode", new Object[0]).withStyle(ChatFormatting.GOLD));
        list.addAll(TooltipHelper.cutTextComponent((Component)CreateLang.translateDirect("linked_controller.press_keybind", keys), FontHelper.Palette.ALL_GRAY));
        int width = 0;
        int n = list.size();
        Objects.requireNonNull(mc.font);
        int height = n * 9;
        for (Component component : list) {
            width = Math.max(width, mc.font.width((FormattedText)component));
        }
        int x = width1 / 3 - width / 2;
        int n2 = height1 - height - 24;
        guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, list, x, n2);
        poseStack.popPose();
    }

    static {
        selectedLocation = BlockPos.ZERO;
    }

    public static enum Mode {
        IDLE,
        ACTIVE,
        BIND;

    }
}
