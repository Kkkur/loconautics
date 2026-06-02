/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.EquipmentSlot$Type
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Equipable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.ComposterBlock
 *  net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 */
package com.simibubi.create.content.logistics.item.filter.attribute;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.item.filter.attribute.SingletonItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.AddedByAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.BookAuthorAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.BookCopyAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.ColorAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.EnchantAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.FluidContentsAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.InItemGroupAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.InTagAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.ItemNameAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.ShulkerFillLevelAttribute;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;

public class AllItemAttributeTypes {
    public static final ItemAttributeType PLACEABLE = AllItemAttributeTypes.singleton("placeable", (ItemStack s) -> s.getItem() instanceof BlockItem);
    public static final ItemAttributeType CONSUMABLE = AllItemAttributeTypes.singleton("consumable", (ItemStack s) -> s.has(DataComponents.FOOD));
    public static final ItemAttributeType FLUID_CONTAINER = AllItemAttributeTypes.singleton("fluid_container", (ItemStack s) -> s.getCapability(Capabilities.FluidHandler.ITEM) != null);
    public static final ItemAttributeType ENCHANTED = AllItemAttributeTypes.singleton("enchanted", ItemStack::isEnchanted);
    public static final ItemAttributeType MAX_ENCHANTED = AllItemAttributeTypes.singleton("max_enchanted", AllItemAttributeTypes::maxEnchanted);
    public static final ItemAttributeType RENAMED = AllItemAttributeTypes.singleton("renamed", (ItemStack s) -> s.has(DataComponents.CUSTOM_NAME));
    public static final ItemAttributeType DAMAGED = AllItemAttributeTypes.singleton("damaged", ItemStack::isDamaged);
    public static final ItemAttributeType BADLY_DAMAGED = AllItemAttributeTypes.singleton("badly_damaged", (ItemStack s) -> s.isDamaged() && (float)s.getDamageValue() / (float)s.getMaxDamage() > 0.75f);
    public static final ItemAttributeType NOT_STACKABLE = AllItemAttributeTypes.singleton("not_stackable", ((Predicate<ItemStack>)ItemStack::isStackable).negate());
    public static final ItemAttributeType EQUIPABLE = AllItemAttributeTypes.singleton("equipable", (ItemStack s) -> {
        Equipable equipable = Equipable.get((ItemStack)s);
        EquipmentSlot.Type type = equipable != null ? equipable.getEquipmentSlot().getType() : EquipmentSlot.MAINHAND.getType();
        return type != EquipmentSlot.Type.HAND;
    });
    public static final ItemAttributeType FURNACE_FUEL = AllItemAttributeTypes.singleton("furnace_fuel", AbstractFurnaceBlockEntity::isFuel);
    public static final ItemAttributeType WASHABLE = AllItemAttributeTypes.singleton("washable", AllFanProcessingTypes.SPLASHING::canProcess);
    public static final ItemAttributeType HAUNTABLE = AllItemAttributeTypes.singleton("hauntable", AllFanProcessingTypes.HAUNTING::canProcess);
    public static final ItemAttributeType CRUSHABLE = AllItemAttributeTypes.singleton("crushable", (ItemStack s, Level w) -> AllItemAttributeTypes.testRecipe(s, w, AllRecipeTypes.CRUSHING.getType()) || AllItemAttributeTypes.testRecipe(s, w, AllRecipeTypes.MILLING.getType()));
    public static final ItemAttributeType SMELTABLE = AllItemAttributeTypes.singleton("smeltable", (ItemStack s, Level w) -> AllItemAttributeTypes.testRecipe(s, w, RecipeType.SMELTING));
    public static final ItemAttributeType SMOKABLE = AllItemAttributeTypes.singleton("smokable", (ItemStack s, Level w) -> AllItemAttributeTypes.testRecipe(s, w, RecipeType.SMOKING));
    public static final ItemAttributeType BLASTABLE = AllItemAttributeTypes.singleton("blastable", (ItemStack s, Level w) -> AllItemAttributeTypes.testRecipe(s, w, RecipeType.BLASTING));
    public static final ItemAttributeType COMPOSTABLE = AllItemAttributeTypes.singleton("compostable", (ItemStack s) -> ComposterBlock.getValue((ItemStack)s) > 0.0f);
    public static final ItemAttributeType IN_TAG = AllItemAttributeTypes.register("in_tag", new InTagAttribute.Type());
    public static final ItemAttributeType IN_ITEM_GROUP = AllItemAttributeTypes.register("in_item_group", new InItemGroupAttribute.Type());
    public static final ItemAttributeType ADDED_BY = AllItemAttributeTypes.register("added_by", new AddedByAttribute.Type());
    public static final ItemAttributeType HAS_ENCHANT = AllItemAttributeTypes.register("has_enchant", new EnchantAttribute.Type());
    public static final ItemAttributeType SHULKER_FILL_LEVEL = AllItemAttributeTypes.register("shulker_fill_level", new ShulkerFillLevelAttribute.Type());
    public static final ItemAttributeType HAS_COLOR = AllItemAttributeTypes.register("has_color", new ColorAttribute.Type());
    public static final ItemAttributeType HAS_FLUID = AllItemAttributeTypes.register("has_fluid", new FluidContentsAttribute.Type());
    public static final ItemAttributeType HAS_NAME = AllItemAttributeTypes.register("has_name", new ItemNameAttribute.Type());
    public static final ItemAttributeType BOOK_AUTHOR = AllItemAttributeTypes.register("book_author", new BookAuthorAttribute.Type());
    public static final ItemAttributeType BOOK_COPY = AllItemAttributeTypes.register("book_copy", new BookCopyAttribute.Type());

    private static <T extends Recipe<SingleRecipeInput>> boolean testRecipe(ItemStack s, Level w, RecipeType<T> type) {
        return w.getRecipeManager().getRecipeFor(type, (RecipeInput)new SingleRecipeInput(s.copy()), w).isPresent();
    }

    private static boolean maxEnchanted(ItemStack s) {
        for (Object2IntMap.Entry entry : s.getTagEnchantments().entrySet()) {
            if (((Enchantment)((Holder)entry.getKey()).value()).getMaxLevel() > entry.getIntValue()) continue;
            return true;
        }
        return false;
    }

    private static ItemAttributeType singleton(String id, Predicate<ItemStack> predicate) {
        return AllItemAttributeTypes.register(id, new SingletonItemAttribute.Type(type -> new SingletonItemAttribute((SingletonItemAttribute.Type)type, (stack, level) -> predicate.test((ItemStack)stack), id)));
    }

    private static ItemAttributeType singleton(String id, BiPredicate<ItemStack, Level> predicate) {
        return AllItemAttributeTypes.register(id, new SingletonItemAttribute.Type(type -> new SingletonItemAttribute((SingletonItemAttribute.Type)type, predicate, id)));
    }

    private static ItemAttributeType register(String id, ItemAttributeType type) {
        return (ItemAttributeType)Registry.register(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, (ResourceLocation)Create.asResource(id), (Object)type);
    }

    public static void init() {
    }
}
