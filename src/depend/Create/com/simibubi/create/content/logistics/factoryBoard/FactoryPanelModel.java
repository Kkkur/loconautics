/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.client.model.data.ModelData$Builder
 *  net.neoforged.neoforge.client.model.data.ModelProperty
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class FactoryPanelModel
extends BakedModelWrapperWithData {
    private static final ModelProperty<FactoryPanelModelData> PANEL_PROPERTY = new ModelProperty();

    public FactoryPanelModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        FactoryPanelModelData data = new FactoryPanelModelData();
        for (FactoryPanelBlock.PanelSlot slot : FactoryPanelBlock.PanelSlot.values()) {
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(world, new FactoryPanelPosition(pos, slot));
            if (behaviour == null) continue;
            data.states.put(slot, behaviour.count == 0 ? FactoryPanelBlock.PanelState.PASSIVE : FactoryPanelBlock.PanelState.ACTIVE);
            data.type = behaviour.panelBE().restocker ? FactoryPanelBlock.PanelType.PACKAGER : FactoryPanelBlock.PanelType.NETWORK;
        }
        data.ponder = world instanceof PonderLevel;
        return builder.with(PANEL_PROPERTY, (Object)data);
    }

    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
        if (side != null || !data.has(PANEL_PROPERTY)) {
            return Collections.emptyList();
        }
        FactoryPanelModelData modelData = (FactoryPanelModelData)data.get(PANEL_PROPERTY);
        ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>(super.getQuads(state, null, rand, data, renderType));
        for (FactoryPanelBlock.PanelSlot panelSlot : FactoryPanelBlock.PanelSlot.values()) {
            if (!modelData.states.containsKey((Object)panelSlot)) continue;
            this.addPanel(quads, state, panelSlot, modelData.type, modelData.states.get((Object)panelSlot), rand, data, renderType, modelData.ponder);
        }
        return quads;
    }

    public void addPanel(List<BakedQuad> quads, BlockState state, FactoryPanelBlock.PanelSlot slot, FactoryPanelBlock.PanelType type, FactoryPanelBlock.PanelState panelState, RandomSource rand, ModelData data, RenderType renderType, boolean ponder) {
        PartialModel factoryPanel = panelState == FactoryPanelBlock.PanelState.PASSIVE ? (type == FactoryPanelBlock.PanelType.NETWORK ? AllPartialModels.FACTORY_PANEL : AllPartialModels.FACTORY_PANEL_RESTOCKER) : (type == FactoryPanelBlock.PanelType.NETWORK ? AllPartialModels.FACTORY_PANEL_WITH_BULB : AllPartialModels.FACTORY_PANEL_RESTOCKER_WITH_BULB);
        List quadsToAdd = factoryPanel.get().getQuads(state, null, rand, data, RenderType.solid());
        float xRot = 57.295776f * FactoryPanelBlock.getXRot(state);
        float yRot = 57.295776f * FactoryPanelBlock.getYRot(state);
        for (BakedQuad bakedQuad : quadsToAdd) {
            int[] vertices = bakedQuad.getVertices();
            int[] transformedVertices = Arrays.copyOf(vertices, vertices.length);
            Vec3 quadNormal = Vec3.atLowerCornerOf((Vec3i)bakedQuad.getDirection().getNormal());
            quadNormal = VecHelper.rotate((Vec3)quadNormal, (double)180.0, (Direction.Axis)Direction.Axis.Y);
            quadNormal = VecHelper.rotate((Vec3)quadNormal, (double)(xRot + 90.0f), (Direction.Axis)Direction.Axis.X);
            quadNormal = VecHelper.rotate((Vec3)quadNormal, (double)yRot, (Direction.Axis)Direction.Axis.Y);
            for (int i = 0; i < vertices.length / BakedQuadHelper.VERTEX_STRIDE; ++i) {
                Vec3 vertex = BakedQuadHelper.getXYZ(vertices, i);
                Vec3 normal = BakedQuadHelper.getNormalXYZ(vertices, i);
                vertex = vertex.add((double)slot.xOffset * 0.5, 0.0, (double)slot.yOffset * 0.5);
                vertex = VecHelper.rotateCentered((Vec3)vertex, (double)180.0, (Direction.Axis)Direction.Axis.Y);
                vertex = VecHelper.rotateCentered((Vec3)vertex, (double)(xRot + 90.0f), (Direction.Axis)Direction.Axis.X);
                vertex = VecHelper.rotateCentered((Vec3)vertex, (double)yRot, (Direction.Axis)Direction.Axis.Y);
                normal = VecHelper.rotate((Vec3)normal, (double)180.0, (Direction.Axis)Direction.Axis.Y);
                normal = VecHelper.rotate((Vec3)normal, (double)(xRot + 90.0f), (Direction.Axis)Direction.Axis.X);
                normal = VecHelper.rotate((Vec3)normal, (double)yRot, (Direction.Axis)Direction.Axis.Y);
                BakedQuadHelper.setXYZ(transformedVertices, i, vertex);
                BakedQuadHelper.setNormalXYZ(transformedVertices, i, new Vec3(0.0, 1.0, 0.0));
            }
            Direction newNormal = Direction.fromDelta((int)((int)Math.round(quadNormal.x)), (int)((int)Math.round(quadNormal.y)), (int)((int)Math.round(quadNormal.z)));
            quads.add(new BakedQuad(transformedVertices, bakedQuad.getTintIndex(), newNormal, bakedQuad.getSprite(), !ponder && bakedQuad.isShade()));
        }
    }

    private static class FactoryPanelModelData {
        public FactoryPanelBlock.PanelType type;
        public EnumMap<FactoryPanelBlock.PanelSlot, FactoryPanelBlock.PanelState> states = new EnumMap(FactoryPanelBlock.PanelSlot.class);
        private boolean ponder;

        private FactoryPanelModelData() {
        }
    }
}
