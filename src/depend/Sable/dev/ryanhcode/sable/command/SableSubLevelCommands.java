/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.coordinates.Coordinates
 *  net.minecraft.commands.arguments.coordinates.RotationArgument
 *  net.minecraft.commands.arguments.coordinates.Vec3Argument
 *  net.minecraft.core.Position
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.phys.Vec2
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector2i
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ryanhcode.sable.api.command.SableCommandHelper;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import java.util.Collection;
import java.util.Objects;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SableSubLevelCommands {
    public static void register(LiteralArgumentBuilder<CommandSourceStack> sableBuilder, CommandBuildContext buildContext) {
        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)sableBuilder.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"name").then(Commands.literal((String)"set").then(Commands.argument((String)"sub_level", (ArgumentType)SubLevelArgumentType.subLevels()).then(Commands.argument((String)"name", (ArgumentType)StringArgumentType.string()).executes(SableSubLevelCommands::executeSetSubLevelNameCommand))))).then(Commands.literal((String)"clear").then(Commands.argument((String)"sub_level", (ArgumentType)SubLevelArgumentType.subLevels()).executes(SableSubLevelCommands::executeClearSubLevelNameCommand)))).then(Commands.literal((String)"get").then(Commands.argument((String)"sub_level", (ArgumentType)SubLevelArgumentType.singleSubLevel()).executes(SableSubLevelCommands::executeGetSubLevelNameCommand))))).then(Commands.literal((String)"teleport").then(Commands.argument((String)"targets", (ArgumentType)SubLevelArgumentType.subLevels()).then(((RequiredArgumentBuilder)Commands.argument((String)"destination", (ArgumentType)Vec3Argument.vec3((boolean)false)).executes(ctx -> SableSubLevelCommands.executeTeleportSubLevelCommand((CommandContext<CommandSourceStack>)ctx, null))).then(Commands.argument((String)"angle", (ArgumentType)RotationArgument.rotation()).executes(ctx -> SableSubLevelCommands.executeTeleportSubLevelCommand((CommandContext<CommandSourceStack>)ctx, RotationArgument.getRotation((CommandContext)ctx, (String)"angle")))))))).then(Commands.literal((String)"remove").then(Commands.argument((String)"targets", (ArgumentType)SubLevelArgumentType.subLevels()).executes(SableSubLevelCommands::executeRemoveSubLevelCommand)));
    }

    private static int setSubLevelNames(Collection<ServerSubLevel> subLevels, @Nullable String name) {
        int modifiedCount = 0;
        for (SubLevel subLevel : subLevels) {
            if (Objects.equals(subLevel.getName(), name)) continue;
            subLevel.setName(name);
            ++modifiedCount;
        }
        return modifiedCount;
    }

    private static int executeSetSubLevelNameCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerSubLevel> subLevels = SubLevelArgumentType.getSubLevels(ctx, "sub_level");
        String name = StringArgumentType.getString(ctx, (String)"name");
        if (subLevels.isEmpty()) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
        }
        int modifiedCount = SableSubLevelCommands.setSubLevelNames(subLevels, name);
        if (modifiedCount == 0) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_MODIFIED.create();
        }
        if (modifiedCount == 1) {
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.set_name.success_singular", (Object[])new Object[]{name}), true);
        } else {
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.set_name.success_multiple", (Object[])new Object[]{modifiedCount, name}), true);
        }
        return modifiedCount;
    }

    private static int executeClearSubLevelNameCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerSubLevel> subLevels = SubLevelArgumentType.getSubLevels(ctx, "sub_level");
        if (subLevels.isEmpty()) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
        }
        int modifiedCount = SableSubLevelCommands.setSubLevelNames(subLevels, null);
        if (modifiedCount == 0) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_MODIFIED.create();
        }
        if (modifiedCount == 1) {
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.clear_name.success_singular"), true);
        } else {
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.clear_name.success_multiple", (Object[])new Object[]{modifiedCount}), true);
        }
        return modifiedCount;
    }

    private static int executeGetSubLevelNameCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerSubLevel subLevel = SubLevelArgumentType.getSingleSubLevel(ctx, "sub_level");
        if (subLevel.getName() == null) {
            throw SableCommandHelper.ERROR_SUB_LEVEL_UNNAMED.create();
        }
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.sub_level.get_name.success", (Object[])new Object[]{subLevel.getName()}), true);
        return 1;
    }

    private static int executeTeleportSubLevelCommand(CommandContext<CommandSourceStack> ctx, @Nullable Coordinates angle) throws CommandSyntaxException {
        Vec2 rotation;
        PhysicsPipeline pipeline = SableCommandHelper.requireSubLevelPhysicsPipeline(ctx);
        Collection<ServerSubLevel> targets = SubLevelArgumentType.getSubLevels(ctx, "targets");
        if (targets.isEmpty()) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
        }
        Vector3d destination = JOMLConversion.toJOML((Position)Vec3Argument.getVec3(ctx, (String)"destination"));
        Quaterniond orientation = new Quaterniond();
        Vec2 vec2 = rotation = angle != null ? angle.getRotation((CommandSourceStack)ctx.getSource()) : null;
        if (angle != null) {
            orientation.rotateY(-Math.toRadians(rotation.y));
            orientation.rotateX(Math.toRadians(rotation.x));
        }
        for (ServerSubLevel target : targets) {
            pipeline.resetVelocity(target);
            pipeline.teleport(target, (Vector3dc)destination, (Quaterniondc)(angle != null ? orientation : target.logicalPose().orientation()));
        }
        if (angle != null) {
            SableCommandHelper.sendSuccessDescribingSubLevels("commands.sable.sub_level.teleport_with_orientation.success", ctx, targets, destination.x, destination.y, destination.z, Float.valueOf(rotation.x), Float.valueOf(rotation.y));
        } else {
            SableCommandHelper.sendSuccessDescribingSubLevels("commands.sable.sub_level.teleport.success", ctx, targets, destination.x, destination.y, destination.z);
        }
        return 1;
    }

    private static int executeRemoveSubLevelCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerSubLevelContainer container = SableCommandHelper.requireSubLevelContainer(ctx);
        Collection<ServerSubLevel> targets = SubLevelArgumentType.getSubLevels(ctx, "targets");
        if (targets.isEmpty()) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
        }
        for (SubLevel subLevel : targets) {
            LevelPlot plot = subLevel.getPlot();
            Vector2i origin = container.getOrigin();
            ((SubLevelContainer)container).removeSubLevel(plot.plotPos.x - origin.x, plot.plotPos.z - origin.y, SubLevelRemovalReason.REMOVED);
        }
        SableCommandHelper.sendSuccessDescribingSubLevels("commands.sable.sub_level.remove.success", ctx, targets, new Object[0]);
        return 1;
    }
}
