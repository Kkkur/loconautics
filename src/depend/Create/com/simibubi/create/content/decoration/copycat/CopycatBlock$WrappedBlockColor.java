/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.color.block.BlockColor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.GrassColor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.decoration.copycat;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(value=Dist.CLIENT)
public static class CopycatBlock.WrappedBlockColor
implements BlockColor {
    public int getColor(BlockState pState, @Nullable BlockAndTintGetter pLevel, @Nullable BlockPos pPos, int pTintIndex) {
        if (pLevel == null || pPos == null) {
            return GrassColor.get((double)0.5, (double)1.0);
        }
        return Minecraft.getInstance().getBlockColors().getColor(CopycatBlock.getMaterial((BlockGetter)pLevel, pPos), pLevel, pPos, pTintIndex);
    }
}
