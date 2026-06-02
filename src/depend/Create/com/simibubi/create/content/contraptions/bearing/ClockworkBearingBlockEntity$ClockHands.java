/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 */
package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;

static enum ClockworkBearingBlockEntity.ClockHands implements INamedIconOptions
{
    HOUR_FIRST(AllIcons.I_HOUR_HAND_FIRST),
    MINUTE_FIRST(AllIcons.I_MINUTE_HAND_FIRST),
    HOUR_FIRST_24(AllIcons.I_HOUR_HAND_FIRST_24);

    private String translationKey;
    private AllIcons icon;

    private ClockworkBearingBlockEntity.ClockHands(AllIcons icon) {
        this.icon = icon;
        this.translationKey = "create.contraptions.clockwork." + Lang.asId((String)this.name());
    }

    @Override
    public AllIcons getIcon() {
        return this.icon;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }
}
