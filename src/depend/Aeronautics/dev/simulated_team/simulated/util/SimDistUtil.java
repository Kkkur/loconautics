/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.HitResult
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class SimDistUtil {
    @Nullable
    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static float getPartialTick() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
    }

    public static HitResult getHitResult() {
        return Minecraft.getInstance().hitResult;
    }
}
