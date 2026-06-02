/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.tterrag.registrate.providers.ProviderType
 *  com.tterrag.registrate.providers.RegistrateLangProvider
 *  net.createmod.ponder.api.registration.PonderPlugin
 *  net.createmod.ponder.foundation.PonderIndex
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.neoforged.neoforge.common.data.ExistingFileHelper
 *  net.neoforged.neoforge.data.event.GatherDataEvent
 */
package com.simibubi.create.infrastructure.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.compat.curios.CuriosDataGenerator;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.data.CreateDatamapProvider;
import com.simibubi.create.foundation.data.DamageTypeTagGen;
import com.simibubi.create.foundation.data.recipe.CreateMechanicalCraftingRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.simibubi.create.foundation.data.recipe.CreateSequencedAssemblyRecipeGen;
import com.simibubi.create.foundation.data.recipe.CreateStandardRecipeGen;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.infrastructure.data.CreateContraptionTypeTagsProvider;
import com.simibubi.create.infrastructure.data.CreateEnchantmentTagsProvider;
import com.simibubi.create.infrastructure.data.CreateMountedItemStorageTypeTagsProvider;
import com.simibubi.create.infrastructure.data.CreateRecipeSerializerTagsProvider;
import com.simibubi.create.infrastructure.data.CreateRegistrateTags;
import com.simibubi.create.infrastructure.data.CreateWikiBlockInfoProvider;
import com.simibubi.create.infrastructure.data.GeneratedEntriesProvider;
import com.simibubi.create.infrastructure.data.TagLangGenerator;
import com.simibubi.create.infrastructure.data.VanillaHatOffsetGenerator;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class CreateDatagen {
    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (event.getMods().contains("create")) {
            CreateDatagen.addExtraRegistrateData();
        }
    }

    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains("create")) {
            return;
        }
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), (DataProvider)AllSoundEvents.provider(generator));
        GeneratedEntriesProvider generatedEntriesProvider = new GeneratedEntriesProvider(output, lookupProvider);
        lookupProvider = generatedEntriesProvider.getRegistryProvider();
        generator.addProvider(event.includeServer(), (DataProvider)generatedEntriesProvider);
        generator.addProvider(event.includeServer(), (DataProvider)new CreateRecipeSerializerTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider)new CreateContraptionTypeTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider)new CreateMountedItemStorageTypeTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider)new DamageTypeTagGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider)new AllAdvancements(output, lookupProvider));
        generator.addProvider(event.includeServer(), (DataProvider)new CreateStandardRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), (DataProvider)new CreateMechanicalCraftingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), (DataProvider)new CreateSequencedAssemblyRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), (DataProvider)new CreateDatamapProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), (DataProvider)new VanillaHatOffsetGenerator(output, lookupProvider));
        generator.addProvider(event.includeServer(), (DataProvider)new CuriosDataGenerator(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider)new CreateEnchantmentTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeClient(), (DataProvider)new CreateWikiBlockInfoProvider(output));
        if (event.includeServer()) {
            CreateRecipeProvider.registerAllProcessing(generator, output, lookupProvider);
        }
    }

    private static void addExtraRegistrateData() {
        CreateRegistrateTags.addGenerators();
        Create.registrate().addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = (arg_0, arg_1) -> ((RegistrateLangProvider)provider).add(arg_0, arg_1);
            CreateDatagen.provideDefaultLang("interface", langConsumer);
            CreateDatagen.provideDefaultLang("tooltips", langConsumer);
            AllAdvancements.provideLang(langConsumer);
            AllSoundEvents.provideLang(langConsumer);
            AllKeys.provideLang(langConsumer);
            CreateDatagen.providePonderLang(langConsumer);
            new TagLangGenerator(langConsumer).generate();
        });
    }

    private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
        String path = "assets/create/lang/default/" + fileName + ".json";
        JsonElement jsonElement = FilesHelper.loadJsonResource(path);
        if (jsonElement == null) {
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Map.Entry entry : jsonObject.entrySet()) {
            String key = (String)entry.getKey();
            String value = ((JsonElement)entry.getValue()).getAsString();
            consumer.accept(key, value);
        }
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
        PonderIndex.addPlugin((PonderPlugin)new CreatePonderPlugin());
        PonderIndex.getLangAccess().provideLang("create", consumer);
    }
}
