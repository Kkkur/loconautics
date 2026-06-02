/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Unmodifiable
 */
package com.simibubi.create.content.logistics.box;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.box.PackageItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

public class PackageStyles {
    @ApiStatus.Internal
    public static final @Unmodifiable List<PackageStyle> STYLES = ImmutableList.of((Object)new PackageStyle("cardboard", 12, 12, 23.0f, false), (Object)new PackageStyle("cardboard", 10, 12, 22.0f, false), (Object)new PackageStyle("cardboard", 10, 8, 18.0f, false), (Object)new PackageStyle("cardboard", 12, 10, 21.0f, false), (Object)PackageStyles.rare("creeper"), (Object)PackageStyles.rare("darcy"), (Object)PackageStyles.rare("evan"), (Object)PackageStyles.rare("jinx"), (Object)PackageStyles.rare("kryppers"), (Object)PackageStyles.rare("simi"), (Object)PackageStyles.rare("starlotte"), (Object)PackageStyles.rare("thunder"), (Object[])new PackageStyle[]{PackageStyles.rare("up"), PackageStyles.rare("vector")});
    public static final List<PackageItem> ALL_BOXES = new ArrayList<PackageItem>();
    public static final List<PackageItem> STANDARD_BOXES = new ArrayList<PackageItem>();
    public static final List<PackageItem> RARE_BOXES = new ArrayList<PackageItem>();
    private static final Random STYLE_PICKER = new Random();
    private static final int RARE_CHANCE = 7500;

    public static ItemStack getRandomBox() {
        List<PackageItem> pool = STYLE_PICKER.nextInt(7500) == 0 ? RARE_BOXES : STANDARD_BOXES;
        return new ItemStack((ItemLike)pool.get(STYLE_PICKER.nextInt(pool.size())));
    }

    public static ItemStack getDefaultBox() {
        return new ItemStack((ItemLike)ALL_BOXES.get(0));
    }

    private static PackageStyle rare(String name) {
        return new PackageStyle("rare_" + name, 12, 10, 21.0f, true);
    }

    public record PackageStyle(String type, int width, int height, float riggingOffset, boolean rare) {
        public ResourceLocation getItemId() {
            String size = "_" + this.width + "x" + this.height;
            String id = this.type + "_package" + (String)(this.rare ? "" : size);
            return Create.asResource(id);
        }

        public ResourceLocation getRiggingModel() {
            String size = this.width + "x" + this.height;
            return Create.asResource("item/package/rigging_" + size);
        }
    }
}
