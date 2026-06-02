/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.PhysicalFloat
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Mth
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.foundation.blockEntity.behaviour.scrollValue;

import net.createmod.catnip.animation.PhysicalFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ScrollValueHandler {
    private static float lastPassiveScroll = 0.0f;
    private static float passiveScroll = 0.0f;
    private static float passiveScrollDirection = 1.0f;
    public static final PhysicalFloat wrenchCog = PhysicalFloat.create().withDrag(0.3);

    public static float getScroll(float partialTicks) {
        return wrenchCog.getValue(partialTicks) + Mth.lerp((float)partialTicks, (float)lastPassiveScroll, (float)passiveScroll);
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void tick() {
        if (!Minecraft.getInstance().isPaused()) {
            lastPassiveScroll = passiveScroll;
            wrenchCog.tick();
            passiveScroll = (float)((double)passiveScroll + (double)passiveScrollDirection * 0.5);
        }
    }
}
