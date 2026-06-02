/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.client.model.data.ModelData$Builder
 *  net.neoforged.neoforge.client.model.data.ModelProperty
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.tableCloth;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlock;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TableClothModel
extends BakedModelWrapperWithData {
    private static final ModelProperty<CullData> CULL_PROPERTY = new ModelProperty();
    private static final Map<TableClothBlock, List<List<BakedQuad>>> CORNERS = new HashMap<TableClothBlock, List<List<BakedQuad>>>();

    public TableClothModel(BakedModel originalModel) {
        super(originalModel);
    }

    public static void reload() {
        CORNERS.clear();
    }

    public boolean useAmbientOcclusion() {
        return false;
    }

    private List<BakedQuad> getCorner(TableClothBlock block, int corner, @NotNull RandomSource rand, @Nullable RenderType renderType) {
        if (!CORNERS.containsKey(block)) {
            TextureAtlasSprite targetSprite = this.getParticleIcon(ModelData.EMPTY);
            ArrayList<List<BakedQuad>> list = new ArrayList<List<BakedQuad>>();
            for (PartialModel pm : List.of(AllPartialModels.TABLE_CLOTH_SW, AllPartialModels.TABLE_CLOTH_NW, AllPartialModels.TABLE_CLOTH_NE, AllPartialModels.TABLE_CLOTH_SE)) {
                list.add(this.getCornerQuads(rand, renderType, targetSprite, pm));
            }
            CORNERS.put(block, list);
        }
        return CORNERS.get(block).get(corner);
    }

    private List<BakedQuad> getCornerQuads(RandomSource rand, RenderType renderType, TextureAtlasSprite targetSprite, PartialModel pm) {
        ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>();
        for (BakedQuad quad : pm.get().getQuads(null, null, rand, ModelData.EMPTY, renderType)) {
            TextureAtlasSprite original = quad.getSprite();
            BakedQuad newQuad = BakedQuadHelper.clone(quad);
            int[] vertexData = newQuad.getVertices();
            for (int vertex = 0; vertex < 4; ++vertex) {
                BakedQuadHelper.setU(vertexData, vertex, targetSprite.getU(SpriteShiftEntry.getUnInterpolatedU((TextureAtlasSprite)original, (float)BakedQuadHelper.getU(vertexData, vertex))));
                BakedQuadHelper.setV(vertexData, vertex, targetSprite.getV(SpriteShiftEntry.getUnInterpolatedV((TextureAtlasSprite)original, (float)BakedQuadHelper.getV(vertexData, vertex))));
            }
            quads.add(newQuad);
        }
        return quads;
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        ArrayList<Direction> culledSides = new ArrayList<Direction>();
        for (Direction side : Iterate.horizontalDirections) {
            if (Block.shouldRenderFace((BlockState)state, (BlockGetter)world, (BlockPos)pos, (Direction)side, (BlockPos)pos.relative(side))) continue;
            culledSides.add(side);
        }
        if (culledSides.isEmpty()) {
            return builder;
        }
        return builder.with(CULL_PROPERTY, (Object)new CullData(EnumSet.copyOf(culledSides)));
    }

    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        Block block;
        @NotNull List mainQuads = super.getQuads(state, side, rand, extraData, renderType);
        if (side == null || side.getAxis() == Direction.Axis.Y) {
            return mainQuads;
        }
        CullData cullData = (CullData)extraData.get(CULL_PROPERTY);
        if (cullData != null && cullData.culled().contains(side.getClockWise())) {
            return mainQuads;
        }
        if (state == null || !((block = state.getBlock()) instanceof TableClothBlock)) {
            return mainQuads;
        }
        TableClothBlock dcb = (TableClothBlock)block;
        ArrayList<BakedQuad> copyOf = new ArrayList<BakedQuad>(mainQuads);
        copyOf.addAll(this.getCorner(dcb, side.get2DDataValue(), rand, renderType));
        return copyOf;
    }

    private record CullData(EnumSet<Direction> culled) {
    }
}
