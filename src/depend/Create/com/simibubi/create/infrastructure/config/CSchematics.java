/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigBool
 *  net.createmod.catnip.config.ConfigBase$ConfigGroup
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 */
package com.simibubi.create.infrastructure.config;

import net.createmod.catnip.config.ConfigBase;

public class CSchematics
extends ConfigBase {
    public final ConfigBase.ConfigBool creativePrintIncludesAir = this.b(false, "creativePrintIncludesAir", new String[]{Comments.creativePrintIncludesAir});
    public final ConfigBase.ConfigInt maxSchematics = this.i(10, 1, "maxSchematics", new String[]{Comments.maxSchematics});
    public final ConfigBase.ConfigInt maxTotalSchematicSize = this.i(256, 16, "maxTotalSchematicSize", new String[]{Comments.kb, Comments.maxSize});
    public final ConfigBase.ConfigInt maxSchematicPacketSize = this.i(1024, 256, Short.MAX_VALUE, "maxSchematicPacketSize", new String[]{Comments.b, Comments.maxPacketSize});
    public final ConfigBase.ConfigInt schematicIdleTimeout = this.i(600, 100, "schematicIdleTimeout", new String[]{Comments.idleTimeout});
    public final ConfigBase.ConfigGroup schematicannon = this.group(0, "schematicannon", new String[]{"Schematicannon"});
    public final ConfigBase.ConfigInt schematicannonDelay = this.i(10, 1, "schematicannonDelay", new String[]{Comments.delay});
    public final ConfigBase.ConfigInt schematicannonShotsPerGunpowder = this.i(400, 1, "schematicannonShotsPerGunpowder", new String[]{Comments.schematicannonShotsPerGunpowder});

    public String getName() {
        return "schematics";
    }

    private static class Comments {
        static String kb = "[in KiloBytes]";
        static String b = "[in Bytes]";
        static String maxSchematics = "The amount of Schematics a player can upload until previous ones are overwritten.";
        static String maxSize = "The maximum allowed file size of uploaded Schematics.";
        static String maxPacketSize = "The maximum packet size uploaded Schematics are split into.";
        static String idleTimeout = "Amount of game ticks without new packets arriving until an active schematic upload process is discarded.";
        static String delay = "Amount of game ticks between shots of the cannon. Higher => Slower";
        static String schematicannonShotsPerGunpowder = "Amount of blocks a Schematicannon can print per Gunpowder item provided.";
        static String creativePrintIncludesAir = "Whether placing a Schematic directly in Creative Mode should replace world blocks with Air";

        private Comments() {
        }
    }
}
