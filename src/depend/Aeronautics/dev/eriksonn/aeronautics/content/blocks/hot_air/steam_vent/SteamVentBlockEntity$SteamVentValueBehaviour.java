/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  net.minecraft.network.chat.Component
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner.HotAirBurnerValueBehaviour;
import dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent.SteamVentBlockEntity;
import net.minecraft.network.chat.Component;

public static class SteamVentBlockEntity.SteamVentValueBehaviour
extends HotAirBurnerValueBehaviour {
    public SteamVentBlockEntity.SteamVentValueBehaviour(Component label, SmartBlockEntity be, SteamVentBlockEntity.SteamVentValueBoxTransform slot) {
        super(label, be, (ValueBoxTransform)slot);
        slot.be = be;
    }
}
