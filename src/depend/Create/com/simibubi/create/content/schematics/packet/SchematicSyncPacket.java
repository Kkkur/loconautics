/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
 */
package com.simibubi.create.content.schematics.packet;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.schematics.SchematicInstances;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

public record SchematicSyncPacket(int slot, boolean deployed, BlockPos anchor, Rotation rotation, Mirror mirror) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, SchematicSyncPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, SchematicSyncPacket::slot, (StreamCodec)ByteBufCodecs.BOOL, SchematicSyncPacket::deployed, (StreamCodec)BlockPos.STREAM_CODEC, SchematicSyncPacket::anchor, (StreamCodec)CatnipStreamCodecs.ROTATION, SchematicSyncPacket::rotation, (StreamCodec)CatnipStreamCodecs.MIRROR, SchematicSyncPacket::mirror, SchematicSyncPacket::new);

    public SchematicSyncPacket(int slot, StructurePlaceSettings settings, BlockPos anchor, boolean deployed) {
        this(slot, deployed, anchor, settings.getRotation(), settings.getMirror());
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.SYNC_SCHEMATIC;
    }

    public void handle(ServerPlayer player) {
        ItemStack stack = this.slot == -1 ? player.getMainHandItem() : player.getInventory().getItem(this.slot);
        if (!AllItems.SCHEMATIC.isIn(stack)) {
            return;
        }
        stack.set(AllDataComponents.SCHEMATIC_DEPLOYED, (Object)this.deployed);
        stack.set(AllDataComponents.SCHEMATIC_ANCHOR, (Object)this.anchor);
        stack.set(AllDataComponents.SCHEMATIC_ROTATION, (Object)this.rotation);
        stack.set(AllDataComponents.SCHEMATIC_MIRROR, (Object)this.mirror);
        SchematicInstances.clearHash(stack);
    }
}
