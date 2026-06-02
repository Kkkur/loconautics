/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  dan200.computercraft.api.network.PacketReceiver
 *  dan200.computercraft.shared.peripheral.modem.wireless.WirelessNetwork
 *  net.minecraft.core.Position
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.compatibility.computercraft;

import com.llamalad7.mixinextras.sugar.Local;
import dan200.computercraft.api.network.PacketReceiver;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessNetwork;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={WirelessNetwork.class})
public class WirelessNetworkMixin {
    @Redirect(remap=false, method={"tryTransmit"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
    private static double getPosition(Vec3 a, Vec3 b, @Local(ordinal=0, argsOnly=true) PacketReceiver packetReceiver) {
        return Sable.HELPER.distanceSquaredWithSubLevels(packetReceiver.getLevel(), (Position)a, (Position)b);
    }
}
