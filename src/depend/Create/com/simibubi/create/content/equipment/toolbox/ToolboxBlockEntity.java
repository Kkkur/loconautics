/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponentMap$Builder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.Nameable
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity$DataComponentInput
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package com.simibubi.create.content.equipment.toolbox;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.content.equipment.toolbox.ToolboxMenu;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.animatedContainer.AnimatedContainerBehaviour;
import com.simibubi.create.foundation.utility.ResetableLazy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ToolboxBlockEntity
extends SmartBlockEntity
implements MenuProvider,
Nameable {
    public LerpedFloat lid = LerpedFloat.linear().startWithValue(0.0);
    public LerpedFloat drawers = LerpedFloat.linear().startWithValue(0.0);
    UUID uniqueId;
    ToolboxInventory inventory;
    ResetableLazy<DyeColor> colorProvider;
    Map<Integer, WeakHashMap<Player, Integer>> connectedPlayers = new HashMap<Integer, WeakHashMap<Player, Integer>>();
    private Component customName;
    private AnimatedContainerBehaviour<ToolboxMenu> openTracker;

    public ToolboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new ToolboxInventory(this);
        this.colorProvider = ResetableLazy.of(() -> {
            BlockState blockState = this.getBlockState();
            if (blockState != null && blockState.getBlock() instanceof ToolboxBlock) {
                return ((ToolboxBlock)blockState.getBlock()).getColor();
            }
            return DyeColor.BROWN;
        });
        this.setLazyTickRate(10);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.TOOLBOX.get(), (be, context) -> be.inventory);
    }

    public DyeColor getColor() {
        return this.colorProvider.get();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.openTracker = new AnimatedContainerBehaviour<ToolboxMenu>(this, ToolboxMenu.class);
        behaviours.add(this.openTracker);
    }

    @Override
    public void initialize() {
        super.initialize();
        ToolboxHandler.onLoad(this);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        ToolboxHandler.onUnload(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.tickAudio();
        }
        if (!this.level.isClientSide) {
            this.tickPlayers();
        }
        this.lid.chase(this.openTracker.openCount > 0 ? 1.0 : 0.0, (double)0.2f, LerpedFloat.Chaser.LINEAR);
        this.drawers.chase(this.openTracker.openCount > 0 ? 1.0 : 0.0, (double)0.2f, LerpedFloat.Chaser.EXP);
        this.lid.tickChaser();
        this.drawers.tickChaser();
    }

    private void tickPlayers() {
        boolean update = false;
        Iterator<Map.Entry<Integer, WeakHashMap<Player, Integer>>> toolboxSlots = this.connectedPlayers.entrySet().iterator();
        while (toolboxSlots.hasNext()) {
            Map.Entry<Integer, WeakHashMap<Player, Integer>> toolboxSlotEntry = toolboxSlots.next();
            WeakHashMap<Player, Integer> set = toolboxSlotEntry.getValue();
            int slot = toolboxSlotEntry.getKey();
            ItemStack referenceItem = this.inventory.filters.get(slot);
            boolean clear = referenceItem.isEmpty();
            Iterator<Map.Entry<Player, Integer>> playerEntries = set.entrySet().iterator();
            while (playerEntries.hasNext()) {
                int deposited;
                int targetAmount;
                Map.Entry<Player, Integer> playerEntry = playerEntries.next();
                Player player = playerEntry.getKey();
                int hotbarSlot = playerEntry.getValue();
                if (!clear && !ToolboxHandler.withinRange(player, this)) continue;
                Inventory playerInv = player.getInventory();
                ItemStack playerStack = playerInv.getItem(hotbarSlot);
                if (clear || !playerStack.isEmpty() && !ToolboxInventory.canItemsShareCompartment(playerStack, referenceItem)) {
                    player.getPersistentData().getCompound("CreateToolboxData").remove(String.valueOf(hotbarSlot));
                    playerEntries.remove();
                    if (!(player instanceof ServerPlayer)) continue;
                    ToolboxHandler.syncData(player);
                    continue;
                }
                int count = playerStack.getCount();
                if (count < (targetAmount = (referenceItem.getMaxStackSize() + 1) / 2)) {
                    ItemStack extracted;
                    int amountToReplenish = targetAmount - count;
                    if (this.isOpenInContainer(player) && !(extracted = this.inventory.takeFromCompartment(amountToReplenish, slot, true)).isEmpty()) {
                        ToolboxHandler.unequip(player, hotbarSlot, false);
                        ToolboxHandler.syncData(player);
                        continue;
                    }
                    extracted = this.inventory.takeFromCompartment(amountToReplenish, slot, false);
                    if (!extracted.isEmpty()) {
                        update = true;
                        ItemStack template = playerStack.isEmpty() ? extracted : playerStack;
                        playerInv.setItem(hotbarSlot, template.copyWithCount(count + extracted.getCount()));
                    }
                }
                if (count <= targetAmount) continue;
                int amountToDeposit = count - targetAmount;
                ItemStack toDistribute = playerStack.copyWithCount(amountToDeposit);
                if (this.isOpenInContainer(player) && (deposited = amountToDeposit - this.inventory.distributeToCompartment(toDistribute, slot, true).getCount()) > 0) {
                    ToolboxHandler.unequip(player, hotbarSlot, true);
                    ToolboxHandler.syncData(player);
                    continue;
                }
                int deposited2 = amountToDeposit - this.inventory.distributeToCompartment(toDistribute, slot, false).getCount();
                if (deposited2 <= 0) continue;
                update = true;
                playerInv.setItem(hotbarSlot, playerStack.copyWithCount(count - deposited2));
            }
            if (!clear) continue;
            toolboxSlots.remove();
        }
        if (update) {
            this.sendData();
        }
    }

    private boolean isOpenInContainer(Player player) {
        return player.containerMenu instanceof ToolboxMenu && ((ToolboxMenu)player.containerMenu).contentHolder == this;
    }

    public void unequipTracked() {
        if (this.level.isClientSide) {
            return;
        }
        HashSet<ServerPlayer> affected = new HashSet<ServerPlayer>();
        for (Map.Entry<Integer, WeakHashMap<Player, Integer>> toolboxSlotEntry : this.connectedPlayers.entrySet()) {
            WeakHashMap<Player, Integer> set = toolboxSlotEntry.getValue();
            for (Map.Entry<Player, Integer> playerEntry : set.entrySet()) {
                Player player = playerEntry.getKey();
                int hotbarSlot = playerEntry.getValue();
                ToolboxHandler.unequip(player, hotbarSlot, false);
                if (!(player instanceof ServerPlayer)) continue;
                affected.add((ServerPlayer)player);
            }
        }
        for (ServerPlayer player : affected) {
            ToolboxHandler.syncData((Player)player);
        }
        this.connectedPlayers.clear();
    }

    public void unequip(int slot, Player player, int hotbarSlot, boolean keepItems) {
        if (!this.connectedPlayers.containsKey(slot)) {
            return;
        }
        this.connectedPlayers.get(slot).remove(player);
        if (keepItems) {
            return;
        }
        Inventory playerInv = player.getInventory();
        ItemStack playerStack = playerInv.getItem(hotbarSlot);
        ItemStack toInsert = ToolboxInventory.cleanItemNBT(playerStack.copy());
        ItemStack remainder = this.inventory.distributeToCompartment(toInsert, slot, false);
        if (remainder.getCount() != toInsert.getCount()) {
            playerInv.setItem(hotbarSlot, remainder);
        }
    }

    private void tickAudio() {
        Vec3 vec = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        if (this.lid.settled()) {
            if (this.openTracker.openCount > 0 && this.lid.getChaseTarget() == 0.0f) {
                this.level.playLocalSound(vec.x, vec.y, vec.z, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 0.25f, this.level.random.nextFloat() * 0.1f + 1.2f, true);
                this.level.playLocalSound(vec.x, vec.y, vec.z, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.1f, this.level.random.nextFloat() * 0.1f + 1.1f, true);
            }
            if (this.openTracker.openCount == 0 && this.lid.getChaseTarget() == 1.0f) {
                this.level.playLocalSound(vec.x, vec.y, vec.z, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.1f, this.level.random.nextFloat() * 0.1f + 1.1f, true);
            }
        } else if (this.openTracker.openCount == 0 && this.lid.getChaseTarget() == 0.0f && this.lid.getValue(0.0f) > 0.0625f && this.lid.getValue(1.0f) < 0.0625f) {
            this.level.playLocalSound(vec.x, vec.y, vec.z, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.25f, this.level.random.nextFloat() * 0.1f + 1.2f, true);
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
        super.read(compound, registries, clientPacket);
        if (compound.contains("UniqueId", 11)) {
            this.uniqueId = compound.getUUID("UniqueId");
        }
        if (compound.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson((String)compound.getString("CustomName"), (HolderLookup.Provider)registries);
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.uniqueId == null) {
            this.uniqueId = UUID.randomUUID();
        }
        compound.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
        compound.putUUID("UniqueId", this.uniqueId);
        if (this.customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson((Component)this.customName, (HolderLookup.Provider)registries));
        }
        super.write(compound, registries, clientPacket);
    }

    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return ToolboxMenu.create(id, inv, this);
    }

    @Override
    public void lazyTick() {
        ToolboxHandler.onLoad(this);
        super.lazyTick();
    }

    public void connectPlayer(int slot, Player player, int hotbarSlot) {
        if (this.level.isClientSide) {
            return;
        }
        WeakHashMap map = this.connectedPlayers.computeIfAbsent(slot, WeakHashMap::new);
        Integer previous = (Integer)map.get(player);
        if (previous != null) {
            if (previous == hotbarSlot) {
                return;
            }
            ToolboxHandler.unequip(player, previous, false);
        }
        map.put(player, hotbarSlot);
    }

    public void readInventory(ToolboxInventory inv) {
        if (inv != null) {
            this.inventory.filters = new ArrayList<ItemStack>(inv.filters);
            for (int i = 0; i < inv.getSlots(); ++i) {
                this.inventory.setStackInSlot(i, inv.getStackInSlot(i));
            }
        }
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public boolean isFullyInitialized() {
        return this.uniqueId != null;
    }

    public void setCustomName(Component customName) {
        this.customName = customName;
    }

    public Component getDisplayName() {
        return this.customName != null ? this.customName : ((ToolboxBlock)AllBlocks.TOOLBOXES.get(this.getColor()).get()).getName();
    }

    public Component getCustomName() {
        return this.customName;
    }

    public boolean hasCustomName() {
        return this.customName != null;
    }

    public Component getName() {
        return this.customName;
    }

    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        this.colorProvider.reset();
    }

    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        this.setUniqueId((UUID)componentInput.get(AllDataComponents.TOOLBOX_UUID));
        this.readInventory((ToolboxInventory)((Object)componentInput.get(AllDataComponents.TOOLBOX_INVENTORY)));
    }

    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        components.set(AllDataComponents.TOOLBOX_UUID, (Object)this.uniqueId);
        components.set(AllDataComponents.TOOLBOX_INVENTORY, (Object)this.inventory);
    }
}
