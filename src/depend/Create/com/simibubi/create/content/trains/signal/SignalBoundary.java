/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.trains.signal;

import com.google.common.base.Objects;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SignalBoundary
extends TrackEdgePoint {
    public Couple<Map<BlockPos, Boolean>> blockEntities = Couple.create(HashMap::new);
    public Couple<SignalBlock.SignalType> types;
    public Couple<UUID> groups;
    public Couple<Boolean> sidesToUpdate;
    public Couple<SignalBlockEntity.SignalState> cachedStates;
    private Couple<Map<UUID, Boolean>> chainedSignals = Couple.create(null, null);

    public SignalBoundary() {
        this.groups = Couple.create(null, null);
        this.sidesToUpdate = Couple.create((Object)true, (Object)true);
        this.types = Couple.create(() -> SignalBlock.SignalType.ENTRY_SIGNAL);
        this.cachedStates = Couple.create(() -> SignalBlockEntity.SignalState.INVALID);
    }

    public void setGroup(boolean primary, UUID groupId) {
        UUID previous = (UUID)this.groups.get(primary);
        this.groups.set(primary, (Object)groupId);
        UUID opposite = (UUID)this.groups.get(!primary);
        Map<UUID, SignalEdgeGroup> signalEdgeGroups = Create.RAILWAYS.signalEdgeGroups;
        if (opposite != null && signalEdgeGroups.containsKey(opposite)) {
            SignalEdgeGroup oppositeGroup = signalEdgeGroups.get(opposite);
            if (previous != null) {
                oppositeGroup.removeAdjacent(previous);
            }
            if (groupId != null) {
                oppositeGroup.putAdjacent(groupId);
            }
        }
        if (groupId != null && signalEdgeGroups.containsKey(groupId)) {
            SignalEdgeGroup group = signalEdgeGroups.get(groupId);
            if (opposite != null) {
                group.putAdjacent(opposite);
            }
        }
    }

    public void setGroupAndUpdate(TrackNode side, UUID groupId) {
        boolean primary = this.isPrimary(side);
        this.setGroup(primary, groupId);
        this.sidesToUpdate.set(primary, (Object)false);
        this.chainedSignals.set(primary, null);
    }

    @Override
    public boolean canMerge() {
        return true;
    }

    @Override
    public void invalidate(LevelAccessor level) {
        this.blockEntities.forEach(s -> s.keySet().forEach(p -> this.invalidateAt(level, (BlockPos)p)));
        this.groups.forEach(uuid -> {
            if (Create.RAILWAYS.signalEdgeGroups.remove(uuid) != null) {
                Create.RAILWAYS.sync.edgeGroupRemoved((UUID)uuid);
            }
        });
    }

    @Override
    public boolean canCoexistWith(EdgePointType<?> otherType, boolean front) {
        return otherType == this.getType();
    }

    @Override
    public void blockEntityAdded(BlockEntity blockEntity, boolean front) {
        SignalBlockEntity ste;
        Map blockEntitiesOnSide = (Map)this.blockEntities.get(front);
        if (blockEntitiesOnSide.isEmpty()) {
            blockEntity.getBlockState().getOptionalValue(SignalBlock.TYPE).ifPresent(type -> this.types.set(front, (Object)type));
        }
        blockEntitiesOnSide.put(blockEntity.getBlockPos(), blockEntity instanceof SignalBlockEntity && (ste = (SignalBlockEntity)blockEntity).getReportedPower());
    }

    public void updateBlockEntityPower(SignalBlockEntity blockEntity) {
        for (boolean front : Iterate.trueAndFalse) {
            ((Map)this.blockEntities.get(front)).computeIfPresent(blockEntity.getBlockPos(), (p, c) -> blockEntity.getReportedPower());
        }
    }

    @Override
    public void blockEntityRemoved(BlockPos blockEntityPos, boolean front) {
        this.blockEntities.forEach(s -> s.remove(blockEntityPos));
        if (this.blockEntities.both(Map::isEmpty)) {
            this.removeFromAllGraphs();
        }
    }

    @Override
    public void onRemoved(TrackGraph graph) {
        super.onRemoved(graph);
        SignalPropagator.onSignalRemoved(graph, this);
    }

    public void queueUpdate(TrackNode side) {
        this.sidesToUpdate.set(this.isPrimary(side), (Object)true);
    }

    public UUID getGroup(TrackNode side) {
        return (UUID)this.groups.get(this.isPrimary(side));
    }

    @Override
    public boolean canNavigateVia(TrackNode side) {
        return !((Map)this.blockEntities.get(this.isPrimary(side))).isEmpty();
    }

    public SignalBlockEntity.OverlayState getOverlayFor(BlockPos blockEntity) {
        for (boolean first : Iterate.trueAndFalse) {
            Map set = (Map)this.blockEntities.get(first);
            Iterator iterator = set.keySet().iterator();
            if (!iterator.hasNext()) continue;
            BlockPos blockPos = (BlockPos)iterator.next();
            if (blockPos.equals((Object)blockEntity)) {
                return ((Map)this.blockEntities.get(!first)).isEmpty() ? SignalBlockEntity.OverlayState.RENDER : SignalBlockEntity.OverlayState.DUAL;
            }
            return SignalBlockEntity.OverlayState.SKIP;
        }
        return SignalBlockEntity.OverlayState.SKIP;
    }

    public SignalBlock.SignalType getTypeFor(BlockPos blockEntity) {
        return (SignalBlock.SignalType)((Object)this.types.get(((Map)this.blockEntities.getFirst()).containsKey(blockEntity)));
    }

    public SignalBlockEntity.SignalState getStateFor(BlockPos blockEntity) {
        for (boolean first : Iterate.trueAndFalse) {
            Map set = (Map)this.blockEntities.get(first);
            if (!set.containsKey(blockEntity)) continue;
            return (SignalBlockEntity.SignalState)((Object)this.cachedStates.get(first));
        }
        return SignalBlockEntity.SignalState.INVALID;
    }

    @Override
    public void tick(TrackGraph graph, boolean preTrains) {
        super.tick(graph, preTrains);
        if (!preTrains) {
            this.tickState(graph);
            return;
        }
        for (boolean front : Iterate.trueAndFalse) {
            if (!((Boolean)this.sidesToUpdate.get(front)).booleanValue()) continue;
            this.sidesToUpdate.set(front, (Object)false);
            SignalPropagator.propagateSignalGroup(graph, this, front);
            this.chainedSignals.set(front, null);
        }
    }

    private void tickState(TrackGraph graph) {
        for (boolean current : Iterate.trueAndFalse) {
            Map set = (Map)this.blockEntities.get(current);
            if (set.isEmpty()) continue;
            boolean forcedRed = this.isForcedRed(current);
            UUID group = (UUID)this.groups.get(current);
            if (Objects.equal((Object)group, (Object)this.groups.get(!current))) {
                this.cachedStates.set(current, (Object)SignalBlockEntity.SignalState.INVALID);
                continue;
            }
            Map<UUID, SignalEdgeGroup> signalEdgeGroups = Create.RAILWAYS.signalEdgeGroups;
            SignalEdgeGroup signalEdgeGroup = signalEdgeGroups.get(group);
            if (signalEdgeGroup == null) {
                this.cachedStates.set(current, (Object)SignalBlockEntity.SignalState.INVALID);
                continue;
            }
            boolean occupiedUnlessBySelf = forcedRed || signalEdgeGroup.isOccupiedUnless(this);
            this.cachedStates.set(current, (Object)(occupiedUnlessBySelf ? SignalBlockEntity.SignalState.RED : this.resolveSignalChain(graph, current)));
        }
    }

    public boolean isForcedRed(TrackNode side) {
        return this.isForcedRed(this.isPrimary(side));
    }

    public boolean isForcedRed(boolean primary) {
        Collection values = ((Map)this.blockEntities.get(primary)).values();
        for (Boolean b : values) {
            if (!b.booleanValue()) continue;
            return true;
        }
        return false;
    }

    private SignalBlockEntity.SignalState resolveSignalChain(TrackGraph graph, boolean side) {
        if (this.types.get(side) != SignalBlock.SignalType.CROSS_SIGNAL) {
            return SignalBlockEntity.SignalState.GREEN;
        }
        if (this.chainedSignals.get(side) == null) {
            this.chainedSignals.set(side, SignalPropagator.collectChainedSignals(graph, this, side));
        }
        boolean allPathsFree = true;
        boolean noPathsFree = true;
        boolean invalid = false;
        for (Map.Entry entry : ((Map)this.chainedSignals.get(side)).entrySet()) {
            UUID uuid = (UUID)entry.getKey();
            boolean sideOfOther = (Boolean)entry.getValue();
            SignalBoundary otherSignal = graph.getPoint(EdgePointType.SIGNAL, uuid);
            if (otherSignal == null) {
                invalid = true;
                break;
            }
            if (((Map)otherSignal.blockEntities.get(sideOfOther)).isEmpty()) continue;
            SignalBlockEntity.SignalState otherState = (SignalBlockEntity.SignalState)((Object)otherSignal.cachedStates.get(sideOfOther));
            allPathsFree &= otherState == SignalBlockEntity.SignalState.GREEN || otherState == SignalBlockEntity.SignalState.INVALID;
            noPathsFree &= otherState == SignalBlockEntity.SignalState.RED;
        }
        if (invalid) {
            this.chainedSignals.set(side, null);
            return SignalBlockEntity.SignalState.INVALID;
        }
        if (allPathsFree) {
            return SignalBlockEntity.SignalState.GREEN;
        }
        if (noPathsFree) {
            return SignalBlockEntity.SignalState.RED;
        }
        return SignalBlockEntity.SignalState.YELLOW;
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean migration, DimensionPalette dimensions) {
        int i;
        super.read(nbt, registries, migration, dimensions);
        if (migration) {
            return;
        }
        this.blockEntities = Couple.create(HashMap::new);
        this.groups = Couple.create(null, null);
        for (i = 1; i <= 2; ++i) {
            if (!nbt.contains("Tiles" + i)) continue;
            boolean first = i == 1;
            NBTHelper.iterateCompoundList((ListTag)nbt.getList("Tiles" + i, 10), c -> ((Map)this.blockEntities.get(first)).put(NBTHelper.readBlockPos((CompoundTag)c, (String)"Pos"), c.getBoolean("Power")));
        }
        for (i = 1; i <= 2; ++i) {
            if (!nbt.contains("Group" + i)) continue;
            this.groups.set(i == 1, (Object)nbt.getUUID("Group" + i));
        }
        for (i = 1; i <= 2; ++i) {
            this.sidesToUpdate.set(i == 1, (Object)nbt.contains("Update" + i));
        }
        for (i = 1; i <= 2; ++i) {
            this.types.set(i == 1, (Object)((SignalBlock.SignalType)NBTHelper.readEnum((CompoundTag)nbt, (String)("Type" + i), SignalBlock.SignalType.class)));
        }
        for (i = 1; i <= 2; ++i) {
            this.cachedStates.set(i == 1, (Object)((SignalBlockEntity.SignalState)NBTHelper.readEnum((CompoundTag)nbt, (String)("State" + i), SignalBlockEntity.SignalState.class)));
        }
    }

    @Override
    public void read(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.read(buffer, dimensions);
        for (int i = 1; i <= 2; ++i) {
            if (!buffer.readBoolean()) continue;
            this.groups.set(i == 1, (Object)buffer.readUUID());
        }
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, DimensionPalette dimensions) {
        int i;
        super.write(nbt, registries, dimensions);
        for (i = 1; i <= 2; ++i) {
            if (((Map)this.blockEntities.get(i == 1)).isEmpty()) continue;
            nbt.put("Tiles" + i, (Tag)NBTHelper.writeCompoundList(((Map)this.blockEntities.get(i == 1)).entrySet(), e -> {
                CompoundTag c = new CompoundTag();
                c.put("Pos", NbtUtils.writeBlockPos((BlockPos)((BlockPos)e.getKey())));
                c.putBoolean("Power", ((Boolean)e.getValue()).booleanValue());
                return c;
            }));
        }
        for (i = 1; i <= 2; ++i) {
            if (this.groups.get(i == 1) == null) continue;
            nbt.putUUID("Group" + i, (UUID)this.groups.get(i == 1));
        }
        for (i = 1; i <= 2; ++i) {
            if (!((Boolean)this.sidesToUpdate.get(i == 1)).booleanValue()) continue;
            nbt.putBoolean("Update" + i, true);
        }
        for (i = 1; i <= 2; ++i) {
            NBTHelper.writeEnum((CompoundTag)nbt, (String)("Type" + i), (Enum)((SignalBlock.SignalType)((Object)this.types.get(i == 1))));
        }
        for (i = 1; i <= 2; ++i) {
            NBTHelper.writeEnum((CompoundTag)nbt, (String)("State" + i), (Enum)((SignalBlockEntity.SignalState)((Object)this.cachedStates.get(i == 1))));
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.write(buffer, dimensions);
        for (int i = 1; i <= 2; ++i) {
            boolean hasGroup = this.groups.get(i == 1) != null;
            buffer.writeBoolean(hasGroup);
            if (!hasGroup) continue;
            buffer.writeUUID((UUID)this.groups.get(i == 1));
        }
    }

    public void cycleSignalType(BlockPos pos) {
        this.types.set(((Map)this.blockEntities.getFirst()).containsKey(pos), (Object)SignalBlock.SignalType.values()[(this.getTypeFor(pos).ordinal() + 1) % SignalBlock.SignalType.values().length]);
    }
}
