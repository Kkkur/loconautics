/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.logistics.depot;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

public class SharedDepotBlockMethods {
    protected static DepotBehaviour get(BlockGetter worldIn, BlockPos pos) {
        return BlockEntityBehaviour.get(worldIn, pos, DepotBehaviour.TYPE);
    }

    public static ItemInteractionResult onUse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        if (ray.getDirection() != Direction.UP) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        DepotBehaviour behaviour = SharedDepotBlockMethods.get((BlockGetter)level, pos);
        if (behaviour == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!behaviour.canAcceptItems.get().booleanValue()) {
            return ItemInteractionResult.SUCCESS;
        }
        boolean wasEmptyHanded = stack.isEmpty();
        boolean shouldntPlaceItem = AllBlocks.MECHANICAL_ARM.isIn(stack);
        ItemStack mainItemStack = behaviour.getHeldItemStack();
        if (!mainItemStack.isEmpty()) {
            player.getInventory().placeItemBackInInventory(mainItemStack);
            behaviour.removeHeldItem();
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, 1.0f + level.getRandom().nextFloat());
        }
        ItemStackHandler outputs = behaviour.processingOutputBuffer;
        for (int i = 0; i < outputs.getSlots(); ++i) {
            player.getInventory().placeItemBackInInventory(outputs.extractItem(i, 64, false));
        }
        if (!wasEmptyHanded && !shouldntPlaceItem) {
            TransportedItemStack transported = new TransportedItemStack(stack);
            transported.insertedFrom = player.getDirection();
            transported.prevBeltPosition = 0.25f;
            transported.beltPosition = 0.25f;
            behaviour.setHeldItem(transported);
            player.setItemInHand(hand, ItemStack.EMPTY);
            AllSoundEvents.DEPOT_SLIDE.playOnServer(level, (Vec3i)pos);
        }
        behaviour.blockEntity.notifyUpdate();
        return ItemInteractionResult.SUCCESS;
    }

    public static void onLanded(BlockGetter worldIn, Entity entityIn) {
        ItemStack asItem = ItemHelper.fromItemEntity(entityIn);
        if (asItem.isEmpty()) {
            return;
        }
        if (entityIn.level().isClientSide) {
            return;
        }
        BlockPos pos = entityIn.blockPosition();
        DirectBeltInputBehaviour inputBehaviour = BlockEntityBehaviour.get(worldIn, pos, DirectBeltInputBehaviour.TYPE);
        if (inputBehaviour == null) {
            return;
        }
        Vec3 targetLocation = VecHelper.getCenterOf((Vec3i)pos).add(0.0, 0.3125, 0.0);
        if (!PackageEntity.centerPackage(entityIn, targetLocation)) {
            return;
        }
        ItemStack remainder = inputBehaviour.handleInsertion(asItem, Direction.DOWN, false);
        if (entityIn instanceof ItemEntity) {
            ((ItemEntity)entityIn).setItem(remainder);
        }
        if (remainder.isEmpty()) {
            entityIn.discard();
        }
    }

    public static int getComparatorInputOverride(BlockState blockState, Level worldIn, BlockPos pos) {
        DepotBehaviour depotBehaviour = SharedDepotBlockMethods.get((BlockGetter)worldIn, pos);
        if (depotBehaviour == null) {
            return 0;
        }
        float f = depotBehaviour.getPresentStackSize();
        Integer max = depotBehaviour.maxStackSize.get();
        return Mth.clamp((int)(Mth.floor((float)((f /= (float)(max == 0 ? 64 : max)) * 14.0f)) + (f > 0.0f ? 1 : 0)), (int)0, (int)15);
    }
}
