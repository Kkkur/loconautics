/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.throttle_lever;

import dev.ryanhcode.sable.Sable;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverClientGripHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GameRenderer.class})
public class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method={"pick(F)V"}, at={@At(value="TAIL")})
    private void simulated$pickThrottleLever(float partialTicks, CallbackInfo ci) {
        if (this.minecraft == null) {
            return;
        }
        LocalPlayer player = this.minecraft.player;
        if (player == null) {
            return;
        }
        Vec3 eyePos = Sable.HELPER.getEyePositionInterpolated((Entity)player, partialTicks);
        HitResult mcHitResult = this.minecraft.hitResult;
        double minDistance = mcHitResult != null && mcHitResult.getType() != HitResult.Type.MISS ? Sable.HELPER.distanceSquaredWithSubLevels(player.level(), (Position)eyePos, (Position)mcHitResult.getLocation()) : Double.MAX_VALUE;
        for (ThrottleLeverBlockEntity lever : ThrottleLeverClientGripHandler.getNearbyThrottleLevers()) {
            Double hitResultDistance;
            if (lever.isRemoved() || (hitResultDistance = ThrottleLeverClientGripHandler.raycastLever(eyePos, player.getViewVector(partialTicks), lever, partialTicks)) == null || !(hitResultDistance < minDistance)) continue;
            minDistance = hitResultDistance;
            this.minecraft.hitResult = new BlockHitResult(lever.getBlockPos().getCenter(), Direction.UP, lever.getBlockPos(), false);
        }
    }
}
