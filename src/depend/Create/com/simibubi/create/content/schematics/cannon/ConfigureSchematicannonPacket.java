/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.content.schematics.cannon;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.content.schematics.cannon.SchematicannonMenu;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public record ConfigureSchematicannonPacket(Option option, boolean set) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ConfigureSchematicannonPacket> STREAM_CODEC = StreamCodec.composite(Option.STREAM_CODEC, ConfigureSchematicannonPacket::option, (StreamCodec)ByteBufCodecs.BOOL, ConfigureSchematicannonPacket::set, ConfigureSchematicannonPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_SCHEMATICANNON;
    }

    public void handle(ServerPlayer player) {
        if (player == null || !(player.containerMenu instanceof SchematicannonMenu)) {
            return;
        }
        SchematicannonBlockEntity be = (SchematicannonBlockEntity)((SchematicannonMenu)player.containerMenu).contentHolder;
        switch (this.option.ordinal()) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                be.replaceMode = this.option.ordinal();
                break;
            }
            case 4: {
                be.skipMissing = this.set;
                break;
            }
            case 5: {
                be.replaceBlockEntities = this.set;
                break;
            }
            case 6: {
                be.state = SchematicannonBlockEntity.State.RUNNING;
                be.statusMsg = "running";
                break;
            }
            case 7: {
                be.state = SchematicannonBlockEntity.State.PAUSED;
                be.statusMsg = "paused";
                break;
            }
            case 8: {
                be.state = SchematicannonBlockEntity.State.STOPPED;
                be.statusMsg = "stopped";
                break;
            }
        }
        be.sendUpdate = true;
    }

    public static enum Option {
        DONT_REPLACE,
        REPLACE_SOLID,
        REPLACE_ANY,
        REPLACE_EMPTY,
        SKIP_MISSING,
        SKIP_BLOCK_ENTITIES,
        PLAY,
        PAUSE,
        STOP;

        public static final StreamCodec<ByteBuf, Option> STREAM_CODEC;

        static {
            STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(Option.class);
        }
    }
}
