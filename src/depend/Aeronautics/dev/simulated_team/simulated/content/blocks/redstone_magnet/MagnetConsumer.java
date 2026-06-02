/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.blocks.redstone_magnet;

import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetPair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface MagnetConsumer<T extends BlockEntity> {
    public MagnetPair<T> apply(Level var1, BlockPos var2, BlockPos var3);
}
