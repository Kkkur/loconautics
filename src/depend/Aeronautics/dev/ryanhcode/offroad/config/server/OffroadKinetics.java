/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 */
package dev.ryanhcode.offroad.config.server;

import dev.ryanhcode.offroad.config.server.OffroadStress;
import net.createmod.catnip.config.ConfigBase;

public class OffroadKinetics
extends ConfigBase {
    public final OffroadStress stressValues = (OffroadStress)this.nested(1, OffroadStress::new, new String[]{Comments.stress});

    public String getName() {
        return "kinetics";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";

        private Comments() {
        }
    }
}
