/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.render.VirtualRenderHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.client.model.BakedModelWrapper
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.client.model.data.ModelProperty
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.Collections;
import java.util.List;
import net.createmod.ponder.render.VirtualRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class BracketedKineticBlockModel
extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<BracketedModelData> BRACKET_PROPERTY = new ModelProperty();

    public BracketedKineticBlockModel(BakedModel template) {
        super(template);
    }

    public ModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        if (VirtualRenderHelper.isVirtual((ModelData)blockEntityData)) {
            return blockEntityData;
        }
        BracketedModelData data = new BracketedModelData();
        BracketedBlockEntityBehaviour attachmentBehaviour = BlockEntityBehaviour.get((BlockGetter)world, pos, BracketedBlockEntityBehaviour.TYPE);
        if (attachmentBehaviour != null) {
            data.putBracket(attachmentBehaviour.getBracket());
        }
        return ModelData.builder().with(BRACKET_PROPERTY, (Object)data).build();
    }

    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
        if (!VirtualRenderHelper.isVirtual((ModelData)data)) {
            BracketedModelData pipeData;
            BakedModel bracket;
            if (data.has(BRACKET_PROPERTY) && (bracket = (pipeData = (BracketedModelData)data.get(BRACKET_PROPERTY)).getBracket()) != null) {
                return bracket.getQuads(state, side, rand, data, renderType);
            }
            return Collections.emptyList();
        }
        return super.getQuads(state, side, rand, data, renderType);
    }

    private static class BracketedModelData {
        private BakedModel bracket;

        private BracketedModelData() {
        }

        public void putBracket(BlockState state) {
            if (state != null) {
                this.bracket = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            }
        }

        public BakedModel getBracket() {
            return this.bracket;
        }
    }
}
