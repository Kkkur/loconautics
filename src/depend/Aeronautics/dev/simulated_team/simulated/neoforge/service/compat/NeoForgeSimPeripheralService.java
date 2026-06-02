/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.network.wired.WiredElement
 *  dan200.computercraft.api.network.wired.WiredElementCapability
 *  dan200.computercraft.api.peripheral.IPeripheral
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package dev.simulated_team.simulated.neoforge.service.compat;

import dan200.computercraft.api.network.wired.WiredElement;
import dan200.computercraft.api.network.wired.WiredElementCapability;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import dev.simulated_team.simulated.service.compat.SimPeripheralService;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class NeoForgeSimPeripheralService
implements SimPeripheralService {
    private static final List<Entry<BlockEntity, IPeripheral>> PERIPHERALS = new ArrayList<Entry<BlockEntity, IPeripheral>>();
    private static final List<Entry<BlockEntity, WiredElement>> WIRED_ELEMENTS = new ArrayList<Entry<BlockEntity, WiredElement>>();

    @Override
    public <T extends BlockEntity> void addPeripheral(Supplier<BlockEntityType<T>> typeSupplier, SimPeripheralService.CapabilityGetter<T, IPeripheral> getter) {
        PERIPHERALS.add(new Entry<T, IPeripheral>(typeSupplier, getter));
    }

    @Override
    public <T extends BlockEntity> void addWired(Supplier<BlockEntityType<T>> typeSupplier, SimPeripheralService.CapabilityGetter<T, WiredElement> getter) {
        WIRED_ELEMENTS.add(new Entry<T, WiredElement>(typeSupplier, getter));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (Entry<BlockEntity, IPeripheral> entry : PERIPHERALS) {
            event.registerBlockEntity(PeripheralCapability.get(), entry.typeSupplier.get(), (be, direction) -> (IPeripheral)entry.peripheralFunction().get((BlockEntity)be, (Direction)direction));
        }
        for (Entry<BlockEntity, IPeripheral> entry : WIRED_ELEMENTS) {
            event.registerBlockEntity(WiredElementCapability.get(), entry.typeSupplier.get(), (be, direction) -> (WiredElement)entry.peripheralFunction().get((BlockEntity)be, (Direction)direction));
        }
    }

    private record Entry<T extends BlockEntity, V>(Supplier<BlockEntityType<T>> typeSupplier, SimPeripheralService.CapabilityGetter<T, V> peripheralFunction) {
    }
}
