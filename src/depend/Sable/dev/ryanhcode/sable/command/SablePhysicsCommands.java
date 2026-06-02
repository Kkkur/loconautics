/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.coordinates.RotationArgument
 *  net.minecraft.commands.arguments.coordinates.Vec3Argument
 *  net.minecraft.core.Position
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.phys.Vec2
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ryanhcode.sable.api.command.SableCommandHelper;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.command.Vec3ArgumentAbsolute;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SablePhysicsCommands {
    public static void register(LiteralArgumentBuilder<CommandSourceStack> sableBuilder, CommandBuildContext buildContext) {
        sableBuilder.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"physics").then(Commands.literal((String)"impulse").then(((RequiredArgumentBuilder)Commands.argument((String)"sub_level", (ArgumentType)SubLevelArgumentType.subLevels()).then(Commands.literal((String)"linear").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument((String)"impulse", (ArgumentType)Vec3ArgumentAbsolute.vec3()).executes(ctx -> SablePhysicsCommands.executeLinearImpulseCommand((CommandContext<CommandSourceStack>)ctx, true))).then(Commands.literal((String)"global").executes(ctx -> SablePhysicsCommands.executeLinearImpulseCommand((CommandContext<CommandSourceStack>)ctx, true)))).then(Commands.literal((String)"local").executes(ctx -> SablePhysicsCommands.executeLinearImpulseCommand((CommandContext<CommandSourceStack>)ctx, false)))))).then(Commands.literal((String)"angular").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument((String)"impulse", (ArgumentType)Vec3ArgumentAbsolute.vec3()).executes(ctx -> SablePhysicsCommands.executeAngularImpulseCommand((CommandContext<CommandSourceStack>)ctx, true))).then(Commands.literal((String)"global").executes(ctx -> SablePhysicsCommands.executeAngularImpulseCommand((CommandContext<CommandSourceStack>)ctx, true)))).then(Commands.literal((String)"local").executes(ctx -> SablePhysicsCommands.executeAngularImpulseCommand((CommandContext<CommandSourceStack>)ctx, false)))))))).then(Commands.literal((String)"rotation").then(((RequiredArgumentBuilder)Commands.argument((String)"sub_level", (ArgumentType)SubLevelArgumentType.subLevels()).then(SablePhysicsCommands.wrapRotationWithMode(true))).then(SablePhysicsCommands.wrapRotationWithMode(false))))).then(Commands.literal((String)"translation").then(((RequiredArgumentBuilder)Commands.argument((String)"sub_level", (ArgumentType)SubLevelArgumentType.subLevels()).then(Commands.literal((String)"add").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument((String)"translation", (ArgumentType)Vec3ArgumentAbsolute.vec3()).executes(ctx -> SablePhysicsCommands.executeAddTranslationCommand((CommandContext<CommandSourceStack>)ctx, true))).then(Commands.literal((String)"global").executes(ctx -> SablePhysicsCommands.executeAddTranslationCommand((CommandContext<CommandSourceStack>)ctx, true)))).then(Commands.literal((String)"local").executes(ctx -> SablePhysicsCommands.executeAddTranslationCommand((CommandContext<CommandSourceStack>)ctx, false)))))).then(Commands.literal((String)"set").then(Commands.argument((String)"translation", (ArgumentType)Vec3Argument.vec3((boolean)false)).executes(SablePhysicsCommands::executeSetTranslationCommand))))));
    }

    private static Component getGlobalComponent(boolean global) {
        return Component.translatable((String)("commands.sable.physics." + (global ? "global" : "local")));
    }

    private static int executeLinearImpulseCommand(CommandContext<CommandSourceStack> ctx, boolean global) throws CommandSyntaxException {
        SubLevelPhysicsSystem system = SableCommandHelper.requireSubLevelPhysicsSystem(ctx);
        Collection<ServerSubLevel> subLevels = SubLevelArgumentType.getSubLevels(ctx, "sub_level");
        Vec3 impulse = (Vec3)ctx.getArgument("impulse", Vec3.class);
        if (subLevels.isEmpty()) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
        }
        for (ServerSubLevel subLevel : subLevels) {
            Vec3 subLevelImpulse = impulse;
            if (global) {
                subLevelImpulse = subLevel.logicalPose().transformNormalInverse(subLevelImpulse);
            }
            system.getPhysicsHandle(subLevel).applyLinearImpulse((Vector3dc)JOMLConversion.toJOML((Position)subLevelImpulse));
        }
        SableCommandHelper.sendSuccessDescribingSubLevelsAtIndex("commands.sable.physics.impulse.linear.success", ctx, subLevels, 1, SablePhysicsCommands.getGlobalComponent(global), impulse.x + ", " + impulse.y + ", " + impulse.z);
        return 0;
    }

    private static int executeAngularImpulseCommand(CommandContext<CommandSourceStack> ctx, boolean global) throws CommandSyntaxException {
        SubLevelPhysicsSystem system = SableCommandHelper.requireSubLevelPhysicsSystem(ctx);
        Collection<ServerSubLevel> subLevels = SubLevelArgumentType.getSubLevels(ctx, "sub_level");
        Vec3 impulse = (Vec3)ctx.getArgument("impulse", Vec3.class);
        if (subLevels.isEmpty()) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
        }
        for (ServerSubLevel subLevel : subLevels) {
            Vec3 subLevelImpulse = impulse;
            if (global) {
                subLevelImpulse = subLevel.logicalPose().transformNormalInverse(subLevelImpulse);
            }
            system.getPhysicsHandle(subLevel).applyAngularImpulse((Vector3dc)JOMLConversion.toJOML((Position)subLevelImpulse));
        }
        SableCommandHelper.sendSuccessDescribingSubLevelsAtIndex("commands.sable.physics.impulse.angular.success", ctx, subLevels, 1, SablePhysicsCommands.getGlobalComponent(global), impulse.x + ", " + impulse.y + ", " + impulse.z);
        return 0;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> wrapRotationWithMode(boolean add) {
        return ((LiteralArgumentBuilder)Commands.literal((String)(add ? "add" : "set")).then(SablePhysicsCommands.wrapRotationWithReferenceFrame(add, false))).then(SablePhysicsCommands.wrapRotationWithReferenceFrame(add, true));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> wrapRotationWithReferenceFrame(boolean add, boolean axis) {
        Command c = ctx -> SablePhysicsCommands.executeRotationCommand((CommandContext<CommandSourceStack>)ctx, add, axis, true);
        Function<ArgumentBuilder, ArgumentBuilder> f = b -> {
            if (add) {
                b.then(SablePhysicsCommands.wrapRotationWithGlobality(axis, true)).then(SablePhysicsCommands.wrapRotationWithGlobality(axis, false));
            }
            return b;
        };
        ArgumentBuilder b2 = axis ? Commands.argument((String)"axis", (ArgumentType)Vec3ArgumentAbsolute.vec3()).then(f.apply(Commands.argument((String)"angle", (ArgumentType)DoubleArgumentType.doubleArg()).executes(c))) : f.apply(Commands.argument((String)"rotation", (ArgumentType)RotationArgument.rotation()).executes(c));
        return Commands.literal((String)(axis ? "axis" : "entity")).then(b2);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> wrapRotationWithGlobality(boolean axis, boolean global) {
        return Commands.literal((String)(global ? "global" : "local")).executes(ctx -> SablePhysicsCommands.executeRotationCommand((CommandContext<CommandSourceStack>)ctx, true, axis, global));
    }

    private static int executeRotationCommand(CommandContext<CommandSourceStack> ctx, boolean add, boolean axis, boolean global) throws CommandSyntaxException {
        Collection<ServerSubLevel> subLevels;
        PhysicsPipeline pipeline = SableCommandHelper.requireSubLevelPhysicsPipeline(ctx);
        Quaterniond orientation = new Quaterniond();
        Vec2 rotation2 = new Vec2(0.0f, 0.0f);
        Vec3 rotationAxis = new Vec3(0.0, 0.0, 0.0);
        double rotationAngle = 0.0;
        if (axis) {
            rotationAxis = (Vec3)ctx.getArgument("axis", Vec3.class);
            rotationAngle = (Double)ctx.getArgument("angle", Double.class);
            orientation.fromAxisAngleDeg(rotationAxis.x, rotationAxis.y, rotationAxis.z, rotationAngle);
            if (rotationAxis.lengthSqr() == 0.0) {
                throw SableCommandHelper.ERROR_NO_AXIS_FOR_ROTATION.create();
            }
        } else {
            rotation2 = RotationArgument.getRotation(ctx, (String)"rotation").getRotation((CommandSourceStack)ctx.getSource());
            orientation.rotateY(-Math.toRadians(rotation2.y));
            orientation.rotateX(Math.toRadians(rotation2.x));
        }
        if ((subLevels = SubLevelArgumentType.getSubLevels(ctx, "sub_level")).isEmpty()) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
        }
        for (ServerSubLevel subLevel : subLevels) {
            Pose3d pose = subLevel.logicalPose();
            if (add) {
                if (global) {
                    pose.orientation().premul((Quaterniondc)orientation);
                } else {
                    pose.orientation().mul((Quaterniondc)orientation);
                }
            } else {
                pose.orientation().set((Quaterniondc)orientation);
            }
            pipeline.teleport(subLevel, (Vector3dc)pose.position(), (Quaterniondc)pose.orientation());
        }
        if (axis) {
            SableCommandHelper.sendSuccessDescribingSubLevelsAtIndex(add ? "commands.sable.physics.rotation.add.success" : "commands.sable.physics.rotation.set.success", ctx, subLevels, 1, SablePhysicsCommands.getGlobalComponent(global), rotationAxis.x + ", " + rotationAxis.y + ", " + rotationAxis.z + ", " + rotationAngle);
        } else {
            SableCommandHelper.sendSuccessDescribingSubLevelsAtIndex(add ? "commands.sable.physics.rotation.add.success" : "commands.sable.physics.rotation.set.success", ctx, subLevels, 1, SablePhysicsCommands.getGlobalComponent(global), rotation2.x + ", " + rotation2.y);
        }
        return 0;
    }

    private static int executeAddTranslationCommand(CommandContext<CommandSourceStack> ctx, boolean global) throws CommandSyntaxException {
        PhysicsPipeline pipeline = SableCommandHelper.requireSubLevelPhysicsPipeline(ctx);
        Collection<ServerSubLevel> subLevels = SubLevelArgumentType.getSubLevels(ctx, "sub_level");
        if (subLevels.isEmpty()) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
        }
        Vec3 translation = (Vec3)ctx.getArgument("translation", Vec3.class);
        Vector3d sublevelTranslation = new Vector3d();
        for (ServerSubLevel subLevel : subLevels) {
            JOMLConversion.toJOML((Position)translation, (Vector3d)sublevelTranslation);
            if (!global) {
                subLevel.logicalPose().transformNormal(sublevelTranslation);
            }
            pipeline.teleport(subLevel, (Vector3dc)subLevel.logicalPose().position().add((Vector3dc)sublevelTranslation), (Quaterniondc)subLevel.logicalPose().orientation());
        }
        SableCommandHelper.sendSuccessDescribingSubLevelsAtIndex("commands.sable.physics.translation.add.success", ctx, subLevels, 1, SablePhysicsCommands.getGlobalComponent(global), translation.x + ", " + translation.y + ", " + translation.z);
        return 0;
    }

    private static int executeSetTranslationCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        PhysicsPipeline pipeline = SableCommandHelper.requireSubLevelPhysicsPipeline(ctx);
        Collection<ServerSubLevel> subLevels = SubLevelArgumentType.getSubLevels(ctx, "sub_level");
        if (subLevels.isEmpty()) {
            throw SableCommandHelper.ERROR_NO_SUB_LEVELS_FOUND.create();
        }
        Vector3d translation = JOMLConversion.toJOML((Position)Vec3Argument.getVec3(ctx, (String)"translation"));
        for (ServerSubLevel subLevel : subLevels) {
            pipeline.teleport(subLevel, (Vector3dc)translation, (Quaterniondc)subLevel.logicalPose().orientation());
        }
        SableCommandHelper.sendSuccessDescribingSubLevels("commands.sable.physics.translation.set.success", ctx, subLevels, translation.x + ", " + translation.y + ", " + translation.z);
        return 0;
    }
}
