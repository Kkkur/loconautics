/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.AdvancementType
 */
package dev.simulated_team.simulated.data.advancements;

import net.minecraft.advancements.AdvancementType;

public static enum SimulatedAdvancement.TaskType {
    SILENT(AdvancementType.TASK, false, false, false),
    NORMAL(AdvancementType.TASK, true, false, false),
    NOISY(AdvancementType.TASK, true, true, false),
    EXPERT(AdvancementType.GOAL, true, true, false),
    SECRET(AdvancementType.GOAL, true, true, true);

    private final AdvancementType advancementType;
    private final boolean toast;
    private final boolean announce;
    private final boolean hide;

    private SimulatedAdvancement.TaskType(AdvancementType advancementType, boolean toast, boolean announce, boolean hide) {
        this.advancementType = advancementType;
        this.toast = toast;
        this.announce = announce;
        this.hide = hide;
    }
}
