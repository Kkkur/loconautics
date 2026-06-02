/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 */
package dev.eriksonn.aeronautics.config.server;

import dev.eriksonn.aeronautics.config.server.AeroBlockConfigs;
import dev.eriksonn.aeronautics.config.server.AeroKinetics;
import dev.eriksonn.aeronautics.config.server.AeroPhysics;
import net.createmod.catnip.config.ConfigBase;

public class AeroServer
extends ConfigBase {
    public final AeroPhysics physics = (AeroPhysics)this.nested(0, AeroPhysics::new, new String[]{Comments.physics});
    public final AeroBlockConfigs blocks = (AeroBlockConfigs)this.nested(0, AeroBlockConfigs::new, new String[]{Comments.blockConfig});
    public final AeroKinetics kinetics = (AeroKinetics)this.nested(0, AeroKinetics::new, new String[]{Comments.kinetics});

    public String getName() {
        return "server";
    }

    private static class Comments {
        static String kinetics = "Parameters and abilities of Aeronautics's kinetic mechanisms";
        static String physics = "Parameters related to the physics of Aeronautics blocks";
        static String blockConfig = "Parameters and abilities of Aeronautics Blocks";

        private Comments() {
        }
    }
}
