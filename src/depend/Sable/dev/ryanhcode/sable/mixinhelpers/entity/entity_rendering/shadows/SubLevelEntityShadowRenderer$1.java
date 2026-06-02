/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction$AxisDirection
 */
package dev.ryanhcode.sable.mixinhelpers.entity.entity_rendering.shadows;

import net.minecraft.core.Direction;

static class SubLevelEntityShadowRenderer.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction$AxisDirection;

    static {
        $SwitchMap$net$minecraft$core$Direction$AxisDirection = new int[Direction.AxisDirection.values().length];
        try {
            SubLevelEntityShadowRenderer.1.$SwitchMap$net$minecraft$core$Direction$AxisDirection[Direction.AxisDirection.POSITIVE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SubLevelEntityShadowRenderer.1.$SwitchMap$net$minecraft$core$Direction$AxisDirection[Direction.AxisDirection.NEGATIVE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
