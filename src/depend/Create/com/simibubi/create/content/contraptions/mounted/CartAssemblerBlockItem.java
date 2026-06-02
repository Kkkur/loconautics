/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.contraptions.mounted;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.mounted.CartAssembleRailType;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlock;
import com.simibubi.create.content.redstone.rail.ControllerRailBlock;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.NotNull;

public class CartAssemblerBlockItem
extends BlockItem {
    public CartAssemblerBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @NotNull
    public InteractionResult useOn(UseOnContext context) {
        if (this.tryPlaceAssembler(context)) {
            context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    public boolean tryPlaceAssembler(UseOnContext context) {
        Direction direction;
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }
        if (!(block instanceof BaseRailBlock)) {
            CreateLang.translate("block.cart_assembler.invalid", new Object[0]).sendStatus(player);
            return false;
        }
        RailShape shape = ((BaseRailBlock)block).getRailDirection(state, (BlockGetter)world, pos, null);
        if (shape != RailShape.EAST_WEST && shape != RailShape.NORTH_SOUTH) {
            return false;
        }
        BlockState newState = (BlockState)AllBlocks.CART_ASSEMBLER.getDefaultState().setValue(CartAssemblerBlock.RAIL_SHAPE, (Comparable)shape);
        CartAssembleRailType newType = null;
        for (CartAssembleRailType type : CartAssembleRailType.values()) {
            if (!type.matches(state)) continue;
            newType = type;
        }
        if (newType == null) {
            return false;
        }
        if (world.isClientSide) {
            return true;
        }
        newState = (BlockState)newState.setValue(CartAssemblerBlock.RAIL_TYPE, newType);
        newState = state.hasProperty((Property)ControllerRailBlock.BACKWARDS) ? (BlockState)newState.setValue((Property)CartAssemblerBlock.BACKWARDS, (Comparable)((Boolean)state.getValue((Property)ControllerRailBlock.BACKWARDS))) : (BlockState)newState.setValue((Property)CartAssemblerBlock.BACKWARDS, (Comparable)Boolean.valueOf((direction = player.getMotionDirection()).getAxisDirection() == Direction.AxisDirection.POSITIVE));
        world.setBlockAndUpdate(pos, newState);
        if (!player.isCreative()) {
            context.getItemInHand().shrink(1);
        }
        AdvancementBehaviour.setPlacedBy(world, pos, (LivingEntity)player);
        return true;
    }
}
