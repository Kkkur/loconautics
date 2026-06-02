/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.physics.object.ArbitraryPhysicsObject
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.rope.strand.server;

import dev.ryanhcode.sable.api.physics.object.ArbitraryPhysicsObject;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerRopeStrand;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

public class ServerLevelRopeManager {
    private static final WorldAttached<ServerLevelRopeManager> worldAttached = new WorldAttached(ServerLevelRopeManager::create);
    private final Level level;
    private final Map<UUID, ServerRopeStrand> ropeStrands = new Object2ObjectOpenHashMap();

    public ServerLevelRopeManager(Level level) {
        this.level = level;
    }

    @Nullable
    public static ServerLevelRopeManager getOrCreate(Level level) {
        return (ServerLevelRopeManager)worldAttached.get((LevelAccessor)level);
    }

    private static ServerLevelRopeManager create(LevelAccessor level) {
        if (!(level instanceof ServerLevel)) {
            return null;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        return new ServerLevelRopeManager((Level)serverLevel);
    }

    public void addStrand(ServerRopeStrand strand) {
        this.ropeStrands.put(strand.getUUID(), strand);
    }

    @Nullable
    public ServerRopeStrand getStrand(UUID uuid) {
        return this.ropeStrands.get(uuid);
    }

    public void removeStrand(UUID uuid) {
        this.ropeStrands.remove(uuid);
    }

    public Collection<ServerRopeStrand> getAllStrands() {
        return this.ropeStrands.values();
    }

    public void physicsTick(SubLevelPhysicsSystem physicsSystem, double timeStep) {
        ServerLevel level = physicsSystem.getLevel();
        for (ServerRopeStrand strand : this.ropeStrands.values()) {
            if (!strand.isActive()) continue;
            if (!strand.isOwnerLoaded(level) || !strand.areAttachmentsLoaded(level)) {
                physicsSystem.removeObject((ArbitraryPhysicsObject)strand);
                continue;
            }
            strand.prePhysicsTick(physicsSystem, level, timeStep);
        }
    }
}
