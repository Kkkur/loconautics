/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class WindmillBearingBlock
extends BearingBlock
implements IBE<WindmillBearingBlockEntity> {
    public WindmillBearingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild()) {
            return ItemInteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.FAIL;
        }
        if (stack.isEmpty()) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            this.withBlockEntityDo((BlockGetter)level, pos, be -> {
                if (be.running) {
                    be.disassemble();
                    return;
                }
                be.assembleNextTick = true;
            });
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public Class<WindmillBearingBlockEntity> getBlockEntityClass() {
        return WindmillBearingBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends WindmillBearingBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.WINDMILL_BEARING.get();
    }
}
