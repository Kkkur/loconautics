/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.content.schematics.cannon;

import com.simibubi.create.content.schematics.cannon.LaunchedItem;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public static class LaunchedItem.ForEntity
extends LaunchedItem {
    public Entity entity;
    private CompoundTag deferredTag;

    LaunchedItem.ForEntity() {
    }

    public LaunchedItem.ForEntity(BlockPos start, BlockPos target, ItemStack stack, Entity entity) {
        super(start, target, stack);
        this.entity = entity;
    }

    @Override
    public boolean update(Level world) {
        if (this.deferredTag != null && this.entity == null) {
            try {
                Optional loadEntityUnchecked = EntityType.create((CompoundTag)this.deferredTag, (Level)world);
                if (!loadEntityUnchecked.isPresent()) {
                    return true;
                }
                this.entity = (Entity)loadEntityUnchecked.get();
            }
            catch (Exception var3) {
                return true;
            }
            this.deferredTag = null;
        }
        return super.update(world);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag serializeNBT = super.serializeNBT(registries);
        if (this.entity != null) {
            serializeNBT.put("Entity", (Tag)this.entity.serializeNBT(registries));
        }
        return serializeNBT;
    }

    @Override
    void readNBT(CompoundTag nbt, HolderLookup.Provider registries, HolderGetter<Block> holderGetter) {
        super.readNBT(nbt, registries, holderGetter);
        if (nbt.contains("Entity")) {
            this.deferredTag = nbt.getCompound("Entity");
        }
    }

    @Override
    void place(Level world) {
        if (this.entity != null) {
            world.addFreshEntity(this.entity);
        }
    }
}
