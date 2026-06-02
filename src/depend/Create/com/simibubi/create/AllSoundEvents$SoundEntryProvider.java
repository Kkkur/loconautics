/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 */
package com.simibubi.create;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.AllSoundEvents;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public static class AllSoundEvents.SoundEntryProvider
implements DataProvider {
    private PackOutput output;

    public AllSoundEvents.SoundEntryProvider(DataGenerator generator) {
        this.output = generator.getPackOutput();
    }

    public CompletableFuture<?> run(CachedOutput cache) {
        return this.generate(this.output.getOutputFolder(), cache);
    }

    public String getName() {
        return "Create's Custom Sounds";
    }

    public CompletableFuture<?> generate(Path path, CachedOutput cache) {
        path = path.resolve("assets/create");
        JsonObject json = new JsonObject();
        ALL.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> ((AllSoundEvents.SoundEntry)entry.getValue()).write(json));
        return DataProvider.saveStable((CachedOutput)cache, (JsonElement)json, (Path)path.resolve("sounds.json"));
    }
}
