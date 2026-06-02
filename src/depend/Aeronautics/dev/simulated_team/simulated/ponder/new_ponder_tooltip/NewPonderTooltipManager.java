/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.ponder.new_ponder_tooltip;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.simulated_team.simulated.Simulated;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class NewPonderTooltipManager {
    private static final Codec<Set<ResourceLocation>> CODEC = ResourceLocation.CODEC.listOf().xmap(HashSet::new, set -> set.stream().toList());
    private static final HashMap<Item, Set<ResourceLocation>> NEW_PONDER_SCENES = new HashMap();
    private static Set<ResourceLocation> WATCHED_PONDER_SCENES = null;

    private static Path filePath() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("ponders_watched.json");
    }

    public static RegisterBuilder forItems(Item ... item) {
        return new RegisterBuilder(item);
    }

    public static boolean hasWatchedAllScenes(Item item) {
        NewPonderTooltipManager.load();
        if (NEW_PONDER_SCENES.containsKey(item)) {
            Set<ResourceLocation> scenes = NEW_PONDER_SCENES.get(item);
            return WATCHED_PONDER_SCENES.containsAll(scenes);
        }
        return true;
    }

    public static void setSceneWatched(ResourceLocation id) {
        NewPonderTooltipManager.load();
        if (WATCHED_PONDER_SCENES != null && !NewPonderTooltipManager.hasWatchedScene(id)) {
            WATCHED_PONDER_SCENES.add(id);
            NewPonderTooltipManager.save();
        }
    }

    public static boolean hasWatchedScene(ResourceLocation id) {
        NewPonderTooltipManager.load();
        return WATCHED_PONDER_SCENES.contains(id);
    }

    public static void save() {
        DataResult result = CODEC.encode(WATCHED_PONDER_SCENES, (DynamicOps)JsonOps.INSTANCE, (Object)new JsonArray());
        if (result.isError()) {
            return;
        }
        try {
            String data = ((JsonElement)result.getOrThrow()).toString();
            Files.writeString(NewPonderTooltipManager.filePath(), (CharSequence)data, StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void load() {
        if (WATCHED_PONDER_SCENES != null) {
            return;
        }
        DataResult result = CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)NewPonderTooltipManager.getOrCreateFile());
        WATCHED_PONDER_SCENES = new HashSet<ResourceLocation>();
        result.ifSuccess(set -> WATCHED_PONDER_SCENES.addAll((Collection<ResourceLocation>)set));
    }

    @NotNull
    private static JsonElement getOrCreateFile() {
        Path path = NewPonderTooltipManager.filePath();
        String jsonString = "[]";
        try {
            File file = path.toFile();
            if (file.exists()) {
                jsonString = Files.readString(path);
            } else {
                Files.writeString(path, (CharSequence)jsonString, new OpenOption[0]);
            }
        }
        catch (IOException ignored) {
            Simulated.LOGGER.info("There was an error reading ponders_watched.json.");
        }
        JsonArray element = new JsonArray();
        try {
            element = JsonParser.parseString((String)jsonString);
        }
        catch (JsonSyntaxException ignored) {
            Simulated.LOGGER.info("ponders_watched.json was malformed.");
        }
        return element;
    }

    public record RegisterBuilder(Item[] items) {
        public RegisterBuilder addScenes(ResourceLocation ... scenes) {
            HashSet<ResourceLocation> sceneSet = new HashSet<ResourceLocation>(List.of(scenes));
            for (Item item : this.items) {
                NEW_PONDER_SCENES.computeIfAbsent(item, k -> new HashSet()).addAll(sceneSet);
            }
            return this;
        }
    }
}
