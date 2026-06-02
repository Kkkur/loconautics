/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.MultiPlayerGameMode
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.simulated_team.simulated.mixin.quiet_use;

import dev.simulated_team.simulated.util.QuietUse;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={MultiPlayerGameMode.class})
public class MultiPlayerGameModeMixin {
    @Inject(method={"useItemOn"}, at={@At(value="INVOKE", target="Lorg/apache/commons/lang3/mutable/MutableObject;<init>()V")}, cancellable=true)
    private void quietUseIntercept(LocalPlayer player, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) {
        QuietUse quietUse;
        InteractionResult useResult;
        BlockState state = player.level().getBlockState(result.getBlockPos());
        Block block = state.getBlock();
        if (block instanceof QuietUse && (useResult = (quietUse = (QuietUse)block).quietUse((Player)player, hand, result.getBlockPos(), state)) != null) {
            cir.setReturnValue((Object)useResult);
        }
    }
}
