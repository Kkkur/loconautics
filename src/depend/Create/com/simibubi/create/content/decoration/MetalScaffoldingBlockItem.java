/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ScaffoldingBlockItem
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.decoration;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ScaffoldingBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MetalScaffoldingBlockItem
extends ScaffoldingBlockItem {
    public MetalScaffoldingBlockItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext pContext) {
        Block block;
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState blockstate = level.getBlockState(blockpos);
        if (!blockstate.is(block = this.getBlock())) {
            return pContext;
        }
        Direction direction = pContext.isSecondaryUseActive() ? (pContext.isInside() ? pContext.getClickedFace().getOpposite() : pContext.getClickedFace()) : (pContext.getClickedFace() == Direction.UP ? pContext.getHorizontalDirection() : Direction.UP);
        int i = 0;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = blockpos.mutable().move(direction);
        while (i < 7) {
            if (!level.isClientSide && !level.isInWorldBounds((BlockPos)blockpos$mutableblockpos)) {
                Player player = pContext.getPlayer();
                int j = level.getMaxBuildHeight();
                if (!(player instanceof ServerPlayer)) break;
                ServerPlayer sp = (ServerPlayer)player;
                if (blockpos$mutableblockpos.getY() < j) break;
                sp.sendSystemMessage((Component)Component.translatable((String)"build.tooHigh", (Object[])new Object[]{j - 1}).withStyle(ChatFormatting.RED), true);
                break;
            }
            blockstate = level.getBlockState((BlockPos)blockpos$mutableblockpos);
            if (!blockstate.is(this.getBlock())) {
                if (!blockstate.canBeReplaced(pContext)) break;
                return BlockPlaceContext.at((BlockPlaceContext)pContext, (BlockPos)blockpos$mutableblockpos, (Direction)direction);
            }
            blockpos$mutableblockpos.move(direction);
            if (!direction.getAxis().isHorizontal()) continue;
            ++i;
        }
        return null;
    }
}
