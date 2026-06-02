/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.coordinates.BlockPosArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.BaseCommandBlock
 *  net.minecraft.world.level.block.CommandBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.CommandBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableInt;

public class ReplaceInCommandBlocksCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"replaceInCommandBlocks").requires(cs -> cs.hasPermission(2))).then(Commands.argument((String)"begin", (ArgumentType)BlockPosArgument.blockPos()).then(Commands.argument((String)"end", (ArgumentType)BlockPosArgument.blockPos()).then(Commands.argument((String)"toReplace", (ArgumentType)StringArgumentType.string()).then(Commands.argument((String)"replaceWith", (ArgumentType)StringArgumentType.string()).executes(ctx -> {
            ReplaceInCommandBlocksCommand.doReplace((CommandSourceStack)ctx.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"begin"), BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"end"), StringArgumentType.getString((CommandContext)ctx, (String)"toReplace"), StringArgumentType.getString((CommandContext)ctx, (String)"replaceWith"));
            return 1;
        })))));
    }

    private static void doReplace(CommandSourceStack source, BlockPos from, BlockPos to, String toReplace, String replaceWith) {
        ServerLevel world = source.getLevel();
        MutableInt blocks = new MutableInt(0);
        BlockPos.betweenClosedStream((BlockPos)from, (BlockPos)to).forEach(pos -> {
            BlockState blockState = world.getBlockState(pos);
            if (!(blockState.getBlock() instanceof CommandBlock)) {
                return;
            }
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof CommandBlockEntity)) {
                return;
            }
            CommandBlockEntity cb = (CommandBlockEntity)blockEntity;
            BaseCommandBlock commandBlockLogic = cb.getCommandBlock();
            String command = commandBlockLogic.getCommand();
            if (command.indexOf(toReplace) != -1) {
                blocks.increment();
            }
            commandBlockLogic.setCommand(command.replaceAll(toReplace, replaceWith));
            cb.setChanged();
            world.sendBlockUpdated(pos, blockState, blockState, 2);
        });
        int intValue = blocks.intValue();
        if (intValue == 0) {
            source.sendSuccess(() -> Component.literal((String)("Couldn't find \"" + toReplace + "\" anywhere.")), true);
            return;
        }
        source.sendSuccess(() -> Component.literal((String)("Replaced occurrences in " + intValue + " blocks.")), true);
    }
}
