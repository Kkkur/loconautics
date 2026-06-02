/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.chaosthedude.naturescompass.NaturesCompass
 */
package dev.simulated_team.simulated.compat.naturescompass;

import com.chaosthedude.naturescompass.NaturesCompass;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.compat.naturescompass.NaturesCompassNavigationTarget;

public class NaturesCompassRegistry {
    public static void init() {
        Simulated.getRegistrate().navTarget("natures_compass", NaturesCompassNavigationTarget::new, () -> NaturesCompass.naturesCompass);
    }
}
