/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  net.createmod.catnip.render.SuperByteBufferCache
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.render;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class ClientContraption {
    private final VirtualRenderWorld renderLevel;
    private final List<BlockEntity> renderedBlockEntities = new ArrayList<BlockEntity>();
    public final List<BlockEntity> renderedBlockEntityView = Collections.unmodifiableList(this.renderedBlockEntities);
    public final BitSet shouldRenderBlockEntities = new BitSet();
    public final BitSet scratchErroredBlockEntities = new BitSet();
    private final ContraptionMatrices matrices = new ContraptionMatrices();
    private final Contraption contraption;
    private int structureVersion = 0;
    private int childrenVersion = 0;

    public ClientContraption(Contraption contraption) {
        Level level = contraption.entity.level();
        this.contraption = contraption;
        BlockPos origin = contraption.anchor;
        int minY = VirtualRenderWorld.nextMultipleOf16(Mth.floor((double)(contraption.bounds.minY - 1.0)));
        int height = VirtualRenderWorld.nextMultipleOf16(Mth.ceil((double)(contraption.bounds.maxY + 1.0))) - minY;
        this.renderLevel = new VirtualRenderWorld(this, level, minY, height, (Vec3i)origin, this::invalidateStructure){

            public boolean supportsVisualization() {
                return VisualizationManager.supportsVisualization((LevelAccessor)this.level);
            }
        };
        this.setupRenderLevelAndRenderedBlockEntities();
    }

    public int structureVersion() {
        return this.structureVersion;
    }

    public int childrenVersion() {
        return this.childrenVersion;
    }

    public void resetRenderLevel() {
        this.renderedBlockEntities.clear();
        this.renderLevel.clear();
        this.shouldRenderBlockEntities.clear();
        this.setupRenderLevelAndRenderedBlockEntities();
        this.invalidateStructure();
        this.invalidateChildren();
    }

    public void invalidateChildren() {
        ++this.childrenVersion;
    }

    public void invalidateStructure() {
        for (RenderType renderType : RenderType.chunkBufferLayers()) {
            SuperByteBufferCache.getInstance().invalidate(ContraptionEntityRenderer.CONTRAPTION, (Object)Pair.of((Object)this.contraption, (Object)renderType));
        }
        ++this.structureVersion;
    }

    private void setupRenderLevelAndRenderedBlockEntities() {
        for (StructureTemplate.StructureBlockInfo info : this.contraption.getBlocks().values()) {
            this.renderLevel.setBlock(info.pos(), info.state(), 0);
            BlockEntity blockEntity = this.readBlockEntity(this.renderLevel, info, this.contraption.getIsLegacy().getBoolean((Object)info.pos()));
            if (blockEntity == null) continue;
            this.renderLevel.setBlockEntity(blockEntity);
            MovementBehaviour movementBehaviour = MovementBehaviour.REGISTRY.get((StateHolder<Block, ?>)info.state());
            if (movementBehaviour != null && movementBehaviour.disableBlockEntityRendering()) continue;
            this.renderedBlockEntities.add(blockEntity);
        }
        this.shouldRenderBlockEntities.set(0, this.renderedBlockEntities.size());
        this.renderLevel.runLightEngine();
    }

    @Nullable
    public BlockEntity readBlockEntity(Level level, StructureTemplate.StructureBlockInfo info, boolean legacy) {
        Block block;
        BlockState state = info.state();
        BlockPos pos = info.pos();
        CompoundTag nbt = info.nbt();
        if (legacy) {
            if (nbt == null) {
                return null;
            }
            nbt.putInt("x", pos.getX());
            nbt.putInt("y", pos.getY());
            nbt.putInt("z", pos.getZ());
            BlockEntity be = BlockEntity.loadStatic((BlockPos)pos, (BlockState)state, (CompoundTag)nbt, (HolderLookup.Provider)level.registryAccess());
            ClientContraption.postprocessReadBlockEntity(level, be, state);
            return be;
        }
        if (!state.hasBlockEntity() || !((block = state.getBlock()) instanceof EntityBlock)) {
            return null;
        }
        EntityBlock entityBlock = (EntityBlock)block;
        BlockEntity be = entityBlock.newBlockEntity(pos, state);
        ClientContraption.postprocessReadBlockEntity(level, be, state);
        if (be != null && nbt != null) {
            be.handleUpdateTag(nbt, (HolderLookup.Provider)level.registryAccess());
        }
        return be;
    }

    protected static void postprocessReadBlockEntity(Level level, @Nullable BlockEntity be, BlockState blockState) {
        if (be != null) {
            be.setLevel(level);
            be.setBlockState(blockState);
            if (be instanceof KineticBlockEntity) {
                KineticBlockEntity kbe = (KineticBlockEntity)be;
                kbe.setSpeed(0.0f);
            }
        }
    }

    public VirtualRenderWorld getRenderLevel() {
        return this.renderLevel;
    }

    public ContraptionMatrices getMatrices() {
        return this.matrices;
    }

    public RenderedBlocks getRenderedBlocks() {
        return new RenderedBlocks(pos -> {
            StructureTemplate.StructureBlockInfo info = this.contraption.getBlocks().get(pos);
            if (info == null) {
                return Blocks.AIR.defaultBlockState();
            }
            return info.state();
        }, this.contraption.getBlocks().keySet());
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos localPos) {
        return this.renderLevel.getBlockEntity(localPos);
    }

    public BitSet getAndAdjustShouldRenderBlockEntities() {
        return this.shouldRenderBlockEntities;
    }

    public record RenderedBlocks(Function<BlockPos, BlockState> lookup, Iterable<BlockPos> positions) {
    }
}
