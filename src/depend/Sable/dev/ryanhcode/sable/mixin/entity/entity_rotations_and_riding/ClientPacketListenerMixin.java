/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entity_rotations_and_riding;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientPacketListener.class})
public class ClientPacketListenerMixin {
    @Inject(method={"handleMovePlayer"}, at={@At(value="INVOKE", target="Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal=0, shift=At.Shift.BEFORE)})
    private void sable$onHandleMovePlayer(ClientboundPlayerPositionPacket clientboundPlayerPositionPacket, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        SubLevel subLevel = Sable.HELPER.getContaining((Entity)player);
        if (subLevel != null) {
            player.setPos(subLevel.logicalPose().transformPosition(player.position()));
            EntitySubLevelUtil.setOldPosNoMovement((Entity)player);
        }
    }
}
