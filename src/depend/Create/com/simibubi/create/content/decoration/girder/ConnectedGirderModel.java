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
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.client.model.data.ModelData$Builder
 *  net.neoforged.neoforge.client.model.data.ModelProperty
 */
package com.simibubi.create.content.decoration.girder;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.decoration.girder.GirderCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class ConnectedGirderModel
extends CTModel {
    protected static final ModelProperty<ConnectionData> CONNECTION_PROPERTY = new ModelProperty();

    public ConnectedGirderModel(BakedModel originalModel) {
        super(originalModel, new GirderCTBehaviour());
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        super.gatherModelData(builder, world, pos, state, blockEntityData);
        ConnectionData connectionData = new ConnectionData();
        for (Direction d : Iterate.horizontalDirections) {
            connectionData.setConnected(d, GirderBlock.isConnected(world, pos, state, d));
        }
        return builder.with(CONNECTION_PROPERTY, (Object)connectionData);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
        List<BakedQuad> superQuads = super.getQuads(state, side, rand, extraData, renderType);
        if (side != null || !extraData.has(CONNECTION_PROPERTY)) {
            return superQuads;
        }
        ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>(superQuads);
        ConnectionData data = (ConnectionData)extraData.get(CONNECTION_PROPERTY);
        for (Direction d : Iterate.horizontalDirections) {
            if (!data.isConnected(d)) continue;
            quads.addAll(AllPartialModels.METAL_GIRDER_BRACKETS.get(d).get().getQuads(state, side, rand, extraData, renderType));
        }
        return quads;
    }

    private static class ConnectionData {
        boolean[] connectedFaces = new boolean[4];

        public ConnectionData() {
            Arrays.fill(this.connectedFaces, false);
        }

        void setConnected(Direction face, boolean connected) {
            this.connectedFaces[face.get2DDataValue()] = connected;
        }

        boolean isConnected(Direction face) {
            return this.connectedFaces[face.get2DDataValue()];
        }
    }
}
