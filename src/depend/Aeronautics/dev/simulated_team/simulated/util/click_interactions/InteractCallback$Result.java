/*
 * Decompiled with CFR 0.152.
 */
package dev.simulated_team.simulated.util.click_interactions;

public record InteractCallback.Result(boolean cancelled) {
    private static final InteractCallback.Result EMPTY = new InteractCallback.Result(false);

    public static InteractCallback.Result empty() {
        return EMPTY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        InteractCallback.Result otherEvent = (InteractCallback.Result)obj;
        return otherEvent.cancelled == this.cancelled;
    }
}
