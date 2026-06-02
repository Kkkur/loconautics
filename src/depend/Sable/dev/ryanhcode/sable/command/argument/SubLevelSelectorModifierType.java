/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  it.unimi.dsi.fastutil.Pair
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class SubLevelSelectorModifierType {
    private static final Map<String, SubLevelSelectorModifierType> MODIFIERS_BY_NAME = new Object2ObjectOpenHashMap();
    private static final SimpleCommandExceptionType UNKNOWN_PROPERTY_NAME = new SimpleCommandExceptionType((Message)Component.translatable((String)"argument.sable.sub_level.unknown_property"));
    private final String name;
    private final Parser parser;
    private final FilterPriority filterPriority;

    public SubLevelSelectorModifierType(String name, Parser parser, FilterPriority priority) {
        this.name = name;
        this.parser = parser;
        this.filterPriority = priority;
    }

    public static void registerType(String name, Parser parser, FilterPriority filterPriority) {
        if (MODIFIERS_BY_NAME.containsKey(name)) {
            throw new IllegalArgumentException("Modifier type " + name + " already registered");
        }
        MODIFIERS_BY_NAME.put(name, new SubLevelSelectorModifierType(name, parser, filterPriority));
    }

    public static SubLevelSelectorModifierType getModifier(String propertyName, StringReader readerForErrorContext) throws CommandSyntaxException {
        if (!MODIFIERS_BY_NAME.containsKey(propertyName)) {
            throw UNKNOWN_PROPERTY_NAME.createWithContext((ImmutableStringReader)readerForErrorContext);
        }
        return MODIFIERS_BY_NAME.get(propertyName);
    }

    public static void clearRegistry() {
        MODIFIERS_BY_NAME.clear();
    }

    public static List<Pair<String, Message>> getAllNamesWithTooltip() {
        ArrayList<Pair<String, Message>> modifiers = new ArrayList<Pair<String, Message>>();
        for (SubLevelSelectorModifierType modifier : MODIFIERS_BY_NAME.values()) {
            modifiers.add((Pair<String, Message>)Pair.of((Object)modifier.name, (Object)Component.translatable((String)("argument.sable.sub_level.modifier." + modifier.name))));
        }
        return modifiers;
    }

    public Parser getParser() {
        return this.parser;
    }

    public FilterPriority getFilterPriority() {
        return this.filterPriority;
    }

    public static interface Parser {
        public Modifier parse(StringReader var1) throws CommandSyntaxException;
    }

    public static enum FilterPriority {
        POSITION,
        FILTER,
        SORTING,
        SORTING_SELECTION;

    }

    public static interface Modifier {
        public int getMaxResults();

        @Nullable
        public List<ServerSubLevel> apply(List<ServerSubLevel> var1, Vector3d var2);
    }
}
