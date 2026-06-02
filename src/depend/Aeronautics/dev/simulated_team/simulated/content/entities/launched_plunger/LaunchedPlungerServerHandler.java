/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 */
package dev.simulated_team.simulated.content.entities.launched_plunger;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.content.entities.launched_plunger.LaunchedPlungerEntity;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class LaunchedPlungerServerHandler {
    private static final WorldAttached<Collection<LaunchedPlungerEntity>> LEVEL_PLUNGERS = new WorldAttached(x -> new ObjectOpenHashSet());

    public static void addLaunchedPlunger(Level level, LaunchedPlungerEntity toAdd) {
        ((Collection)LEVEL_PLUNGERS.get((LevelAccessor)level)).add(toAdd);
    }

    public static void removeLaunchedPlunger(Level level, LaunchedPlungerEntity toRemove) {
        Collection launchedPlungers = (Collection)LEVEL_PLUNGERS.get((LevelAccessor)level);
        launchedPlungers.remove((Object)toRemove);
    }

    public static void removePlayerPlungers(Player player) {
        ArrayList<LaunchedPlungerEntity> plungersForRemoval = new ArrayList<LaunchedPlungerEntity>();
        for (LaunchedPlungerEntity launchedPlungerEntity : (Collection)LEVEL_PLUNGERS.get((LevelAccessor)player.level())) {
            if (launchedPlungerEntity.getOwner() != player) continue;
            plungersForRemoval.add(launchedPlungerEntity);
        }
        for (LaunchedPlungerEntity launchedPlungerEntity : plungersForRemoval) {
            launchedPlungerEntity.discard();
            LaunchedPlungerEntity other = launchedPlungerEntity.getOther();
            if (other == null) continue;
            other.discard();
        }
    }

    public static void physicsTickAllPlungers(SubLevelPhysicsSystem physicsSystem, double timeStep) {
        ServerLevel level = physicsSystem.getLevel();
        Collection plungers = (Collection)LEVEL_PLUNGERS.get((LevelAccessor)level);
        for (LaunchedPlungerEntity launchedPlunger : plungers) {
            ServerSubLevel sublevel = (ServerSubLevel)Sable.HELPER.getContaining((Entity)launchedPlunger);
            if (sublevel == null) continue;
            launchedPlunger.physicsTick(sublevel, physicsSystem.getPhysicsHandle(sublevel), timeStep);
        }
    }
}
