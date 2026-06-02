/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 */
package dev.simulated_team.simulated.neoforge.service;

import dev.simulated_team.simulated.multiloader.inventory.AbstractContainer;
import java.util.function.BiFunction;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public record NeoForgeSimInventoryService.InventoryGetterHolder<T extends BlockEntity>(BiFunction<T, Direction, AbstractContainer> getter, BlockEntityType<T> type) {
    public AbstractContainer castBlockEntityAndGetInv(BlockEntity be, Direction dir) {
        BlockEntity casted = be;
        return this.getter.apply(casted, dir);
    }
}
