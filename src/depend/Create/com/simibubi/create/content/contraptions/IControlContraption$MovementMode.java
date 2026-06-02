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

public static enum IControlContraption.MovementMode implements INamedIconOptions
{
    MOVE_PLACE(AllIcons.I_MOVE_PLACE),
    MOVE_PLACE_RETURNED(AllIcons.I_MOVE_PLACE_RETURNED),
    MOVE_NEVER_PLACE(AllIcons.I_MOVE_NEVER_PLACE);

    private String translationKey;
    private AllIcons icon;

    private IControlContraption.MovementMode(AllIcons icon) {
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
