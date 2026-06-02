/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 */
package dev.simulated_team.simulated.ponder.new_ponder_tooltip;

import java.util.HashSet;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record NewPonderTooltipManager.RegisterBuilder(Item[] items) {
    public NewPonderTooltipManager.RegisterBuilder addScenes(ResourceLocation ... scenes) {
        HashSet<ResourceLocation> sceneSet = new HashSet<ResourceLocation>(List.of(scenes));
        for (Item item : this.items) {
            NEW_PONDER_SCENES.computeIfAbsent(item, k -> new HashSet()).addAll(sceneSet);
        }
        return this;
    }
}
