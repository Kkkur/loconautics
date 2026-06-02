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

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public enum SequencerInstructions {
    TURN_ANGLE("angle", AllGuiTextures.SEQUENCER_INSTRUCTION, true, true, 360, 45, 90),
    TURN_DISTANCE("distance", AllGuiTextures.SEQUENCER_INSTRUCTION, true, true, 128, 5, 5),
    DELAY("duration", AllGuiTextures.SEQUENCER_DELAY, true, false, 600, 20, 10),
    AWAIT("", AllGuiTextures.SEQUENCER_AWAIT),
    END("", AllGuiTextures.SEQUENCER_END);

    public static final StreamCodec<ByteBuf, SequencerInstructions> STREAM_CODEC;
    public final String translationKey;
    public final String descriptiveTranslationKey;
    public final String parameterKey;
    public final boolean hasValueParameter;
    public final boolean hasSpeedParameter;
    public final AllGuiTextures background;
    public final int maxValue;
    public final int shiftStep;
    public final int defaultValue;

    private SequencerInstructions(String parameterName, AllGuiTextures background) {
        this(parameterName, background, false, false, -1, -1, -1);
    }

    private SequencerInstructions(String parameterName, AllGuiTextures background, boolean hasValueParameter, boolean hasSpeedParameter, int maxValue, int shiftStep, int defaultValue) {
        this.hasValueParameter = hasValueParameter;
        this.hasSpeedParameter = hasSpeedParameter;
        this.background = background;
        this.maxValue = maxValue;
        this.shiftStep = shiftStep;
        this.defaultValue = defaultValue;
        this.translationKey = "gui.sequenced_gearshift.instruction." + Lang.asId((String)this.name());
        this.descriptiveTranslationKey = this.translationKey + ".descriptive";
        this.parameterKey = this.translationKey + "." + parameterName;
    }

    public boolean needsPropagation() {
        return this == TURN_ANGLE || this == TURN_DISTANCE;
    }

    static List<Component> getOptions() {
        ArrayList<Component> options = new ArrayList<Component>();
        for (SequencerInstructions entry : SequencerInstructions.values()) {
            options.add((Component)CreateLang.translateDirect(entry.descriptiveTranslationKey, new Object[0]));
        }
        return options;
    }

    String formatValue(int value) {
        if (this == TURN_ANGLE) {
            return value + CreateLang.translateDirect("generic.unit.degrees", new Object[0]).getString();
        }
        if (this == TURN_DISTANCE) {
            return value + "m";
        }
        if (this == DELAY) {
            if (value >= 20) {
                return value / 20 + "s";
            }
            return value + "t";
        }
        return "" + value;
    }

    static {
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(SequencerInstructions.class);
    }
}
