/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package dev.ryanhcode.offroad.mixin_interface.level_renderer;

import dev.ryanhcode.offroad.handlers.client.MultiMiningClientHandler;
import java.util.Map;
import net.minecraft.core.BlockPos;

public interface MultiMiningDestructionExtension {
    public void offroad$manuallyAddMultiDestructionProgress(int var1, Map<BlockPos, MultiMiningClientHandler.ClientBlockBreakingData> var2);
}
