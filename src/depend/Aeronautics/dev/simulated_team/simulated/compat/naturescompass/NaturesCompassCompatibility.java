/*
 * Decompiled with CFR 0.152.
 */
package dev.simulated_team.simulated.compat.naturescompass;

import dev.simulated_team.simulated.compat.naturescompass.NaturesCompassRegistry;
import dev.simulated_team.simulated.service.SimModCompatibilityService;

public class NaturesCompassCompatibility
implements SimModCompatibilityService {
    @Override
    public void init() {
        NaturesCompassRegistry.init();
    }

    @Override
    public String getModId() {
        return "naturescompass";
    }
}
