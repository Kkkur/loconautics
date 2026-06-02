/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package com.simibubi.create.content.logistics.crate;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.logistics.crate.CrateBlock;
import com.simibubi.create.content.logistics.crate.CreativeCrateBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CreativeCrateBlock
extends CrateBlock
implements IBE<CreativeCrateBlockEntity> {
    public CreativeCrateBlock(BlockBehaviour.Properties p_i48415_1_) {
        super(p_i48415_1_);
    }

    @Override
    public Class<CreativeCrateBlockEntity> getBlockEntityClass() {
        return CreativeCrateBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreativeCrateBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.CREATIVE_CRATE.get();
    }
}
