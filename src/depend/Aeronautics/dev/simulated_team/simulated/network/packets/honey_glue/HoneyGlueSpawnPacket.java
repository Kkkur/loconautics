/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 */
package dev.simulated_team.simulated.network.packets.honey_glue;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueEntity;
import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueMaxSizing;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.index.SimEntityTypes;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.index.SimSoundEvents;
import foundry.veil.api.network.handler.PacketContext;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public record HoneyGlueSpawnPacket(BlockPos from, BlockPos to) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<HoneyGlueSpawnPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("honey_glue_spawn"));
    public static StreamCodec<RegistryFriendlyByteBuf, HoneyGlueSpawnPacket> CODEC = StreamCodec.of(HoneyGlueSpawnPacket::writeToBuf, HoneyGlueSpawnPacket::readFromBuf);

    public static void writeToBuf(RegistryFriendlyByteBuf buf, HoneyGlueSpawnPacket packet) {
        buf.writeBlockPos(packet.from);
        buf.writeBlockPos(packet.to);
    }

    public static HoneyGlueSpawnPacket readFromBuf(RegistryFriendlyByteBuf buf) {
        return new HoneyGlueSpawnPacket(buf.readBlockPos(), buf.readBlockPos());
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private InteractionHand getHoneyGlueHand(Player player) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).is(SimItems.HONEY_GLUE) ? InteractionHand.MAIN_HAND : (player.getItemInHand(InteractionHand.OFF_HAND).is(SimItems.HONEY_GLUE) ? InteractionHand.OFF_HAND : null);
    }

    public void handle(PacketContext context) {
        ServerPlayer player = (ServerPlayer)context.player();
        assert (player != null);
        InteractionHand hand = this.getHoneyGlueHand((Player)player);
        if (hand == null) {
            return;
        }
        AABB newBounds = AABB.encapsulatingFullBlocks((BlockPos)this.from, (BlockPos)this.to);
        Pair<Boolean, String> pair = HoneyGlueMaxSizing.checkBounds(newBounds);
        if (((Boolean)pair.getFirst()).booleanValue()) {
            ServerLevel level = (ServerLevel)context.level();
            assert (level != null);
            level.playSound((Player)player, this.to, SimSoundEvents.HONEY_ADDED.event(), SoundSource.BLOCKS, 0.5f, 0.95f);
            level.playSound((Player)player, this.to, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.75f, 1.0f);
            ItemStack honeyGlueItem = player.getItemInHand(hand);
            honeyGlueItem.hurtAndBreak(1, level, player, item -> {});
            HoneyGlueEntity entity = (HoneyGlueEntity)SimEntityTypes.HONEY_GLUE.create((Level)level);
            assert (entity != null);
            entity.setBounds(newBounds);
            level.addFreshEntity((Entity)entity);
            entity.spawnParticles();
            SimAdvancements.NOT_GONNA_SUGARCOAT_IT.awardTo((Player)player);
        }
    }
}
