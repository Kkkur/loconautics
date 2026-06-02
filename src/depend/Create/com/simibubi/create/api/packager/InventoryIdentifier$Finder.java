/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.packager;

import com.simibubi.create.api.packager.InventoryIdentifier;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public static interface InventoryIdentifier.Finder {
    @Nullable
    public InventoryIdentifier find(Level var1, BlockState var2, BlockFace var3);
}
