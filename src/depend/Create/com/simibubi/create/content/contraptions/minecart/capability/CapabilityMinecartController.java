/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectLists
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
 *  net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$StartTracking
 *  net.neoforged.neoforge.event.level.ChunkEvent$Unload
 *  net.neoforged.neoforge.event.tick.EntityTickEvent
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.minecart.capability;

import com.simibubi.create.AllAttachmentTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.minecart.CouplingHandler;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;

public class CapabilityMinecartController {
    public static WorldAttached<Map<UUID, MinecartController>> loadedMinecartsByUUID = new WorldAttached($ -> new HashMap());
    public static WorldAttached<Set<UUID>> loadedMinecartsWithCoupling = new WorldAttached($ -> new HashSet());
    static WorldAttached<List<AbstractMinecart>> queuedAdditions = new WorldAttached($ -> ObjectLists.synchronize((ObjectList)new ObjectArrayList()));
    static WorldAttached<List<UUID>> queuedUnloads = new WorldAttached($ -> ObjectLists.synchronize((ObjectList)new ObjectArrayList()));

    public static void tick(Level world) {
        MinecartController controller;
        Map carts = (Map)loadedMinecartsByUUID.get((LevelAccessor)world);
        List queued = (List)queuedAdditions.get((LevelAccessor)world);
        List queuedRemovals = (List)queuedUnloads.get((LevelAccessor)world);
        Set cartsWithCoupling = (Set)loadedMinecartsWithCoupling.get((LevelAccessor)world);
        Set keySet = carts.keySet();
        for (UUID removal : queuedRemovals) {
            keySet.remove(removal);
            cartsWithCoupling.remove(removal);
        }
        for (AbstractMinecart cart : queued) {
            AbstractMinecart minecartEntity;
            MinecartController minecartController;
            UUID uniqueID = cart.getUUID();
            if (world.isClientSide && carts.containsKey(uniqueID) && (minecartController = (MinecartController)carts.get(uniqueID)) != null && (minecartEntity = minecartController.cart()) != null && minecartEntity.getId() != cart.getId()) continue;
            cartsWithCoupling.remove(uniqueID);
            controller = (MinecartController)cart.getData(AllAttachmentTypes.MINECART_CONTROLLER);
            if (controller == MinecartController.EMPTY) continue;
            carts.put(uniqueID, controller);
            if (controller.isLeadingCoupling()) {
                cartsWithCoupling.add(uniqueID);
            }
            if (world.isClientSide || controller == null) continue;
            controller.sendData();
        }
        queuedRemovals.clear();
        queued.clear();
        ArrayList<UUID> toRemove = new ArrayList<UUID>();
        for (Map.Entry entry : carts.entrySet()) {
            controller = (MinecartController)entry.getValue();
            if (controller != null && controller.isPresent()) continue;
            toRemove.add((UUID)entry.getKey());
        }
        for (UUID uuid : toRemove) {
            keySet.remove(uuid);
            cartsWithCoupling.remove(uuid);
        }
    }

    public static void entityTick(EntityTickEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof AbstractMinecart)) {
            return;
        }
        MinecartController data = (MinecartController)entity.getData(AllAttachmentTypes.MINECART_CONTROLLER);
        if (data != MinecartController.EMPTY) {
            data.tick();
        }
    }

    public static void onChunkUnloaded(ChunkEvent.Unload event) {
        ChunkPos chunkPos = event.getChunk().getPos();
        Map carts = (Map)loadedMinecartsByUUID.get(event.getLevel());
        for (MinecartController minecartController : carts.values()) {
            AbstractMinecart cart;
            if (minecartController == null || !minecartController.isPresent() || !(cart = minecartController.cart()).chunkPosition().equals((Object)chunkPos)) continue;
            ((List)queuedUnloads.get(event.getLevel())).add(cart.getUUID());
        }
    }

    protected static void onCartRemoved(Level world, AbstractMinecart entity) {
        entity.removeData(AllAttachmentTypes.MINECART_CONTROLLER);
        Map carts = (Map)loadedMinecartsByUUID.get((LevelAccessor)world);
        List unloads = (List)queuedUnloads.get((LevelAccessor)world);
        UUID uniqueID = entity.getUUID();
        if (!carts.containsKey(uniqueID) || unloads.contains(uniqueID)) {
            return;
        }
        if (world.isClientSide) {
            return;
        }
        CapabilityMinecartController.handleKilledMinecart(world, (MinecartController)carts.get(uniqueID), entity.position());
    }

    protected static void handleKilledMinecart(Level world, MinecartController controller, Vec3 removedPos) {
        if (controller == null) {
            return;
        }
        for (boolean forward : Iterate.trueAndFalse) {
            AbstractMinecart cart;
            MinecartController next = CouplingHandler.getNextInCouplingChain(world, controller, forward);
            if (next == null || next == MinecartController.EMPTY) continue;
            next.removeConnection(!forward);
            if (controller.hasContraptionCoupling(forward) || (cart = next.cart()) == null) continue;
            Vec3 itemPos = cart.position().add(removedPos).scale(0.5);
            ItemEntity itemEntity = new ItemEntity(world, itemPos.x, itemPos.y, itemPos.z, AllItems.MINECART_COUPLING.asStack());
            itemEntity.setDefaultPickUpDelay();
            world.addFreshEntity((Entity)itemEntity);
        }
    }

    @Nullable
    public static MinecartController getIfPresent(Level world, UUID cartId) {
        Map carts = (Map)loadedMinecartsByUUID.get((LevelAccessor)world);
        if (carts == null) {
            return null;
        }
        if (!carts.containsKey(cartId)) {
            return null;
        }
        return (MinecartController)carts.get(cartId);
    }

    public static void attach(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof AbstractMinecart)) {
            return;
        }
        AbstractMinecart abstractMinecart = (AbstractMinecart)entity;
        if (event.loadedFromDisk()) {
            return;
        }
        MinecartController controller = new MinecartController(abstractMinecart);
        abstractMinecart.setData(AllAttachmentTypes.MINECART_CONTROLLER, (Object)controller);
        ((List)queuedAdditions.get((LevelAccessor)entity.level())).add(abstractMinecart);
    }

    public static void onEntityDeath(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof AbstractMinecart) {
            AbstractMinecart abstractMinecart = (AbstractMinecart)entity;
            CapabilityMinecartController.onCartRemoved(event.getLevel(), abstractMinecart);
        }
    }

    public static void startTracking(PlayerEvent.StartTracking event) {
        Entity entity = event.getTarget();
        if (!(entity instanceof AbstractMinecart)) {
            return;
        }
        AbstractMinecart abstractMinecart = (AbstractMinecart)entity;
        MinecartController controller = (MinecartController)entity.getData(AllAttachmentTypes.MINECART_CONTROLLER);
        if (controller != MinecartController.EMPTY) {
            controller.sendData(abstractMinecart);
        }
    }
}
