/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.MountedStorageManager
 *  com.tterrag.registrate.util.nullness.NonNullConsumer
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.neoforge.service;

import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import dev.simulated_team.simulated.multiloader.inventory.AbstractContainer;
import dev.simulated_team.simulated.multiloader.inventory.InventoryLoaderWrapper;
import dev.simulated_team.simulated.multiloader.inventory.neoforge.InventoryLoaderWrapperImpl;
import dev.simulated_team.simulated.multiloader.tanks.SingleTank;
import dev.simulated_team.simulated.service.SimInventoryService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class NeoForgeSimInventoryService
implements SimInventoryService {
    public static Set<InventoryGetterHolder<? extends BlockEntity>> inventoryGetters = new HashSet<InventoryGetterHolder<? extends BlockEntity>>();
    public static Set<TankGetterHolder<? extends BlockEntity>> fluidTankGetters = new HashSet<TankGetterHolder<? extends BlockEntity>>();
    public static HashMap<BlockEntityType<BlockEntity>, Function<BlockEntity, SingleTank>> tankGetters = new HashMap();

    @Override
    public <T extends BlockEntity> NonNullConsumer<BlockEntityType<T>> registerInventory(BiFunction<T, Direction, AbstractContainer> getter) {
        return type -> inventoryGetters.add(new InventoryGetterHolder(getter, type));
    }

    @Override
    public <T extends BlockEntity> NonNullConsumer<BlockEntityType<T>> registerTank(BiFunction<T, Direction, SingleTank> getter) {
        return type -> fluidTankGetters.add(new TankGetterHolder(getter, type));
    }

    @Override
    public <T extends InventoryLoaderWrapper> T getInventory(@Nullable BlockEntity be, @Nullable Direction dir) {
        IItemHandler handler;
        if (be != null && (handler = (IItemHandler)be.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), (Object)dir)) != null) {
            return (T)new InventoryLoaderWrapperImpl(handler);
        }
        return null;
    }

    @Override
    public <T extends InventoryLoaderWrapper> T getWrappedAllItemsFromContraption(MountedStorageManager manager) {
        return (T)new InventoryLoaderWrapperImpl((IItemHandler)manager.getAllItems());
    }

    @Override
    public <T extends InventoryLoaderWrapper> T getWrappedMountedItemsFromContraption(MountedStorageManager manager) {
        return (T)new InventoryLoaderWrapperImpl((IItemHandler)manager.getMountedItems());
    }

    public record TankGetterHolder<T extends BlockEntity>(BiFunction<T, Direction, SingleTank> getter, BlockEntityType<T> type) {
        public SingleTank castBlockEntityAndGetInv(BlockEntity be, Direction dir) {
            BlockEntity casted = be;
            return this.getter.apply(casted, dir);
        }
    }

    public record InventoryGetterHolder<T extends BlockEntity>(BiFunction<T, Direction, AbstractContainer> getter, BlockEntityType<T> type) {
        public AbstractContainer castBlockEntityAndGetInv(BlockEntity be, Direction dir) {
            BlockEntity casted = be;
            return this.getter.apply(casted, dir);
        }
    }
}
