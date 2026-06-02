/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 */
package dev.ryanhcode.sable.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.physics.config.PhysicsConfigData;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SableConfigCommands {
    public static void register(LiteralArgumentBuilder<CommandSourceStack> sableBuilder, CommandBuildContext buildContext) {
        sableBuilder.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"config").then(Commands.literal((String)"min_island_size").then(Commands.argument((String)"size", (ArgumentType)IntegerArgumentType.integer((int)0, (int)Integer.MAX_VALUE)).executes(ctx -> {
            SubLevelPhysicsSystem physicsSystem = SubLevelContainer.getContainer(((CommandSourceStack)ctx.getSource()).getLevel()).physicsSystem();
            PhysicsConfigData config = physicsSystem.getConfig();
            config.minDynamicBodiesPerIsland = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"size");
            physicsSystem.onConfigUpdated();
            return 0;
        })))).then(Commands.literal((String)"contact_spring_natural_frequency").then(Commands.argument((String)"natural_frequency", (ArgumentType)FloatArgumentType.floatArg((float)0.0f)).executes(ctx -> {
            SubLevelPhysicsSystem physicsSystem = SubLevelContainer.getContainer(((CommandSourceStack)ctx.getSource()).getLevel()).physicsSystem();
            PhysicsConfigData config = physicsSystem.getConfig();
            config.contactSpringFrequency = FloatArgumentType.getFloat((CommandContext)ctx, (String)"natural_frequency");
            physicsSystem.onConfigUpdated();
            return 0;
        })))).then(Commands.literal((String)"contact_spring_damping_ratio").then(Commands.argument((String)"damping_ratio", (ArgumentType)FloatArgumentType.floatArg((float)0.0f)).executes(ctx -> {
            SubLevelPhysicsSystem physicsSystem = SubLevelContainer.getContainer(((CommandSourceStack)ctx.getSource()).getLevel()).physicsSystem();
            PhysicsConfigData config = physicsSystem.getConfig();
            config.contactSpringDampingRatio = FloatArgumentType.getFloat((CommandContext)ctx, (String)"damping_ratio");
            physicsSystem.onConfigUpdated();
            return 0;
        })))).then(Commands.literal((String)"solver_iterations").then(Commands.argument((String)"iterations", (ArgumentType)IntegerArgumentType.integer((int)0, (int)Integer.MAX_VALUE)).executes(ctx -> {
            SubLevelPhysicsSystem physicsSystem = SubLevelContainer.getContainer(((CommandSourceStack)ctx.getSource()).getLevel()).physicsSystem();
            PhysicsConfigData config = physicsSystem.getConfig();
            config.solverIterations = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"iterations");
            physicsSystem.onConfigUpdated();
            return 0;
        })))).then(Commands.literal((String)"stabilization_iterations").then(Commands.argument((String)"iterations", (ArgumentType)IntegerArgumentType.integer((int)0, (int)Integer.MAX_VALUE)).executes(ctx -> {
            SubLevelPhysicsSystem physicsSystem = SubLevelContainer.getContainer(((CommandSourceStack)ctx.getSource()).getLevel()).physicsSystem();
            PhysicsConfigData config = physicsSystem.getConfig();
            config.stabilizationIterations = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"iterations");
            physicsSystem.onConfigUpdated();
            return 0;
        })))).then(Commands.literal((String)"pgs_iterations").then(Commands.argument((String)"iterations", (ArgumentType)IntegerArgumentType.integer((int)0, (int)Integer.MAX_VALUE)).executes(ctx -> {
            SubLevelPhysicsSystem physicsSystem = SubLevelContainer.getContainer(((CommandSourceStack)ctx.getSource()).getLevel()).physicsSystem();
            PhysicsConfigData config = physicsSystem.getConfig();
            config.pgsIterations = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"iterations");
            physicsSystem.onConfigUpdated();
            return 0;
        })))).then(Commands.literal((String)"substeps").then(Commands.argument((String)"substeps", (ArgumentType)IntegerArgumentType.integer((int)0, (int)Integer.MAX_VALUE)).executes(ctx -> {
            SubLevelPhysicsSystem physicsSystem = SubLevelContainer.getContainer(((CommandSourceStack)ctx.getSource()).getLevel()).physicsSystem();
            PhysicsConfigData config = physicsSystem.getConfig();
            config.substepsPerTick = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"substeps");
            physicsSystem.onConfigUpdated();
            return 0;
        }))));
    }
}
