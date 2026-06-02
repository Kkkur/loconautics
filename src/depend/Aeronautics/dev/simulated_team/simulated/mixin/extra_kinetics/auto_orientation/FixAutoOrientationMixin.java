/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock
 *  com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
 *  com.simibubi.create.content.kinetics.base.HorizontalKineticBlock
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.simulated_team.simulated.mixin.extra_kinetics.auto_orientation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={HorizontalKineticBlock.class, DirectionalAxisKineticBlock.class, DirectionalKineticBlock.class})
public class FixAutoOrientationMixin {
    @WrapOperation(method={"*"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/base/IRotate;hasShaftTowards(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z")}, remap=false)
    public boolean test(IRotate instance, LevelReader world, BlockPos pos, BlockState state, Direction incomingDir, Operation<Boolean> originalWrap) {
        Block block;
        ExtraKinetics ek;
        KineticBlockEntity extraKinetics;
        BlockEntity be;
        Boolean original = (Boolean)originalWrap.call(new Object[]{instance, world, pos, state, incomingDir});
        if (!original.booleanValue() && (be = world.getBlockEntity(pos)) instanceof ExtraKinetics && (extraKinetics = (ek = (ExtraKinetics)be).getExtraKinetics()) != null && (block = extraKinetics.getBlockState().getBlock()) instanceof ExtraKinetics.ExtraKineticsBlock) {
            ExtraKinetics.ExtraKineticsBlock ekb = (ExtraKinetics.ExtraKineticsBlock)block;
            original = ekb.getExtraKineticsRotationConfiguration().hasShaftTowards(world, pos, state, incomingDir);
        }
        return original;
    }
}
