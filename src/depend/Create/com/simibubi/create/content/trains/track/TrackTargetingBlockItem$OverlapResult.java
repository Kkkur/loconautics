/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.trains.track;

public static enum TrackTargetingBlockItem.OverlapResult {
    VALID,
    OCCUPIED("track_target.occupied"),
    JUNCTION("track_target.no_junctions"),
    NO_TRACK("track_target.invalid");

    public String feedback;

    private TrackTargetingBlockItem.OverlapResult() {
    }

    private TrackTargetingBlockItem.OverlapResult(String feedback) {
        this.feedback = feedback;
    }
}
