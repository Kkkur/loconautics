/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.network.wired.WiredElement
 *  dan200.computercraft.api.peripheral.IPeripheral
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.service.compat;

import dan200.computercraft.api.network.wired.WiredElement;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public interface SimPeripheralService {
    public <T extends BlockEntity> void addPeripheral(Supplier<BlockEntityType<T>> var1, CapabilityGetter<T, IPeripheral> var2);

    default public <T extends BlockEntity> void addPeripheral(Supplier<BlockEntityType<T>> typeSupplier, SimpleCapabilityGetter<T, IPeripheral> getter) {
        this.addPeripheral(typeSupplier, (CapabilityGetter<T, IPeripheral>)getter);
    }

    public <T extends BlockEntity> void addWired(Supplier<BlockEntityType<T>> var1, CapabilityGetter<T, WiredElement> var2);

    default public <T extends BlockEntity> void addWired(Supplier<BlockEntityType<T>> typeSupplier, SimpleCapabilityGetter<T, WiredElement> getter) {
        this.addWired(typeSupplier, (CapabilityGetter<T, WiredElement>)getter);
    }

    @FunctionalInterface
    public static interface CapabilityGetter<T extends BlockEntity, V> {
        @Nullable
        public V get(T var1, Direction var2);
    }

    @FunctionalInterface
    public static interface SimpleCapabilityGetter<T extends BlockEntity, V>
    extends CapabilityGetter<T, V> {
        @Nullable
        public V get(T var1);

        @Override
        default public V get(T blockEntity, Direction direction) {
            return this.get(blockEntity);
        }
    }
}
