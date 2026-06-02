/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.depot;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.logistics.depot.storage.DepotMountedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class MountedDepotInteractionBehaviour
extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        ItemStack itemInHand = player.getItemInHand(activeHand);
        if (activeHand == InteractionHand.OFF_HAND) {
            return false;
        }
        if (player.level().isClientSide) {
            return true;
        }
        MountedStorageManager manager = contraptionEntity.getContraption().getStorage();
        MountedItemStorage storage = (MountedItemStorage)manager.getAllItemStorages().get((Object)localPos);
        if (!(storage instanceof DepotMountedStorage)) {
            return false;
        }
        DepotMountedStorage depot = (DepotMountedStorage)storage;
        ItemStack itemOnDepot = depot.getItem();
        if (itemOnDepot.isEmpty() && itemInHand.isEmpty()) {
            return true;
        }
        depot.setItem(itemInHand.copy());
        player.setItemInHand(activeHand, itemOnDepot.copy());
        AllSoundEvents.DEPOT_PLOP.playOnServer(player.level(), (Vec3i)BlockPos.containing((Position)contraptionEntity.toGlobalVector(Vec3.atCenterOf((Vec3i)localPos), 0.0f)));
        return true;
    }
}
