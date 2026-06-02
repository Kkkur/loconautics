/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package dev.ryanhcode.sable.physics.config.block_properties;

import com.mojang.serialization.Codec;

public record PhysicsBlockPropertyTypes.PhysicsBlockPropertyType<T>(int id, Codec<T> codec, T defaultValue) {
}
