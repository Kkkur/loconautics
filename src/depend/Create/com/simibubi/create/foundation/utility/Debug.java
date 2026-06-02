/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.fml.util.thread.EffectiveSide
 */
package com.simibubi.create.foundation.utility;

import com.simibubi.create.Create;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.util.thread.EffectiveSide;

@OnlyIn(value=Dist.CLIENT)
public class Debug {
    @Deprecated
    public static void debugChat(String message) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage((Component)Component.literal((String)message), false);
        }
    }

    @Deprecated
    public static void debugChatAndShowStack(String message, int depth) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage((Component)Component.literal((String)message).append("@").append(Debug.debugStack(depth)), false);
        }
    }

    @Deprecated
    public static void debugMessage(String message) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage((Component)Component.literal((String)message), true);
        }
    }

    @Deprecated
    public static void log(String message) {
        Create.LOGGER.info(message);
    }

    @Deprecated
    public static String getLogicalSide() {
        return EffectiveSide.get().isClient() ? "CL" : "SV";
    }

    @Deprecated
    public static Component debugStack(int depth) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        MutableComponent text = Component.literal((String)"[").append((Component)Component.literal((String)Debug.getLogicalSide()).withStyle(ChatFormatting.GOLD)).append("] ");
        for (int i = 1; i < depth + 2 && i < stackTraceElements.length; ++i) {
            StackTraceElement e = stackTraceElements[i];
            if (e.getClassName().equals(Debug.class.getName())) continue;
            text.append((Component)Component.literal((String)e.getMethodName()).withStyle(ChatFormatting.YELLOW)).append(", ");
        }
        return text.append((Component)Component.literal((String)" ...").withStyle(ChatFormatting.GRAY));
    }

    @Deprecated
    public static void markTemporary() {
    }
}
