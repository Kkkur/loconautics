/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.level.Level
 */
package dev.eriksonn.aeronautics.events;

import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.BalloonMap;
import dev.eriksonn.aeronautics.content.blocks.levitite.LevititeShaderManager;
import dev.eriksonn.aeronautics.util.AeroSoundDistUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

public class AeronauticsClientEvents {
    public static void clientLevelTick(boolean post) {
        if (post) {
            ClientLevel level = Minecraft.getInstance().level;
            AeroSoundDistUtil.tickGlobalBurnerSound();
            LevititeShaderManager.tick();
            if (level != null) {
                BalloonMap.tick((Level)level);
            }
        }
    }
}
