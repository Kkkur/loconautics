/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.MountedStorageManager
 *  com.tterrag.registrate.util.nullness.NonNullConsumer
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.service;

import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import dev.simulated_team.simulated.multiloader.inventory.AbstractContainer;
import dev.simulated_team.simulated.multiloader.inventory.InventoryLoaderWrapper;
import dev.simulated_team.simulated.multiloader.tanks.SingleTank;
import dev.simulated_team.simulated.service.ServiceUtil;
import java.util.function.BiFunction;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public interface SimInventoryService {
    public static final SimInventoryService INSTANCE = ServiceUtil.load(SimInventoryService.class);

    public <T extends InventoryLoaderWrapper> T getInventory(@Nullable BlockEntity var1, @Nullable Direction var2);

    public <T extends InventoryLoaderWrapper> T getWrappedAllItemsFromContraption(MountedStorageManager var1);

    public <T extends InventoryLoaderWrapper> T getWrappedMountedItemsFromContraption(MountedStorageManager var1);

    public <T extends BlockEntity> NonNullConsumer<BlockEntityType<T>> registerInventory(BiFunction<T, Direction, AbstractContainer> var1);

    public <T extends BlockEntity> NonNullConsumer<BlockEntityType<T>> registerTank(BiFunction<T, Direction, SingleTank> var1);
}
