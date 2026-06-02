/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 */
package com.simibubi.create.content.contraptions.mounted;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;

public static enum CartAssemblerBlockEntity.CartMovementMode implements INamedIconOptions
{
    ROTATE(AllIcons.I_CART_ROTATE),
    ROTATE_PAUSED(AllIcons.I_CART_ROTATE_PAUSED),
    ROTATION_LOCKED(AllIcons.I_CART_ROTATE_LOCKED);

    private String translationKey;
    private AllIcons icon;

    private CartAssemblerBlockEntity.CartMovementMode(AllIcons icon) {
        this.icon = icon;
        this.translationKey = "create.contraptions.cart_movement_mode." + Lang.asId((String)this.name());
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
