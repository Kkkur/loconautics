/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelObserver
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.eriksonn.aeronautics.events;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeCrystallizerManager;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.BalloonMap;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class AeronauticsCommonEvents {
    public static void onServerTickEnd(ServerLevel level) {
        LevititeCrystallizerManager.tick((Level)level);
        BalloonMap.tick((Level)level);
    }

    public static void onBlockModifiedEvent(LevelAccessor level, BlockPos blockPos, BlockState oldState, BlockState newState) {
        ((BalloonMap)BalloonMap.MAP.get(level)).updateNearbyBalloons(blockPos, oldState, newState);
    }

    public static void onSubLevelContainerReady(Level level, SubLevelContainer subLevelContainer) {
        if (!(subLevelContainer instanceof ServerSubLevelContainer)) {
            return;
        }
        ServerSubLevelContainer serverContainer = (ServerSubLevelContainer)subLevelContainer;
        serverContainer.addObserver((SubLevelObserver)new BalloonMap.BalloonSubLevelObserver(level));
    }

    public static void onServerStopped(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            LevititeCrystallizerManager.clearLevel((LevelAccessor)level);
        }
    }

    public static void physicsTick(SubLevelPhysicsSystem physicsSystem, double timeStep) {
        ServerLevel level = physicsSystem.getLevel();
        BalloonMap.physicsTick(level, timeStep);
    }
}
