package com.lycoris.loconautics.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.UUIDUtil;

/**
 * Marks a Create {@link com.simibubi.create.content.trains.entity.Train} as assembled in Sable
 * physics mode, and records which Sable sub-level backs each carriage.
 *
 * <p>The list index lines up with the train's carriage index: {@code subLevelIds.get(i)} is the
 * sub-level UUID for {@code train.carriages.get(i)}. A null entry means that carriage has no
 * sub-level yet (should not normally happen once assembly succeeds).
 *
 * <p>This is a plain data holder. It never keeps a hard reference to the {@code Train} (which can
 * be garbage-collected / managed by Create); the train is always resolved by UUID through
 * {@code Create.RAILWAYS}.
 */
public final class PhysicsTrainTag {

    private final UUID trainId;
    private final List<UUID> subLevelIds;

    public PhysicsTrainTag(UUID trainId, List<UUID> subLevelIds) {
        this.trainId = trainId;
        this.subLevelIds = new ArrayList<>(subLevelIds);
    }

    public PhysicsTrainTag(UUID trainId) {
        this(trainId, new ArrayList<>());
    }

    public UUID trainId() {
        return trainId;
    }

    public List<UUID> subLevelIds() {
        return subLevelIds;
    }

    public int carriageCount() {
        return subLevelIds.size();
    }

    // ----- NBT (for PhysicsTrainRegistry SavedData persistence) -----

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("TrainId", trainId);
        ListTag list = new ListTag();
        for (UUID id : subLevelIds) {
            CompoundTag entry = new CompoundTag();
            if (id != null) {
                entry.putUUID("SubLevel", id);
            }
            list.add(entry);
        }
        tag.put("SubLevels", list);
        return tag;
    }

    public static PhysicsTrainTag fromNbt(CompoundTag tag) {
        UUID trainId = tag.getUUID("TrainId");
        List<UUID> ids = new ArrayList<>();
        ListTag list = tag.getList("SubLevels", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ids.add(entry.contains("SubLevel") ? entry.getUUID("SubLevel") : null);
        }
        return new PhysicsTrainTag(trainId, ids);
    }

    // ----- Network codec (for sync packets) -----

    public static final StreamCodec<RegistryFriendlyByteBuf, PhysicsTrainTag> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, PhysicsTrainTag::trainId,
                    ByteBufCodecs.collection(ArrayList::new, UUIDUtil.STREAM_CODEC), PhysicsTrainTag::subLevelIds,
                    PhysicsTrainTag::new
            );
}
