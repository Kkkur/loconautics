/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entities_stick_sublevels.packet_mixin;

import dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels.packet_mixin.PacketActuallyInSubLevelExtension;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientboundTeleportEntityPacket.class})
public class ClientboundTeleportEntityPacketMixin
implements PacketActuallyInSubLevelExtension {
    @Unique
    private boolean sable$actuallyInSubLevel;

    @Inject(method={"<init>(Lnet/minecraft/network/FriendlyByteBuf;)V"}, at={@At(value="RETURN")})
    private void sable$readActuallyInSubLevel(FriendlyByteBuf friendlyByteBuf, CallbackInfo ci) {
        this.sable$setActuallyInSubLevel(friendlyByteBuf.readBoolean());
    }

    @Inject(method={"write"}, at={@At(value="TAIL")})
    private void sable$writeActuallyInSubLevel(FriendlyByteBuf friendlyByteBuf, CallbackInfo ci) {
        friendlyByteBuf.writeBoolean(this.sable$actuallyInSubLevel);
    }

    @Override
    public void sable$setActuallyInSubLevel(boolean actuallyInSubLevel) {
        this.sable$actuallyInSubLevel = actuallyInSubLevel;
    }

    @Override
    public boolean sable$isActuallyInSubLevel() {
        return this.sable$actuallyInSubLevel;
    }
}
