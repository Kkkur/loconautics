package com.lycoris.loconautics.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.UUIDUtil;

/**
 * Marks a Create Train as assembled in Sable physics mode.
 *
 * Each carriage entry holds its sub-level UUID and the world-space anchor BlockPos
 * that was used during assembly (= contraption.anchor). The anchor is needed at
 * disassembly time to map plot-local block positions back to world positions for
 * NBT restoration.
 */
public final class PhysicsTrainTag {

    public record CarriageEntry(UUID subLevelId, BlockPos anchor) {}

    private final UUID trainId;
    private final List<CarriageEntry> carriages;

    public PhysicsTrainTag(UUID trainId, List<CarriageEntry> carriages) {
        this.trainId = trainId;
        this.carriages = new ArrayList<>(carriages);
    }

    public UUID trainId() { return trainId; }
    public List<CarriageEntry> carriages() { return carriages; }
    public int carriageCount() { return carriages.size(); }

    /** Back-compat helper used by tick driver. */
    public List<UUID> subLevelIds() {
        List<UUID> ids = new ArrayList<>(carriages.size());
        for (CarriageEntry e : carriages) ids.add(e.subLevelId());
        return ids;
    }

    // ----- NBT -----

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("TrainId", trainId);
        ListTag list = new ListTag();
        for (CarriageEntry e : carriages) {
            CompoundTag entry = new CompoundTag();
            if (e.subLevelId() != null) entry.putUUID("SubLevel", e.subLevelId());
            if (e.anchor() != null) entry.put("Anchor", NbtUtils.writeBlockPos(e.anchor()));
            list.add(entry);
        }
        tag.put("SubLevels", list);
        return tag;
    }

    public static PhysicsTrainTag fromNbt(CompoundTag tag) {
        UUID trainId = tag.getUUID("TrainId");
        List<CarriageEntry> entries = new ArrayList<>();
        ListTag list = tag.getList("SubLevels", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag e = list.getCompound(i);
            UUID subId = e.contains("SubLevel") ? e.getUUID("SubLevel") : null;
            BlockPos anchor = e.contains("Anchor") ? NbtUtils.readBlockPos(e, "Anchor").orElse(null) : null;
            entries.add(new CarriageEntry(subId, anchor));
        }
        return new PhysicsTrainTag(trainId, entries);
    }

    // ----- Network codec (clients only need subLevelIds for render suppression) -----

    public static final StreamCodec<RegistryFriendlyByteBuf, PhysicsTrainTag> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, PhysicsTrainTag::trainId,
                    ByteBufCodecs.collection(ArrayList::new, UUIDUtil.STREAM_CODEC), PhysicsTrainTag::subLevelIds,
                    (id, ids) -> {
                        List<CarriageEntry> entries = new ArrayList<>();
                        for (UUID u : ids) entries.add(new CarriageEntry(u, BlockPos.ZERO));
                        return new PhysicsTrainTag(id, entries);
                    }
            );
}