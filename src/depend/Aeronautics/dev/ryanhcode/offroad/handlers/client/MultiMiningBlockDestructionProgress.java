/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.BlockDestructionProgress
 */
package dev.ryanhcode.offroad.handlers.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;

public class MultiMiningBlockDestructionProgress
extends BlockDestructionProgress {
    public final Map<BlockPos, BlockDestructionProgress> otherProgresses = new Object2ObjectOpenHashMap();

    public MultiMiningBlockDestructionProgress(int id, BlockPos pos) {
        super(id, pos);
    }
}
