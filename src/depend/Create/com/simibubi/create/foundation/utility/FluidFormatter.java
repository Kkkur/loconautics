/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 */
package com.simibubi.create.foundation.utility;

import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Couple;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class FluidFormatter {
    public static String asString(long amount, boolean shorten) {
        Couple<MutableComponent> couple = FluidFormatter.asComponents(amount, shorten);
        return ((MutableComponent)couple.getFirst()).getString() + " " + ((MutableComponent)couple.getSecond()).getString();
    }

    public static Couple<MutableComponent> asComponents(long amount, boolean shorten) {
        if (shorten && amount >= 1000L) {
            return Couple.create((Object)Component.literal((String)String.format("%.1f", (double)amount / 1000.0)), (Object)CreateLang.translateDirect("generic.unit.buckets", new Object[0]));
        }
        return Couple.create((Object)Component.literal((String)String.valueOf(amount)), (Object)CreateLang.translateDirect("generic.unit.millibuckets", new Object[0]));
    }
}
