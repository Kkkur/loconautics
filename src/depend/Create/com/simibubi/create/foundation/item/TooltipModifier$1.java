/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
 */
package com.simibubi.create.foundation.item;

import com.simibubi.create.foundation.item.TooltipModifier;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

class TooltipModifier.1
implements TooltipModifier {
    TooltipModifier.1() {
    }

    @Override
    public void modify(ItemTooltipEvent context) {
    }

    @Override
    public TooltipModifier andThen(TooltipModifier after) {
        return after;
    }
}
