/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  net.minecraft.advancements.critereon.MinMaxBounds$Doubles
 *  net.minecraft.network.chat.Component
 */
package dev.ryanhcode.sable.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.command.argument.SubLevelSelectorModifierType;
import dev.ryanhcode.sable.command.argument.modifier_type.SubLevelDoubleFilter;
import dev.ryanhcode.sable.command.argument.modifier_type.SubLevelDoubleRangeFilter;
import dev.ryanhcode.sable.command.argument.modifier_type.SubLevelLimitFilter;
import dev.ryanhcode.sable.command.argument.modifier_type.SubLevelNameFilter;
import dev.ryanhcode.sable.command.argument.modifier_type.SubLevelSortModifier;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;

public class SubLevelSelectorModifiers {
    public static final SimpleCommandExceptionType EXPECTED_END_OF_MODIFIER = new SimpleCommandExceptionType((Message)Component.translatable((String)"argument.sable.sub_level.expected_end_of_modifier"));
    public static final SimpleCommandExceptionType EXPECTED_POSITIVE_INTEGER = new SimpleCommandExceptionType((Message)Component.translatable((String)"argument.sable.sub_level.expected_positive_integer"));
    public static final SimpleCommandExceptionType EXPECTED_POSITIVE_DECIMAL = new SimpleCommandExceptionType((Message)Component.translatable((String)"argument.sable.sub_level.expected_positive_decimal"));
    public static final SimpleCommandExceptionType EXPECTED_SORTING_TYPE = new SimpleCommandExceptionType((Message)Component.translatable((String)"argument.sable.sub_level.expected_sorting"));
    public static final SimpleCommandExceptionType EXPECTED_POSITIVE_RANGE = new SimpleCommandExceptionType((Message)Component.translatable((String)"argument.sable.sub_level.expected_positive_range"));

    private static void registerDoubleArgument(String name, boolean onlyPositive, SubLevelDoubleFilter.Factory factory) {
        SubLevelSelectorModifierType.registerType(name, reader -> {
            int i = reader.getCursor();
            double value = reader.readDouble();
            if (onlyPositive && value < 0.0) {
                reader.setCursor(i);
                throw EXPECTED_POSITIVE_DECIMAL.createWithContext((ImmutableStringReader)reader);
            }
            return factory.create(value);
        }, SubLevelSelectorModifierType.FilterPriority.FILTER);
    }

    private static void registerDoubleRangeArgument(String name, boolean onlyPositive, SubLevelDoubleRangeFilter.Factory factory) {
        SubLevelSelectorModifierType.registerType(name, reader -> {
            int i = reader.getCursor();
            MinMaxBounds.Doubles doubles = MinMaxBounds.Doubles.fromReader((StringReader)reader);
            if (onlyPositive && (doubles.min().isPresent() && (Double)doubles.min().get() < 0.0 || doubles.max().isPresent() && (Double)doubles.max().get() < 0.0)) {
                reader.setCursor(i);
                throw EXPECTED_POSITIVE_RANGE.createWithContext((ImmutableStringReader)reader);
            }
            return factory.create(doubles);
        }, SubLevelSelectorModifierType.FilterPriority.FILTER);
    }

