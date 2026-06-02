/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.BlockModelBuilder
 *  net.neoforged.neoforge.client.model.generators.BlockModelProvider
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.redstone.diodes;

import com.simibubi.create.content.redstone.diodes.AbstractDiodeGenerator;
import com.simibubi.create.content.redstone.diodes.PoweredLatchBlock;
import com.tterrag.registrate.providers.DataGenContext;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class PoweredLatchGenerator
extends AbstractDiodeGenerator {
    @Override
    protected <T extends Block> List<ModelFile> createModels(DataGenContext<Block, T> ctx, BlockModelProvider prov) {
        ArrayList<ModelFile> models = new ArrayList<ModelFile>(2);
        String name = ctx.getName();
        ResourceLocation off = this.existing("latch_off");
        ResourceLocation on = this.existing("latch_on");
        models.add((ModelFile)((BlockModelBuilder)prov.withExistingParent(name, off)).texture("top", this.texture(ctx, "idle")));
        models.add((ModelFile)((BlockModelBuilder)prov.withExistingParent(name + "_powered", on)).texture("top", this.texture(ctx, "powering")));
        return models;
    }

    @Override
    protected int getModelIndex(BlockState state) {
        return (Boolean)state.getValue((Property)PoweredLatchBlock.POWERING) != false ? 1 : 0;
    }
}
