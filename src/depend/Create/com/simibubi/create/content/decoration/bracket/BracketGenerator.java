/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.ItemBuilder
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.BlockModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ItemModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.decoration.bracket;

import com.simibubi.create.content.decoration.bracket.BracketBlock;
import com.simibubi.create.foundation.data.DirectionalAxisBlockStateGen;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class BracketGenerator
extends DirectionalAxisBlockStateGen {
    private String material;

    public BracketGenerator(String material) {
        this.material = material;
    }

    @Override
    public <T extends Block> String getModelPrefix(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        return "";
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        String type = ((BracketBlock.BracketType)((Object)state.getValue(BracketBlock.TYPE))).getSerializedName();
        boolean vertical = ((Direction)state.getValue((Property)BracketBlock.FACING)).getAxis().isVertical();
        String path = "block/bracket/" + type + "/" + (vertical ? "ground" : "wall");
        return ((BlockModelBuilder)((BlockModelBuilder)prov.models().withExistingParent(path + "_" + this.material, prov.modLoc(path))).texture("bracket", prov.modLoc("block/bracket_" + this.material))).texture("plate", prov.modLoc("block/bracket_plate_" + this.material));
    }

    public static <I extends BlockItem, P> NonNullFunction<ItemBuilder<I, P>, P> itemModel(String material) {
        return b -> b.model((c, p) -> ((ItemModelBuilder)((ItemModelBuilder)p.withExistingParent(c.getName(), p.modLoc("block/bracket/item"))).texture("bracket", p.modLoc("block/bracket_" + material))).texture("plate", p.modLoc("block/bracket_plate_" + material))).build();
    }
}
