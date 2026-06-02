/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.content.trains.schedule.condition;

import com.simibubi.create.foundation.utility.CreateLang;
import java.util.Arrays;
import java.util.List;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.chat.Component;

public static enum CargoThresholdCondition.Ops {
    GREATER(">"),
    LESS("<"),
    EQUAL("=");

    public String formatted;

    private CargoThresholdCondition.Ops(String formatted) {
        this.formatted = formatted;
    }

    public boolean test(int current, int target) {
        return switch (this.ordinal()) {
            case 0 -> {
                if (current > target) {
                    yield true;
                }
                yield false;
            }
            case 2 -> {
                if (current == target) {
                    yield true;
                }
                yield false;
            }
            case 1 -> {
                if (current < target) {
                    yield true;
                }
                yield false;
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + String.valueOf((Object)this));
        };
    }

    public static List<? extends Component> translatedOptions() {
        return Arrays.stream(CargoThresholdCondition.Ops.values()).map(op -> CreateLang.translateDirect("schedule.condition.threshold." + Lang.asId((String)op.name()), new Object[0])).toList();
    }
}
