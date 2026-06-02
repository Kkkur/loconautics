/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.contraptions.actors.psi;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import com.simibubi.create.foundation.item.ItemHandlerWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class PortableItemInterfaceBlockEntity
extends PortableStorageInterfaceBlockEntity {
    protected IItemHandlerModifiable capability = this.createEmptyHandler();

    public PortableItemInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.PORTABLE_STORAGE_INTERFACE.get(), (be, context) -> be.capability);
    }

    @Override
    public void startTransferringTo(Contraption contraption, float distance) {
        this.capability = new InterfaceItemHandler((IItemHandlerModifiable)contraption.getStorage().getAllItems());
        this.invalidateCapability();
        if (this.level != null && !this.level.isClientSide) {
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        }
        super.startTransferringTo(contraption, distance);
    }

    @Override
    protected void stopTransferring() {
        this.capability = this.createEmptyHandler();
        this.invalidateCapability();
        if (this.level != null && !this.level.isClientSide) {
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        }
        super.stopTransferring();
    }

    private IItemHandlerModifiable createEmptyHandler() {
        return new InterfaceItemHandler((IItemHandlerModifiable)new ItemStackHandler(0));
    }

    @Override
    protected void invalidateCapability() {
        this.invalidateCapabilities();
    }

    class InterfaceItemHandler
    extends ItemHandlerWrapper {
        public InterfaceItemHandler(IItemHandlerModifiable wrapped) {
            super(wrapped);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!PortableItemInterfaceBlockEntity.this.canTransfer()) {
                return ItemStack.EMPTY;
            }
            ItemStack extractItem = super.extractItem(slot, amount, simulate);
            if (!simulate && !extractItem.isEmpty()) {
                PortableItemInterfaceBlockEntity.this.onContentTransferred();
            }
            return extractItem;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!PortableItemInterfaceBlockEntity.this.canTransfer()) {
                return stack;
            }
            ItemStack insertItem = super.insertItem(slot, stack, simulate);
            if (!simulate && !ItemStack.matches((ItemStack)insertItem, (ItemStack)stack)) {
                PortableItemInterfaceBlockEntity.this.onContentTransferred();
            }
            return insertItem;
        }
    }
}
