/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.InterModComms
 *  net.neoforged.fml.event.lifecycle.InterModEnqueueEvent
 */
package com.simibubi.create.compat.inventorySorter;

import com.simibubi.create.compat.Mods;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterMenu;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

public class InventorySorterCompat {
    public static final String SLOT_BLACKLIST = "slotblacklist";

    public static void init(IEventBus bus) {
        bus.addListener(InventorySorterCompat::sendImc);
    }

    private static void sendImc(InterModEnqueueEvent event) {
        InterModComms.sendTo((String)Mods.INVENTORYSORTER.id(), (String)SLOT_BLACKLIST, RedstoneRequesterMenu.SorterProofSlot.class::getName);
    }
}
