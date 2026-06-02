/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.compat.jei.ConversionRecipe
 *  com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory
 *  com.simibubi.create.content.processing.recipe.StandardProcessingRecipe$Builder
 *  dev.simulated_team.simulated.service.SimPlatformService
 *  net.createmod.catnip.config.ConfigBase
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.event.config.ModConfigEvent$Loading
 *  net.neoforged.fml.event.config.ModConfigEvent$Reloading
 *  net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.neoforged.neoforge.data.event.GatherDataEvent
 *  net.neoforged.neoforge.registries.RegisterEvent
 */
package dev.eriksonn.aeronautics.neoforge.events;

import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.data.AeroAdvancementTriggers;
import dev.eriksonn.aeronautics.index.AeroAdvancements;
import dev.eriksonn.aeronautics.index.AeroArmInteractionPoints;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.eriksonn.aeronautics.index.AeroItems;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
import dev.eriksonn.aeronautics.index.AeroTags;
import dev.eriksonn.aeronautics.neoforge.data.recipe.AeroProcessingRecipeGen;
import dev.eriksonn.aeronautics.neoforge.index.AeroFluidsNeoForge;
import dev.eriksonn.aeronautics.neoforge.service.NeoForgeAeroConfigService;
import dev.simulated_team.simulated.service.SimPlatformService;
import java.util.concurrent.CompletableFuture;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid="aeronautics")
public static class AeroNeoForgeCommonEvents.ModBusEvents {
    @SubscribeEvent
    public static void registerEvent(RegisterEvent event) {
        AeroArmInteractionPoints.init();
        if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
            AeroAdvancements.init();
            AeroAdvancementTriggers.register();
            if (SimPlatformService.INSTANCE.isLoaded("jei")) {
                AeroNeoForgeCommonEvents.ModBusEvents.jeiCompat();
            }
        }
    }

    private static void jeiCompat() {
        MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create((ItemStack)((Item)AeroFluidsNeoForge.LEVITITE_BLEND.getBucket().get()).getDefaultInstance(), (ItemStack)AeroBlocks.LEVITITE.asItem().getDefaultInstance()));
        MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create((ItemStack)((Item)AeroFluidsNeoForge.LEVITITE_BLEND.getBucket().get()).getDefaultInstance(), (ItemStack)AeroBlocks.PEARLESCENT_LEVITITE.asItem().getDefaultInstance()));
        ResourceLocation recipeId = Aeronautics.path("conversion_music_disc_cloud_skipper");
        ConversionRecipe recipe = (ConversionRecipe)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)new StandardProcessingRecipe.Builder(ConversionRecipe::new, recipeId).withItemIngredients(new Ingredient[]{Ingredient.of(AeroTags.ItemTags.CONVERTS_TO_CLOUD_SKIPPER)})).withSingleItemOutput(AeroItems.MUSIC_DISC_CLOUD_SKIPPER.asStack())).build();
        MysteriousItemConversionCategory.RECIPES.add(new RecipeHolder(recipeId, (Recipe)recipe));
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (event.getMods().contains("aeronautics")) {
            AeroTags.addGenerators();
        }
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), (DataProvider)new AeroAdvancements(output, lookupProvider));
        generator.addProvider(event.includeServer(), AeroProcessingRecipeGen.registerAll(output, lookupProvider));
        event.addProvider((DataProvider)AeroSoundEvents.REGISTRY.getProvider(output));
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        AeroFluidsNeoForge.registerFluidInteractions();
    }

    @SubscribeEvent
    public static void loadConfig(ModConfigEvent.Loading event) {
        for (ConfigBase config : NeoForgeAeroConfigService.CONFIGS.values()) {
            if (config.specification != event.getConfig().getSpec()) continue;
            config.onLoad();
        }
    }

    @SubscribeEvent
    public static void reloadConfig(ModConfigEvent.Reloading event) {
        for (ConfigBase config : NeoForgeAeroConfigService.CONFIGS.values()) {
            if (config.specification != event.getConfig().getSpec()) continue;
            config.onReload();
        }
    }
}
