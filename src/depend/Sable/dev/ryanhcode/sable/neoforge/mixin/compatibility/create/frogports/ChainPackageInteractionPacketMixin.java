/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainPackageInteractionPacket
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerPlayer
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.frogports;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.kinetics.chainConveyor.ChainPackageInteractionPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChainPackageInteractionPacket.class})
public class ChainPackageInteractionPacketMixin {
    @Shadow
    @Final
    private BlockPos selectedConnection;
    @Shadow
    @Final
    private float chainPosition;

    @Inject(method={"applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;)V"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;addLoopingPackage(Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorPackage;)Z")})
    private void sable$initialiseLoopingWorldPosition(ServerPlayer player, ChainConveyorBlockEntity be, CallbackInfo ci, @Local(name={"chainConveyorPackage"}) ChainConveyorPackage chainConveyorPackage) {
        chainConveyorPackage.worldPosition = be.getPackagePosition(this.chainPosition, null);
    }

    @Inject(method={"applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;)V"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;addTravellingPackage(Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorPackage;Lnet/minecraft/core/BlockPos;)Z")})
    private void sable$initialiseTravellingWorldPosition(ServerPlayer player, ChainConveyorBlockEntity be, CallbackInfo ci, @Local(name={"chainConveyorPackage"}) ChainConveyorPackage chainConveyorPackage) {
        chainConveyorPackage.worldPosition = be.getPackagePosition(this.chainPosition, this.selectedConnection);
    }
}
