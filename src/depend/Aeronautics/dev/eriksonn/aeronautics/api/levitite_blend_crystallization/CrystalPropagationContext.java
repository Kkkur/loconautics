/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.utility.BlockHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 */
package dev.eriksonn.aeronautics.api.levitite_blend_crystallization;

import com.simibubi.create.foundation.utility.BlockHelper;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendHelper;
import dev.eriksonn.aeronautics.config.AeroConfig;
import dev.eriksonn.aeronautics.index.AeroTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public interface CrystalPropagationContext {
    public void onCrystallizationInitialize(Level var1, BlockPos var2, boolean var3);

    public void onCrystallize(Level var1, BlockPos var2);

    default public void onDefaultCrystallize(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            level.setBlockAndUpdate(pos, this.getCrystalBlockState(level, pos));
            if (((Boolean)AeroConfig.server().blocks.breakBlocksOnCrystallize.get()).booleanValue()) {
                for (Direction dir : Direction.values()) {
                    if (!level.getBlockState(pos.relative(dir)).is(AeroTags.BlockTags.LEVITITE_BREAKABLE)) continue;
                    boolean shouldBreak = true;
                    for (Direction dir2 : Direction.values()) {
                        if (!level.getFluidState(pos.relative(dir).relative(dir2)).is(LevititeBlendHelper.getFluid())) continue;
                        shouldBreak = false;
                        break;
                    }
                    if (!shouldBreak) continue;
                    BlockHelper.destroyBlock((Level)level, (BlockPos)pos.relative(dir), (float)1.0f);
                }
            }
        }
    }

    public void onCrystallizationFail(Level var1, BlockPos var2, int var3, boolean var4);

    public BlockState getCrystalBlockState(Level var1, BlockPos var2);

    default public int getNewAge(Level level, int attempts, boolean isDormant) {
        return level.random.nextInt(10, 40);
    }

    default public boolean shouldCrystallize(Level level, int attempts, boolean isDormant) {
        float maxAttempts = isDormant ? 10.0f : 5.0f;
        return level.random.nextFloat() < (float)attempts / maxAttempts;
    }

    public boolean canSpreadTo(FluidState var1);

    public CrystalPropagationContext getContextForSpread(Level var1, BlockPos var2);

    public TagKey<Block> getCatalyzerTag();
}
