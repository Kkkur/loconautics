/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.CatnipCodecUtils
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponentMap$Builder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntity$DataComponentInput
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.piston.PistonHeadBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BedPart
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.wrapper.EmptyItemHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.schematics.cannon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.schematics.SchematicPrinter;
import com.simibubi.create.content.schematics.cannon.ConfigureSchematicannonPacket;
import com.simibubi.create.content.schematics.cannon.LaunchedItem;
import com.simibubi.create.content.schematics.cannon.MaterialChecklist;
import com.simibubi.create.content.schematics.cannon.SchematicannonInventory;
import com.simibubi.create.content.schematics.cannon.SchematicannonMenu;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.mixin.accessor.ItemStackHandlerAccessor;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CSchematics;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import org.jetbrains.annotations.Nullable;

public class SchematicannonBlockEntity
extends SmartBlockEntity
implements MenuProvider,
Clearable {
    public static final int NEIGHBOUR_CHECKING = 100;
    public static final int MAX_ANCHOR_DISTANCE = 256;
    public SchematicannonInventory inventory;
    public boolean sendUpdate;
    public boolean dontUpdateChecklist;
    public int neighbourCheckCooldown;
    public SchematicPrinter printer;
    public ItemStack missingItem;
    public boolean positionNotLoaded;
    public boolean hasCreativeCrate;
    private int printerCooldown;
    private int skipsLeft;
    private boolean blockSkipped;
    public BlockPos previousTarget;
    public LinkedHashSet<IItemHandler> attachedInventories;
    public List<LaunchedItem> flyingBlocks;
    public MaterialChecklist checklist;
    public int remainingFuel;
    public float bookPrintingProgress;
    public float schematicProgress;
    public String statusMsg;
    public State state;
    public int blocksPlaced;
    public int blocksToPlace;
    public int replaceMode;
    public boolean skipMissing;
    public boolean replaceBlockEntities;
    public boolean firstRenderTick;
    public float defaultYaw;

    public SchematicannonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(30);
        this.attachedInventories = new LinkedHashSet();
        this.flyingBlocks = new LinkedList<LaunchedItem>();
        this.inventory = new SchematicannonInventory(this);
        this.statusMsg = "idle";
        this.state = State.STOPPED;
        this.replaceMode = 2;
        this.checklist = new MaterialChecklist();
        this.printer = new SchematicPrinter();
    }

    public void findInventories() {
        this.hasCreativeCrate = false;
        this.attachedInventories.clear();
        for (Direction facing : Iterate.directions) {
            IItemHandler capability;
            BlockEntity blockEntity;
            if (!this.level.isLoaded(this.worldPosition.relative(facing))) continue;
            if (AllBlocks.CREATIVE_CRATE.has(this.level.getBlockState(this.worldPosition.relative(facing)))) {
                this.hasCreativeCrate = true;
            }
            if ((blockEntity = this.level.getBlockEntity(this.worldPosition.relative(facing))) == null || (capability = (IItemHandler)this.level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), (Object)facing.getOpposite())) == null) continue;
            this.attachedInventories.add(capability);
        }
    }

    public void clearContent() {
        ((ItemStackHandlerAccessor)((Object)this.inventory)).create$getStacks().clear();
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (!clientPacket) {
            this.inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
        }
        this.statusMsg = compound.getString("Status");
        this.schematicProgress = compound.getFloat("Progress");
        this.bookPrintingProgress = compound.getFloat("PaperProgress");
        this.remainingFuel = compound.getInt("RemainingFuel");
        String stateString = compound.getString("State");
        this.state = stateString.isEmpty() ? State.STOPPED : State.valueOf(compound.getString("State"));
        this.blocksPlaced = compound.getInt("AmountPlaced");
        this.blocksToPlace = compound.getInt("AmountToPlace");
        this.missingItem = null;
        if (compound.contains("MissingItem")) {
            ItemStack.parse((HolderLookup.Provider)registries, (Tag)compound.getCompound("MissingItem")).ifPresent(i -> {
                this.missingItem = i;
            });
        }
        SchematicannonOptions options = CatnipCodecUtils.decode(SchematicannonOptions.CODEC, (HolderLookup.Provider)registries, (Tag)compound.getCompound("Options")).orElse(new SchematicannonOptions(2, false, false));
        this.replaceMode = options.replaceMode;
        this.skipMissing = options.skipMissing;
        this.replaceBlockEntities = options.replaceBlockEntities;
        if (compound.contains("Printer")) {
            this.printer.fromTag(compound.getCompound("Printer"), clientPacket);
        }
        if (compound.contains("FlyingBlocks")) {
            this.readFlyingBlocks(compound, registries);
        }
        this.defaultYaw = compound.getFloat("DefaultYaw");
        super.read(compound, registries, clientPacket);
    }

    protected void readFlyingBlocks(CompoundTag compound, HolderLookup.Provider registries) {
        ListTag tagBlocks = compound.getList("FlyingBlocks", 10);
        if (tagBlocks.isEmpty()) {
            this.flyingBlocks.clear();
        }
        boolean pastDead = false;
        for (int i = 0; i < tagBlocks.size(); ++i) {
            CompoundTag c = tagBlocks.getCompound(i);
            LaunchedItem launched = LaunchedItem.fromNBT(c, registries, this.blockHolderGetter());
            BlockPos readBlockPos = launched.target;
            if (this.level == null || !this.level.isClientSide) {
                this.flyingBlocks.add(launched);
                continue;
            }
            while (!(pastDead || this.flyingBlocks.isEmpty() || this.flyingBlocks.get((int)0).target.equals((Object)readBlockPos))) {
                this.flyingBlocks.remove(0);
            }
            pastDead = true;
            if (i < this.flyingBlocks.size()) continue;
            this.flyingBlocks.add(launched);
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (!clientPacket) {
            compound.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
            if (this.state == State.RUNNING) {
                compound.putBoolean("Running", true);
            }
        }
        compound.putFloat("Progress", this.schematicProgress);
        compound.putFloat("PaperProgress", this.bookPrintingProgress);
        compound.putInt("RemainingFuel", this.remainingFuel);
        compound.putString("Status", this.statusMsg);
        compound.putString("State", this.state.name());
        compound.putInt("AmountPlaced", this.blocksPlaced);
        compound.putInt("AmountToPlace", this.blocksToPlace);
        if (this.missingItem != null) {
            compound.put("MissingItem", this.missingItem.saveOptional(registries));
        }
        Tag options = (Tag)CatnipCodecUtils.encode(SchematicannonOptions.CODEC, (HolderLookup.Provider)registries, (Object)new SchematicannonOptions(this.replaceMode, this.skipMissing, this.replaceBlockEntities)).orElseThrow();
        compound.put("Options", options);
        CompoundTag printerData = new CompoundTag();
        this.printer.write(printerData);
        compound.put("Printer", (Tag)printerData);
        ListTag tagFlyingBlocks = new ListTag();
        for (LaunchedItem b : this.flyingBlocks) {
            tagFlyingBlocks.add((Object)b.serializeNBT(registries));
        }
        compound.put("FlyingBlocks", (Tag)tagFlyingBlocks);
        compound.putFloat("DefaultYaw", this.defaultYaw);
        super.write(compound, registries, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.state != State.STOPPED && this.neighbourCheckCooldown-- <= 0) {
            this.neighbourCheckCooldown = 100;
            this.findInventories();
        }
        this.firstRenderTick = true;
        this.previousTarget = this.printer.getCurrentTarget();
        this.tickFlyingBlocks();
        if (this.level.isClientSide) {
            return;
        }
        this.tickPaperPrinter();
        this.refillFuelIfPossible();
        this.skipsLeft = 1000;
        this.blockSkipped = true;
        while (this.blockSkipped && this.skipsLeft-- > 0) {
            this.tickPrinter();
        }
        this.schematicProgress = 0.0f;
        if (this.blocksToPlace > 0) {
            this.schematicProgress = (float)this.blocksPlaced / (float)this.blocksToPlace;
        }
        if (this.sendUpdate) {
            this.sendUpdate = false;
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 6);
        }
    }

    public CSchematics config() {
        return AllConfigs.server().schematics;
    }

    protected void tickPrinter() {
        ItemRequirement requirement;
        ItemStack blueprint = this.inventory.getStackInSlot(0);
        this.blockSkipped = false;
        if (blueprint.isEmpty() && !this.statusMsg.equals("idle") && this.inventory.getStackInSlot(1).isEmpty()) {
            this.state = State.STOPPED;
            this.statusMsg = "idle";
            this.sendUpdate = true;
            return;
        }
        if (this.state == State.STOPPED) {
            if (this.printer.isLoaded()) {
                this.resetPrinter();
            }
            return;
        }
        if (this.state == State.PAUSED && !this.positionNotLoaded && this.missingItem == null && this.remainingFuel > 0) {
            return;
        }
        if (!this.printer.isLoaded()) {
            this.initializePrinter(blueprint);
            return;
        }
        if (this.printerCooldown > 0) {
            --this.printerCooldown;
            return;
        }
        if (this.remainingFuel <= 0 && !this.hasCreativeCrate) {
            this.refillFuelIfPossible();
            if (this.remainingFuel <= 0) {
                this.state = State.PAUSED;
                this.statusMsg = "noGunpowder";
                this.sendUpdate = true;
                return;
            }
        }
        if (this.hasCreativeCrate) {
            this.remainingFuel = 0;
            if (this.missingItem != null) {
                this.missingItem = null;
                this.state = State.RUNNING;
            }
        }
        if (this.missingItem == null && !this.positionNotLoaded) {
            if (!this.printer.advanceCurrentPos()) {
                this.finishedPrinting();
                return;
            }
            this.sendUpdate = true;
        }
        if (!this.getLevel().isLoaded(this.printer.getCurrentTarget())) {
            this.positionNotLoaded = true;
            this.statusMsg = "targetNotLoaded";
            this.state = State.PAUSED;
            return;
        }
        if (this.positionNotLoaded) {
            this.positionNotLoaded = false;
            this.state = State.RUNNING;
        }
        if ((requirement = this.printer.getCurrentRequirement()).isInvalid() || !this.printer.shouldPlaceCurrent(this.level, this::shouldPlace)) {
            this.sendUpdate = !this.statusMsg.equals("searching");
            this.statusMsg = "searching";
            this.blockSkipped = true;
            return;
        }
        List<ItemRequirement.StackRequirement> requiredItems = requirement.getRequiredItems();
        if (!requirement.isEmpty()) {
            for (ItemRequirement.StackRequirement required : requiredItems) {
                if (this.grabItemsFromAttachedInventories(required, true)) continue;
                if (this.skipMissing) {
                    this.statusMsg = "skipping";
                    this.blockSkipped = true;
                    if (this.missingItem != null) {
                        this.missingItem = null;
                        this.state = State.RUNNING;
                    }
                    return;
                }
                this.missingItem = required.stack;
                this.state = State.PAUSED;
                this.statusMsg = "missingBlock";
                return;
            }
            for (ItemRequirement.StackRequirement required : requiredItems) {
                this.grabItemsFromAttachedInventories(required, false);
            }
        }
        this.state = State.RUNNING;
        ItemStack icon = requirement.isEmpty() || requiredItems.isEmpty() ? ItemStack.EMPTY : requiredItems.get((int)0).stack;
        this.printer.handleCurrentTarget((target, blockState, blockEntity) -> {
            this.statusMsg = blockState.getBlock() != Blocks.AIR ? "placing" : "clearing";
            this.launchBlockOrBelt(target, icon, blockState, blockEntity);
        }, (target, entity) -> {
            this.statusMsg = "placing";
            this.launchEntity(target, icon, entity);
        });
        this.printerCooldown = (Integer)this.config().schematicannonDelay.get();
        --this.remainingFuel;
        this.sendUpdate = true;
        this.missingItem = null;
    }

    public int getShotsPerGunpowder() {
        return this.hasCreativeCrate ? 0 : (Integer)this.config().schematicannonShotsPerGunpowder.get();
    }

    protected void initializePrinter(ItemStack blueprint) {
        if (!blueprint.has(AllDataComponents.SCHEMATIC_ANCHOR)) {
            this.state = State.STOPPED;
            this.statusMsg = "schematicInvalid";
            this.sendUpdate = true;
            return;
        }
        if (!((Boolean)blueprint.getOrDefault(AllDataComponents.SCHEMATIC_DEPLOYED, (Object)false)).booleanValue()) {
            this.state = State.STOPPED;
            this.statusMsg = "schematicNotPlaced";
            this.sendUpdate = true;
            return;
        }
        this.printer.loadSchematic(blueprint, this.level, true);
        if (this.printer.isErrored()) {
            this.state = State.STOPPED;
            this.statusMsg = "schematicErrored";
            this.inventory.setStackInSlot(0, ItemStack.EMPTY);
            this.inventory.setStackInSlot(1, new ItemStack((ItemLike)AllItems.EMPTY_SCHEMATIC.get()));
            this.printer.resetSchematic();
            this.sendUpdate = true;
            return;
        }
        if (this.printer.isWorldEmpty()) {
            this.state = State.STOPPED;
            this.statusMsg = "schematicExpired";
            this.inventory.setStackInSlot(0, ItemStack.EMPTY);
            this.inventory.setStackInSlot(1, new ItemStack((ItemLike)AllItems.EMPTY_SCHEMATIC.get()));
            this.printer.resetSchematic();
            this.sendUpdate = true;
            return;
        }
        if (!this.printer.getAnchor().closerThan((Vec3i)this.getBlockPos(), 256.0)) {
            this.state = State.STOPPED;
            this.statusMsg = "targetOutsideRange";
            this.printer.resetSchematic();
            this.sendUpdate = true;
            return;
        }
        this.state = State.PAUSED;
        this.statusMsg = "ready";
        this.updateChecklist();
        this.sendUpdate = true;
        this.blocksToPlace += this.blocksPlaced;
    }

    protected ItemStack getItemForBlock(BlockState blockState) {
        Item item = BlockItem.BY_BLOCK.getOrDefault(blockState.getBlock(), Items.AIR);
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack((ItemLike)item);
    }

    protected boolean grabItemsFromAttachedInventories(ItemRequirement.StackRequirement required, boolean simulate) {
        if (this.hasCreativeCrate) {
            return true;
        }
        this.attachedInventories.removeIf(Objects::isNull);
        ItemRequirement.ItemUseType usage = required.usage;
        if (usage == ItemRequirement.ItemUseType.DAMAGE) {
            for (IItemHandler cap : this.attachedInventories) {
                if (cap == null) {
                    cap = EmptyItemHandler.INSTANCE;
                }
                for (int slot = 0; slot < cap.getSlots(); ++slot) {
                    ItemStack extractItem = cap.extractItem(slot, 1, true);
                    if (!required.matches(extractItem) || !extractItem.isDamageableItem()) continue;
                    if (!simulate) {
                        ItemStack stack = cap.extractItem(slot, 1, false);
                        stack.setDamageValue(stack.getDamageValue() + 1);
                        if (stack.getDamageValue() <= stack.getMaxDamage()) {
                            if (cap.getStackInSlot(slot).isEmpty()) {
                                cap.insertItem(slot, stack, false);
                            } else {
                                ItemHandlerHelper.insertItem((IItemHandler)cap, (ItemStack)stack, (boolean)false);
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        boolean success = false;
        int amountFound = 0;
        for (IItemHandler cap : this.attachedInventories) {
            if (cap == null) {
                cap = EmptyItemHandler.INSTANCE;
            }
            if ((amountFound += ItemHelper.extract(cap, required::matches, ItemHelper.ExtractionCountMode.UPTO, required.stack.getCount(), true).getCount()) < required.stack.getCount()) continue;
            success = true;
            break;
        }
        if (!simulate && success) {
            amountFound = 0;
            for (IItemHandler cap : this.attachedInventories) {
                if (cap == null) {
                    cap = EmptyItemHandler.INSTANCE;
                }
                if ((amountFound += ItemHelper.extract(cap, required::matches, ItemHelper.ExtractionCountMode.UPTO, required.stack.getCount(), false).getCount()) < required.stack.getCount()) continue;
            }
        }
        return success;
    }

    public void finishedPrinting() {
        if (this.replaceMode == ConfigureSchematicannonPacket.Option.REPLACE_EMPTY.ordinal()) {
            this.printer.sendBlockUpdates(this.level);
        }
        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
        this.inventory.setStackInSlot(1, new ItemStack((ItemLike)AllItems.EMPTY_SCHEMATIC.get(), this.inventory.getStackInSlot(1).getCount() + 1));
        this.state = State.STOPPED;
        this.statusMsg = "finished";
        this.resetPrinter();
        AllSoundEvents.SCHEMATICANNON_FINISH.playOnServer(this.level, (Vec3i)this.worldPosition);
        this.sendUpdate = true;
    }

    protected void resetPrinter() {
        this.printer.resetSchematic();
        this.missingItem = null;
        this.sendUpdate = true;
        this.schematicProgress = 0.0f;
        this.blocksPlaced = 0;
        this.blocksToPlace = 0;
    }

    protected boolean shouldPlace(BlockPos pos, BlockState state, BlockEntity be, BlockState toReplace, BlockState toReplaceOther, boolean isNormalCube) {
        if (pos.closerThan((Vec3i)this.getBlockPos(), 2.0)) {
            return false;
        }
        if (!this.replaceBlockEntities && (toReplace.hasBlockEntity() || toReplaceOther != null && toReplaceOther.hasBlockEntity())) {
            return false;
        }
        if (this.shouldIgnoreBlockState(state, be)) {
            return false;
        }
        boolean placingAir = state.isAir();
        if (this.replaceMode == 3) {
            return true;
        }
        if (this.replaceMode == 2 && !placingAir) {
            return true;
        }
        if (!(this.replaceMode != 1 || !isNormalCube && (toReplace.isRedstoneConductor((BlockGetter)this.level, pos) || toReplaceOther != null && toReplaceOther.isRedstoneConductor((BlockGetter)this.level, pos)) || placingAir)) {
            return true;
        }
        return this.replaceMode == 0 && !toReplace.isRedstoneConductor((BlockGetter)this.level, pos) && (toReplaceOther == null || !toReplaceOther.isRedstoneConductor((BlockGetter)this.level, pos)) && !placingAir;
    }

    protected boolean shouldIgnoreBlockState(BlockState state, BlockEntity be) {
        if (state.getBlock() == Blocks.STRUCTURE_VOID) {
            return true;
        }
        ItemRequirement requirement = ItemRequirement.of(state, be);
        if (requirement.isEmpty()) {
            return false;
        }
        if (requirement.isInvalid()) {
            return false;
        }
        if (state.hasProperty((Property)BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue((Property)BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            return true;
        }
        if (state.hasProperty((Property)BlockStateProperties.BED_PART) && state.getValue((Property)BlockStateProperties.BED_PART) == BedPart.HEAD) {
            return true;
        }
        if (state.getBlock() instanceof PistonHeadBlock) {
            return true;
        }
        if (AllBlocks.BELT.has(state)) {
            return state.getValue(BeltBlock.PART) == BeltPart.MIDDLE;
        }
        return false;
    }

    protected void tickFlyingBlocks() {
        LinkedList<LaunchedItem> toRemove = new LinkedList<LaunchedItem>();
        for (LaunchedItem b : this.flyingBlocks) {
            if (!b.update(this.level)) continue;
            toRemove.add(b);
        }
        this.flyingBlocks.removeAll(toRemove);
    }

    protected void refillFuelIfPossible() {
        if (this.hasCreativeCrate) {
            return;
        }
        if (this.remainingFuel > this.getShotsPerGunpowder()) {
            this.remainingFuel = this.getShotsPerGunpowder();
            this.sendUpdate = true;
            return;
        }
        if (this.remainingFuel > 0) {
            return;
        }
        if (!this.inventory.getStackInSlot(4).isEmpty()) {
            this.inventory.getStackInSlot(4).shrink(1);
        } else {
            boolean externalGunpowderFound = false;
            for (IItemHandler cap : this.attachedInventories) {
                IItemHandler itemHandler = cap;
                if (itemHandler == null) {
                    itemHandler = EmptyItemHandler.INSTANCE;
                }
                if (ItemHelper.extract(itemHandler, stack -> this.inventory.isItemValid(4, (ItemStack)stack), 1, false).isEmpty()) continue;
                externalGunpowderFound = true;
                break;
            }
            if (!externalGunpowderFound) {
                return;
            }
        }
        this.remainingFuel += this.getShotsPerGunpowder();
        if (this.statusMsg.equals("noGunpowder")) {
            if (this.blocksPlaced > 0) {
                this.state = State.RUNNING;
            }
            this.statusMsg = "ready";
        }
        this.sendUpdate = true;
    }

    protected void tickPaperPrinter() {
        boolean outputFull;
        int BookInput = 2;
        int BookOutput = 3;
        ItemStack blueprint = this.inventory.getStackInSlot(0);
        ItemStack paper = this.inventory.extractItem(BookInput, 1, true);
        boolean bl = outputFull = this.inventory.getStackInSlot(BookOutput).getCount() == this.inventory.getSlotLimit(BookOutput);
        if (this.printer.isErrored()) {
            return;
        }
        if (!this.printer.isLoaded()) {
            if (!blueprint.isEmpty()) {
                this.initializePrinter(blueprint);
            }
            return;
        }
        if (paper.isEmpty() || outputFull) {
            if (this.bookPrintingProgress != 0.0f) {
                this.sendUpdate = true;
            }
            this.bookPrintingProgress = 0.0f;
            this.dontUpdateChecklist = false;
            return;
        }
        if (this.bookPrintingProgress >= 1.0f) {
            this.bookPrintingProgress = 0.0f;
            if (!this.dontUpdateChecklist) {
                this.updateChecklist();
            }
            this.dontUpdateChecklist = true;
            ItemStack extractItem = this.inventory.extractItem(BookInput, 1, false);
            ItemStack stack = AllBlocks.CLIPBOARD.isIn(extractItem) ? this.checklist.createWrittenClipboard() : this.checklist.createWrittenBook();
            stack.setCount(this.inventory.getStackInSlot(BookOutput).getCount() + 1);
            this.inventory.setStackInSlot(BookOutput, stack);
            this.sendUpdate = true;
            return;
        }
        this.bookPrintingProgress += 0.05f;
        this.sendUpdate = true;
    }

    public static BlockState stripBeltIfNotLast(BlockState blockState) {
        BeltPart part = (BeltPart)((Object)blockState.getValue(BeltBlock.PART));
        if (part == BeltPart.MIDDLE) {
            return Blocks.AIR.defaultBlockState();
        }
        boolean isLastSegment = false;
        Direction facing = (Direction)blockState.getValue(BeltBlock.HORIZONTAL_FACING);
        BeltSlope slope = (BeltSlope)((Object)blockState.getValue(BeltBlock.SLOPE));
        boolean positive = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE;
        boolean start = part == BeltPart.START;
        boolean end = part == BeltPart.END;
        switch (slope) {
            case DOWNWARD: {
                isLastSegment = start;
                break;
            }
            case UPWARD: {
                isLastSegment = end;
                break;
            }
            default: {
                boolean bl = isLastSegment = positive && end || !positive && start;
            }
        }
        if (isLastSegment) {
            return blockState;
        }
        return (BlockState)AllBlocks.SHAFT.getDefaultState().setValue((Property)AbstractSimpleShaftBlock.AXIS, (Comparable)(slope == BeltSlope.SIDEWAYS ? Direction.Axis.Y : facing.getClockWise().getAxis()));
    }

    /*
     * Unable to fully structure code
     */
    protected void launchBlockOrBelt(BlockPos target, ItemStack icon, BlockState blockState, BlockEntity blockEntity) {
        block4: {
            if (!AllBlocks.BELT.has(blockState)) break block4;
            blockState = SchematicannonBlockEntity.stripBeltIfNotLast(blockState);
            if (!(blockEntity instanceof BeltBlockEntity)) ** GOTO lbl-1000
            bbe = (BeltBlockEntity)blockEntity;
            if (AllBlocks.BELT.has(blockState)) {
                casings = new BeltBlockEntity.CasingType[bbe.beltLength];
                Arrays.fill((Object[])casings, (Object)BeltBlockEntity.CasingType.NONE);
                currentPos = target;
                for (i = 0; i < bbe.beltLength && (currentState = bbe.getLevel().getBlockState(currentPos)).getBlock() instanceof BeltBlock && (var11_12 = bbe.getLevel().getBlockEntity(currentPos)) instanceof BeltBlockEntity; ++i) {
                    beltAtSegment = (BeltBlockEntity)var11_12;
                    casings[i] = beltAtSegment.casing;
                    currentPos = BeltBlock.nextSegmentPosition(currentState, currentPos, blockState.getValue(BeltBlock.PART) != BeltPart.END);
                }
                this.launchBelt(target, blockState, bbe.beltLength, casings);
            } else if (blockState != Blocks.AIR.defaultBlockState()) {
                this.launchBlock(target, icon, blockState, null);
            }
            return;
        }
        data = BlockHelper.prepareBlockEntityData(this.level, blockState, blockEntity);
        this.launchBlock(target, icon, blockState, data);
    }

    protected void launchBelt(BlockPos target, BlockState state, int length, BeltBlockEntity.CasingType[] casings) {
        ++this.blocksPlaced;
        ItemStack connector = AllItems.BELT_CONNECTOR.asStack();
        this.flyingBlocks.add(new LaunchedItem.ForBelt(this.getBlockPos(), target, connector, state, casings));
        this.playFiringSound();
    }

    protected void launchBlock(BlockPos target, ItemStack stack, BlockState state, @Nullable CompoundTag data) {
        if (!state.isAir()) {
            ++this.blocksPlaced;
        }
        this.flyingBlocks.add(new LaunchedItem.ForBlockState(this.getBlockPos(), target, stack, state, data));
        this.playFiringSound();
    }

    protected void launchEntity(BlockPos target, ItemStack stack, Entity entity) {
        ++this.blocksPlaced;
        this.flyingBlocks.add(new LaunchedItem.ForEntity(this.getBlockPos(), target, stack, entity));
        this.playFiringSound();
    }

    public void playFiringSound() {
        AllSoundEvents.SCHEMATICANNON_LAUNCH_BLOCK.playOnServer(this.level, (Vec3i)this.worldPosition);
    }

    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return SchematicannonMenu.create(id, inv, this);
    }

    public Component getDisplayName() {
        return CreateLang.translateDirect("gui.schematicannon.title", new Object[0]);
    }

    public void updateChecklist() {
        this.checklist.required.clear();
        this.checklist.damageRequired.clear();
        this.checklist.blocksNotLoaded = false;
        if (this.printer.isLoaded() && !this.printer.isErrored()) {
            this.blocksToPlace = this.blocksPlaced;
            this.blocksToPlace += this.printer.markAllBlockRequirements(this.checklist, this.level, this::shouldPlace);
            this.printer.markAllEntityRequirements(this.checklist);
        }
        this.checklist.gathered.clear();
        this.findInventories();
        for (IItemHandler cap : this.attachedInventories) {
            if (cap == null) continue;
            for (int slot = 0; slot < cap.getSlots(); ++slot) {
                ItemStack stackInSlot = cap.getStackInSlot(slot);
                if (cap.extractItem(slot, 1, true).isEmpty()) continue;
                this.checklist.collect(stackInSlot);
            }
        }
        this.sendUpdate = true;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        this.findInventories();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return AABB.INFINITE;
    }

    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        SchematicannonOptions options = (SchematicannonOptions)componentInput.getOrDefault(AllDataComponents.SCHEMATICANNON_OPTIONS, (Object)new SchematicannonOptions(2, true, false));
        this.replaceMode = options.replaceMode;
        this.skipMissing = options.skipMissing;
        this.replaceBlockEntities = options.replaceBlockEntities;
    }

    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        components.set(AllDataComponents.SCHEMATICANNON_OPTIONS, (Object)new SchematicannonOptions(this.replaceMode, this.skipMissing, this.replaceBlockEntities));
    }

    public static enum State {
        STOPPED,
        PAUSED,
        RUNNING;

    }

    public record SchematicannonOptions(int replaceMode, boolean skipMissing, boolean replaceBlockEntities) {
        public static final Codec<SchematicannonOptions> CODEC = RecordCodecBuilder.create(i -> i.group((App)Codec.INT.fieldOf("replace_mode").forGetter(SchematicannonOptions::replaceMode), (App)Codec.BOOL.fieldOf("skip_missing").forGetter(SchematicannonOptions::skipMissing), (App)Codec.BOOL.fieldOf("replace_block_entities").forGetter(SchematicannonOptions::replaceBlockEntities)).apply((Applicative)i, SchematicannonOptions::new));
        public static final StreamCodec<ByteBuf, SchematicannonOptions> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, SchematicannonOptions::replaceMode, (StreamCodec)ByteBufCodecs.BOOL, SchematicannonOptions::skipMissing, (StreamCodec)ByteBufCodecs.BOOL, SchematicannonOptions::replaceBlockEntities, SchematicannonOptions::new);
    }
}
