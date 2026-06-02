/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.punching;

import dev.ryanhcode.sable.index.SableTags;
import dev.ryanhcode.sable.mixin.punching.ItemInvoker;
import dev.ryanhcode.sable.network.client.ClientSubLevelPunchHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Minecraft.class})
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;
    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    @Nullable
    public abstract ClientPacketListener getConnection();

    @Inject(method={"startAttack"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V")})
    private void tryPaddling(CallbackInfoReturnable<Boolean> cir) {
        FluidState state;
        if (!this.player.getMainHandItem().is(SableTags.PADDLES) || this.player.getCooldowns().isOnCooldown(this.player.getMainHandItem().getItem())) {
            return;
        }
        BlockHitResult hitResult = ItemInvoker.sable$getPlayerPOVHitResult((Level)this.level, (Player)this.player, ClipContext.Fluid.ANY);
        if (hitResult.getType() == HitResult.Type.BLOCK && !(state = this.level.getFluidState(hitResult.getBlockPos())).isEmpty()) {
            ClientSubLevelPunchHelper.clientTryPunch(hitResult, (Level)this.level, false);
        }
    }
}
