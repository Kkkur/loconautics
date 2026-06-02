/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.decoration.bracket;

import com.simibubi.create.content.decoration.bracket.BracketBlock;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BracketBlockItem
extends BlockItem {
    public BracketBlockItem(Block p_i48527_1_, Item.Properties p_i48527_2_) {
        super(p_i48527_1_, p_i48527_2_);
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockState newBracket;
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        BracketBlock bracketBlock = this.getBracketBlock();
        Player player = context.getPlayer();
        BracketedBlockEntityBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)world, pos, BracketedBlockEntityBehaviour.TYPE);
        if (behaviour == null) {
            return InteractionResult.FAIL;
        }
        if (!behaviour.canHaveBracket()) {
            return InteractionResult.FAIL;
        }
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Optional<BlockState> suitableBracket = bracketBlock.getSuitableBracket(state, context.getClickedFace());
        if (!suitableBracket.isPresent() && player != null) {
            suitableBracket = bracketBlock.getSuitableBracket(state, Direction.orderedByNearest((Entity)player)[0].getOpposite());
        }
        if (!suitableBracket.isPresent()) {
            return InteractionResult.SUCCESS;
        }
        BlockState bracket = behaviour.getBracket();
        if (bracket == (newBracket = suitableBracket.get())) {
            return InteractionResult.SUCCESS;
        }
        world.playSound(null, pos, newBracket.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 0.75f, 1.0f);
        behaviour.applyBracket(newBracket);
        if (player == null || !player.isCreative()) {
            context.getItemInHand().shrink(1);
            if (bracket != null) {
                ItemStack returnedStack = new ItemStack((ItemLike)bracket.getBlock());
                if (player == null) {
                    Block.popResource((Level)world, (BlockPos)pos, (ItemStack)returnedStack);
                } else {
                    player.getInventory().placeItemBackInInventory(returnedStack);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    private BracketBlock getBracketBlock() {
        return (BracketBlock)this.getBlock();
    }
}
