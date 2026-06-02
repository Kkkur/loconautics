/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.LayeredDraw$Layer
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 */
package com.simibubi.create.content.equipment.toolbox;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.equipment.toolbox.RadialToolboxMenu;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxEquipPacket;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import java.util.Comparator;
import java.util.List;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ToolboxHandlerClient {
    public static final LayeredDraw.Layer OVERLAY = ToolboxHandlerClient::renderOverlay;
    static int COOLDOWN = 0;

    public static void clientTick() {
        if (COOLDOWN > 0 && !AllKeys.TOOLBELT.isPressed()) {
            --COOLDOWN;
        }
    }

    public static boolean onPickItem() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return false;
        }
        Level level = player.level();
        HitResult hitResult = mc.hitResult;
        if (hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
            return false;
        }
        if (player.isCreative()) {
            return false;
        }
        ItemStack result = ItemStack.EMPTY;
        List<ToolboxBlockEntity> toolboxes = ToolboxHandler.getNearest((LevelAccessor)player.level(), (Player)player, 8);
        if (toolboxes.isEmpty()) {
            return false;
        }
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult)hitResult).getBlockPos();
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) {
                return false;
            }
            result = state.getCloneItemStack(hitResult, (LevelReader)level, pos, (Player)player);
        } else if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult)hitResult).getEntity();
            result = entity.getPickedResult(hitResult);
        }
        if (result.isEmpty()) {
            return false;
        }
        for (ToolboxBlockEntity toolboxBlockEntity : toolboxes) {
            ToolboxInventory inventory = toolboxBlockEntity.inventory;
            for (int comp = 0; comp < 8; ++comp) {
                ItemStack inSlot = inventory.takeFromCompartment(1, comp, true);
                if (inSlot.isEmpty() || inSlot.getItem() != result.getItem() || !ItemStack.matches((ItemStack)inSlot, (ItemStack)result)) continue;
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ToolboxEquipPacket(toolboxBlockEntity.getBlockPos(), comp, player.getInventory().selected));
                return true;
            }
        }
        return false;
    }

    public static void onKeyInput(int key, boolean pressed) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        if (!AllKeys.TOOLBELT.doesModifierAndCodeMatch(key)) {
            return;
        }
        if (COOLDOWN > 0) {
            return;
        }
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        Level level = player.level();
        List<ToolboxBlockEntity> toolboxes = ToolboxHandler.getNearest((LevelAccessor)player.level(), (Player)player, 8);
        toolboxes.sort(Comparator.comparing(ToolboxBlockEntity::getUniqueId));
        CompoundTag compound = player.getPersistentData().getCompound("CreateToolboxData");
        String slotKey = String.valueOf(player.getInventory().selected);
        boolean equipped = compound.contains(slotKey);
        if (equipped) {
            BlockEntity blockEntity;
            boolean canReachToolbox;
            BlockPos pos = NBTHelper.readBlockPos((CompoundTag)compound.getCompound(slotKey), (String)"Pos");
            double max = ToolboxHandler.getMaxRange((Player)player);
            boolean bl = canReachToolbox = ToolboxHandler.distance(player.position(), pos) < max * max;
            if (canReachToolbox && (blockEntity = level.getBlockEntity(pos)) instanceof ToolboxBlockEntity) {
                RadialToolboxMenu screen = new RadialToolboxMenu(toolboxes, RadialToolboxMenu.State.SELECT_ITEM_UNEQUIP, (ToolboxBlockEntity)blockEntity);
                screen.prevSlot(compound.getCompound(slotKey).getInt("Slot"));
                ScreenOpener.open((Screen)screen);
                return;
            }
            ScreenOpener.open((Screen)new RadialToolboxMenu((List<ToolboxBlockEntity>)ImmutableList.of(), RadialToolboxMenu.State.DETACH, null));
            return;
        }
        if (toolboxes.isEmpty()) {
            return;
        }
        if (toolboxes.size() == 1) {
            ScreenOpener.open((Screen)new RadialToolboxMenu(toolboxes, RadialToolboxMenu.State.SELECT_ITEM, toolboxes.get(0)));
        } else {
            ScreenOpener.open((Screen)new RadialToolboxMenu(toolboxes, RadialToolboxMenu.State.SELECT_BOX, null));
        }
    }

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        int x = width / 2 - 90;
        int y = height - 23;
        RenderSystem.enableDepthTest();
        LocalPlayer player = mc.player;
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains("CreateToolboxData")) {
            return;
        }
        CompoundTag compound = player.getPersistentData().getCompound("CreateToolboxData");
        if (compound.isEmpty()) {
            return;
        }
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        for (int slot = 0; slot < 9; ++slot) {
            int offset;
            String key = String.valueOf(slot);
            if (!compound.contains(key)) continue;
            BlockPos pos = NBTHelper.readBlockPos((CompoundTag)compound.getCompound(key), (String)"Pos");
            double max = ToolboxHandler.getMaxRange((Player)player);
            boolean selected = player.getInventory().selected == slot;
            int n = offset = selected ? 1 : 0;
            AllGuiTextures texture = ToolboxHandler.distance(player.position(), pos) < max * max ? (selected ? AllGuiTextures.TOOLBELT_SELECTED_ON : AllGuiTextures.TOOLBELT_HOTBAR_ON) : (selected ? AllGuiTextures.TOOLBELT_SELECTED_OFF : AllGuiTextures.TOOLBELT_HOTBAR_OFF);
            texture.render(guiGraphics, x + 20 * slot - offset, y + offset);
        }
        poseStack.popPose();
    }
}
