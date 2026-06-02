/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.simibubi.create.foundation.utility.FilesHelper
 *  com.tterrag.registrate.providers.RegistrateLangProvider
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.lang.LangBuilder
 *  net.createmod.catnip.lang.LangNumberFormat
 *  net.createmod.ponder.api.registration.PonderPlugin
 *  net.createmod.ponder.foundation.PonderIndex
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.ryanhcode.offroad.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.ryanhcode.offroad.content.ponder.OffroadPonderPlugin;
import dev.ryanhcode.offroad.index.OffroadAdvancements;
import dev.ryanhcode.offroad.index.OffroadSoundEvents;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.lang.LangNumberFormat;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class OffroadLang {
    public static LangBuilder builder() {
        return Lang.builder((String)"offroad");
    }

    public static LangBuilder text(String text) {
        return OffroadLang.builder().text(text);
    }

    public static LangBuilder translate(String key, Object ... args) {
        return OffroadLang.builder().translate(key, args);
    }

    public static LangBuilder number(double number) {
        return OffroadLang.builder().text(LangNumberFormat.format((double)number));
    }

    public static LangBuilder space() {
        return OffroadLang.builder().space();
    }

    public static void emptyLine(List<Component> tooltip) {
        OffroadLang.builder().text("").forGoggles(tooltip);
    }

    public static LangBuilder blockName(BlockState blockState) {
        return OffroadLang.builder().add(blockState.getBlock().getName());
    }

    public static List<Component> translatedOptions(String prefix, String ... keys) {
        ArrayList<Component> result = new ArrayList<Component>(keys.length);
        for (String key : keys) {
            result.add((Component)OffroadLang.translate((String)(prefix != null ? prefix + "." : "") + key, new Object[0]).component());
        }
        return result;
    }

    public static LangBuilder kilopixelGram(double value) {
        return OffroadLang.kilopixelGram(value, "%.2f");
    }

    public static LangBuilder kilopixelGram(double value, String format) {
        return OffroadLang.getPrefixedUnit("pg", value, format);
    }

    public static LangBuilder kilopixelNewton(double value) {
        return OffroadLang.kilopixelNewton(value, "%.2f");
    }

    public static LangBuilder kilopixelNewton(double value, String format) {
        return OffroadLang.getPrefixedUnit("pn", value, format);
    }

    private static LangBuilder getPrefixedUnit(String unit, double value, String format) {
        int index;
        String[] prefixes = new String[]{"k", "m", "g"};
        for (index = 0; value >= 1000.0 && index < prefixes.length - 1; value /= 1000.0, ++index) {
        }
        return OffroadLang.translate("unit." + prefixes[index] + unit, format.formatted(value));
    }

    public static void registrateLang(RegistrateLangProvider provider) {
        BiConsumer<String, String> consumer = (arg_0, arg_1) -> ((RegistrateLangProvider)provider).add(arg_0, arg_1);
        Map<String, String> lang = OffroadLang.getLangMap("en_us");
        lang.forEach(consumer);
        OffroadAdvancements.provideLang(consumer);
        OffroadSoundEvents.REGISTRY.provideLang(consumer);
        PonderIndex.addPlugin((PonderPlugin)new OffroadPonderPlugin());
        PonderIndex.getLangAccess().provideLang("offroad", consumer);
    }

    private static Map<String, String> getLangMap(String lang) {
        String filepath = "datagen/lang/%s.json".formatted(lang);
        JsonObject langObject = FilesHelper.loadJsonResource((String)filepath).getAsJsonObject();
        HashMap<String, String> langMap = new HashMap<String, String>();
        OffroadLang.flattenJson(langMap, (JsonElement)langObject, null);
        return langMap;
    }

    private static void flattenJson(Map<String, String> outputMap, JsonElement element, String currentPath) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String string = element.getAsJsonPrimitive().getAsString();
            outputMap.put(currentPath, string);
            return;
        }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (String key : object.keySet()) {
                JsonElement value = object.get(key);
                String path = currentPath != null ? currentPath + "." + key : key;
                OffroadLang.flattenJson(outputMap, value, path);
            }
        }
    }
}
