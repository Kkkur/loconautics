/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeatheredIronWindowCTBehaviour
extends ConnectedTextureBehaviour.Base {
    private List<CTSpriteShiftEntry> shifts = List.of(AllSpriteShifts.OLD_FACTORY_WINDOW_1, AllSpriteShifts.OLD_FACTORY_WINDOW_2, AllSpriteShifts.OLD_FACTORY_WINDOW_3, AllSpriteShifts.OLD_FACTORY_WINDOW_4);

    @Override
    @Nullable
    public CTSpriteShiftEntry getShift(BlockState state, RandomSource rand, Direction direction, @NotNull TextureAtlasSprite sprite) {
        if (direction.getAxis() == Direction.Axis.Y || sprite == null) {
            return null;
        }
        CTSpriteShiftEntry entry = this.shifts.get(rand.nextInt(this.shifts.size()));
        if (entry.getOriginal() == sprite) {
            return entry;
        }
        return super.getShift(state, rand, direction, sprite);
    }

    @Override
    @Nullable
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        return null;
    }

    @Override
    @Nullable
    public CTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        return AllCTTypes.RECTANGLE;
    }
}
