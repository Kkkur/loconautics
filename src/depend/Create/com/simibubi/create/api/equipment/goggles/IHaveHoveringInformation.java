/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.api.equipment.goggles;

import com.simibubi.create.api.equipment.goggles.IHaveCustomOverlayIcon;
import java.util.List;
import net.minecraft.network.chat.Component;

public non-sealed interface IHaveHoveringInformation
extends IHaveCustomOverlayIcon {
    default public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return false;
    }
}
