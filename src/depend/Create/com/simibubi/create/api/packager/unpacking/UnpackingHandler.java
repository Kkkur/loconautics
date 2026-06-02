/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.packager.unpacking;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.impl.unpacking.DefaultUnpackingHandler;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public interface UnpackingHandler {
    public static final SimpleRegistry<Block, UnpackingHandler> REGISTRY = SimpleRegistry.create();
    public static final UnpackingHandler DEFAULT = DefaultUnpackingHandler.INSTANCE;

    public boolean unpack(Level var1, BlockPos var2, BlockState var3, Direction var4, List<ItemStack> var5, @Nullable PackageOrderWithCrafts var6, boolean var7);
}
