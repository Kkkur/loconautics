/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.ryanhcode.sable.physics.config.block_properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public record BlockStateConditionSet(List<BlockStateCondition> blockStateConditions) {
    public static final Codec<BlockStateConditionSet> CODEC = Codec.STRING.comapFlatMap(BlockStateConditionSet::parse, BlockStateConditionSet::toString).stable();

    public static DataResult<BlockStateConditionSet> parse(String value) {
        String[] parts = value.split(",");
        ArrayList<BlockStateCondition> conditions = new ArrayList<BlockStateCondition>();
        try {
            for (String part : parts) {
                conditions.add(BlockStateCondition.parse(part));
            }
        }
        catch (IllegalArgumentException exception) {
            return DataResult.error(exception::getMessage);
        }
        return DataResult.success((Object)new BlockStateConditionSet(conditions));
    }

    @Override
    public String toString() {
        return String.join((CharSequence)",", this.blockStateConditions.stream().map(BlockStateCondition::toString).toList());
    }

    public boolean matches(StateDefinition<Block, BlockState> stateDefinition, BlockState state) {
        for (BlockStateCondition condition : this.blockStateConditions) {
            Property property = stateDefinition.getProperty(condition.property());
            if (property == null) {
                return false;
            }
            Comparable expectedValue = property.getValue(condition.value()).orElse(null);
            if (expectedValue == null) {
                return false;
            }
            if (state.getValue(property).equals(expectedValue)) continue;
            return false;
        }
        return true;
    }

    public record BlockStateCondition(String property, String value) {
        public static BlockStateCondition parse(String value) {
            String[] parts = value.split("=");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid block state condition: " + value);
            }
            return new BlockStateCondition(parts[0], parts[1]);
        }

        @Override
        public String toString() {
            return this.property + "=" + this.value;
        }
    }
}
