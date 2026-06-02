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
import com.simibubi.create.content.redstone.diodes.BrassDiodeBlock;
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

public class BrassDiodeGenerator
extends AbstractDiodeGenerator {
    @Override
    protected <T extends Block> List<ModelFile> createModels(DataGenContext<Block, T> ctx, BlockModelProvider prov) {
        ArrayList<ModelFile> models = new ArrayList<ModelFile>(4);
        String name = ctx.getName();
        ResourceLocation template = this.existing(name);
        models.add((ModelFile)prov.getExistingFile(template));
        models.add((ModelFile)((BlockModelBuilder)prov.withExistingParent(name + "_powered", template)).texture("top", this.texture(ctx, "powered")));
        models.add((ModelFile)((BlockModelBuilder)((BlockModelBuilder)prov.withExistingParent(name + "_powering", template)).texture("torch", this.poweredTorch())).texture("top", this.texture(ctx, "powering")));
        models.add((ModelFile)((BlockModelBuilder)((BlockModelBuilder)prov.withExistingParent(name + "_powered_powering", template)).texture("torch", this.poweredTorch())).texture("top", this.texture(ctx, "powered_powering")));
        return models;
    }

    @Override
    protected int getModelIndex(BlockState state) {
        return ((Boolean)state.getValue((Property)BrassDiodeBlock.POWERING) ^ (Boolean)state.getValue((Property)BrassDiodeBlock.INVERTED) ? 2 : 0) + ((Boolean)state.getValue((Property)BrassDiodeBlock.POWERED) != false ? 1 : 0);
    }
}
