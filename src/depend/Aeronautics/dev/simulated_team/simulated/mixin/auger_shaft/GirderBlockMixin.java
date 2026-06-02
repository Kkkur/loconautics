/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.decoration.girder.GirderBlock
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.simulated_team.simulated.mixin.auger_shaft;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={GirderBlock.class})
public class GirderBlockMixin {
    @Inject(method={"isConnected"}, at={@At(value="TAIL")}, cancellable=true)
    private static void connectToAugers(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction side, CallbackInfoReturnable<Boolean> cir, @Local(ordinal=1) BlockState otherState) {
        if (otherState.getBlock() instanceof AugerShaftBlock) {
            cir.setReturnValue((Object)true);
        }
    }
}
