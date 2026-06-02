/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.offroad.handlers.server;

import dev.ryanhcode.offroad.handlers.MultiminingDataTickResult;

static class MultiMiningServerManager.1 {
    static final /* synthetic */ int[] $SwitchMap$dev$ryanhcode$offroad$handlers$MultiminingDataTickResult;

    static {
        $SwitchMap$dev$ryanhcode$offroad$handlers$MultiminingDataTickResult = new int[MultiminingDataTickResult.values().length];
        try {
            MultiMiningServerManager.1.$SwitchMap$dev$ryanhcode$offroad$handlers$MultiminingDataTickResult[MultiminingDataTickResult.BROKEN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MultiMiningServerManager.1.$SwitchMap$dev$ryanhcode$offroad$handlers$MultiminingDataTickResult[MultiminingDataTickResult.STOP.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MultiMiningServerManager.1.$SwitchMap$dev$ryanhcode$offroad$handlers$MultiminingDataTickResult[MultiminingDataTickResult.CONTINUE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
