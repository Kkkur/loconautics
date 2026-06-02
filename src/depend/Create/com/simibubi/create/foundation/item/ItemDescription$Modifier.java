/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.item.Item
 *  net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
 */
package com.simibubi.create.foundation.item;

import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public static class ItemDescription.Modifier
implements TooltipModifier {
    protected final Item item;
    protected final FontHelper.Palette palette;
    protected String cachedLanguage;
    protected ItemDescription description;

    public ItemDescription.Modifier(Item item, FontHelper.Palette palette) {
        this.item = item;
        this.palette = palette;
    }

    @Override
    public void modify(ItemTooltipEvent context) {
        if (this.checkLocale()) {
            this.description = ItemDescription.create(this.item, this.palette);
        }
        if (this.description == null) {
            return;
        }
        context.getToolTip().addAll(1, this.description.getCurrentLines());
    }

    protected boolean checkLocale() {
        String currentLanguage = Minecraft.getInstance().getLanguageManager().getSelected();
        if (!currentLanguage.equals(this.cachedLanguage)) {
            this.cachedLanguage = currentLanguage;
            return true;
        }
        return false;
    }
}
