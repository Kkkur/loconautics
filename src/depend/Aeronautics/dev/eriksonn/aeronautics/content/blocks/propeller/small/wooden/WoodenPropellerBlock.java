/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.small.wooden;

import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity;
import dev.eriksonn.aeronautics.index.AeroBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WoodenPropellerBlock
extends BasePropellerBlock {
    public WoodenPropellerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockEntityType<? extends BasePropellerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AeroBlockEntityTypes.WOODEN_PROPELLER.get();
    }
}
