/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.client;

import dev.simulated_team.simulated.client.BlockPropertiesTooltip;
import org.jetbrains.annotations.NotNull;

public record BlockPropertiesTooltip.Entry(BlockPropertiesTooltip.TooltipFunction tooltipFunction, float priority) implements Comparable<BlockPropertiesTooltip.Entry>
{
    @Override
    public int compareTo(@NotNull BlockPropertiesTooltip.Entry o) {
        return Float.compare(this.priority, o.priority);
    }
}
