/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FlowingFluid
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.fluids_on_sub_levels;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={FlowingFluid.class})
public class FlowingFluidMixin {
    @Inject(at={@At(value="HEAD")}, method={"canSpreadTo(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/material/Fluid;)Z"}, cancellable=true)
    private void sable$canSpreadTo(BlockGetter pLevel, BlockPos pFromPos, BlockState pFromBlockState, Direction pDirection, BlockPos pToPos, BlockState pToBlockState, FluidState pToFluidState, Fluid pFluid, CallbackInfoReturnable<Boolean> cir) {
        if (!(pLevel instanceof Level)) {
            return;
        }
        Level level = (Level)pLevel;
        SubLevel subLevel = Sable.HELPER.getContaining(level, (Position)Vec3.atCenterOf((Vec3i)pToPos));
        if (subLevel != null) {
            BlockPos mut = pToPos;
            boolean ableToFlow = false;
            while (subLevel.getPlot().getBoundingBox().contains(mut.getX(), mut.getY(), mut.getZ())) {
                if ((mut = mut.below()).getY() >= 0 && pLevel.getBlockState(mut).isAir()) continue;
                ableToFlow = true;
                break;
            }
            if (!ableToFlow) {
                cir.setReturnValue((Object)false);
            }
        }
    }
}
