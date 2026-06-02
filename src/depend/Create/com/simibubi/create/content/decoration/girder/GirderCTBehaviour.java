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
package com.simibubi.create.content.decoration.girder;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class GirderCTBehaviour
extends ConnectedTextureBehaviour.Base {
    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (!state.hasProperty((Property)GirderBlock.X)) {
            return null;
        }
        return (Boolean)state.getValue((Property)GirderBlock.X) == false && (Boolean)state.getValue((Property)GirderBlock.Z) == false && direction.getAxis() != Direction.Axis.Y ? AllSpriteShifts.GIRDER_POLE : null;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
        if (other.getBlock() != state.getBlock()) {
            return false;
        }
        return (Boolean)other.getValue((Property)GirderBlock.X) == false && (Boolean)other.getValue((Property)GirderBlock.Z) == false;
    }
}
