/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.client.renderer.block.BlockModelShaper
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.ModelResourceLocation
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.client.event.ModelEvent$ModifyBakingResult
 */
package com.simibubi.create.foundation.model;

import com.simibubi.create.foundation.block.render.CustomBlockModels;
import com.simibubi.create.foundation.item.render.CustomItemModels;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItems;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

public class ModelSwapper {
    protected CustomBlockModels customBlockModels = new CustomBlockModels();
    protected CustomItemModels customItemModels = new CustomItemModels();

    public CustomBlockModels getCustomBlockModels() {
        return this.customBlockModels;
    }

    public CustomItemModels getCustomItemModels() {
        return this.customItemModels;
    }

    public void onModelBake(ModelEvent.ModifyBakingResult event) {
        Map modelRegistry = event.getModels();
        this.customBlockModels.forEach((NonNullBiConsumer<Block, NonNullFunction<BakedModel, ? extends BakedModel>>)((NonNullBiConsumer)(block, modelFunc) -> ModelSwapper.swapModels((Map<ModelResourceLocation, BakedModel>)modelRegistry, ModelSwapper.getAllBlockStateModelLocations(block), modelFunc)));
        this.customItemModels.forEach((NonNullBiConsumer<Item, NonNullFunction<BakedModel, ? extends BakedModel>>)((NonNullBiConsumer)(item, modelFunc) -> ModelSwapper.swapModels((Map<ModelResourceLocation, BakedModel>)modelRegistry, ModelSwapper.getItemModelLocation(item), modelFunc)));
        CustomRenderedItems.forEach(item -> ModelSwapper.swapModels((Map<ModelResourceLocation, BakedModel>)modelRegistry, ModelSwapper.getItemModelLocation(item), CustomRenderedItemModel::new));
    }

    public void registerListeners(IEventBus modEventBus) {
        modEventBus.addListener(this::onModelBake);
    }

    public static <T extends BakedModel> void swapModels(Map<ModelResourceLocation, BakedModel> modelRegistry, List<ModelResourceLocation> locations, Function<BakedModel, T> factory) {
        locations.forEach(location -> ModelSwapper.swapModels(modelRegistry, location, factory));
    }

    public static <T extends BakedModel> void swapModels(Map<ModelResourceLocation, BakedModel> modelRegistry, ModelResourceLocation location, Function<BakedModel, T> factory) {
        modelRegistry.put(location, (BakedModel)factory.apply(modelRegistry.get(location)));
    }

    public static List<ModelResourceLocation> getAllBlockStateModelLocations(Block block) {
        ArrayList<ModelResourceLocation> models = new ArrayList<ModelResourceLocation>();
        ResourceLocation blockRl = RegisteredObjectsHelper.getKeyOrThrow((Block)block);
        block.getStateDefinition().getPossibleStates().forEach(state -> models.add(BlockModelShaper.stateToModelLocation((ResourceLocation)blockRl, (BlockState)state)));
        return models;
    }

    public static ModelResourceLocation getItemModelLocation(Item item) {
        return new ModelResourceLocation(RegisteredObjectsHelper.getKeyOrThrow((Item)item), "inventory");
    }
}
