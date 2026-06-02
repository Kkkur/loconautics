/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper
 *  dev.simulated_team.simulated.multiloader.inventory.SingleSlotContainer
 */
package dev.ryanhcode.offroad.content.blocks.wheel_mount;

import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity;
import dev.ryanhcode.offroad.content.components.TireLike;
import dev.ryanhcode.offroad.index.OffroadDataComponents;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import dev.simulated_team.simulated.multiloader.inventory.SingleSlotContainer;

public class WheelMountInventory
extends SingleSlotContainer {
    private WheelMountBlockEntity be;
    public boolean suppressUpdate = false;

    public WheelMountInventory(WheelMountBlockEntity be) {
        super(1);
        this.be = be;
    }

    public boolean canInsertItem(ItemInfoWrapper item) {
        TireLike tireLike = (TireLike)ItemInfoWrapper.generateFromInfo((ItemInfoWrapper)item).get(OffroadDataComponents.TIRE);
        return tireLike != null;
    }

    public void setChanged() {
        if (!this.suppressUpdate) {
            this.be.notifyUpdate();
        }
    }
}
