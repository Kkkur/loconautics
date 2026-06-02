/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.data.ModelData
 */
package com.simibubi.create.content.decoration.copycat;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.content.decoration.copycat.CopycatPanelBlock;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CopycatBarsModel
extends CopycatModel {
    public CopycatBarsModel(BakedModel originalModel) {
        super(originalModel);
    }

    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material, ModelData wrappedData, RenderType renderType) {
        boolean vertical;
        BakedModel model = CopycatBarsModel.getModelOf(material);
        List superQuads = this.originalModel.getQuads(state, side, rand, wrappedData, renderType);
        TextureAtlasSprite targetSprite = model.getParticleIcon(wrappedData);
        boolean bl = vertical = ((Direction)state.getValue((Property)CopycatPanelBlock.FACING)).getAxis() == Direction.Axis.Y;
        if (side != null && (vertical || side.getAxis() == Direction.Axis.Y)) {
            List templateQuads = model.getQuads(material, null, rand, wrappedData, renderType);
            for (BakedQuad quad : templateQuads) {
                if (quad.getDirection() != Direction.UP) continue;
                targetSprite = quad.getSprite();
                break;
            }
        }
        if (targetSprite == null) {
            return superQuads;
        }
        ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>();
        for (BakedQuad quad : superQuads) {
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
}
