/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
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
 *  net.neoforged.neoforge.client.ChunkRenderTypeSet
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.client.model.data.ModelData$Builder
 *  net.neoforged.neoforge.client.model.data.ModelProperty
 *  net.neoforged.neoforge.common.util.TriState
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.createmod.catnip.data.Iterate;
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
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.NotNull;

public class PipeAttachmentModel
extends BakedModelWrapperWithData {
    private static final ModelProperty<PipeModelData> PIPE_PROPERTY = new ModelProperty();
    private boolean ao;

    public static PipeAttachmentModel withAO(BakedModel template) {
        return new PipeAttachmentModel(template, true);
    }

    public static PipeAttachmentModel withoutAO(BakedModel template) {
        return new PipeAttachmentModel(template, false);
    }

    public PipeAttachmentModel(BakedModel template, boolean ao) {
        super(template);
        this.ao = ao;
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        PipeModelData data = new PipeModelData();
        FluidTransportBehaviour transport = BlockEntityBehaviour.get((BlockGetter)world, pos, FluidTransportBehaviour.TYPE);
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get((BlockGetter)world, pos, BracketedBlockEntityBehaviour.TYPE);
        if (transport != null) {
            for (Direction d : Iterate.directions) {
                data.putAttachment(d, transport.getRenderedRimAttachment(world, pos, state, d));
            }
        }
        if (bracket != null) {
            data.putBracket(bracket.getBracket());
        }
        data.setEncased(FluidPipeBlock.shouldDrawCasing(world, pos, state));
        return builder.with(PIPE_PROPERTY, (Object)data);
    }

    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        ArrayList<ChunkRenderTypeSet> set = new ArrayList<ChunkRenderTypeSet>();
        set.add(super.getRenderTypes(state, rand, data));
        set.add(AllPartialModels.FLUID_PIPE_CASING.get().getRenderTypes(state, rand, data));
        if (data.has(PIPE_PROPERTY)) {
            PipeModelData pipeData = (PipeModelData)data.get(PIPE_PROPERTY);
            for (Direction d : Iterate.directions) {
                FluidTransportBehaviour.AttachmentTypes type = pipeData.getAttachment(d);
                for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials partial : type.partials) {
                    ChunkRenderTypeSet attachmentRenderTypeSet = AllPartialModels.PIPE_ATTACHMENTS.get((Object)partial).get(d).get().getRenderTypes(state, rand, data);
                    set.add(attachmentRenderTypeSet);
                }
            }
        }
        return ChunkRenderTypeSet.union(set);
    }

    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
        ArrayList<BakedQuad> quads = super.getQuads(state, side, rand, data, renderType);
        if (data.has(PIPE_PROPERTY)) {
            PipeModelData pipeData = (PipeModelData)data.get(PIPE_PROPERTY);
            quads = new ArrayList<BakedQuad>(quads);
            this.addQuads(quads, state, side, rand, data, pipeData, renderType);
        }
        return quads;
    }

    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
        if (this.ao) {
            return TriState.TRUE;
        }
        return TriState.FALSE;
    }

    public boolean useAmbientOcclusion() {
        return this.ao;
    }

    private void addQuads(List<BakedQuad> quads, BlockState state, Direction side, RandomSource rand, ModelData data, PipeModelData pipeData, RenderType renderType) {
        BakedModel bracket = pipeData.getBracket();
        if (bracket != null) {
            quads.addAll(bracket.getQuads(state, side, rand, data, renderType));
        }
        for (Direction d : Iterate.directions) {
            FluidTransportBehaviour.AttachmentTypes type = pipeData.getAttachment(d);
            for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials partial : type.partials) {
                quads.addAll(AllPartialModels.PIPE_ATTACHMENTS.get((Object)partial).get(d).get().getQuads(state, side, rand, data, renderType));
            }
        }
        if (pipeData.isEncased()) {
            quads.addAll(AllPartialModels.FLUID_PIPE_CASING.get().getQuads(state, side, rand, data, renderType));
        }
    }

    private static class PipeModelData {
        private FluidTransportBehaviour.AttachmentTypes[] attachments = new FluidTransportBehaviour.AttachmentTypes[6];
        private boolean encased;
        private BakedModel bracket;

        public PipeModelData() {
            Arrays.fill((Object[])this.attachments, (Object)FluidTransportBehaviour.AttachmentTypes.NONE);
        }

        public void putBracket(BlockState state) {
            if (state != null) {
                this.bracket = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            }
        }

        public BakedModel getBracket() {
            return this.bracket;
        }

        public void putAttachment(Direction face, FluidTransportBehaviour.AttachmentTypes rim) {
            this.attachments[face.get3DDataValue()] = rim;
        }

        public FluidTransportBehaviour.AttachmentTypes getAttachment(Direction face) {
            return this.attachments[face.get3DDataValue()];
        }

        public void setEncased(boolean encased) {
            this.encased = encased;
        }

        public boolean isEncased() {
            return this.encased;
        }
    }
}
