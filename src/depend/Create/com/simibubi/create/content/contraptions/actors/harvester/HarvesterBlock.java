/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.contraptions.actors.harvester;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.contraptions.actors.AttachedActorBlock;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public class HarvesterBlock
extends AttachedActorBlock
implements IBE<HarvesterBlockEntity> {
    public static final MapCodec<HarvesterBlock> CODEC = HarvesterBlock.simpleCodec(HarvesterBlock::new);

    public HarvesterBlock(BlockBehaviour.Properties p_i48377_1_) {
        super(p_i48377_1_);
    }

    @Override
    public Class<HarvesterBlockEntity> getBlockEntityClass() {
        return HarvesterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HarvesterBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.HARVESTER.get();
    }

    @NotNull
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
