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
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.BlockModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.logistics.funnel;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class BeltFunnelGenerator
extends SpecialBlockStateGen {
    private String type;
    private ResourceLocation materialBlockTexture;

    public BeltFunnelGenerator(String type) {
        this.type = type;
        this.materialBlockTexture = Create.asResource("block/" + type + "_block");
    }

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return this.horizontalAngle((Direction)state.getValue((Property)BeltFunnelBlock.HORIZONTAL_FACING)) + 180;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        String poweredSuffix;
        String prefix = "block/funnel/";
        BeltFunnelBlock.Shape shape = (BeltFunnelBlock.Shape)((Object)state.getValue(BeltFunnelBlock.SHAPE));
        String shapeName = shape.getSerializedName();
        boolean powered = state.getOptionalValue((Property)BlockStateProperties.POWERED).orElse(false);
        String string = poweredSuffix = powered ? "_powered" : "_unpowered";
        String shapeSuffix = shape == BeltFunnelBlock.Shape.PULLING ? "_pull" : (shape == BeltFunnelBlock.Shape.PUSHING ? "_push" : "_neutral");
        String name = ctx.getName() + "_" + shapeName + poweredSuffix;
        return ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)prov.models().withExistingParent(name, prov.modLoc("block/belt_funnel/block_" + shapeName))).texture("particle", this.materialBlockTexture)).texture("block", this.materialBlockTexture)).texture("direction", prov.modLoc(prefix + this.type + "_funnel" + shapeSuffix))).texture("redstone", prov.modLoc(prefix + this.type + "_funnel" + poweredSuffix))).texture("base", prov.modLoc(prefix + this.type + "_funnel"));
    }
}
