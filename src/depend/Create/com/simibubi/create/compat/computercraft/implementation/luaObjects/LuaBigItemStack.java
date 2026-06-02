/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.detail.VanillaDetailRegistries
 */
package com.simibubi.create.compat.computercraft.implementation.luaObjects;

import com.simibubi.create.compat.computercraft.implementation.luaObjects.LuaComparable;
import com.simibubi.create.content.logistics.BigItemStack;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import java.util.Map;

public class LuaBigItemStack
implements LuaComparable {
    private final BigItemStack stack;

    public LuaBigItemStack(BigItemStack stack) {
        this.stack = stack;
    }

    @Override
    public Map<?, ?> getTableRepresentation() {
        Map details = VanillaDetailRegistries.ITEM_STACK.getDetails((Object)this.stack.stack);
        details.put("count", this.stack.count);
        return details;
    }
}
