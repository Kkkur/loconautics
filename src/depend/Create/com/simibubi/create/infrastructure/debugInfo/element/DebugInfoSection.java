/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.infrastructure.debugInfo.element;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.infrastructure.debugInfo.DebugInformation;
import com.simibubi.create.infrastructure.debugInfo.InfoProvider;
import com.simibubi.create.infrastructure.debugInfo.element.InfoElement;
import com.simibubi.create.infrastructure.debugInfo.element.InfoEntry;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record DebugInfoSection(String name, ImmutableList<InfoElement> elements) implements InfoElement
{
    public Builder builder() {
        return DebugInfoSection.builder(this.name).putAll((Collection<? extends InfoElement>)this.elements);
    }

    @Override
    public void print(int depth, @Nullable Player player, Consumer<String> lineConsumer) {
        String indent = DebugInformation.getIndent(depth);
        lineConsumer.accept(indent + this.name + ":");
        this.elements.forEach(element -> element.print(depth + 1, player, lineConsumer));
    }

    public static Builder builder(String name) {
        return new Builder(null, name);
    }

    public static DebugInfoSection of(String name, Collection<DebugInfoSection> children) {
        return DebugInfoSection.builder(name).putAll(children).build();
    }

    public static class Builder {
        private final Builder parent;
        private final String name;
        private final ImmutableList.Builder<InfoElement> elements;

        public Builder(Builder parent, String name) {
            this.parent = parent;
            this.name = name;
            this.elements = ImmutableList.builder();
        }

        public Builder put(InfoElement element) {
            this.elements.add((Object)element);
            return this;
        }

        public Builder put(String key, InfoProvider provider) {
            return this.put(new InfoEntry(key, provider));
        }

        public Builder put(String key, Supplier<String> value) {
            return this.put(key, (Player player) -> (String)value.get());
        }

        public Builder put(String key, String value) {
            return this.put(key, (Player player) -> value);
        }

        public Builder putAll(Collection<? extends InfoElement> elements) {
            elements.forEach(this::put);
            return this;
        }

        public Builder section(String name) {
            return new Builder(this, name);
        }

        public Builder finishSection() {
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
}
