/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageEntityHandler;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class Carriage.DimensionalCarriageEntity {
    public Vec3 positionAnchor;
    public Couple<Vec3> rotationAnchors;
    public WeakReference<CarriageContraptionEntity> entity = new WeakReference<Object>(null);
    public TrackNodeLocation pivot;
    int discardTicks;
    public float cutoff;
    public boolean pointsInitialised = false;

    public Carriage.DimensionalCarriageEntity() {
        this.rotationAnchors = Couple.create(null, null);
    }

    public void discardPivot() {
        int prevmin = this.minAllowedLocalCoord();
        int prevmax = this.maxAllowedLocalCoord();
        this.cutoff = 0.0f;
        this.pivot = null;
        if (!Carriage.this.serialisedPassengers.isEmpty() && this.entity.get() != null || prevmin != this.minAllowedLocalCoord() || prevmax != this.maxAllowedLocalCoord()) {
            this.updatePassengerLoadout();
            this.updateRenderedCutoff();
        }
    }

    public void updateCutoff(boolean leadingIsCurrent) {
        Vec3 leadingAnchor = (Vec3)this.rotationAnchors.getFirst();
        Vec3 trailingAnchor = (Vec3)this.rotationAnchors.getSecond();
        if (leadingAnchor == null || trailingAnchor == null) {
            return;
        }
        if (this.pivot == null) {
            this.cutoff = 0.0f;
            return;
        }
        Vec3 pivotLoc = this.pivot.getLocation().add(0.0, 1.0, 0.0);
        double leadingSpacing = Carriage.this.leadingBogey().type.getWheelPointSpacing() / 2.0;
        double trailingSpacing = Carriage.this.trailingBogey().type.getWheelPointSpacing() / 2.0;
        double anchorSpacing = leadingSpacing + (double)Carriage.this.bogeySpacing + trailingSpacing;
        if (Carriage.this.isOnTwoBogeys()) {
            Vec3 diff = trailingAnchor.subtract(leadingAnchor).normalize();
            trailingAnchor = trailingAnchor.add(diff.scale(trailingSpacing));
            leadingAnchor = leadingAnchor.add(diff.scale(-leadingSpacing));
        }
        double leadingDiff = leadingAnchor.distanceTo(pivotLoc);
        double trailingDiff = trailingAnchor.distanceTo(pivotLoc);
        this.cutoff = leadingIsCurrent && leadingDiff > trailingDiff && leadingDiff > 1.0 ? 0.0f : (leadingIsCurrent && leadingDiff < trailingDiff && trailingDiff > 1.0 ? 1.0f : (!leadingIsCurrent && leadingDiff > trailingDiff && leadingDiff > 1.0 ? -1.0f : (!leadingIsCurrent && leadingDiff < trailingDiff && trailingDiff > 1.0 ? 0.0f : (float)Mth.clamp((double)(1.0 - (leadingIsCurrent ? (leadingDiff /= anchorSpacing) : (trailingDiff /= anchorSpacing))), (double)0.0, (double)1.0) * (float)(leadingIsCurrent ? 1 : -1))));
    }

    public TrackNodeLocation findPivot(ResourceKey<Level> dimension, boolean leading) {
        if (this.pivot != null) {
            return this.pivot;
        }
        TravellingPoint start = leading ? Carriage.this.getLeadingPoint() : Carriage.this.getTrailingPoint();
        TravellingPoint end = !leading ? Carriage.this.getLeadingPoint() : Carriage.this.getTrailingPoint();
        Carriage.this.portalScout.node1 = start.node1;
        Carriage.this.portalScout.node2 = start.node2;
        Carriage.this.portalScout.edge = start.edge;
        Carriage.this.portalScout.position = start.position;
        TravellingPoint.ITrackSelector trackSelector = Carriage.this.portalScout.follow(end);
        int distance = Carriage.this.bogeySpacing + 10;
        int direction = leading ? -1 : 1;
        Carriage.this.portalScout.travel(Carriage.this.train.graph, direction * distance, trackSelector, Carriage.this.portalScout.ignoreEdgePoints(), Carriage.this.portalScout.ignoreTurns(), nodes -> {
            for (boolean b : Iterate.trueAndFalse) {
                if (!((TrackNodeLocation)((Object)((Object)nodes.get((boolean)b)))).dimension.equals((Object)dimension)) continue;
                this.pivot = (TrackNodeLocation)((Object)((Object)nodes.get(b)));
            }
            return true;
        });
        return this.pivot;
    }

    public CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Cutoff", this.cutoff);
        tag.putInt("DiscardTicks", this.discardTicks);
        Carriage.this.storage.write(tag, registries, false);
        if (this.pivot != null) {
            tag.put("Pivot", (Tag)this.pivot.write(null));
        }
        if (this.positionAnchor != null) {
            tag.put("PositionAnchor", (Tag)VecHelper.writeNBT((Vec3)this.positionAnchor));
        }
        if (this.rotationAnchors.both(Objects::nonNull)) {
            tag.put("RotationAnchors", (Tag)this.rotationAnchors.serializeEach(VecHelper::writeNBTCompound));
        }
        return tag;
    }

    public void read(CompoundTag tag, HolderLookup.Provider registries) {
        this.cutoff = tag.getFloat("Cutoff");
        this.discardTicks = tag.getInt("DiscardTicks");
        Carriage.this.storage.read(tag, registries, false, null);
        if (tag.contains("Pivot")) {
            this.pivot = TrackNodeLocation.read(tag.getCompound("Pivot"), null);
        }
        if (this.positionAnchor != null) {
            return;
        }
        if (tag.contains("PositionAnchor")) {
            this.positionAnchor = VecHelper.readNBT((ListTag)tag.getList("PositionAnchor", 6));
        }
        if (tag.contains("RotationAnchors")) {
            this.rotationAnchors = Couple.deserializeEach((ListTag)tag.getList("RotationAnchors", 10), VecHelper::readNBTCompound);
        }
    }

    public Vec3 leadingAnchor() {
        return Carriage.this.isOnTwoBogeys() ? (Vec3)this.rotationAnchors.getFirst() : this.positionAnchor;
    }

    public Vec3 trailingAnchor() {
        return Carriage.this.isOnTwoBogeys() ? (Vec3)this.rotationAnchors.getSecond() : this.positionAnchor;
    }

    public int minAllowedLocalCoord() {
        if (this.cutoff <= 0.0f) {
            return Integer.MIN_VALUE;
        }
        if (this.cutoff >= 1.0f) {
            return Integer.MAX_VALUE;
        }
        return Mth.floor((float)((float)(-Carriage.this.bogeySpacing + -1) + (float)(2 + Carriage.this.bogeySpacing) * this.cutoff));
    }

    public int maxAllowedLocalCoord() {
        if (this.cutoff >= 0.0f) {
            return Integer.MAX_VALUE;
        }
        if (this.cutoff <= -1.0f) {
            return Integer.MIN_VALUE;
        }
        return Mth.ceil((float)((float)(-Carriage.this.bogeySpacing + -1) + (float)(2 + Carriage.this.bogeySpacing) * (this.cutoff + 1.0f)));
    }

    public void updatePassengerLoadout() {
        Entity entity = (Entity)this.entity.get();
        if (!(entity instanceof CarriageContraptionEntity)) {
            return;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)entity;
        Level level = entity.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel sLevel = (ServerLevel)level;
        HashSet<Integer> loadedPassengers = new HashSet<Integer>();
        int min = this.minAllowedLocalCoord();
        int max = this.maxAllowedLocalCoord();
        for (Map.Entry<Integer, CompoundTag> entry : Carriage.this.serialisedPassengers.entrySet()) {
            BlockPos localPos;
            Integer seatId = entry.getKey();
            List<BlockPos> seats = cce.getContraption().getSeats();
            if (seatId >= seats.size() || !cce.isLocalCoordWithin(localPos = seats.get(seatId), min, max)) continue;
            CompoundTag tag = entry.getValue();
            Entity passenger = null;
            if (tag.contains("PlayerPassenger")) {
                passenger = sLevel.getServer().getPlayerList().getPlayer(tag.getUUID("PlayerPassenger"));
            } else {
                passenger = EntityType.loadEntityRecursive((CompoundTag)tag, (Level)entity.level(), e -> {
                    e.moveTo(this.positionAnchor);
                    return e;
                });
                if (passenger != null) {
                    sLevel.tryAddFreshEntityWithPassengers(passenger);
                }
            }
            if (passenger != null) {
                ResourceKey passengerDimension = passenger.level().dimension();
                if (!passengerDimension.equals(sLevel.dimension()) && passenger instanceof ServerPlayer) {
                    ServerPlayer sp = (ServerPlayer)passenger;
                    continue;
                }
                cce.addSittingPassenger(passenger, seatId);
            }
            loadedPassengers.add(seatId);
        }
        loadedPassengers.forEach(Carriage.this.serialisedPassengers::remove);
        Map<UUID, Integer> mapping = cce.getContraption().getSeatMapping();
        for (Entity passenger : entity.getPassengers()) {
            BlockPos localPos = cce.getContraption().getSeatOf(passenger.getUUID());
            if (cce.isLocalCoordWithin(localPos, min, max) || !mapping.containsKey(passenger.getUUID())) continue;
            Integer seat = mapping.get(passenger.getUUID());
            if (passenger instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer)passenger;
                this.dismountPlayer(sLevel, sp, seat, true);
                continue;
            }
            CompoundTag passengerData = new CompoundTag();
            passenger.saveAsPassenger(passengerData);
            Carriage.this.serialisedPassengers.put(seat, passengerData);
            passenger.discard();
        }
    }

    private void dismountPlayer(ServerLevel sLevel, ServerPlayer sp, Integer seat, boolean capture) {
        if (!capture) {
            sp.stopRiding();
            return;
        }
        CompoundTag tag = new CompoundTag();
        tag.putUUID("PlayerPassenger", sp.getUUID());
        Carriage.this.serialisedPassengers.put(seat, tag);
        sp.stopRiding();
        sp.getPersistentData().remove("ContraptionDismountLocation");
        for (Map.Entry<ResourceKey<Level>, Carriage.DimensionalCarriageEntity> other : Carriage.this.entities.entrySet()) {
            Vec3 loc;
            Carriage.DimensionalCarriageEntity otherDce = other.getValue();
            if (otherDce == this || sp.level().dimension().equals(other.getKey())) continue;
            Vec3 vec3 = loc = otherDce.pivot == null ? otherDce.positionAnchor : otherDce.pivot.getLocation();
            if (loc == null) continue;
            ServerLevel level = sLevel.getServer().getLevel(other.getKey());
            sp.teleportTo(level, loc.x, loc.y, loc.z, sp.getYRot(), sp.getXRot());
            sp.setPortalCooldown();
            AllAdvancements.TRAIN_PORTAL.awardTo((Player)sp);
        }
    }

    public void updateRenderedCutoff() {
        Entity entity = (Entity)this.entity.get();
        if (!(entity instanceof CarriageContraptionEntity)) {
            return;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)entity;
        Contraption contraption = cce.getContraption();
        if (!(contraption instanceof CarriageContraption)) {
            return;
        }
        CarriageContraption cc = (CarriageContraption)contraption;
        cc.portalCutoffMin = this.minAllowedLocalCoord();
        cc.portalCutoffMax = this.maxAllowedLocalCoord();
        if (!entity.level().isClientSide()) {
            return;
        }
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.invalidate(cce));
    }

    @OnlyIn(value=Dist.CLIENT)
    private void invalidate(CarriageContraptionEntity entity) {
        entity.updateRenderedPortalCutoff();
        entity.getContraption().invalidateClientContraptionStructure();
        entity.getContraption().invalidateClientContraptionChildren();
    }

    private void createEntity(Level level, boolean loadPassengers) {
        Entity entity;
        if (this.positionAnchor != null) {
            Carriage.this.serialisedEntity.put("Pos", (Tag)VecHelper.writeNBT((Vec3)this.positionAnchor));
        }
        if (!((entity = (Entity)EntityType.create((CompoundTag)Carriage.this.serialisedEntity, (Level)level).orElse(null)) instanceof CarriageContraptionEntity)) {
            Carriage.this.train.invalid = true;
            return;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)entity;
        entity.moveTo(this.positionAnchor);
        this.entity = new WeakReference<CarriageContraptionEntity>(cce);
        cce.setCarriage(Carriage.this);
        cce.syncCarriage();
        if (level instanceof ServerLevel) {
            ServerLevel sl = (ServerLevel)level;
            sl.addFreshEntity(entity);
        }
        this.updatePassengerLoadout();
    }

    private void removeAndSaveEntity(CarriageContraptionEntity entity, boolean portal) {
        Contraption contraption = entity.getContraption();
        if (contraption != null) {
            Map<UUID, Integer> mapping = contraption.getSeatMapping();
            for (Entity passenger : entity.getPassengers()) {
                if (!mapping.containsKey(passenger.getUUID())) continue;
                Integer seat = mapping.get(passenger.getUUID());
                if (passenger instanceof ServerPlayer) {
                    ServerPlayer sp = (ServerPlayer)passenger;
                    this.dismountPlayer(sp.serverLevel(), sp, seat, portal);
                    continue;
                }
                CompoundTag passengerData = new CompoundTag();
                passenger.saveAsPassenger(passengerData);
                Carriage.this.serialisedPassengers.put(seat, passengerData);
            }
        }
        for (Entity passenger : entity.getPassengers()) {
            if (passenger instanceof Player) continue;
            passenger.discard();
        }
        Carriage.this.serialize(entity);
        entity.discard();
        this.entity.clear();
    }

    public void alignEntity(CarriageContraptionEntity entity) {
        if (this.rotationAnchors.either(Objects::isNull)) {
            return;
        }
        Vec3 positionVec = (Vec3)this.rotationAnchors.getFirst();
        Vec3 coupledVec = (Vec3)this.rotationAnchors.getSecond();
        double diffX = positionVec.x - coupledVec.x;
        double diffY = positionVec.y - coupledVec.y;
        double diffZ = positionVec.z - coupledVec.z;
        entity.prevYaw = entity.yaw;
        entity.prevPitch = entity.pitch;
        if (!entity.level().isClientSide()) {
            Vec3 lookahead = this.positionAnchor.add(this.positionAnchor.subtract(entity.position()).normalize().scale(16.0));
            for (Entity e : entity.getPassengers()) {
                if (!(e instanceof Player) || e.distanceToSqr((Entity)entity) > 1024.0) continue;
                if (CarriageEntityHandler.isActiveChunk(entity.level(), BlockPos.containing((Position)lookahead))) break;
                Carriage.this.train.carriageWaitingForChunks = Carriage.this.id;
                return;
            }
            if (entity.getPassengers().stream().anyMatch(p -> p instanceof Player)) {
                // empty if block
            }
            if (Carriage.this.train.carriageWaitingForChunks == Carriage.this.id) {
                Carriage.this.train.carriageWaitingForChunks = -1;
            }
            entity.setServerSidePrevPosition();
        }
        entity.setPos(this.positionAnchor);
        entity.yaw = (float)(Mth.atan2((double)diffZ, (double)diffX) * 180.0 / Math.PI) + 180.0f;
        entity.pitch = (float)(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)) * 180.0 / Math.PI) * -1.0f;
        if (!entity.firstPositionUpdate) {
            return;
        }
        entity.xo = entity.getX();
        entity.yo = entity.getY();
        entity.zo = entity.getZ();
        entity.prevYaw = entity.yaw;
        entity.prevPitch = entity.pitch;
    }
}
