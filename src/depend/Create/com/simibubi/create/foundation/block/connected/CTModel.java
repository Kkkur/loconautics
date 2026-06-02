/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.client.model.data.ModelData$Builder
 *  net.neoforged.neoforge.client.model.data.ModelProperty
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class CTModel
extends BakedModelWrapperWithData {
    private static final ModelProperty<CTData> CT_PROPERTY = new ModelProperty();
    private final ConnectedTextureBehaviour behaviour;

    public CTModel(BakedModel originalModel, ConnectedTextureBehaviour behaviour) {
        super(originalModel);
        this.behaviour = behaviour;
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        return builder.with(CT_PROPERTY, (Object)this.createCTData(world, pos, state));
    }

    protected CTData createCTData(BlockAndTintGetter world, BlockPos pos, BlockState state) {
        CTData data = new CTData();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction face : Iterate.directions) {
            CTType dataType;
            CopycatBlock ufb;
            Block block;
            BlockState actualState = world.getBlockState(pos);
            if (!this.behaviour.buildContextForOccludedDirections() && !Block.shouldRenderFace((BlockState)state, (BlockGetter)world, (BlockPos)pos, (Direction)face, (BlockPos)mutablePos.setWithOffset((Vec3i)pos, face)) && (!((block = actualState.getBlock()) instanceof CopycatBlock) || (ufb = (CopycatBlock)block).canFaceBeOccluded(actualState, face)) || (dataType = this.behaviour.getDataType(world, pos, state, face)) == null) continue;
            ConnectedTextureBehaviour.CTContext context = this.behaviour.buildContext(world, pos, state, face, dataType.getContextRequirement());
            data.put(face, dataType.getTextureIndex(context));
        }
        return data;
    }

    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
        ArrayList<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        if (!extraData.has(CT_PROPERTY)) {
            return quads;
        }
        CTData data = (CTData)extraData.get(CT_PROPERTY);
        quads = new ArrayList<BakedQuad>(quads);
        for (int i = 0; i < quads.size(); ++i) {
            CTSpriteShiftEntry spriteShift;
            BakedQuad quad = (BakedQuad)quads.get(i);
            int index = data.get(quad.getDirection());
            if (index == -1 || (spriteShift = this.behaviour.getShift(state, rand, quad.getDirection(), quad.getSprite())) == null || quad.getSprite() != spriteShift.getOriginal()) continue;
            BakedQuad newQuad = BakedQuadHelper.clone(quad);
            int[] vertexData = newQuad.getVertices();
            for (int vertex = 0; vertex < 4; ++vertex) {
                float u = BakedQuadHelper.getU(vertexData, vertex);
                float v = BakedQuadHelper.getV(vertexData, vertex);
                BakedQuadHelper.setU(vertexData, vertex, spriteShift.getTargetU(u, index));
                BakedQuadHelper.setV(vertexData, vertex, spriteShift.getTargetV(v, index));
            }
            quads.set(i, newQuad);
        }
        return quads;
    }

    private static class CTData {
        private final int[] indices = new int[6];

        public CTData() {
            Arrays.fill(this.indices, -1);
        }

        public void put(Direction face, int texture) {
            this.indices[face.get3DDataValue()] = texture;
        }

        public int get(Direction face) {
            return this.indices[face.get3DDataValue()];
        }
    }
}
