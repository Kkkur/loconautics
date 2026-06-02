/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.PushReaction
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.equipment.wrench.IWrenchableWithBracket;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractShaftBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;

public abstract class AbstractSimpleShaftBlock
extends AbstractShaftBlock
implements IWrenchableWithBracket {
    public AbstractSimpleShaftBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return IWrenchableWithBracket.super.onWrenched(state, context);
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        boolean wasWaterLogged;
        boolean bl = wasWaterLogged = state.hasProperty((Property)WATERLOGGED) && newState.hasProperty((Property)WATERLOGGED) && state.getValue((Property)WATERLOGGED) != newState.getValue((Property)WATERLOGGED);
        if (state != newState && !isMoving && !wasWaterLogged) {
            this.removeBracket((BlockGetter)world, pos, true).ifPresent(stack -> Block.popResource((Level)world, (BlockPos)pos, (ItemStack)stack));
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public Optional<ItemStack> removeBracket(BlockGetter world, BlockPos pos, boolean inOnReplacedContext) {
        BracketedBlockEntityBehaviour behaviour = BlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE);
        if (behaviour == null) {
            return Optional.empty();
        }
        BlockState bracket = behaviour.removeBracket(inOnReplacedContext);
        if (bracket == null) {
            return Optional.empty();
        }
        return Optional.of(new ItemStack((ItemLike)bracket.getBlock()));
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.BRACKETED_KINETIC.get();
    }
}
