/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.saw;

import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public static class TreeCutter.Tree
extends AbstractBlockBreakQueue {
    private final List<BlockPos> logs;
    private final List<BlockPos> leaves;
    private final List<BlockPos> attachments;

    public TreeCutter.Tree(List<BlockPos> logs, List<BlockPos> leaves, List<BlockPos> attachments) {
        this.logs = logs;
        this.leaves = leaves;
        this.attachments = attachments;
    }

    @Override
    public void destroyBlocks(Level world, ItemStack toDamage, @Nullable Player playerEntity, BiConsumer<BlockPos, ItemStack> drop) {
        this.attachments.forEach(this.makeCallbackFor(world, 0.03125f, toDamage, playerEntity, drop));
        this.logs.forEach(this.makeCallbackFor(world, 0.5f, toDamage, playerEntity, drop));
        this.leaves.forEach(this.makeCallbackFor(world, 0.125f, toDamage, playerEntity, drop));
    }
}
