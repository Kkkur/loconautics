/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigGroup
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 */
package com.simibubi.create.infrastructure.config;

import com.simibubi.create.infrastructure.config.CEquipment;
import com.simibubi.create.infrastructure.config.CFluids;
import com.simibubi.create.infrastructure.config.CKinetics;
import com.simibubi.create.infrastructure.config.CLogistics;
import com.simibubi.create.infrastructure.config.CRecipes;
import com.simibubi.create.infrastructure.config.CSchematics;
import com.simibubi.create.infrastructure.config.CTrains;
import net.createmod.catnip.config.ConfigBase;

public class CServer
extends ConfigBase {
    public final ConfigBase.ConfigGroup infrastructure = this.group(0, "infrastructure", new String[]{Comments.infrastructure});
    public final ConfigBase.ConfigInt tickrateSyncTimer = this.i(20, 5, "tickrateSyncTimer", new String[]{"[in Ticks]", Comments.tickrateSyncTimer, Comments.tickrateSyncTimer2});
    public final CRecipes recipes = (CRecipes)this.nested(0, CRecipes::new, new String[]{Comments.recipes});
    public final CKinetics kinetics = (CKinetics)this.nested(0, CKinetics::new, new String[]{Comments.kinetics});
    public final CFluids fluids = (CFluids)this.nested(0, CFluids::new, new String[]{Comments.fluids});
    public final CLogistics logistics = (CLogistics)this.nested(0, CLogistics::new, new String[]{Comments.logistics});
    public final CSchematics schematics = (CSchematics)this.nested(0, CSchematics::new, new String[]{Comments.schematics});
    public final CEquipment equipment = (CEquipment)this.nested(0, CEquipment::new, new String[]{Comments.equipment});
    public final CTrains trains = (CTrains)this.nested(0, CTrains::new, new String[]{Comments.trains});

    public String getName() {
        return "server";
    }

    private static class Comments {
        static String recipes = "Packmakers' control panel for internal recipe compat";
        static String schematics = "Everything related to Schematic tools";
        static String kinetics = "Parameters and abilities of Create's kinetic mechanisms";
        static String fluids = "Create's liquid manipulation tools";
        static String logistics = "Tweaks for logistical components";
        static String equipment = "Equipment and gadgets added by Create";
        static String trains = "Create's builtin Railway systems";
        static String infrastructure = "The Backbone of Create";
        static String tickrateSyncTimer = "The amount of time a server waits before sending out tickrate synchronization packets.";
        static String tickrateSyncTimer2 = "These packets help animations to be more accurate when tps is below 20.";

        private Comments() {
        }
    }
}
