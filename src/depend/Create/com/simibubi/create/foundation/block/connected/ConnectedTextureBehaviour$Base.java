/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public static abstract class ConnectedTextureBehaviour.Base
extends ConnectedTextureBehaviour {
    @Override
    @Nullable
    public abstract CTSpriteShiftEntry getShift(BlockState var1, Direction var2, @Nullable TextureAtlasSprite var3);

    @Override
    @Nullable
    public CTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        CTSpriteShiftEntry shift = this.getShift(state, direction, null);
        if (shift == null) {
            return null;
        }
        return shift.getType();
    }
}
