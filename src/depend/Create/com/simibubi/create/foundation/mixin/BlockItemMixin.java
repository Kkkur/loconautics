/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.simibubi.create.foundation.mixin;

import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.foundation.mixin.accessor.UseOnContextAccessor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockItem.class})
public class BlockItemMixin {
    @Inject(method={"place"}, at={@At(value="HEAD")}, cancellable=true)
    private void create$fixDeployerPlacement(BlockPlaceContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
        BlockState state = pContext.getLevel().getBlockState(((UseOnContextAccessor)pContext).create$getHitResult().getBlockPos());
        if (!state.canBeReplaced() && pContext.getPlayer() instanceof DeployerFakePlayer) {
            cir.setReturnValue((Object)InteractionResult.PASS);
        }
    }
}
