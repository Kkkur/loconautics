/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.simibubi.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.bogey.StandardBogeyRenderer;
import com.simibubi.create.content.trains.bogey.StandardBogeyVisual;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public class AllBogeyStyles {
    public static final Map<ResourceLocation, BogeyStyle> BOGEY_STYLES = new HashMap<ResourceLocation, BogeyStyle>();
    public static final Map<ResourceLocation, Map<ResourceLocation, BogeyStyle>> CYCLE_GROUPS = new HashMap<ResourceLocation, Map<ResourceLocation, BogeyStyle>>();
    private static final Map<ResourceLocation, BogeyStyle> EMPTY_GROUP = Collections.emptyMap();
    public static final ResourceLocation STANDARD_CYCLE_GROUP = Create.asResource("standard");
    public static final BogeyStyle STANDARD = AllBogeyStyles.builder("standard", STANDARD_CYCLE_GROUP).displayName((Component)Component.translatable((String)"create.bogey.style.standard")).size(BogeySizes.SMALL, (Supplier<? extends AbstractBogeyBlock<?>>)AllBlocks.SMALL_BOGEY, () -> () -> new BogeyStyle.SizeRenderer(new StandardBogeyRenderer.Small(), StandardBogeyVisual.Small::new)).size(BogeySizes.LARGE, (Supplier<? extends AbstractBogeyBlock<?>>)AllBlocks.LARGE_BOGEY, () -> () -> new BogeyStyle.SizeRenderer(new StandardBogeyRenderer.Large(), StandardBogeyVisual.Large::new)).build();

    public static Map<ResourceLocation, BogeyStyle> getCycleGroup(ResourceLocation cycleGroup) {
        return CYCLE_GROUPS.getOrDefault(cycleGroup, EMPTY_GROUP);
    }

    private static BogeyStyle.Builder builder(String name, ResourceLocation cycleGroup) {
        return new BogeyStyle.Builder(Create.asResource(name), cycleGroup);
    }

    @ApiStatus.Internal
    public static void init() {
    }
}
