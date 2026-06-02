/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.logistics.vault;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.content.logistics.vault.ItemVaultBlock;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryWrapper;
import com.simibubi.create.foundation.mixin.accessor.ItemStackHandlerAccessor;
import com.simibubi.create.foundation.utility.SameSizeCombinedInvWrapper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ItemVaultBlockEntity
extends SmartBlockEntity
implements IMultiBlockEntityContainer.Inventory,
Clearable {
    protected ICapabilityProvider<IItemHandler> itemCapability = null;
    protected InventoryIdentifier invId;
    protected ItemStackHandler inventory;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected int radius;
    protected int length;

    public ItemVaultBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new ItemStackHandler((Integer)AllConfigs.server().logistics.vaultCapacity.get()){

            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                ItemVaultBlockEntity.this.updateComparators();
                ItemVaultBlockEntity.this.level.blockEntityChanged(ItemVaultBlockEntity.this.worldPosition);
            }
        };
        this.radius = 1;
        this.length = 1;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.ITEM_VAULT.get(), (be, context) -> {
            be.initCapability();
            if (be.itemCapability == null) {
                return null;
            }
            return be.itemCapability.getCapability();
        });
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    protected void updateConnectivity() {
        this.updateConnectivity = false;
        if (this.level.isClientSide()) {
            return;
        }
        if (!this.isController()) {
            return;
        }
        ConnectivityHandler.formMulti(this);
    }

    protected void updateComparators() {
        ItemVaultBlockEntity controllerBE = this.getControllerBE();
        if (controllerBE == null) {
            return;
        }
        this.level.blockEntityChanged(controllerBE.worldPosition);
        BlockPos pos = controllerBE.getBlockPos();
        int radius = controllerBE.radius;
        int length = controllerBE.length;
        Direction.Axis axis = controllerBE.getMainConnectionAxis();
        int zMax = axis == Direction.Axis.X ? radius : length;
        int xMax = axis == Direction.Axis.Z ? radius : length;
        BlockPos.MutableBlockPos updatePos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos provokingPos = new BlockPos.MutableBlockPos();
        for (int y = 0; y < radius; ++y) {
            for (int z = 0; z < zMax; ++z) {
                for (int x = 0; x < xMax; ++x) {
                    int sectionZ;
                    int sectionX = SectionPos.blockToSectionCoord((int)(pos.getX() + x));
                    if (!this.level.hasChunk(sectionX, sectionZ = SectionPos.blockToSectionCoord((int)(pos.getZ() + z)))) continue;
                    provokingPos.setWithOffset((Vec3i)pos, x, y, z);
                    Block provokingBlock = this.level.getBlockState((BlockPos)provokingPos).getBlock();
                    if (y == 0) {
                        ItemVaultBlockEntity.updateComaratorsInner(this.level, provokingBlock, (BlockPos)provokingPos, updatePos, Direction.DOWN);
                    }
                    if (y == radius - 1) {
                        ItemVaultBlockEntity.updateComaratorsInner(this.level, provokingBlock, (BlockPos)provokingPos, updatePos, Direction.UP);
                    }
                    if (z == 0) {
                        ItemVaultBlockEntity.updateComaratorsInner(this.level, provokingBlock, (BlockPos)provokingPos, updatePos, Direction.NORTH);
                    }
                    if (z == zMax - 1) {
                        ItemVaultBlockEntity.updateComaratorsInner(this.level, provokingBlock, (BlockPos)provokingPos, updatePos, Direction.SOUTH);
                    }
                    if (x == 0) {
                        ItemVaultBlockEntity.updateComaratorsInner(this.level, provokingBlock, (BlockPos)provokingPos, updatePos, Direction.WEST);
                    }
                    if (x != xMax - 1) continue;
                    ItemVaultBlockEntity.updateComaratorsInner(this.level, provokingBlock, (BlockPos)provokingPos, updatePos, Direction.EAST);
                }
            }
        }
    }

    private static void updateComaratorsInner(Level level, Block provokingBlock, BlockPos provokingPos, BlockPos.MutableBlockPos updatePos, Direction direction) {
        updatePos.setWithOffset((Vec3i)provokingPos, direction);
        int sectionX = SectionPos.blockToSectionCoord((int)updatePos.getX());
        int sectionZ = SectionPos.blockToSectionCoord((int)updatePos.getZ());
        if (!level.hasChunk(sectionX, sectionZ)) {
            return;
        }
        BlockState blockstate = level.getBlockState((BlockPos)updatePos);
        blockstate.onNeighborChange((LevelReader)level, (BlockPos)updatePos, provokingPos);
        if (blockstate.isRedstoneConductor((BlockGetter)level, (BlockPos)updatePos)) {
            updatePos.move(direction);
            blockstate = level.getBlockState((BlockPos)updatePos);
            if (blockstate.getWeakChanges((LevelReader)level, (BlockPos)updatePos)) {
                level.neighborChanged(blockstate, (BlockPos)updatePos, provokingBlock, provokingPos, false);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.lastKnownPos == null) {
            this.lastKnownPos = this.getBlockPos();
        } else if (!this.lastKnownPos.equals((Object)this.worldPosition) && this.worldPosition != null) {
            this.onPositionChanged();
            return;
        }
        if (this.updateConnectivity) {
            this.updateConnectivity();
        }
    }

    @Override
    public BlockPos getLastKnownPos() {
        return this.lastKnownPos;
    }

    @Override
    public boolean isController() {
        return this.controller == null || this.worldPosition.getX() == this.controller.getX() && this.worldPosition.getY() == this.controller.getY() && this.worldPosition.getZ() == this.controller.getZ();
    }

    private void onPositionChanged() {
        this.removeController(true);
        this.lastKnownPos = this.worldPosition;
    }

    public ItemVaultBlockEntity getControllerBE() {
        if (this.isController()) {
            return this;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(this.controller);
        if (blockEntity instanceof ItemVaultBlockEntity) {
            return (ItemVaultBlockEntity)blockEntity;
        }
        return null;
    }

    @Override
    public void removeController(boolean keepContents) {
        if (this.level.isClientSide()) {
            return;
        }
        this.updateConnectivity = true;
        this.controller = null;
        this.radius = 1;
        this.length = 1;
        BlockState state = this.getBlockState();
        if (ItemVaultBlock.isVault(state)) {
            state = (BlockState)state.setValue((Property)ItemVaultBlock.LARGE, (Comparable)Boolean.valueOf(false));
            this.getLevel().setBlock(this.worldPosition, state, 22);
        }
        this.itemCapability = null;
        this.invalidateCapabilities();
        this.setChanged();
        this.sendData();
    }

    @Override
    public void setController(BlockPos controller) {
        if (this.level.isClientSide && !this.isVirtual()) {
            return;
        }
        if (controller.equals((Object)this.controller)) {
            return;
        }
        this.controller = controller;
        this.itemCapability = null;
        this.invalidateCapabilities();
        this.setChanged();
        this.sendData();
    }

    @Override
    public BlockPos getController() {
        return this.isController() ? this.worldPosition : this.controller;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        boolean changeOfController;
        super.read(compound, registries, clientPacket);
        BlockPos controllerBefore = this.controller;
        int prevSize = this.radius;
        int prevLength = this.length;
        this.updateConnectivity = compound.contains("Uninitialized");
        this.lastKnownPos = null;
        if (compound.contains("LastKnownPos")) {
            this.lastKnownPos = NBTHelper.readBlockPos((CompoundTag)compound, (String)"LastKnownPos");
        }
        this.controller = null;
        if (compound.contains("Controller")) {
            this.controller = NBTHelper.readBlockPos((CompoundTag)compound, (String)"Controller");
        }
        if (this.isController()) {
            this.radius = compound.getInt("Size");
            this.length = compound.getInt("Length");
        }
        if (!clientPacket) {
            this.inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
            return;
        }
        boolean bl = controllerBefore == null ? this.controller != null : (changeOfController = !controllerBefore.equals((Object)this.controller));
        if (this.hasLevel() && (changeOfController || prevSize != this.radius || prevLength != this.length)) {
            this.level.setBlocksDirty(this.getBlockPos(), Blocks.AIR.defaultBlockState(), this.getBlockState());
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.updateConnectivity) {
            compound.putBoolean("Uninitialized", true);
        }
        if (this.lastKnownPos != null) {
            compound.put("LastKnownPos", NbtUtils.writeBlockPos((BlockPos)this.lastKnownPos));
        }
        if (!this.isController()) {
            compound.put("Controller", NbtUtils.writeBlockPos((BlockPos)this.controller));
        }
        if (this.isController()) {
            compound.putInt("Size", this.radius);
            compound.putInt("Length", this.length);
        }
        super.write(compound, registries, clientPacket);
        if (!clientPacket) {
            compound.putString("StorageType", "CombinedInv");
            compound.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
        }
    }

    public void clearContent() {
        ((ItemStackHandlerAccessor)this.inventory).create$getStacks().clear();
    }

    public ItemStackHandler getInventoryOfBlock() {
        return this.inventory;
    }

    public InventoryIdentifier getInvId() {
        this.initCapability();
        return this.invId;
    }

    public void applyInventoryToBlock(ItemStackHandler handler) {
        for (int i = 0; i < this.inventory.getSlots(); ++i) {
            this.inventory.setStackInSlot(i, i < handler.getSlots() ? handler.getStackInSlot(i) : ItemStack.EMPTY);
        }
    }

    private void initCapability() {
        if (this.itemCapability != null && this.itemCapability.getCapability() != null) {
            return;
        }
        if (!this.isController()) {
            ItemVaultBlockEntity controllerBE = this.getControllerBE();
            if (controllerBE == null) {
                return;
            }
            controllerBE.initCapability();
            this.itemCapability = ICapabilityProvider.of(() -> {
                if (controllerBE.isRemoved()) {
                    return null;
                }
                if (controllerBE.itemCapability == null) {
                    return null;
                }
                return controllerBE.itemCapability.getCapability();
            });
            this.invId = controllerBE.invId;
            return;
        }
        boolean alongZ = ItemVaultBlock.getVaultBlockAxis(this.getBlockState()) == Direction.Axis.Z;
        IItemHandlerModifiable[] invs = new IItemHandlerModifiable[this.length * this.radius * this.radius];
        for (int yOffset = 0; yOffset < this.length; ++yOffset) {
            for (int xOffset = 0; xOffset < this.radius; ++xOffset) {
                for (int zOffset = 0; zOffset < this.radius; ++zOffset) {
                    BlockPos vaultPos = alongZ ? this.worldPosition.offset(xOffset, zOffset, yOffset) : this.worldPosition.offset(yOffset, xOffset, zOffset);
                    ItemVaultBlockEntity vaultAt = (ItemVaultBlockEntity)ConnectivityHandler.partAt((BlockEntityType)AllBlockEntityTypes.ITEM_VAULT.get(), (BlockGetter)this.level, vaultPos);
                    invs[yOffset * this.radius * this.radius + xOffset * this.radius + zOffset] = vaultAt != null ? vaultAt.inventory : new ItemStackHandler();
                }
            }
        }
        this.itemCapability = ICapabilityProvider.of(new VersionedInventoryWrapper((IItemHandlerModifiable)SameSizeCombinedInvWrapper.create(invs)));
        BlockPos farCorner = alongZ ? this.worldPosition.offset(this.radius, this.radius, this.length) : this.worldPosition.offset(this.length, this.radius, this.radius);
        BoundingBox bounds = BoundingBox.fromCorners((Vec3i)this.worldPosition, (Vec3i)farCorner);
        this.invId = new InventoryIdentifier.Bounds(bounds);
    }

    public static int getMaxLength(int radius) {
        return radius * 3;
    }

    @Override
    public void preventConnectivityUpdate() {
        this.updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (ItemVaultBlock.isVault(state)) {
            this.level.setBlock(this.getBlockPos(), (BlockState)state.setValue((Property)ItemVaultBlock.LARGE, (Comparable)Boolean.valueOf(this.radius > 2)), 6);
        }
        this.itemCapability = null;
        this.invalidateCapabilities();
        this.setChanged();
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return this.getMainAxisOf(this);
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y) {
            return this.getMaxWidth();
        }
        return ItemVaultBlockEntity.getMaxLength(width);
    }

    @Override
    public int getMaxWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return this.length;
    }

    @Override
    public int getWidth() {
        return this.radius;
    }

    @Override
    public void setHeight(int height) {
        this.length = height;
    }

    @Override
    public void setWidth(int width) {
        this.radius = width;
    }

    @Override
    public boolean hasInventory() {
        return true;
    }
}
