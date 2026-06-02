/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class KillTPSCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"killtps").requires(cs -> cs.hasPermission(2))).executes(ctx -> {
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> CreateLang.translateDirect("command.killTPSCommand.status.slowed_by.0", Create.LAGGER.isLagging() ? Create.LAGGER.getTickTime() : 0), true);
            if (Create.LAGGER.isLagging()) {
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> CreateLang.translateDirect("command.killTPSCommand.status.usage.0", new Object[0]), true);
            } else {
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> CreateLang.translateDirect("command.killTPSCommand.status.usage.1", new Object[0]), true);
            }
            return 1;
        })).then(((LiteralArgumentBuilder)Commands.literal((String)"start").executes(ctx -> {
            int tickTime = Create.LAGGER.getTickTime();
            if (tickTime > 0) {
                Create.LAGGER.setLagging(true);
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> CreateLang.translateDirect("command.killTPSCommand.status.slowed_by.1", tickTime), true);
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> CreateLang.translateDirect("command.killTPSCommand.status.usage.0", new Object[0]), true);
            } else {
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> CreateLang.translateDirect("command.killTPSCommand.status.usage.1", new Object[0]), true);
            }
            return 1;
        })).then(Commands.argument((String)CreateLang.translateDirect("command.killTPSCommand.argument.tickTime", new Object[0]).getString(), (ArgumentType)IntegerArgumentType.integer((int)1)).executes(ctx -> {
            int tickTime = IntegerArgumentType.getInteger((CommandContext)ctx, (String)CreateLang.translateDirect("command.killTPSCommand.argument.tickTime", new Object[0]).getString());
            Create.LAGGER.setTickTime(tickTime);
            Create.LAGGER.setLagging(true);
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> CreateLang.translateDirect("command.killTPSCommand.status.slowed_by.1", tickTime), true);
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> CreateLang.translateDirect("command.killTPSCommand.status.usage.0", new Object[0]), true);
            return 1;
        })))).then(Commands.literal((String)"stop").executes(ctx -> {
            Create.LAGGER.setLagging(false);
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> CreateLang.translateDirect("command.killTPSCommand.status.slowed_by.2", new Object[0]), false);
            return 1;
        }));
    }
}
