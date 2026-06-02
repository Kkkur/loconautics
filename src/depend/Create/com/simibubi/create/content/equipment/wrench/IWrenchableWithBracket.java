/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.equipment.wrench;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IWrenchableWithBracket
extends IWrenchable {
    public Optional<ItemStack> removeBracket(BlockGetter var1, BlockPos var2, boolean var3);

    @Override
    default public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (this.tryRemoveBracket(context)) {
            return InteractionResult.SUCCESS;
        }
        return IWrenchable.super.onWrenched(state, context);
    }

    default public boolean tryRemoveBracket(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Optional<ItemStack> bracket = this.removeBracket((BlockGetter)world, pos, false);
        BlockState blockState = world.getBlockState(pos);
        if (bracket.isPresent()) {
            Player player = context.getPlayer();
            if (!world.isClientSide && !player.isCreative()) {
                player.getInventory().placeItemBackInInventory(bracket.get());
            }
            if (!world.isClientSide && AllBlocks.FLUID_PIPE.has(blockState)) {
                Direction.Axis preferred = FluidPropagator.getStraightPipeAxis(blockState);
                Direction preferredDirection = preferred == null ? Direction.UP : Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)preferred);
                BlockState updated = ((FluidPipeBlock)AllBlocks.FLUID_PIPE.get()).updateBlockState(blockState, preferredDirection, null, (BlockAndTintGetter)world, pos);
                if (updated != blockState) {
                    world.setBlockAndUpdate(pos, updated);
                }
            }
            return true;
        }
        return false;
    }
}
