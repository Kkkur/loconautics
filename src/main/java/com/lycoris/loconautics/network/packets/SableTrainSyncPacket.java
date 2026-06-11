package com.lycoris.loconautics.network.packets;

import java.util.UUID;

import com.lycoris.loconautics.allsable.SableTrain;
import com.lycoris.loconautics.allsable.SableTrainClientRegistry;
import com.lycoris.loconautics.allsable.SableTrainRegistry;
import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: announces the marker state of a Sable train sub-level so the client wrench-relocation flow
 * can recognise it ({@link SableTrainClientRegistry}). One packet per train: {@code present=true} adds/updates the
 * marker (with its carriage span + derail state), {@code present=false} removes it.
 *
 * <p>Mirrors how Create's client already knows its {@code Train}s (via {@code Create.RAILWAYS.sided}); since a
 * {@link SableTrain} is server-only, we replicate just the relocation-relevant facts to clients.
 */
public record SableTrainSyncPacket(UUID subLevelId, boolean present, double bogeySpacing, boolean derailed,
                                   double speed)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SableTrainSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("sable_train_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SableTrainSyncPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        buf.writeUUID(p.subLevelId());
                        buf.writeBoolean(p.present());
                        buf.writeDouble(p.bogeySpacing());
                        buf.writeBoolean(p.derailed());
                        buf.writeDouble(p.speed());
                    },
                    buf -> new SableTrainSyncPacket(buf.readUUID(), buf.readBoolean(), buf.readDouble(),
                            buf.readBoolean(), buf.readDouble())
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(SableTrainSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.present()) {
                SableTrainClientRegistry.put(packet.subLevelId(), packet.bogeySpacing(), packet.derailed(),
                        packet.speed());
            } else {
                SableTrainClientRegistry.remove(packet.subLevelId());
            }
        });
    }

    // ----- server-side send helpers -----

    /** Builds an "add/update" packet from a live train (no-op-safe payload describing its body sub-level). */
    public static SableTrainSyncPacket add(SableTrain train) {
        SableTrain.Car car = train.car();
        double spacing = car.carriage() != null ? car.carriage().bogeySpacing() : 1.0;
        return new SableTrainSyncPacket(car.subLevelId(), true, spacing, train.isDerailed(), train.speed());
    }

    /** Builds a "remove" packet for a sub-level id. */
    public static SableTrainSyncPacket remove(UUID subLevelId) {
        return new SableTrainSyncPacket(subLevelId, false, 0.0, false, 0.0);
    }

    /** Broadcasts a train's current marker state to every connected client. */
    public static void broadcast(SableTrain train) {
        if (train.car().subLevelId() == null) {
            return;
        }
        PacketDistributor.sendToAllPlayers(add(train));
    }

    /** Broadcasts removal of a train sub-level marker to every connected client. */
    public static void broadcastRemoval(UUID subLevelId) {
        if (subLevelId == null) {
            return;
        }
        PacketDistributor.sendToAllPlayers(remove(subLevelId));
    }

    /** Sends every currently-registered train's marker to one player (full sync on login). */
    public static void syncAllTo(ServerPlayer player) {
        for (SableTrain train : SableTrainRegistry.all()) {
            if (train.car().subLevelId() != null) {
                PacketDistributor.sendToPlayer(player, add(train));
            }
        }
    }
}
