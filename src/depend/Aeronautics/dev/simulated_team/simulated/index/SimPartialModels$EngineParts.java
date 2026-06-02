/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 */
package dev.simulated_team.simulated.index;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.index.SimPartialModels;

public static class SimPartialModels.EngineParts {
    public final PartialModel pipeLeft;
    public final PartialModel pipeRight;
    public final PartialModel outletLeft;
    public final PartialModel outletRight;
    public final PartialModel hatchBottom;
    public final PartialModel hatchTop;
    public final PartialModel mouth;

    public SimPartialModels.EngineParts(String prefix) {
        this.pipeLeft = SimPartialModels.block("portable_engine/" + prefix + "exhaust_pipe_left");
        this.pipeRight = SimPartialModels.block("portable_engine/" + prefix + "exhaust_pipe_right");
        this.outletLeft = SimPartialModels.block("portable_engine/" + prefix + "exhaust_outlet_left");
        this.outletRight = SimPartialModels.block("portable_engine/" + prefix + "exhaust_outlet_right");
        this.hatchBottom = SimPartialModels.block("portable_engine/" + prefix + "hatch_bottom");
        this.hatchTop = SimPartialModels.block("portable_engine/" + prefix + "hatch_top");
        this.mouth = SimPartialModels.block("portable_engine/" + prefix + "mouth");
    }
}
