/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.common.util.FakePlayer
 */
package com.simibubi.create.foundation.advancement;

import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;

public class AdvancementBehaviour
extends BlockEntityBehaviour {
    public static final BehaviourType<AdvancementBehaviour> TYPE = new BehaviourType();
    private UUID playerId;
    private final Set<CreateAdvancement> advancements = new HashSet<CreateAdvancement>();

    public AdvancementBehaviour(SmartBlockEntity be, CreateAdvancement ... advancements) {
        super(be);
        this.add(advancements);
    }

    public void add(CreateAdvancement ... advancements) {
        Collections.addAll(this.advancements, advancements);
    }

    public boolean isOwnerPresent() {
        return this.playerId != null;
    }

    public void setPlayer(UUID id) {
        Player player = this.getWorld().getPlayerByUUID(id);
        if (player == null) {
            return;
        }
        this.playerId = id;
        this.removeAwarded();
        this.blockEntity.setChanged();
    }

    @Override
    public void initialize() {
        super.initialize();
        this.removeAwarded();
    }

    private void removeAwarded() {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        this.advancements.removeIf(c -> c.isAlreadyAwardedTo(player));
        if (this.advancements.isEmpty()) {
            this.playerId = null;
            this.blockEntity.setChanged();
        }
    }

    public void awardPlayerIfNear(CreateAdvancement advancement, int maxDistance) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        if (player.distanceToSqr(Vec3.atCenterOf((Vec3i)this.getPos())) > (double)(maxDistance * maxDistance)) {
            return;
        }
        this.award(advancement, player);
    }

    public void awardPlayer(CreateAdvancement advancement) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        this.award(advancement, player);
    }

    private void award(CreateAdvancement advancement, Player player) {
        if (this.advancements.contains(advancement)) {
            advancement.awardTo(player);
        }
        this.removeAwarded();
    }

    private Player getPlayer() {
        if (this.playerId == null) {
            return null;
        }
        return this.getWorld().getPlayerByUUID(this.playerId);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        if (this.playerId != null) {
            nbt.putUUID("Owner", this.playerId);
        }
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        if (nbt.contains("Owner")) {
            this.playerId = nbt.getUUID("Owner");
        }
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public static void tryAward(BlockGetter reader, BlockPos pos, CreateAdvancement advancement) {
        AdvancementBehaviour behaviour = BlockEntityBehaviour.get(reader, pos, TYPE);
        if (behaviour != null) {
            behaviour.awardPlayer(advancement);
        }
    }

    public static void setPlacedBy(Level worldIn, BlockPos pos, LivingEntity placer) {
        AdvancementBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)worldIn, pos, TYPE);
        if (behaviour == null) {
            return;
        }
        if (placer instanceof FakePlayer) {
            return;
        }
        if (placer instanceof ServerPlayer) {
            behaviour.setPlayer(placer.getUUID());
        }
    }
}
