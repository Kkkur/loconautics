/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.physics.config.block_properties;

public record BlockStateConditionSet.BlockStateCondition(String property, String value) {
    public static BlockStateConditionSet.BlockStateCondition parse(String value) {
        String[] parts = value.split("=");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid block state condition: " + value);
        }
        return new BlockStateConditionSet.BlockStateCondition(parts[0], parts[1]);
    }

    @Override
    public String toString() {
        return this.property + "=" + this.value;
    }
}
