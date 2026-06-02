/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions
 *  com.simibubi.create.foundation.gui.AllIcons
 */
package dev.simulated_team.simulated.content.blocks.swivel_bearing;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;

public static enum SwivelBearingBlockEntity.LockingSetting implements INamedIconOptions
{
    LOCKED_ALWAYS(AllIcons.I_CONFIG_LOCKED, "swivel_default_always_locked"),
    LOCKED_DEFAULT(AllIcons.I_CONFIG_LOCKED, "swivel_default_locked"),
    UNLOCKED_DEFAULT(AllIcons.I_CONFIG_UNLOCKED, "swivel_default_unlocked"),
    UNLOCKED_ALWAYS(AllIcons.I_CONFIG_UNLOCKED, "swivel_default_always_unlocked");

    private final String translationKey;
    private final AllIcons icon;

    private SwivelBearingBlockEntity.LockingSetting(AllIcons icon, String name) {
        this.icon = icon;
        this.translationKey = "simulated.generic." + name;
    }

    public AllIcons getIcon() {
        return this.icon;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public boolean shouldLock(int signal) {
        if (this == UNLOCKED_ALWAYS) {
            return false;
        }
        if (this == LOCKED_ALWAYS) {
            return true;
        }
        return signal > 0 != (this == LOCKED_DEFAULT);
    }
}
