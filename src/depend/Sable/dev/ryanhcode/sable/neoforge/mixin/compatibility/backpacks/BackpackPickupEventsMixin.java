/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef
 *  com.spydnel.backpacks.events.BackpackPickupEvents
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.backpacks;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.spydnel.backpacks.events.BackpackPickupEvents;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={BackpackPickupEvents.class})
public class BackpackPickupEventsMixin {
    @Inject(method={"onRightClickBlock"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;isUnobstructed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Z")}, locals=LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void sable$onRightClickBlock(PlayerInteractEvent.RightClickBlock event, CallbackInfo ci, @Local(name={"isAbove"}) LocalBooleanRef isAbove) {
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        SubLevel containing = Sable.HELPER.getContaining(player.level(), (Vec3i)pos);
        if (containing != null) {
            Vec3 world = containing.logicalPose().transformPosition(pos.above().getBottomCenter());
            isAbove.set(world.y - 0.1 > player.getEyeY());
        }
    }
}
