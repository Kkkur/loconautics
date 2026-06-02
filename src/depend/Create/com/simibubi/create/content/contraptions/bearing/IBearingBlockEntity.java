/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.content.contraptions.DirectionalExtenderScrollOptionSlot;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Property;

public interface IBearingBlockEntity
extends IControlContraption {
    public float getInterpolatedAngle(float var1);

    public boolean isWoodenTop();

    default public ValueBoxTransform getMovementModeSlot() {
        return new DirectionalExtenderScrollOptionSlot((state, d) -> {
            Direction.Axis axis = d.getAxis();
            Direction.Axis bearingAxis = ((Direction)state.getValue((Property)BearingBlock.FACING)).getAxis();
            return bearingAxis != axis;
        });
    }

    public void setAngle(float var1);
}
