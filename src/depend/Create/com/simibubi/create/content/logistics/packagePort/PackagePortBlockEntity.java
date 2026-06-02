/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.CatnipCodecUtils
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 */
package com.simibubi.create.content.logistics.packagePort;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packagePort.PackagePortAutomationInventoryWrapper;
import com.simibubi.create.content.logistics.packagePort.PackagePortMenu;
import com.simibubi.create.content.logistics.packagePort.PackagePortTarget;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.animatedContainer.AnimatedContainerBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Clearable;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public abstract class PackagePortBlockEntity
extends SmartBlockEntity
implements MenuProvider,
Clearable {
    public boolean acceptsPackages = true;
    public String addressFilter = "";
    public PackagePortTarget target;
    public SmartInventory inventory = new SmartInventory(18, this, (slot, stack) -> PackageItem.isPackage(stack));
    protected AnimatedContainerBehaviour<PackagePortMenu> openTracker;
    protected IItemHandler itemHandler = new PackagePortAutomationInventoryWrapper(this.inventory, this);

    public PackagePortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean isBackedUp() {
        for (int i = 0; i < this.inventory.getSlots(); ++i) {
            if (!this.inventory.getStackInSlot(i).isEmpty()) continue;
            return false;
        }
        return true;
    }

    public void filterChanged() {
        if (this.target != null) {
            this.target.deregister(this, (LevelAccessor)this.level, this.worldPosition);
            this.target.register(this, (LevelAccessor)this.level, this.worldPosition);
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.target != null) {
            this.target.register(this, (LevelAccessor)this.level, this.worldPosition);
        }
    }

    public String getFilterString() {
        return this.acceptsPackages ? this.addressFilter : null;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (this.target != null) {
            tag.put("Target", (Tag)CatnipCodecUtils.encode(PackagePortTarget.CODEC, (HolderLookup.Provider)registries, (Object)this.target).orElseThrow());
        }
        tag.putString("AddressFilter", this.addressFilter);
        tag.putBoolean("AcceptsPackages", this.acceptsPackages);
        tag.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        PackagePortTarget prevTarget = this.target;
        this.target = (PackagePortTarget)CatnipCodecUtils.decodeOrNull(PackagePortTarget.CODEC, (HolderLookup.Provider)registries, (Tag)tag.getCompound("Target"));
        this.addressFilter = tag.getString("AddressFilter");
        this.acceptsPackages = tag.getBoolean("AcceptsPackages");
        if (clientPacket && prevTarget != this.target) {
            this.invalidateRenderBoundingBox();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    public void clearContent() {
        this.inventory.clearContent();
    }

    @Override
    public void destroy() {
        if (this.target != null) {
            this.target.deregister(this, (LevelAccessor)this.level, this.worldPosition);
        }
        super.destroy();
        for (int i = 0; i < this.inventory.getSlots(); ++i) {
            this.drop(this.inventory.getStackInSlot(i));
        }
    }

    public void drop(ItemStack box) {
        if (box.isEmpty()) {
            return;
        }
        Block.popResource((Level)this.level, (BlockPos)this.worldPosition, (ItemStack)box);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.openTracker = new AnimatedContainerBehaviour<PackagePortMenu>(this, PackagePortMenu.class);
        behaviours.add(this.openTracker);
        this.openTracker.onOpenChanged(this::onOpenChange);
    }

    protected abstract void onOpenChange(boolean var1);

    public ItemInteractionResult use(Player player) {
        if (player == null || player.isCrouching()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (player instanceof FakePlayer) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        boolean clipboard = AllBlocks.CLIPBOARD.isIn(mainHandItem);
        if (this.level.isClientSide) {
            if (!clipboard) {
                this.onOpenedManually();
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (clipboard) {
            this.addAddressToClipboard(player, mainHandItem);
            return ItemInteractionResult.SUCCESS;
        }
        player.openMenu((MenuProvider)this, this.worldPosition);
        return ItemInteractionResult.SUCCESS;
    }

    protected void onOpenedManually() {
    }

    private void addAddressToClipboard(Player player, ItemStack mainHandItem) {
        if (this.addressFilter == null || this.addressFilter.isBlank()) {
            return;
        }
        ClipboardContent clipboard = (ClipboardContent)mainHandItem.getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, (Object)ClipboardContent.EMPTY);
        List<List<ClipboardEntry>> list = ClipboardEntry.readAll(clipboard);
        for (List<ClipboardEntry> page : list) {
            for (ClipboardEntry entry : page) {
                String existing = entry.text.getString();
                if (!existing.equals("#" + this.addressFilter) && !existing.equals("# " + this.addressFilter)) continue;
                return;
            }
        }
        List<ClipboardEntry> page = null;
        for (List<ClipboardEntry> freePage : list) {
            if (freePage.size() > 11) continue;
            page = freePage;
            break;
        }
        if (page == null) {
            page = new ArrayList<ClipboardEntry>();
            list.add(page);
        }
        page.add(new ClipboardEntry(false, Component.literal((String)("#" + this.addressFilter))));
        player.displayClientMessage((Component)CreateLang.translate("clipboard.address_added", this.addressFilter).component(), true);
        clipboard = clipboard.setPages(list).setType(ClipboardOverrides.ClipboardType.WRITTEN);
        mainHandItem.set(AllDataComponents.CLIPBOARD_CONTENT, (Object)clipboard);
    }

    public Component getDisplayName() {
        return Component.empty();
    }

    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return PackagePortMenu.create(pContainerId, pPlayerInventory, this);
    }

    public int getComparatorOutput() {
        return ItemHandlerHelper.calcRedstoneFromInventory((IItemHandler)this.inventory);
    }
}
