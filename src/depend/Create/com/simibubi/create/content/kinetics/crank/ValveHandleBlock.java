/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.content.kinetics.crank;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlockEntity;
import com.simibubi.create.foundation.utility.BlockHelper;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@ParametersAreNonnullByDefault
@EventBusSubscriber
public class ValveHandleBlock
extends HandCrankBlock {
    public final DyeColor color;

    public static ValveHandleBlock copper(BlockBehaviour.Properties properties) {
        return new ValveHandleBlock(properties, null);
    }

    public static ValveHandleBlock dyed(BlockBehaviour.Properties properties, DyeColor color) {
        return new ValveHandleBlock(properties, color);
    }

    private ValveHandleBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.VALVE_HANDLE.get((Direction)pState.getValue((Property)FACING));
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public static void onBlockActivated(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        Player player = event.getEntity();
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        if (!(block instanceof ValveHandleBlock)) {
            return;
        }
        ValveHandleBlock vhb = (ValveHandleBlock)block;
        if (!player.mayBuild()) {
            return;
        }
        if (AllItems.WRENCH.isIn(player.getItemInHand(event.getHand())) && player.isShiftKeyDown()) {
            return;
        }
        if (vhb.clicked(level, pos, blockState, player, event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!(pNewState.getBlock() instanceof ValveHandleBlock)) {
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    public boolean clicked(Level level, BlockPos pos, BlockState blockState, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        DyeColor color = DyeColor.getColor((ItemStack)heldItem);
        if (color != null && color != this.color) {
            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos, BlockHelper.copyProperties(blockState, AllBlocks.DYED_VALVE_HANDLES.get(color).getDefaultState()));
            }
            return true;
        }
        this.onBlockEntityUse((BlockGetter)level, pos, hcbe -> {
            ValveHandleBlockEntity vhbe;
            return hcbe instanceof ValveHandleBlockEntity && (vhbe = (ValveHandleBlockEntity)hcbe).activate(player.isShiftKeyDown()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        });
        return true;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockEntityType<? extends HandCrankBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.VALVE_HANDLE.get();
    }

    @Override
    public int getRotationSpeed() {
        return 32;
    }
}
