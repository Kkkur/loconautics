/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.mounted;

import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlock;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

private class CartAssemblerBlockEntity.CartAssemblerValueBoxTransform
extends CenteredSideValueBoxTransform {
    public CartAssemblerBlockEntity.CartAssemblerValueBoxTransform(CartAssemblerBlockEntity cartAssemblerBlockEntity) {
        super((BlockState state, Direction d) -> {
            if (d.getAxis().isVertical()) {
                return false;
            }
            if (!state.hasProperty(CartAssemblerBlock.RAIL_SHAPE)) {
                return false;
            }
            RailShape railShape = (RailShape)state.getValue(CartAssemblerBlock.RAIL_SHAPE);
            return d.getAxis() == Direction.Axis.X == (railShape == RailShape.NORTH_SOUTH);
        });
    }

    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)7.0, (double)17.5);
    }
}
