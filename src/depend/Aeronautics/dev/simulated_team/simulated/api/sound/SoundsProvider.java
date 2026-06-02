/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 */
package dev.simulated_team.simulated.api.sound;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.simulated_team.simulated.api.sound.SoundDefinition;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class SoundsProvider
implements DataProvider {
    private static final Codec<Map<String, SoundDefinition>> CODEC = Codec.unboundedMap((Codec)Codec.STRING, SoundDefinition.CODEC);
    private final String modId;
    private final PackOutput output;
    private final Map<String, SoundDefinition> definitionMap;

    public SoundsProvider(String modId, PackOutput output, Map<String, SoundDefinition> definitionMap) {
        this.modId = modId;
        this.output = output;
        this.definitionMap = definitionMap;
    }

    public CompletableFuture<?> run(CachedOutput cache) {
        return this.generate(this.output.getOutputFolder(), cache);
    }

    private CompletableFuture<?> generate(Path path, CachedOutput cache) {
        path = path.resolve("assets").resolve(this.modId);
        JsonObject json = new JsonObject();
        DataResult result = CODEC.encode(this.definitionMap, (DynamicOps)JsonOps.INSTANCE, (Object)new JsonObject());
        if (result.isSuccess()) {
            json = ((JsonElement)result.getOrThrow()).getAsJsonObject();
        }
        return DataProvider.saveStable((CachedOutput)cache, (JsonElement)json, (Path)path.resolve("sounds.json"));
    }

    public String getName() {
        return this.modId + " sounds.json";
    }
}
