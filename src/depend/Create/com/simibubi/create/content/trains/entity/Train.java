/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.ComponentSerialization
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.Level$ExplosionInteraction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.wrapper.CombinedInvWrapper
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.Create;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.entity.RemoveTrainPacket;
import com.simibubi.create.content.trains.entity.TrainIconType;
import com.simibubi.create.content.trains.entity.TrainMigration;
import com.simibubi.create.content.trains.entity.TrainStatus;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.observer.TrackObserver;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class Train {
    public static final StreamCodec<RegistryFriendlyByteBuf, Train> STREAM_CODEC = CatnipLargerStreamCodecs.composite((StreamCodec)UUIDUtil.STREAM_CODEC, train -> train.id, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)UUIDUtil.STREAM_CODEC), train -> train.owner, (StreamCodec)CatnipStreamCodecBuilders.list(Carriage.STREAM_CODEC), train -> train.carriages, (StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)ByteBufCodecs.VAR_INT), train -> train.carriageSpacing, (StreamCodec)ByteBufCodecs.BOOL, train -> train.doubleEnded, (StreamCodec)ComponentSerialization.STREAM_CODEC, train -> train.name, TrainIconType.STREAM_CODEC, train -> train.icon, (StreamCodec)ByteBufCodecs.INT, train -> train.mapColorIndex, Train::new);
    public double speed = 0.0;
    public double targetSpeed = 0.0;
    public Double speedBeforeStall = null;
    public int carriageWaitingForChunks = -1;
    public double throttle = 1.0;
    public boolean honk = false;
    public UUID id;
    @Nullable
    public UUID owner;
    public TrackGraph graph;
    public Navigation navigation;
    public ScheduleRuntime runtime;
    public TrainIconType icon;
    public int mapColorIndex;
    public Component name;
    public TrainStatus status;
    public boolean invalid;
    public TravellingPoint.SteerDirection manualSteer;
    public boolean manualTick;
    public UUID currentStation;
    public boolean currentlyBackwards;
    public boolean doubleEnded;
    public List<Carriage> carriages;
    public List<Integer> carriageSpacing;
    public boolean updateSignalBlocks;
    public Map<UUID, UUID> occupiedSignalBlocks;
    public Set<UUID> reservedSignalBlocks;
    public Set<UUID> occupiedObservers;
    public Map<UUID, Pair<Integer, Boolean>> cachedObserverFiltering;
    List<TrainMigration> migratingPoints;
    public int migrationCooldown;
    public boolean derailed;
    public int fuelTicks;
    public int honkTicks;
    public Boolean lowHonk;
    public int honkPitch;
    public float accumulatedSteamRelease;
    int tickOffset;
    int ticksSinceLastMailTransfer;
    double[] stress;
    public Player backwardsDriver;

    private Train(UUID id, UUID owner, List<Carriage> carriages, List<Integer> carriageSpacing, boolean doubleEnded, Component name, TrainIconType icon, int mapColorIndex) {
        this(id, owner, null, carriages, carriageSpacing, doubleEnded, name, icon, mapColorIndex);
    }

    public Train(UUID id, UUID owner, TrackGraph graph, List<Carriage> carriages, List<Integer> carriageSpacing, boolean doubleEnded, int mapColorIndex) {
        this(id, owner, graph, carriages, carriageSpacing, doubleEnded, (Component)CreateLang.translateDirect("train.unnamed", new Object[0]), TrainIconType.getDefault(), mapColorIndex);
    }

    public Train(UUID id, UUID owner, TrackGraph graph, List<Carriage> carriages, List<Integer> carriageSpacing, boolean doubleEnded, Component name, TrainIconType icon, int mapColorIndex) {
        this.id = id;
        this.owner = owner;
        this.graph = graph;
        this.carriages = carriages;
        this.carriageSpacing = carriageSpacing;
        this.icon = icon;
        this.mapColorIndex = mapColorIndex;
        this.stress = new double[carriageSpacing.size()];
        this.name = name;
        this.status = new TrainStatus(this);
        this.doubleEnded = doubleEnded;
        carriages.forEach(c -> c.setTrain(this));
        this.navigation = new Navigation(this);
        this.runtime = new ScheduleRuntime(this);
        this.migratingPoints = new ArrayList<TrainMigration>();
        this.currentStation = null;
        this.manualSteer = TravellingPoint.SteerDirection.NONE;
        this.occupiedSignalBlocks = new HashMap<UUID, UUID>();
        this.reservedSignalBlocks = new HashSet<UUID>();
        this.occupiedObservers = new HashSet<UUID>();
        this.cachedObserverFiltering = new HashMap<UUID, Pair<Integer, Boolean>>();
        this.tickOffset = Create.RANDOM.nextInt(100);
    }

    public void earlyTick(Level level) {
        this.status.tick(level);
        if (this.graph == null && !this.migratingPoints.isEmpty()) {
            this.reattachToTracks(level);
        }
        if (this.graph == null) {
            this.addToSignalGroups(this.occupiedSignalBlocks.keySet());
            return;
        }
        if (this.updateSignalBlocks) {
            this.updateSignalBlocks = false;
            this.collectInitiallyOccupiedSignalBlocks();
        }
        this.addToSignalGroups(this.occupiedSignalBlocks.keySet());
        this.addToSignalGroups(this.reservedSignalBlocks);
        if (this.occupiedObservers.isEmpty()) {
            return;
        }
        this.tickOccupiedObservers(level);
    }

    private void tickOccupiedObservers(Level level) {
        int storageVersion = 0;
        for (Carriage carriage : this.carriages) {
            storageVersion += carriage.storage.getVersion();
        }
        for (UUID uuid : this.occupiedObservers) {
            TrackObserver observer = this.graph.getPoint(EdgePointType.OBSERVER, uuid);
            if (observer == null) continue;
            FilterItemStack filter = observer.getFilter();
            if (filter.isEmpty()) {
                observer.keepAlive(this);
                continue;
            }
            Pair cachedMatch = this.cachedObserverFiltering.computeIfAbsent(uuid, $ -> Pair.of((Object)-1, (Object)false));
            boolean shouldActivate = (Boolean)cachedMatch.getSecond();
            if ((Integer)cachedMatch.getFirst() == storageVersion) {
                if (!shouldActivate) continue;
                observer.keepAlive(this);
                continue;
            }
            shouldActivate = false;
            for (Carriage carriage : this.carriages) {
                MountedFluidStorageWrapper tank;
                if (shouldActivate) break;
                CombinedInvWrapper inv = carriage.storage.getAllItems();
                if (inv != null) {
                    for (int slot = 0; slot < inv.getSlots() && !shouldActivate; ++slot) {
                        ItemStack extractItem = inv.extractItem(slot, 1, true);
                        if (extractItem.isEmpty()) continue;
                        shouldActivate |= filter.test(level, extractItem);
                    }
                }
                if ((tank = carriage.storage.getFluids()) == null) continue;
                for (int slot = 0; slot < tank.getTanks() && !shouldActivate; ++slot) {
                    FluidStack drain = tank.drain(1, IFluidHandler.FluidAction.SIMULATE);
                    if (drain.isEmpty()) continue;
                    shouldActivate |= filter.test(level, drain);
                }
            }
            this.cachedObserverFiltering.put(uuid, (Pair<Integer, Boolean>)Pair.of((Object)storageVersion, (Object)shouldActivate));
            if (!shouldActivate) continue;
            observer.keepAlive(this);
        }
    }

    private void addToSignalGroups(Collection<UUID> groups) {
        Map<UUID, SignalEdgeGroup> groupMap = Create.RAILWAYS.signalEdgeGroups;
        Iterator<UUID> iterator = groups.iterator();
        while (iterator.hasNext()) {
            SignalEdgeGroup signalEdgeGroup = groupMap.get(iterator.next());
            if (signalEdgeGroup == null) {
                iterator.remove();
                continue;
            }
            signalEdgeGroup.trains.add(this);
        }
    }

    public void tick(Level level) {
        Create.RAILWAYS.markTracksDirty();
        if (this.graph == null) {
            this.carriages.forEach(c -> c.manageEntities(level));
            this.updateConductors();
            return;
        }
        GlobalStation currentStation = this.getCurrentStation();
        if (currentStation != null) {
            ++this.ticksSinceLastMailTransfer;
            if (this.ticksSinceLastMailTransfer > 20) {
                currentStation.runMailTransfer();
                this.ticksSinceLastMailTransfer = 0;
            }
        }
        this.updateConductors();
        this.runtime.tick(level);
        this.navigation.tick(level);
        this.tickPassiveSlowdown();
        if (this.derailed) {
            this.tickDerailedSlowdown();
        }
        double distance = this.speed;
        Carriage previousCarriage = null;
        int carriageCount = this.carriages.size();
        boolean stalled = false;
        double maxStress = 0.0;
        if (this.carriageWaitingForChunks != -1) {
            distance = 0.0;
        }
        for (int i = 0; i < carriageCount; ++i) {
            Carriage carriage = this.carriages.get(i);
            if (previousCarriage != null) {
                int target = this.carriageSpacing.get(i - 1);
                double actual = target;
                TravellingPoint leadingPoint = carriage.getLeadingPoint();
                TravellingPoint trailingPoint = previousCarriage.getTrailingPoint();
                int entries = 0;
                double total = 0.0;
                if (leadingPoint.node1 != null && trailingPoint.node1 != null) {
                    ResourceKey<Level> d1 = leadingPoint.node1.getLocation().dimension;
                    ResourceKey<Level> d2 = trailingPoint.node1.getLocation().dimension;
                    for (boolean b : Iterate.trueAndFalse) {
                        ResourceKey<Level> d;
                        ResourceKey<Level> resourceKey = d = b ? d1 : d2;
                        if (!b && d1.equals(d2) || !d1.equals(d2)) continue;
                        Carriage.DimensionalCarriageEntity dimensional = carriage.getDimensionalIfPresent(d);
                        Carriage.DimensionalCarriageEntity dimensional2 = previousCarriage.getDimensionalIfPresent(d);
                        if (dimensional == null || dimensional2 == null) continue;
                        Vec3 leadingAnchor = dimensional.leadingAnchor();
                        Vec3 trailingAnchor = dimensional2.trailingAnchor();
                        if (leadingAnchor == null || trailingAnchor == null) continue;
                        double distanceTo = leadingAnchor.distanceToSqr(trailingAnchor);
                        distanceTo = carriage.leadingBogey().isUpsideDown() != previousCarriage.trailingBogey().isUpsideDown() ? Math.sqrt(distanceTo - 4.0) : Math.sqrt(distanceTo);
                        total += distanceTo;
                        ++entries;
                    }
                }
                if (entries > 0) {
                    actual = total / (double)entries;
                }
                this.stress[i - 1] = (double)target - actual;
                maxStress = Math.max(maxStress, Math.abs((double)target - actual));
            }
            previousCarriage = carriage;
            if (!carriage.stalled) continue;
            if (this.speedBeforeStall == null) {
                this.speedBeforeStall = this.speed;
            }
            distance = 0.0;
            this.speed = 0.0;
            stalled = true;
        }
        if (!stalled && this.speedBeforeStall != null) {
            this.speed = Mth.clamp((double)this.speedBeforeStall, (double)-1.0, (double)1.0);
            this.speedBeforeStall = null;
        }
        boolean approachingStation = this.navigation.distanceToDestination < 5.0;
        double leadingModifier = approachingStation ? 0.75 : 0.5;
        double trailingModifier = approachingStation ? 0.0 : 0.125;
        boolean blocked = false;
        boolean iterateFromBack = this.speed < 0.0;
        for (int index = 0; index < carriageCount; ++index) {
            boolean last;
            double leadingStress;
            int i;
            int n = i = iterateFromBack ? carriageCount - 1 - index : index;
            double d = i == 0 ? 0.0 : (leadingStress = this.stress[i - 1] * -(iterateFromBack ? trailingModifier : leadingModifier));
            double trailingStress = i == this.stress.length ? 0.0 : this.stress[i] * (iterateFromBack ? leadingModifier : trailingModifier);
            Carriage carriage = this.carriages.get(i);
            TravellingPoint toFollowForward = i == 0 ? null : this.carriages.get(i - 1).getTrailingPoint();
            TravellingPoint toFollowBackward = i == carriageCount - 1 ? null : this.carriages.get(i + 1).getLeadingPoint();
            double totalStress = this.derailed ? 0.0 : leadingStress + trailingStress;
            boolean first = i == 0;
            boolean bl = last = i == carriageCount - 1;
            int carriageType = first ? (last ? 3 : 0) : (last ? 2 : 1);
            double actualDistance = carriage.travel(level, this.graph, distance + totalStress, toFollowForward, toFollowBackward, carriageType);
            blocked |= carriage.blocked || carriage.isOnIncompatibleTrack();
            boolean onTwoBogeys = carriage.isOnTwoBogeys();
            maxStress = Math.max(maxStress, onTwoBogeys ? (double)carriage.bogeySpacing - carriage.getAnchorDiff() : 0.0);
            maxStress = Math.max(maxStress, carriage.leadingBogey().getStress());
            if (onTwoBogeys) {
                maxStress = Math.max(maxStress, carriage.trailingBogey().getStress());
            }
            if (index != 0) continue;
            distance = actualDistance;
            this.collideWithOtherTrains(level, carriage);
            this.backwardsDriver = null;
            if (this.graph != null) continue;
            return;
        }
        if (blocked) {
            this.speed = 0.0;
            this.navigation.cancelNavigation();
            this.runtime.tick(level);
            this.status.endOfTrack();
        } else if (maxStress > 4.0) {
            this.speed = 0.0;
            this.navigation.cancelNavigation();
            this.runtime.tick(level);
            this.derailed = true;
            this.status.highStress();
        } else if (this.speed != 0.0) {
            this.status.trackOK();
        }
        this.updateNavigationTarget(level, distance);
    }

    public TravellingPoint.IEdgePointListener frontSignalListener() {
        return (distance, couple) -> {
            Object patt0$temp = couple.getFirst();
            if (patt0$temp instanceof GlobalStation) {
                GlobalStation station = (GlobalStation)patt0$temp;
                if (!station.canApproachFrom((TrackNode)((Couple)couple.getSecond()).getSecond()) || this.navigation.destination != station) {
                    return false;
                }
                this.speed = 0.0;
                this.navigation.distanceToDestination = 0.0;
                this.navigation.currentPath.clear();
                this.arriveAt(this.navigation.destination);
                this.navigation.destination = null;
                return true;
            }
            Object patt1$temp = couple.getFirst();
            if (patt1$temp instanceof TrackObserver) {
                TrackObserver observer = (TrackObserver)patt1$temp;
                this.occupiedObservers.add(observer.getId());
                return false;
            }
            Object patt2$temp = couple.getFirst();
            if (!(patt2$temp instanceof SignalBoundary)) {
                return false;
            }
            SignalBoundary signal = (SignalBoundary)patt2$temp;
            if (this.navigation.waitingForSignal != null && ((UUID)this.navigation.waitingForSignal.getFirst()).equals(signal.getId())) {
                this.speed = 0.0;
                this.navigation.distanceToSignal = 0.0;
                return true;
            }
            UUID groupId = signal.getGroup((TrackNode)((Couple)couple.getSecond()).getSecond());
            SignalEdgeGroup signalEdgeGroup = Create.RAILWAYS.signalEdgeGroups.get(groupId);
            if (signalEdgeGroup == null) {
                return false;
            }
            if ((this.runtime.getSchedule() == null || this.runtime.paused) && signalEdgeGroup.isOccupiedUnless(this)) {
                this.carriages.forEach(c -> c.forEachPresentEntity(cce -> cce.getControllingPlayer().ifPresent(uuid -> AllAdvancements.RED_SIGNAL.awardTo(cce.level().getPlayerByUUID(uuid)))));
            }
            signalEdgeGroup.reserved = signal;
            this.occupy(groupId, signal.id);
            return false;
        };
    }

    public void cancelStall() {
        this.speedBeforeStall = null;
        this.carriages.forEach(c -> {
            c.stalled = false;
            c.forEachPresentEntity(cce -> cce.getContraption().getActors().forEach(pair -> {
                MovementBehaviour behaviour = MovementBehaviour.REGISTRY.get((StateHolder<Block, ?>)((StructureTemplate.StructureBlockInfo)pair.getKey()).state());
                if (behaviour != null) {
                    behaviour.cancelStall((MovementContext)pair.getValue());
                }
            }));
        });
    }

    private boolean occupy(UUID groupId, @Nullable UUID boundaryId) {
        this.reservedSignalBlocks.remove(groupId);
        if (boundaryId != null && this.occupiedSignalBlocks.containsKey(groupId) && boundaryId.equals(this.occupiedSignalBlocks.get(groupId))) {
            return false;
        }
        return this.occupiedSignalBlocks.put(groupId, boundaryId) == null;
    }

    public TravellingPoint.IEdgePointListener backSignalListener() {
        return (distance, couple) -> {
            Object patt0$temp = couple.getFirst();
            if (patt0$temp instanceof TrackObserver) {
                TrackObserver observer = (TrackObserver)patt0$temp;
                this.occupiedObservers.remove(observer.getId());
                this.cachedObserverFiltering.remove(observer.getId());
                return false;
            }
            Object patt1$temp = couple.getFirst();
            if (!(patt1$temp instanceof SignalBoundary)) {
                return false;
            }
            SignalBoundary signal = (SignalBoundary)patt1$temp;
            UUID groupId = signal.getGroup((TrackNode)((Couple)couple.getSecond()).getFirst());
            this.occupiedSignalBlocks.remove(groupId);
            return false;
        };
    }

    private void updateNavigationTarget(Level level, double distance) {
        DiscoveredPath preferredPath;
        if (this.navigation.destination == null) {
            return;
        }
        Pair<UUID, Boolean> blockingSignal = this.navigation.waitingForSignal;
        boolean fullRefresh = this.navigation.distanceToDestination > 100.0 && this.navigation.distanceToDestination % 100.0 > 20.0;
        boolean signalRefresh = blockingSignal != null && this.navigation.distanceToSignal % 50.0 > 5.0;
        boolean partialRefresh = this.navigation.distanceToDestination < 100.0 && this.navigation.distanceToDestination % 50.0 > 5.0;
        double toSubstract = this.navigation.destinationBehindTrain ? -distance : distance;
        boolean navigatingManually = this.runtime.paused;
        this.navigation.distanceToDestination -= toSubstract;
        if (blockingSignal != null) {
            this.navigation.distanceToSignal -= toSubstract;
            signalRefresh &= this.navigation.distanceToSignal % 50.0 < 5.0;
        }
        fullRefresh &= this.navigation.distanceToDestination % 100.0 <= 20.0;
        partialRefresh &= this.navigation.distanceToDestination % 50.0 <= 5.0;
        if (blockingSignal != null && this.navigation.ticksWaitingForSignal % 100 == 50) {
            SignalBoundary signal = this.graph.getPoint(EdgePointType.SIGNAL, (UUID)blockingSignal.getFirst());
            fullRefresh |= signal != null && signal.types.get(((Boolean)blockingSignal.getSecond()).booleanValue()) == SignalBlock.SignalType.CROSS_SIGNAL;
        }
        if (signalRefresh) {
            this.navigation.waitingForSignal = null;
        }
        if (!fullRefresh && !partialRefresh) {
            return;
        }
        if (!this.reservedSignalBlocks.isEmpty()) {
            return;
        }
        if (!navigatingManually && fullRefresh && (preferredPath = this.runtime.startCurrentInstruction(level)) != null) {
            this.navigation.startNavigation(preferredPath);
        }
    }

    private void tickDerailedSlowdown() {
        this.speed /= 3.0;
        if (Mth.equal((double)this.speed, (double)0.0)) {
            this.speed = 0.0;
        }
    }

    private void tickPassiveSlowdown() {
        if (!this.manualTick && this.navigation.destination == null && this.speed != 0.0) {
            double acceleration = this.acceleration();
            this.speed = this.speed > 0.0 ? Math.max(this.speed - acceleration, 0.0) : Math.min(this.speed + acceleration, 0.0);
        }
        this.manualTick = false;
    }

    private void updateConductors() {
        for (Carriage carriage : this.carriages) {
            carriage.updateConductors();
        }
    }

    public boolean hasForwardConductor() {
        for (Carriage carriage : this.carriages) {
            if (!((Boolean)carriage.presentConductors.getFirst()).booleanValue()) continue;
            return true;
        }
        return false;
    }

    public boolean hasBackwardConductor() {
        for (Carriage carriage : this.carriages) {
            if (!((Boolean)carriage.presentConductors.getSecond()).booleanValue()) continue;
            return true;
        }
        return false;
    }

    private void collideWithOtherTrains(Level level, Carriage carriage) {
        Vec3 end;
        if (this.derailed) {
            return;
        }
        TravellingPoint trailingPoint = carriage.getTrailingPoint();
        TravellingPoint leadingPoint = carriage.getLeadingPoint();
        if (leadingPoint.node1 == null || trailingPoint.node1 == null || leadingPoint.edge == null || trailingPoint.edge == null) {
            return;
        }
        ResourceKey<Level> dimension = leadingPoint.node1.getLocation().dimension;
        if (!dimension.equals(trailingPoint.node1.getLocation().dimension)) {
            return;
        }
        Vec3 start = (this.speed < 0.0 ? trailingPoint : leadingPoint).getPosition(this.graph);
        Pair<Train, Vec3> collision = this.findCollidingTrain(level, start, end = (this.speed < 0.0 ? leadingPoint : trailingPoint).getPosition(this.graph), dimension);
        if (collision == null) {
            return;
        }
        Train train = (Train)collision.getFirst();
        double combinedSpeed = Math.abs(this.speed) + Math.abs(train.speed);
        if (combinedSpeed > (double)0.2f) {
            Vec3 v = (Vec3)collision.getSecond();
            level.explode(null, v.x, v.y, v.z, (float)Math.min(3.0 * combinedSpeed, 5.0), Level.ExplosionInteraction.NONE);
        }
        this.crash();
        train.crash();
    }

    public Pair<Train, Vec3> findCollidingTrain(Level level, Vec3 start, Vec3 end, ResourceKey<Level> dimension) {
        Vec3 diff = end.subtract(start);
        double maxDistanceSqr = Math.pow(((Integer)AllConfigs.server().trains.maxAssemblyLength.get()).intValue(), 2.0);
        block0: for (Train train : Create.RAILWAYS.sided((LevelAccessor)level).trains.values()) {
            if (train == this || train.graph != null && train.graph != this.graph) continue;
            Vec3 lastPoint = null;
            for (Carriage otherCarriage : train.carriages) {
                for (boolean betweenBits : Iterate.trueAndFalse) {
                    Vec3 normedDiff2;
                    ResourceKey<Level> otherDimension;
                    if (betweenBits && lastPoint == null) continue;
                    TravellingPoint otherLeading = otherCarriage.getLeadingPoint();
                    TravellingPoint otherTrailing = otherCarriage.getTrailingPoint();
                    if (otherLeading.edge == null || otherTrailing.edge == null || !(otherDimension = otherLeading.node1.getLocation().dimension).equals(otherTrailing.node1.getLocation().dimension) || !otherDimension.equals(dimension)) continue;
                    Vec3 start2 = otherLeading.getPosition(train.graph);
                    Vec3 end2 = otherTrailing.getPosition(train.graph);
                    if (Math.min(start2.distanceToSqr(start), end2.distanceToSqr(start)) > maxDistanceSqr) continue block0;
                    if (betweenBits) {
                        end2 = start2;
                        start2 = lastPoint;
                    }
                    lastPoint = end2;
                    if ((end.y < end2.y - 3.0 || end2.y < end.y - 3.0) && (start.y < start2.y - 3.0 || start2.y < start.y - 3.0)) continue;
                    Vec3 diff2 = end2.subtract(start2);
                    Vec3 normedDiff = diff.normalize();
                    double[] intersect = VecHelper.intersect((Vec3)start, (Vec3)start2, (Vec3)normedDiff, (Vec3)(normedDiff2 = diff2.normalize()), (Direction.Axis)Direction.Axis.Y);
                    if (intersect == null) {
                        Vec3 intersectSphere = VecHelper.intersectSphere((Vec3)start2, (Vec3)normedDiff2, (Vec3)start, (double)0.125);
                        if (intersectSphere == null || !Mth.equal((double)normedDiff2.dot(intersectSphere.subtract(start2).normalize()), (double)1.0)) continue;
                        intersect = new double[]{intersectSphere.distanceTo(start) - 0.125, intersectSphere.distanceTo(start2) - 0.125};
                    }
                    if (intersect[0] > diff.length() || intersect[1] > diff2.length() || intersect[0] < 0.0 || intersect[1] < 0.0) continue;
                    return Pair.of((Object)train, (Object)start.add(normedDiff.scale(intersect[0])));
                }
            }
        }
        return null;
    }

    public void crash() {
        this.navigation.cancelNavigation();
        if (this.derailed) {
            return;
        }
        this.speed = -Mth.clamp((double)this.speed, (double)-0.5, (double)0.5);
        this.derailed = true;
        this.graph = null;
        this.status.crash();
        for (Carriage carriage : this.carriages) {
            carriage.forEachPresentEntity(e -> e.getIndirectPassengers().forEach(entity -> {
                if (!(entity instanceof Player)) {
                    return;
                }
                Player p = (Player)entity;
                Optional<UUID> controllingPlayer = e.getControllingPlayer();
                if (controllingPlayer.isPresent() && controllingPlayer.get().equals(p.getUUID())) {
                    return;
                }
                AllAdvancements.TRAIN_CRASH.awardTo(p);
            }));
        }
        if (this.backwardsDriver != null) {
            AllAdvancements.TRAIN_CRASH_BACKWARDS.awardTo(this.backwardsDriver);
        }
    }

    public boolean disassemble(Direction assemblyDirection, BlockPos pos) {
        if (!this.canDisassemble()) {
            return false;
        }
        int offset = 1;
        boolean backwards = this.currentlyBackwards;
        Level level = null;
        for (int i = 0; i < this.carriages.size(); ++i) {
            Carriage carriage = this.carriages.get(backwards ? this.carriages.size() - i - 1 : i);
            CarriageContraptionEntity entity = carriage.anyAvailableEntity();
            if (entity == null) {
                return false;
            }
            level = entity.level();
            Contraption contraption = entity.getContraption();
            if (contraption instanceof CarriageContraption) {
                CarriageContraption cc = (CarriageContraption)contraption;
                cc.returnStorageForDisassembly(carriage.storage);
            }
            entity.setPos(Vec3.atLowerCornerOf((Vec3i)pos.relative(assemblyDirection, backwards ? offset + carriage.bogeySpacing : offset).below(carriage.leadingBogey().isUpsideDown() ? 2 : 0)));
            entity.disassemble();
            for (CarriageBogey bogey : carriage.bogeys) {
                BlockEntity be;
                Vec3 bogeyPosition;
                if (bogey == null || (bogeyPosition = bogey.getAnchorPosition()) == null || !((be = level.getBlockEntity(BlockPos.containing((Position)bogeyPosition))) instanceof AbstractBogeyBlockEntity)) continue;
                AbstractBogeyBlockEntity sbbe = (AbstractBogeyBlockEntity)be;
                sbbe.setBogeyData(bogey.bogeyData);
            }
            offset += carriage.bogeySpacing;
            if (i >= this.carriageSpacing.size()) continue;
            offset += this.carriageSpacing.get(backwards ? this.carriageSpacing.size() - i - 1 : i).intValue();
        }
        GlobalStation currentStation = this.getCurrentStation();
        if (currentStation != null) {
            currentStation.cancelReservation(this);
            BlockPos blockEntityPos = currentStation.getBlockEntityPos();
            BlockEntity blockEntity = level.getBlockEntity(blockEntityPos);
            if (blockEntity instanceof StationBlockEntity) {
                StationBlockEntity sbe = (StationBlockEntity)blockEntity;
                sbe.lastDisassembledTrainName = this.name.copy();
                sbe.lastDisassembledMapColorIndex = this.mapColorIndex;
            }
        }
        Create.RAILWAYS.removeTrain(this.id);
        CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new RemoveTrainPacket(this));
        return true;
    }

    public boolean canDisassemble() {
        for (Carriage carriage : this.carriages) {
            if (carriage.presentInMultipleDimensions()) {
                return false;
            }
            CarriageContraptionEntity entity = carriage.anyAvailableEntity();
            if (entity == null) {
                return false;
            }
            if (!Mth.equal((float)entity.pitch, (float)0.0f)) {
                return false;
            }
            if (Mth.equal((float)((entity.yaw % 90.0f + 360.0f) % 90.0f), (float)0.0f)) continue;
            return false;
        }
        return true;
    }

    public boolean isTravellingOn(TrackNode node) {
        MutableBoolean affected = new MutableBoolean(false);
        this.forEachTravellingPoint(tp -> {
            if (tp.node1 == node || tp.node2 == node) {
                affected.setTrue();
            }
        });
        return affected.booleanValue();
    }

    public void detachFromTracks() {
        this.migratingPoints.clear();
        this.navigation.cancelNavigation();
        this.forEachTravellingPoint(tp -> this.migratingPoints.add(new TrainMigration((TravellingPoint)tp)));
        this.graph = null;
    }

    public void forEachTravellingPoint(Consumer<TravellingPoint> callback) {
        for (Carriage c : this.carriages) {
            c.leadingBogey().points.forEach(callback::accept);
            if (!c.isOnTwoBogeys()) continue;
            c.trailingBogey().points.forEach(callback::accept);
        }
    }

    public void forEachTravellingPointBackwards(BiConsumer<TravellingPoint, Double> callback) {
        double lastWheelOffset = 0.0;
        for (int i = 0; i < this.carriages.size(); ++i) {
            int index = this.carriages.size() - i - 1;
            Carriage carriage = this.carriages.get(index);
            CarriageBogey trailingBogey = carriage.trailingBogey();
            double trailSpacing = trailingBogey.type.getWheelPointSpacing();
            callback.accept(trailingBogey.trailing(), i == 0 ? 0.0 : (double)this.carriageSpacing.get(index).intValue() - lastWheelOffset - trailSpacing / 2.0);
            callback.accept(trailingBogey.leading(), trailSpacing);
            lastWheelOffset = trailSpacing / 2.0;
            if (!carriage.isOnTwoBogeys()) continue;
            CarriageBogey leadingBogey = carriage.leadingBogey();
            double leadSpacing = carriage.leadingBogey().type.getWheelPointSpacing();
            callback.accept(leadingBogey.trailing(), (double)carriage.bogeySpacing - lastWheelOffset - leadSpacing / 2.0);
            callback.accept(trailingBogey.leading(), leadSpacing);
            lastWheelOffset = leadSpacing / 2.0;
        }
    }

    public void reattachToTracks(Level level) {
        if (this.migrationCooldown > 0) {
            --this.migrationCooldown;
            return;
        }
        HashSet<Map.Entry<UUID, TrackGraph>> entrySet = new HashSet<Map.Entry<UUID, TrackGraph>>(Create.RAILWAYS.trackNetworks.entrySet());
        HashMap<UUID, List> successfulMigrations = new HashMap<UUID, List>();
        for (TrainMigration md : this.migratingPoints) {
            Iterator iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                TrackGraphLocation gl = md.tryMigratingTo((TrackGraph)entry.getValue());
                if (gl == null) {
                    iterator.remove();
                    continue;
                }
                successfulMigrations.computeIfAbsent((UUID)entry.getKey(), uuid -> new ArrayList()).add(gl);
            }
        }
        if (entrySet.isEmpty()) {
            this.migrationCooldown = 40;
            this.status.failedMigration();
            this.derailed = true;
            return;
        }
        Iterator<TrainMigration> iterator = entrySet.iterator();
        if (iterator.hasNext()) {
            GlobalStation currentStation;
            Map.Entry entry = (Map.Entry)((Object)iterator.next());
            this.graph = (TrackGraph)entry.getValue();
            List locations = (List)successfulMigrations.get(entry.getKey());
            this.forEachTravellingPoint(tp -> tp.migrateTo(locations));
            this.migratingPoints.clear();
            if (this.derailed) {
                this.status.successfulMigration();
            }
            this.derailed = false;
            if (this.runtime.getSchedule() != null && this.runtime.state == ScheduleRuntime.State.IN_TRANSIT) {
                this.runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
            }
            if ((currentStation = this.getCurrentStation()) != null) {
                currentStation.reserveFor(this);
            }
            this.updateSignalBlocks = true;
            this.migrationCooldown = 0;
            return;
        }
    }

    public int getTotalLength() {
        int length = 0;
        for (int i = 0; i < this.carriages.size(); ++i) {
            Carriage carriage = this.carriages.get(i);
            if (i == 0) {
                length = (int)((double)length + carriage.leadingBogey().type.getWheelPointSpacing() / 2.0);
            }
            if (i == this.carriages.size() - 1) {
                length = (int)((double)length + carriage.trailingBogey().type.getWheelPointSpacing() / 2.0);
            }
            length += carriage.bogeySpacing;
            if (i >= this.carriageSpacing.size()) continue;
            length += this.carriageSpacing.get(i).intValue();
        }
        return length;
    }

    public void leaveStation() {
        GlobalStation currentStation = this.getCurrentStation();
        if (currentStation != null) {
            currentStation.trainDeparted(this);
        }
        this.currentStation = null;
    }

    public void arriveAt(GlobalStation station) {
        this.setCurrentStation(station);
        this.reservedSignalBlocks.clear();
        this.runtime.destinationReached();
        station.runMailTransfer();
        this.ticksSinceLastMailTransfer = 0;
    }

    public void setCurrentStation(GlobalStation station) {
        this.currentStation = station.id;
    }

    public GlobalStation getCurrentStation() {
        if (this.currentStation == null) {
            return null;
        }
        if (this.graph == null) {
            return null;
        }
        return this.graph.getPoint(EdgePointType.STATION, this.currentStation);
    }

    @Nullable
    public LivingEntity getOwner(Level level) {
        if (level.getServer() == null) {
            return null;
        }
        try {
            UUID uuid = this.owner;
            return uuid == null ? null : level.getServer().getPlayerList().getPlayer(uuid);
        }
        catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    public void approachTargetSpeed(float accelerationMod) {
        double actualTarget = this.targetSpeed;
        if (Mth.equal((double)actualTarget, (double)this.speed)) {
            return;
        }
        if (this.manualTick) {
            this.leaveStation();
        }
        double acceleration = this.acceleration();
        if (this.speed < actualTarget) {
            this.speed = Math.min(this.speed + acceleration * (double)accelerationMod, actualTarget);
        } else if (this.speed > actualTarget) {
            this.speed = Math.max(this.speed - acceleration * (double)accelerationMod, actualTarget);
        }
    }

    public void collectInitiallyOccupiedSignalBlocks() {
        TravellingPoint trailingPoint = this.carriages.get(this.carriages.size() - 1).getTrailingPoint();
        TrackNode node1 = trailingPoint.node1;
        TrackNode node2 = trailingPoint.node2;
        TrackEdge edge = trailingPoint.edge;
        if (edge == null) {
            return;
        }
        double position = trailingPoint.position;
        EdgeData signalData = edge.getEdgeData();
        this.occupiedSignalBlocks.clear();
        this.reservedSignalBlocks.clear();
        this.occupiedObservers.clear();
        this.cachedObserverFiltering.clear();
        TravellingPoint signalScout = new TravellingPoint(node1, node2, edge, position, false);
        Map<UUID, SignalEdgeGroup> allGroups = Create.RAILWAYS.signalEdgeGroups;
        MutableObject prevGroup = new MutableObject(null);
        if (signalData.hasSignalBoundaries()) {
            SignalBoundary nextBoundary = signalData.next(EdgePointType.SIGNAL, position);
            if (nextBoundary == null) {
                UUID group;
                double d2 = 0.0;
                SignalBoundary prev = null;
                SignalBoundary current = signalData.next(EdgePointType.SIGNAL, 0.0);
                while (current != null) {
                    prev = current;
                    d2 = current.getLocationOn(edge);
                    current = signalData.next(EdgePointType.SIGNAL, d2);
                }
                if (prev != null && Create.RAILWAYS.signalEdgeGroups.containsKey(group = prev.getGroup(node2))) {
                    this.occupy(group, null);
                    prevGroup.setValue((Object)group);
                }
            } else {
                UUID group = nextBoundary.getGroup(node1);
                if (Create.RAILWAYS.signalEdgeGroups.containsKey(group)) {
                    this.occupy(group, null);
                    prevGroup.setValue((Object)group);
                }
            }
        } else {
            UUID groupId = signalData.getEffectiveEdgeGroupId(this.graph);
            if (allGroups.containsKey(groupId)) {
                this.occupy(groupId, null);
                prevGroup.setValue((Object)groupId);
            }
        }
        this.forEachTravellingPointBackwards((tp, d) -> signalScout.travel(this.graph, (double)d, signalScout.follow((TravellingPoint)tp), (distance, couple) -> {
            Object patt0$temp = couple.getFirst();
            if (patt0$temp instanceof TrackObserver) {
                TrackObserver observer = (TrackObserver)patt0$temp;
                this.occupiedObservers.add(observer.getId());
                return false;
            }
            Object patt1$temp = couple.getFirst();
            if (!(patt1$temp instanceof SignalBoundary)) {
                return false;
            }
            SignalBoundary signal = (SignalBoundary)patt1$temp;
            ((Couple)couple.getSecond()).map(signal::getGroup).forEach(id -> {
                if (!Create.RAILWAYS.signalEdgeGroups.containsKey(id)) {
                    return;
                }
                if (id.equals(prevGroup.getValue())) {
                    return;
                }
                this.occupy((UUID)id, null);
                prevGroup.setValue(id);
            });
            return false;
        }, signalScout.ignoreTurns()));
    }

    public boolean shouldCarriageSyncThisTick(long gameTicks, int updateInterval) {
        return (gameTicks + (long)this.tickOffset) % (long)updateInterval == 0L;
    }

    public Couple<Couple<TrackNode>> getEndpointEdges() {
        return Couple.create((Object)this.carriages.get(0).getLeadingPoint(), (Object)this.carriages.get(this.carriages.size() - 1).getTrailingPoint()).map(tp -> Couple.create((Object)tp.node1, (Object)tp.node2));
    }

    public int getNavigationPenalty() {
        if (this.manualTick) {
            return 200;
        }
        if (this.runtime.getSchedule() == null || this.runtime.paused) {
            return 700;
        }
        if (this.navigation.waitingForSignal != null && this.navigation.ticksWaitingForSignal > 0) {
            return 50 + Math.min(this.navigation.ticksWaitingForSignal / 20, 1000);
        }
        if (this.navigation.destination != null && this.navigation.distanceToDestination < 50.0 || this.navigation.distanceToSignal < 20.0) {
            return 50;
        }
        return 25;
    }

    public void burnFuel() {
        if (this.fuelTicks > 0) {
            --this.fuelTicks;
            return;
        }
        boolean iterateFromBack = this.speed < 0.0;
        int carriageCount = this.carriages.size();
        for (int index = 0; index < carriageCount; ++index) {
            int i = iterateFromBack ? carriageCount - 1 - index : index;
            Carriage carriage = this.carriages.get(i);
            MountedItemStorageWrapper fuelItems = carriage.storage.getFuelItems();
            if (fuelItems == null) continue;
            for (int slot = 0; slot < fuelItems.getSlots(); ++slot) {
                ItemStack stack = fuelItems.extractItem(slot, 1, true);
                int burnTime = stack.getBurnTime(null);
                if (burnTime <= 0) continue;
                stack = fuelItems.extractItem(slot, 1, false);
                this.fuelTicks += burnTime * stack.getCount();
                ItemStack containerItem = stack.getCraftingRemainingItem();
                if (!containerItem.isEmpty()) {
                    ItemHandlerHelper.insertItemStacked((IItemHandler)fuelItems, (ItemStack)containerItem, (boolean)false);
                }
                return;
            }
        }
    }

    public float maxSpeed() {
        return (this.fuelTicks > 0 ? AllConfigs.server().trains.poweredTrainTopSpeed.getF() : AllConfigs.server().trains.trainTopSpeed.getF()) / 20.0f;
    }

    public float maxTurnSpeed() {
        return (this.fuelTicks > 0 ? AllConfigs.server().trains.poweredTrainTurningTopSpeed.getF() : AllConfigs.server().trains.trainTurningTopSpeed.getF()) / 20.0f;
    }

    public float acceleration() {
        return (this.fuelTicks > 0 ? AllConfigs.server().trains.poweredTrainAcceleration.getF() : AllConfigs.server().trains.trainAcceleration.getF()) / 400.0f;
    }

    public CompoundTag write(DimensionPalette dimensions, HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", this.id);
        if (this.owner != null) {
            tag.putUUID("Owner", this.owner);
        }
        if (this.graph != null) {
            tag.putUUID("Graph", this.graph.id);
        }
        tag.put("Carriages", (Tag)NBTHelper.writeCompoundList(this.carriages, c -> c.write(dimensions, registries)));
        tag.putIntArray("CarriageSpacing", this.carriageSpacing);
        tag.putBoolean("DoubleEnded", this.doubleEnded);
        tag.putDouble("Speed", this.speed);
        tag.putDouble("Throttle", this.throttle);
        if (this.speedBeforeStall != null) {
            tag.putDouble("SpeedBeforeStall", this.speedBeforeStall.doubleValue());
        }
        tag.putInt("Fuel", this.fuelTicks);
        tag.putDouble("TargetSpeed", this.targetSpeed);
        tag.putString("IconType", this.icon.id.toString());
        tag.putInt("MapColorIndex", this.mapColorIndex);
        tag.putString("Name", Component.Serializer.toJson((Component)this.name, (HolderLookup.Provider)registries));
        if (this.currentStation != null) {
            tag.putUUID("Station", this.currentStation);
        }
        tag.putBoolean("Backwards", this.currentlyBackwards);
        tag.putBoolean("Derailed", this.derailed);
        tag.putBoolean("UpdateSignals", this.updateSignalBlocks);
        tag.put("SignalBlocks", (Tag)NBTHelper.writeCompoundList(this.occupiedSignalBlocks.entrySet(), e -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putUUID("Id", (UUID)e.getKey());
            if (e.getValue() != null) {
                compoundTag.putUUID("Boundary", (UUID)e.getValue());
            }
            return compoundTag;
        }));
        tag.put("ReservedSignalBlocks", (Tag)NBTHelper.writeCompoundList(this.reservedSignalBlocks, uid -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putUUID("Id", uid);
            return compoundTag;
        }));
        tag.put("OccupiedObservers", (Tag)NBTHelper.writeCompoundList(this.occupiedObservers, uid -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putUUID("Id", uid);
            return compoundTag;
        }));
        tag.put("MigratingPoints", (Tag)NBTHelper.writeCompoundList(this.migratingPoints, tm -> tm.write(dimensions)));
        tag.put("Runtime", (Tag)this.runtime.write(registries));
        tag.put("Navigation", (Tag)this.navigation.write(dimensions));
        return tag;
    }

    public static Train read(CompoundTag tag, HolderLookup.Provider registries, Map<UUID, TrackGraph> trackNetworks, DimensionPalette dimensions) {
        UUID id = tag.getUUID("Id");
        UUID owner = tag.contains("Owner") ? tag.getUUID("Owner") : null;
        UUID graphId = tag.contains("Graph") ? tag.getUUID("Graph") : null;
        TrackGraph graph = graphId == null ? null : trackNetworks.get(graphId);
        ArrayList<Carriage> carriages = new ArrayList<Carriage>();
        NBTHelper.iterateCompoundList((ListTag)tag.getList("Carriages", 10), c -> carriages.add(Carriage.read(c, registries, graph, dimensions)));
        ArrayList<Integer> carriageSpacing = new ArrayList<Integer>();
        for (int i : tag.getIntArray("CarriageSpacing")) {
            carriageSpacing.add(i);
        }
        boolean doubleEnded = tag.getBoolean("DoubleEnded");
        int mapColorIndex = tag.getInt("MapColorIndex");
        Train train = new Train(id, owner, graph, carriages, carriageSpacing, doubleEnded, mapColorIndex);
        train.speed = tag.getDouble("Speed");
        train.throttle = tag.getDouble("Throttle");
        if (tag.contains("SpeedBeforeStall")) {
            train.speedBeforeStall = tag.getDouble("SpeedBeforeStall");
        }
        train.targetSpeed = tag.getDouble("TargetSpeed");
        train.icon = TrainIconType.byId(ResourceLocation.parse((String)tag.getString("IconType")));
        train.name = Component.Serializer.fromJson((String)tag.getString("Name"), (HolderLookup.Provider)registries);
        train.currentStation = tag.contains("Station") ? tag.getUUID("Station") : null;
        train.currentlyBackwards = tag.getBoolean("Backwards");
        train.derailed = tag.getBoolean("Derailed");
        train.updateSignalBlocks = tag.getBoolean("UpdateSignals");
        train.fuelTicks = tag.getInt("Fuel");
        NBTHelper.iterateCompoundList((ListTag)tag.getList("SignalBlocks", 10), c -> train.occupiedSignalBlocks.put(c.getUUID("Id"), c.contains("Boundary") ? c.getUUID("Boundary") : null));
        NBTHelper.iterateCompoundList((ListTag)tag.getList("ReservedSignalBlocks", 10), c -> train.reservedSignalBlocks.add(c.getUUID("Id")));
        NBTHelper.iterateCompoundList((ListTag)tag.getList("OccupiedObservers", 10), c -> train.occupiedObservers.add(c.getUUID("Id")));
        NBTHelper.iterateCompoundList((ListTag)tag.getList("MigratingPoints", 10), c -> train.migratingPoints.add(TrainMigration.read(c, dimensions)));
        train.runtime.read(registries, tag.getCompound("Runtime"));
        train.navigation.read(tag.getCompound("Navigation"), graph, dimensions);
        if (train.getCurrentStation() != null) {
            train.getCurrentStation().reserveFor(train);
        }
        return train;
    }

    public int countPlayerPassengers() {
        AtomicInteger count = new AtomicInteger();
        for (Carriage carriage : this.carriages) {
            carriage.forEachPresentEntity(e -> e.getIndirectPassengers().forEach(p -> {
                if (p instanceof Player) {
                    count.incrementAndGet();
                }
            }));
        }
        return count.intValue();
    }

    public void determineHonk(Level level) {
        if (this.lowHonk != null) {
            return;
        }
        for (Carriage carriage : this.carriages) {
            Contraption contraption;
            Carriage.DimensionalCarriageEntity dimensional = carriage.getDimensionalIfPresent((ResourceKey<Level>)level.dimension());
            if (dimensional == null) {
                return;
            }
            CarriageContraptionEntity entity = (CarriageContraptionEntity)((Object)dimensional.entity.get());
            if (entity == null || !((contraption = entity.getContraption()) instanceof CarriageContraption)) break;
            CarriageContraption otherCC = (CarriageContraption)contraption;
            Pair<Boolean, Integer> first = otherCC.soundQueue.getFirstWhistle(entity);
            if (first == null) continue;
            this.lowHonk = (Boolean)first.getFirst();
            this.honkPitch = (Integer)first.getSecond();
        }
    }

    public float distanceToLocationSqr(Level level, Vec3 location) {
        float distance = Float.MAX_VALUE;
        for (Carriage carriage : this.carriages) {
            Carriage.DimensionalCarriageEntity dce = carriage.getDimensionalIfPresent((ResourceKey<Level>)level.dimension());
            if (dce == null || dce.positionAnchor == null) continue;
            distance = Math.min(distance, (float)dce.positionAnchor.distanceToSqr(location));
        }
        return distance;
    }

    public List<ResourceKey<Level>> getPresentDimensions() {
        return this.carriages.stream().flatMap(carriage -> carriage.getPresentDimensions().stream()).distinct().toList();
    }

    public Optional<BlockPos> getPositionInDimension(ResourceKey<Level> dimension) {
        return this.carriages.stream().map(carriage -> carriage.getPositionInDimension(dimension)).filter(Optional::isPresent).map(Optional::get).findFirst();
    }

    public static class Penalties {
        static final int STATION = 50;
        static final int STATION_WITH_TRAIN = 300;
        static final int MANUAL_TRAIN = 200;
        static final int IDLE_TRAIN = 700;
        static final int ARRIVING_TRAIN = 50;
        static final int WAITING_TRAIN = 50;
        static final int ANY_TRAIN = 25;
        static final int RED_SIGNAL = 25;
        static final int REDSTONE_RED_SIGNAL = 400;
    }
}
