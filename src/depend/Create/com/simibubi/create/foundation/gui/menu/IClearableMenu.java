/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 */
package com.simibubi.create.foundation.gui.menu;

import com.simibubi.create.foundation.gui.menu.ClearMenuPacket;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IClearableMenu {
    default public void sendClearPacket() {
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)ClearMenuPacket.INSTANCE);
    }

    public void clearContents();
}
