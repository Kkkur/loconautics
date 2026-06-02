/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.WorldAttached
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.equipment.toolbox;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.foundation.networking.ISyncPersistentData;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.createmod.catnip.data.WorldAttached;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class ToolboxHandler {
    public static final WorldAttached<WeakHashMap<BlockPos, ToolboxBlockEntity>> toolboxes = new WorldAttached(w -> new WeakHashMap());
    static int validationTimer = 20;

    public static void onLoad(ToolboxBlockEntity be) {
        ((WeakHashMap)toolboxes.get((LevelAccessor)be.getLevel())).put(be.getBlockPos(), be);
    }

    public static void onUnload(ToolboxBlockEntity be) {
        ((WeakHashMap)toolboxes.get((LevelAccessor)be.getLevel())).remove(be.getBlockPos());
    }

    public static void entityTick(Entity entity, Level world) {
        if (world.isClientSide) {
            return;
        }
        if (!(world instanceof ServerLevel)) {
            return;
        }
        if (!(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)entity;
        if (entity.tickCount % validationTimer != 0) {
            return;
        }
        if (!player.getPersistentData().contains("CreateToolboxData")) {
            return;
        }
        boolean sendData = false;
        CompoundTag compound = player.getPersistentData().getCompound("CreateToolboxData");
        for (int i = 0; i < 9; ++i) {
            String key = String.valueOf(i);
            if (!compound.contains(key)) continue;
            CompoundTag data = compound.getCompound(key);
            BlockPos pos = NBTHelper.readBlockPos((CompoundTag)data, (String)"Pos");
            int slot = data.getInt("Slot");
            if (!world.isLoaded(pos)) continue;
            if (!(world.getBlockState(pos).getBlock() instanceof ToolboxBlock)) {
                compound.remove(key);
                sendData = true;
                continue;
            }
            BlockEntity prevBlockEntity = world.getBlockEntity(pos);
            if (!(prevBlockEntity instanceof ToolboxBlockEntity)) continue;
            ((ToolboxBlockEntity)prevBlockEntity).connectPlayer(slot, (Player)player, i);
        }
        if (sendData) {
            ToolboxHandler.syncData((Player)player);
        }
    }

    public static void playerLogin(Player player) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        if (player.getPersistentData().contains("CreateToolboxData") && !player.getPersistentData().getCompound("CreateToolboxData").isEmpty()) {
            ToolboxHandler.syncData(player);
        }
    }

    public static void syncData(Player player) {
        CatnipServices.NETWORK.sendToClient((ServerPlayer)player, (CustomPacketPayload)new ISyncPersistentData.PersistentDataPacket((Entity)player));
    }

    public static List<ToolboxBlockEntity> getNearest(LevelAccessor world, Player player, int maxAmount) {
        Vec3 location = player.position();
        double maxRange = ToolboxHandler.getMaxRange(player);
        return ((WeakHashMap)toolboxes.get(world)).keySet().stream().filter(p -> ToolboxHandler.distance(location, p) < maxRange * maxRange).sorted((p1, p2) -> Double.compare(ToolboxHandler.distance(location, p1), ToolboxHandler.distance(location, p2))).limit(maxAmount).map(((WeakHashMap)toolboxes.get(world))::get).filter(ToolboxBlockEntity::isFullyInitialized).collect(Collectors.toList());
    }

    public static void unequip(Player player, int hotbarSlot, boolean keepItems) {
        CompoundTag compound = player.getPersistentData().getCompound("CreateToolboxData");
        Level world = player.level();
        String key = String.valueOf(hotbarSlot);
        if (!compound.contains(key)) {
            return;
        }
        CompoundTag prevData = compound.getCompound(key);
        BlockPos prevPos = NBTHelper.readBlockPos((CompoundTag)prevData, (String)"Pos");
        int prevSlot = prevData.getInt("Slot");
        BlockEntity prevBlockEntity = world.getBlockEntity(prevPos);
        if (prevBlockEntity instanceof ToolboxBlockEntity) {
            ToolboxBlockEntity toolbox = (ToolboxBlockEntity)prevBlockEntity;
            toolbox.unequip(prevSlot, player, hotbarSlot, keepItems || !ToolboxHandler.withinRange(player, toolbox));
        }
        compound.remove(key);
    }

    public static boolean withinRange(Player player, ToolboxBlockEntity box) {
        if (player.level() != box.getLevel()) {
            return false;
        }
        double maxRange = ToolboxHandler.getMaxRange(player);
        return ToolboxHandler.distance(player.position(), box.getBlockPos()) < maxRange * maxRange;
    }

    public static double distance(Vec3 location, BlockPos p) {
        return location.distanceToSqr((double)((float)p.getX() + 0.5f), (double)p.getY(), (double)((float)p.getZ() + 0.5f));
    }

    public static double getMaxRange(Player player) {
        return ((Integer)AllConfigs.server().equipment.toolboxRange.get()).doubleValue();
    }
}
