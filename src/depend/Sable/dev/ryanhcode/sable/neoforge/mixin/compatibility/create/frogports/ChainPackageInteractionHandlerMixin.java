/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage$ChainConveyorPackagePhysicsData
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainPackageInteractionHandler
 *  com.simibubi.create.foundation.utility.RaycastHelper
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.frogports;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.kinetics.chainConveyor.ChainPackageInteractionHandler;
import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Position;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ChainPackageInteractionHandler.class})
public class ChainPackageInteractionHandlerMixin {
    @Redirect(method={"lambda$onUse$0"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;getEyePosition()Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 sable$getTraceOrigin(LocalPlayer instance, @Local(argsOnly=true) ChainConveyorPackage.ChainConveyorPackagePhysicsData data) {
        Vec3 origin = instance.getEyePosition();
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Position)data.targetPos);
        if (subLevel != null) {
            origin = subLevel.logicalPose().transformPositionInverse(origin);
        }
        return origin;
    }

    @Redirect(method={"lambda$onUse$0"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/foundation/utility/RaycastHelper;getTraceTarget(Lnet/minecraft/world/entity/player/Player;DLnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 sable$getTraceTarget(Player playerIn, double range, Vec3 from, @Local(argsOnly=true) ChainConveyorPackage.ChainConveyorPackagePhysicsData data) {
        Vec3 target = RaycastHelper.getTraceTarget((Player)playerIn, (double)range, (Vec3)playerIn.getEyePosition());
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Position)data.targetPos);
        if (subLevel != null) {
            target = subLevel.logicalPose().transformPositionInverse(target);
        }
        return target;
    }
}
