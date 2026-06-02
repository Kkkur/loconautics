/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package dev.simulated_team.simulated.content.entities.diagram.screen;

import com.mojang.serialization.Codec;
import java.util.List;

public record Greeble.TextureSlice(int x, int y, int width, int height) {
    public static Codec<Greeble.TextureSlice> CODEC = Codec.INT.listOf(4, 4).xmap(Greeble.TextureSlice::new, Greeble.TextureSlice::asList);

    public Greeble.TextureSlice(List<Integer> list) {
        this(list.get(0), list.get(1), list.get(2), list.get(3));
    }

    public List<Integer> asList() {
        return List.of(Integer.valueOf(this.x()), Integer.valueOf(this.y()), Integer.valueOf(this.width()), Integer.valueOf(this.height()));
    }
}
