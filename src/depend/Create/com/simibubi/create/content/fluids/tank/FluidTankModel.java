/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.client.model.data.ModelData$Builder
 *  net.neoforged.neoforge.client.model.data.ModelProperty
 */
package com.simibubi.create.content.fluids.tank;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class FluidTankModel
extends CTModel {
    protected static final ModelProperty<CullData> CULL_PROPERTY = new ModelProperty();

    public static FluidTankModel standard(BakedModel originalModel) {
        return new FluidTankModel(originalModel, AllSpriteShifts.FLUID_TANK, AllSpriteShifts.FLUID_TANK_TOP, AllSpriteShifts.FLUID_TANK_INNER);
    }

    public static FluidTankModel creative(BakedModel originalModel) {
        return new FluidTankModel(originalModel, AllSpriteShifts.CREATIVE_FLUID_TANK, AllSpriteShifts.CREATIVE_CASING, AllSpriteShifts.CREATIVE_CASING);
    }

    private FluidTankModel(BakedModel originalModel, CTSpriteShiftEntry side, CTSpriteShiftEntry top, CTSpriteShiftEntry inner) {
        super(originalModel, new FluidTankCTBehaviour(side, top, inner));
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        super.gatherModelData(builder, world, pos, state, blockEntityData);
        CullData cullData = new CullData();
        for (Direction d : Iterate.horizontalDirections) {
            cullData.setCulled(d, ConnectivityHandler.isConnected((BlockGetter)world, pos, pos.relative(d)));
        }
        return builder.with(CULL_PROPERTY, (Object)cullData);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
        if (side != null) {
            return Collections.emptyList();
        }
        ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>();
        for (Direction d : Iterate.directions) {
            if (extraData.has(CULL_PROPERTY) && ((CullData)extraData.get(CULL_PROPERTY)).isCulled(d)) continue;
            quads.addAll(super.getQuads(state, d, rand, extraData, renderType));
        }
        quads.addAll(super.getQuads(state, null, rand, extraData, renderType));
        return quads;
    }

    private static class CullData {
        boolean[] culledFaces = new boolean[4];

        public CullData() {
            Arrays.fill(this.culledFaces, false);
        }

        void setCulled(Direction face, boolean cull) {
            if (face.getAxis().isVertical()) {
                return;
            }
            this.culledFaces[face.get2DDataValue()] = cull;
        }

        boolean isCulled(Direction face) {
            if (face.getAxis().isVertical()) {
                return false;
            }
            return this.culledFaces[face.get2DDataValue()];
        }
    }
}
