/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import java.util.BitSet;
import java.util.HashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class CarriageContraption.CarriageClientContraption
extends ClientContraption {
    public final BitSet scratchBlockEntitiesOutsidePortal;

    public CarriageContraption.CarriageClientContraption(CarriageContraption contraption) {
        super(contraption);
        this.scratchBlockEntitiesOutsidePortal = new BitSet();
    }

    @Override
    public ClientContraption.RenderedBlocks getRenderedBlocks() {
        if (CarriageContraption.this.notInPortal()) {
            return super.getRenderedBlocks();
        }
        HashMap values = new HashMap();
        CarriageContraption.this.blocks.forEach((pos, info) -> {
            if (CarriageContraption.this.withinVisible((BlockPos)pos)) {
                values.put(pos, info.state());
            } else if (CarriageContraption.this.atSeam((BlockPos)pos)) {
                values.put(pos, Blocks.PURPLE_STAINED_GLASS.defaultBlockState());
            }
        });
        return new ClientContraption.RenderedBlocks(pos -> values.getOrDefault(pos, Blocks.AIR.defaultBlockState()), values.keySet());
    }

    @Override
    public BlockEntity readBlockEntity(Level level, StructureTemplate.StructureBlockInfo info, boolean legacy) {
        AbstractBogeyBlock bogey;
        Block block = info.state().getBlock();
        if (block instanceof AbstractBogeyBlock && !(bogey = (AbstractBogeyBlock)block).captureBlockEntityForTrain()) {
            return null;
        }
        return super.readBlockEntity(level, info, legacy);
    }

    @Override
    public BitSet getAndAdjustShouldRenderBlockEntities() {
        if (CarriageContraption.this.notInPortal()) {
            return super.getAndAdjustShouldRenderBlockEntities();
        }
        this.scratchBlockEntitiesOutsidePortal.clear();
        this.scratchBlockEntitiesOutsidePortal.or(this.shouldRenderBlockEntities);
        for (int i = 0; i < this.renderedBlockEntityView.size(); ++i) {
            BlockEntity be = (BlockEntity)this.renderedBlockEntityView.get(i);
            if (!CarriageContraption.this.isHiddenInPortal(be.getBlockPos())) continue;
            this.scratchBlockEntitiesOutsidePortal.clear(i);
        }
        return this.scratchBlockEntitiesOutsidePortal;
    }
}
