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
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.simibubi.create.content.trains.CameraDistanceModifier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CameraDistanceCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"camera").then(Commands.literal((String)"reset").executes(ctx -> {
            CameraDistanceModifier.zoomOut(1.0f);
            return 1;
        }))).then(Commands.argument((String)"multiplier", (ArgumentType)FloatArgumentType.floatArg((float)1.0f)).executes(ctx -> {
            float multiplier = FloatArgumentType.getFloat((CommandContext)ctx, (String)"multiplier");
            CameraDistanceModifier.zoomOut(multiplier);
            return 1;
        }));
    }
}
