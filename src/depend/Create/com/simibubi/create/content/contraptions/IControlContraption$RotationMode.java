/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;

public static enum IControlContraption.RotationMode implements INamedIconOptions
{
    ROTATE_PLACE(AllIcons.I_ROTATE_PLACE),
    ROTATE_PLACE_RETURNED(AllIcons.I_ROTATE_PLACE_RETURNED),
    ROTATE_NEVER_PLACE(AllIcons.I_ROTATE_NEVER_PLACE);

    private String translationKey;
    private AllIcons icon;

    private IControlContraption.RotationMode(AllIcons icon) {
        this.icon = icon;
        this.translationKey = "create.contraptions.movement_mode." + Lang.asId((String)this.name());
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
