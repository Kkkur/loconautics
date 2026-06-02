/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateItemModelProvider
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.generators.BlockModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ItemModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.foundation.data;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class AssetLookup {
    public static ModelFile partialBaseModel(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov, String ... suffix) {
        Object string = "/block";
        for (String suf : suffix) {
            if (suf.isEmpty()) continue;
            string = (String)string + "_" + suf;
        }
        String location = "block/" + ctx.getName() + (String)string;
        return prov.models().getExistingFile(prov.modLoc(location));
    }

    public static ModelFile standardModel(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov) {
        return prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName()));
    }

    public static <I extends BlockItem> ItemModelBuilder customItemModel(DataGenContext<Item, I> ctx, RegistrateItemModelProvider prov) {
        return prov.blockItem(() -> ((BlockItem)ctx.getEntry()).getBlock(), "/item");
    }

    public static <I extends BlockItem> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> customBlockItemModel(String ... folders) {
        return (c, p) -> {
            Object path = "block";
            for (String string : folders) {
                path = (String)path + "/" + ("_".equals(string) ? c.getName() : string);
            }
            p.withExistingParent(c.getName(), p.modLoc((String)path));
        };
    }

    public static <I extends Item> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> customGenericItemModel(String ... folders) {
        return (c, p) -> {
            Object path = "block";
            for (String string : folders) {
                path = (String)path + "/" + ("_".equals(string) ? c.getName() : string);
            }
            p.withExistingParent(c.getName(), p.modLoc((String)path));
        };
    }

    public static Function<BlockState, ModelFile> forPowered(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov) {
        return state -> (Boolean)state.getValue((Property)BlockStateProperties.POWERED) != false ? AssetLookup.partialBaseModel(ctx, prov, "powered") : AssetLookup.partialBaseModel(ctx, prov, new String[0]);
    }

    public static Function<BlockState, ModelFile> forPowered(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov, String path) {
        return state -> prov.models().getExistingFile(prov.modLoc("block/" + path + ((Boolean)state.getValue((Property)BlockStateProperties.POWERED) != false ? "_powered" : "")));
    }

    public static Function<BlockState, ModelFile> withIndicator(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov, Function<BlockState, ModelFile> baseModelFunc, IntegerProperty property) {
        return state -> {
            ResourceLocation baseModel = ((ModelFile)baseModelFunc.apply((BlockState)state)).getLocation();
            Integer integer = (Integer)state.getValue((Property)property);
            return ((BlockModelBuilder)prov.models().withExistingParent(ctx.getName() + "_" + integer, baseModel)).texture("indicator", "block/indicator/" + integer);
        };
    }

    public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> existingItemModel() {
        return (c, p) -> p.getExistingFile(p.modLoc("item/" + c.getName()));
    }

    public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> itemModel(String name) {
        return (c, p) -> p.getExistingFile(p.modLoc("item/" + name));
    }

    public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> itemModelWithPartials() {
        return (c, p) -> p.withExistingParent("item/" + c.getName(), p.modLoc("item/" + c.getName() + "/item"));
    }
}
