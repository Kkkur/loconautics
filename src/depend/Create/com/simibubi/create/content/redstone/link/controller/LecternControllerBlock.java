/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LecternBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 */
package com.simibubi.create.content.redstone.link.controller;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class LecternControllerBlock
extends LecternBlock
implements IBE<LecternControllerBlockEntity>,
SpecialBlockItemRequirement {
    public LecternControllerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)HAS_BOOK, (Comparable)Boolean.valueOf(true)));
    }

    @Override
    public Class<LecternControllerBlockEntity> getBlockEntityClass() {
        return LecternControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LecternControllerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.LECTERN_CONTROLLER.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_153573_, BlockState p_153574_) {
        return IBE.super.newBlockEntity(p_153573_, p_153574_);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.isShiftKeyDown() && LecternControllerBlockEntity.playerInRange(player, level, pos)) {
            if (!level.isClientSide) {
                this.withBlockEntityDo((BlockGetter)level, pos, be -> be.tryStartUsing(player));
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                this.replaceWithLectern(state, level, pos);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (!world.isClientSide) {
                this.withBlockEntityDo((BlockGetter)world, pos, be -> be.dropController(state));
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return 15;
    }

    public void replaceLectern(BlockState lecternState, Level world, BlockPos pos, ItemStack controller) {
        world.setBlockAndUpdate(pos, (BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)((Direction)lecternState.getValue((Property)FACING)))).setValue((Property)POWERED, (Comparable)((Boolean)lecternState.getValue((Property)POWERED))));
        this.withBlockEntityDo((BlockGetter)world, pos, be -> be.setController(controller));
    }

    public void replaceWithLectern(BlockState state, Level world, BlockPos pos) {
        AllSoundEvents.CONTROLLER_TAKE.playOnServer(world, (Vec3i)pos);
        world.setBlockAndUpdate(pos, (BlockState)((BlockState)Blocks.LECTERN.defaultBlockState().setValue((Property)FACING, (Comparable)((Direction)state.getValue((Property)FACING)))).setValue((Property)POWERED, (Comparable)((Boolean)state.getValue((Property)POWERED))));
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return Blocks.LECTERN.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        ArrayList<ItemStack> requiredItems = new ArrayList<ItemStack>();
        requiredItems.add(new ItemStack((ItemLike)Blocks.LECTERN));
        requiredItems.add(new ItemStack((ItemLike)AllItems.LINKED_CONTROLLER.get()));
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, requiredItems);
    }
}
