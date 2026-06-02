/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$EntityInteract
 */
package com.simibubi.create.content.contraptions.minecart;

import com.simibubi.create.AllAttachmentTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.minecart.CouplingHandler;
import com.simibubi.create.content.contraptions.minecart.CouplingHandlerClient;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class MinecartCouplingItem
extends Item {
    public MinecartCouplingItem(Item.Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void handleInteractionWithMinecart(PlayerInteractEvent.EntityInteract event) {
        Entity interacted = event.getTarget();
        if (!(interacted instanceof AbstractMinecart)) {
            return;
        }
        AbstractMinecart minecart = (AbstractMinecart)interacted;
        Player player = event.getEntity();
        if (player == null) {
            return;
        }
        MinecartController controller = (MinecartController)minecart.getData(AllAttachmentTypes.MINECART_CONTROLLER);
        if (controller == MinecartController.EMPTY || !controller.isPresent()) {
            return;
        }
        ItemStack heldItem = player.getItemInHand(event.getHand());
        if (AllItems.MINECART_COUPLING.isIn(heldItem)) {
            if (!MinecartCouplingItem.onCouplingInteractOnMinecart(event, minecart, player, controller)) {
                return;
            }
        } else if (AllItems.WRENCH.isIn(heldItem)) {
            if (!MinecartCouplingItem.onWrenchInteractOnMinecart(event, minecart, player, controller)) {
                return;
            }
        } else {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    protected static boolean onCouplingInteractOnMinecart(PlayerInteractEvent.EntityInteract event, AbstractMinecart minecart, Player player, MinecartController controller) {
        Level world = event.getLevel();
        if (controller.isFullyCoupled()) {
            if (!world.isClientSide) {
                CouplingHandler.status(player, "two_couplings_max");
            }
            return true;
        }
        if (world != null && world.isClientSide) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> MinecartCouplingItem.cartClicked(player, minecart));
        }
        return true;
    }

    private static boolean onWrenchInteractOnMinecart(PlayerInteractEvent.EntityInteract event, AbstractMinecart minecart, Player player, MinecartController controller) {
        int couplings = (controller.isConnectedToCoupling() ? 1 : 0) + (controller.isLeadingCoupling() ? 1 : 0);
        if (couplings == 0) {
            return false;
        }
        if (event.getLevel().isClientSide) {
            return true;
        }
        for (boolean forward : Iterate.trueAndFalse) {
            if (!controller.hasContraptionCoupling(forward)) continue;
            --couplings;
        }
        CouplingHandler.status(player, "removed");
        controller.decouple();
        if (!player.isCreative()) {
            player.getInventory().placeItemBackInInventory(new ItemStack((ItemLike)AllItems.MINECART_COUPLING.get(), couplings));
        }
        return true;
    }

    @OnlyIn(value=Dist.CLIENT)
    private static void cartClicked(Player player, AbstractMinecart interacted) {
        CouplingHandlerClient.onCartClicked(player, interacted);
    }
}
