/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.kinetics.base.KineticBlock
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.extra_kinetics;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={KineticBlock.class})
public class KineticBlockMixin {
    @Inject(method={"updateIndirectNeighbourShapes"}, at={@At(value="TAIL")})
    public void extraKineticsReset(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count, CallbackInfo ci, @Local BlockEntity be) {
        ExtraKinetics ek;
        KineticBlockEntity extraKinetics;
        if (be instanceof ExtraKinetics && (extraKinetics = (ek = (ExtraKinetics)be).getExtraKinetics()) != null) {
            extraKinetics.warnOfMovement();
            extraKinetics.clearKineticInformation();
            extraKinetics.updateSpeed = true;
        }
    }
}
