/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.coordinates.BlockPosArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class GlueCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"glue").requires(cs -> cs.hasPermission(2))).then(Commands.argument((String)"from", (ArgumentType)BlockPosArgument.blockPos()).then(Commands.argument((String)"to", (ArgumentType)BlockPosArgument.blockPos()).executes(ctx -> {
            BlockPos from = BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"from");
            BlockPos to = BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"to");
            ServerLevel world = ((CommandSourceStack)ctx.getSource()).getLevel();
            SuperGlueEntity entity = new SuperGlueEntity((Level)world, SuperGlueEntity.span(from, to));
            entity.playPlaceSound();
            world.addFreshEntity((Entity)entity);
            return 1;
        })));
    }
}
