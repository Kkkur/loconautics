/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.BlockModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.decoration.steamWhistle;

import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class WhistleGenerator
extends SpecialBlockStateGen {
    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return this.horizontalAngle((Direction)state.getValue((Property)WhistleBlock.FACING));
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        String wall = (Boolean)state.getValue((Property)WhistleBlock.WALL) != false ? "wall" : "floor";
        String size = ((WhistleBlock.WhistleSize)((Object)state.getValue(WhistleBlock.SIZE))).getSerializedName();
        boolean powered = (Boolean)state.getValue((Property)WhistleBlock.POWERED);
        ModelFile model = AssetLookup.partialBaseModel(ctx, prov, size, wall);
        if (!powered) {
            return model;
        }
        ResourceLocation parentLocation = model.getLocation();
        return ((BlockModelBuilder)prov.models().withExistingParent(parentLocation.getPath() + "_powered", parentLocation)).texture("2", Create.asResource("block/copper_redstone_plate_powered"));
    }
}
