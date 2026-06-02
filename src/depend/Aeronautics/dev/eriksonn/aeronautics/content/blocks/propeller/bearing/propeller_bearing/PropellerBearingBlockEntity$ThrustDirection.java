/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions
 *  com.simibubi.create.foundation.gui.AllIcons
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;

public static enum PropellerBearingBlockEntity.ThrustDirection implements INamedIconOptions
{
    RIGHT_HANDED(AllIcons.I_REFRESH, "pull_when_clockwise"),
    LEFT_HANDED(AllIcons.I_ROTATE_CCW, "push_when_clockwise");

    private final String translationKey;
    private final AllIcons icon;

    private PropellerBearingBlockEntity.ThrustDirection(AllIcons icon, String name) {
        this.icon = icon;
        this.translationKey = "aeronautics.generic." + name;
    }

    public AllIcons getIcon() {
        return this.icon;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }
}
