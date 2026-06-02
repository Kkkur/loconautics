/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.BucketPickup
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create;

import com.simibubi.create.api.contraption.dispenser.DefaultMountedDispenseBehavior;
import com.simibubi.create.api.contraption.dispenser.MountedDispenseBehavior;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

class AllMountedDispenseItemBehaviors.5
extends DefaultMountedDispenseBehavior {
    AllMountedDispenseItemBehaviors.5() {
    }

    @Override
    protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
        BlockPos interactionPos = pos.relative(MountedDispenseBehavior.getClosestFacingDirection(facing));
        BlockState state = context.world.getBlockState(interactionPos);
        Block block = state.getBlock();
        if (!(block instanceof BucketPickup)) {
            return super.execute(stack, context, pos, facing);
        }
        BucketPickup bucketPickup = (BucketPickup)block;
        ItemStack bucket = bucketPickup.pickupBlock(null, (LevelAccessor)context.world, interactionPos, state);
        MountedDispenseBehavior.placeItemInInventory(bucket, context, pos);
        stack.shrink(1);
        return stack;
    }
}
