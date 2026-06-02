/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Connection
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.network.CommonListenerCookie
 *  net.minecraft.server.players.PlayerList
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.udp;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.network.udp.SableUDPServer;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PlayerList.class})
public class PlayerListMixin {
    @Inject(method={"placeNewPlayer"}, at={@At(value="INVOKE", target="Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal=0, shift=At.Shift.AFTER)})
    private void onPlayerJoin(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        SableUDPServer server = SableUDPServer.getServer(serverPlayer.server);
        if (server == null) {
            return;
        }
        Sable.LOGGER.info("Beginning attempted authentication with player {}", (Object)serverPlayer.getName().getString());
        server.beginAuthentication(serverPlayer);
    }
}
