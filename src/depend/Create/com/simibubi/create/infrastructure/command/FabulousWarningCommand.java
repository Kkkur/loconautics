/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class FabulousWarningCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal((String)"dismissFabulousWarning").executes(ctx -> {
            AllConfigs.client().ignoreFabulousWarning.set((Object)true);
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)"Disabled Fabulous graphics warning"), false);
            return 1;
        });
    }
}
