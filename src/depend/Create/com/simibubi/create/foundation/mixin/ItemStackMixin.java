/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.PatchedDataComponentMap
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.simibubi.create.foundation.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import java.util.function.BiFunction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated(since="6.0.7", forRemoval=true)
@ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
@Mixin(value={ItemStack.class})
public class ItemStackMixin {
    @Unique
    private static final ResourceLocation create$CLIPBOARD_ID = ResourceLocation.fromNamespaceAndPath((String)"create", (String)"clipboard");

    @Inject(method={"<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/item/Item;verifyComponentsAfterLoad(Lnet/minecraft/world/item/ItemStack;)V")})
    private void create$migrateOldClipboardComponents(ItemLike item, int count, PatchedDataComponentMap components, CallbackInfo ci) {
        if (!BuiltInRegistries.ITEM.getKey((Object)item.asItem()).equals((Object)create$CLIPBOARD_ID)) {
            return;
        }
        ClipboardContent content = ClipboardContent.EMPTY;
        content = ItemStackMixin.create$migrateComponent(content, components, AllDataComponents.CLIPBOARD_PAGES, ClipboardContent::setPages);
        content = ItemStackMixin.create$migrateComponent(content, components, AllDataComponents.CLIPBOARD_TYPE, ClipboardContent::setType);
        content = ItemStackMixin.create$migrateComponent(content, components, AllDataComponents.CLIPBOARD_READ_ONLY, (c, v) -> c.setReadOnly(true));
        content = ItemStackMixin.create$migrateComponent(content, components, AllDataComponents.CLIPBOARD_COPIED_VALUES, ClipboardContent::setCopiedValues);
        if ((content = ItemStackMixin.create$migrateComponent(content, components, AllDataComponents.CLIPBOARD_PREVIOUSLY_OPENED_PAGE, ClipboardContent::setPreviouslyOpenedPage)) != ClipboardContent.EMPTY) {
            components.set(AllDataComponents.CLIPBOARD_CONTENT, (Object)content);
        }
    }

    @Unique
    private static <T> ClipboardContent create$migrateComponent(ClipboardContent content, PatchedDataComponentMap components, DataComponentType<T> componentType, BiFunction<ClipboardContent, T, ClipboardContent> function) {
        Object value = components.get(componentType);
        if (value != null) {
            components.remove(componentType);
            content = function.apply(content, (ClipboardContent)value);
        }
        return content;
    }
}
