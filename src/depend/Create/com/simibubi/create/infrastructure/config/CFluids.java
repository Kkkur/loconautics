/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigBool
 *  net.createmod.catnip.config.ConfigBase$ConfigEnum
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 */
package com.simibubi.create.infrastructure.config;

import com.simibubi.create.content.fluids.transfer.FluidManipulationBehaviour;
import net.createmod.catnip.config.ConfigBase;

public class CFluids
extends ConfigBase {
    public final ConfigBase.ConfigInt fluidTankCapacity = this.i(8, 1, "fluidTankCapacity", new String[]{Comments.buckets, Comments.fluidTankCapacity});
    public final ConfigBase.ConfigInt fluidTankMaxHeight = this.i(32, 1, "fluidTankMaxHeight", new String[]{Comments.blocks, Comments.fluidTankMaxHeight});
    public final ConfigBase.ConfigInt mechanicalPumpRange = this.i(16, 1, "mechanicalPumpRange", new String[]{Comments.blocks, Comments.mechanicalPumpRange});
    public final ConfigBase.ConfigInt hosePulleyRange = this.i(128, 1, "hosePulleyRange", new String[]{Comments.blocks, Comments.hosePulleyRange});
    public final ConfigBase.ConfigInt hosePulleyBlockThreshold = this.i(10000, -1, "hosePulleyBlockThreshold", new String[]{Comments.blocks, Comments.toDisable, Comments.hosePulleyBlockThreshold});
    public final ConfigBase.ConfigBool fillInfinite = this.b(false, "fillInfinite", new String[]{Comments.fillInfinite});
    public final ConfigBase.ConfigEnum<FluidManipulationBehaviour.BottomlessFluidMode> bottomlessFluidMode = this.e(FluidManipulationBehaviour.BottomlessFluidMode.ALLOW_BY_TAG, "bottomlessFluidMode", new String[]{Comments.bottomlessFluidMode});
    public ConfigBase.ConfigBool fluidFillPlaceFluidSourceBlocks = this.b(true, "fluidFillPlaceFluidSourceBlocks", new String[]{Comments.fluidFillPlaceFluidSourceBlocks});
    public ConfigBase.ConfigBool pipesPlaceFluidSourceBlocks = this.b(true, "pipesPlaceFluidSourceBlocks", new String[]{Comments.pipesPlaceFluidSourceBlocks});

    public String getName() {
        return "fluids";
    }

    private static class Comments {
        static String blocks = "[in Blocks]";
        static String buckets = "[in Buckets]";
        static String toDisable = "[-1 to disable this behaviour]";
        static String fluidTankCapacity = "The amount of liquid a tank can hold per block.";
        static String fluidTankMaxHeight = "The maximum height a fluid tank can reach.";
        static String mechanicalPumpRange = "The maximum distance a mechanical pump can push or pull liquids on either side.";
        static String hosePulleyRange = "The maximum distance a hose pulley can draw fluid blocks from.";
        static String hosePulleyBlockThreshold = "The minimum amount of fluid blocks the hose pulley needs to find before deeming it an infinite source.";
        static String fillInfinite = "Whether hose pulleys should continue filling up above-threshold sources.";
        static String bottomlessFluidMode = "Configure which fluids can be drained infinitely.";
        static String fluidFillPlaceFluidSourceBlocks = "Whether hose pulleys should be allowed to place fluid sources.";
        static String pipesPlaceFluidSourceBlocks = "Whether open-ended pipes should be allowed to place fluid sources.";

        private Comments() {
        }
    }
}
