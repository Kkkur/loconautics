/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.codecs.CatnipCodecUtils
 *  net.createmod.catnip.codecs.CatnipCodecs
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ItemStackLinkedSet
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.common.Tags$Items
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Math
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionHandler;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelEffectPacket;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelScreen;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSetItemMenu;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSlotPositioning;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedClientHandler;
import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import com.simibubi.create.content.logistics.packagerLink.RequestPromise;
import com.simibubi.create.content.logistics.packagerLink.RequestPromiseQueue;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.codecs.CatnipCodecs;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

public class FactoryPanelBehaviour
extends FilteringBehaviour
implements MenuProvider {
    public static final BehaviourType<FactoryPanelBehaviour> TOP_LEFT = new BehaviourType();
    public static final BehaviourType<FactoryPanelBehaviour> TOP_RIGHT = new BehaviourType();
    public static final BehaviourType<FactoryPanelBehaviour> BOTTOM_LEFT = new BehaviourType();
    public static final BehaviourType<FactoryPanelBehaviour> BOTTOM_RIGHT = new BehaviourType();
    public Map<FactoryPanelPosition, FactoryPanelConnection> targetedBy;
    public Map<BlockPos, FactoryPanelConnection> targetedByLinks;
    public Set<FactoryPanelPosition> targeting;
    public List<ItemStack> activeCraftingArrangement;
    public boolean satisfied;
    public boolean promisedSatisfied;
    public boolean waitingForNetwork;
    public String recipeAddress;
    public int recipeOutput;
    public LerpedFloat bulb;
    public FactoryPanelBlock.PanelSlot slot;
    public int promiseClearingInterval;
    public boolean forceClearPromises;
    public UUID network;
    public boolean active;
    public boolean redstonePowered;
    public RequestPromiseQueue restockerPromises;
    private boolean promisePrimedForMarkDirty;
    private int lastReportedUnloadedLinks;
    private int lastReportedLevelInStorage;
    private int lastReportedPromises;
    private int timer;

    public FactoryPanelBehaviour(FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(be, new FactoryPanelSlotPositioning(slot));
        this.slot = slot;
        this.targetedBy = new HashMap<FactoryPanelPosition, FactoryPanelConnection>();
        this.targetedByLinks = new HashMap<BlockPos, FactoryPanelConnection>();
        this.targeting = new HashSet<FactoryPanelPosition>();
        this.count = 0;
        this.satisfied = false;
        this.promisedSatisfied = false;
        this.waitingForNetwork = false;
        this.activeCraftingArrangement = List.of();
        this.recipeAddress = "";
        this.recipeOutput = 1;
        this.active = false;
        this.forceClearPromises = false;
        this.redstonePowered = false;
        this.promiseClearingInterval = -1;
        this.bulb = LerpedFloat.linear().startWithValue(0.0).chase(0.0, 0.175, LerpedFloat.Chaser.EXP);
        this.restockerPromises = new RequestPromiseQueue(() -> ((FactoryPanelBlockEntity)be).setChanged());
        this.promisePrimedForMarkDirty = true;
        this.network = UUID.randomUUID();
        this.setLazyTickRate(40);
    }

    public void setNetwork(UUID network) {
        this.network = network;
    }

    @Nullable
    public static FactoryPanelBehaviour at(BlockAndTintGetter world, FactoryPanelConnection connection) {
        Object cached = connection.cachedSource.get();
        if (cached instanceof FactoryPanelBehaviour) {
            FactoryPanelBehaviour fbe = (FactoryPanelBehaviour)cached;
            if (!fbe.blockEntity.isRemoved()) {
                return fbe;
            }
        }
        FactoryPanelBehaviour result = FactoryPanelBehaviour.at(world, connection.from);
        connection.cachedSource = new WeakReference<FactoryPanelBehaviour>(result);
        return result;
    }

    @Nullable
    public static FactoryPanelBehaviour at(BlockAndTintGetter world, FactoryPanelPosition pos) {
        Level l;
        if (world instanceof Level && !(l = (Level)world).isLoaded(pos.pos())) {
            return null;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos.pos());
        if (!(blockEntity instanceof FactoryPanelBlockEntity)) {
            return null;
        }
        FactoryPanelBlockEntity fpbe = (FactoryPanelBlockEntity)blockEntity;
        FactoryPanelBehaviour behaviour = fpbe.panels.get((Object)pos.slot());
        if (!behaviour.active) {
            return null;
        }
        return behaviour;
    }

    @Nullable
    public static FactoryPanelSupportBehaviour linkAt(BlockAndTintGetter world, FactoryPanelConnection connection) {
        Object cached = connection.cachedSource.get();
        if (cached instanceof FactoryPanelSupportBehaviour) {
            FactoryPanelSupportBehaviour fpsb = (FactoryPanelSupportBehaviour)cached;
            if (!fpsb.blockEntity.isRemoved()) {
                return fpsb;
            }
        }
        FactoryPanelSupportBehaviour result = FactoryPanelBehaviour.linkAt(world, connection.from);
        connection.cachedSource = new WeakReference<FactoryPanelSupportBehaviour>(result);
        return result;
    }

    @Nullable
    public static FactoryPanelSupportBehaviour linkAt(BlockAndTintGetter world, FactoryPanelPosition pos) {
        Level l;
        if (world instanceof Level && !(l = (Level)world).isLoaded(pos.pos())) {
            return null;
        }
        return BlockEntityBehaviour.get((BlockGetter)world, pos.pos(), FactoryPanelSupportBehaviour.TYPE);
    }

    public void moveTo(FactoryPanelPosition newPos, ServerPlayer player) {
        BlockEntityBehaviour at;
        FactoryPanelBlockEntity fpbe;
        Level level = this.getWorld();
        BlockState existingState = level.getBlockState(newPos.pos());
        if (FactoryPanelBehaviour.at((BlockAndTintGetter)level, newPos) != null) {
            return;
        }
        boolean isAddedToOtherGauge = AllBlocks.FACTORY_GAUGE.has(existingState);
        if (!existingState.isAir() && !isAddedToOtherGauge) {
            return;
        }
        if (isAddedToOtherGauge && existingState != this.blockEntity.getBlockState()) {
            return;
        }
        if (!isAddedToOtherGauge) {
            level.setBlock(newPos.pos(), this.blockEntity.getBlockState(), 3);
        }
        for (BlockPos blockPos : this.targetedByLinks.keySet()) {
            if (blockPos.closerThan((Vec3i)newPos.pos(), 24.0)) continue;
            return;
        }
        for (FactoryPanelPosition factoryPanelPosition : this.targetedBy.keySet()) {
            if (factoryPanelPosition.pos().closerThan((Vec3i)newPos.pos(), 24.0)) continue;
            return;
        }
        for (FactoryPanelPosition factoryPanelPosition : this.targeting) {
            if (factoryPanelPosition.pos().closerThan((Vec3i)newPos.pos(), 24.0)) continue;
            return;
        }
        for (BlockPos blockPos : this.targetedByLinks.keySet()) {
            FactoryPanelSupportBehaviour at2 = FactoryPanelBehaviour.linkAt((BlockAndTintGetter)level, new FactoryPanelPosition(blockPos, this.slot));
            if (at2 == null) continue;
            at2.disconnect(this);
        }
        SmartBlockEntity oldBE = this.blockEntity;
        FactoryPanelPosition factoryPanelPosition = this.getPanelPosition();
        this.moveToSlot(newPos.slot());
        BlockEntity blockEntity = level.getBlockEntity(newPos.pos());
        if (blockEntity instanceof FactoryPanelBlockEntity) {
            fpbe = (FactoryPanelBlockEntity)blockEntity;
            fpbe.attachBehaviourLate(this);
            fpbe.panels.put(this.slot, this);
            fpbe.redraw = true;
            fpbe.lastShape = null;
            fpbe.notifyUpdate();
        }
        if (oldBE instanceof FactoryPanelBlockEntity) {
            fpbe = (FactoryPanelBlockEntity)oldBE;
            FactoryPanelBehaviour newBehaviour = new FactoryPanelBehaviour(fpbe, factoryPanelPosition.slot());
            fpbe.attachBehaviourLate(newBehaviour);
            fpbe.panels.put(factoryPanelPosition.slot(), newBehaviour);
            fpbe.redraw = true;
            fpbe.lastShape = null;
            fpbe.notifyUpdate();
        }
        for (FactoryPanelPosition position : this.targeting) {
            at = FactoryPanelBehaviour.at((BlockAndTintGetter)level, position);
            if (at == null) continue;
            FactoryPanelConnection connection = ((FactoryPanelBehaviour)at).targetedBy.remove(factoryPanelPosition);
            connection.from = newPos;
            ((FactoryPanelBehaviour)at).targetedBy.put(newPos, connection);
            ((FactoryPanelBehaviour)at).blockEntity.sendData();
        }
        for (FactoryPanelPosition position : this.targetedBy.keySet()) {
            at = FactoryPanelBehaviour.at((BlockAndTintGetter)level, position);
            if (at == null) continue;
            ((FactoryPanelBehaviour)at).targeting.remove(factoryPanelPosition);
            ((FactoryPanelBehaviour)at).targeting.add(newPos);
        }
        for (BlockPos pos : this.targetedByLinks.keySet()) {
            at = FactoryPanelBehaviour.linkAt((BlockAndTintGetter)level, new FactoryPanelPosition(pos, this.slot));
            if (at == null) continue;
            ((FactoryPanelSupportBehaviour)at).connect(this);
        }
        player.displayClientMessage((Component)CreateLang.translate("factory_panel.relocated", new Object[0]).style(ChatFormatting.GREEN).component(), true);
        player.level().playSound(null, newPos.pos(), SoundEvents.COPPER_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    private void moveToSlot(FactoryPanelBlock.PanelSlot slot) {
        this.slot = slot;
        ValueBoxTransform valueBoxTransform = this.getSlotPositioning();
        if (valueBoxTransform instanceof FactoryPanelSlotPositioning) {
            FactoryPanelSlotPositioning fpsp = (FactoryPanelSlotPositioning)valueBoxTransform;
            fpsp.slot = slot;
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        this.notifyRedstoneOutputs();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClientSide()) {
            if (this.blockEntity.isVirtual()) {
                this.tickStorageMonitor();
            }
            this.bulb.updateChaseTarget(this.redstonePowered || this.satisfied ? 1.0f : 0.0f);
            this.bulb.tickChaser();
            if (this.active) {
                this.tickOutline();
            }
            return;
        }
        if (!this.promisePrimedForMarkDirty) {
            this.restockerPromises.setOnChanged(() -> ((SmartBlockEntity)this.blockEntity).setChanged());
            this.promisePrimedForMarkDirty = true;
        }
        this.tickStorageMonitor();
        this.tickRequests();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.getWorld().isClientSide()) {
            return;
        }
        this.checkForRedstoneInput();
    }

    public void checkForRedstoneInput() {
        if (!this.active) {
            return;
        }
        boolean shouldPower = false;
        for (FactoryPanelConnection connection : this.targetedByLinks.values()) {
            if (!this.getWorld().isLoaded(connection.from.pos())) {
                return;
            }
            FactoryPanelSupportBehaviour linkAt = FactoryPanelBehaviour.linkAt((BlockAndTintGetter)this.getWorld(), connection);
            if (linkAt == null) {
                return;
            }
            shouldPower |= linkAt.shouldPanelBePowered();
        }
        if (shouldPower == this.redstonePowered) {
            return;
        }
        this.redstonePowered = shouldPower;
        this.blockEntity.notifyUpdate();
        this.timer = 1;
    }

    private void notifyRedstoneOutputs() {
        for (FactoryPanelConnection connection : this.targetedByLinks.values()) {
            if (!this.getWorld().isLoaded(connection.from.pos())) {
                return;
            }
            FactoryPanelSupportBehaviour linkAt = FactoryPanelBehaviour.linkAt((BlockAndTintGetter)this.getWorld(), connection);
            if (linkAt == null || linkAt.isOutput()) {
                return;
            }
            linkAt.notifyLink();
        }
    }

    private void tickStorageMonitor() {
        boolean shouldWait;
        ItemStack filter = this.getFilter();
        int unloadedLinkCount = this.getUnloadedLinks();
        FactoryPanelBlockEntity panelBE = this.panelBE();
        if (!panelBE.restocker && unloadedLinkCount == 0 && this.lastReportedUnloadedLinks != 0) {
            LogisticsManager.SUMMARIES.invalidate((Object)this.network);
        }
        int inStorage = this.getLevelInStorage();
        int promised = this.getPromised();
        int demand = this.getAmount() * (this.upTo ? 1 : filter.getMaxStackSize());
        boolean shouldSatisfy = filter.isEmpty() || inStorage >= demand;
        boolean shouldPromiseSatisfy = filter.isEmpty() || inStorage + promised >= demand;
        boolean bl = shouldWait = unloadedLinkCount > 0;
        if (this.lastReportedLevelInStorage == inStorage && this.lastReportedPromises == promised && this.lastReportedUnloadedLinks == unloadedLinkCount && this.satisfied == shouldSatisfy && this.promisedSatisfied == shouldPromiseSatisfy && this.waitingForNetwork == shouldWait) {
            return;
        }
        if (!this.satisfied && shouldSatisfy && demand > 0) {
            AllSoundEvents.CONFIRM.playOnServer(this.getWorld(), (Vec3i)this.getPos(), 0.075f, 1.0f);
            AllSoundEvents.CONFIRM_2.playOnServer(this.getWorld(), (Vec3i)this.getPos(), 0.125f, 0.575f);
        }
        boolean notifyOutputs = this.satisfied != shouldSatisfy;
        this.lastReportedLevelInStorage = inStorage;
        this.satisfied = shouldSatisfy;
        this.lastReportedPromises = promised;
        this.promisedSatisfied = shouldPromiseSatisfy;
        this.lastReportedUnloadedLinks = unloadedLinkCount;
        this.waitingForNetwork = shouldWait;
        if (!this.getWorld().isClientSide) {
            this.blockEntity.sendData();
        }
        if (notifyOutputs) {
            this.notifyRedstoneOutputs();
        }
    }

    /*
     * WARNING - void declaration
     */
    private void tickRequests() {
        FactoryPanelBlockEntity panelBE = this.panelBE();
        if (this.targetedBy.isEmpty() && !panelBE.restocker) {
            return;
        }
        if (panelBE.restocker) {
            this.restockerPromises.tick();
        }
        if (this.satisfied || this.promisedSatisfied || this.waitingForNetwork || this.redstonePowered) {
            return;
        }
        if (this.timer > 0) {
            this.timer = Math.min((int)this.timer, (int)this.getConfigRequestIntervalInTicks());
            --this.timer;
            return;
        }
        this.resetTimer();
        if (this.recipeAddress.isBlank()) {
            return;
        }
        if (panelBE.restocker) {
            this.tryRestock();
            return;
        }
        boolean failed = false;
        HashMap<UUID, Map> consolidated = new HashMap<UUID, Map>();
        for (FactoryPanelConnection factoryPanelConnection : this.targetedBy.values()) {
            FactoryPanelBehaviour factoryPanelBehaviour = FactoryPanelBehaviour.at((BlockAndTintGetter)this.getWorld(), factoryPanelConnection);
            if (factoryPanelBehaviour == null) {
                return;
            }
            ItemStack item = factoryPanelBehaviour.getFilter();
            Map networkItemCounts = consolidated.computeIfAbsent(factoryPanelBehaviour.network, $ -> new Object2ObjectOpenCustomHashMap(ItemStackLinkedSet.TYPE_AND_TAG));
            networkItemCounts.computeIfAbsent(item, $ -> new ItemStackConnections(item));
            ItemStackConnections itemStackConnections = (ItemStackConnections)networkItemCounts.get(item);
            itemStackConnections.add(factoryPanelConnection);
            itemStackConnections.totalAmount += factoryPanelConnection.amount;
        }
        HashMultimap toRequest = HashMultimap.create();
        for (Map.Entry entry : consolidated.entrySet()) {
            UUID network = (UUID)entry.getKey();
            Iterator summary = LogisticsManager.getSummaryOfNetwork(network, true);
            for (ItemStackConnections connections : ((Map)entry.getValue()).values()) {
                if (connections.totalAmount == 0 || connections.item.isEmpty() || ((InventorySummary)((Object)summary)).getCountOf(connections.item) < connections.totalAmount) {
                    for (FactoryPanelConnection connection : connections) {
                        this.sendEffect(connection.from, false);
                    }
                    failed = true;
                    continue;
                }
                BigItemStack stack2 = new BigItemStack(connections.item, connections.totalAmount);
                toRequest.put((Object)network, (Object)stack2);
                for (FactoryPanelConnection connection : connections) {
                    this.sendEffect(connection.from, true);
                }
            }
        }
        if (failed) {
            return;
        }
        Map map = toRequest.asMap();
        PackageOrderWithCrafts packageOrderWithCrafts = PackageOrderWithCrafts.empty();
        ArrayList<Multimap<PackagerBlockEntity, PackagingRequest>> requests = new ArrayList<Multimap<PackagerBlockEntity, PackagingRequest>>();
        if (!this.activeCraftingArrangement.isEmpty()) {
            PackageOrderWithCrafts packageOrderWithCrafts2 = PackageOrderWithCrafts.singleRecipe(this.activeCraftingArrangement.stream().map(stack -> new BigItemStack(stack.copyWithCount(1))).toList());
        }
        for (Map.Entry entry : map.entrySet()) {
            void var6_13;
            PackageOrderWithCrafts order = new PackageOrderWithCrafts(new PackageOrder(new ArrayList<BigItemStack>((Collection)entry.getValue())), var6_13.orderedCrafts());
            Multimap<PackagerBlockEntity, PackagingRequest> request = LogisticsManager.findPackagersForRequest((UUID)entry.getKey(), order, null, this.recipeAddress);
            requests.add(request);
        }
        for (Multimap multimap : requests) {
            for (PackagerBlockEntity packager : multimap.keySet()) {
                if (!packager.isTooBusyFor(LogisticallyLinkedBehaviour.RequestType.RESTOCK)) continue;
                return;
            }
        }
        for (Multimap multimap : requests) {
            LogisticsManager.performPackageRequests((Multimap<PackagerBlockEntity, PackagingRequest>)multimap);
        }
        RequestPromiseQueue promises = Create.LOGISTICS.getQueuedPromises(this.network);
        if (promises != null) {
            promises.add(new RequestPromise(new BigItemStack(this.getFilter(), this.recipeOutput)));
        }
        panelBE.advancements.awardPlayer(AllAdvancements.FACTORY_GAUGE);
    }

    private void tryRestock() {
        ItemStack item = this.getFilter();
        if (item.isEmpty()) {
            return;
        }
        FactoryPanelBlockEntity panelBE = this.panelBE();
        PackagerBlockEntity packager = panelBE.getRestockedPackager();
        if (packager == null || !packager.targetInventory.hasInventory()) {
            return;
        }
        int availableOnNetwork = LogisticsManager.getStockOf(this.network, item, packager.targetInventory.getIdentifiedInventory());
        if (availableOnNetwork == 0) {
            this.sendEffect(this.getPanelPosition(), false);
            return;
        }
        int inStorage = this.getLevelInStorage();
        int promised = this.getPromised();
        int maxStackSize = item.getMaxStackSize();
        int demand = this.getAmount() * (this.upTo ? 1 : maxStackSize);
        int amountToOrder = Math.clamp((int)(demand - promised - inStorage), (int)0, (int)(maxStackSize * 9));
        BigItemStack orderedItem = new BigItemStack(item, Math.min((int)amountToOrder, (int)availableOnNetwork));
        PackageOrderWithCrafts order = PackageOrderWithCrafts.simple(List.of(orderedItem));
        this.sendEffect(this.getPanelPosition(), true);
        if (!LogisticsManager.broadcastPackageRequest(this.network, LogisticallyLinkedBehaviour.RequestType.RESTOCK, order, packager.targetInventory.getIdentifiedInventory(), this.recipeAddress)) {
            return;
        }
        this.restockerPromises.add(new RequestPromise(orderedItem));
    }

    private void sendEffect(FactoryPanelPosition fromPos, boolean success) {
        Level level = this.getWorld();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            CatnipServices.NETWORK.sendToClientsAround(serverLevel, (Vec3i)this.getPos(), 64.0, (CustomPacketPayload)new FactoryPanelEffectPacket(fromPos, this.getPanelPosition(), success));
        }
    }

    public void addConnection(FactoryPanelPosition fromPos) {
        FactoryPanelSupportBehaviour link = FactoryPanelBehaviour.linkAt((BlockAndTintGetter)this.getWorld(), fromPos);
        if (link != null) {
            this.targetedByLinks.put(fromPos.pos(), new FactoryPanelConnection(fromPos, 1));
            link.connect(this);
            this.blockEntity.notifyUpdate();
            return;
        }
        if (this.panelBE().restocker) {
            return;
        }
        if (this.targetedBy.size() >= 9) {
            return;
        }
        FactoryPanelBehaviour source = FactoryPanelBehaviour.at((BlockAndTintGetter)this.getWorld(), fromPos);
        if (source == null) {
            return;
        }
        source.targeting.add(this.getPanelPosition());
        this.targetedBy.put(fromPos, new FactoryPanelConnection(fromPos, 1));
        this.blockEntity.notifyUpdate();
    }

    public FactoryPanelPosition getPanelPosition() {
        return new FactoryPanelPosition(this.getPos(), this.slot);
    }

    public FactoryPanelBlockEntity panelBE() {
        return (FactoryPanelBlockEntity)this.blockEntity;
    }

    @Override
    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
        if (!Create.LOGISTICS.mayInteract(this.network, player)) {
            player.displayClientMessage((Component)CreateLang.translate("logistically_linked.protected", new Object[0]).style(ChatFormatting.RED).component(), true);
            return;
        }
        boolean isClientSide = player.level().isClientSide;
        if (this.targeting.size() + this.targetedByLinks.size() > 0 && player.getItemInHand(hand).is(Tags.Items.TOOLS_WRENCH)) {
            int sharedMode = -1;
            boolean notifySelf = false;
            for (FactoryPanelPosition target : this.targeting) {
                FactoryPanelConnection connection;
                FactoryPanelBehaviour at = FactoryPanelBehaviour.at((BlockAndTintGetter)this.getWorld(), target);
                if (at == null || (connection = at.targetedBy.get(this.getPanelPosition())) == null) continue;
                if (sharedMode == -1) {
                    sharedMode = (connection.arrowBendMode + 1) % 4;
                }
                connection.arrowBendMode = sharedMode;
                if (isClientSide) continue;
                at.blockEntity.notifyUpdate();
            }
            for (FactoryPanelConnection connection : this.targetedByLinks.values()) {
                if (sharedMode == -1) {
                    sharedMode = (connection.arrowBendMode + 1) % 4;
                }
                connection.arrowBendMode = sharedMode;
                if (isClientSide) continue;
                notifySelf = true;
            }
            if (sharedMode == -1) {
                return;
            }
            char[] boxes = "\u25a1\u25a1\u25a1\u25a1".toCharArray();
            boxes[sharedMode] = 9632;
            player.displayClientMessage((Component)CreateLang.translate("factory_panel.cycled_arrow_path", new String(boxes)).component(), true);
            if (notifySelf) {
                this.blockEntity.notifyUpdate();
            }
            return;
        }
        if (isClientSide && FactoryPanelConnectionHandler.panelClicked((LevelAccessor)this.getWorld(), player, this)) {
            return;
        }
        ItemStack heldItem = player.getItemInHand(hand);
        if (this.getFilter().isEmpty()) {
            if (heldItem.isEmpty()) {
                if (!isClientSide && player instanceof ServerPlayer) {
                    ServerPlayer sp = (ServerPlayer)player;
                    sp.openMenu((MenuProvider)this, buf -> FactoryPanelPosition.STREAM_CODEC.encode(buf, (Object)this.getPanelPosition()));
                }
                return;
            }
            super.onShortInteract(player, hand, side, hitResult);
            return;
        }
        if (heldItem.getItem() instanceof LogisticallyLinkedBlockItem) {
            if (!isClientSide) {
                LogisticallyLinkedBlockItem.assignFrequency(heldItem, player, this.network);
            }
            return;
        }
        if (isClientSide) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.displayScreen(player));
        }
    }

    public void enable() {
        this.active = true;
        this.blockEntity.notifyUpdate();
    }

    public void disable() {
        this.destroy();
        this.active = false;
        this.targetedBy = new HashMap<FactoryPanelPosition, FactoryPanelConnection>();
        this.targeting = new HashSet<FactoryPanelPosition>();
        this.count = 0;
        this.satisfied = false;
        this.promisedSatisfied = false;
        this.recipeAddress = "";
        this.recipeOutput = 1;
        this.setFilter(ItemStack.EMPTY);
        this.blockEntity.notifyUpdate();
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public boolean isMissingAddress() {
        return (!this.targetedBy.isEmpty() || this.panelBE().restocker) && this.count != 0 && this.recipeAddress.isBlank();
    }

    @Override
    public void destroy() {
        this.disconnectAll();
        super.destroy();
    }

    public void disconnectAll() {
        FactoryPanelPosition panelPosition = this.getPanelPosition();
        this.disconnectAllLinks();
        for (FactoryPanelConnection connection : this.targetedBy.values()) {
            FactoryPanelBehaviour source = FactoryPanelBehaviour.at((BlockAndTintGetter)this.getWorld(), connection);
            if (source == null) continue;
            source.targeting.remove(panelPosition);
            source.blockEntity.sendData();
        }
        for (FactoryPanelPosition position : this.targeting) {
            FactoryPanelBehaviour target = FactoryPanelBehaviour.at((BlockAndTintGetter)this.getWorld(), position);
            if (target == null) continue;
            target.targetedBy.remove(panelPosition);
            target.blockEntity.sendData();
        }
        this.targetedBy.clear();
        this.targeting.clear();
    }

    public void disconnectAllLinks() {
        for (FactoryPanelConnection connection : this.targetedByLinks.values()) {
            FactoryPanelSupportBehaviour source = FactoryPanelBehaviour.linkAt((BlockAndTintGetter)this.getWorld(), connection);
            if (source == null) continue;
            source.disconnect(this);
        }
        this.targetedByLinks.clear();
    }

    public int getUnloadedLinks() {
        if (this.getWorld().isClientSide()) {
            return this.lastReportedUnloadedLinks;
        }
        if (this.panelBE().restocker) {
            return this.panelBE().getRestockedPackager() == null ? 1 : 0;
        }
        return Create.LOGISTICS.getUnloadedLinkCount(this.network);
    }

    public int getLevelInStorage() {
        if (this.blockEntity.isVirtual()) {
            return 1;
        }
        if (this.getWorld().isClientSide()) {
            return this.lastReportedLevelInStorage;
        }
        if (this.getFilter().isEmpty()) {
            return 0;
        }
        InventorySummary summary = this.getRelevantSummary();
        return summary.getCountOf(this.getFilter());
    }

    private InventorySummary getRelevantSummary() {
        FactoryPanelBlockEntity panelBE = this.panelBE();
        if (!panelBE.restocker) {
            return LogisticsManager.getSummaryOfNetwork(this.network, false);
        }
        PackagerBlockEntity packager = panelBE.getRestockedPackager();
        if (packager == null) {
            return InventorySummary.EMPTY;
        }
        return packager.getAvailableItems();
    }

    public int getPromised() {
        if (this.getWorld().isClientSide()) {
            return this.lastReportedPromises;
        }
        ItemStack item = this.getFilter();
        if (item.isEmpty()) {
            return 0;
        }
        if (this.panelBE().restocker) {
            if (this.forceClearPromises) {
                this.restockerPromises.forceClear(item);
                this.resetTimerSlightly();
            }
            this.forceClearPromises = false;
            return this.restockerPromises.getTotalPromisedAndRemoveExpired(item, this.getPromiseExpiryTimeInTicks());
        }
        RequestPromiseQueue promises = Create.LOGISTICS.getQueuedPromises(this.network);
        if (promises == null) {
            return 0;
        }
        if (this.forceClearPromises) {
            promises.forceClear(item);
            this.resetTimerSlightly();
        }
        this.forceClearPromises = false;
        return promises.getTotalPromisedAndRemoveExpired(item, this.getPromiseExpiryTimeInTicks());
    }

    public void resetTimer() {
        this.timer = this.getConfigRequestIntervalInTicks();
    }

    public void resetTimerSlightly() {
        this.timer = this.getConfigRequestIntervalInTicks() / 2;
    }

    private int getConfigRequestIntervalInTicks() {
        return (Integer)AllConfigs.server().logistics.factoryGaugeTimer.get();
    }

    private int getPromiseExpiryTimeInTicks() {
        if (this.promiseClearingInterval == -1) {
            return -1;
        }
        if (this.promiseClearingInterval == 0) {
            return 600;
        }
        return this.promiseClearingInterval * 20 * 60;
    }

    @Override
    public void writeSafe(CompoundTag nbt, HolderLookup.Provider registries) {
        if (!this.active) {
            return;
        }
        CompoundTag panelTag = new CompoundTag();
        panelTag.put("Filter", this.getFilter().saveOptional(registries));
        panelTag.putBoolean("UpTo", this.upTo);
        panelTag.putInt("FilterAmount", this.count);
        panelTag.putUUID("Freq", this.network);
        panelTag.putString("RecipeAddress", this.recipeAddress);
        panelTag.putInt("PromiseClearingInterval", -1);
        panelTag.putInt("RecipeOutput", 1);
        if (this.panelBE().restocker) {
            panelTag.put("Promises", (Tag)this.restockerPromises.write(registries));
        }
        nbt.put(CreateLang.asId((String)this.slot.name()), (Tag)panelTag);
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        if (!this.active) {
            return;
        }
        CompoundTag panelTag = new CompoundTag();
        super.write(panelTag, registries, clientPacket);
        panelTag.putInt("Timer", this.timer);
        panelTag.putInt("LastLevel", this.lastReportedLevelInStorage);
        panelTag.putInt("LastPromised", this.lastReportedPromises);
        panelTag.putInt("LastUnloadedLinks", this.lastReportedUnloadedLinks);
        panelTag.putBoolean("Satisfied", this.satisfied);
        panelTag.putBoolean("PromisedSatisfied", this.promisedSatisfied);
        panelTag.putBoolean("Waiting", this.waitingForNetwork);
        panelTag.putBoolean("RedstonePowered", this.redstonePowered);
        panelTag.put("Targeting", (Tag)CatnipCodecUtils.encode((Codec)CatnipCodecs.set(FactoryPanelPosition.CODEC), (HolderLookup.Provider)registries, this.targeting).orElseThrow());
        panelTag.put("TargetedBy", (Tag)CatnipCodecUtils.encode((Codec)Codec.list(FactoryPanelConnection.CODEC), (HolderLookup.Provider)registries, new ArrayList<FactoryPanelConnection>(this.targetedBy.values())).orElseThrow());
        panelTag.put("TargetedByLinks", (Tag)CatnipCodecUtils.encode((Codec)Codec.list(FactoryPanelConnection.CODEC), (HolderLookup.Provider)registries, new ArrayList<FactoryPanelConnection>(this.targetedByLinks.values())).orElseThrow());
        panelTag.putString("RecipeAddress", this.recipeAddress);
        panelTag.putInt("RecipeOutput", this.recipeOutput);
        panelTag.putInt("PromiseClearingInterval", this.promiseClearingInterval);
        panelTag.putUUID("Freq", this.network);
        panelTag.put("Craft", (Tag)NBTHelper.writeItemList(this.activeCraftingArrangement, (HolderLookup.Provider)registries));
        if (this.panelBE().restocker && !clientPacket) {
            panelTag.put("Promises", (Tag)this.restockerPromises.write(registries));
        }
        nbt.put(CreateLang.asId((String)this.slot.name()), (Tag)panelTag);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        CompoundTag panelTag = nbt.getCompound(CreateLang.asId((String)this.slot.name()));
        if (panelTag.isEmpty()) {
            this.active = false;
            return;
        }
        this.active = true;
        this.filter = FilterItemStack.of(registries, panelTag.getCompound("Filter"));
        this.count = panelTag.getInt("FilterAmount");
        this.upTo = panelTag.getBoolean("UpTo");
        this.timer = panelTag.getInt("Timer");
        this.lastReportedLevelInStorage = panelTag.getInt("LastLevel");
        this.lastReportedPromises = panelTag.getInt("LastPromised");
        this.lastReportedUnloadedLinks = panelTag.getInt("LastUnloadedLinks");
        this.satisfied = panelTag.getBoolean("Satisfied");
        this.promisedSatisfied = panelTag.getBoolean("PromisedSatisfied");
        this.waitingForNetwork = panelTag.getBoolean("Waiting");
        this.redstonePowered = panelTag.getBoolean("RedstonePowered");
        this.promiseClearingInterval = panelTag.getInt("PromiseClearingInterval");
        if (panelTag.hasUUID("Freq")) {
            this.network = panelTag.getUUID("Freq");
        }
        this.targeting.clear();
        this.targeting.addAll(CatnipCodecUtils.decode((Codec)CatnipCodecs.set(FactoryPanelPosition.CODEC), (HolderLookup.Provider)registries, (Tag)panelTag.get("Targeting")).orElse(Set.of()));
        this.targetedBy.clear();
        CatnipCodecUtils.decode((Codec)Codec.list(FactoryPanelConnection.CODEC), (HolderLookup.Provider)registries, (Tag)panelTag.get("TargetedBy")).orElse(List.of()).forEach(c -> this.targetedBy.put(c.from, (FactoryPanelConnection)c));
        this.targetedByLinks.clear();
        CatnipCodecUtils.decode((Codec)Codec.list(FactoryPanelConnection.CODEC), (HolderLookup.Provider)registries, (Tag)panelTag.get("TargetedByLinks")).orElse(List.of()).forEach(c -> this.targetedByLinks.put(c.from.pos(), (FactoryPanelConnection)c));
        this.activeCraftingArrangement = NBTHelper.readItemList((ListTag)panelTag.getList("Craft", 10), (HolderLookup.Provider)registries);
        this.recipeAddress = panelTag.getString("RecipeAddress");
        this.recipeOutput = panelTag.getInt("RecipeOutput");
        if (nbt.getBoolean("Restocker") && !clientPacket) {
            this.restockerPromises = RequestPromiseQueue.read(panelTag.getCompound("Promises"), registries, () -> {});
            this.promisePrimedForMarkDirty = false;
        }
    }

    @Override
    public float getRenderDistance() {
        return 64.0f;
    }

    @Override
    public MutableComponent formatValue(ValueSettingsBehaviour.ValueSettings value) {
        if (value.value() == 0) {
            return CreateLang.translateDirect("gui.factory_panel.inactive", new Object[0]);
        }
        return Component.literal((String)(Math.max((int)0, (int)value.value()) + (value.row() == 0 ? "" : "\u25a4")));
    }

    @Override
    public boolean setFilter(ItemStack stack) {
        ItemStack filter = stack.copy();
        if (stack.getItem() instanceof FilterItem) {
            return false;
        }
        this.filter = FilterItemStack.of(filter);
        this.blockEntity.setChanged();
        this.blockEntity.sendData();
        return true;
    }

    @Override
    public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings settings, boolean ctrlDown) {
        if (this.getValueSettings().equals(settings)) {
            return;
        }
        this.count = Math.max((int)0, (int)settings.value());
        this.upTo = settings.row() == 0;
        this.panelBE().redraw = true;
        this.blockEntity.setChanged();
        this.blockEntity.sendData();
        this.playFeedbackSound(this);
        this.resetTimerSlightly();
        if (!this.getWorld().isClientSide) {
            this.notifyRedstoneOutputs();
        }
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        int maxAmount = 100;
        return new ValueSettingsBoard((Component)CreateLang.translate("factory_panel.target_amount", new Object[0]).component(), maxAmount, 10, List.of(CreateLang.translate("schedule.condition.threshold.items", new Object[0]).component(), CreateLang.translate("schedule.condition.threshold.stacks", new Object[0]).component()), new ValueSettingsFormatter(this::formatValue));
    }

    @Override
    public MutableComponent getLabel() {
        Object key = "";
        if (!this.targetedBy.isEmpty() && this.count == 0) {
            return CreateLang.translate("gui.factory_panel.no_target_amount_set", new Object[0]).style(ChatFormatting.RED).component();
        }
        if (this.isMissingAddress()) {
            return CreateLang.translate("gui.factory_panel.address_missing", new Object[0]).style(ChatFormatting.RED).component();
        }
        if (this.getFilter().isEmpty()) {
            key = "factory_panel.new_factory_task";
        } else if (this.waitingForNetwork) {
            key = "factory_panel.some_links_unloaded";
        } else {
            if (this.getAmount() == 0 || this.targetedBy.isEmpty()) {
                return this.getFilter().getHoverName().plainCopy();
            }
            key = this.getFilter().getHoverName().getString();
            if (this.redstonePowered) {
                key = (String)key + " " + CreateLang.translate("factory_panel.redstone_paused", new Object[0]).string();
            } else if (!this.satisfied) {
                key = (String)key + " " + CreateLang.translate("factory_panel.in_progress", new Object[0]).string();
            }
            return CreateLang.text((String)key).component();
        }
        return CreateLang.translate((String)key, new Object[0]).component();
    }

    @Override
    public ValueSettingsBehaviour.ValueSettings getValueSettings() {
        return new ValueSettingsBehaviour.ValueSettings(this.upTo ? 0 : 1, this.count);
    }

    @Override
    public MutableComponent getTip() {
        return CreateLang.translateDirect(this.filter.isEmpty() ? "logistics.filter.click_to_set" : "factory_panel.click_to_configure", new Object[0]);
    }

    @Override
    public MutableComponent getAmountTip() {
        return CreateLang.translateDirect("factory_panel.hold_to_set_amount", new Object[0]);
    }

    @Override
    public MutableComponent getCountLabelForValueBox() {
        String stacks;
        if (this.filter.isEmpty()) {
            return Component.empty();
        }
        if (this.waitingForNetwork) {
            return Component.literal((String)"?");
        }
        int levelInStorage = this.getLevelInStorage();
        boolean inf = levelInStorage >= 1000000000;
        int inStorage = levelInStorage / (this.upTo ? 1 : this.getFilter().getMaxStackSize());
        int promised = this.getPromised();
        String string = stacks = this.upTo ? "" : "\u25a4";
        if (this.count == 0) {
            return CreateLang.text((String)(inf ? "  \u221e" : inStorage + stacks)).color(15855592).component();
        }
        return CreateLang.text((String)(inf ? "  \u221e" : "   " + inStorage + stacks)).color(this.satisfied ? 14155688 : (this.promisedSatisfied ? 16764277 : 16760744)).add(CreateLang.text(promised == 0 ? "" : "\u23f6")).add(CreateLang.text("/").style(ChatFormatting.WHITE)).add(CreateLang.text(this.count + stacks + "  ").color(15855592)).component();
    }

    @Override
    public int netId() {
        return 2 + this.slot.ordinal();
    }

    @Override
    public boolean isCountVisible() {
        return !this.getFilter().isEmpty();
    }

    @Override
    public BehaviourType<?> getType() {
        return FactoryPanelBehaviour.getTypeForSlot(this.slot);
    }

    public static BehaviourType<?> getTypeForSlot(FactoryPanelBlock.PanelSlot slot) {
        return switch (slot) {
            default -> throw new MatchException(null, null);
            case FactoryPanelBlock.PanelSlot.BOTTOM_LEFT -> BOTTOM_LEFT;
            case FactoryPanelBlock.PanelSlot.TOP_LEFT -> TOP_LEFT;
            case FactoryPanelBlock.PanelSlot.TOP_RIGHT -> TOP_RIGHT;
            case FactoryPanelBlock.PanelSlot.BOTTOM_RIGHT -> BOTTOM_RIGHT;
        };
    }

    @OnlyIn(value=Dist.CLIENT)
    public void displayScreen(Player player) {
        if (player instanceof LocalPlayer) {
            ScreenOpener.open((Screen)new FactoryPanelScreen(this));
        }
    }

    public int getIngredientStatusColor() {
        return this.count == 0 || this.isMissingAddress() || this.redstonePowered ? 0x888898 : (this.waitingForNetwork ? 0x5B3B3B : (this.satisfied ? 10420095 : (this.promisedSatisfied ? 0x22AFAF : 4026045)));
    }

    @Override
    public ItemRequirement getRequiredItems() {
        return this.isActive() ? new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, AllBlocks.FACTORY_GAUGE.asItem()) : ItemRequirement.NONE;
    }

    @Override
    public boolean canShortInteract(ItemStack toApply) {
        return true;
    }

    @Override
    public boolean readFromClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        return false;
    }

    @Override
    public boolean writeToClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        return false;
    }

    private void tickOutline() {
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> LogisticallyLinkedClientHandler.tickPanel(this));
    }

    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return FactoryPanelSetItemMenu.create(containerId, playerInventory, this);
    }

    public Component getDisplayName() {
        return this.blockEntity.getBlockState().getBlock().getName();
    }

    public String getFrogAddress() {
        PackagerBlockEntity packager = this.panelBE().getRestockedPackager();
        if (packager == null) {
            return null;
        }
        BlockEntity blockEntity = packager.getLevel().getBlockEntity(packager.getBlockPos().above());
        if (blockEntity instanceof FrogportBlockEntity) {
            FrogportBlockEntity fpbe = (FrogportBlockEntity)blockEntity;
            if (fpbe.addressFilter != null && !fpbe.addressFilter.isBlank()) {
                return fpbe.addressFilter;
            }
        }
        return null;
    }

    public static class ItemStackConnections
    extends ArrayList<FactoryPanelConnection> {
        public ItemStack item;
        public int totalAmount;

        public ItemStackConnections(ItemStack item) {
            this.item = item;
        }
    }
}
