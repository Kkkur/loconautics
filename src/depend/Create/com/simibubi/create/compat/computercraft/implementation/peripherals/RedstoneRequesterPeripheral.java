/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.detail.VanillaDetailRegistries
 *  dan200.computercraft.api.lua.IArguments
 *  dan200.computercraft.api.lua.LuaException
 *  dan200.computercraft.api.lua.LuaFunction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class RedstoneRequesterPeripheral
extends SyncedPeripheral<RedstoneRequesterBlockEntity> {
    public RedstoneRequesterPeripheral(RedstoneRequesterBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction(mainThread=true)
    public final void request() throws LuaException {
        ((RedstoneRequesterBlockEntity)this.blockEntity).triggerRequest();
    }

    @LuaFunction(mainThread=true)
    public final void setRequest(IArguments arguments) throws LuaException {
        List<BigItemStack> orderStacks = this.generateOrder(arguments);
        ((RedstoneRequesterBlockEntity)this.blockEntity).encodedRequest = PackageOrderWithCrafts.simple(orderStacks);
        ((RedstoneRequesterBlockEntity)this.blockEntity).notifyUpdate();
    }

    @LuaFunction(mainThread=true)
    public final void setCraftingRequest(IArguments arguments) throws LuaException {
        int count = arguments.getInt(0);
        arguments = arguments.drop(1);
        List<BigItemStack> orderStacks = this.generateOrder(arguments);
        PackageOrder order = new PackageOrder(orderStacks);
        PackageOrderWithCrafts.CraftingEntry orderContext = new PackageOrderWithCrafts.CraftingEntry(new PackageOrder(orderStacks.stream().map(stack -> new BigItemStack(stack.stack.copyWithCount(1))).toList()), count);
        ((RedstoneRequesterBlockEntity)this.blockEntity).encodedRequest = new PackageOrderWithCrafts(order, List.of(orderContext));
        ((RedstoneRequesterBlockEntity)this.blockEntity).notifyUpdate();
    }

    @LuaFunction(mainThread=true)
    public final Map<Integer, Map<String, ?>> getRequest() throws LuaException {
        List<BigItemStack> stacks = ((RedstoneRequesterBlockEntity)this.blockEntity).encodedRequest.stacks();
        HashMap result = new HashMap();
        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack stack = stacks.get((int)i).stack;
            HashMap<String, Integer> details = new HashMap<String, Integer>(VanillaDetailRegistries.ITEM_STACK.getDetails((Object)stack));
            if (details.get("name").equals("minecraft:air")) continue;
            details.put("count", stacks.get((int)i).count);
            result.put(i + 1, details);
        }
        return result;
    }

    @LuaFunction(mainThread=true)
    public final String getConfiguration() throws LuaException {
        if (((RedstoneRequesterBlockEntity)this.blockEntity).allowPartialRequests) {
            return "allow_partial";
        }
        return "strict";
    }

    @LuaFunction(mainThread=true)
    public void setConfiguration(String config) throws LuaException {
        if (config.equals("allow_partial")) {
            ((RedstoneRequesterBlockEntity)this.blockEntity).allowPartialRequests = true;
            ((RedstoneRequesterBlockEntity)this.blockEntity).notifyUpdate();
            return;
        }
        if (config.equals("strict")) {
            ((RedstoneRequesterBlockEntity)this.blockEntity).allowPartialRequests = false;
            ((RedstoneRequesterBlockEntity)this.blockEntity).notifyUpdate();
            return;
        }
        throw new LuaException("Unknown configuration: \"" + config + "\" Possible configurations are: \"allow_partial\" and \"strict\".");
    }

    @LuaFunction(mainThread=true)
    public final void setAddress(String address) throws LuaException {
        ((RedstoneRequesterBlockEntity)this.blockEntity).encodedTargetAdress = address;
        ((RedstoneRequesterBlockEntity)this.blockEntity).notifyUpdate();
    }

    @LuaFunction(mainThread=true)
    public final String getAddress() throws LuaException {
        return ((RedstoneRequesterBlockEntity)this.blockEntity).encodedTargetAdress;
    }

    @NotNull
    public String getType() {
        return "Create_RedstoneRequester";
    }

    private List<BigItemStack> generateOrder(IArguments arguments) throws LuaException {
        ArrayList<BigItemStack> list = new ArrayList<BigItemStack>();
        for (int i = 0; i < 9; ++i) {
            if (arguments.get(i) == null) {
                list.add(new BigItemStack(ItemStack.EMPTY, 1));
                continue;
            }
            Object arg = arguments.get(i);
            if (arg instanceof String) {
                String itemName = (String)arg;
                ResourceLocation resourceLocation = ResourceLocation.tryParse((String)itemName);
                ItemLike item = (ItemLike)BuiltInRegistries.ITEM.get(resourceLocation);
                list.add(new BigItemStack(new ItemStack(item), 1));
                continue;
            }
            if (!(arg instanceof Map)) continue;
            Map itemData = (Map)arg;
            String itemName = "minecraft:air";
            if (itemData.get("name") instanceof String) {
                itemName = (String)itemData.get("name");
            }
            int count = 1;
            if (itemData.get("count") instanceof Number) {
                Object countObj = itemData.get("count");
                int n = count = countObj instanceof Number ? ((Number)countObj).intValue() : 1;
                if (count > 256) {
                    throw new LuaException("Count for item " + itemName + " exceeds 256");
                }
            }
            ResourceLocation resourceLocation = ResourceLocation.tryParse((String)itemName);
            ItemLike item = (ItemLike)BuiltInRegistries.ITEM.get(resourceLocation);
            list.add(new BigItemStack(new ItemStack(item), count));
        }
        return list;
    }
}
