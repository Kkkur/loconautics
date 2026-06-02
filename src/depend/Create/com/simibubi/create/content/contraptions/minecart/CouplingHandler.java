/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.EntityMountEvent
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.minecart;

import com.simibubi.create.AllAttachmentTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class CouplingHandler {
    @SubscribeEvent
    public static void preventEntitiesFromMoutingOccupiedCart(EntityMountEvent event) {
        Entity e = event.getEntityBeingMounted();
        MinecartController controller = (MinecartController)e.getData(AllAttachmentTypes.MINECART_CONTROLLER);
        if (controller != MinecartController.EMPTY) {
            if (event.getEntityMounting() instanceof AbstractContraptionEntity) {
                return;
            }
            if (controller.isCoupledThroughContraption()) {
                event.setCanceled(true);
            }
        }
    }

    public static void forEachLoadedCoupling(Level world, Consumer<Couple<MinecartController>> consumer) {
        if (world == null) {
            return;
        }
        Set cartsWithCoupling = (Set)CapabilityMinecartController.loadedMinecartsWithCoupling.get((LevelAccessor)world);
        if (cartsWithCoupling == null) {
            return;
        }
        for (UUID id : cartsWithCoupling) {
            MinecartController controller = CapabilityMinecartController.getIfPresent(world, id);
            if (controller == null) {
                return;
            }
            if (!controller.isLeadingCoupling()) {
                return;
            }
            UUID coupledCart = controller.getCoupledCart(true);
            MinecartController coupledController = CapabilityMinecartController.getIfPresent(world, coupledCart);
            if (coupledController == null) {
                return;
            }
            consumer.accept((Couple<MinecartController>)Couple.create((Object)controller, (Object)coupledController));
        }
    }

    public static boolean tryToCoupleCarts(@Nullable Player player, Level world, int cartId1, int cartId2) {
        boolean contraptionCoupling;
        Entity entity1 = world.getEntity(cartId1);
        Entity entity2 = world.getEntity(cartId2);
        if (!(entity1 instanceof AbstractMinecart)) {
            return false;
        }
        AbstractMinecart cart1 = (AbstractMinecart)entity1;
        if (!(entity2 instanceof AbstractMinecart)) {
            return false;
        }
        AbstractMinecart cart2 = (AbstractMinecart)entity2;
        String tooMany = "two_couplings_max";
        String unloaded = "unloaded";
        String noLoops = "no_loops";
        String tooFar = "too_far";
        int distanceTo = (int)entity1.position().distanceTo(entity2.position());
        boolean bl = contraptionCoupling = player == null;
        if (distanceTo < 2) {
            if (contraptionCoupling) {
                return false;
            }
            distanceTo = 2;
        }
        if (distanceTo > (Integer)AllConfigs.server().kinetics.maxCartCouplingLength.get()) {
            CouplingHandler.status(player, tooFar);
            return false;
        }
        UUID mainID = cart1.getUUID();
        UUID connectedID = cart2.getUUID();
        MinecartController mainController = CapabilityMinecartController.getIfPresent(world, mainID);
        MinecartController connectedController = CapabilityMinecartController.getIfPresent(world, connectedID);
        if (mainController == null || connectedController == null) {
            CouplingHandler.status(player, unloaded);
            return false;
        }
        if (mainController.isFullyCoupled() || connectedController.isFullyCoupled()) {
            CouplingHandler.status(player, tooMany);
            return false;
        }
        if (mainController.isLeadingCoupling() && mainController.getCoupledCart(true).equals(connectedID) || connectedController.isLeadingCoupling() && connectedController.getCoupledCart(true).equals(mainID)) {
            return false;
        }
        for (boolean main : Iterate.trueAndFalse) {
            MinecartController current = main ? mainController : connectedController;
            boolean forward = current.isLeadingCoupling();
            int safetyCount = 1000;
            do {
                if (safetyCount-- <= 0) {
                    Create.LOGGER.warn("Infinite loop in coupling iteration");
                    return false;
                }
                if ((current = CouplingHandler.getNextInCouplingChain(world, current, forward)) == null) {
                    CouplingHandler.status(player, unloaded);
                    return false;
                }
                if (current != connectedController) continue;
                CouplingHandler.status(player, noLoops);
                return false;
            } while (current != MinecartController.EMPTY);
        }
        if (!contraptionCoupling) {
            for (InteractionHand hand : InteractionHand.values()) {
                if (player.isCreative()) break;
                ItemStack heldItem = player.getItemInHand(hand);
                if (!AllItems.MINECART_COUPLING.isIn(heldItem)) continue;
                heldItem.shrink(1);
                break;
            }
        }
        mainController.prepareForCoupling(true);
        connectedController.prepareForCoupling(false);
        mainController.coupleWith(true, connectedID, distanceTo, contraptionCoupling);
        connectedController.coupleWith(false, mainID, distanceTo, contraptionCoupling);
        return true;
    }

    @Nullable
    public static MinecartController getNextInCouplingChain(Level world, MinecartController controller, boolean forward) {
        UUID coupledCart = controller.getCoupledCart(forward);
        if (coupledCart == null) {
            return MinecartController.EMPTY;
        }
        return CapabilityMinecartController.getIfPresent(world, coupledCart);
    }

    public static void status(Player player, String key) {
        if (player == null) {
            return;
        }
        player.displayClientMessage((Component)CreateLang.translateDirect("minecart_coupling." + key, new Object[0]), true);
    }
}
