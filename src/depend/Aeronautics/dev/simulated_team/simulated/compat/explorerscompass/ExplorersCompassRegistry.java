/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.chaosthedude.explorerscompass.ExplorersCompass
 */
package dev.simulated_team.simulated.compat.explorerscompass;

import com.chaosthedude.explorerscompass.ExplorersCompass;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.compat.explorerscompass.ExplorersCompassNavigationTarget;

public class ExplorersCompassRegistry {
    public static void init() {
        Simulated.getRegistrate().navTarget("explorers_compass", ExplorersCompassNavigationTarget::new, () -> ExplorersCompass.explorersCompass);
    }
}
