/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import java.util.ConcurrentModificationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityBehaviour {
    public SmartBlockEntity blockEntity;
    private int lazyTickRate;
    private int lazyTickCounter;

    public BlockEntityBehaviour(SmartBlockEntity be) {
        this.blockEntity = be;
        this.setLazyTickRate(10);
    }

    public abstract BehaviourType<?> getType();

    public void initialize() {
    }

    public void tick() {
        if (this.lazyTickCounter-- <= 0) {
            this.lazyTickCounter = this.lazyTickRate;
            this.lazyTick();
        }
    }

    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
    }

    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
    }

    public void writeSafe(CompoundTag nbt, HolderLookup.Provider registries) {
        this.write(nbt, registries, false);
    }

    public boolean isSafeNBT() {
        return false;
    }

    public ItemRequirement getRequiredItems() {
        return ItemRequirement.NONE;
    }

    public void onBlockChanged(BlockState oldState) {
    }

    public void onNeighborChanged(BlockPos neighborPos) {
    }

    public void unload() {
    }

    public void destroy() {
    }

    public void setLazyTickRate(int slowTickRate) {
        this.lazyTickRate = slowTickRate;
        this.lazyTickCounter = slowTickRate;
    }

    public void lazyTick() {
    }

    public BlockPos getPos() {
        return this.blockEntity.getBlockPos();
    }

    public Level getWorld() {
        return this.blockEntity.getLevel();
    }

    public static <T extends BlockEntityBehaviour> T get(BlockGetter reader, BlockPos pos, BehaviourType<T> type) {
        BlockEntity be;
        try {
            be = reader.getBlockEntity(pos);
        }
        catch (ConcurrentModificationException e) {
            be = null;
        }
        return BlockEntityBehaviour.get(be, type);
    }

    public static <T extends BlockEntityBehaviour> T get(BlockEntity be, BehaviourType<T> type) {
        if (be == null) {
            return null;
        }
        if (!(be instanceof SmartBlockEntity)) {
            return null;
        }
        SmartBlockEntity ste = (SmartBlockEntity)be;
        return ste.getBehaviour(type);
    }
}
