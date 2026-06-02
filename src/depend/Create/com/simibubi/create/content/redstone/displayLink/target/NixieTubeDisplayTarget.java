/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package com.simibubi.create.content.redstone.displayLink.target;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.SingleLineDisplayTarget;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

public class NixieTubeDisplayTarget
extends SingleLineDisplayTarget {
    @Override
    protected void acceptLine(MutableComponent text, DisplayLinkContext context) {
        String tagElement = Component.Serializer.toJson((Component)text, (HolderLookup.Provider)context.level().registryAccess());
        NixieTubeBlock.walkNixies((LevelAccessor)context.level(), context.getTargetPos(), false, (currentPos, rowPosition) -> {
            BlockEntity blockEntity = context.level().getBlockEntity(currentPos);
            if (blockEntity instanceof NixieTubeBlockEntity) {
                NixieTubeBlockEntity nixie = (NixieTubeBlockEntity)blockEntity;
                nixie.displayCustomText(tagElement, (int)rowPosition);
            }
        });
    }

    @Override
    protected int getWidth(DisplayLinkContext context) {
        MutableInt count = new MutableInt(0);
        NixieTubeBlock.walkNixies((LevelAccessor)context.level(), context.getTargetPos(), false, (currentPos, rowPosition) -> count.add(2));
        return count.intValue();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public AABB getMultiblockBounds(LevelAccessor level, BlockPos pos) {
        MutableObject start = new MutableObject(null);
        MutableObject end = new MutableObject(null);
        NixieTubeBlock.walkNixies(level, pos, true, (currentPos, rowPosition) -> {
            end.setValue(currentPos);
            if (start.getValue() == null) {
                start.setValue(currentPos);
            }
        });
        BlockPos diffToCurrent = ((BlockPos)start.getValue()).subtract((Vec3i)pos);
        BlockPos diff = ((BlockPos)end.getValue()).subtract((Vec3i)start.getValue());
        return super.getMultiblockBounds(level, pos).move(diffToCurrent).expandTowards(Vec3.atLowerCornerOf((Vec3i)diff));
    }
}
