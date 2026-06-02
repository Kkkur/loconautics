/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.kinetics.millstone;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class MillstoneBlock
extends KineticBlock
implements IBE<MillstoneBlockEntity>,
ICogWheel {
    public MillstoneBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.MILLSTONE;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        this.withBlockEntityDo((BlockGetter)level, pos, millstone -> {
            int slot;
            boolean emptyOutput = true;
            ItemStackHandler inv = millstone.outputInv;
            for (slot = 0; slot < inv.getSlots(); ++slot) {
                ItemStack stackInSlot = inv.getStackInSlot(slot);
                if (!stackInSlot.isEmpty()) {
                    emptyOutput = false;
                }
                player.getInventory().placeItemBackInInventory(stackInSlot);
                inv.setStackInSlot(slot, ItemStack.EMPTY);
            }
            if (emptyOutput) {
                inv = millstone.inputInv;
                for (slot = 0; slot < inv.getSlots(); ++slot) {
                    player.getInventory().placeItemBackInInventory(inv.getStackInSlot(slot));
                    inv.setStackInSlot(slot, ItemStack.EMPTY);
                }
            }
            millstone.setChanged();
            millstone.sendData();
        });
        return ItemInteractionResult.SUCCESS;
    }

    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        if (entityIn.level().isClientSide) {
            return;
        }
        if (!(entityIn instanceof ItemEntity)) {
            return;
        }
        ItemEntity itemEntity = (ItemEntity)entityIn;
        if (!entityIn.isAlive()) {
            return;
        }
        MillstoneBlockEntity millstone = null;
        for (BlockPos pos : Iterate.hereAndBelow((BlockPos)entityIn.blockPosition())) {
            if (millstone != null) continue;
            millstone = (MillstoneBlockEntity)this.getBlockEntity(worldIn, pos);
        }
        if (millstone == null) {
            return;
        }
        IItemHandler capability = (IItemHandler)millstone.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, millstone.getBlockPos(), null);
        if (capability == null) {
            return;
        }
        ItemStack remainder = capability.insertItem(0, itemEntity.getItem(), false);
        if (remainder.isEmpty()) {
            itemEntity.discard();
        }
        if (remainder.getCount() < itemEntity.getItem().getCount()) {
            itemEntity.setItem(remainder);
        }
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<MillstoneBlockEntity> getBlockEntityClass() {
        return MillstoneBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MillstoneBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.MILLSTONE.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
