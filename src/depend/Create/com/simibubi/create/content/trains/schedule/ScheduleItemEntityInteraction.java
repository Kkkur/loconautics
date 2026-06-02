/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$EntityInteractSpecific
 */
package com.simibubi.create.content.trains.schedule;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.ScheduleItem;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class ScheduleItemEntityInteraction {
    @SubscribeEvent
    public static void interactWithConductor(PlayerInteractEvent.EntityInteractSpecific event) {
        boolean onServer;
        ScheduleItem si;
        InteractionResult result;
        Entity entity = event.getTarget();
        Player player = event.getEntity();
        if (player == null || entity == null) {
            return;
        }
        if (player.isSpectator()) {
            return;
        }
        Entity rootVehicle = entity.getRootVehicle();
        if (!(rootVehicle instanceof CarriageContraptionEntity)) {
            return;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)rootVehicle;
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity living = (LivingEntity)entity;
        if (player.getCooldowns().isOnCooldown((Item)AllItems.SCHEDULE.get())) {
            return;
        }
        ItemStack itemStack = event.getItemStack();
        Item item = itemStack.getItem();
        if (item instanceof ScheduleItem && (result = (si = (ScheduleItem)item).handScheduleTo(itemStack, player, living, event.getHand())).consumesAction()) {
            player.getCooldowns().addCooldown((Item)AllItems.SCHEDULE.get(), 5);
            event.setCancellationResult(result);
            event.setCanceled(true);
            return;
        }
        if (event.getHand() == InteractionHand.OFF_HAND) {
            return;
        }
        Contraption contraption = cce.getContraption();
        if (!(contraption instanceof CarriageContraption)) {
            return;
        }
        CarriageContraption cc = (CarriageContraption)contraption;
        Train train = cce.getCarriage().train;
        if (train == null) {
            return;
        }
        if (train.runtime.getSchedule() == null) {
            return;
        }
        Integer seatIndex = contraption.getSeatMapping().get(entity.getUUID());
        if (seatIndex == null) {
            return;
        }
        BlockPos seatPos = contraption.getSeats().get(seatIndex);
        Couple<Boolean> directions = cc.conductorSeats.get(seatPos);
        if (directions == null) {
            return;
        }
        boolean bl = onServer = !event.getLevel().isClientSide;
        if (train.runtime.paused && !train.runtime.completed) {
            if (onServer) {
                train.runtime.paused = false;
                AllSoundEvents.CONFIRM.playOnServer(player.level(), (Vec3i)player.blockPosition(), 1.0f, 1.0f);
                player.displayClientMessage((Component)CreateLang.translateDirect("schedule.continued", new Object[0]), true);
            }
            player.getCooldowns().addCooldown((Item)AllItems.SCHEDULE.get(), 5);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }
        ItemStack itemInHand = player.getItemInHand(event.getHand());
        if (!itemInHand.isEmpty()) {
            if (onServer) {
                AllSoundEvents.DENY.playOnServer(player.level(), (Vec3i)player.blockPosition(), 1.0f, 1.0f);
                player.displayClientMessage((Component)CreateLang.translateDirect("schedule.remove_with_empty_hand", new Object[0]), true);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }
        if (onServer) {
            AllSoundEvents.playItemPickup(player);
            player.displayClientMessage((Component)CreateLang.translateDirect(train.runtime.isAutoSchedule ? "schedule.auto_removed_from_train" : "schedule.removed_from_train", new Object[0]), true);
            player.getInventory().placeItemBackInInventory(train.runtime.returnSchedule((HolderLookup.Provider)player.registryAccess()));
        }
        player.getCooldowns().addCooldown((Item)AllItems.SCHEDULE.get(), 5);
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
