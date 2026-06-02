/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 */
package dev.simulated_team.simulated.neoforge.service.compat;

import dev.simulated_team.simulated.service.compat.SimPeripheralService;
import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

private record NeoForgeSimPeripheralService.Entry<T extends BlockEntity, V>(Supplier<BlockEntityType<T>> typeSupplier, SimPeripheralService.CapabilityGetter<T, V> peripheralFunction) {
}
