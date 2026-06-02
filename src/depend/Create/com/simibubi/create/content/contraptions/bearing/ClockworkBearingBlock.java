/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
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
import com.simibubi.create.content.contraptions.bearing.ClockworkBearingBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ClockworkBearingBlock
extends BearingBlock
implements IBE<ClockworkBearingBlockEntity> {
    public ClockworkBearingBlock(BlockBehaviour.Properties properties) {
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
            if (!level.isClientSide) {
                this.withBlockEntityDo((BlockGetter)level, pos, be -> {
                    if (be.running) {
                        be.disassemble();
                        return;
                    }
                    be.assembleNextTick = true;
                });
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public Class<ClockworkBearingBlockEntity> getBlockEntityClass() {
        return ClockworkBearingBlockEntity.class;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        InteractionResult resultType = super.onWrenched(state, context);
        if (!context.getLevel().isClientSide && resultType.consumesAction()) {
            this.withBlockEntityDo((BlockGetter)context.getLevel(), context.getClickedPos(), ClockworkBearingBlockEntity::disassemble);
        }
        return resultType;
    }

    @Override
    public BlockEntityType<? extends ClockworkBearingBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.CLOCKWORK_BEARING.get();
    }
}
