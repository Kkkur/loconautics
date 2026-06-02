/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.infrastructure.debugInfo;

import java.util.Objects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface InfoProvider {
    @Nullable
    public String getInfo(@Nullable Player var1);

    default public String getInfoSafe(Player player) {
        try {
            return Objects.toString(this.getInfo(player));
        }
        catch (Throwable t) {
            StringBuilder builder = new StringBuilder("Error getting information!");
            builder.append(' ').append(t.getMessage());
            for (StackTraceElement element : t.getStackTrace()) {
                builder.append('\n').append("\t").append(element.toString());
            }
            return builder.toString();
        }
    }
}
