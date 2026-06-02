/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Connection
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.network.CommonListenerCookie
 *  net.minecraft.server.players.PlayerList
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.player_freezing;

import dev.ryanhcode.sable.mixinterface.player_freezing.PlayerFreezeExtension;
import dev.ryanhcode.sable.mixinterface.respawn_point.ServerPlayerRespawnExtension;
import dev.ryanhcode.sable.network.packets.tcp.ClientboundFreezePlayerPacket;
import java.util.UUID;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PlayerList.class})
public class PlayerListMixin {
    @Inject(method={"placeNewPlayer"}, at={@At(value="TAIL")})
    private void sable$player(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        PlayerFreezeExtension extension;
        UUID uuid;
        if (serverPlayer instanceof PlayerFreezeExtension && (uuid = (extension = (PlayerFreezeExtension)serverPlayer).sable$getFrozenToSubLevel()) != null) {
            serverPlayer.connection.send((Packet)new ClientboundCustomPayloadPacket((CustomPacketPayload)new ClientboundFreezePlayerPacket(uuid, extension.sable$getFrozenToSubLevelAnchor())));
        }
    }

    @Inject(method={"respawn"}, at={@At(value="TAIL")})
    private void sable$respawn(ServerPlayer oldPlayer, boolean bl, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer newPlayer = (ServerPlayer)cir.getReturnValue();
        ((ServerPlayerRespawnExtension)newPlayer).sable$takeQueuedFreezeFrom(oldPlayer);
    }
}
