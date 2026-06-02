/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.items.rope.RopeItem;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.rope_winch.RopeWinchBlockEntity;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.index.SimDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RopeItem
extends Item {
    public RopeItem(Item.Properties properties) {
        super(properties);
    }

    public static boolean isValidRopeAttachment(Level level, BlockPos blockPos) {
        SmartBlockEntity smartBlockEntity;
        RopeStrandHolderBehavior behavior;
        boolean validLocation = false;
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof SmartBlockEntity && (behavior = (RopeStrandHolderBehavior)(smartBlockEntity = (SmartBlockEntity)blockEntity).getBehaviour(RopeStrandHolderBehavior.TYPE)) != null && !behavior.isAttached()) {
            validLocation = true;
        }
        return validLocation;
    }

    public static RopeStrandHolderBehavior getRopeHolder(Level level, BlockPos blockPos) {
        SmartBlockEntity smartBlockEntity;
        RopeStrandHolderBehavior behavior;
        RopeStrandHolderBehavior holder = null;
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof SmartBlockEntity && (behavior = (RopeStrandHolderBehavior)(smartBlockEntity = (SmartBlockEntity)blockEntity).getBehaviour(RopeStrandHolderBehavior.TYPE)) != null) {
            holder = behavior;
        }
        return holder;
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        ItemStack heldStack = context.getItemInHand();
        Player player = context.getPlayer();
        boolean validLocation = RopeItem.isValidRopeAttachment(level, clickedPos);
        if (player != null && player.isShiftKeyDown()) {
            heldStack.remove(SimDataComponents.ROPE_FIRST_CONNECTION);
            return InteractionResult.SUCCESS;
        }
        if (validLocation) {
            if (heldStack.has(SimDataComponents.ROPE_FIRST_CONNECTION)) {
                if (!level.isClientSide) {
                    if (!this.attachRope(level, (BlockPos)heldStack.get(SimDataComponents.ROPE_FIRST_CONNECTION), clickedPos)) {
                        heldStack.remove(SimDataComponents.ROPE_FIRST_CONNECTION);
                        return InteractionResult.SUCCESS;
                    }
                    SimAdvancements.LEARNING_THE_ROPES.awardTo(player);
                }
                heldStack.remove(SimDataComponents.ROPE_FIRST_CONNECTION);
                if (!player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            heldStack.set(SimDataComponents.ROPE_FIRST_CONNECTION, (Object)clickedPos);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    private boolean attachRope(Level level, BlockPos posA, BlockPos posB) {
        RopeStrandHolderBehavior ropeHolderA = RopeItem.getRopeHolder(level, posA);
        if (ropeHolderA == null) {
            return false;
        }
        RopeStrandHolderBehavior ropeHolderB = RopeItem.getRopeHolder(level, posB);
        if (ropeHolderB == null) {
            return false;
        }
        if (ropeHolderB.blockEntity instanceof RopeWinchBlockEntity && !(ropeHolderA.blockEntity instanceof RopeWinchBlockEntity)) {
            RopeStrandHolderBehavior temp = ropeHolderA;
            ropeHolderA = ropeHolderB;
            ropeHolderB = temp;
        }
        if (ropeHolderA.blockEntity instanceof RopeWinchBlockEntity && ropeHolderB.blockEntity instanceof RopeWinchBlockEntity) {
            return false;
        }
        if (ropeHolderA.createRope(ropeHolderB)) {
            level.playSound(null, posA, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);
            level.playSound(null, posB, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);
            return true;
        }
        return false;
    }
}
