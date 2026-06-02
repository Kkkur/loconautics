/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.small.andesite;

import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity;
import dev.eriksonn.aeronautics.index.AeroBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class AndesitePropellerBlock
extends BasePropellerBlock {
    public AndesitePropellerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockEntityType<? extends BasePropellerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AeroBlockEntityTypes.ANDESITE_PROPELLER.get();
    }
}
