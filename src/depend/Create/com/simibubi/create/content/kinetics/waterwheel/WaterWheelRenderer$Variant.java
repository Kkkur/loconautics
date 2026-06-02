/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.waterwheel;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlock;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public static enum WaterWheelRenderer.Variant {
    SMALL(AllPartialModels.WATER_WHEEL),
    LARGE(AllPartialModels.LARGE_WATER_WHEEL),
    LARGE_EXTENSION(AllPartialModels.LARGE_WATER_WHEEL_EXTENSION);

    private final PartialModel partial;

    private WaterWheelRenderer.Variant(PartialModel partial) {
        this.partial = partial;
    }

    public BakedModel model() {
        return this.partial.get();
    }

    public static WaterWheelRenderer.Variant of(boolean large, BlockState blockState) {
        if (large) {
            boolean extension = (Boolean)blockState.getValue((Property)LargeWaterWheelBlock.EXTENSION);
            if (extension) {
                return LARGE_EXTENSION;
            }
            return LARGE;
        }
        return SMALL;
    }
}
