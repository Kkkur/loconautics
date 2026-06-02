/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigBool
 */
package com.simibubi.create.infrastructure.config;

import net.createmod.catnip.config.ConfigBase;

public class CWorldGen
extends ConfigBase {
    public final ConfigBase.ConfigBool disable = this.b(false, "disableWorldGen", new String[]{Comments.disable});

    public String getName() {
        return "worldgen";
    }

    private static class Comments {
        static String disable = "Prevents all worldgen added by Create from taking effect";

        private Comments() {
        }
    }
}
