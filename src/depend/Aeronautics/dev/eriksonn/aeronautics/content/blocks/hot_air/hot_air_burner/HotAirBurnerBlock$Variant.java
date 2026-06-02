/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.Item
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner;

import dev.eriksonn.aeronautics.index.AeroTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

public static enum HotAirBurnerBlock.Variant implements StringRepresentable
{
    FIRE("fire", SoundEvents.NETHERRACK_PLACE),
    SOUL_FIRE("soulful", SoundEvents.SOUL_SAND_PLACE);

    public final String name;
    public final SoundEvent sound;

    private HotAirBurnerBlock.Variant(String name, SoundEvent sound) {
        this.name = name;
        this.sound = sound;
    }

    public static HotAirBurnerBlock.Variant getConversionFromItem(Item item) {
        if (item.builtInRegistryHolder().is(AeroTags.ItemTags.BURNER_FIRE)) {
            return FIRE;
        }
        if (item.builtInRegistryHolder().is(ItemTags.SOUL_FIRE_BASE_BLOCKS)) {
            return SOUL_FIRE;
        }
        return null;
    }

    public String getSerializedName() {
        return this.name;
    }
}
