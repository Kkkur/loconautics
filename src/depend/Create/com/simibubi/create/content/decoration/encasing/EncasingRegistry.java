/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.util.nullness.NonNullUnaryOperator
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.content.decoration.encasing;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public class EncasingRegistry {
    private static final Map<Block, List<Block>> ENCASED_VARIANTS = new HashMap<Block, List<Block>>();

    public static <B extends Block, E extends Block, P> void addVariant(B encasable, E encased) {
        ENCASED_VARIANTS.computeIfAbsent(encasable, b -> new ArrayList()).add(encased);
    }

    public static List<Block> getVariants(Block block) {
        return ENCASED_VARIANTS.getOrDefault(block, Collections.emptyList());
    }

    public static <B extends Block, P, E extends Block> NonNullUnaryOperator<BlockBuilder<B, P>> addVariantTo(Supplier<E> encasable) {
        return builder -> {
            builder.onRegisterAfter(Registries.BLOCK, arg_0 -> EncasingRegistry.lambda$addVariantTo$1((Supplier)encasable, arg_0));
            return builder;
        };
    }

    private static /* synthetic */ void lambda$addVariantTo$1(Supplier encasable, Block b) {
        EncasingRegistry.addVariant((Block)encasable.get(), b);
    }
}
