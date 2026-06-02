/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.UuidArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

public class TrainCommand {
    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"train").requires(cs -> cs.hasPermission(2))).then(Commands.literal((String)"remove").then(Commands.argument((String)"train", (ArgumentType)UuidArgument.uuid()).executes(ctx -> TrainCommand.runDelete((CommandSourceStack)ctx.getSource(), UuidArgument.getUuid((CommandContext)ctx, (String)"train")))))).then(Commands.literal((String)"tp").then(((RequiredArgumentBuilder)Commands.argument((String)"train", (ArgumentType)UuidArgument.uuid()).requires(CommandSourceStack::isPlayer)).executes(ctx -> TrainCommand.runTeleport((CommandSourceStack)ctx.getSource(), UuidArgument.getUuid((CommandContext)ctx, (String)"train")))));
    }

    private static int runDelete(CommandSourceStack source, UUID argument) {
        Train train = Create.RAILWAYS.trains.get(argument);
        if (train == null) {
            source.sendFailure((Component)Component.literal((String)("No Train with id " + argument.toString().substring(0, 5) + "[...] was found")));
            return 0;
        }
        train.invalid = true;
        source.sendSuccess(() -> Component.literal((String)"Train '").append(train.name).append("' removed successfully"), true);
        return 1;
    }

    private static int runTeleport(CommandSourceStack source, UUID argument) throws CommandSyntaxException {
        ServerPlayer serverPlayer = source.getPlayerOrException();
        GameType gameMode = serverPlayer.gameMode.getGameModeForPlayer();
        if (gameMode != GameType.CREATIVE && gameMode != GameType.SPECTATOR) {
            source.sendFailure((Component)Component.literal((String)"Can only teleport to train when in Creative or Spectator Mode!"));
            return 0;
        }
        Train train = Create.RAILWAYS.trains.get(argument);
        if (train == null) {
            source.sendFailure((Component)Component.literal((String)("No Train with id " + argument.toString().substring(0, 5) + "[...] was found")));
            return 0;
        }
        List<ResourceKey<Level>> presentDimensions = train.getPresentDimensions();
        if (presentDimensions.isEmpty()) {
            source.sendFailure((Component)Component.literal((String)"Unable to teleport to Train. No valid location found"));
            return 0;
        }
        ResourceKey<Level> levelKey = presentDimensions.get(0);
        ServerLevel serverLevel = serverPlayer.getServer().getLevel(levelKey);
        Optional<BlockPos> positionInDimension = train.getPositionInDimension(levelKey);
        if (positionInDimension.isEmpty() || serverLevel == null) {
            source.sendFailure((Component)Component.literal((String)"Unable to teleport to Train. No valid location found"));
            return 0;
        }
        BlockPos pos = positionInDimension.get();
        serverPlayer.teleportTo(serverLevel, (double)pos.getX(), (double)(pos.getY() + 5), (double)pos.getZ(), serverPlayer.getViewYRot(0.0f), serverPlayer.getViewXRot(0.0f));
        source.sendSuccess(() -> Component.literal((String)"Teleported to Train '").append(train.name).append("' successfully"), true);
        return 1;
    }
}
