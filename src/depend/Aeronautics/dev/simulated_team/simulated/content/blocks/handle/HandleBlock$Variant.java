/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.neoforged.neoforge.common.Tags$Items
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.handle;

import com.simibubi.create.AllTags;
import java.util.Locale;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public static enum HandleBlock.Variant implements StringRepresentable
{
    IRON(Ingredient.of((TagKey)Tags.Items.NUGGETS_IRON)),
    COPPER(Ingredient.of((TagKey)AllTags.commonItemTag((String)"nuggets/copper"))),
    DYED(null);

    @Nullable
    final Ingredient ingredient;

    private HandleBlock.Variant(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    @Nullable
    public Ingredient getIngredient() {
        return this.ingredient;
    }
}
