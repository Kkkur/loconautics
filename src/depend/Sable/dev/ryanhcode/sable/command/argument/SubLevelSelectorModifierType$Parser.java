/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package dev.ryanhcode.sable.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ryanhcode.sable.command.argument.SubLevelSelectorModifierType;

public static interface SubLevelSelectorModifierType.Parser {
    public SubLevelSelectorModifierType.Modifier parse(StringReader var1) throws CommandSyntaxException;
}
