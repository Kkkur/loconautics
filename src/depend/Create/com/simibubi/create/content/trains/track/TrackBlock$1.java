/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;

static class TrackBlock.1 {
    static final /* synthetic */ int[] $SwitchMap$com$simibubi$create$content$trains$track$TrackShape;
    static final /* synthetic */ int[] $SwitchMap$com$simibubi$create$content$trains$track$TrackTargetingBehaviour$RenderedTrackOverlayType;

    static {
        $SwitchMap$com$simibubi$create$content$trains$track$TrackTargetingBehaviour$RenderedTrackOverlayType = new int[TrackTargetingBehaviour.RenderedTrackOverlayType.values().length];
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackTargetingBehaviour$RenderedTrackOverlayType[TrackTargetingBehaviour.RenderedTrackOverlayType.DUAL_SIGNAL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackTargetingBehaviour$RenderedTrackOverlayType[TrackTargetingBehaviour.RenderedTrackOverlayType.OBSERVER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackTargetingBehaviour$RenderedTrackOverlayType[TrackTargetingBehaviour.RenderedTrackOverlayType.SIGNAL.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackTargetingBehaviour$RenderedTrackOverlayType[TrackTargetingBehaviour.RenderedTrackOverlayType.STATION.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$com$simibubi$create$content$trains$track$TrackShape = new int[TrackShape.values().length];
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.AE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.AW.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.AN.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.AS.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.CR_D.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.CR_NDX.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.CR_NDZ.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.CR_O.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.CR_PDX.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.CR_PDZ.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.ND.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.PD.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.XO.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.ZO.ordinal()] = 14;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.TE.ordinal()] = 15;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.TW.ordinal()] = 16;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.TS.ordinal()] = 17;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.TN.ordinal()] = 18;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackBlock.1.$SwitchMap$com$simibubi$create$content$trains$track$TrackShape[TrackShape.NONE.ordinal()] = 19;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
