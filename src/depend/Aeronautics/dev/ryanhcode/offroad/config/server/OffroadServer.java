/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 */
package dev.ryanhcode.offroad.config.server;

import dev.ryanhcode.offroad.config.server.OffroadBlockConfigs;
import dev.ryanhcode.offroad.config.server.OffroadKinetics;
import net.createmod.catnip.config.ConfigBase;

public class OffroadServer
extends ConfigBase {
    public final OffroadBlockConfigs blocks = (OffroadBlockConfigs)this.nested(0, OffroadBlockConfigs::new, new String[]{Comments.blockConfig});
    public final OffroadKinetics kinetics = (OffroadKinetics)this.nested(0, OffroadKinetics::new, new String[]{Comments.kinetics});

    public String getName() {
        return "server";
    }

    private static class Comments {
        static String kinetics = "Parameters and abilities of Offroad's kinetic mechanisms";
        static String blockConfig = "Parameters and abilities of Offroad Blocks";

        private Comments() {
        }
    }
}
