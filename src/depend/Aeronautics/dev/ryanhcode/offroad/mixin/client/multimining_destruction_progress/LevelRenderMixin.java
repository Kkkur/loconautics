/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.BlockDestructionProgress
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.ryanhcode.offroad.mixin.client.multimining_destruction_progress;

import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.offroad.handlers.client.MultiMiningBlockDestructionProgress;
import dev.ryanhcode.offroad.handlers.client.MultiMiningClientHandler;
import dev.ryanhcode.offroad.mixin_interface.level_renderer.MultiMiningDestructionExtension;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={LevelRenderer.class})
public abstract class LevelRenderMixin
implements MultiMiningDestructionExtension {
    @Shadow
    @Final
    private Int2ObjectMap<BlockDestructionProgress> destroyingBlocks;
    @Shadow
    @Final
    private Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;
    @Shadow
    private int ticks;

    @WrapMethod(method={"removeProgress"})
    private void offroad$handleMultiMiningProgressRemoval(BlockDestructionProgress progress, Operation<Void> original) {
        if (progress instanceof MultiMiningBlockDestructionProgress) {
            MultiMiningBlockDestructionProgress mmProgress = (MultiMiningBlockDestructionProgress)progress;
            if (!mmProgress.otherProgresses.isEmpty()) {
                for (BlockDestructionProgress innerProgress : mmProgress.otherProgresses.values()) {
                    original.call(new Object[]{innerProgress});
                }
                mmProgress.otherProgresses.clear();
            }
        } else {
            original.call(new Object[]{progress});
        }
    }

    @Override
    public void offroad$manuallyAddMultiDestructionProgress(int id, Map<BlockPos, MultiMiningClientHandler.ClientBlockBreakingData> clientData) {
        BlockDestructionProgress multiMineBlockHolder = (BlockDestructionProgress)this.destroyingBlocks.computeIfAbsent(id, $ -> new MultiMiningBlockDestructionProgress(id, BlockPos.ZERO));
        if (multiMineBlockHolder instanceof MultiMiningBlockDestructionProgress) {
            MultiMiningBlockDestructionProgress mmProgress = (MultiMiningBlockDestructionProgress)multiMineBlockHolder;
            for (Map.Entry<BlockPos, MultiMiningClientHandler.ClientBlockBreakingData> clientSet : clientData.entrySet()) {
                mmProgress.otherProgresses.computeIfAbsent(clientSet.getKey(), pos -> new BlockDestructionProgress(id, (BlockPos)clientSet.getKey())).setProgress((int)((byte)clientSet.getValue().destroyProgress));
            }
            Iterator<Map.Entry<BlockPos, BlockDestructionProgress>> iter = mmProgress.otherProgresses.entrySet().iterator();
            while (iter.hasNext()) {
                BlockDestructionProgress blockDestructionProgress = iter.next().getValue();
                if (blockDestructionProgress.getProgress() < 0 || blockDestructionProgress.getProgress() >= 10) {
                    this.offroad$removeProgress(blockDestructionProgress);
                    iter.remove();
                    continue;
                }
                ((SortedSet)this.destructionProgress.computeIfAbsent(blockDestructionProgress.getPos().asLong(), l -> Sets.newTreeSet())).add(blockDestructionProgress);
            }
            mmProgress.updateTick(this.ticks);
        }
    }

    @Unique
    private void offroad$removeProgress(BlockDestructionProgress innerProgress) {
        long progressID = innerProgress.getPos().asLong();
        SortedSet progressSet = (SortedSet)this.destructionProgress.get(progressID);
        if (progressSet != null) {
            progressSet.remove(innerProgress);
            if (progressSet.isEmpty()) {
                this.destructionProgress.remove(progressID);
            }
        }
    }
}
