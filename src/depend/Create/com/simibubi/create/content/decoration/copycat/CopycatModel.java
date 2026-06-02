/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
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
 *  net.neoforged.neoforge.client.model.QuadTransformers
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.client.model.data.ModelData$Builder
 *  net.neoforged.neoforge.client.model.data.ModelProperty
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.decoration.copycat;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.decoration.copycat.FilteredBlockAndTintGetter;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

public abstract class CopycatModel
extends BakedModelWrapperWithData {
    public static final ModelProperty<BlockState> MATERIAL_PROPERTY = new ModelProperty();
    private static final ModelProperty<OcclusionData> OCCLUSION_PROPERTY = new ModelProperty();
    private static final ModelProperty<ModelData> WRAPPED_DATA_PROPERTY = new ModelProperty();
    private static final ModelProperty<Boolean> IS_EMISSIVE_PROPERTY = new ModelProperty();

    public CopycatModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        BlockState material = CopycatModel.getMaterial(blockEntityData);
        builder.with(MATERIAL_PROPERTY, (Object)material);
        Block block = state.getBlock();
        if (!(block instanceof CopycatBlock)) {
            return builder;
        }
        CopycatBlock copycatBlock = (CopycatBlock)block;
        OcclusionData occlusionData = new OcclusionData();
        this.gatherOcclusionData(world, pos, state, material, occlusionData, copycatBlock);
        builder.with(OCCLUSION_PROPERTY, (Object)occlusionData);
        ModelData wrappedData = CopycatModel.getModelOf(material).getModelData((BlockAndTintGetter)new FilteredBlockAndTintGetter(world, targetPos -> copycatBlock.canConnectTexturesToward(world, pos, (BlockPos)targetPos, state)), pos, material, ModelData.EMPTY);
        builder.with(WRAPPED_DATA_PROPERTY, (Object)wrappedData);
        boolean isEmissive = material.emissiveRendering((BlockGetter)world, pos);
        builder.with(IS_EMISSIVE_PROPERTY, (Object)isEmissive);
        return builder;
    }

    private void gatherOcclusionData(BlockAndTintGetter level, BlockPos pos, BlockState state, BlockState material, OcclusionData occlusionData, CopycatBlock copycatBlock) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction face : Iterate.directions) {
            BlockPos.MutableBlockPos neighbourPos = mutablePos.setWithOffset((Vec3i)pos, face);
            BlockState neighbourState = level.getBlockState((BlockPos)neighbourPos);
            if (state.supportsExternalFaceHiding() && neighbourState.hidesNeighborFace((BlockGetter)level, (BlockPos)neighbourPos, state, face.getOpposite())) {
                occlusionData.occlude(face);
                continue;
            }
            if (!copycatBlock.canFaceBeOccluded(state, face) || Block.shouldRenderFace((BlockState)material, (BlockGetter)level, (BlockPos)pos, (Direction)face, (BlockPos)neighbourPos)) continue;
            occlusionData.occlude(face);
        }
    }

    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
        return this.getCroppedQuads(state, side, rand, CopycatModel.getMaterial(ModelData.EMPTY), ModelData.EMPTY, RenderType.cutoutMipped());
    }

    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
        Block block;
        CopycatBlock ccb;
        Block block2;
        if (side != null && (block2 = state.getBlock()) instanceof CopycatBlock && (ccb = (CopycatBlock)block2).shouldFaceAlwaysRender(state, side)) {
            return Collections.emptyList();
        }
        BlockState material = CopycatModel.getMaterial(data);
        if (material == null) {
            return super.getQuads(state, side, rand, data, renderType);
        }
        OcclusionData occlusionData = (OcclusionData)data.get(OCCLUSION_PROPERTY);
        if (occlusionData != null && occlusionData.isOccluded(side)) {
            return super.getQuads(state, side, rand, data, renderType);
        }
        ModelData wrappedData = (ModelData)data.get(WRAPPED_DATA_PROPERTY);
        if (wrappedData == null) {
            wrappedData = ModelData.EMPTY;
        }
        if (renderType != null && !Minecraft.getInstance().getBlockRenderer().getBlockModel(material).getRenderTypes(material, rand, wrappedData).contains(renderType)) {
            return super.getQuads(state, side, rand, data, renderType);
        }
        List<BakedQuad> croppedQuads = this.getCroppedQuads(state, side, rand, material, wrappedData, renderType);
        if (side == null && (block = state.getBlock()) instanceof CopycatBlock) {
            CopycatBlock ccb2 = (CopycatBlock)block;
            boolean immutable = true;
            for (Direction nonOcclusionSide : Iterate.directions) {
                if (!ccb2.shouldFaceAlwaysRender(state, nonOcclusionSide)) continue;
                if (immutable) {
                    croppedQuads = new ArrayList<BakedQuad>(croppedQuads);
                    immutable = false;
                }
                croppedQuads.addAll(this.getCroppedQuads(state, nonOcclusionSide, rand, material, wrappedData, renderType));
            }
        }
        if (Boolean.TRUE.equals(data.get(IS_EMISSIVE_PROPERTY))) {
            QuadTransformers.settingMaxEmissivity().processInPlace(croppedQuads);
        }
        return croppedQuads;
    }

    protected abstract List<BakedQuad> getCroppedQuads(BlockState var1, Direction var2, RandomSource var3, BlockState var4, ModelData var5, RenderType var6);

    public TextureAtlasSprite getParticleIcon(ModelData data) {
        BlockState material = CopycatModel.getMaterial(data);
        ModelData wrappedData = (ModelData)data.get(WRAPPED_DATA_PROPERTY);
        if (wrappedData == null) {
            wrappedData = ModelData.EMPTY;
        }
        return CopycatModel.getModelOf(material).getParticleIcon(wrappedData);
    }

    @NotNull
    public static BlockState getMaterial(ModelData data) {
        BlockState material = data == null ? null : (BlockState)data.get(MATERIAL_PROPERTY);
        return material == null ? AllBlocks.COPYCAT_BASE.getDefaultState() : material;
    }

    public static BakedModel getModelOf(BlockState state) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    }

    private static class OcclusionData {
        private final boolean[] occluded = new boolean[6];

        public void occlude(Direction face) {
            this.occluded[face.get3DDataValue()] = true;
        }

        public boolean isOccluded(Direction face) {
            return face != null && this.occluded[face.get3DDataValue()];
        }
    }
}
