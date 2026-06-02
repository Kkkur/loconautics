/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package com.simibubi.create.content.trains.display;

import com.simibubi.create.content.trains.display.FlapDisplaySection;
import java.util.Arrays;
import java.util.List;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.apache.commons.lang3.mutable.MutableInt;

public class FlapDisplayLayout {
    List<FlapDisplaySection> sections;
    String layoutKey;

    public FlapDisplayLayout(int maxCharCount) {
        this.loadDefault(maxCharCount);
    }

    public void loadDefault(int maxCharCount) {
        this.configure("Default", Arrays.asList(new FlapDisplaySection((float)maxCharCount * 7.0f, "alphabet", false, false)));
    }

    public boolean isLayout(String key) {
        return this.layoutKey.equals(key);
    }

    public void configure(String layoutKey, List<FlapDisplaySection> sections) {
        this.layoutKey = layoutKey;
        this.sections = sections;
    }

    public CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Key", this.layoutKey);
        tag.put("Sections", (Tag)NBTHelper.writeCompoundList(this.sections, s -> s.write(registries)));
        return tag;
    }

    public void read(CompoundTag tag, HolderLookup.Provider registries) {
        String prevKey = this.layoutKey;
        this.layoutKey = tag.getString("Key");
        ListTag sectionsTag = tag.getList("Sections", 10);
        if (!prevKey.equals(this.layoutKey)) {
            this.sections = NBTHelper.readCompoundList((ListTag)sectionsTag, i -> FlapDisplaySection.load(i, registries));
            return;
        }
        MutableInt index = new MutableInt(0);
        NBTHelper.iterateCompoundList((ListTag)sectionsTag, nbt -> this.sections.get(index.getAndIncrement()).update((CompoundTag)nbt, registries));
    }

    public List<FlapDisplaySection> getSections() {
        return this.sections;
    }
}
