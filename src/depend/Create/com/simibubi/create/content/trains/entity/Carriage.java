/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.MinecraftServer
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
 *  org.apache.commons.lang3.mutable.MutableDouble
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.minecart.TrainCargoManager;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageEntityHandler;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
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
import org.apache.commons.lang3.mutable.MutableDouble;
import org.jetbrains.annotations.Nullable;

public class Carriage {
    public static final StreamCodec<RegistryFriendlyByteBuf, Carriage> STREAM_CODEC = StreamCodec.composite(CarriageBogey.STREAM_CODEC, carriage -> (CarriageBogey)carriage.bogeys.getFirst(), (StreamCodec)CatnipStreamCodecBuilders.nullable(CarriageBogey.STREAM_CODEC), carriage -> (CarriageBogey)carriage.bogeys.getSecond(), (StreamCodec)ByteBufCodecs.VAR_INT, carriage -> carriage.bogeySpacing, Carriage::new);
    public static final AtomicInteger netIdGenerator = new AtomicInteger();
    public Train train;
    public int id;
    public boolean blocked;
    public boolean stalled;
    public Couple<Boolean> presentConductors;
    public int bogeySpacing;
    public Couple<CarriageBogey> bogeys;
    public TrainCargoManager storage;
    CompoundTag serialisedEntity;
    Map<Integer, CompoundTag> serialisedPassengers;
    private Map<ResourceKey<Level>, DimensionalCarriageEntity> entities;
    static final int FIRST = 0;
    static final int MIDDLE = 1;
    static final int LAST = 2;
    static final int BOTH = 3;
    private Set<ResourceKey<Level>> currentlyTraversedDimensions = new HashSet<ResourceKey<Level>>();
    private TravellingPoint portalScout = new TravellingPoint();

    public Carriage(CarriageBogey bogey1, @Nullable CarriageBogey bogey2, int bogeySpacing) {
        this.bogeySpacing = bogeySpacing;
        this.bogeys = Couple.create((Object)bogey1, (Object)bogey2);
        this.id = netIdGenerator.incrementAndGet();
        this.serialisedEntity = new CompoundTag();
        this.presentConductors = Couple.create((Object)false, (Object)false);
        this.serialisedPassengers = new HashMap<Integer, CompoundTag>();
        this.entities = new HashMap<ResourceKey<Level>, DimensionalCarriageEntity>();
        this.storage = new TrainCargoManager();
        bogey1.setLeading();
        bogey1.carriage = this;
        if (bogey2 != null) {
            bogey2.carriage = this;
        }
    }

