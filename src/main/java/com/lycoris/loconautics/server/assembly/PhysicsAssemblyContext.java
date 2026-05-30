package com.lycoris.loconautics.server.assembly;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.lycoris.loconautics.core.PhysicsTrainTag.CarriageEntry;

import net.minecraft.core.BlockPos;

/**
 * Carries state across a physics assembly that spans two call sites inside
 * StationBlockEntity.assemble (the per-carriage redirect + the post-assembly orchestrator).
 */
public final class PhysicsAssemblyContext {

    private static boolean pending = false;
    private static final List<CarriageEntry> created = new ArrayList<>();

    private PhysicsAssemblyContext() {}

    public static void begin() { pending = true; created.clear(); }
    public static boolean isPending() { return pending; }

    public static void addSubLevel(UUID subLevelId, BlockPos anchor) {
        created.add(new CarriageEntry(subLevelId, anchor));
    }

    public static List<CarriageEntry> drain() { return new ArrayList<>(created); }

    public static void end() { pending = false; created.clear(); }
}