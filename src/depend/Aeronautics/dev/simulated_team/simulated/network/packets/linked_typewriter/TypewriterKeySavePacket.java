/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.network.packets.linked_typewriter;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import foundry.veil.api.network.handler.ServerPacketContext;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record TypewriterKeySavePacket(Map<Integer, LinkedTypewriterEntries.KeyboardEntry> changedKeys, BlockPos pos, boolean clearAll) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<TypewriterKeySavePacket> TYPE = new CustomPacketPayload.Type(Simulated.path("linked_typewriter_save"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TypewriterKeySavePacket> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.map(HashMap::new, (StreamCodec)ByteBufCodecs.INT, LinkedTypewriterEntries.KeyboardEntry.STREAM_CODEC), TypewriterKeySavePacket::changedKeys, (StreamCodec)BlockPos.STREAM_CODEC, TypewriterKeySavePacket::pos, (StreamCodec)ByteBufCodecs.BOOL, TypewriterKeySavePacket::clearAll, TypewriterKeySavePacket::new);

    public TypewriterKeySavePacket(LinkedTypewriterEntries keys, BlockPos pos, boolean clearAll) {
        this(keys.getKeyMap(), pos, clearAll);
    }

    public void handle(ServerPacketContext context) {
        Level level = context.level();
        BlockEntity be = level.getBlockEntity(this.pos);
        if (be instanceof LinkedTypewriterBlockEntity) {
            LinkedTypewriterBlockEntity lbe = (LinkedTypewriterBlockEntity)be;
            for (LinkedTypewriterEntries.KeyboardEntry entry : this.changedKeys.values()) {
                entry.setLocation(this.pos);
            }
            lbe.getTypewriterEntries().clearAll();
            if (!this.clearAll) {
                lbe.getTypewriterEntries().addAll(this.changedKeys);
            }
            if (lbe.getTypewriterEntries().getSize() >= 26) {
                SimAdvancements.I_PAID_FOR_THE_WHOLE_TYPEWRITER.awardTo((Player)context.player());
            }
            lbe.setChanged();
            lbe.sendData();
        }
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
