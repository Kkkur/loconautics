package com.lycoris.loconautics.block.casing;

import com.simibubi.create.content.decoration.encasing.CasingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ReinforcedCasingBlock extends CasingBlock {
    public ReinforcedCasingBlock(Properties properties) {
        super(properties.mapColor(MapColor.STONE));
    }
}