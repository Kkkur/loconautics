/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.server.commands.ExecuteCommand
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.phys.Vec2
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.command;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.ryanhcode.sable.api.command.SableCommandHelper;
import dev.ryanhcode.sable.api.command.SubLevelArgumentType;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import java.util.ArrayList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={ExecuteCommand.class})
public class ExecuteCommandMixin {
    @WrapOperation(method={"register"}, at={@At(value="INVOKE", target="Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;then(Lcom/mojang/brigadier/builder/ArgumentBuilder;)Lcom/mojang/brigadier/builder/ArgumentBuilder;", ordinal=31, remap=false)})
    private static ArgumentBuilder sable$then(LiteralArgumentBuilder instance, ArgumentBuilder argumentBuilder, Operation<ArgumentBuilder> original, @Local LiteralCommandNode<CommandSourceStack> literalCommandNode) {
        return instance.then(argumentBuilder).then(Commands.literal((String)"in_sub_level").then(Commands.argument((String)"sub_levels", (ArgumentType)SubLevelArgumentType.subLevels()).fork(literalCommandNode, commandContext -> {
            ArrayList list = Lists.newArrayList();
            ServerSubLevelContainer container = SubLevelContainer.getContainer(((CommandSourceStack)commandContext.getSource()).getLevel());
            for (SubLevel subLevel : SubLevelArgumentType.getSubLevels((CommandContext<CommandSourceStack>)commandContext, "sub_levels")) {
                Pose3d pose = subLevel.logicalPose();
                Vec3 localPos = pose.transformPositionInverse(((CommandSourceStack)commandContext.getSource()).getPosition());
                if (container.getPlot(new ChunkPos(BlockPos.containing((Position)localPos))) != subLevel.getPlot()) {
                    throw SableCommandHelper.ERROR_NOT_INSIDE_SUB_LEVEL.create();
                }
                list.add(((CommandSourceStack)commandContext.getSource()).withLevel((ServerLevel)subLevel.getLevel()).withPosition(localPos).withRotation(new Vec2(0.0f, 0.0f)));
            }
            return list;
        }))).then(Commands.literal((String)"out_sub_level").then(Commands.argument((String)"sub_levels", (ArgumentType)SubLevelArgumentType.subLevels()).fork(literalCommandNode, commandContext -> {
            ArrayList list = Lists.newArrayList();
            ServerSubLevelContainer container = SubLevelContainer.getContainer(((CommandSourceStack)commandContext.getSource()).getLevel());
            for (SubLevel subLevel : SubLevelArgumentType.getSubLevels((CommandContext<CommandSourceStack>)commandContext, "sub_levels")) {
                Pose3d pose = subLevel.logicalPose();
                Vec3 sourcePosition = ((CommandSourceStack)commandContext.getSource()).getPosition();
                Vec3 globalPos = pose.transformPosition(sourcePosition);
                if (container.getPlot(new ChunkPos(BlockPos.containing((Position)sourcePosition))) != subLevel.getPlot()) {
                    throw SableCommandHelper.ERROR_NOT_INSIDE_SUB_LEVEL.create();
                }
                list.add(((CommandSourceStack)commandContext.getSource()).withLevel((ServerLevel)subLevel.getLevel()).withPosition(globalPos).withRotation(new Vec2(0.0f, 0.0f)));
            }
            return list;
        }))).then(Commands.literal((String)"centered_in_sub_level").then(Commands.argument((String)"sub_levels", (ArgumentType)SubLevelArgumentType.subLevels()).fork(literalCommandNode, commandContext -> {
            ArrayList list = Lists.newArrayList();
            for (SubLevel subLevel : SubLevelArgumentType.getSubLevels((CommandContext<CommandSourceStack>)commandContext, "sub_levels")) {
                LevelPlot plot = subLevel.getPlot();
                Vec3 center = plot.getCenterBlock().getCenter();
                list.add(((CommandSourceStack)commandContext.getSource()).withLevel((ServerLevel)subLevel.getLevel()).withPosition(center).withRotation(new Vec2(0.0f, 0.0f)));
            }
            return list;
        })));
    }
}
