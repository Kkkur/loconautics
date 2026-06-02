/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.kinetics.saw;

import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class SawGenerator
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        return state.getValue((Property)SawBlock.FACING) == Direction.DOWN ? 180 : 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        Direction facing = (Direction)state.getValue((Property)SawBlock.FACING);
        boolean axisAlongFirst = (Boolean)state.getValue((Property)SawBlock.AXIS_ALONG_FIRST_COORDINATE);
        if (facing.getAxis().isVertical()) {
            return (axisAlongFirst ? 270 : 0) + ((Boolean)state.getValue((Property)SawBlock.FLIPPED) != false ? 180 : 0);
        }
        return this.horizontalAngle(facing);
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        String path = "block/" + ctx.getName() + "/";
        String orientation = ((Direction)state.getValue((Property)SawBlock.FACING)).getAxis().isVertical() ? "vertical" : "horizontal";
        return prov.models().getExistingFile(prov.modLoc(path + orientation));
    }
}
