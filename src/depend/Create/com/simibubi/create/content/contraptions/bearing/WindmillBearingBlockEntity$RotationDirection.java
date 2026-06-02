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

public static enum WindmillBearingBlockEntity.RotationDirection implements INamedIconOptions
{
    CLOCKWISE(AllIcons.I_REFRESH),
    COUNTER_CLOCKWISE(AllIcons.I_ROTATE_CCW);

    private String translationKey;
    private AllIcons icon;

    private WindmillBearingBlockEntity.RotationDirection(AllIcons icon) {
        this.icon = icon;
        this.translationKey = "create.generic." + Lang.asId((String)this.name());
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
