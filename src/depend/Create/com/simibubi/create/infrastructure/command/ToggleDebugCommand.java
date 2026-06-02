/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.simibubi.create.content.kinetics.KineticDebugger;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ToggleDebugCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"rainbowDebug").requires(cs -> cs.hasPermission(0))).then(Commands.argument((String)"status", (ArgumentType)BoolArgumentType.bool()).executes(ctx -> {
            KineticDebugger.rainbowDebug = BoolArgumentType.getBool((CommandContext)ctx, (String)"status");
            MutableComponent text = ToggleDebugCommand.boolToText(KineticDebugger.rainbowDebug).append((Component)Component.literal((String)" Rainbow Debug Utility").withStyle(ChatFormatting.WHITE));
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> ToggleDebugCommand.lambda$register$1((Component)text), false);
            return 1;
        }))).executes(ctx -> {
            MutableComponent text = Component.literal((String)"Rainbow Debug Utility is currently: ").append((Component)ToggleDebugCommand.boolToText(KineticDebugger.rainbowDebug));
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> ToggleDebugCommand.lambda$register$3((Component)text), false);
            return 1;
        });
    }

    private static MutableComponent boolToText(boolean b) {
        if (b) {
            return Component.literal((String)"enabled").withStyle(ChatFormatting.GREEN);
        }
        return Component.literal((String)"disabled").withStyle(ChatFormatting.RED);
    }

    private static /* synthetic */ Component lambda$register$3(Component text) {
        return text;
    }

    private static /* synthetic */ Component lambda$register$1(Component text) {
        return text;
    }
}
