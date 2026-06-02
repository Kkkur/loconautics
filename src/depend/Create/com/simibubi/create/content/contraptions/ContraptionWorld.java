/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.levelWrappers.WrappedLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.content.contraptions.Contraption;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class ContraptionWorld
extends WrappedLevel {
    final Contraption contraption;
    private final int minY;
    private final int height;

    public ContraptionWorld(Level world, Contraption contraption) {
        super(world);
        this.contraption = contraption;
        this.minY = ContraptionWorld.nextMultipleOf16(contraption.bounds.minY - 1.0);
        this.height = ContraptionWorld.nextMultipleOf16(contraption.bounds.maxY + 1.0) - this.minY;
    }

    private static int nextMultipleOf16(double a) {
        return ((Math.abs((int)a) - 1 | 0xF) + 1) * Mth.sign((double)a);
    }

    public BlockState getBlockState(BlockPos pos) {
        StructureTemplate.StructureBlockInfo blockInfo = this.contraption.getBlocks().get(pos);
        if (blockInfo != null) {
            return blockInfo.state();
        }
        return Blocks.AIR.defaultBlockState();
    }

    public void playLocalSound(double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay) {
        this.level.playLocalSound(x, y, z, sound, category, volume, pitch, distanceDelay);
    }

    public int getHeight() {
        return this.height;
    }

    public int getMinBuildHeight() {
        return this.minY;
    }
}
