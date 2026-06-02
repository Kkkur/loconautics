/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.foundation.blockEntity.behaviour.inventory;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryWrapper;
import net.neoforged.neoforge.items.IItemHandler;

public class VersionedInventoryTrackerBehaviour
extends BlockEntityBehaviour {
    public static final BehaviourType<VersionedInventoryTrackerBehaviour> TYPE = new BehaviourType();
    private int ignoredId;
    private int ignoredVersion;

    public VersionedInventoryTrackerBehaviour(SmartBlockEntity be) {
        super(be);
        this.reset();
    }

    public boolean stillWaiting(InvManipulationBehaviour behaviour) {
        return behaviour.hasInventory() && this.stillWaiting((IItemHandler)behaviour.getInventory());
    }

    public boolean stillWaiting(IItemHandler handler) {
        if (handler instanceof VersionedInventoryWrapper) {
            VersionedInventoryWrapper viw = (VersionedInventoryWrapper)handler;
            return viw.getId() == this.ignoredId && viw.getVersion() == this.ignoredVersion;
        }
        return false;
    }

    public void awaitNewVersion(InvManipulationBehaviour behaviour) {
        if (behaviour.hasInventory()) {
            this.awaitNewVersion((IItemHandler)behaviour.getInventory());
        }
    }

    public void awaitNewVersion(IItemHandler handler) {
        if (handler instanceof VersionedInventoryWrapper) {
            VersionedInventoryWrapper viw = (VersionedInventoryWrapper)handler;
            this.ignoredId = viw.getId();
            this.ignoredVersion = viw.getVersion();
        }
    }

    public void reset() {
        this.ignoredVersion = -1;
        this.ignoredId = -1;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
