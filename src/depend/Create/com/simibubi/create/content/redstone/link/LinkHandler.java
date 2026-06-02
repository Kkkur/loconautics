/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.LogicalSide
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.content.redstone.link;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.RaycastHelper;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class LinkHandler {
    @SubscribeEvent
    public static void onBlockActivated(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        if (player.isShiftKeyDown() || player.isSpectator()) {
            return;
        }
        LinkBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)world, pos, LinkBehaviour.TYPE);
        if (behaviour == null) {
            return;
        }
        ItemStack heldItem = player.getItemInHand(hand);
        BlockHitResult ray = RaycastHelper.rayTraceRange(world, player, 10.0);
        if (ray == null) {
            return;
        }
        if (AllItems.LINKED_CONTROLLER.isIn(heldItem)) {
            return;
        }
        if (AllItems.WRENCH.isIn(heldItem)) {
            return;
        }
        boolean fakePlayer = player instanceof FakePlayer;
        boolean fakePlayerChoice = false;
        if (fakePlayer) {
            BlockState blockState = world.getBlockState(pos);
            Vec3 localHit = ray.getLocation().subtract(Vec3.atLowerCornerOf((Vec3i)pos)).add(Vec3.atLowerCornerOf((Vec3i)ray.getDirection().getNormal()).scale(0.25));
            fakePlayerChoice = localHit.distanceToSqr(behaviour.firstSlot.getLocalOffset((LevelAccessor)world, pos, blockState)) > localHit.distanceToSqr(behaviour.secondSlot.getLocalOffset((LevelAccessor)world, pos, blockState));
        }
        for (boolean first : Arrays.asList(false, true)) {
            if (!behaviour.testHit(first, ray.getLocation()) && (!fakePlayer || fakePlayerChoice != first)) continue;
            if (event.getSide() != LogicalSide.CLIENT) {
                behaviour.setFrequency(first, heldItem);
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            world.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.25f, 0.1f);
        }
    }
}
