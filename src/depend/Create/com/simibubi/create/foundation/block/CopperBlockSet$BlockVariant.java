/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.WeatheringCopper$WeatherState
 *  net.minecraft.world.level.block.WeatheringCopperFullBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.foundation.block;

import com.simibubi.create.foundation.block.CopperBlockSet;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import java.util.Objects;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperFullBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public static class CopperBlockSet.BlockVariant
implements CopperBlockSet.Variant<Block> {
    public static final CopperBlockSet.BlockVariant INSTANCE = new CopperBlockSet.BlockVariant();

    protected CopperBlockSet.BlockVariant() {
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public NonNullFunction<BlockBehaviour.Properties, Block> getFactory(CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
        if (waxed) {
            return Block::new;
        }
        return p -> new WeatheringCopperFullBlock(state, p);
    }

    @Override
    public void generateBlockState(DataGenContext<Block, Block> ctx, RegistrateBlockstateProvider prov, CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
        Block block = (Block)ctx.get();
        String path = RegisteredObjectsHelper.getKeyOrThrow((Block)block).getPath();
        String baseLoc = "block/" + blocks.generalDirectory + CopperBlockSet.getWeatherStatePrefix(state);
        ResourceLocation texture = prov.modLoc(baseLoc + blocks.getName());
        if (Objects.equals(blocks.getName(), blocks.getEndTextureName())) {
            prov.simpleBlock(block, (ModelFile)prov.models().cubeAll(path, texture));
        } else {
            ResourceLocation endTexture = prov.modLoc(baseLoc + blocks.getEndTextureName());
            prov.simpleBlock(block, (ModelFile)prov.models().cubeColumn(path, texture, endTexture));
        }
    }

    @Override
    public void generateRecipes(BlockEntry<?> blockVariant, DataGenContext<Block, Block> ctx, RegistrateRecipeProvider prov) {
    }
}
