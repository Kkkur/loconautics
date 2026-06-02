/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.item.alchemy.Potions
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.BeehiveBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BeehiveBlockEntity$BeeReleaseStatus
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create;

import com.simibubi.create.api.contraption.dispenser.MountedDispenseBehavior;
import com.simibubi.create.api.contraption.dispenser.OptionalMountedDispenseBehavior;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

class AllMountedDispenseItemBehaviors.7
extends OptionalMountedDispenseBehavior {
    AllMountedDispenseItemBehaviors.7() {
    }

    @Override
    @Nullable
    protected ItemStack doExecute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
        BlockPos interactionPos = pos.relative(MountedDispenseBehavior.getClosestFacingDirection(facing));
        BlockState state = context.world.getBlockState(interactionPos);
        Block block = state.getBlock();
        if (block instanceof BeehiveBlock) {
            BeehiveBlock hive = (BeehiveBlock)block;
            if (state.is(BlockTags.BEEHIVES) && (Integer)state.getValue((Property)BeehiveBlock.HONEY_LEVEL) >= 5) {
                hive.releaseBeesAndResetHoneyLevel(context.world, state, interactionPos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                MountedDispenseBehavior.placeItemInInventory(new ItemStack((ItemLike)Items.HONEY_BOTTLE), context, pos);
                stack.shrink(1);
                return stack;
            }
        }
        if (context.world.getFluidState(interactionPos).is(FluidTags.WATER)) {
            ItemStack waterBottle = PotionContents.createItemStack((Item)Items.POTION, (Holder)Potions.WATER);
            MountedDispenseBehavior.placeItemInInventory(waterBottle, context, pos);
            stack.shrink(1);
            return stack;
        }
        return null;
    }
}
