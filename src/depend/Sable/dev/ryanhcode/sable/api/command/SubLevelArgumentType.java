/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  it.unimi.dsi.fastutil.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo$Template
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.api.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.ryanhcode.sable.api.command.SableCommandHelper;
import dev.ryanhcode.sable.command.argument.SubLevelSelector;
import dev.ryanhcode.sable.command.argument.SubLevelSelectorModifierType;
import dev.ryanhcode.sable.command.argument.SubLevelSelectorType;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SubLevelArgumentType
implements ArgumentType<SubLevelSelector> {
    public static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> NO_SUGGESTIONS = SuggestionsBuilder::buildFuture;
    private static final SimpleCommandExceptionType ERROR_SINGLE_SUB_LEVEL_REQUIRED = new SimpleCommandExceptionType((Message)Component.translatable((String)"argument.sable.single_sub_level_required"));
    private static final SimpleCommandExceptionType ERROR_INVALID_SUBLEVEL = new SimpleCommandExceptionType((Message)Component.translatable((String)"argument.sable.sub_level.invalid"));
    private static final SimpleCommandExceptionType UNEXPECTED_END_OF_INPUT = new SimpleCommandExceptionType((Message)Component.translatable((String)"argument.sable.unexpected_end_of_input"));
    private static final String STATIC_WORLD = "static_world";
    private static final Collection<String> EXAMPLES = Arrays.stream(SubLevelSelectorType.values()).map(type -> "@" + type.getChar()).toList();
    private static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = NO_SUGGESTIONS;
    private final boolean allowStaticLevel;
    private final boolean allowMultiple;

    public SubLevelArgumentType(boolean allowStaticLevel, boolean allowMultiple) {
        this.allowStaticLevel = allowStaticLevel;
        this.allowMultiple = allowMultiple;
    }

    public static Collection<ServerSubLevel> getSubLevels(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        return ((SubLevelSelector)ctx.getArgument(name, SubLevelSelector.class)).getSubLevels((CommandSourceStack)ctx.getSource());
    }

    public static ServerSubLevel getSingleSubLevel(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        Collection<ServerSubLevel> subLevels = ((SubLevelSelector)ctx.getArgument(name, SubLevelSelector.class)).getSubLevels((CommandSourceStack)ctx.getSource());
        if (subLevels.size() > 1) {
            throw ERROR_SINGLE_SUB_LEVEL_REQUIRED.create();
        }
        if (subLevels.isEmpty()) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
        }
        return subLevels.stream().findFirst().orElseThrow();
    }

    public static SubLevelArgumentType singleSubLevel() {
        return new SubLevelArgumentType(false, false);
    }

    public static SubLevelArgumentType subLevels() {
        return new SubLevelArgumentType(false, true);
    }

    public static SubLevelArgumentType subLevelsOrLevel() {
        return new SubLevelArgumentType(true, true);
    }

    @NotNull
    private static List<Pair<SubLevelSelectorModifierType, SubLevelSelectorModifierType.Modifier>> parseSelectorArguments(StringReader reader) throws CommandSyntaxException {
        ObjectArrayList modifiers = new ObjectArrayList();
        SubLevelArgumentType.setSuggestions(reader, "[");
        ArrayList<Pair<String, @Nullable Message>> permittedPreEntryToken = new ArrayList<Pair<String, Message>>(SubLevelSelectorModifierType.getAllNamesWithTooltip().stream().map(s -> Pair.of((Object)((String)s.first() + "="), (Object)((Message)s.second()))).toList());
        permittedPreEntryToken.add((Pair)Pair.of((Object)"]", null));
        boolean isFirstEntry = true;
        if (reader.canRead() && reader.peek() == '[') {
            reader.skip();
            SubLevelArgumentType.setSuggestionsWithTooltip(reader, permittedPreEntryToken);
            while (reader.canRead() && reader.peek() != ']') {
                if (reader.peek() == ',') {
                    reader.skip();
                }
                SubLevelArgumentType.setSuggestionsWithTooltip(reader, permittedPreEntryToken);
                String propertyName = SubLevelArgumentType.readUntilEndOrCharacter(reader, '=');
                if (!reader.canRead() || reader.peek() != '=') {
                    throw UNEXPECTED_END_OF_INPUT.createWithContext((ImmutableStringReader)reader);
                }
                reader.skip();
                SubLevelSelectorModifierType modifierType = SubLevelSelectorModifierType.getModifier(propertyName, reader);
                if (modifierType == null) {
                    throw UNEXPECTED_END_OF_INPUT.createWithContext((ImmutableStringReader)reader);
                }
                SubLevelSelectorModifierType.Modifier modifier = modifierType.getParser().parse(reader);
                modifiers.add(Pair.of((Object)modifierType, (Object)modifier));
                SubLevelArgumentType.setSuggestionsWithTooltip(reader, permittedPreEntryToken);
                if (!isFirstEntry) continue;
                permittedPreEntryToken.add((Pair<String, Message>)Pair.of((Object)",", null));
                isFirstEntry = false;
            }
            if (reader.canRead() && reader.peek() == ']') {
                reader.skip();
            } else {
                throw UNEXPECTED_END_OF_INPUT.createWithContext((ImmutableStringReader)reader);
            }
        }
        return modifiers;
    }

    public static void setSuggestions(StringReader reader, String ... suggested) {
        SubLevelArgumentType.setSuggestions(reader, Arrays.asList(suggested));
    }

    public static void setSuggestions(StringReader reader, List<String> suggested) {
        SubLevelArgumentType.setSuggestionsWithTooltip(reader, suggested.stream().map(s -> Pair.of((Object)s, (Object)null)).toList());
    }

    @SafeVarargs
    public static void setSuggestionsWithTooltip(StringReader reader, Pair<String, Message> ... suggested) {
        SubLevelArgumentType.setSuggestionsWithTooltip(reader, Arrays.asList(suggested));
    }

    public static void setSuggestionsWithTooltip(StringReader reader, List<Pair<String, @Nullable Message>> suggested) {
        int cursor = reader.getCursor();
        suggestions = builder -> {
            SuggestionsBuilder nextSuggestion = builder.createOffset(cursor);
            for (Pair suggestion : suggested) {
                if (!((String)suggestion.first()).startsWith(builder.getInput().substring(cursor))) continue;
                if (suggestion.second() != null) {
                    nextSuggestion.suggest((String)suggestion.first(), (Message)suggestion.second());
                    continue;
                }
                nextSuggestion.suggest((String)suggestion.first());
            }
            return nextSuggestion.buildFuture();
        };
    }

    public static String readUntilEndOrCharacter(StringReader reader, char character) throws CommandSyntaxException {
        StringBuilder builder = new StringBuilder();
        while (reader.canRead() && reader.peek() != character) {
            builder.append(reader.read());
        }
        if (builder.isEmpty()) {
            throw UNEXPECTED_END_OF_INPUT.create();
        }
        return builder.toString();
    }

    /*
     * Issues handling annotations - annotations may be inaccurate
     */
    public SubLevelSelector parse(StringReader reader) throws CommandSyntaxException {
        @Nullable ObjectArrayList allowedSelectors = new ObjectArrayList();
        if (this.allowStaticLevel) {
            allowedSelectors.add((Object)Pair.of((Object)STATIC_WORLD, (Object)Component.translatable((String)"argument.sable.body.static_world")));
        }
        for (SubLevelSelectorType selector : SubLevelSelectorType.values()) {
            allowedSelectors.add((Object)Pair.of((Object)("@" + selector.getChar()), (Object)selector.getTooltip()));
        }
        SubLevelArgumentType.setSuggestionsWithTooltip(reader, (List<Pair<String, Message>>)allowedSelectors);
        if (this.allowStaticLevel && reader.canRead(STATIC_WORLD.length()) && reader.peek() == STATIC_WORLD.charAt(0)) {
            String staticWorld = reader.readString();
            if (!staticWorld.equals(STATIC_WORLD)) {
                throw ERROR_INVALID_SUBLEVEL.create();
            }
            return new SubLevelSelector(null, (List<Pair<SubLevelSelectorModifierType, SubLevelSelectorModifierType.Modifier>>)new ObjectArrayList());
        }
        if (!reader.canRead()) {
            throw ERROR_INVALID_SUBLEVEL.create();
        }
        char firstChar = reader.read();
        if (!reader.canRead() || firstChar != '@') {
            throw ERROR_INVALID_SUBLEVEL.create();
        }
        if (!reader.canRead()) {
            throw ERROR_INVALID_SUBLEVEL.create();
        }
        SubLevelSelectorType selectorType = SubLevelSelectorType.of(reader.read());
        if (selectorType == null) {
            throw ERROR_INVALID_SUBLEVEL.create();
        }
        int maximumResults = Integer.MAX_VALUE;
        if (selectorType.single()) {
            maximumResults = 1;
        }
        List<Pair<SubLevelSelectorModifierType, SubLevelSelectorModifierType.Modifier>> modifiers = SubLevelArgumentType.parseSelectorArguments(reader);
        for (Pair<SubLevelSelectorModifierType, SubLevelSelectorModifierType.Modifier> modifierPair : modifiers) {
            maximumResults = Math.min(maximumResults, ((SubLevelSelectorModifierType.Modifier)modifierPair.second()).getMaxResults());
        }
        if (maximumResults > 1 && !this.allowMultiple) {
            throw ERROR_SINGLE_SUB_LEVEL_REQUIRED.create();
        }
        return new SubLevelSelector(selectorType, modifiers);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder builder) {
        StringReader stringreader = new StringReader(builder.getInput());
        stringreader.setCursor(builder.getStart());
        suggestions = NO_SUGGESTIONS;
        try {
            this.parse(stringreader);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return suggestions.apply(builder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static class Info
    implements ArgumentTypeInfo<SubLevelArgumentType, Template> {
        private static final byte FLAG_MULTIPLE = 1;
        private static final byte FLAG_STATIC_ALLOWED = 2;

        public void serializeToNetwork(Template template, FriendlyByteBuf byteBuf) {
            int serialized = 0;
            if (template.allowMultiple) {
                serialized |= 1;
            }
            if (template.allowStaticLevel) {
                serialized |= 2;
            }
            byteBuf.writeByte(serialized);
        }

        public Template deserializeFromNetwork(FriendlyByteBuf arg) {
            byte serialized = arg.readByte();
            return new Template((serialized & 1) != 0, (serialized & 2) != 0);
        }

        public void serializeToJson(Template arg, JsonObject jsonObject) {
            jsonObject.addProperty("amount", arg.allowMultiple ? "single" : "multiple");
            jsonObject.addProperty("type", arg.allowStaticLevel ? "players" : "entities");
        }

        public Template unpack(SubLevelArgumentType arg) {
            return new Template(arg.allowMultiple, arg.allowStaticLevel);
        }

        public final class Template
        implements ArgumentTypeInfo.Template<SubLevelArgumentType> {
            final boolean allowMultiple;
            final boolean allowStaticLevel;

            Template(boolean allowMultiple, boolean allowStaticLevel) {
                this.allowMultiple = allowMultiple;
                this.allowStaticLevel = allowStaticLevel;
            }

            public SubLevelArgumentType instantiate(CommandBuildContext commandBuildContext) {
                return new SubLevelArgumentType(this.allowStaticLevel, this.allowMultiple);
            }

            public ArgumentTypeInfo<SubLevelArgumentType, ?> type() {
                return Info.this;
            }
        }
    }
}
