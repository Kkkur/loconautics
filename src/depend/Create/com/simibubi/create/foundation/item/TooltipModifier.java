/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.item;

import com.simibubi.create.api.registry.SimpleRegistry;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TooltipModifier {
    public static final SimpleRegistry<Item, TooltipModifier> REGISTRY = SimpleRegistry.create();
    public static final TooltipModifier EMPTY = new TooltipModifier(){

        @Override
        public void modify(ItemTooltipEvent context) {
        }

        @Override
        public TooltipModifier andThen(TooltipModifier after) {
            return after;
        }
    };

    public void modify(ItemTooltipEvent var1);

    default public TooltipModifier andThen(TooltipModifier after) {
        if (after == EMPTY) {
            return this;
        }
        return tooltip -> {
            this.modify(tooltip);
            after.modify(tooltip);
        };
    }

    public static TooltipModifier mapNull(@Nullable TooltipModifier modifier) {
        if (modifier == null) {
            return EMPTY;
        }
        return modifier;
    }
}
