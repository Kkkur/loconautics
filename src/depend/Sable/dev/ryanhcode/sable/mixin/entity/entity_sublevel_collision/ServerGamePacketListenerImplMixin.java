/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.ServerPlayerGameMode
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.entity.entity_sublevel_collision;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ServerGamePacketListenerImpl.class})
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Redirect(method={"handleMovePlayer"}, at=@At(value="INVOKE", target="Lnet/minecraft/server/level/ServerPlayerGameMode;isCreative()Z"))
    private boolean sable$ignoreCreativeModeForSubLevelCollision(ServerPlayerGameMode instance) {
        return true;
    }
}
