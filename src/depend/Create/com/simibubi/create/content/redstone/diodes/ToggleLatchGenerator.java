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
import com.simibubi.create.content.redstone.diodes.ToggleLatchBlock;
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

public class ToggleLatchGenerator
extends AbstractDiodeGenerator {
    @Override
    protected <T extends Block> List<ModelFile> createModels(DataGenContext<Block, T> ctx, BlockModelProvider prov) {
        String name = ctx.getName();
        ArrayList<ModelFile> models = new ArrayList<ModelFile>(4);
        ResourceLocation off = this.existing("latch_off");
        ResourceLocation on = this.existing("latch_on");
        models.add((ModelFile)prov.getExistingFile(off));
        models.add((ModelFile)((BlockModelBuilder)prov.withExistingParent(name + "_off_powered", off)).texture("top", this.texture(ctx, "powered")));
        models.add((ModelFile)prov.getExistingFile(on));
        models.add((ModelFile)((BlockModelBuilder)prov.withExistingParent(name + "_on_powered", on)).texture("top", this.texture(ctx, "powered_powering")));
        return models;
    }

    @Override
    protected int getModelIndex(BlockState state) {
        return ((Boolean)state.getValue((Property)ToggleLatchBlock.POWERING) != false ? 2 : 0) + ((Boolean)state.getValue((Property)ToggleLatchBlock.POWERED) != false ? 1 : 0);
    }
}
