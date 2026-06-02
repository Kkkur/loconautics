/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.offroad.network.borehead_bearing;

import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.handlers.client.MultiMiningClientHandler;
import dev.ryanhcode.offroad.handlers.server.MultiMiningServerManager;
import foundry.veil.api.network.handler.PacketContext;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

public final class ClientboundMultiMiningSync
implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundMultiMiningSync> TYPE = new CustomPacketPayload.Type(Offroad.path("borehead_sync_blocks"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundMultiMiningSync> CODEC = StreamCodec.of((buf, p) -> p.write((FriendlyByteBuf)buf), ClientboundMultiMiningSync::read);
    private int breakingID;
    public final Map<BlockPos, MultiMiningServerManager.BlockBreakingData> inData;
    private final Map<BlockPos, MultiMiningClientHandler.ClientBlockBreakingData> clientInData;

    private ClientboundMultiMiningSync(Map<BlockPos, MultiMiningServerManager.BlockBreakingData> inData, Map<BlockPos, MultiMiningClientHandler.ClientBlockBreakingData> outData) {
        this.inData = inData;
        this.clientInData = outData;
    }

    public static ClientboundMultiMiningSync serverOutboundData(int id) {
        ClientboundMultiMiningSync syncPacket = new ClientboundMultiMiningSync((Map<BlockPos, MultiMiningServerManager.BlockBreakingData>)new Object2ObjectOpenHashMap(), null);
        syncPacket.breakingID = id;
        return syncPacket;
    }

    @ApiStatus.Internal
    private static ClientboundMultiMiningSync clientInboundData() {
        return new ClientboundMultiMiningSync(null, (Map<BlockPos, MultiMiningClientHandler.ClientBlockBreakingData>)new Object2ObjectOpenHashMap());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeInt(this.breakingID);
        buf.writeInt(this.inData.size());
        for (Map.Entry<BlockPos, MultiMiningServerManager.BlockBreakingData> set : this.inData.entrySet()) {
            BlockPos.STREAM_CODEC.encode((Object)buf, (Object)set.getKey());
            set.getValue().clientAimedSerialization((ByteBuf)buf);
        }
    }

    private static ClientboundMultiMiningSync read(FriendlyByteBuf buf) {
        ClientboundMultiMiningSync clientSidePacket = ClientboundMultiMiningSync.clientInboundData();
        clientSidePacket.breakingID = buf.readInt();
        int size = buf.readInt();
        for (int i = 0; i < size; ++i) {
            BlockPos pos = (BlockPos)BlockPos.STREAM_CODEC.decode((Object)buf);
            MultiMiningClientHandler.ClientBlockBreakingData clientData = new MultiMiningClientHandler.ClientBlockBreakingData();
            clientData.invalid = buf.readBoolean();
            if (!clientData.invalid) {
                clientData.destroyProgress = buf.readByte();
            }
            clientSidePacket.clientInData.put(pos, clientData);
        }
        return clientSidePacket;
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(PacketContext context) {
        if (context.player() == null || !context.level().isClientSide || this.clientInData == null) {
            return;
        }
        MultiMiningClientHandler.handleInboundClientUpdate(context.level(), this.clientInData, this.breakingID);
    }
}
