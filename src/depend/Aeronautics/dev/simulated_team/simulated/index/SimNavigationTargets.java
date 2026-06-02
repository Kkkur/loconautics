/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 */
package dev.simulated_team.simulated.index;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.navigation_targets.CompassNavigationTarget;
import dev.simulated_team.simulated.content.navigation_targets.MagnetNavigationTarget;
import dev.simulated_team.simulated.content.navigation_targets.MapNavigationTarget;
import dev.simulated_team.simulated.content.navigation_targets.RecoveryCompassNavigationTarget;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import java.util.function.Supplier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class SimNavigationTargets {
    private static final SimulatedRegistrate REGISTRATE = Simulated.getRegistrate();
    public static final Supplier<CompassNavigationTarget> COMPASS = REGISTRATE.navTarget("compass", CompassNavigationTarget::new, (ItemLike)Items.COMPASS);
    public static final Supplier<RecoveryCompassNavigationTarget> RECOVERY_COMPASS = REGISTRATE.navTarget("recovery_compass", RecoveryCompassNavigationTarget::new, (ItemLike)Items.RECOVERY_COMPASS);
    public static final Supplier<MapNavigationTarget> MAP = REGISTRATE.navTarget("map", MapNavigationTarget::new, (ItemLike)Items.FILLED_MAP);
    public static final Supplier<MagnetNavigationTarget> MAGNET = REGISTRATE.navTarget("magnet", MagnetNavigationTarget::new, (ItemLike)SimBlocks.REDSTONE_MAGNET);

    public static void register() {
    }
}
