/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateItemModelProvider
 *  net.minecraft.data.models.ItemModelGenerators
 *  net.minecraft.data.models.ItemModelGenerators$TrimModelData
 *  net.minecraft.data.models.model.ModelLocationUtils
 *  net.minecraft.data.models.model.TextureMapping
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.Item
 *  net.neoforged.neoforge.client.model.generators.ItemModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.foundation.mixin.accessor.ItemModelGeneratorsAccessor;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Map;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class TrimmableArmorModelGenerator {
    public static final VarHandle TEXTURES_HANDLE;

    public static <T extends ArmorItem> void generate(DataGenContext<Item, T> c, RegistrateItemModelProvider p) {
        ArmorItem item = (ArmorItem)c.get();
        ItemModelBuilder builder = p.generated(c);
        for (ItemModelGenerators.TrimModelData data : ItemModelGeneratorsAccessor.create$getGENERATED_TRIM_MODELS()) {
            ResourceLocation modelLoc = ModelLocationUtils.getModelLocation((Item)item);
            ResourceLocation textureLoc = TextureMapping.getItemTexture((Item)item);
            String trimId = data.name(item.getMaterial());
            ResourceLocation trimModelLoc = modelLoc.withSuffix("_" + trimId + "_trim");
            ResourceLocation trimLoc = ResourceLocation.withDefaultNamespace((String)("trims/items/" + item.getType().getName() + "_trim_" + trimId));
            String parent = "item/generated";
            if (item.getMaterial() == AllArmorMaterials.CARDBOARD) {
                trimLoc = Create.asResource("trims/items/card_" + item.getType().getName() + "_trim_" + trimId);
            }
            ItemModelBuilder itemModel = (ItemModelBuilder)((ItemModelBuilder)p.withExistingParent(trimModelLoc.getPath(), parent)).texture("layer0", textureLoc);
            Map textures = TEXTURES_HANDLE.get(itemModel);
            textures.put("layer1", trimLoc.toString());
            builder.override().predicate(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, data.itemModelIndex()).model((ModelFile)itemModel).end();
        }
    }

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(ModelBuilder.class, MethodHandles.lookup());
            TEXTURES_HANDLE = lookup.findVarHandle(ModelBuilder.class, "textures", Map.class);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
