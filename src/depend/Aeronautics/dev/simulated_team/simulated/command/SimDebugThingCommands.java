/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  dev.ryanhcode.sable.api.physics.force.ForceTotal
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.server.level.ServerLevel
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ryanhcode.sable.api.physics.force.ForceTotal;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.util.SimDebugThing;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SimDebugThingCommands {
    public static int start(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int steps = (Integer)context.getArgument("steps", Integer.class);
        SimDebugThing.start(steps, ((CommandSourceStack)context.getSource()).getLevel());
        return 1;
    }

    public static int stop(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SimDebugThing.stop();
        return 1;
    }

    public static int abort(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SimDebugThing.abort();
        return 1;
    }

    public static int stopSublevels(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        SubLevelContainerHolder holder;
        SubLevelContainer plotContainer;
        ServerLevel serverLevel = ((CommandSourceStack)context.getSource()).getLevel();
        if (serverLevel instanceof SubLevelContainerHolder && (plotContainer = (holder = (SubLevelContainerHolder)serverLevel).sable$getPlotContainer()) instanceof ServerSubLevelContainer) {
            ServerSubLevelContainer serverContainer = (ServerSubLevelContainer)plotContainer;
            SubLevelPhysicsSystem physicsSystem = serverContainer.physicsSystem();
            Vector3d angularVelocity = new Vector3d();
            Vector3d linearVelocity = new Vector3d();
            for (SubLevel sublevel : holder.sable$getPlotContainer().getAllSubLevels()) {
                if (!(sublevel instanceof ServerSubLevel)) continue;
                ServerSubLevel serverSubLevel = (ServerSubLevel)sublevel;
                RigidBodyHandle rigidBodyHandle = physicsSystem.getPhysicsHandle(serverSubLevel);
                rigidBodyHandle.getAngularVelocity(angularVelocity).negate();
                rigidBodyHandle.getLinearVelocity(linearVelocity).negate();
                serverSubLevel.logicalPose().orientation().transformInverse(angularVelocity);
                serverSubLevel.logicalPose().orientation().transformInverse(linearVelocity);
                ForceTotal forceTotal = new ForceTotal();
                forceTotal.applyLinearImpulse((Vector3dc)linearVelocity.mul(serverSubLevel.getMassTracker().getMass()));
                forceTotal.applyTorqueImpulse((Vector3dc)serverSubLevel.getMassTracker().getInertiaTensor().transform(angularVelocity));
                rigidBodyHandle.applyForcesAndReset(forceTotal);
            }
        }
        return 1;
    }
}
