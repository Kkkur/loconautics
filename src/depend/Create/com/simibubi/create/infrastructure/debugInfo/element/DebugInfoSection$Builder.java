/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.minecraft.world.entity.player.Player
 */
package com.simibubi.create.infrastructure.debugInfo.element;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.infrastructure.debugInfo.InfoProvider;
import com.simibubi.create.infrastructure.debugInfo.element.DebugInfoSection;
import com.simibubi.create.infrastructure.debugInfo.element.InfoElement;
import com.simibubi.create.infrastructure.debugInfo.element.InfoEntry;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;

public static class DebugInfoSection.Builder {
    private final DebugInfoSection.Builder parent;
    private final String name;
    private final ImmutableList.Builder<InfoElement> elements;

    public DebugInfoSection.Builder(DebugInfoSection.Builder parent, String name) {
        this.parent = parent;
        this.name = name;
        this.elements = ImmutableList.builder();
    }

    public DebugInfoSection.Builder put(InfoElement element) {
        this.elements.add((Object)element);
        return this;
    }

    public DebugInfoSection.Builder put(String key, InfoProvider provider) {
        return this.put(new InfoEntry(key, provider));
    }

    public DebugInfoSection.Builder put(String key, Supplier<String> value) {
        return this.put(key, (Player player) -> (String)value.get());
    }

    public DebugInfoSection.Builder put(String key, String value) {
        return this.put(key, (Player player) -> value);
    }

    public DebugInfoSection.Builder putAll(Collection<? extends InfoElement> elements) {
        elements.forEach(this::put);
        return this;
    }

    public DebugInfoSection.Builder section(String name) {
        return new DebugInfoSection.Builder(this, name);
    }

    public DebugInfoSection.Builder finishSection() {
        if (this.parent == null) {
            throw new IllegalStateException("Cannot finish the root section");
        }
        this.parent.elements.add((Object)this.build());
        return this.parent;
    }

    public DebugInfoSection build() {
        return new DebugInfoSection(this.name, (ImmutableList<InfoElement>)this.elements.build());
    }

    public void buildTo(Consumer<DebugInfoSection> consumer) {
        consumer.accept(this.build());
    }
}
