/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.ComparatorBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.simulated_team.simulated.mixin.torsion_spring;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.simulated_team.simulated.api.IDirectionalAnalogOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={ComparatorBlock.class})
public class ComparatorBlockMixin {
    @WrapOperation(method={"getInputSignal"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/block/state/BlockState;getAnalogOutputSignal(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)I")})
    private int simulated$potentiallyDirectionalAnalogueSignal(BlockState instance, Level level, BlockPos pos, Operation<Integer> original, @Local(name={"direction"}) Direction direction) {
        Block block = instance.getBlock();
        if (block instanceof IDirectionalAnalogOutput) {
            IDirectionalAnalogOutput directionalAnalogOutput = (IDirectionalAnalogOutput)block;
            return directionalAnalogOutput.getAnalogOutputSignalFrom(instance, level, pos, direction);
        }
        return (Integer)original.call(new Object[]{instance, level, pos});
    }
}
