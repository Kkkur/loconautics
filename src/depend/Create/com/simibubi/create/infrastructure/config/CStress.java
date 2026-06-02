/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.util.nullness.NonNullUnaryOperator
 *  it.unimi.dsi.fastutil.objects.Object2DoubleMap
 *  it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 *  net.neoforged.neoforge.common.ModConfigSpec$ConfigValue
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.infrastructure.config;

import com.simibubi.create.Create;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

public class CStress
extends ConfigBase {
    private static final int VERSION = 2;
    private static final Object2DoubleMap<ResourceLocation> DEFAULT_IMPACTS = new Object2DoubleOpenHashMap();
    private static final Object2DoubleMap<ResourceLocation> DEFAULT_CAPACITIES = new Object2DoubleOpenHashMap();
    protected final Map<ResourceLocation, ModConfigSpec.ConfigValue<Double>> capacities = new HashMap<ResourceLocation, ModConfigSpec.ConfigValue<Double>>();
    protected final Map<ResourceLocation, ModConfigSpec.ConfigValue<Double>> impacts = new HashMap<ResourceLocation, ModConfigSpec.ConfigValue<Double>>();

    public void registerAll(ModConfigSpec.Builder builder) {
        builder.comment(new String[]{".", Comments.su, Comments.impact}).push("impact");
        DEFAULT_IMPACTS.forEach((id, value) -> this.impacts.put((ResourceLocation)id, (ModConfigSpec.ConfigValue<Double>)builder.define(id.getPath(), value)));
        builder.pop();
        builder.comment(new String[]{".", Comments.su, Comments.capacity}).push("capacity");
        DEFAULT_CAPACITIES.forEach((id, value) -> this.capacities.put((ResourceLocation)id, (ModConfigSpec.ConfigValue<Double>)builder.define(id.getPath(), value)));
        builder.pop();
    }

    public String getName() {
        return "stressValues.v2";
    }

    @Nullable
    public DoubleSupplier getImpact(Block block) {
        ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow((Block)block);
        ModConfigSpec.ConfigValue<Double> value = this.impacts.get(id);
        return value == null ? null : () -> value.get();
    }

    @Nullable
    public DoubleSupplier getCapacity(Block block) {
        ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow((Block)block);
        ModConfigSpec.ConfigValue<Double> value = this.capacities.get(id);
        return value == null ? null : () -> value.get();
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setNoImpact() {
        return CStress.setImpact(0.0);
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double value) {
        return builder -> {
            CStress.assertFromCreate(builder);
            ResourceLocation id = Create.asResource(builder.getName());
            DEFAULT_IMPACTS.put((Object)id, value);
            return builder;
        };
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setCapacity(double value) {
        return builder -> {
            CStress.assertFromCreate(builder);
            ResourceLocation id = Create.asResource(builder.getName());
            DEFAULT_CAPACITIES.put((Object)id, value);
            return builder;
        };
    }

    private static void assertFromCreate(BlockBuilder<?, ?> builder) {
        if (!builder.getOwner().getModid().equals("create")) {
            throw new IllegalStateException("Non-Create blocks cannot be added to Create's config.");
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
