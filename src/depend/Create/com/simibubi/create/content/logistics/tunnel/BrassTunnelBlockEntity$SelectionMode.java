/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 */
package com.simibubi.create.content.logistics.tunnel;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;

public static enum BrassTunnelBlockEntity.SelectionMode implements INamedIconOptions
{
    SPLIT(AllIcons.I_TUNNEL_SPLIT),
    FORCED_SPLIT(AllIcons.I_TUNNEL_FORCED_SPLIT),
    ROUND_ROBIN(AllIcons.I_TUNNEL_ROUND_ROBIN),
    FORCED_ROUND_ROBIN(AllIcons.I_TUNNEL_FORCED_ROUND_ROBIN),
    PREFER_NEAREST(AllIcons.I_TUNNEL_PREFER_NEAREST),
    RANDOMIZE(AllIcons.I_TUNNEL_RANDOMIZE),
    SYNCHRONIZE(AllIcons.I_TUNNEL_SYNCHRONIZE);

    private final String translationKey;
    private final AllIcons icon;

    private BrassTunnelBlockEntity.SelectionMode(AllIcons icon) {
        this.icon = icon;
        this.translationKey = "create.tunnel.selection_mode." + Lang.asId((String)this.name());
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
