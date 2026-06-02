/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.simulated_team.simulated.index.SimClickInteractions
 *  dev.simulated_team.simulated.util.click_interactions.InteractCallback
 */
package dev.eriksonn.aeronautics.index;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeCatalyzerHandler;
import dev.simulated_team.simulated.index.SimClickInteractions;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;

public class AeroClickInteractions
extends SimClickInteractions {
    public static LevititeCatalyzerHandler LEVITITE_CATALYZER_HANDLER = (LevititeCatalyzerHandler)AeroClickInteractions.register((InteractCallback)new LevititeCatalyzerHandler());

    public static void init() {
    }
}
