/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.navigation_targets;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import dev.simulated_team.simulated.content.navigation_targets.lodestone_compass_compatability.LodestoneInformation;
import dev.simulated_team.simulated.content.navigation_targets.lodestone_compass_compatability.LodestoneTrackingMap;
import dev.simulated_team.simulated.index.SimDataComponents;
import java.util.UUID;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;

public class CompassNavigationTarget
implements NavigationTarget {
    @Override
    @Nullable
    public Vec3 getTarget(NavTableBlockEntity navBE, ItemStack self) {
        LodestoneInformation information;
        LodestoneTrackingMap map;
        Level level = navBE.getLevel();
        if (self.has(SimDataComponents.LODESTONE_COMPASS_SUBLEVEL_TRACKER) && (map = LodestoneTrackingMap.getOrLoad(level)) != null && (information = map.getInformation((UUID)self.get(SimDataComponents.LODESTONE_COMPASS_SUBLEVEL_TRACKER))) != null) {
            return JOMLConversion.toMojang((Vector3dc)information.projectedPos());
        }
        return level.getSharedSpawnPos().getCenter();
    }

    @Override
    public float getMaxRange() {
        return 0.0f;
    }
}
