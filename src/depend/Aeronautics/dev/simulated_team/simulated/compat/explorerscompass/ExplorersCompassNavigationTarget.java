/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.chaosthedude.explorerscompass.ExplorersCompass
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.compat.explorerscompass;

import com.chaosthedude.explorerscompass.ExplorersCompass;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ExplorersCompassNavigationTarget
implements NavigationTarget {
    @Override
    @Nullable
    public Vec3 getTarget(NavTableBlockEntity navBE, ItemStack self) {
        Integer x = (Integer)self.getComponents().get(ExplorersCompass.FOUND_X_COMPONENT);
        Integer z = (Integer)self.getComponents().get(ExplorersCompass.FOUND_Z_COMPONENT);
        if (x != null && z != null) {
            Vec3 pos = navBE.getProjectedSelfPos();
            return new Vec3((double)x.intValue(), pos.y(), (double)z.intValue());
        }
        return null;
    }

    @Override
    public float getMaxRange() {
        return 0.0f;
    }
}
