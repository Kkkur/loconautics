/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.server.commands.data.DataAccessor
 *  net.minecraft.server.commands.data.DataCommands$DataProvider
 */
package dev.ryanhcode.sable.command.data_accessor;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.command.data_accessor.SubLevelDataAccessor;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;

static class SubLevelDataAccessor.1
implements DataCommands.DataProvider {
    final /* synthetic */ String val$string;

    SubLevelDataAccessor.1(String string) {
        this.val$string = string;
    }

    public DataAccessor access(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        return new SubLevelDataAccessor(SubLevelArgumentType.getSingleSubLevel(commandContext, this.val$string));
    }

    public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> argumentBuilder, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> function) {
        return argumentBuilder.then(Commands.literal((String)"sub_level").then(function.apply((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument((String)this.val$string, (ArgumentType)SubLevelArgumentType.singleSubLevel()))));
    }
}
