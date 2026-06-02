/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package com.simibubi.create.content.trains.signal;

import com.google.common.base.Predicates;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.signal.EdgeGroupColor;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.apache.commons.lang3.mutable.MutableInt;

public class SignalEdgeGroup {
    public UUID id;
    public EdgeGroupColor color;
    public Set<Train> trains;
    public SignalBoundary reserved;
    public Map<UUID, UUID> intersecting;
    public Set<SignalEdgeGroup> intersectingResolved;
    public Set<UUID> adjacent;
    public boolean fallbackGroup;

    public SignalEdgeGroup(UUID id) {
        this.id = id;
        this.trains = new HashSet<Train>();
        this.adjacent = new HashSet<UUID>();
        this.intersecting = new HashMap<UUID, UUID>();
        this.intersectingResolved = new HashSet<SignalEdgeGroup>();
        this.color = EdgeGroupColor.getDefault();
    }

    public SignalEdgeGroup asFallback() {
        this.fallbackGroup = true;
        return this;
    }

    public boolean isOccupiedUnless(Train train) {
        if (this.intersectingResolved.isEmpty()) {
            this.walkIntersecting(this.intersectingResolved::add);
        }
        for (SignalEdgeGroup group : this.intersectingResolved) {
            if (!group.isThisOccupiedUnless(train)) continue;
            return true;
        }
        return false;
    }

    private boolean isThisOccupiedUnless(Train train) {
        return this.reserved != null || this.trains.size() > 1 || !this.trains.contains(train) && !this.trains.isEmpty();
    }

    public boolean isOccupiedUnless(SignalBoundary boundary) {
        if (this.intersectingResolved.isEmpty()) {
            this.walkIntersecting(this.intersectingResolved::add);
        }
        for (SignalEdgeGroup group : this.intersectingResolved) {
            if (!group.isThisOccupiedUnless(boundary)) continue;
            return true;
        }
        return false;
    }

    private boolean isThisOccupiedUnless(SignalBoundary boundary) {
        return !this.trains.isEmpty() || this.reserved != null && this.reserved != boundary;
    }

    public void putIntersection(UUID intersectionId, UUID targetGroup) {
        this.intersecting.put(intersectionId, targetGroup);
        this.walkIntersecting(g -> g.intersectingResolved.clear());
        this.resolveColor();
    }

    public void removeIntersection(UUID intersectionId) {
        this.walkIntersecting(g -> g.intersectingResolved.clear());
        UUID removed = this.intersecting.remove(intersectionId);
        SignalEdgeGroup other = Create.RAILWAYS.signalEdgeGroups.get(removed);
        if (other != null) {
            other.intersecting.remove(intersectionId);
        }
        this.resolveColor();
    }

    public void putAdjacent(UUID adjacent) {
        this.adjacent.add(adjacent);
    }

    public void removeAdjacent(UUID adjacent) {
        this.adjacent.remove(adjacent);
    }

    public void resolveColor() {
        if (this.intersectingResolved.isEmpty()) {
            this.walkIntersecting(this.intersectingResolved::add);
        }
        MutableInt mask = new MutableInt(0);
        this.intersectingResolved.forEach(group -> group.adjacent.stream().map(Create.RAILWAYS.signalEdgeGroups::get).filter(Objects::nonNull).filter((Predicate<SignalEdgeGroup>)Predicates.not(this.intersectingResolved::contains)).forEach(adjacent -> mask.setValue(adjacent.color.strikeFrom(mask.getValue()))));
        EdgeGroupColor newColour = EdgeGroupColor.findNextAvailable(mask.getValue());
        if (newColour == this.color) {
            return;
        }
        this.walkIntersecting(group -> {
            group.color = newColour;
            Create.RAILWAYS.sync.edgeGroupCreated(group.id, group.color);
        });
        Create.RAILWAYS.markTracksDirty();
    }

    private void walkIntersecting(Consumer<SignalEdgeGroup> callback) {
        this.walkIntersectingRec(new HashSet<SignalEdgeGroup>(), callback);
    }

    private void walkIntersectingRec(Set<SignalEdgeGroup> visited, Consumer<SignalEdgeGroup> callback) {
        if (!visited.add(this)) {
            return;
        }
        callback.accept(this);
        for (UUID uuid : this.intersecting.values()) {
            SignalEdgeGroup group = Create.RAILWAYS.signalEdgeGroups.get(uuid);
            if (group == null) continue;
            group.walkIntersectingRec(visited, callback);
        }
    }

    public static SignalEdgeGroup read(CompoundTag tag) {
        SignalEdgeGroup group = new SignalEdgeGroup(tag.getUUID("Id"));
        group.color = (EdgeGroupColor)NBTHelper.readEnum((CompoundTag)tag, (String)"Color", EdgeGroupColor.class);
        NBTHelper.iterateCompoundList((ListTag)tag.getList("Connected", 10), nbt -> group.intersecting.put(nbt.getUUID("Key"), nbt.getUUID("Value")));
        group.fallbackGroup = tag.getBoolean("Fallback");
        return group;
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", this.id);
        NBTHelper.writeEnum((CompoundTag)tag, (String)"Color", (Enum)this.color);
        tag.put("Connected", (Tag)NBTHelper.writeCompoundList(this.intersecting.entrySet(), e -> {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID("Key", (UUID)e.getKey());
            nbt.putUUID("Value", (UUID)e.getValue());
            return nbt;
        }));
        tag.putBoolean("Fallback", this.fallbackGroup);
        return tag;
    }
}
