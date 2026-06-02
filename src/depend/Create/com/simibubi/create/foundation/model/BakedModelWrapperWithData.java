/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.client.model.BakedModelWrapper
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.client.model.data.ModelData$Builder
 */
package com.simibubi.create.foundation.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

public abstract class BakedModelWrapperWithData
extends BakedModelWrapper<BakedModel> {
    public BakedModelWrapperWithData(BakedModel originalModel) {
        super(originalModel);
    }

    public final ModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        ModelData.Builder builder = ModelData.builder();
        if (this.originalModel instanceof BakedModelWrapperWithData) {
            ((BakedModelWrapperWithData)this.originalModel).gatherModelData(builder, world, pos, state, blockEntityData);
        }
        this.gatherModelData(builder, world, pos, state, blockEntityData);
        return builder.build();
    }

    protected abstract ModelData.Builder gatherModelData(ModelData.Builder var1, BlockAndTintGetter var2, BlockPos var3, BlockState var4, ModelData var5);
}
