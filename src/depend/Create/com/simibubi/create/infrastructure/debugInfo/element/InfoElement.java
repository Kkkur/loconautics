/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.infrastructure.debugInfo.element;

import com.simibubi.create.infrastructure.debugInfo.element.DebugInfoSection;
import com.simibubi.create.infrastructure.debugInfo.element.InfoEntry;
import java.util.function.Consumer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public sealed interface InfoElement
permits DebugInfoSection, InfoEntry {
    public void print(int var1, @Nullable Player var2, Consumer<String> var3);

    default public void print(@Nullable Player player, Consumer<String> lineConsumer) {
        this.print(0, player, lineConsumer);
    }
}
