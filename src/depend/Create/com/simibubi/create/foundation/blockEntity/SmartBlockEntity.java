/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  net.createmod.ponder.api.VirtualBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.common.NeoForge
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.blockEntity;

import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.api.schematic.nbt.PartialSafeNBT;
import com.simibubi.create.api.schematic.requirement.SpecialBlockEntityItemRequirement;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.CachedRenderBBBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.IInteractionChecker;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.createmod.ponder.api.VirtualBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

public abstract class SmartBlockEntity
extends CachedRenderBBBlockEntity
implements PartialSafeNBT,
IInteractionChecker,
SpecialBlockEntityItemRequirement,
VirtualBlockEntity {
    private final Map<BehaviourType<?>, BlockEntityBehaviour> behaviours = new Reference2ObjectArrayMap();
    private boolean initialized = false;
    private boolean firstNbtRead = true;
    protected int lazyTickRate;
    protected int lazyTickCounter;
    private boolean chunkUnloaded;
    private boolean virtualMode;

    public SmartBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(10);
        ArrayList<BlockEntityBehaviour> list = new ArrayList<BlockEntityBehaviour>();
        this.addBehaviours(list);
        list.forEach(b -> this.behaviours.put(b.getType(), (BlockEntityBehaviour)b));
    }

    public abstract void addBehaviours(List<BlockEntityBehaviour> var1);

    public void addBehavioursDeferred(List<BlockEntityBehaviour> behaviours) {
    }

    public void initialize() {
        if (this.firstNbtRead) {
            this.firstNbtRead = false;
            NeoForge.EVENT_BUS.post((Event)new BlockEntityBehaviourEvent(this, this.behaviours));
        }
        this.forEachBehaviour(BlockEntityBehaviour::initialize);
        this.lazyTick();
    }

    public void tick() {
        if (!this.initialized && this.hasLevel()) {
            this.initialize();
            this.initialized = true;
        }
        if (this.lazyTickCounter-- <= 0) {
            this.lazyTickCounter = this.lazyTickRate;
            this.lazyTick();
        }
        this.forEachBehaviour(BlockEntityBehaviour::tick);
    }

    public void lazyTick() {
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.saveAdditional(tag, registries);
        this.forEachBehaviour(tb -> tb.write(tag, registries, clientPacket));
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.forEachBehaviour(tb -> {
            if (tb.isSafeNBT()) {
                tb.writeSafe(tag, registries);
            }
        });
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.firstNbtRead) {
            this.firstNbtRead = false;
            ArrayList<BlockEntityBehaviour> list = new ArrayList<BlockEntityBehaviour>();
            this.addBehavioursDeferred(list);
            list.forEach(b -> this.behaviours.put(b.getType(), (BlockEntityBehaviour)b));
            NeoForge.EVENT_BUS.post((Event)new BlockEntityBehaviourEvent(this, this.behaviours));
        }
        super.loadAdditional(tag, registries);
        this.forEachBehaviour(tb -> tb.read(tag, registries, clientPacket));
    }

    protected void loadAdditional(@NotNull CompoundTag tag, // Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries) {
        this.read(tag, registries, false);
    }

    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.chunkUnloaded = true;
    }

    public final void setRemoved() {
        super.setRemoved();
        if (!this.chunkUnloaded) {
            this.remove();
        }
        this.invalidate();
    }

    public void invalidate() {
        this.forEachBehaviour(BlockEntityBehaviour::unload);
    }

    public void remove() {
    }

    public void destroy() {
        this.forEachBehaviour(BlockEntityBehaviour::destroy);
    }

    public final void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.write(tag, registries, false);
    }

    @Override
    public final void readClient(CompoundTag tag, HolderLookup.Provider registries) {
        this.read(tag, registries, true);
    }

    @Override
    public final CompoundTag writeClient(CompoundTag tag, HolderLookup.Provider registries) {
        this.write(tag, registries, true);
        return tag;
    }

    public <T extends BlockEntityBehaviour> T getBehaviour(BehaviourType<T> type) {
        return (T)this.behaviours.get(type);
    }

    public void forEachBehaviour(Consumer<BlockEntityBehaviour> action) {
        this.getAllBehaviours().forEach(action);
    }

    public Collection<BlockEntityBehaviour> getAllBehaviours() {
        return this.behaviours.values();
    }

    public void attachBehaviourLate(BlockEntityBehaviour behaviour) {
        this.behaviours.put(behaviour.getType(), behaviour);
        behaviour.blockEntity = this;
        behaviour.initialize();
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state) {
        return this.getAllBehaviours().stream().reduce(ItemRequirement.NONE, (r, b) -> r.union(b.getRequiredItems()), ItemRequirement::union);
    }

    public void removeBehaviour(BehaviourType<?> type) {
        BlockEntityBehaviour remove = this.behaviours.remove(type);
        if (remove != null) {
            remove.unload();
        }
    }

    public void setLazyTickRate(int slowTickRate) {
        this.lazyTickRate = slowTickRate;
        this.lazyTickCounter = slowTickRate;
    }

    public void markVirtual() {
        this.virtualMode = true;
    }

    public boolean isVirtual() {
        return this.virtualMode;
    }

    public boolean isChunkUnloaded() {
        return this.chunkUnloaded;
    }

    @Override
    public boolean canPlayerUse(Player player) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    public void sendToMenu(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.getBlockPos());
        buffer.writeNbt((Tag)this.getUpdateTag((HolderLookup.Provider)buffer.registryAccess()));
    }

    public void refreshBlockState() {
        this.setBlockState(this.getLevel().getBlockState(this.getBlockPos()));
    }

    public void registerAwardables(List<BlockEntityBehaviour> behaviours, CreateAdvancement ... advancements) {
        for (BlockEntityBehaviour behaviour : behaviours) {
            if (!(behaviour instanceof AdvancementBehaviour)) continue;
            AdvancementBehaviour ab = (AdvancementBehaviour)behaviour;
            ab.add(advancements);
            return;
        }
        behaviours.add(new AdvancementBehaviour(this, advancements));
    }

    public void award(CreateAdvancement advancement) {
        AdvancementBehaviour behaviour = this.getBehaviour(AdvancementBehaviour.TYPE);
        if (behaviour != null) {
            behaviour.awardPlayer(advancement);
        }
    }

    public void awardIfNear(CreateAdvancement advancement, int range) {
        AdvancementBehaviour behaviour = this.getBehaviour(AdvancementBehaviour.TYPE);
        if (behaviour != null) {
            behaviour.awardPlayerIfNear(advancement, range);
        }
    }
}
