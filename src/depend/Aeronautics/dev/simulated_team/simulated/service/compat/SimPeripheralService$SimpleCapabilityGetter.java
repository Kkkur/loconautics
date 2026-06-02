/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.service.compat;

import dev.simulated_team.simulated.service.compat.SimPeripheralService;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public static interface SimPeripheralService.SimpleCapabilityGetter<T extends BlockEntity, V>
extends SimPeripheralService.CapabilityGetter<T, V> {
    @Nullable
    public V get(T var1);

    @Override
    default public V get(T blockEntity, Direction direction) {
        return this.get(blockEntity);
    }
}
