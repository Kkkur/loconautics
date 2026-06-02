/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.sounds.Music
 */
package dev.eriksonn.aeronautics.api;

import dev.eriksonn.aeronautics.index.client.AeroClientRegistries;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;

public record CustomSituationalMusic(Music music, Condition condition) {
    public static Music getSituationalMusic(ClientLevel level, LocalPlayer player) {
        for (Map.Entry entry : AeroClientRegistries.CUSTOM_SITUATIONAL_MUSIC.asVanillaRegistry().entrySet()) {
            CustomSituationalMusic value = (CustomSituationalMusic)entry.getValue();
            if (!value.condition().test(level, player)) continue;
            return value.music();
        }
        return null;
    }

    @FunctionalInterface
    public static interface Condition {
        public boolean test(ClientLevel var1, LocalPlayer var2);
    }
}
