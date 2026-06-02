/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.behaviour.display.DisplayTarget
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkContext
 *  net.minecraft.network.chat.Component
 */
package dev.simulated_team.simulated.api;

import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import net.minecraft.network.chat.Component;

public abstract class ConditionalDisplayTarget
extends DisplayTarget {
    public abstract boolean allowsWriting(DisplayLinkContext var1);

    public abstract Component getErrorMessage(DisplayLinkContext var1);
}
