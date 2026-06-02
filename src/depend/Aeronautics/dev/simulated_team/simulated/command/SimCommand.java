/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.simibubi.create.content.contraptions.glue.SuperGlueEntity
 *  dev.ryanhcode.sable.api.command.SubLevelArgumentType
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.coordinates.BlockPosArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 */
package dev.simulated_team.simulated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.command.SimDebugThingCommands;
import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueEntity;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffServerHandler;
import java.util.Collection;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class SimCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        LiteralArgumentBuilder cmd = Commands.literal((String)"simulated");
        if (CatnipServices.PLATFORM.isDevelopmentEnvironment()) {
            cmd.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"debugthing").requires(command -> command.hasPermission(2))).then(Commands.literal((String)"start").then(Commands.argument((String)"steps", (ArgumentType)IntegerArgumentType.integer()).executes(SimDebugThingCommands::start)))).then(Commands.literal((String)"stop").executes(SimDebugThingCommands::stop))).then(Commands.literal((String)"abort").executes(SimDebugThingCommands::abort))).then(Commands.literal((String)"stop_sublevels").executes(SimDebugThingCommands::stopSublevels)));
        }
        cmd.then(((LiteralArgumentBuilder)Commands.literal((String)"lock").requires(command -> command.hasPermission(2))).then(((RequiredArgumentBuilder)Commands.argument((String)"sub_levels", (ArgumentType)SubLevelArgumentType.subLevels()).executes(ctx -> SimCommand.lockSubLevels((CommandContext<CommandSourceStack>)ctx, true))).then(Commands.argument((String)"locked", (ArgumentType)BoolArgumentType.bool()).executes(ctx -> SimCommand.lockSubLevels((CommandContext<CommandSourceStack>)ctx, false)))));
        cmd.then(((LiteralArgumentBuilder)Commands.literal((String)"glue").requires(command -> command.hasPermission(2))).then(Commands.argument((String)"from", (ArgumentType)BlockPosArgument.blockPos()).then(Commands.argument((String)"to", (ArgumentType)BlockPosArgument.blockPos()).executes(SimCommand::glueArea))));
        dispatcher.register(cmd);
    }

    private static int glueArea(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BlockPos from = BlockPosArgument.getLoadedBlockPos(ctx, (String)"from");
        BlockPos to = BlockPosArgument.getLoadedBlockPos(ctx, (String)"to");
        ServerLevel world = ((CommandSourceStack)ctx.getSource()).getLevel();
        HoneyGlueEntity entity = new HoneyGlueEntity((Level)world, SuperGlueEntity.span((BlockPos)from, (BlockPos)to));
        world.addFreshEntity((Entity)entity);
        return 1;
    }

    private static int lockSubLevels(CommandContext<CommandSourceStack> ctx, boolean toggle) throws CommandSyntaxException {
        Collection subLevels = SubLevelArgumentType.getSubLevels(ctx, (String)"sub_levels");
        int updated = 0;
        PhysicsStaffServerHandler handler = PhysicsStaffServerHandler.get(((CommandSourceStack)ctx.getSource()).getLevel());
        for (ServerSubLevel subLevel : subLevels) {
            if (toggle) {
                handler.toggleLock(subLevel.getUniqueId());
                ++updated;
                continue;
            }
            boolean isLocked = handler.isLocked((SubLevel)subLevel);
            boolean shouldLock = BoolArgumentType.getBool(ctx, (String)"locked");
            if (shouldLock == isLocked) continue;
            handler.toggleLock(subLevel.getUniqueId());
            ++updated;
        }
        MutableComponent message = Component.translatable((String)"commands.simulated.lock.success", (Object[])new Object[]{updated, updated == 1 ? "" : "s"});
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> SimCommand.lambda$lockSubLevels$5((Component)message), true);
        return updated;
    }

    private static /* synthetic */ Component lambda$lockSubLevels$5(Component message) {
        return message;
    }
}
