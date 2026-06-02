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
package dev.eriksonn.aeronautics.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.eriksonn.aeronautics.content.ponder.AeroPonderPlugin;
import dev.eriksonn.aeronautics.index.AeroAdvancements;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
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

public class AeroLang {
    public static LangBuilder builder() {
        return Lang.builder((String)"aeronautics");
    }

    public static LangBuilder text(String text) {
        return AeroLang.builder().text(text);
    }

    public static LangBuilder translate(String key, Object ... args) {
        return AeroLang.builder().translate(key, args);
    }

    public static LangBuilder number(double number) {
        return AeroLang.builder().text(LangNumberFormat.format((double)number));
    }

    public static LangBuilder space() {
        return AeroLang.builder().space();
    }

    public static void emptyLine(List<Component> tooltip) {
        AeroLang.builder().text("").forGoggles(tooltip);
    }

    public static LangBuilder blockName(BlockState blockState) {
        return AeroLang.builder().add(blockState.getBlock().getName());
    }

    public static List<Component> translatedOptions(String prefix, String ... keys) {
        ArrayList<Component> result = new ArrayList<Component>(keys.length);
        for (String key : keys) {
            result.add((Component)AeroLang.translate((String)(prefix != null ? prefix + "." : "") + key, new Object[0]).component());
        }
        return result;
    }

    public static LangBuilder kilopixelGram(double value) {
        return AeroLang.translate("unit.kpg", String.format("%,.2f", value));
    }

    public static LangBuilder kilopixelGram(double value, String format) {
        return AeroLang.getPrefixedUnit("pg", value, format, 1);
    }

    public static LangBuilder pixelNewton(double value) {
        return AeroLang.pixelNewton(value, "%,.2f");
    }

    public static LangBuilder pixelNewton(double value, String format) {
        return AeroLang.getPrefixedUnit("pn", value, format, 0);
    }

    private static LangBuilder getPrefixedUnit(String unit, double value, String format, int offset) {
        int index;
        String[] prefixes = new String[]{"k", "m", "g"};
        for (index = offset - 1; value >= 1000.0 && index < prefixes.length - 1; value /= 1000.0, ++index) {
        }
        if (index >= 0) {
            unit = prefixes[index] + (String)unit;
        }
        return AeroLang.translate("unit." + (String)unit, format.formatted(value));
    }

    public static void registrateLang(RegistrateLangProvider provider) {
        BiConsumer<String, String> consumer = (arg_0, arg_1) -> ((RegistrateLangProvider)provider).add(arg_0, arg_1);
        Map<String, String> lang = AeroLang.getLangMap("en_us");
        lang.forEach(consumer);
        AeroAdvancements.provideLang(consumer);
        AeroSoundEvents.REGISTRY.provideLang(consumer);
        PonderIndex.addPlugin((PonderPlugin)new AeroPonderPlugin());
        PonderIndex.getLangAccess().provideLang("aeronautics", consumer);
    }

    private static Map<String, String> getLangMap(String lang) {
        String filepath = "datagen/lang/%s.json".formatted(lang);
        JsonObject langObject = FilesHelper.loadJsonResource((String)filepath).getAsJsonObject();
        HashMap<String, String> langMap = new HashMap<String, String>();
        AeroLang.flattenJson(langMap, (JsonElement)langObject, null);
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
                AeroLang.flattenJson(outputMap, value, path);
            }
        }
    }
}
