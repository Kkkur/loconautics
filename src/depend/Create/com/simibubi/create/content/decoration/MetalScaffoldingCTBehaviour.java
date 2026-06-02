/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.decoration;

import com.simibubi.create.content.decoration.MetalScaffoldingBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class MetalScaffoldingCTBehaviour
extends HorizontalCTBehaviour {
    protected CTSpriteShiftEntry insideShift;

    public MetalScaffoldingCTBehaviour(CTSpriteShiftEntry outsideShift, CTSpriteShiftEntry insideShift, CTSpriteShiftEntry topShift) {
        super(outsideShift, topShift);
        this.insideShift = insideShift;
    }

    @Override
    public boolean buildContextForOccludedDirections() {
        return true;
    }

    @Override
    protected boolean isBeingBlocked(BlockState state, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        return face.getAxis() == Direction.Axis.Y && super.isBeingBlocked(state, reader, pos, otherPos, face);
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (direction.getAxis() != Direction.Axis.Y && sprite == this.insideShift.getOriginal()) {
            return this.insideShift;
        }
        return super.getShift(state, direction, sprite);
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        return super.connectsTo(state, other, reader, pos, otherPos, face) && (Boolean)state.getValue((Property)MetalScaffoldingBlock.BOTTOM) != false && (Boolean)other.getValue((Property)MetalScaffoldingBlock.BOTTOM) != false;
    }
}
