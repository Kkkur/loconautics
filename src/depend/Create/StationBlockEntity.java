/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.WorldAttached
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.station;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.compat.computercraft.events.StationTrainPresenceEvent;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.decoration.slidingDoor.DoorControlBehaviour;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.entity.AddTrainPacket;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleItem;
import com.simibubi.create.content.trains.station.GlobalPackagePort;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.WorldAttached;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class StationBlockEntity
extends SmartBlockEntity
implements TransformableBlockEntity {
    public TrackTargetingBehaviour<GlobalStation> edgePoint;
    public DoorControlBehaviour doorControls;
    public LerpedFloat flag;
    protected int failedCarriageIndex;
    protected AssemblyException lastException;
    protected DepotBehaviour depotBehaviour;
    public AbstractComputerBehaviour computerBehaviour;
    UUID imminentTrain;
    boolean trainPresent;
    boolean trainBackwards;
    boolean trainCanDisassemble;
    boolean trainHasSchedule;
    boolean trainHasAutoSchedule;
    int flagYRot = -1;
    boolean flagFlipped;
    public Component lastDisassembledTrainName;
    public int lastDisassembledMapColorIndex;
    public static WorldAttached<Map<BlockPos, BoundingBox>> assemblyAreas = new WorldAttached(w -> new HashMap());
    Direction assemblyDirection;
    int assemblyLength;
    int[] bogeyLocations;
    AbstractBogeyBlock<?>[] bogeyTypes;
    boolean[] upsideDownBogeys;
    int bogeyCount;

    public StationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(20);
        this.lastException = null;
        this.failedCarriageIndex = -1;
        this.flag = LerpedFloat.linear().startWithValue(0.0);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.TRACK_STATION.get(), (be, context) -> be.depotBehaviour.itemHandler);
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.TRACK_STATION.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.edgePoint = new TrackTargetingBehaviour<GlobalStation>(this, EdgePointType.STATION);
        behaviours.add(this.edgePoint);
        this.doorControls = new DoorControlBehaviour(this);
        behaviours.add(this.doorControls);
        this.depotBehaviour = new DepotBehaviour(this).onlyAccepts(arg_0 -> AllItems.SCHEDULE.isIn(arg_0)).withCallback(s -> this.applyAutoSchedule());
        behaviours.add(this.depotBehaviour);
        this.depotBehaviour.addSubBehaviours(behaviours);
        this.registerAwardables(behaviours, AllAdvancements.CONTRAPTION_ACTORS, AllAdvancements.TRAIN, AllAdvancements.LONG_TRAIN, AllAdvancements.CONDUCTOR);
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        this.lastException = AssemblyException.read(tag, registries);
        this.failedCarriageIndex = tag.getInt("FailedCarriageIndex");
        super.read(tag, registries, clientPacket);
        this.invalidateRenderBoundingBox();
        if (tag.contains("ForceFlag")) {
            this.trainPresent = tag.getBoolean("ForceFlag");
        }
        if (tag.contains("PrevTrainName")) {
            this.lastDisassembledTrainName = Component.Serializer.fromJson((String)tag.getString("PrevTrainName"), (HolderLookup.Provider)registries);
        }
        this.lastDisassembledMapColorIndex = tag.getInt("PrevTrainColor");
        if (!clientPacket) {
            return;
        }
        if (!tag.contains("ImminentTrain")) {
            this.imminentTrain = null;
            this.trainPresent = false;
            this.trainCanDisassemble = false;
            this.trainBackwards = false;
            return;
        }
        this.imminentTrain = tag.getUUID("ImminentTrain");
        this.trainPresent = tag.contains("TrainPresent");
        this.trainCanDisassemble = tag.contains("TrainCanDisassemble");
        this.trainBackwards = tag.contains("TrainBackwards");
        this.trainHasSchedule = tag.contains("TrainHasSchedule");
        this.trainHasAutoSchedule = tag.contains("TrainHasAutoSchedule");
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        AssemblyException.write(tag, registries, this.lastException);
        tag.putInt("FailedCarriageIndex", this.failedCarriageIndex);
        if (this.lastDisassembledTrainName != null) {
            tag.putString("PrevTrainName", Component.Serializer.toJson((Component)this.lastDisassembledTrainName, (HolderLookup.Provider)registries));
        }
        tag.putInt("PrevTrainColor", this.lastDisassembledMapColorIndex);
        super.write(tag, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        if (this.imminentTrain == null) {
            return;
        }
        tag.putUUID("ImminentTrain", this.imminentTrain);
        if (this.trainPresent) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"TrainPresent");
        }
        if (this.trainCanDisassemble) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"TrainCanDisassemble");
        }
        if (this.trainBackwards) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"TrainBackwards");
        }
        if (this.trainHasSchedule) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"TrainHasSchedule");
        }
        if (this.trainHasAutoSchedule) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"TrainHasAutoSchedule");
        }
    }

    @Nullable
    public GlobalStation getStation() {
        return this.edgePoint.getEdgePoint();
    }

    @Override
    public void lazyTick() {
        if (this.isAssembling() && !this.level.isClientSide) {
            this.refreshAssemblyInfo();
        }
        super.lazyTick();
    }

    @Override
    public void tick() {
        boolean newlyArrived;
        if (this.isAssembling() && this.level.isClientSide) {
            this.refreshAssemblyInfo();
        }
        super.tick();
        if (this.level.isClientSide) {
            float currentTarget = this.flag.getChaseTarget();
            if (currentTarget == 0.0f || this.flag.settled()) {
                boolean target;
                boolean bl = target = this.trainPresent || this.isAssembling();
                if ((float)target != currentTarget) {
                    this.flag.chase((double)target, (double)0.1f, LerpedFloat.Chaser.LINEAR);
                    if (target) {
                        AllSoundEvents.CONTRAPTION_DISASSEMBLE.playAt(this.level, (Vec3i)this.worldPosition, 1.0f, 2.0f, true);
                    }
                }
            }
            boolean settled = this.flag.getValue() > 0.15f;
            this.flag.tickChaser();
            if (currentTarget == 0.0f && settled != this.flag.getValue() > 0.15f) {
                AllSoundEvents.CONTRAPTION_ASSEMBLE.playAt(this.level, (Vec3i)this.worldPosition, 0.75f, 1.5f, true);
            }
            return;
        }
        GlobalStation station = this.getStation();
        if (station == null) {
            return;
        }
        Train imminentTrain = station.getImminentTrain();
        boolean trainPresent = imminentTrain != null && imminentTrain.getCurrentStation() == station;
        boolean canDisassemble = trainPresent && imminentTrain.canDisassemble();
        UUID imminentID = imminentTrain != null ? imminentTrain.id : null;
        boolean trainHasSchedule = trainPresent && imminentTrain.runtime.getSchedule() != null;
        boolean trainHasAutoSchedule = trainHasSchedule && imminentTrain.runtime.isAutoSchedule;
        boolean bl = newlyArrived = this.trainPresent != trainPresent;
        if (trainPresent && imminentTrain.runtime.displayLinkUpdateRequested) {
            DisplayLinkBlock.notifyGatherers((LevelAccessor)this.level, this.worldPosition);
            imminentTrain.runtime.displayLinkUpdateRequested = false;
        }
        if (!this.level.isClientSide && this.computerBehaviour.hasAttachedComputer()) {
            if (this.imminentTrain == null && imminentTrain != null) {
                this.computerBehaviour.prepareComputerEvent(new StationTrainPresenceEvent(StationTrainPresenceEvent.Type.IMMINENT, imminentTrain));
            }
            if (newlyArrived) {
                if (trainPresent) {
                    this.computerBehaviour.prepareComputerEvent(new StationTrainPresenceEvent(StationTrainPresenceEvent.Type.ARRIVAL, imminentTrain));
                } else {
                    Train train = Create.RAILWAYS.trains.get(this.imminentTrain);
                    if (train != null) {
                        this.computerBehaviour.prepareComputerEvent(new StationTrainPresenceEvent(StationTrainPresenceEvent.Type.DEPARTURE, train));
                    }
                }
            }
        }
        if (newlyArrived) {
            this.applyAutoSchedule();
        }
        if (newlyArrived || this.trainCanDisassemble != canDisassemble || !Objects.equals(imminentID, this.imminentTrain) || this.trainHasSchedule != trainHasSchedule || this.trainHasAutoSchedule != trainHasAutoSchedule) {
            this.imminentTrain = imminentID;
            this.trainPresent = trainPresent;
            this.trainCanDisassemble = canDisassemble;
            this.trainBackwards = imminentTrain != null && imminentTrain.currentlyBackwards;
            this.trainHasSchedule = trainHasSchedule;
            this.trainHasAutoSchedule = trainHasAutoSchedule;
            this.notifyUpdate();
        }
    }

    public boolean trackClicked(Player player, InteractionHand hand, ITrackBlock track, BlockState state, BlockPos pos) {
        BlockPos targetPos;
        AbstractBogeyBlock bogey;
        Block upsideDown2;
        this.refreshAssemblyInfo();
        BoundingBox bb = (BoundingBox)((Map)assemblyAreas.get((LevelAccessor)this.level)).get(this.worldPosition);
        if (bb == null || !bb.isInside((Vec3i)pos)) {
            return false;
        }
        BlockPos up = BlockPos.containing((Position)track.getUpNormal((BlockGetter)this.level, pos, state));
        BlockPos down = BlockPos.containing((Position)track.getUpNormal((BlockGetter)this.level, pos, state).scale(-1.0));
        int bogeyOffset = pos.distManhattan((Vec3i)this.edgePoint.getGlobalPosition()) - 1;
        if (!this.isValidBogeyOffset(bogeyOffset)) {
            for (boolean upsideDown2 : Iterate.falseAndTrue) {
                for (int i = -1; i <= 1; ++i) {
                    BlockPos bogeyPos = pos.relative(this.assemblyDirection, i).offset((Vec3i)(upsideDown2 ? down : up));
                    BlockState blockState = this.level.getBlockState(bogeyPos);
                    Block block = blockState.getBlock();
                    if (!(block instanceof AbstractBogeyBlock)) continue;
                    AbstractBogeyBlock bogey2 = (AbstractBogeyBlock)block;
                    BlockEntity be = this.level.getBlockEntity(bogeyPos);
                    if (!(be instanceof AbstractBogeyBlockEntity)) continue;
                    AbstractBogeyBlockEntity oldBE = (AbstractBogeyBlockEntity)be;
                    CompoundTag oldData = oldBE.getBogeyData();
                    BlockState newBlock = bogey2.getNextSize(oldBE);
                    if (newBlock.getBlock() == bogey2) {
                        player.displayClientMessage((Component)CreateLang.translateDirect("bogey.style.no_other_sizes", new Object[0]).withStyle(ChatFormatting.RED), true);
                    }
                    this.level.setBlock(bogeyPos, newBlock, 3);
                    BlockEntity newEntity = this.level.getBlockEntity(bogeyPos);
                    if (!(newEntity instanceof AbstractBogeyBlockEntity)) continue;
                    AbstractBogeyBlockEntity newBE = (AbstractBogeyBlockEntity)newEntity;
                    newBE.setBogeyData(oldData);
                    IWrenchable.playRotateSound(this.level, bogeyPos);
                    return true;
                }
            }
            return false;
        }
        ItemStack handItem = player.getItemInHand(hand);
        if (!player.isCreative() && !AllBlocks.RAILWAY_CASING.isIn(handItem)) {
            player.displayClientMessage((Component)CreateLang.translateDirect("train_assembly.requires_casing", new Object[0]), true);
            return false;
        }
        boolean upsideDown3 = player.getViewXRot(1.0f) < 0.0f && (upsideDown2 = track.getBogeyAnchor((BlockGetter)this.level, pos, state).getBlock()) instanceof AbstractBogeyBlock && (bogey = (AbstractBogeyBlock)upsideDown2).canBeUpsideDown();
        BlockPos blockPos = targetPos = upsideDown3 ? pos.offset((Vec3i)down) : pos.offset((Vec3i)up);
        if (this.level.getBlockState(targetPos).getDestroySpeed((BlockGetter)this.level, targetPos) == -1.0f) {
            return false;
        }
        this.level.destroyBlock(targetPos, true);
        BlockState bogeyAnchor = track.getBogeyAnchor((BlockGetter)this.level, pos, state);
        Block bogeyPos = bogeyAnchor.getBlock();
        if (bogeyPos instanceof AbstractBogeyBlock) {
            AbstractBogeyBlock bogey3 = (AbstractBogeyBlock)bogeyPos;
            bogeyAnchor = bogey3.getVersion(bogeyAnchor, upsideDown3);
        }
        bogeyAnchor = ProperWaterloggedBlock.withWater((LevelAccessor)this.level, bogeyAnchor, pos);
        this.level.setBlock(targetPos, bogeyAnchor, 3);
        player.displayClientMessage((Component)CreateLang.translateDirect("train_assembly.bogey_created", new Object[0]), true);
        SoundType soundtype = bogeyAnchor.getBlock().getSoundType(state, (LevelReader)this.level, pos, (Entity)player);
        this.level.playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
        if (!player.isCreative()) {
            ItemStack itemInHand = player.getItemInHand(hand);
            itemInHand.shrink(1);
            if (itemInHand.isEmpty()) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
        }
        return true;
    }

    public boolean enterAssemblyMode(@Nullable ServerPlayer sender) {
        if (this.isAssembling()) {
            return false;
        }
        this.tryDisassembleTrain(sender);
        if (!this.tryEnterAssemblyMode()) {
            return false;
        }
        if (!(this.level.getBlockState(this.worldPosition).getBlock() instanceof StationBlock)) {
            return true;
        }
        BlockState newState = (BlockState)this.getBlockState().setValue((Property)StationBlock.ASSEMBLING, (Comparable)Boolean.valueOf(true));
        this.level.setBlock(this.getBlockPos(), newState, 3);
        this.refreshBlockState();
        this.refreshAssemblyInfo();
        this.updateStationState(station -> {
            station.assembling = true;
        });
        GlobalStation station2 = this.getStation();
        if (station2 != null) {
            for (Train train : Create.RAILWAYS.sided((LevelAccessor)this.level).trains.values()) {
                if (train.navigation.destination != station2) continue;
                DiscoveredPath preferredPath = train.runtime.startCurrentInstruction(this.level);
                train.navigation.startNavigation(preferredPath != null ? preferredPath : train.navigation.findPathTo(station2, Double.MAX_VALUE));
            }
        }
        return true;
    }

    public boolean exitAssemblyMode() {
        if (!this.isAssembling()) {
            return false;
        }
        this.cancelAssembly();
        BlockState newState = (BlockState)this.getBlockState().setValue((Property)StationBlock.ASSEMBLING, (Comparable)Boolean.valueOf(false));
        this.level.setBlock(this.getBlockPos(), newState, 3);
        this.refreshBlockState();
        return this.updateStationState(station -> {
            station.assembling = false;
        });
    }

    public boolean tryDisassembleTrain(@Nullable ServerPlayer sender) {
        GlobalStation station = this.getStation();
        if (station == null) {
            return false;
        }
        Train train = station.getPresentTrain();
        if (train == null) {
            return false;
        }
        BlockPos trackPosition = this.edgePoint.getGlobalPosition();
        if (!train.disassemble(this.getAssemblyDirection(), trackPosition.above())) {
            return false;
        }
        this.dropSchedule(sender, train);
        return true;
    }

    public boolean isAssembling() {
        BlockState state = this.getBlockState();
        return state.hasProperty((Property)StationBlock.ASSEMBLING) && (Boolean)state.getValue((Property)StationBlock.ASSEMBLING) != false;
    }

    public boolean tryEnterAssemblyMode() {
        if (!this.edgePoint.hasValidTrack()) {
            return false;
        }
        BlockPos targetPosition = this.edgePoint.getGlobalPosition();
        BlockState trackState = this.edgePoint.getTrackBlockState();
        ITrackBlock track = this.edgePoint.getTrack();
        Vec3 trackAxis = track.getTrackAxes((BlockGetter)this.level, targetPosition, trackState).get(0);
        boolean axisFound = false;
        for (Direction.Axis axis : Iterate.axes) {
            if (trackAxis.get(axis) == 0.0) continue;
            if (axisFound) {
                return false;
            }
            axisFound = true;
        }
        return true;
    }

    public void dropSchedule(@Nullable ServerPlayer sender, @Nullable Train train) {
        GlobalStation station = this.getStation();
        if (station == null) {
            return;
        }
        if (train == null) {
            return;
        }
        ItemStack schedule = train.runtime.returnSchedule((HolderLookup.Provider)this.level.registryAccess());
        if (schedule.isEmpty()) {
            return;
        }
        if (sender != null && sender.getMainHandItem().isEmpty()) {
            sender.getInventory().placeItemBackInInventory(schedule);
            return;
        }
        Vec3 v = VecHelper.getCenterOf((Vec3i)this.getBlockPos());
        ItemEntity itemEntity = new ItemEntity(this.getLevel(), v.x, v.y, v.z, schedule);
        itemEntity.setDeltaMovement(Vec3.ZERO);
        this.getLevel().addFreshEntity((Entity)itemEntity);
    }

    public void updateMapColor(int color) {
        GlobalStation station = this.getStation();
        if (station == null) {
            return;
        }
        Train train = station.getPresentTrain();
        if (train == null) {
            return;
        }
        train.mapColorIndex = color;
    }

    private boolean updateStationState(Consumer<GlobalStation> updateState) {
        GlobalStation station = this.getStation();
        TrackGraphLocation graphLocation = this.edgePoint.determineGraphLocation();
        if (station == null || graphLocation == null) {
            return false;
        }
        updateState.accept(station);
        Create.RAILWAYS.sync.pointAdded(graphLocation.graph, station);
        Create.RAILWAYS.markTracksDirty();
        return true;
    }

    public void refreshAssemblyInfo() {
        GlobalStation station;
        if (!this.edgePoint.hasValidTrack()) {
            return;
        }
        if (!(this.isVirtual() || (station = this.getStation()) != null && station.getPresentTrain() == null)) {
            return;
        }
        int prevLength = this.assemblyLength;
        BlockPos targetPosition = this.edgePoint.getGlobalPosition();
        BlockState trackState = this.edgePoint.getTrackBlockState();
        ITrackBlock track = this.edgePoint.getTrack();
        this.getAssemblyDirection();
        BlockPos.MutableBlockPos currentPos = targetPosition.mutable();
        currentPos.move(this.assemblyDirection);
        BlockPos bogeyOffset = BlockPos.containing((Position)track.getUpNormal((BlockGetter)this.level, targetPosition, trackState));
        int maxLength = (Integer)AllConfigs.server().trains.maxAssemblyLength.get();
        int maxBogeyCount = (Integer)AllConfigs.server().trains.maxBogeyCount.get();
        int bogeyIndex = 0;
        if (this.bogeyLocations == null) {
            this.bogeyLocations = new int[maxBogeyCount];
        }
        if (this.bogeyTypes == null) {
            this.bogeyTypes = new AbstractBogeyBlock[maxBogeyCount];
        }
        if (this.upsideDownBogeys == null) {
            this.upsideDownBogeys = new boolean[maxBogeyCount];
        }
        Arrays.fill(this.bogeyLocations, -1);
        Arrays.fill(this.bogeyTypes, null);
        Arrays.fill(this.upsideDownBogeys, false);
        for (int i = 0; i < maxLength; ++i) {
            if (i == maxLength - 1) {
                this.assemblyLength = i;
                break;
            }
            if (!track.trackEquals(trackState, this.level.getBlockState((BlockPos)currentPos))) {
                this.assemblyLength = Math.max(0, i - 1);
                break;
            }
            BlockState potentialBogeyState = this.level.getBlockState(bogeyOffset.offset((Vec3i)currentPos));
            BlockPos upsideDownBogeyOffset = new BlockPos(bogeyOffset.getX(), bogeyOffset.getY() * -1, bogeyOffset.getZ());
            if (bogeyIndex < this.bogeyLocations.length) {
                AbstractBogeyBlock bogey;
                Block block = potentialBogeyState.getBlock();
                if (block instanceof AbstractBogeyBlock && !(bogey = (AbstractBogeyBlock)block).isUpsideDown(potentialBogeyState)) {
                    this.bogeyTypes[bogeyIndex] = bogey;
                    this.bogeyLocations[bogeyIndex] = i;
                    this.upsideDownBogeys[bogeyIndex] = false;
                    ++bogeyIndex;
                } else {
                    AbstractBogeyBlock bogey2;
                    potentialBogeyState = this.level.getBlockState(upsideDownBogeyOffset.offset((Vec3i)currentPos));
                    block = potentialBogeyState.getBlock();
                    if (block instanceof AbstractBogeyBlock && (bogey2 = (AbstractBogeyBlock)block).isUpsideDown(potentialBogeyState)) {
                        this.bogeyTypes[bogeyIndex] = bogey2;
                        this.bogeyLocations[bogeyIndex] = i;
                        this.upsideDownBogeys[bogeyIndex] = true;
                        ++bogeyIndex;
                    }
                }
            }
            currentPos.move(this.assemblyDirection);
        }
        this.bogeyCount = bogeyIndex;
        if (this.level.isClientSide) {
            return;
        }
        if (prevLength == this.assemblyLength) {
            return;
        }
        if (this.isVirtual()) {
            return;
        }
        Map map = (Map)assemblyAreas.get((LevelAccessor)this.level);
        BlockPos startPosition = targetPosition.relative(this.assemblyDirection);
        BlockPos trackEnd = startPosition.relative(this.assemblyDirection, this.assemblyLength - 1);
        map.put(this.worldPosition, BoundingBox.fromCorners((Vec3i)startPosition, (Vec3i)trackEnd));
    }

    public boolean updateName(String name) {
        if (!this.updateStationState(station -> {
            station.name = name;
        })) {
            return false;
        }
        this.notifyUpdate();
        return true;
    }

    public boolean isValidBogeyOffset(int i) {
        if ((i < 3 || this.bogeyCount == 0) && i != 0) {
            return false;
        }
        for (int j : this.bogeyLocations) {
            if (j == -1) break;
            if (i < j - 2 || i > j + 2) continue;
            return false;
        }
        return true;
    }

    public Direction getAssemblyDirection() {
        if (this.assemblyDirection != null) {
            return this.assemblyDirection;
        }
        if (!this.edgePoint.hasValidTrack()) {
            return null;
        }
        BlockPos targetPosition = this.edgePoint.getGlobalPosition();
        BlockState trackState = this.edgePoint.getTrackBlockState();
        ITrackBlock track = this.edgePoint.getTrack();
        Direction.AxisDirection axisDirection = this.edgePoint.getTargetDirection();
        Vec3 axis = track.getTrackAxes((BlockGetter)this.level, targetPosition, trackState).get(0).normalize().scale((double)axisDirection.getStep());
        this.assemblyDirection = Direction.getNearest((double)axis.x, (double)axis.y, (double)axis.z);
        return this.assemblyDirection;
    }

    @Override
    public void remove() {
        ((Map)assemblyAreas.get((LevelAccessor)this.level)).remove(this.worldPosition);
        super.remove();
    }

    public void assemble(UUID playerUUID) {
        int pointIndex;
        int loc;
        this.refreshAssemblyInfo();
        if (this.bogeyLocations == null) {
            return;
        }
        if (this.bogeyLocations[0] != 0) {
            this.exception(new AssemblyException((Component)CreateLang.translateDirect("train_assembly.frontmost_bogey_at_station", new Object[0])), -1);
            return;
        }
        if (!this.edgePoint.hasValidTrack()) {
            return;
        }
        BlockPos trackPosition = this.edgePoint.getGlobalPosition();
        BlockState trackState = this.edgePoint.getTrackBlockState();
        ITrackBlock track = this.edgePoint.getTrack();
        BlockPos bogeyOffset = BlockPos.containing((Position)track.getUpNormal((BlockGetter)this.level, trackPosition, trackState));
        TrackNodeLocation location = null;
        Vec3 center = Vec3.atBottomCenterOf((Vec3i)trackPosition).add(0.0, track.getElevationAtCenter((BlockGetter)this.level, trackPosition, trackState), 0.0);
        Collection<TrackNodeLocation.DiscoveredLocation> ends = track.getConnected((BlockGetter)this.level, trackPosition, trackState, true, null);
        Vec3 targetOffset = Vec3.atLowerCornerOf((Vec3i)this.assemblyDirection.getNormal());
        for (TrackNodeLocation.DiscoveredLocation end : ends) {
            if (!Mth.equal((double)0.0, (double)targetOffset.distanceToSqr(end.getLocation().subtract(center).normalize()))) continue;
            location = end;
        }
        if (location == null) {
            return;
        }
        ArrayList<Double> pointOffsets = new ArrayList<Double>();
        int iPrevious = -100;
        for (int i = 0; i < this.bogeyLocations.length && (loc = this.bogeyLocations[i]) != -1; ++i) {
            if (loc - iPrevious < 3) {
                this.exception(new AssemblyException((Component)CreateLang.translateDirect("train_assembly.bogeys_too_close", i, i + 1)), -1);
                return;
            }
            double bogeySize = this.bogeyTypes[i].getWheelPointSpacing();
            pointOffsets.add((double)loc + 0.5 - bogeySize / 2.0);
            pointOffsets.add((double)loc + 0.5 + bogeySize / 2.0);
            iPrevious = loc;
        }
        ArrayList<TravellingPoint> points = new ArrayList<TravellingPoint>();
        Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)this.assemblyDirection.getNormal());
        TrackGraph graph = null;
        TrackNode secondNode = null;
        for (int j = 0; j < this.assemblyLength * 2 + 40; ++j) {
            double offset;
            TrackNode node;
            double i = (double)j / 2.0;
            if (points.size() == pointOffsets.size()) break;
            TrackNodeLocation.DiscoveredLocation currentLocation = location;
            location = new TrackNodeLocation(location.getLocation().add(directionVec.scale(0.5))).in(location.dimension);
            if (graph == null) {
                graph = Create.RAILWAYS.getGraph((LevelAccessor)this.level, currentLocation);
            }
            if (graph == null || (node = graph.locateNode(currentLocation)) == null) continue;
            for (pointIndex = points.size(); pointIndex < pointOffsets.size() && !((offset = ((Double)pointOffsets.get(pointIndex)).doubleValue()) > i); ++pointIndex) {
                double positionOnEdge = i - offset;
                Map<TrackNode, TrackEdge> connectionsFromNode = graph.getConnectionsFrom(node);
                if (secondNode == null) {
                    for (Map.Entry<TrackNode, TrackEdge> entry : connectionsFromNode.entrySet()) {
                        Vec3 edgeDirection;
                        TrackEdge edge = entry.getValue();
                        TrackNode otherNode = entry.getKey();
                        if (edge.isTurn() || !Mth.equal((double)(edgeDirection = edge.getDirection(true)).normalize().dot(directionVec), (double)-1.0)) continue;
                        secondNode = otherNode;
                    }
                }
                if (secondNode == null) {
                    Create.LOGGER.warn("Cannot assemble: No valid starting node found");
                    return;
                }
                TrackEdge edge = connectionsFromNode.get(secondNode);
                if (edge == null) {
                    Create.LOGGER.warn("Cannot assemble: Missing graph edge");
                    return;
                }
                points.add(new TravellingPoint(node, secondNode, edge, positionOnEdge, false));
            }
            secondNode = node;
        }
        if (points.size() != pointOffsets.size()) {
            Create.LOGGER.warn("Cannot assemble: Not all Points created");
            return;
        }
        if (points.size() == 0) {
            this.exception(new AssemblyException((Component)CreateLang.translateDirect("train_assembly.no_bogeys", new Object[0])), -1);
            return;
        }
        ArrayList<CarriageContraption> contraptions = new ArrayList<CarriageContraption>();
        ArrayList<Carriage> carriages = new ArrayList<Carriage>();
        ArrayList<Integer> spacing = new ArrayList<Integer>();
        boolean atLeastOneForwardControls = false;
        for (int bogeyIndex = 0; bogeyIndex < this.bogeyCount; ++bogeyIndex) {
            pointIndex = bogeyIndex * 2;
            if (bogeyIndex > 0) {
                spacing.add(this.bogeyLocations[bogeyIndex] - this.bogeyLocations[bogeyIndex - 1]);
            }
            CarriageContraption contraption = new CarriageContraption(this.assemblyDirection);
            BlockPos bogeyPosOffset = trackPosition.offset((Vec3i)bogeyOffset);
            BlockPos upsideDownBogeyPosOffset = trackPosition.offset((Vec3i)new BlockPos(bogeyOffset.getX(), bogeyOffset.getY() * -1, bogeyOffset.getZ()));
            try {
                int offset = this.bogeyLocations[bogeyIndex] + 1;
                boolean success = contraption.assemble(this.level, this.upsideDownBogeys[bogeyIndex] ? upsideDownBogeyPosOffset.relative(this.assemblyDirection, offset) : bogeyPosOffset.relative(this.assemblyDirection, offset));
                atLeastOneForwardControls |= contraption.hasForwardControls();
                contraption.setSoundQueueOffset(offset);
                if (!success) {
                    this.exception(new AssemblyException((Component)CreateLang.translateDirect("train_assembly.nothing_attached", bogeyIndex + 1)), -1);
                    return;
                }
            }
            catch (AssemblyException e) {
                this.exception(e, contraptions.size() + 1);
                return;
            }
            AbstractBogeyBlock<?> typeOfFirstBogey = this.bogeyTypes[bogeyIndex];
            boolean firstBogeyIsUpsideDown = this.upsideDownBogeys[bogeyIndex];
            BlockPos firstBogeyPos = contraption.anchor;
            AbstractBogeyBlockEntity firstBogeyBlockEntity = (AbstractBogeyBlockEntity)this.level.getBlockEntity(firstBogeyPos);
            CarriageBogey firstBogey = new CarriageBogey(typeOfFirstBogey, firstBogeyIsUpsideDown, firstBogeyBlockEntity.getBogeyData(), (TravellingPoint)points.get(pointIndex), (TravellingPoint)points.get(pointIndex + 1));
            CarriageBogey secondBogey = null;
            BlockPos secondBogeyPos = contraption.getSecondBogeyPos();
            int bogeySpacing = 0;
            if (secondBogeyPos != null) {
                if (bogeyIndex == this.bogeyCount - 1 || !secondBogeyPos.equals((Object)(this.upsideDownBogeys[bogeyIndex + 1] ? upsideDownBogeyPosOffset : bogeyPosOffset).relative(this.assemblyDirection, this.bogeyLocations[bogeyIndex + 1] + 1))) {
                    this.exception(new AssemblyException((Component)CreateLang.translateDirect("train_assembly.not_connected_in_order", new Object[0])), contraptions.size() + 1);
                    return;
                }
                AbstractBogeyBlockEntity secondBogeyBlockEntity = (AbstractBogeyBlockEntity)this.level.getBlockEntity(secondBogeyPos);
                bogeySpacing = this.bogeyLocations[bogeyIndex + 1] - this.bogeyLocations[bogeyIndex];
                secondBogey = new CarriageBogey(this.bogeyTypes[bogeyIndex + 1], this.upsideDownBogeys[bogeyIndex + 1], secondBogeyBlockEntity.getBogeyData(), (TravellingPoint)points.get(pointIndex + 2), (TravellingPoint)points.get(pointIndex + 3));
                ++bogeyIndex;
            } else if (!typeOfFirstBogey.allowsSingleBogeyCarriage()) {
                this.exception(new AssemblyException((Component)CreateLang.translateDirect("train_assembly.single_bogey_carriage", new Object[0])), contraptions.size() + 1);
                return;
            }
            contraptions.add(contraption);
            carriages.add(new Carriage(firstBogey, secondBogey, bogeySpacing));
        }
        if (!atLeastOneForwardControls) {
            this.exception(new AssemblyException((Component)CreateLang.translateDirect("train_assembly.no_controls", new Object[0])), -1);
            return;
        }
        for (CarriageContraption contraption : contraptions) {
            contraption.removeBlocksFromWorld(this.level, BlockPos.ZERO);
            contraption.expandBoundsAroundAxis(Direction.Axis.Y);
        }
        Train train = new Train(UUID.randomUUID(), playerUUID, graph, carriages, spacing, contraptions.stream().anyMatch(CarriageContraption::hasBackwardControls), 0);
        if (this.lastDisassembledTrainName != null) {
            train.name = this.lastDisassembledTrainName;
            train.mapColorIndex = this.lastDisassembledMapColorIndex;
            this.lastDisassembledTrainName = null;
            this.lastDisassembledMapColorIndex = 0;
        }
        for (int i = 0; i < contraptions.size(); ++i) {
            CarriageContraption contraption = (CarriageContraption)contraptions.get(i);
            Carriage carriage = (Carriage)carriages.get(i);
            carriage.setContraption(this.level, contraption);
            if (!contraption.containsBlockBreakers()) continue;
            this.award(AllAdvancements.CONTRAPTION_ACTORS);
        }
        GlobalStation station = this.getStation();
        if (station != null) {
            train.setCurrentStation(station);
            station.reserveFor(train);
        }
        train.collectInitiallyOccupiedSignalBlocks();
        Create.RAILWAYS.addTrain(train);
        CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new AddTrainPacket(train));
        this.clearException();
        this.award(AllAdvancements.TRAIN);
        if (contraptions.size() >= 6) {
            this.award(AllAdvancements.LONG_TRAIN);
        }
    }

    public void cancelAssembly() {
        this.assemblyLength = 0;
        ((Map)assemblyAreas.get((LevelAccessor)this.level)).remove(this.worldPosition);
        this.clearException();
    }

    private void clearException() {
        this.exception(null, -1);
    }

    private void exception(AssemblyException exception, int carriage) {
        this.failedCarriageIndex = carriage;
        this.lastException = exception;
        this.sendData();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        if (this.isAssembling()) {
            return AABB.INFINITE;
        }
        return super.getRenderBoundingBox();
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(Vec3.atLowerCornerOf((Vec3i)this.worldPosition), Vec3.atLowerCornerOf((Vec3i)this.edgePoint.getGlobalPosition())).inflate(2.0);
    }

    public ItemStack getAutoSchedule() {
        return this.depotBehaviour.getHeldItemStack();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }

    private void applyAutoSchedule() {
        ItemStack stack = this.getAutoSchedule();
        if (!AllItems.SCHEDULE.isIn(stack)) {
            return;
        }
        Schedule schedule = ScheduleItem.getSchedule((HolderLookup.Provider)this.level.registryAccess(), stack);
        if (schedule == null || schedule.entries.isEmpty()) {
            return;
        }
        GlobalStation station = this.getStation();
        if (station == null) {
            return;
        }
        Train imminentTrain = station.getImminentTrain();
        if (imminentTrain == null || imminentTrain.getCurrentStation() != station) {
            return;
        }
        this.award(AllAdvancements.CONDUCTOR);
        imminentTrain.runtime.setSchedule(schedule, true);
        AllSoundEvents.CONFIRM.playOnServer(this.level, (Vec3i)this.worldPosition, 1.0f, 1.0f);
        Level level = this.level;
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)level;
        Vec3 v = Vec3.atBottomCenterOf((Vec3i)this.worldPosition.above());
        server.sendParticles((ParticleOptions)ParticleTypes.HAPPY_VILLAGER, v.x, v.y, v.z, 8, 0.35, 0.05, 0.35, 1.0);
        server.sendParticles((ParticleOptions)ParticleTypes.END_ROD, v.x, v.y + 0.25, v.z, 10, 0.05, 1.0, 0.05, (double)0.005f);
    }

    public boolean resolveFlagAngle() {
        if (this.flagYRot != -1) {
            return true;
        }
        BlockState target = this.edgePoint.getTrackBlockState();
        Block block = target.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return false;
        }
        ITrackBlock def = (ITrackBlock)block;
        Vec3 axis = null;
        BlockPos trackPos = this.edgePoint.getGlobalPosition();
        for (Vec3 vec3 : def.getTrackAxes((BlockGetter)this.level, trackPos, target)) {
            axis = vec3.scale((double)this.edgePoint.getTargetDirection().getStep());
        }
        if (axis == null) {
            return false;
        }
        Direction nearest = Direction.getNearest((double)axis.x, (double)0.0, (double)axis.z);
        this.flagYRot = (int)(-nearest.toYRot() - 90.0f);
        Vec3 diff = Vec3.atLowerCornerOf((Vec3i)trackPos.subtract((Vec3i)this.worldPosition)).multiply(1.0, 0.0, 1.0);
        if (diff.lengthSqr() == 0.0) {
            return true;
        }
        this.flagFlipped = diff.dot(Vec3.atLowerCornerOf((Vec3i)nearest.getClockWise().getNormal())) > 0.0;
        return true;
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        this.edgePoint.transform(be, transform);
    }

    public void attachPackagePort(PackagePortBlockEntity ppbe) {
        GlobalPackagePort globalPackagePort;
        GlobalStation station = this.getStation();
        if (station == null || this.level.isClientSide) {
            return;
        }
        if (ppbe instanceof PostboxBlockEntity) {
            PostboxBlockEntity pbe = (PostboxBlockEntity)ppbe;
            pbe.trackedGlobalStation = new WeakReference<GlobalStation>(station);
        }
        if ((globalPackagePort = station.connectedPorts.get(ppbe.getBlockPos())) == null) {
            globalPackagePort = new GlobalPackagePort();
            globalPackagePort.address = ppbe.addressFilter;
            station.connectedPorts.put(ppbe.getBlockPos(), globalPackagePort);
        } else {
            globalPackagePort.restoreOfflineBuffer(ppbe.inventory);
        }
    }

    public void removePackagePort(PackagePortBlockEntity ppbe) {
        GlobalStation station = this.getStation();
        if (station == null) {
            return;
        }
        station.connectedPorts.remove(ppbe.getBlockPos());
    }
}
