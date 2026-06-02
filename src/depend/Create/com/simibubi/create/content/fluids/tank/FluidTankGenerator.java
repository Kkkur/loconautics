/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.BlockModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.fluids.tank;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class FluidTankGenerator
extends SpecialBlockStateGen {
    private String prefix;

    public FluidTankGenerator() {
        this("");
    }

    public FluidTankGenerator(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return 0;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        Boolean top = (Boolean)state.getValue((Property)FluidTankBlock.TOP);
        Boolean bottom = (Boolean)state.getValue((Property)FluidTankBlock.BOTTOM);
        FluidTankBlock.Shape shape = (FluidTankBlock.Shape)((Object)state.getValue(FluidTankBlock.SHAPE));
        String shapeName = "middle";
        if (top.booleanValue() && bottom.booleanValue()) {
            shapeName = "single";
        } else if (top.booleanValue()) {
            shapeName = "top";
        } else if (bottom.booleanValue()) {
            shapeName = "bottom";
        }
        String modelName = shapeName + (String)(shape == FluidTankBlock.Shape.PLAIN ? "" : "_" + shape.getSerializedName());
        if (!this.prefix.isEmpty()) {
            return ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)prov.models().withExistingParent(this.prefix + modelName, prov.modLoc("block/fluid_tank/block_" + modelName))).texture("0", prov.modLoc("block/" + this.prefix + "casing"))).texture("1", prov.modLoc("block/" + this.prefix + "fluid_tank"))).texture("3", prov.modLoc("block/" + this.prefix + "fluid_tank_window"))).texture("4", prov.modLoc("block/" + this.prefix + "casing"))).texture("5", prov.modLoc("block/" + this.prefix + "fluid_tank_window_single"))).texture("particle", prov.modLoc("block/" + this.prefix + "fluid_tank"));
        }
        return AssetLookup.partialBaseModel(ctx, prov, modelName);
    }
}
