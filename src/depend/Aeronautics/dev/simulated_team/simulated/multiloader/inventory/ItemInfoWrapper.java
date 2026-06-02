/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.multiloader.inventory;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record ItemInfoWrapper(Item type, DataComponentPatch patchMap) {
    public static ItemInfoWrapper generateFromStack(ItemStack stack) {
        return new ItemInfoWrapper(stack.getItem(), stack.getComponentsPatch());
    }

    @NotNull
    public static ItemStack generateFromInfo(ItemInfoWrapper info) {
        ItemStack newstack = info.type().getDefaultInstance();
        for (Map.Entry set : info.patchMap().entrySet()) {
            ItemInfoWrapper.setDataComponent((DataComponentType)set.getKey(), (Optional)set.getValue(), newstack);
        }
        return newstack;
    }

    private static <T> void setDataComponent(DataComponentType<?> type, Optional<?> set, ItemStack newstack) {
        newstack.set(type, set.get());
    }
}
