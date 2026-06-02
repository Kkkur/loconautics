/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.capabilities.BlockCapability
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.blockEntity.behaviour.inventory;

import com.google.common.base.Predicates;
import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.function.Predicate;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class InvManipulationBehaviour
extends CapManipulationBehaviourBase<IItemHandler, InvManipulationBehaviour> {
    public static final BehaviourType<InvManipulationBehaviour> TYPE = new BehaviourType();
    public static final BehaviourType<InvManipulationBehaviour> EXTRACT = new BehaviourType();
    public static final BehaviourType<InvManipulationBehaviour> INSERT = new BehaviourType();
    private BehaviourType<InvManipulationBehaviour> behaviourType;

    public static InvManipulationBehaviour forExtraction(SmartBlockEntity be, CapManipulationBehaviourBase.InterfaceProvider target) {
        return new InvManipulationBehaviour(EXTRACT, be, target);
    }

    public static InvManipulationBehaviour forInsertion(SmartBlockEntity be, CapManipulationBehaviourBase.InterfaceProvider target) {
        return new InvManipulationBehaviour(INSERT, be, target);
    }

    public InvManipulationBehaviour(SmartBlockEntity be, CapManipulationBehaviourBase.InterfaceProvider target) {
        this(TYPE, be, target);
    }

    private InvManipulationBehaviour(BehaviourType<InvManipulationBehaviour> type, SmartBlockEntity be, CapManipulationBehaviourBase.InterfaceProvider target) {
        super(be, target);
        this.behaviourType = type;
    }

    @Nullable
    public IdentifiedInventory getIdentifiedInventory() {
        IItemHandler inventory = (IItemHandler)this.getInventory();
        if (inventory == null) {
            return null;
        }
        InventoryIdentifier identifier = InventoryIdentifier.get(this.getWorld(), this.getTarget().getOpposite());
        return new IdentifiedInventory(identifier, inventory);
    }

    @Override
    protected BlockCapability<IItemHandler, Direction> capability() {
        return Capabilities.ItemHandler.BLOCK;
    }

    public ItemStack extract() {
        return this.extract(this.getModeFromFilter(), this.getAmountFromFilter());
    }

    public ItemStack extract(ItemHelper.ExtractionCountMode mode, int amount) {
        return this.extract(mode, amount, (Predicate<ItemStack>)Predicates.alwaysTrue());
    }

    public ItemStack extract(ItemHelper.ExtractionCountMode mode, int amount, Predicate<ItemStack> filter) {
        boolean shouldSimulate = this.simulateNext;
        this.simulateNext = false;
        if (this.getWorld().isClientSide) {
            return ItemStack.EMPTY;
        }
        IItemHandler inventory = (IItemHandler)this.targetCapability;
        if (inventory == null) {
            return ItemStack.EMPTY;
        }
        Predicate<ItemStack> test = this.getFilterTest(filter);
        return ItemHelper.extract(inventory, test, mode, amount, shouldSimulate);
    }

    public ItemStack insert(ItemStack stack) {
        boolean shouldSimulate = this.simulateNext;
        this.simulateNext = false;
        IItemHandler inventory = (IItemHandler)this.targetCapability;
        if (inventory == null) {
            return stack;
        }
        return ItemHandlerHelper.insertItemStacked((IItemHandler)inventory, (ItemStack)stack, (boolean)shouldSimulate);
    }

    protected Predicate<ItemStack> getFilterTest(Predicate<ItemStack> customFilter) {
        Predicate<ItemStack> test = customFilter;
        FilteringBehaviour filter = this.blockEntity.getBehaviour(FilteringBehaviour.TYPE);
        if (filter != null) {
            test = customFilter.and(filter::test);
        }
        return test;
    }

    @Override
    public BehaviourType<?> getType() {
        return this.behaviourType;
    }
}
