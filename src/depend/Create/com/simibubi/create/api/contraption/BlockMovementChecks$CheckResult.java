/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.api.contraption;

public static enum BlockMovementChecks.CheckResult {
    SUCCESS,
    FAIL,
    PASS;


    public boolean toBoolean() {
        if (this == PASS) {
            throw new IllegalStateException("PASS does not have a boolean value");
        }
        return this == SUCCESS;
    }

    public static BlockMovementChecks.CheckResult of(boolean b) {
        return b ? SUCCESS : FAIL;
    }

    public static BlockMovementChecks.CheckResult of(Boolean b) {
        return b == null ? PASS : (b != false ? SUCCESS : FAIL);
    }
}
