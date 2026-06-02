/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
private static interface SableSpawnCommands.NamedSpawnInvoker<S> {
    public int run(CommandContext<S> var1, @Nullable String var2) throws CommandSyntaxException;
}
