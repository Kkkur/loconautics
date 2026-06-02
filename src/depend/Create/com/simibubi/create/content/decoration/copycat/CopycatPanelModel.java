/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.client.model.data.ModelData
 */
package com.simibubi.create.content.decoration.copycat;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.content.decoration.copycat.CopycatPanelBlock;
import com.simibubi.create.content.decoration.copycat.CopycatSpecialCases;
import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CopycatPanelModel
extends CopycatModel {
    protected static final AABB CUBE_AABB = new AABB(BlockPos.ZERO);

    public CopycatPanelModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, RandomSource rand, BlockState material, ModelData wrappedData, RenderType renderType) {
        BakedModel blockModel;
        Direction facing = state.getOptionalValue((Property)CopycatPanelBlock.FACING).orElse(Direction.UP);
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BlockState specialCopycatModelState = null;
        if (CopycatSpecialCases.isBarsMaterial(material)) {
            specialCopycatModelState = AllBlocks.COPYCAT_BARS.getDefaultState();
        }
        if (CopycatSpecialCases.isTrapdoorMaterial(material)) {
            return blockRenderer.getBlockModel(material).getQuads(material, side, rand, wrappedData, renderType);
        }
        if (specialCopycatModelState != null && (blockModel = blockRenderer.getBlockModel((BlockState)specialCopycatModelState.setValue((Property)DirectionalBlock.FACING, (Comparable)facing))) instanceof CopycatModel) {
            CopycatModel cm = (CopycatModel)blockModel;
            return cm.getCroppedQuads(state, side, rand, material, wrappedData, renderType);
        }
        BakedModel model = CopycatPanelModel.getModelOf(material);
        List templateQuads = model.getQuads(material, side, rand, wrappedData, renderType);
        int size = templateQuads.size();
        ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>();
        Vec3 normal = Vec3.atLowerCornerOf((Vec3i)facing.getNormal());
        Vec3 normalScaled14 = normal.scale(0.875);
        for (boolean front : Iterate.trueAndFalse) {
            Vec3 normalScaledN13 = normal.scale(front ? 0.0 : -0.8125);
            float contract = 16 - (front ? 1 : 2);
            AABB bb = CUBE_AABB.contract(normal.x * (double)contract / 16.0, normal.y * (double)contract / 16.0, normal.z * (double)contract / 16.0);
            if (!front) {
                bb = bb.move(normalScaled14);
            }
            for (int i = 0; i < size; ++i) {
                BakedQuad quad = (BakedQuad)templateQuads.get(i);
                Direction direction = quad.getDirection();
                if (front && direction == facing || !front && direction == facing.getOpposite()) continue;
                quads.add(BakedQuadHelper.cloneWithCustomGeometry(quad, BakedModelHelper.cropAndMove(quad.getVertices(), quad.getSprite(), bb, normalScaledN13)));
            }
        }
        return quads;
    }
}
