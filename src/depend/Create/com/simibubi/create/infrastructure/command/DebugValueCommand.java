/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class DebugValueCommand {
    public static float value = 0.0f;

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"debugValue").requires(cs -> cs.hasPermission(4))).then(Commands.argument((String)"value", (ArgumentType)FloatArgumentType.floatArg()).executes(ctx -> {
            value = FloatArgumentType.getFloat((CommandContext)ctx, (String)"value");
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)("Set value to: " + value)), true);
            return 1;
        }));
    }
}
