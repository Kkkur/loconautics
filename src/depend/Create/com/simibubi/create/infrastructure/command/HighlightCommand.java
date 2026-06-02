/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.EntityArgument
 *  net.minecraft.commands.arguments.coordinates.BlockPosArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.infrastructure.command.HighlightPacket;
import java.util.Collection;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class HighlightCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"highlight").then(((RequiredArgumentBuilder)Commands.argument((String)"pos", (ArgumentType)BlockPosArgument.blockPos()).then(Commands.argument((String)"players", (ArgumentType)EntityArgument.players()).executes(ctx -> {
            Collection players = EntityArgument.getPlayers((CommandContext)ctx, (String)"players");
            BlockPos pos = BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"pos");
            CatnipServices.NETWORK.sendToClients((Iterable)players, (CustomPacketPayload)new HighlightPacket(pos));
            return players.size();
        }))).executes(ctx -> {
            BlockPos pos = BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"pos");
            CatnipServices.NETWORK.sendToClient((ServerPlayer)((CommandSourceStack)ctx.getSource()).getEntity(), (CustomPacketPayload)new HighlightPacket(pos));
            return 1;
        }))).executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
            return HighlightCommand.highlightAssemblyExceptionFor(player, (CommandSourceStack)ctx.getSource());
        });
    }

    private static void sendMissMessage(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal((String)"Try looking at a Block that has failed to assemble a Contraption and try again."), true);
    }

    private static int highlightAssemblyExceptionFor(ServerPlayer player, CommandSourceStack source) {
        double distance = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        Vec3 start = player.getEyePosition(1.0f);
        Vec3 look = player.getViewVector(1.0f);
        Vec3 end = start.add(look.x * distance, look.y * distance, look.z * distance);
        Level world = player.level();
        BlockHitResult ray = world.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)player));
        if (ray.getType() == HitResult.Type.MISS) {
            HighlightCommand.sendMissMessage(source);
            return 0;
        }
        BlockPos pos = ray.getBlockPos();
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof IDisplayAssemblyExceptions)) {
            HighlightCommand.sendMissMessage(source);
            return 0;
        }
        IDisplayAssemblyExceptions display = (IDisplayAssemblyExceptions)be;
        AssemblyException exception = display.getLastAssemblyException();
        if (exception == null) {
            HighlightCommand.sendMissMessage(source);
            return 0;
        }
        if (!exception.hasPosition()) {
            source.sendSuccess(() -> Component.literal((String)"Can't highlight a specific position for this issue"), true);
            return 1;
        }
        BlockPos p = exception.getPosition();
        String command = "/create highlight " + p.getX() + " " + p.getY() + " " + p.getZ();
        player.server.getCommands().performPrefixedCommand(source, command);
        return 1;
    }
}