    public static void registerModifiers() {
        SubLevelSelectorModifiers.registerDoubleRangeArgument("distance", true, SubLevelDoubleRangeFilter.squared((subLevel, sourcePos) -> subLevel.logicalPose().position().distanceSquared(sourcePos)));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("x", false, SubLevelDoubleRangeFilter.linear((subLevel, sourcePos) -> subLevel.logicalPose().position().x()));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("y", false, SubLevelDoubleRangeFilter.linear((subLevel, sourcePos) -> subLevel.logicalPose().position().y()));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("z", false, SubLevelDoubleRangeFilter.linear((subLevel, sourcePos) -> subLevel.logicalPose().position().z()));
        SubLevelSelectorModifiers.registerDoubleArgument("dx", false, SubLevelDoubleFilter.factory((subLevel, sourcePos, value) -> {
            double dx = subLevel.logicalPose().position().x() - sourcePos.x();
            if (value < 0.0) {
                return dx < 0.0 && dx > value;
            }
            return dx > 0.0 && dx < value;
        }));
        SubLevelSelectorModifiers.registerDoubleArgument("dy", false, SubLevelDoubleFilter.factory((subLevel, sourcePos, value) -> {
            double dy = subLevel.logicalPose().position().y() - sourcePos.y();
            if (value < 0.0) {
                return dy < 0.0 && dy > value;
            }
            return dy > 0.0 && dy < value;
        }));
        SubLevelSelectorModifiers.registerDoubleArgument("dz", false, SubLevelDoubleFilter.factory((subLevel, sourcePos, value) -> {
            double dz = subLevel.logicalPose().position().z() - sourcePos.z();
            if (value < 0.0) {
                return dz < 0.0 && dz > value;
            }
            return dz > 0.0 && dz < value;
        }));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("vx", false, SubLevelDoubleRangeFilter.linear((subLevel, sourcePos) -> subLevel.latestLinearVelocity.x));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("vy", false, SubLevelDoubleRangeFilter.linear((subLevel, sourcePos) -> subLevel.latestLinearVelocity.y));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("vz", false, SubLevelDoubleRangeFilter.linear((subLevel, sourcePos) -> subLevel.latestLinearVelocity.z));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("speed", true, SubLevelDoubleRangeFilter.squared((subLevel, sourcePos) -> subLevel.latestLinearVelocity.lengthSquared()));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("mass", true, SubLevelDoubleRangeFilter.linear((subLevel, sourcePos) -> subLevel.getMassTracker().getMass()));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("volume", true, SubLevelDoubleRangeFilter.linear((subLevel, sourcePos) -> subLevel.getPlot().getBoundingBox().volume()));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("width", true, SubLevelDoubleRangeFilter.linear((subLevel, sourcePos) -> subLevel.getPlot().getBoundingBox().width()));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("height", true, SubLevelDoubleRangeFilter.linear((subLevel, sourcePos) -> subLevel.getPlot().getBoundingBox().height()));
        SubLevelSelectorModifiers.registerDoubleRangeArgument("length", true, SubLevelDoubleRangeFilter.linear((subLevel, sourcePos) -> subLevel.getPlot().getBoundingBox().length()));
        SubLevelSelectorModifierType.registerType("name", reader -> {
            String name = SubLevelSelectorModifiers.readUntilEndOfModifier(reader);
            return new SubLevelNameFilter(name);
        }, SubLevelSelectorModifierType.FilterPriority.FILTER);
        SubLevelSelectorModifierType.registerType("sort", reader -> {
            SubLevelArgumentType.setSuggestions(reader, "nearest", "furthest");
            String filtering = SubLevelSelectorModifiers.tryReadString(reader, EXPECTED_SORTING_TYPE, "nearest", "furthest");
            SubLevelSelectorModifiers.expectEndOfModifier(reader);
            return new SubLevelSortModifier(filtering);
        }, SubLevelSelectorModifierType.FilterPriority.SORTING);
        SubLevelSelectorModifierType.registerType("limit", reader -> {
            int limit = SubLevelSelectorModifiers.readPositiveIntStrict(reader);
            return new SubLevelLimitFilter(limit);
        }, SubLevelSelectorModifierType.FilterPriority.SORTING_SELECTION);
    }

    private static Integer readPositiveIntStrict(StringReader reader) throws CommandSyntaxException {
        StringBuilder builder = new StringBuilder();
        while (reader.canRead() && reader.peek() >= '0' && reader.peek() <= '9') {
            builder.append(reader.read());
        }
        if (builder.isEmpty()) {
            throw EXPECTED_POSITIVE_INTEGER.createWithContext((ImmutableStringReader)reader);
        }
        return Integer.parseInt(builder.toString());
    }

    private static boolean isEndOfModifier(StringReader reader) {
        return reader.peek() == ',' || reader.peek() == ']';
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static String readUntilEndOfModifier(StringReader reader) throws CommandSyntaxException {
        StringBuilder builder = new StringBuilder();
        if (reader.canRead() && reader.peek() == '\"') {
            reader.skip();
            boolean thereIsNoEscape = false;
            while (reader.canRead() && (thereIsNoEscape || reader.peek() != '\"')) {
                if (!thereIsNoEscape && reader.peek() == '\\') {
                    thereIsNoEscape = true;
                    reader.skip();
                    continue;
                }
                builder.append(reader.read());
                thereIsNoEscape = false;
            }
            if (!reader.canRead()) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext((ImmutableStringReader)reader);
            reader.skip();
            return builder.toString();
        } else {
            while (reader.canRead() && !SubLevelSelectorModifiers.isEndOfModifier(reader)) {
                builder.append(reader.read());
            }
        }
        return builder.toString();
    }

    private static String tryReadString(StringReader reader, SimpleCommandExceptionType exception, String ... accepted) throws CommandSyntaxException {
        StringBuilder builder = new StringBuilder();
        while (reader.canRead()) {
            if (SubLevelSelectorModifiers.isEndOfModifier(reader)) {
                throw exception.createWithContext((ImmutableStringReader)reader);
            }
            builder.append(reader.read());
            for (String s : accepted) {
                if (!builder.toString().equals(s)) continue;
                return builder.toString();
            }
        }
        throw exception.createWithContext((ImmutableStringReader)reader);
    }

    private static void expectEndOfModifier(StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead() || !SubLevelSelectorModifiers.isEndOfModifier(reader)) {
            throw EXPECTED_END_OF_MODIFIER.createWithContext((ImmutableStringReader)reader);
        }
    }
}
