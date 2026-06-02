/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class KineticBlockEntityVisual<T extends KineticBlockEntity>
extends AbstractBlockEntityVisual<T> {
    public KineticBlockEntityVisual(VisualizationContext context, T blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }

    protected Direction.Axis rotationAxis() {
        return KineticBlockEntityVisual.rotationAxis(this.blockState);
    }

    public static float rotationOffset(BlockState state, Direction.Axis axis, Vec3i pos) {
        if (KineticBlockEntityVisual.shouldOffset(axis, pos)) {
            return 22.5f;
        }
        return ICogWheel.isLargeCog(state) ? 11.25f : 0.0f;
    }

    public static boolean shouldOffset(Direction.Axis axis, Vec3i pos) {
        int x = axis == Direction.Axis.X ? 0 : pos.getX();
        int y = axis == Direction.Axis.Y ? 0 : pos.getY();
        int z = axis == Direction.Axis.Z ? 0 : pos.getZ();
        return (x + y + z) % 2 == 0;
    }

    public static Direction.Axis rotationAxis(BlockState blockState) {
        Direction.Axis axis;
        Block block = blockState.getBlock();
        if (block instanceof IRotate) {
            IRotate irotate = (IRotate)block;
            axis = irotate.getRotationAxis(blockState);
        } else {
            axis = Direction.Axis.Y;
        }
        return axis;
    }

    public static void applyOverstressEffect(KineticBlockEntity be, RotatingInstance ... instances) {
        float overStressedEffect = be.effects.overStressedEffect;
        if (overStressedEffect != 0.0f) {
            boolean overstressed = overStressedEffect > 0.0f;
            Color color = overstressed ? Color.RED : Color.SPRING_GREEN;
            float weight = overstressed ? overStressedEffect : -overStressedEffect;
            for (RotatingInstance instance : instances) {
                instance.setColor(Color.WHITE.mixWith(color, weight));
            }
        } else {
            for (RotatingInstance instance : instances) {
                instance.setColor(Color.WHITE);
            }
        }
        for (RotatingInstance instance : instances) {
            instance.setChanged();
        }
    }
}
