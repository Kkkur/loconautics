/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.blockEntity.behaviour.filtering;

import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SidedFilteringBehaviour
extends FilteringBehaviour {
    Map<Direction, FilteringBehaviour> sidedFilters;
    private BiFunction<Direction, FilteringBehaviour, FilteringBehaviour> filterFactory;
    private Predicate<Direction> validDirections;

    public SidedFilteringBehaviour(SmartBlockEntity be, ValueBoxTransform.Sided sidedSlot, BiFunction<Direction, FilteringBehaviour, FilteringBehaviour> filterFactory, Predicate<Direction> validDirections) {
        super(be, sidedSlot);
        this.filterFactory = filterFactory;
        this.validDirections = validDirections;
        this.sidedFilters = new IdentityHashMap<Direction, FilteringBehaviour>();
        this.updateFilterPresence();
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    public FilteringBehaviour get(Direction side) {
        return this.sidedFilters.get(side);
    }

    public void updateFilterPresence() {
        HashSet<Direction> valid = new HashSet<Direction>();
        for (Direction d : Iterate.directions) {
            if (!this.validDirections.test(d)) continue;
            valid.add(d);
        }
        for (Direction d : Iterate.directions) {
            if (valid.contains(d)) {
                if (this.sidedFilters.containsKey(d)) continue;
                this.sidedFilters.put(d, this.filterFactory.apply(d, new FilteringBehaviour(this.blockEntity, this.slotPositioning)));
                continue;
            }
            if (!this.sidedFilters.containsKey(d)) continue;
            this.removeFilter(d);
        }
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.put("Filters", (Tag)NBTHelper.writeCompoundList(this.sidedFilters.entrySet(), entry -> {
            CompoundTag compound = new CompoundTag();
            compound.putInt("Side", ((Direction)entry.getKey()).get3DDataValue());
            ((FilteringBehaviour)entry.getValue()).write(compound, registries, clientPacket);
            return compound;
        }));
        super.write(nbt, registries, clientPacket);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        NBTHelper.iterateCompoundList((ListTag)nbt.getList("Filters", 10), compound -> {
            Direction face = Direction.from3DDataValue((int)compound.getInt("Side"));
            if (this.sidedFilters.containsKey(face)) {
                this.sidedFilters.get(face).read((CompoundTag)compound, registries, clientPacket);
            }
        });
        super.read(nbt, registries, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        this.sidedFilters.values().forEach(BlockEntityBehaviour::tick);
    }

    @Override
    public boolean setFilter(Direction side, ItemStack stack) {
        if (!this.sidedFilters.containsKey(side)) {
            return true;
        }
        this.sidedFilters.get(side).setFilter(stack);
        return true;
    }

    @Override
    public ItemStack getFilter(Direction side) {
        if (!this.sidedFilters.containsKey(side)) {
            return ItemStack.EMPTY;
        }
        return this.sidedFilters.get(side).getFilter();
    }

    public boolean test(Direction side, ItemStack stack) {
        if (!this.sidedFilters.containsKey(side)) {
            return true;
        }
        return this.sidedFilters.get(side).test(stack);
    }

    @Override
    public void destroy() {
        this.sidedFilters.values().forEach(FilteringBehaviour::destroy);
        super.destroy();
    }

    @Override
    public ItemRequirement getRequiredItems() {
        return this.sidedFilters.values().stream().reduce(ItemRequirement.NONE, (a, b) -> a.union(b.getRequiredItems()), (a, b) -> a.union((ItemRequirement)b));
    }

    public void removeFilter(Direction side) {
        if (!this.sidedFilters.containsKey(side)) {
            return;
        }
        this.sidedFilters.remove(side).destroy();
    }

    public boolean testHit(LevelAccessor level, BlockPos pos, Direction direction, Vec3 hit) {
        ValueBoxTransform.Sided sidedPositioning = (ValueBoxTransform.Sided)this.slotPositioning;
        BlockState state = this.blockEntity.getBlockState();
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf((Vec3i)this.blockEntity.getBlockPos()));
        return sidedPositioning.fromSide(direction).testHit(level, pos, state, localHit);
    }
}
