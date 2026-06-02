/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.AdvancementType
 */
package com.simibubi.create.foundation.advancement;

import net.minecraft.advancements.AdvancementType;

static enum CreateAdvancement.TaskType {
    SILENT(AdvancementType.TASK, false, false, false),
    NORMAL(AdvancementType.TASK, true, false, false),
    NOISY(AdvancementType.TASK, true, true, false),
    EXPERT(AdvancementType.GOAL, true, true, false),
    SECRET(AdvancementType.GOAL, true, true, true);

    private final AdvancementType advancementType;
    private final boolean toast;
    private final boolean announce;
    private final boolean hide;

    private CreateAdvancement.TaskType(AdvancementType advancementType, boolean toast, boolean announce, boolean hide) {
        this.advancementType = advancementType;
        this.toast = toast;
        this.announce = announce;
        this.hide = hide;
    }
}
