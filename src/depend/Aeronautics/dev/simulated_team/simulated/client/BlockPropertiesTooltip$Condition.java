/*
 * Decompiled with CFR 0.152.
 */
package dev.simulated_team.simulated.client;

public static enum BlockPropertiesTooltip.Condition {
    ALWAYS(true, false, false),
    SHIFT(true, true, false),
    GOGGLES(true, false, true),
    SHIFT_GOGGLES(true, true, true),
    NEVER(false, false, false);

    private final boolean allow;
    private final boolean requireShift;
    private final boolean requireGoggles;

    private BlockPropertiesTooltip.Condition(boolean allow, boolean requireShift, boolean requireGoggles) {
        this.allow = allow;
        this.requireShift = requireShift;
        this.requireGoggles = requireGoggles;
    }

    public boolean test(boolean shift, boolean goggles) {
        return !(!this.allow || this.requireShift && !shift || this.requireGoggles && !goggles);
    }

    public boolean allows() {
        return this.allow;
    }
}
