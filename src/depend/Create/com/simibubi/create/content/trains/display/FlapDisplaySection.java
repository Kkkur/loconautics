/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 */
package com.simibubi.create.content.trains.display;

import com.google.common.base.Strings;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class FlapDisplaySection {
    static final Map<String, String[]> LOADED_FLAP_CYCLES = new HashMap<String, String[]>();
    public static final float MONOSPACE = 7.0f;
    public static final float WIDE_MONOSPACE = 9.0f;
    float size;
    boolean singleFlap;
    boolean hasGap;
    boolean rightAligned;
    boolean wideFlaps;
    boolean sendTransition;
    String cycle;
    Component component;
    String[] cyclingOptions;
    boolean[] spinning;
    int spinningTicks;
    String text;

    public FlapDisplaySection(float width, String cycle, boolean singleFlap, boolean hasGap) {
        this.size = width;
        this.cycle = cycle;
        this.hasGap = hasGap;
        this.singleFlap = singleFlap;
        this.spinning = new boolean[singleFlap ? 1 : Math.max(0, (int)(width / 7.0f))];
        this.text = Strings.repeat((String)" ", (int)this.spinning.length);
        this.component = null;
    }

    public FlapDisplaySection rightAligned() {
        this.rightAligned = true;
        return this;
    }

    public FlapDisplaySection wideFlaps() {
        this.wideFlaps = true;
        return this;
    }

    public void setText(Component component) {
        this.component = component;
        this.sendTransition = true;
    }

    public void refresh(boolean transition) {
        if (this.component == null) {
            return;
        }
        Object newText = this.component.getString();
        if (!this.singleFlap) {
            if (this.rightAligned) {
                newText = ((String)newText).trim();
            }
            newText = ((String)newText).toUpperCase(Locale.ROOT);
            newText = ((String)newText).substring(0, Math.min(this.spinning.length, ((String)newText).length()));
            String whitespace = Strings.repeat((String)" ", (int)(this.spinning.length - ((String)newText).length()));
            Object object = newText = this.rightAligned ? whitespace + (String)newText : (String)newText + whitespace;
            if (!this.text.isEmpty()) {
                for (int i = 0; i < this.spinning.length; ++i) {
                    int n = i;
                    this.spinning[n] = this.spinning[n] | (transition && this.text.charAt(i) != ((String)newText).charAt(i));
                }
            }
        } else if (!this.text.isEmpty()) {
            this.spinning[0] = this.spinning[0] | (transition && !((String)newText).equals(this.text));
        }
        this.text = newText;
        this.spinningTicks = 0;
    }

    public int tick(boolean instant, RandomSource randomSource) {
        if (this.cyclingOptions == null) {
            return 0;
        }
        int max = Math.max(4, (int)((float)this.cyclingOptions.length * 1.75f));
        if (this.spinningTicks > max) {
            return 0;
        }
        ++this.spinningTicks;
        if (this.spinningTicks <= max && this.spinningTicks < 2) {
            return this.spinningTicks == 1 ? 0 : this.spinning.length;
        }
        int spinningFlaps = 0;
        for (int i = 0; i < this.spinning.length; ++i) {
            int increasingChance = Mth.clamp((int)(8 - this.spinningTicks), (int)1, (int)10);
            boolean continueSpin = !instant && randomSource.nextInt(increasingChance * max / 4) != 0;
            boolean bl = max > 5 || this.spinningTicks < 2;
            int n = i;
            this.spinning[n] = this.spinning[n] & (continueSpin &= bl);
            if (i > 0 && randomSource.nextInt(3) > 0) {
                int n2 = i - 1;
                this.spinning[n2] = this.spinning[n2] & continueSpin;
            }
            if (i < this.spinning.length - 1 && randomSource.nextInt(3) > 0) {
                int n3 = i + 1;
                this.spinning[n3] = this.spinning[n3] & continueSpin;
            }
            if (this.spinningTicks > max) {
                this.spinning[i] = false;
            }
            if (!this.spinning[i]) continue;
            ++spinningFlaps;
        }
        return spinningFlaps;
    }

    public float getSize() {
        return this.size;
    }

    public CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Width", this.size);
        tag.putString("Cycle", this.cycle);
        if (this.rightAligned) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"RightAligned");
        }
        if (this.singleFlap) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"SingleFlap");
        }
        if (this.hasGap) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"Gap");
        }
        if (this.wideFlaps) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"Wide");
        }
        if (this.component != null) {
            tag.putString("Text", Component.Serializer.toJson((Component)this.component, (HolderLookup.Provider)registries));
        }
        if (this.sendTransition) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"Transition");
        }
        this.sendTransition = false;
        return tag;
    }

    public static FlapDisplaySection load(CompoundTag tag, HolderLookup.Provider registries) {
        float width = tag.getFloat("Width");
        String cycle = tag.getString("Cycle");
        boolean singleFlap = tag.contains("SingleFlap");
        boolean hasGap = tag.contains("Gap");
        FlapDisplaySection section = new FlapDisplaySection(width, cycle, singleFlap, hasGap);
        section.cyclingOptions = FlapDisplaySection.getFlapCycle(cycle);
        section.rightAligned = tag.contains("RightAligned");
        section.wideFlaps = tag.contains("Wide");
        if (!tag.contains("Text")) {
            return section;
        }
        section.component = Component.Serializer.fromJson((String)tag.getString("Text"), (HolderLookup.Provider)registries);
        section.refresh(tag.getBoolean("Transition"));
        return section;
    }

    public void update(CompoundTag tag, HolderLookup.Provider registries) {
        String text = tag.getString("Text");
        if (!text.isEmpty()) {
            this.component = Component.Serializer.fromJson((String)text, (HolderLookup.Provider)registries);
        }
        if (this.cyclingOptions == null) {
            this.cyclingOptions = FlapDisplaySection.getFlapCycle(this.cycle);
        }
        this.refresh(tag.getBoolean("Transition"));
    }

    public boolean renderCharsIndividually() {
        return !this.singleFlap;
    }

    public Component getText() {
        return this.component;
    }

    public static String[] getFlapCycle(String key) {
        return LOADED_FLAP_CYCLES.computeIfAbsent(key, k -> CreateLang.translateDirect("flap_display.cycles." + key, new Object[0]).getString().split(";"));
    }
}
