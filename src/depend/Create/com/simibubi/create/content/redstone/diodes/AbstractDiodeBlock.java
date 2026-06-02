/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.DiodeBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.redstone.diodes;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractDiodeBlock
extends DiodeBlock
implements IWrenchable {
    public AbstractDiodeBlock(BlockBehaviour.Properties builder) {
        super(builder);
    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }
}
