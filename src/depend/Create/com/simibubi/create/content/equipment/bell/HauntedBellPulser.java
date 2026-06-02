/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  net.createmod.catnip.data.IntAttached
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.tick.PlayerTickEvent$Post
 */
package com.simibubi.create.content.equipment.bell;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.bell.SoulPulseEffectPacket;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class HauntedBellPulser {
    public static final int DISTANCE = 3;
    public static final int RECHARGE_TICKS = 8;
    public static final int WARMUP_TICKS = 10;
    public static final Cache<UUID, IntAttached<Entity>> WARMUP = CacheBuilder.newBuilder().expireAfterAccess(250L, TimeUnit.MILLISECONDS).build();

    @SubscribeEvent
    public static void hauntedBellCreatesPulse(PlayerTickEvent.Post event) {
        Level level;
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        if (player.isSpectator()) {
            return;
        }
        if (!player.isHolding(arg_0 -> AllBlocks.HAUNTED_BELL.isIn(arg_0))) {
            return;
        }
        boolean firstPulse = false;
        try {
            IntAttached ticker = (IntAttached)WARMUP.get((Object)player.getUUID(), () -> IntAttached.with((int)10, (Object)player));
            firstPulse = (Integer)ticker.getFirst() == 1;
            ticker.decrement();
            if (!ticker.isOrBelowZero()) {
                return;
            }
        }
        catch (ExecutionException ticker) {
            // empty catch block
        }
        long gameTime = player.level().getGameTime();
        if ((firstPulse || gameTime % 8L != 0L) && (level = player.level()) instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            HauntedBellPulser.sendPulse(serverLevel, player.blockPosition(), 3, false);
        }
    }

    public static void sendPulse(ServerLevel world, BlockPos pos, int distance, boolean canOverlap) {
        ChunkPos chunk = world.getChunkAt(pos).getPos();
        CatnipServices.NETWORK.sendToClientsTrackingChunk(world, chunk, (CustomPacketPayload)new SoulPulseEffectPacket(pos, distance, canOverlap));
    }
}
