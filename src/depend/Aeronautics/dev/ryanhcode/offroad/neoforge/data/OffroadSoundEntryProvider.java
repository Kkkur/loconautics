/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.simibubi.create.AllSoundEvents$SoundEntry
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 */
package dev.ryanhcode.offroad.neoforge.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.AllSoundEvents;
import dev.ryanhcode.offroad.index.OffroadSoundEvents;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class OffroadSoundEntryProvider
implements DataProvider {
    private final PackOutput output;

    public OffroadSoundEntryProvider(DataGenerator generator) {
        this.output = generator.getPackOutput();
    }

    public CompletableFuture<?> run(CachedOutput cache) {
        return this.generate(this.output.getOutputFolder(), cache);
    }

    public String getName() {
        return "Offroad's Custom Sounds";
    }

    public CompletableFuture<?> generate(Path path, CachedOutput cache) {
        path = path.resolve("assets/offroad");
        JsonObject json = new JsonObject();
        OffroadSoundEvents.ALL.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> ((AllSoundEvents.SoundEntry)entry.getValue()).write(json));
        return DataProvider.saveStable((CachedOutput)cache, (JsonElement)json, (Path)path.resolve("sounds.json"));
    }
}
