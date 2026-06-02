/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.EntityArgument
 *  net.neoforged.neoforge.server.command.EnumArgument
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.simibubi.create.foundation.utility.CameraAngleAnimationService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.neoforged.neoforge.server.command.EnumArgument;

public class CameraAngleCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"angle").requires(cs -> cs.hasPermission(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument((String)"players", (ArgumentType)EntityArgument.players()).then(Commands.literal((String)"yaw").then(Commands.argument((String)"degrees", (ArgumentType)FloatArgumentType.floatArg()).executes(ctx -> {
            float angleTarget = FloatArgumentType.getFloat((CommandContext)ctx, (String)"degrees");
            CameraAngleAnimationService.setYawTarget(angleTarget);
            return 1;
        })))).then(Commands.literal((String)"pitch").then(Commands.argument((String)"degrees", (ArgumentType)FloatArgumentType.floatArg()).executes(ctx -> {
            float angleTarget = FloatArgumentType.getFloat((CommandContext)ctx, (String)"degrees");
            CameraAngleAnimationService.setPitchTarget(angleTarget);
            return 1;
        })))).then(Commands.literal((String)"mode").then(((RequiredArgumentBuilder)Commands.argument((String)"mode", (ArgumentType)EnumArgument.enumArgument(CameraAngleAnimationService.Mode.class)).executes(ctx -> {
            CameraAngleAnimationService.Mode mode = (CameraAngleAnimationService.Mode)((Object)((Object)ctx.getArgument("mode", CameraAngleAnimationService.Mode.class)));
            CameraAngleAnimationService.setAnimationMode(mode);
            return 1;
        })).then(Commands.argument((String)"speed", (ArgumentType)FloatArgumentType.floatArg((float)0.0f)).executes(ctx -> {
            CameraAngleAnimationService.Mode mode = (CameraAngleAnimationService.Mode)((Object)((Object)ctx.getArgument("mode", CameraAngleAnimationService.Mode.class)));
            float speed = FloatArgumentType.getFloat((CommandContext)ctx, (String)"speed");
            CameraAngleAnimationService.setAnimationMode(mode);
            CameraAngleAnimationService.setAnimationSpeed(speed);
            return 1;
        })))));
    }
}
