/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.Item
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent;

import dev.eriksonn.aeronautics.index.AeroTags;
import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

public static enum SteamVentBlock.Variant implements StringRepresentable
{
    GOLD,
    IRON;


    public static SteamVentBlock.Variant getConversionFromItem(Item item) {
        if (item.builtInRegistryHolder().is(AeroTags.ItemTags.GOLD_SHEET)) {
            return GOLD;
        }
        if (item.builtInRegistryHolder().is(AeroTags.ItemTags.IRON_SHEET)) {
            return IRON;
        }
        return null;
    }

    public String getSerializedName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }
}
