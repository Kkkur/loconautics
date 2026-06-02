/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.coordinates.Vec3Argument
 *  net.minecraft.core.Position
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.ryanhcode.sable.api.command.SableCommandHelper;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.constraint.rotary.RotaryConstraintConfiguration;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import java.util.Collection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;

public class SableJointCommands {
    public static final SimpleCommandExceptionType MISSING_JOINT_SUBLEVEL_TARGET = new SimpleCommandExceptionType((Message)Component.translatable((String)"commands.sable.joint.missing_sublevel_target"));

    public static void register(LiteralArgumentBuilder<CommandSourceStack> sableBuilder, CommandBuildContext buildContext) {
        sableBuilder.then(Commands.literal((String)"joint").then(Commands.literal((String)"add").then(Commands.argument((String)"subLevel1", (ArgumentType)SubLevelArgumentType.subLevels()).then(Commands.argument((String)"subLevel2", (ArgumentType)SubLevelArgumentType.subLevels()).then(Commands.literal((String)"rotary").then(Commands.argument((String)"pos1", (ArgumentType)Vec3Argument.vec3((boolean)false)).then(Commands.argument((String)"pos2", (ArgumentType)Vec3Argument.vec3((boolean)false)).then(Commands.argument((String)"axis1", (ArgumentType)Vec3Argument.vec3((boolean)false)).then(Commands.argument((String)"axis2", (ArgumentType)Vec3Argument.vec3((boolean)false)).executes(SableJointCommands::executeAddJointCommand))))))))));
    }

    private static int executeAddJointCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerSubLevelContainer container = SableCommandHelper.requireSubLevelContainer(ctx);
        PhysicsPipeline pipeline = SableCommandHelper.requireSubLevelPhysicsSystem(container).getPipeline();
        SableJointCommands.addRotaryJoint(pipeline, SubLevelArgumentType.getSubLevels(ctx, "subLevel1"), SubLevelArgumentType.getSubLevels(ctx, "subLevel2"), Vec3Argument.getVec3(ctx, (String)"pos1"), Vec3Argument.getVec3(ctx, (String)"pos2"), Vec3Argument.getVec3(ctx, (String)"axis1"), Vec3Argument.getVec3(ctx, (String)"axis2"));
        ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable((String)"commands.sable.joint.success"), true);
        return 0;
    }

    private static void addRotaryJoint(PhysicsPipeline pipeline, Collection<ServerSubLevel> subLevel1, Collection<ServerSubLevel> subLevel2, Vec3 pos1, Vec3 pos2, Vec3 axis1, Vec3 axis2) throws CommandSyntaxException {
        RotaryConstraintConfiguration constraintConfig = new RotaryConstraintConfiguration((Vector3dc)JOMLConversion.toJOML((Position)pos1), (Vector3dc)JOMLConversion.toJOML((Position)pos2), (Vector3dc)JOMLConversion.toJOML((Position)axis1), (Vector3dc)JOMLConversion.toJOML((Position)axis2));
        ServerSubLevel jointSubLevel1 = subLevel1.stream().findFirst().orElseThrow(() -> ((SimpleCommandExceptionType)MISSING_JOINT_SUBLEVEL_TARGET).create());
        ServerSubLevel jointSubLevel2 = subLevel2.stream().findFirst().orElseThrow(() -> ((SimpleCommandExceptionType)MISSING_JOINT_SUBLEVEL_TARGET).create());
        pipeline.addConstraint(jointSubLevel1, jointSubLevel2, constraintConfig);
    }
}
