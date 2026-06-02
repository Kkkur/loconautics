/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.EntityArgument
 *  net.minecraft.world.entity.Entity
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;

public class PassengerCommand {
    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"passenger").requires(cs -> cs.hasPermission(2))).then(Commands.argument((String)"rider", (ArgumentType)EntityArgument.entity()).then(((RequiredArgumentBuilder)Commands.argument((String)"vehicle", (ArgumentType)EntityArgument.entity()).executes(ctx -> {
            PassengerCommand.run((CommandSourceStack)ctx.getSource(), EntityArgument.getEntity((CommandContext)ctx, (String)"vehicle"), EntityArgument.getEntity((CommandContext)ctx, (String)"rider"), 0);
            return 1;
        })).then(Commands.argument((String)"seatIndex", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(ctx -> {
            PassengerCommand.run((CommandSourceStack)ctx.getSource(), EntityArgument.getEntity((CommandContext)ctx, (String)"vehicle"), EntityArgument.getEntity((CommandContext)ctx, (String)"rider"), IntegerArgumentType.getInteger((CommandContext)ctx, (String)"seatIndex"));
            return 1;
        }))));
    }

    private static void run(CommandSourceStack source, Entity vehicle, Entity rider, int index) {
        if (vehicle == rider) {
            return;
        }
        if (rider instanceof CarriageContraptionEntity) {
            return;
        }
        if (rider instanceof ControlledContraptionEntity) {
            return;
        }
        if (vehicle instanceof AbstractContraptionEntity) {
            AbstractContraptionEntity ace = (AbstractContraptionEntity)vehicle;
            if (ace.getContraption().getSeats().size() > index) {
                ace.addSittingPassenger(rider, index);
            }
            return;
        }
        rider.startRiding(vehicle, true);
    }
}
