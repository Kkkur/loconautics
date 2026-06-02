/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.foundation.data;

import com.simibubi.create.content.kinetics.gauge.GaugeBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public abstract class DirectionalAxisBlockStateGen
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        Direction direction = (Direction)state.getValue((Property)GaugeBlock.FACING);
        boolean alongFirst = (Boolean)state.getValue((Property)GaugeBlock.AXIS_ALONG_FIRST_COORDINATE);
        if (direction == Direction.DOWN) {
            return 180;
        }
        if (direction == Direction.UP) {
            return 0;
        }
        if (direction.getAxis() == Direction.Axis.X == alongFirst) {
            return 90;
        }
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        Direction direction = (Direction)state.getValue((Property)GaugeBlock.FACING);
        boolean alongFirst = (Boolean)state.getValue((Property)GaugeBlock.AXIS_ALONG_FIRST_COORDINATE);
        if (direction.getAxis().isVertical()) {
            return alongFirst ? 90 : 0;
        }
        return this.horizontalAngle(direction) + 90;
    }

    public abstract <T extends Block> String getModelPrefix(DataGenContext<Block, T> var1, RegistrateBlockstateProvider var2, BlockState var3);

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        boolean vertical = ((Direction)state.getValue((Property)GaugeBlock.FACING)).getAxis().isVertical();
        String partial = vertical ? "" : "_wall";
        return prov.models().getExistingFile(prov.modLoc(this.getModelPrefix(ctx, prov, state) + partial));
    }
}
