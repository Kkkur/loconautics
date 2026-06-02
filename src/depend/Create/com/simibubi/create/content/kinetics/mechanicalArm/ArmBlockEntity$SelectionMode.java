/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;

public static enum ArmBlockEntity.SelectionMode implements INamedIconOptions
{
    ROUND_ROBIN(AllIcons.I_ARM_ROUND_ROBIN),
    FORCED_ROUND_ROBIN(AllIcons.I_ARM_FORCED_ROUND_ROBIN),
    PREFER_FIRST(AllIcons.I_ARM_PREFER_FIRST);

    private final String translationKey;
    private final AllIcons icon;

    private ArmBlockEntity.SelectionMode(AllIcons icon) {
        this.icon = icon;
        this.translationKey = "create.mechanical_arm.selection_mode." + Lang.asId((String)this.name());
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
