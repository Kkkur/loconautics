/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.service.compat;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public static interface SimPeripheralService.CapabilityGetter<T extends BlockEntity, V> {
    @Nullable
    public V get(T var1, Direction var2);
}
