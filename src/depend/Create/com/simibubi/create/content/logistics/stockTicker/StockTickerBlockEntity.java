/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.Containers
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.WiFiParticle;
import com.simibubi.create.content.logistics.stockTicker.LogisticalStockRequestPacket;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.logistics.stockTicker.StockCheckingBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryMenu;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.CreateLang;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

public class StockTickerBlockEntity
extends StockCheckingBlockEntity
implements IHaveHoveringInformation,
Clearable {
    public AbstractComputerBehaviour computerBehaviour;
    protected List<List<BigItemStack>> lastClientsideStockSnapshot;
    protected InventorySummary lastClientsideStockSnapshotAsSummary;
    protected List<BigItemStack> newlyReceivedStockSnapshot;
    protected String previouslyUsedAddress = "";
    protected int activeLinks;
    protected int ticksSinceLastUpdate;
    protected List<ItemStack> categories;
    protected Map<UUID, List<Integer>> hiddenCategoriesByPlayer;
    protected SmartInventory receivedPayments = new SmartInventory(27, this, 64, false);

    public StockTickerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.categories = new ArrayList<ItemStack>();
        this.hiddenCategoriesByPlayer = new HashMap<UUID, List<Integer>>();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.STOCK_TICKER.get(), (be, context) -> be.receivedPayments);
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.STOCK_TICKER.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }

    public void refreshClientStockSnapshot() {
        this.ticksSinceLastUpdate = 0;
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new LogisticalStockRequestPacket(this.worldPosition));
    }

    public IItemHandler getReceivedPaymentsHandler() {
        return this.receivedPayments;
    }

    public List<List<BigItemStack>> getClientStockSnapshot() {
        return this.lastClientsideStockSnapshot;
    }

    public InventorySummary getLastClientsideStockSnapshotAsSummary() {
        return this.lastClientsideStockSnapshotAsSummary;
    }

    public int getTicksSinceLastUpdate() {
        return this.ticksSinceLastUpdate;
    }

    @Override
    public boolean broadcastPackageRequest(LogisticallyLinkedBehaviour.RequestType type, PackageOrderWithCrafts order, IdentifiedInventory ignoredHandler, String address) {
        boolean result = super.broadcastPackageRequest(type, order, ignoredHandler, address);
        this.previouslyUsedAddress = address;
        this.notifyUpdate();
        return result;
    }

    @Override
    public InventorySummary getRecentSummary() {
        InventorySummary recentSummary = super.getRecentSummary();
        int contributingLinks = recentSummary.contributingLinks;
        if (this.activeLinks != contributingLinks && !this.isRemoved()) {
            this.activeLinks = contributingLinks;
            this.sendData();
        }
        return recentSummary;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            if (this.ticksSinceLastUpdate < 100) {
                ++this.ticksSinceLastUpdate;
            }
            return;
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putString("PreviousAddress", this.previouslyUsedAddress);
        tag.put("ReceivedPayments", (Tag)this.receivedPayments.serializeNBT(registries));
        tag.put("Categories", (Tag)NBTHelper.writeItemList(this.categories, (HolderLookup.Provider)registries));
        tag.put("HiddenCategories", (Tag)NBTHelper.writeCompoundList(this.hiddenCategoriesByPlayer.entrySet(), e -> {
            CompoundTag c = new CompoundTag();
            c.putUUID("Id", (UUID)e.getKey());
            c.putIntArray("Indices", (List)e.getValue());
            return c;
        }));
        if (clientPacket) {
            tag.putInt("ActiveLinks", this.activeLinks);
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.previouslyUsedAddress = tag.getString("PreviousAddress");
        this.receivedPayments.deserializeNBT(registries, tag.getCompound("ReceivedPayments"));
        this.categories = NBTHelper.readItemList((ListTag)tag.getList("Categories", 10), (HolderLookup.Provider)registries);
        this.categories.removeIf(stack -> !stack.isEmpty() && !(stack.getItem() instanceof FilterItem));
        this.hiddenCategoriesByPlayer.clear();
        NBTHelper.iterateCompoundList((ListTag)tag.getList("HiddenCategories", 10), c -> this.hiddenCategoriesByPlayer.put(c.getUUID("Id"), IntStream.of(c.getIntArray("Indices")).boxed().toList()));
        if (clientPacket) {
            this.activeLinks = tag.getInt("ActiveLinks");
        }
    }

    public void receiveStockPacket(List<BigItemStack> stacks, boolean endOfTransmission) {
        if (this.newlyReceivedStockSnapshot == null) {
            this.newlyReceivedStockSnapshot = new ArrayList<BigItemStack>();
        }
        this.newlyReceivedStockSnapshot.addAll(stacks);
        if (!endOfTransmission) {
            return;
        }
        this.lastClientsideStockSnapshotAsSummary = new InventorySummary();
        this.lastClientsideStockSnapshot = new ArrayList<List<BigItemStack>>();
        for (BigItemStack bigStack : this.newlyReceivedStockSnapshot) {
            this.lastClientsideStockSnapshotAsSummary.add(bigStack);
        }
        for (ItemStack filter : this.categories) {
            ArrayList<BigItemStack> inCategory = new ArrayList<BigItemStack>();
            if (!filter.isEmpty()) {
                FilterItemStack filterItemStack = FilterItemStack.of(filter);
                Iterator<BigItemStack> iterator = this.newlyReceivedStockSnapshot.iterator();
                while (iterator.hasNext()) {
                    BigItemStack bigStack = iterator.next();
                    if (!filterItemStack.test(this.level, bigStack.stack)) continue;
                    inCategory.add(bigStack);
                    iterator.remove();
                }
            }
            this.lastClientsideStockSnapshot.add(inCategory);
        }
        ArrayList<BigItemStack> unsorted = new ArrayList<BigItemStack>(this.newlyReceivedStockSnapshot);
        this.lastClientsideStockSnapshot.add(unsorted);
        this.newlyReceivedStockSnapshot = null;
    }

    public boolean isKeeperPresent() {
        for (int yOffset : Iterate.zeroAndOne) {
            for (Direction side : Iterate.horizontalDirections) {
                BlockPos seatPos = this.worldPosition.below(yOffset).relative(side);
                for (SeatEntity seatEntity : this.level.getEntitiesOfClass(SeatEntity.class, new AABB(seatPos))) {
                    if (!seatEntity.isVehicle()) continue;
                    return true;
                }
                if (yOffset != 0 || !AllBlockEntityTypes.HEATER.is(this.level.getBlockEntity(seatPos))) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (this.receivedPayments.isEmpty()) {
            return false;
        }
        if (!this.behaviour.mayAdministrate((Player)Minecraft.getInstance().player)) {
            return false;
        }
        CreateLang.translate("stock_ticker.contains_payments", new Object[0]).style(ChatFormatting.WHITE).forGoggles(tooltip);
        InventorySummary summary = new InventorySummary();
        for (int i = 0; i < this.receivedPayments.getSlots(); ++i) {
            summary.add(this.receivedPayments.getStackInSlot(i));
        }
        for (BigItemStack entry : summary.getStacksByCount()) {
            CreateLang.builder().text(Component.translatable((String)entry.stack.getDescriptionId()).getString() + " x" + entry.count).style(ChatFormatting.GREEN).forGoggles(tooltip);
        }
        CreateLang.translate("stock_ticker.click_to_retrieve", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
        return true;
    }

    public void clearContent() {
        this.categories.clear();
        this.receivedPayments.clearContent();
    }

    @Override
    public void destroy() {
        ItemHelper.dropContents(this.level, this.worldPosition, (IItemHandler)this.receivedPayments);
        for (ItemStack filter : this.categories) {
            if (filter.isEmpty() || !(filter.getItem() instanceof FilterItem)) continue;
            Containers.dropItemStack((Level)this.level, (double)this.worldPosition.getX(), (double)this.worldPosition.getY(), (double)this.worldPosition.getZ(), (ItemStack)filter);
        }
        super.destroy();
    }

    public void playEffect() {
        AllSoundEvents.STOCK_LINK.playAt(this.level, (Vec3i)this.worldPosition, 1.0f, 1.0f, false);
        Vec3 vec3 = Vec3.atCenterOf((Vec3i)this.worldPosition);
        this.level.addParticle((ParticleOptions)new WiFiParticle.Data(), vec3.x, vec3.y, vec3.z, 1.0, 1.0, 1.0);
    }

    public class RequestMenuProvider
    implements MenuProvider {
        public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
            return StockKeeperRequestMenu.create(pContainerId, pPlayerInventory, StockTickerBlockEntity.this);
        }

        public Component getDisplayName() {
            return Component.empty();
        }
    }

    public class CategoryMenuProvider
    implements MenuProvider {
        public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
            return StockKeeperCategoryMenu.create(pContainerId, pPlayerInventory, StockTickerBlockEntity.this);
        }

        public Component getDisplayName() {
            return Component.empty();
        }
    }
}
