/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.simulated_team.simulated.mixin.extra_kinetics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={EncasedCogwheelBlock.class})
public class EncasedCogwheelBlockMixin {
    @WrapOperation(method={"handleEncasing"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/base/IRotate;hasShaftTowards(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z")})
    private boolean test(IRotate instance, LevelReader level, BlockPos pos, BlockState state, Direction direction, Operation<Boolean> original) {
        Block block;
        ExtraKinetics ek;
        KineticBlockEntity extraKinetics;
        BlockEntity blockEntity;
        boolean bl = (Boolean)original.call(new Object[]{instance, level, pos, state, direction});
        if (!bl && (blockEntity = level.getBlockEntity(pos)) instanceof ExtraKinetics && (extraKinetics = (ek = (ExtraKinetics)blockEntity).getExtraKinetics()) != null && (block = extraKinetics.getBlockState().getBlock()) instanceof ExtraKinetics.ExtraKineticsBlock) {
            ExtraKinetics.ExtraKineticsBlock ekb = (ExtraKinetics.ExtraKineticsBlock)block;
            return ekb.getExtraKineticsRotationConfiguration().hasShaftTowards(level, pos, state, direction);
        }
        return bl;
    }
}
