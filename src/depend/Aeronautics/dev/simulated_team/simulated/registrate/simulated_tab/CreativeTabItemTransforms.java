/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.builders.ItemBuilder
 *  com.tterrag.registrate.util.nullness.NonNullUnaryOperator
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Block
 */
package dev.simulated_team.simulated.registrate.simulated_tab;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class CreativeTabItemTransforms {
    private static final HashMap<Item, VisibilityType> ITEM_VISIBILITY = new HashMap();
    private static final HashMap<Item, Function<Item, ItemStack>> STACK_TRANSFORM = new HashMap();

    public static ItemStack applyTransform(ItemStack item) {
        Function<Item, ItemStack> transform = STACK_TRANSFORM.get(item.getItem());
        if (transform == null) {
            return item;
        }
        return transform.apply(item.getItem());
    }

    public static <B extends Item, R> NonNullUnaryOperator<ItemBuilder<B, R>> transformItem(Function<Item, ItemStack> func) {
        return builder -> (ItemBuilder)builder.onRegisterAfter(Registries.ITEM, i -> STACK_TRANSFORM.put(i.asItem(), func));
    }

    public static enum VisibilityType {
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
}
