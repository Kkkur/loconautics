/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 */
package dev.eriksonn.aeronautics.api;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

@FunctionalInterface
public static interface CustomSituationalMusic.Condition {
    public boolean test(ClientLevel var1, LocalPlayer var2);
}
