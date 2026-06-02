/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.util.DataIngredient
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.IronBarsBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.MapColor
 *  net.neoforged.neoforge.client.model.generators.BlockModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 *  net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder$PartBuilder
 */
package com.simibubi.create.foundation.data;

import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.function.Supplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

public class MetalBarsGen {
    public static <P extends IronBarsBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> barsBlockState(String name, boolean specialEdge) {
        return (c, p) -> {
            ModelFile post_ends = MetalBarsGen.barsSubModel(p, name, "post_ends", specialEdge);
            ModelFile post = MetalBarsGen.barsSubModel(p, name, "post", specialEdge);
            ModelFile cap = MetalBarsGen.barsSubModel(p, name, "cap", specialEdge);
            ModelFile cap_alt = MetalBarsGen.barsSubModel(p, name, "cap_alt", specialEdge);
            ModelFile side = MetalBarsGen.barsSubModel(p, name, "side", specialEdge);
            ModelFile side_alt = MetalBarsGen.barsSubModel(p, name, "side_alt", specialEdge);
            ((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)((MultiPartBlockStateBuilder.PartBuilder)p.getMultipartBuilder((Block)c.get()).part().modelFile(post_ends).addModel()).end().part().modelFile(post).addModel()).condition((Property)BlockStateProperties.NORTH, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.EAST, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.SOUTH, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.WEST, (Comparable[])new Boolean[]{false}).end().part().modelFile(cap).addModel()).condition((Property)BlockStateProperties.NORTH, (Comparable[])new Boolean[]{true}).condition((Property)BlockStateProperties.EAST, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.SOUTH, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.WEST, (Comparable[])new Boolean[]{false}).end().part().modelFile(cap).rotationY(90).addModel()).condition((Property)BlockStateProperties.NORTH, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.EAST, (Comparable[])new Boolean[]{true}).condition((Property)BlockStateProperties.SOUTH, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.WEST, (Comparable[])new Boolean[]{false}).end().part().modelFile(cap_alt).addModel()).condition((Property)BlockStateProperties.NORTH, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.EAST, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.SOUTH, (Comparable[])new Boolean[]{true}).condition((Property)BlockStateProperties.WEST, (Comparable[])new Boolean[]{false}).end().part().modelFile(cap_alt).rotationY(90).addModel()).condition((Property)BlockStateProperties.NORTH, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.EAST, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.SOUTH, (Comparable[])new Boolean[]{false}).condition((Property)BlockStateProperties.WEST, (Comparable[])new Boolean[]{true}).end().part().modelFile(side).addModel()).condition((Property)BlockStateProperties.NORTH, (Comparable[])new Boolean[]{true}).end().part().modelFile(side).rotationY(90).addModel()).condition((Property)BlockStateProperties.EAST, (Comparable[])new Boolean[]{true}).end().part().modelFile(side_alt).addModel()).condition((Property)BlockStateProperties.SOUTH, (Comparable[])new Boolean[]{true}).end().part().modelFile(side_alt).rotationY(90).addModel()).condition((Property)BlockStateProperties.WEST, (Comparable[])new Boolean[]{true}).end();
        };
    }

    private static ModelFile barsSubModel(RegistrateBlockstateProvider p, String name, String suffix, boolean specialEdge) {
        ResourceLocation barsTexture = p.modLoc("block/bars/" + name + "_bars");
        ResourceLocation edgeTexture = specialEdge ? p.modLoc("block/bars/" + name + "_bars_edge") : barsTexture;
        return ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)p.models().withExistingParent(name + "_" + suffix, p.modLoc("block/bars/" + suffix))).texture("bars", barsTexture)).texture("particle", barsTexture)).texture("edge", edgeTexture);
    }

    public static BlockEntry<IronBarsBlock> createBars(String name, boolean specialEdge, Supplier<DataIngredient> ingredient, MapColor color) {
        return ((BlockBuilder)((BlockBuilder)Create.registrate().block(name + "_bars", IronBarsBlock::new).addLayer(() -> RenderType::cutoutMipped).initialProperties(() -> Blocks.IRON_BARS).properties(p -> p.sound(SoundType.COPPER).mapColor(color)).tag(new TagKey[]{AllTags.AllBlockTags.WRENCH_PICKUP.tag}).tag(new TagKey[]{AllTags.AllBlockTags.FAN_TRANSPARENT.tag}).transform(TagGen.pickaxeOnly())).blockstate(MetalBarsGen.barsBlockState(name, specialEdge)).item().model((c, p) -> {
            ResourceLocation barsTexture = p.modLoc("block/bars/" + name + "_bars");
            p.generated((NonNullSupplier)c, new ResourceLocation[]{barsTexture});
        }).recipe((c, p) -> p.stonecutting((DataIngredient)ingredient.get(), RecipeCategory.DECORATIONS, () -> ((DataGenContext)c).get(), 4)).build()).register();
    }
}
