/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.detail.VanillaDetailRegistries
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.compat.computercraft.implementation.luaObjects;

import com.simibubi.create.compat.computercraft.implementation.luaObjects.LuaComparable;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import java.util.Map;
import net.minecraft.world.item.ItemStack;

public class LuaItemStack
implements LuaComparable {
    private final ItemStack stack;

    public LuaItemStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public Map<?, ?> getTableRepresentation() {
        return VanillaDetailRegistries.ITEM_STACK.getDetails((Object)this.stack);
    }
}
