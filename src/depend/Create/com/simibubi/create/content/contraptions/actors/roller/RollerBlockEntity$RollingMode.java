/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 */
package com.simibubi.create.content.contraptions.actors.roller;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;

static enum RollerBlockEntity.RollingMode implements INamedIconOptions
{
    TUNNEL_PAVE(AllIcons.I_ROLLER_PAVE),
    STRAIGHT_FILL(AllIcons.I_ROLLER_FILL),
    WIDE_FILL(AllIcons.I_ROLLER_WIDE_FILL);

    private String translationKey;
    private AllIcons icon;

    private RollerBlockEntity.RollingMode(AllIcons icon) {
        this.icon = icon;
        this.translationKey = "create.contraptions.roller_mode." + Lang.asId((String)this.name());
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
