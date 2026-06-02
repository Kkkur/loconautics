/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.RandomSource
 */
package dev.simulated_team.simulated.content.entities.diagram.screen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public record Greeble(ResourceLocation texture, List<TextureSlice> slices, int width, int height, float weight) {
    public static final Codec<Greeble> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ResourceLocation.CODEC.fieldOf("texture").forGetter(g -> g.texture), (App)TextureSlice.CODEC.listOf().fieldOf("slices").forGetter(g -> g.slices), (App)Codec.INT.fieldOf("width").forGetter(g -> g.width), (App)Codec.INT.fieldOf("height").forGetter(g -> g.height), (App)Codec.FLOAT.optionalFieldOf("weight", (Object)Float.valueOf(100.0f)).forGetter(g -> Float.valueOf(g.weight))).apply((Applicative)instance, Greeble::new));

    public TextureSlice random(RandomSource random) {
        return this.slices.get(random.nextInt(this.slices.size()));
    }

    public ArrayList<TextureSlice> shuffled() {
        ArrayList<TextureSlice> list = new ArrayList<TextureSlice>(this.slices());
        Collections.shuffle(list);
        return list;
    }

    public record TextureSlice(int x, int y, int width, int height) {
        public static Codec<TextureSlice> CODEC = Codec.INT.listOf(4, 4).xmap(TextureSlice::new, TextureSlice::asList);

        public TextureSlice(List<Integer> list) {
            this(list.get(0), list.get(1), list.get(2), list.get(3));
        }

        public List<Integer> asList() {
            return List.of(Integer.valueOf(this.x()), Integer.valueOf(this.y()), Integer.valueOf(this.width()), Integer.valueOf(this.height()));
        }
    }
}
