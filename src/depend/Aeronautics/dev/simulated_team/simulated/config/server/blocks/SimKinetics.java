/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 */
package dev.simulated_team.simulated.config.server.blocks;

import dev.simulated_team.simulated.config.server.blocks.SimStress;
import net.createmod.catnip.config.ConfigBase;

public class SimKinetics
extends ConfigBase {
    public final SimStress stressValues = (SimStress)this.nested(1, SimStress::new, new String[]{Comments.stress});

    public String getName() {
        return "kinetics";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";

        private Comments() {
        }
    }
}
