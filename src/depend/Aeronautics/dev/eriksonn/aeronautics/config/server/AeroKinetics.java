/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 */
package dev.eriksonn.aeronautics.config.server;

import dev.eriksonn.aeronautics.config.server.AeroStress;
import net.createmod.catnip.config.ConfigBase;

public class AeroKinetics
extends ConfigBase {
    public final AeroStress stressValues = (AeroStress)this.nested(1, AeroStress::new, new String[]{Comments.stress});

    public String getName() {
        return "kinetics";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";

        private Comments() {
        }
    }
}
