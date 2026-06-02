/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.nullness.NonNullConsumer
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.api.stress;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import java.util.function.DoubleSupplier;
import net.minecraft.world.level.block.Block;

public class BlockStressValues {
    public static final SimpleRegistry<Block, DoubleSupplier> IMPACTS = SimpleRegistry.create();
    public static final SimpleRegistry<Block, DoubleSupplier> CAPACITIES = SimpleRegistry.create();
    public static final SimpleRegistry<Block, GeneratedRpm> RPM = SimpleRegistry.create();

    public static double getImpact(Block block) {
        DoubleSupplier supplier = IMPACTS.get(block);
        return supplier == null ? 0.0 : supplier.getAsDouble();
    }

    public static double getCapacity(Block block) {
        DoubleSupplier supplier = CAPACITIES.get(block);
        return supplier == null ? 0.0 : supplier.getAsDouble();
    }

    public static NonNullConsumer<Block> setGeneratorSpeed(int value) {
        return block -> RPM.register((Block)block, new GeneratedRpm(value, false));
    }

    public static NonNullConsumer<Block> setGeneratorSpeed(int value, boolean mayGenerateLess) {
        return block -> RPM.register((Block)block, new GeneratedRpm(value, mayGenerateLess));
    }

    private BlockStressValues() {
        throw new AssertionError((Object)"This class should not be instantiated");
    }

    public record GeneratedRpm(int value, boolean mayGenerateLess) {
    }
}
