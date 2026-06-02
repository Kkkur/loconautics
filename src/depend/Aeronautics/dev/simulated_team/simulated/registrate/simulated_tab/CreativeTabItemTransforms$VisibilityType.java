/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.builders.ItemBuilder
 *  com.tterrag.registrate.util.nullness.NonNullUnaryOperator
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 */
package dev.simulated_team.simulated.registrate.simulated_tab;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public static enum CreativeTabItemTransforms.VisibilityType {
    INVISIBLE,
    SEARCH_ONLY;


    public boolean has(Item item) {
        return this == ITEM_VISIBILITY.get(item);
    }

    public <B extends Block, R> NonNullUnaryOperator<BlockBuilder<B, R>> applyBlock() {
        return builder -> (BlockBuilder)builder.onRegisterAfter(Registries.ITEM, b -> ITEM_VISIBILITY.put(b.asItem(), this));
    }

    public <B extends Block, R> NonNullUnaryOperator<BlockBuilder<B, R>> conditionalApplyBlock(Supplier<Boolean> visibiltySup) {
        return builder -> (BlockBuilder)builder.onRegisterAfter(Registries.ITEM, arg_0 -> this.lambda$conditionalApplyBlock$2((Supplier)visibiltySup, arg_0));
    }

    public <B extends Item, R> NonNullUnaryOperator<ItemBuilder<B, R>> applyItem() {
        return builder -> (ItemBuilder)builder.onRegisterAfter(Registries.ITEM, b -> ITEM_VISIBILITY.put(b.asItem(), this));
    }

    private /* synthetic */ void lambda$conditionalApplyBlock$2(Supplier visibiltySup, Block b) {
        if (((Boolean)visibiltySup.get()).booleanValue()) {
            ITEM_VISIBILITY.put(b.asItem(), this);
        }
    }
}
