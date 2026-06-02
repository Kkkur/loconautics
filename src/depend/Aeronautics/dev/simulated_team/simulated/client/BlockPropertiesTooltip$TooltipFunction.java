/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.BlockItem
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.client;

import dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public static interface BlockPropertiesTooltip.TooltipFunction {
    @Nullable
    public Component apply(BlockStateExtension var1, BlockItem var2, boolean var3);
}