    public boolean isOnIncompatibleTrack() {
        return this.leadingBogey().type.isOnIncompatibleTrack(this, true) || this.trailingBogey().type.isOnIncompatibleTrack(this, false);
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public boolean presentInMultipleDimensions() {
        return this.entities.size() > 1;
    }

    public List<ResourceKey<Level>> getPresentDimensions() {
        return this.entities.keySet().stream().distinct().toList();
    }

    public Optional<BlockPos> getPositionInDimension(ResourceKey<Level> dimension) {
        return Optional.ofNullable(this.entities.get(dimension)).map(carriage -> BlockPos.containing((Position)carriage.positionAnchor));
    }

    public void setContraption(Level level, CarriageContraption contraption) {
        this.storage = null;
        CarriageContraptionEntity entity = CarriageContraptionEntity.create(level, contraption);
        entity.setCarriage(this);
        contraption.startMoving(level);
        contraption.onEntityInitialize(level, entity);
        this.updateContraptionAnchors();
        DimensionalCarriageEntity dimensional = this.getDimensional(level);
        dimensional.alignEntity(entity);
        dimensional.removeAndSaveEntity(entity, true);
    }

    public DimensionalCarriageEntity getDimensional(Level level) {
        return this.getDimensional((ResourceKey<Level>)level.dimension());
    }

    public DimensionalCarriageEntity getDimensional(ResourceKey<Level> dimension) {
        return this.entities.computeIfAbsent(dimension, $ -> new DimensionalCarriageEntity());
    }

    @Nullable
    public DimensionalCarriageEntity getDimensionalIfPresent(ResourceKey<Level> dimension) {
        return this.entities.get(dimension);
    }

    public double travel(Level level, TrackGraph graph, double distance, TravellingPoint toFollowForward, TravellingPoint toFollowBackward, int type) {
        Function<TravellingPoint, TravellingPoint.ITrackSelector> forwardControl;
        Function<TravellingPoint, TravellingPoint.ITrackSelector> function = toFollowForward == null ? this.train.navigation::control : (forwardControl = mp -> mp.follow(toFollowForward));
        Function<TravellingPoint, TravellingPoint.ITrackSelector> backwardControl = toFollowBackward == null ? this.train.navigation::control : mp -> mp.follow(toFollowBackward);
        boolean onTwoBogeys = this.isOnTwoBogeys();
        double stress = this.train.derailed ? 0.0 : (onTwoBogeys ? (double)this.bogeySpacing - this.getAnchorDiff() : 0.0);
        this.blocked = false;
        MutableDouble distanceMoved = new MutableDouble(distance);
        boolean iterateFromBack = distance < 0.0;
        for (boolean firstBogey : Iterate.trueAndFalse) {
            if (!firstBogey && !onTwoBogeys) continue;
            boolean actuallyFirstBogey = !onTwoBogeys || firstBogey ^ iterateFromBack;
            CarriageBogey bogey = (CarriageBogey)this.bogeys.get(actuallyFirstBogey);
            double bogeyCorrection = stress * (actuallyFirstBogey ? 0.5 : -0.5);
            double bogeyStress = bogey.getStress();
            for (boolean firstWheel : Iterate.trueAndFalse) {
                TravellingPoint.ITrackSelector trackSelector;
                TravellingPoint prevPoint;
                boolean actuallyFirstWheel = firstWheel ^ iterateFromBack;
                TravellingPoint point = (TravellingPoint)bogey.points.get(actuallyFirstWheel);
                TravellingPoint travellingPoint = !actuallyFirstWheel ? (TravellingPoint)bogey.points.getFirst() : (prevPoint = !actuallyFirstBogey && onTwoBogeys ? (TravellingPoint)((CarriageBogey)this.bogeys.getFirst()).points.getSecond() : null);
                TravellingPoint nextPoint = actuallyFirstWheel ? (TravellingPoint)bogey.points.getSecond() : (actuallyFirstBogey && onTwoBogeys ? (TravellingPoint)((CarriageBogey)this.bogeys.getSecond()).points.getFirst() : null);
                double correction = bogeyStress * (actuallyFirstWheel ? 0.5 : -0.5);
                double toMove = distanceMoved.getValue();
                TravellingPoint.ITrackSelector frontTrackSelector = prevPoint == null ? forwardControl.apply(point) : point.follow(prevPoint);
                TravellingPoint.ITrackSelector backTrackSelector = nextPoint == null ? backwardControl.apply(point) : point.follow(nextPoint);
                boolean atFront = (type == 0 || type == 3) && actuallyFirstWheel && actuallyFirstBogey;
                boolean atBack = !(type != 2 && type != 3 || actuallyFirstWheel || actuallyFirstBogey && onTwoBogeys);
                TravellingPoint.IEdgePointListener frontListener = this.train.frontSignalListener();
                TravellingPoint.IEdgePointListener backListener = this.train.backSignalListener();
                TravellingPoint.IEdgePointListener passiveListener = point.ignoreEdgePoints();
                TravellingPoint.ITrackSelector iTrackSelector = trackSelector = (toMove += correction + bogeyCorrection) > 0.0 ? frontTrackSelector : backTrackSelector;
                TravellingPoint.IEdgePointListener signalListener = toMove > 0.0 ? (atFront ? frontListener : (atBack ? backListener : passiveListener)) : (atFront ? backListener : (atBack ? frontListener : passiveListener));
                double moved = point.travel(graph, toMove, trackSelector, signalListener, point.ignoreTurns(), c -> {
                    for (DimensionalCarriageEntity dce : this.entities.values()) {
                        if (!c.either(tnl -> tnl.equalsIgnoreDim((Object)dce.pivot))) continue;
                        return false;
                    }
                    if (this.entities.size() > 1) {
                        this.train.status.doublePortal();
                        return true;
                    }
                    return false;
                });
                this.blocked |= point.blocked;
                distanceMoved.setValue(moved);
            }
        }
        this.updateContraptionAnchors();
        this.manageEntities(level);
        return distanceMoved.getValue();
    }

    public double getAnchorDiff() {
        double diff = 0.0;
        int entries = 0;
        TravellingPoint leadingPoint = this.getLeadingPoint();
        TravellingPoint trailingPoint = this.getTrailingPoint();
        if (leadingPoint.node1 != null && trailingPoint.node1 != null && !leadingPoint.node1.getLocation().dimension.equals(trailingPoint.node1.getLocation().dimension)) {
            return this.bogeySpacing;
        }
        for (DimensionalCarriageEntity dce : this.entities.values()) {
            if (dce.leadingAnchor() == null || dce.trailingAnchor() == null) continue;
            ++entries;
            diff += dce.leadingAnchor().distanceTo(dce.trailingAnchor());
        }
        if (entries == 0) {
            return this.bogeySpacing;
        }
        return diff / (double)entries;
    }

    public void updateConductors() {
        if (this.anyAvailableEntity() == null || this.entities.size() > 1 || this.serialisedPassengers.size() > 0) {
            return;
        }
        this.presentConductors.replace($ -> false);
        for (DimensionalCarriageEntity dimensionalCarriageEntity : this.entities.values()) {
            CarriageContraptionEntity entity = (CarriageContraptionEntity)((Object)dimensionalCarriageEntity.entity.get());
            if (entity == null || !entity.isAlive()) continue;
            this.presentConductors.replaceWithParams((current, checked) -> current != false || checked != false, entity.checkConductors());
        }
    }

    public void manageEntities(Level level) {
        this.currentlyTraversedDimensions.clear();
        this.bogeys.forEach(cb -> {
            if (cb == null) {
                return;
            }
            cb.points.forEach(tp -> {
                if (tp.node1 == null) {
                    return;
                }
                this.currentlyTraversedDimensions.add(tp.node1.getLocation().dimension);
            });
        });
        Iterator<Map.Entry<ResourceKey<Level>, DimensionalCarriageEntity>> iterator = this.entities.entrySet().iterator();
        while (iterator.hasNext()) {
            CarriageContraptionEntity entity;
            DimensionalCarriageEntity dimensionalCarriageEntity;
            block6: {
                boolean discard;
                block4: {
                    ServerLevel currentLevel;
                    block5: {
                        Map.Entry<ResourceKey<Level>, DimensionalCarriageEntity> entry = iterator.next();
                        boolean bl = discard = !this.currentlyTraversedDimensions.isEmpty() && !this.currentlyTraversedDimensions.contains(entry.getKey());
                        MinecraftServer server = level.getServer();
                        if (server == null || (currentLevel = server.getLevel(entry.getKey())) == null) continue;
                        dimensionalCarriageEntity = entry.getValue();
                        entity = (CarriageContraptionEntity)((Object)dimensionalCarriageEntity.entity.get());
                        if (entity != null) break block4;
                        if (!discard) break block5;
                        iterator.remove();
                        break block6;
                    }
                    if (dimensionalCarriageEntity.positionAnchor == null || !CarriageEntityHandler.isActiveChunk((Level)currentLevel, BlockPos.containing((Position)dimensionalCarriageEntity.positionAnchor))) break block6;
                    dimensionalCarriageEntity.createEntity((Level)currentLevel, this.anyAvailableEntity() == null);
                    break block6;
                }
                if (discard) {
                    discard = dimensionalCarriageEntity.discardTicks > 3;
                    ++dimensionalCarriageEntity.discardTicks;
                } else {
                    dimensionalCarriageEntity.discardTicks = 0;
                }
                CarriageEntityHandler.validateCarriageEntity(entity);
                if (!entity.isAlive() || entity.leftTickingChunks || discard) {
                    dimensionalCarriageEntity.removeAndSaveEntity(entity, discard);
                    if (!discard) continue;
                    iterator.remove();
                    continue;
                }
            }
            if ((entity = (CarriageContraptionEntity)((Object)dimensionalCarriageEntity.entity.get())) == null || dimensionalCarriageEntity.positionAnchor == null) continue;
            dimensionalCarriageEntity.alignEntity(entity);
            entity.syncCarriage();
        }
    }

    public void updateContraptionAnchors() {
        CarriageBogey leadingBogey = this.leadingBogey();
        if (leadingBogey.points.either(t -> t.edge == null)) {
            return;
        }
        CarriageBogey trailingBogey = this.trailingBogey();
        if (trailingBogey.points.either(t -> t.edge == null)) {
            return;
        }
        ResourceKey<Level> leadingBogeyDim = leadingBogey.getDimension();
        ResourceKey<Level> trailingBogeyDim = trailingBogey.getDimension();
        double leadingWheelSpacing = leadingBogey.type.getWheelPointSpacing();
        double trailingWheelSpacing = trailingBogey.type.getWheelPointSpacing();
        boolean leadingUpsideDown = leadingBogey.isUpsideDown();
        boolean trailingUpsideDown = trailingBogey.isUpsideDown();
        for (boolean leading : Iterate.trueAndFalse) {
            TravellingPoint point = leading ? this.getLeadingPoint() : this.getTrailingPoint();
            TravellingPoint otherPoint = !leading ? this.getLeadingPoint() : this.getTrailingPoint();
            ResourceKey<Level> dimension = point.node1.getLocation().dimension;
            ResourceKey<Level> otherDimension = otherPoint.node1.getLocation().dimension;
            if (dimension.equals(otherDimension) && leading) {
                this.getDimensional(dimension).discardPivot();
                continue;
            }
            DimensionalCarriageEntity dce = this.getDimensional(dimension);
            dce.positionAnchor = dimension.equals(leadingBogeyDim) ? leadingBogey.getAnchorPosition() : this.pivoted(dce, dimension, point, leading ? leadingWheelSpacing / 2.0 : (double)this.bogeySpacing + trailingWheelSpacing / 2.0, leadingUpsideDown, trailingUpsideDown);
            boolean backAnchorFlip = trailingBogey.isUpsideDown() ^ leadingBogey.isUpsideDown();
            if (this.isOnTwoBogeys()) {
                dce.rotationAnchors.setFirst((Object)(dimension.equals(leadingBogeyDim) ? leadingBogey.getAnchorPosition() : this.pivoted(dce, dimension, point, leading ? leadingWheelSpacing / 2.0 : (double)this.bogeySpacing + trailingWheelSpacing / 2.0, leadingUpsideDown, trailingUpsideDown)));
                dce.rotationAnchors.setSecond((Object)(dimension.equals(trailingBogeyDim) ? trailingBogey.getAnchorPosition(backAnchorFlip) : this.pivoted(dce, dimension, point, leading ? leadingWheelSpacing / 2.0 + (double)this.bogeySpacing : trailingWheelSpacing / 2.0, leadingUpsideDown, trailingUpsideDown)));
            } else if (dimension.equals(otherDimension)) {
                dce.rotationAnchors = leadingBogey.points.map(tp -> tp.getPosition(this.train.graph));
            } else {
                dce.rotationAnchors.setFirst((Object)(leadingBogey.points.getFirst() == point ? point.getPosition(this.train.graph) : this.pivoted(dce, dimension, point, leadingWheelSpacing, leadingUpsideDown, trailingUpsideDown)));
                dce.rotationAnchors.setSecond((Object)(leadingBogey.points.getSecond() == point ? point.getPosition(this.train.graph) : this.pivoted(dce, dimension, point, leadingWheelSpacing, leadingUpsideDown, trailingUpsideDown)));
            }
            int prevmin = dce.minAllowedLocalCoord();
            int prevmax = dce.maxAllowedLocalCoord();
            dce.updateCutoff(leading);
            if (prevmin == dce.minAllowedLocalCoord() && prevmax == dce.maxAllowedLocalCoord()) continue;
            dce.updateRenderedCutoff();
            dce.updatePassengerLoadout();
        }
    }

    private Vec3 pivoted(DimensionalCarriageEntity dce, ResourceKey<Level> dimension, TravellingPoint start, double offset, boolean leadingUpsideDown, boolean trailingUpsideDown) {
        if (this.train.graph == null) {
            return dce.pivot == null ? null : dce.pivot.getLocation();
        }
        TrackNodeLocation pivot = dce.findPivot(dimension, start == this.getLeadingPoint());
        if (pivot == null) {
            return null;
        }
        boolean flipped = start != this.getLeadingPoint() && leadingUpsideDown != trailingUpsideDown;
        Vec3 startVec = start.getPosition(this.train.graph, flipped);
        Vec3 portalVec = pivot.getLocation().add(0.0, leadingUpsideDown ? -1.0 : 1.0, 0.0);
        return VecHelper.lerp((float)((float)(offset / startVec.distanceTo(portalVec))), (Vec3)startVec, (Vec3)portalVec);
    }

    public void alignEntity(Level level) {
        CarriageContraptionEntity entity;
        DimensionalCarriageEntity dimensionalCarriageEntity = this.entities.get(level.dimension());
        if (dimensionalCarriageEntity != null && (entity = (CarriageContraptionEntity)((Object)dimensionalCarriageEntity.entity.get())) != null) {
            dimensionalCarriageEntity.alignEntity(entity);
        }
    }

    public TravellingPoint getLeadingPoint() {
        return this.leadingBogey().leading();
    }

    public TravellingPoint getTrailingPoint() {
        return this.trailingBogey().trailing();
    }

    public CarriageBogey leadingBogey() {
        return (CarriageBogey)this.bogeys.getFirst();
    }

    public CarriageBogey trailingBogey() {
        return this.isOnTwoBogeys() ? (CarriageBogey)this.bogeys.getSecond() : this.leadingBogey();
    }

    public boolean isOnTwoBogeys() {
        return this.bogeys.getSecond() != null;
    }

    public CarriageContraptionEntity anyAvailableEntity() {
        for (DimensionalCarriageEntity dimensionalCarriageEntity : this.entities.values()) {
            CarriageContraptionEntity entity = (CarriageContraptionEntity)((Object)dimensionalCarriageEntity.entity.get());
            if (entity == null) continue;
            return entity;
        }
        return null;
    }

    public Pair<ResourceKey<Level>, DimensionalCarriageEntity> anyAvailableDimensionalCarriage() {
        for (Map.Entry<ResourceKey<Level>, DimensionalCarriageEntity> entry : this.entities.entrySet()) {
            if (entry.getValue().entity.get() == null) continue;
            return Pair.of(entry.getKey(), (Object)entry.getValue());
        }
        return null;
    }

    public void forEachPresentEntity(Consumer<CarriageContraptionEntity> callback) {
        for (DimensionalCarriageEntity dimensionalCarriageEntity : this.entities.values()) {
            CarriageContraptionEntity entity = (CarriageContraptionEntity)((Object)dimensionalCarriageEntity.entity.get());
            if (entity == null) continue;
            callback.accept(entity);
        }
    }

    public CompoundTag write(DimensionPalette dimensions, HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("FirstBogey", (Tag)((CarriageBogey)this.bogeys.getFirst()).write(dimensions));
        if (this.isOnTwoBogeys()) {
            tag.put("SecondBogey", (Tag)((CarriageBogey)this.bogeys.getSecond()).write(dimensions));
        }
        tag.putInt("Spacing", this.bogeySpacing);
        tag.putBoolean("FrontConductor", ((Boolean)this.presentConductors.getFirst()).booleanValue());
        tag.putBoolean("BackConductor", ((Boolean)this.presentConductors.getSecond()).booleanValue());
        tag.putBoolean("Stalled", this.stalled);
        HashMap<Integer, CompoundTag> passengerMap = new HashMap<Integer, CompoundTag>();
        for (DimensionalCarriageEntity dimensionalCarriageEntity : this.entities.values()) {
            CarriageContraptionEntity entity = (CarriageContraptionEntity)((Object)dimensionalCarriageEntity.entity.get());
            if (entity == null) continue;
            this.serialize(entity);
            Contraption contraption = entity.getContraption();
            if (contraption == null) continue;
            Map<UUID, Integer> mapping = contraption.getSeatMapping();
            for (Entity passenger : entity.getPassengers()) {
                CompoundTag data;
                if (!mapping.containsKey(passenger.getUUID()) || !passenger.saveAsPassenger(data = new CompoundTag())) continue;
                passengerMap.put(mapping.get(passenger.getUUID()), data);
            }
        }
        tag.put("Entity", (Tag)this.serialisedEntity.copy());
        CompoundTag passengerTag = new CompoundTag();
        passengerMap.putAll(this.serialisedPassengers);
        passengerMap.forEach((seat, nbt) -> passengerTag.put("Seat" + seat, (Tag)nbt.copy()));
        tag.put("Passengers", (Tag)passengerTag);
        tag.put("EntityPositioning", (Tag)NBTHelper.writeCompoundList(this.entities.entrySet(), e -> {
            CompoundTag c = ((DimensionalCarriageEntity)e.getValue()).write(registries);
            c.putInt("Dim", dimensions.encode((ResourceKey<Level>)((ResourceKey)e.getKey())));
            return c;
        }));
        return tag;
    }

    private void serialize(Entity entity) {
        this.serialisedEntity = new CompoundTag();
        entity.saveAsPassenger(this.serialisedEntity);
        this.serialisedEntity.remove("Passengers");
        this.serialisedEntity.getCompound("Contraption").remove("Passengers");
    }

    public static Carriage read(CompoundTag tag, HolderLookup.Provider registries, TrackGraph graph, DimensionPalette dimensions) {
        CarriageBogey bogey1 = CarriageBogey.read(tag.getCompound("FirstBogey"), graph, dimensions);
        CarriageBogey bogey2 = tag.contains("SecondBogey") ? CarriageBogey.read(tag.getCompound("SecondBogey"), graph, dimensions) : null;
        Carriage carriage = new Carriage(bogey1, bogey2, tag.getInt("Spacing"));
        carriage.stalled = tag.getBoolean("Stalled");
        carriage.presentConductors = Couple.create((Object)tag.getBoolean("FrontConductor"), (Object)tag.getBoolean("BackConductor"));
        carriage.serialisedEntity = tag.getCompound("Entity").copy();
        NBTHelper.iterateCompoundList((ListTag)tag.getList("EntityPositioning", 10), c -> carriage.getDimensional(dimensions.decode(c.getInt("Dim"))).read((CompoundTag)c, registries));
        CompoundTag passengersTag = tag.getCompound("Passengers");
        passengersTag.getAllKeys().forEach(key -> carriage.serialisedPassengers.put(Integer.valueOf(key.substring(4)), passengersTag.getCompound(key)));
        return carriage;
    }

    public class DimensionalCarriageEntity {
        public Vec3 positionAnchor;
        public Couple<Vec3> rotationAnchors;
        public WeakReference<CarriageContraptionEntity> entity = new WeakReference<Object>(null);
        public TrackNodeLocation pivot;
        int discardTicks;
        public float cutoff;
        public boolean pointsInitialised = false;

        public DimensionalCarriageEntity() {
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
            for (Map.Entry<ResourceKey<Level>, DimensionalCarriageEntity> other : Carriage.this.entities.entrySet()) {
                Vec3 loc;
                DimensionalCarriageEntity otherDce = other.getValue();
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
}
