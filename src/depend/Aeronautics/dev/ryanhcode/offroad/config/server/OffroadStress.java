/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.infrastructure.config.CStress
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.util.nullness.NonNullUnaryOperator
 *  it.unimi.dsi.fastutil.objects.Object2DoubleMap
 *  it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 */
package dev.ryanhcode.offroad.config.server;

import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import dev.ryanhcode.offroad.Offroad;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec;

public class OffroadStress
extends CStress {
    private static final Object2DoubleMap<ResourceLocation> DEFAULT_IMPACTS = new Object2DoubleOpenHashMap();
    private static final Object2DoubleMap<ResourceLocation> DEFAULT_CAPACITIES = new Object2DoubleOpenHashMap();

    public void registerAll(ModConfigSpec.Builder builder) {
        builder.comment(new String[]{".", Comments.su, Comments.impact}).push("impact");
        DEFAULT_IMPACTS.forEach((id, value) -> this.impacts.put(id, builder.define(id.getPath(), value)));
        builder.pop();
        builder.comment(new String[]{".", Comments.su, Comments.capacity}).push("capacity");
        DEFAULT_CAPACITIES.forEach((id, value) -> this.capacities.put(id, builder.define(id.getPath(), value)));
        builder.pop();
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setNoImpact() {
        return OffroadStress.setImpact(0.0);
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double value) {
        return builder -> {
            OffroadStress.assertFromOffroad(builder);
            DEFAULT_IMPACTS.put((Object)Offroad.path(builder.getName()), value);
            return builder;
        };
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setCapacity(double value) {
        return builder -> {
            OffroadStress.assertFromOffroad(builder);
            DEFAULT_CAPACITIES.put((Object)Offroad.path(builder.getName()), value);
            return builder;
        };
    }

    private static void assertFromOffroad(BlockBuilder<?, ?> builder) {
        if (!builder.getOwner().getModid().equals("offroad")) {
            throw new IllegalStateException("Non-Offroad blocks cannot be added to Offroad's config.");
        }
    }

    private static class Comments {
        static String su = "[in Stress Units]";
        static String impact = "Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives.";
        static String capacity = "Configure how much stress a source can accommodate for.";

        private Comments() {
        }
    }
}
