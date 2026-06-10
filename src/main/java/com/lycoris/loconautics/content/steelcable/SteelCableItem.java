package com.lycoris.loconautics.content.steelcable;

import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.rope_winch.RopeWinchBlockEntity;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.index.SimDataComponents;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
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

/**
 * Steel Cable — a long-range rope item.
 * Identical behaviour to Simulated's RopeItem, but the strand it creates
 * allows twice the maximum length.
 */
public class SteelCableItem extends Item {

    public SteelCableItem(Properties properties) {
        super(properties);
    }

    public static boolean isValidAttachment(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SmartBlockEntity sbe) {
            RopeStrandHolderBehavior behavior = sbe.getBehaviour(RopeStrandHolderBehavior.TYPE);
            return behavior != null && !behavior.isAttached();
        }
        return false;
    }

    public static RopeStrandHolderBehavior getRopeHolder(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SmartBlockEntity sbe) {
            return sbe.getBehaviour(RopeStrandHolderBehavior.TYPE);
        }
        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (player != null && player.isShiftKeyDown()) {
            stack.remove(SimDataComponents.ROPE_FIRST_CONNECTION);
            return InteractionResult.SUCCESS;
        }

        if (isValidAttachment(level, clickedPos)) {
            if (stack.has(SimDataComponents.ROPE_FIRST_CONNECTION)) {
                if (!level.isClientSide) {
                    BlockPos firstPos = stack.get(SimDataComponents.ROPE_FIRST_CONNECTION);
                    if (!attachRope(level, firstPos, clickedPos)) {
                        stack.remove(SimDataComponents.ROPE_FIRST_CONNECTION);
                        return InteractionResult.SUCCESS;
                    }
                    SimAdvancements.LEARNING_THE_ROPES.awardTo(player);
                }
                stack.remove(SimDataComponents.ROPE_FIRST_CONNECTION);
                if (!player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            stack.set(SimDataComponents.ROPE_FIRST_CONNECTION, clickedPos);
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    private boolean attachRope(Level level, BlockPos posA, BlockPos posB) {
        RopeStrandHolderBehavior holderA = getRopeHolder(level, posA);
        if (holderA == null) return false;
        RopeStrandHolderBehavior holderB = getRopeHolder(level, posB);
        if (holderB == null) return false;

        // Winch must always be holderA
        if (holderB.blockEntity instanceof RopeWinchBlockEntity
                && !(holderA.blockEntity instanceof RopeWinchBlockEntity)) {
            RopeStrandHolderBehavior tmp = holderA;
            holderA = holderB;
            holderB = tmp;
        }
        if (holderA.blockEntity instanceof RopeWinchBlockEntity
                && holderB.blockEntity instanceof RopeWinchBlockEntity) {
            return false;
        }

        if (holderA.createRope(holderB)) {
            level.playSound(null, posA, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);
            level.playSound(null, posB, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);
            return true;
        }
        return false;
    }
}