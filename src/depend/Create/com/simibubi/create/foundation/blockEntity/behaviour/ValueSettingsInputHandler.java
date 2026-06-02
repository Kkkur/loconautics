/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.LogicalSide
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.common.Tags$Items
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.SidedFilteringBehaviour;
import com.simibubi.create.foundation.utility.AdventureUtil;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class ValueSettingsInputHandler {
    @SubscribeEvent
    public static void onBlockActivated(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        if (!ValueSettingsInputHandler.canInteract(player)) {
            return;
        }
        if (AllBlocks.CLIPBOARD.isIn(player.getMainHandItem())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof SmartBlockEntity)) {
            return;
        }
        SmartBlockEntity sbe = (SmartBlockEntity)blockEntity;
        if (event.getSide() == LogicalSide.CLIENT) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> CreateClient.VALUE_SETTINGS_HANDLER.cancelIfWarmupAlreadyStarted(event));
        }
        if (event.isCanceled()) {
            return;
        }
        for (BlockEntityBehaviour behaviour : sbe.getAllBehaviours()) {
            ValueSettingsBehaviour valueSettingsBehaviour;
            if (!(behaviour instanceof ValueSettingsBehaviour) || (valueSettingsBehaviour = (ValueSettingsBehaviour)((Object)behaviour)).bypassesInput(player.getMainHandItem()) || !valueSettingsBehaviour.mayInteract(player)) continue;
            BlockHitResult ray = event.getHitVec();
            if (ray == null) {
                return;
            }
            if (behaviour instanceof SidedFilteringBehaviour && (behaviour = ((SidedFilteringBehaviour)behaviour).get(ray.getDirection())) == null || !valueSettingsBehaviour.isActive() || valueSettingsBehaviour.onlyVisibleWithWrench() && !player.getItemInHand(hand).is(Tags.Items.TOOLS_WRENCH)) continue;
            ValueBoxTransform valueBoxTransform = valueSettingsBehaviour.getSlotPositioning();
            if (valueBoxTransform instanceof ValueBoxTransform.Sided) {
                ValueBoxTransform.Sided sidedSlot = (ValueBoxTransform.Sided)valueBoxTransform;
                if (!sidedSlot.isSideActive(sbe.getBlockState(), ray.getDirection())) continue;
                sidedSlot.fromSide(ray.getDirection());
            }
            boolean fakePlayer = player instanceof FakePlayer;
            if (!valueSettingsBehaviour.testHit(ray.getLocation()) && !fakePlayer) continue;
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            if (!valueSettingsBehaviour.acceptsValueSettings() || fakePlayer) {
                valueSettingsBehaviour.onShortInteract(player, hand, ray.getDirection(), ray);
                return;
            }
            if (event.getSide() == LogicalSide.CLIENT) {
                BehaviourType<?> type = behaviour.getType();
                CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> CreateClient.VALUE_SETTINGS_HANDLER.startInteractionWith(pos, type, hand, ray.getDirection()));
            }
            return;
        }
    }

    public static boolean canInteract(Player player) {
        return player != null && !player.isSpectator() && !player.isShiftKeyDown() && !AdventureUtil.isAdventure(player);
    }
}
