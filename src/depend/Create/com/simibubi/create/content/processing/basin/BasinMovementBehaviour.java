/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.processing.basin;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BasinMovementBehaviour
implements MovementBehaviour {
    public Map<String, ItemStackHandler> getOrReadInventory(MovementContext context) {
        HashMap<String, ItemStackHandler> map = new HashMap<String, ItemStackHandler>();
        map.put("InputItems", new ItemStackHandler(9));
        map.put("OutputItems", new ItemStackHandler(8));
        map.forEach((s, h) -> h.deserializeNBT((HolderLookup.Provider)context.world.registryAccess(), context.blockEntityData.getCompound(s)));
        return map;
    }

    @Override
    public void tick(MovementContext context) {
        MovementBehaviour.super.tick(context);
        if (context.temporaryData == null || ((Boolean)context.temporaryData).booleanValue()) {
            Vec3 facingVec = (Vec3)context.rotation.apply(Vec3.atLowerCornerOf((Vec3i)Direction.UP.getNormal()));
            facingVec.normalize();
            if (Direction.getNearest((double)facingVec.x, (double)facingVec.y, (double)facingVec.z) == Direction.DOWN) {
                this.dump(context, facingVec);
            }
        }
    }

    private void dump(MovementContext context, Vec3 facingVec) {
        BlockEntity blockEntity;
        this.getOrReadInventory(context).forEach((key, itemStackHandler) -> {
            for (int i = 0; i < itemStackHandler.getSlots(); ++i) {
                if (itemStackHandler.getStackInSlot(i).isEmpty()) continue;
                ItemEntity itemEntity = new ItemEntity(context.world, context.position.x, context.position.y, context.position.z, itemStackHandler.getStackInSlot(i));
                itemEntity.setDeltaMovement(facingVec.scale(0.05));
                context.world.addFreshEntity((Entity)itemEntity);
                itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
            }
            context.blockEntityData.put(key, (Tag)itemStackHandler.serializeNBT((HolderLookup.Provider)context.world.registryAccess()));
        });
        if (context.contraption.entity.level().isClientSide && (blockEntity = context.contraption.getBlockEntityClientSide(context.localPos)) instanceof BasinBlockEntity) {
            ((BasinBlockEntity)blockEntity).readOnlyItems(context.blockEntityData, (HolderLookup.Provider)context.world.registryAccess());
        }
        context.temporaryData = false;
    }
}
