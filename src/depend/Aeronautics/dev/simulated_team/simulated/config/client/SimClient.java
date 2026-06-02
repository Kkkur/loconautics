/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 */
package dev.simulated_team.simulated.config.client;

import dev.simulated_team.simulated.config.client.items.SimItemConfigs;
import net.createmod.catnip.config.ConfigBase;

public class SimClient
extends ConfigBase {
    public final SimItemConfigs itemConfig = (SimItemConfigs)this.nested(0, SimItemConfigs::new, new String[]{Comments.itemConfig});

    public String getName() {
        return "client";
    }

    private static class Comments {
        static String itemConfig = "Settings of Simulated Items";

        private Comments() {
        }
    }
}
