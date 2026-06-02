/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.ValueListDisplaySource;
import com.simibubi.create.content.redstone.smartObserver.SmartObserverBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.item.CountedItemStackList;
import java.util.stream.Stream;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemListDisplaySource
extends ValueListDisplaySource {
    @Override
    protected Stream<IntAttached<MutableComponent>> provideEntries(DisplayLinkContext context, int maxRows) {
        BlockEntity sourceBE = context.getSourceBlockEntity();
        if (!(sourceBE instanceof SmartObserverBlockEntity)) {
            return Stream.empty();
        }
        SmartObserverBlockEntity cobe = (SmartObserverBlockEntity)sourceBE;
        InvManipulationBehaviour invManipulationBehaviour = cobe.getBehaviour(InvManipulationBehaviour.TYPE);
        FilteringBehaviour filteringBehaviour = cobe.getBehaviour(FilteringBehaviour.TYPE);
        IItemHandler handler = (IItemHandler)invManipulationBehaviour.getInventory();
        if (handler == null) {
            return Stream.empty();
        }
        return new CountedItemStackList(handler, filteringBehaviour).getTopNames(maxRows);
    }

    @Override
    protected String getTranslationKey() {
        return "list_items";
    }

    @Override
    protected boolean valueFirst() {
        return true;
    }
}
