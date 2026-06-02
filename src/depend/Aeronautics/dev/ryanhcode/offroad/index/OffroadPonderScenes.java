/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.AbstractRegistrate
 *  com.tterrag.registrate.util.entry.ItemProviderEntry
 *  net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.neoforged.neoforge.registries.DeferredHolder
 */
package dev.ryanhcode.offroad.index;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.content.ponder.scenes.BoreheadBearingScenes;
import dev.ryanhcode.offroad.index.OffroadBlocks;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class OffroadPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> registry) {
        PonderSceneRegistrationHelper helper = registry.withKeyFunction(DeferredHolder::getId);
        helper.forComponents((Object[])new ItemProviderEntry[]{OffroadBlocks.BOREHEAD_BEARING_BLOCK, OffroadBlocks.ROCK_CUTTER_BLOCK}).addStoryBoard("borehead_bearing/intro", BoreheadBearingScenes::boreheadIntro).addStoryBoard("borehead_bearing/excavating", BoreheadBearingScenes::boreheadExcavating).addStoryBoard("borehead_bearing/efficiency", BoreheadBearingScenes::boreheadEfficiency);
    }

    private static ItemProviderEntry<Item, Item> offroadItemProvider(String id) {
        return new ItemProviderEntry((AbstractRegistrate)Offroad.getRegistrate(), DeferredHolder.create((ResourceKey)ResourceKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)Offroad.path(id))));
    }
}
