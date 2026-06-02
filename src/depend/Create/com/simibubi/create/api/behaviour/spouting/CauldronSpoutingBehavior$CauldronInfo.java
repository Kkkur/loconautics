/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.api.behaviour.spouting;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record CauldronSpoutingBehavior.CauldronInfo(int amount, BlockState cauldron) {
    public CauldronSpoutingBehavior.CauldronInfo(int amount, Block block) {
        this(amount, block.defaultBlockState());
    }
}
