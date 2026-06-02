/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.compat.trainmap;

import com.simibubi.create.compat.trainmap.TrainMapSync;
import com.simibubi.create.infrastructure.config.CClient;

static class TrainMapManager.1 {
    static final /* synthetic */ int[] $SwitchMap$com$simibubi$create$compat$trainmap$TrainMapSync$TrainState;
    static final /* synthetic */ int[] $SwitchMap$com$simibubi$create$infrastructure$config$CClient$TrainMapTheme;

    static {
        $SwitchMap$com$simibubi$create$infrastructure$config$CClient$TrainMapTheme = new int[CClient.TrainMapTheme.values().length];
        try {
            TrainMapManager.1.$SwitchMap$com$simibubi$create$infrastructure$config$CClient$TrainMapTheme[CClient.TrainMapTheme.GREY.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrainMapManager.1.$SwitchMap$com$simibubi$create$infrastructure$config$CClient$TrainMapTheme[CClient.TrainMapTheme.WHITE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$com$simibubi$create$compat$trainmap$TrainMapSync$TrainState = new int[TrainMapSync.TrainState.values().length];
        try {
            TrainMapManager.1.$SwitchMap$com$simibubi$create$compat$trainmap$TrainMapSync$TrainState[TrainMapSync.TrainState.CONDUCTOR_MISSING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrainMapManager.1.$SwitchMap$com$simibubi$create$compat$trainmap$TrainMapSync$TrainState[TrainMapSync.TrainState.DERAILED.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrainMapManager.1.$SwitchMap$com$simibubi$create$compat$trainmap$TrainMapSync$TrainState[TrainMapSync.TrainState.NAVIGATION_FAILED.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrainMapManager.1.$SwitchMap$com$simibubi$create$compat$trainmap$TrainMapSync$TrainState[TrainMapSync.TrainState.SCHEDULE_INTERRUPTED.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrainMapManager.1.$SwitchMap$com$simibubi$create$compat$trainmap$TrainMapSync$TrainState[TrainMapSync.TrainState.RUNNING_MANUALLY.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrainMapManager.1.$SwitchMap$com$simibubi$create$compat$trainmap$TrainMapSync$TrainState[TrainMapSync.TrainState.RUNNING.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
