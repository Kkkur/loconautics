/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.kinetics.transmission.sequencer;

import com.simibubi.create.foundation.utility.CreateLang;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public enum InstructionSpeedModifiers {
    FORWARD_FAST(2, ">>"),
    FORWARD(1, "->"),
    BACK(-1, "<-"),
    BACK_FAST(-2, "<<");

    public static final StreamCodec<ByteBuf, InstructionSpeedModifiers> STREAM_CODEC;
    String translationKey;
    int value;
    Component label;

    private InstructionSpeedModifiers(int modifier, Component label) {
        this.label = label;
        this.translationKey = "gui.sequenced_gearshift.speed." + Lang.asId((String)this.name());
        this.value = modifier;
    }

    private InstructionSpeedModifiers(int modifier, String label) {
        this.label = Component.literal((String)label);
        this.translationKey = "gui.sequenced_gearshift.speed." + Lang.asId((String)this.name());
        this.value = modifier;
    }

    static List<Component> getOptions() {
        ArrayList<Component> options = new ArrayList<Component>();
        for (InstructionSpeedModifiers entry : InstructionSpeedModifiers.values()) {
            options.add((Component)CreateLang.translateDirect(entry.translationKey, new Object[0]));
        }
        return options;
    }

    public static InstructionSpeedModifiers getByModifier(int modifier) {
        return Arrays.stream(InstructionSpeedModifiers.values()).filter(speedModifier -> speedModifier.value == modifier).findAny().orElse(FORWARD);
    }

    static {
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(InstructionSpeedModifiers.class);
    }
}
