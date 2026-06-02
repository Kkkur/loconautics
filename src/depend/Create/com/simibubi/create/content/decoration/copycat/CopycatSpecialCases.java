/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.IronBarsBlock
 *  net.minecraft.world.level.block.StainedGlassPaneBlock
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.decoration.copycat;

import com.simibubi.create.content.decoration.palettes.GlassPaneBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class CopycatSpecialCases {
    public static boolean isBarsMaterial(BlockState material) {
        return material.getBlock() instanceof IronBarsBlock && !(material.getBlock() instanceof GlassPaneBlock) && !(material.getBlock() instanceof StainedGlassPaneBlock) && material.getBlock() != Blocks.GLASS_PANE;
    }

    public static boolean isTrapdoorMaterial(BlockState material) {
        return material.getBlock() instanceof TrapDoorBlock && material.hasProperty((Property)TrapDoorBlock.HALF) && material.hasProperty((Property)TrapDoorBlock.OPEN) && material.hasProperty((Property)TrapDoorBlock.FACING);
    }
}
