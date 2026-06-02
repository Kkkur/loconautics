/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.navigation_targets;

import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MagnetNavigationTarget
implements NavigationTarget {
    @Override
    @Nullable
    public Vec3 getTarget(NavTableBlockEntity navBE, ItemStack self) {
        Vec3i normal = Direction.NORTH.getNormal().multiply(10);
        return navBE.getProjectedSelfPos().add((double)normal.getX(), (double)normal.getY(), (double)normal.getZ());
    }
}
