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
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.redstoneRequester.AutoRequestData;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class TableClothShopPeripheral
extends SyncedPeripheral<TableClothBlockEntity> {
    public TableClothShopPeripheral(TableClothBlockEntity blockEntity) {
        super(blockEntity);
    }

    private void assertShop() throws LuaException {
        if (!((TableClothBlockEntity)this.blockEntity).isShop()) {
            throw new LuaException("TableCloth is not a shop!");
        }
    }

    @LuaFunction(mainThread=true)
    public final boolean isShop() {
        return ((TableClothBlockEntity)this.blockEntity).isShop();
    }

    @LuaFunction(mainThread=true)
    public final String getAddress() throws LuaException {
        this.assertShop();
        return ((TableClothBlockEntity)this.blockEntity).requestData.encodedTargetAddress();
    }

    @LuaFunction(mainThread=true)
    public final void setAddress(String address) throws LuaException {
        this.assertShop();
        AutoRequestData.Mutable mutable = new AutoRequestData.Mutable(((TableClothBlockEntity)this.blockEntity).requestData);
        mutable.encodedTargetAddress = address;
        ((TableClothBlockEntity)this.blockEntity).requestData = mutable.toImmutable();
    }

    @LuaFunction(mainThread=true)
    public final Map<String, ?> getPriceTagItem() throws LuaException {
        this.assertShop();
        return VanillaDetailRegistries.ITEM_STACK.getDetails((Object)((TableClothBlockEntity)this.blockEntity).priceTag.getFilter());
    }

    @LuaFunction(mainThread=true)
    public final void setPriceTagItem(Optional<String> itemName) throws LuaException {
        this.assertShop();
        ResourceLocation resourceLocation = ResourceLocation.tryParse((String)"minecraft:air");
        if (itemName.isPresent()) {
            resourceLocation = ResourceLocation.tryParse((String)itemName.get());
        }
        ItemLike item = (ItemLike)BuiltInRegistries.ITEM.get(resourceLocation);
        ((TableClothBlockEntity)this.blockEntity).priceTag.setFilter(new ItemStack(item));
    }

    @LuaFunction(mainThread=true)
    public final int getPriceTagCount() throws LuaException {
        this.assertShop();
        return ((TableClothBlockEntity)this.blockEntity).priceTag.count;
    }

    @LuaFunction(mainThread=true)
    public final void setPriceTagCount(Optional<Double> argument) throws LuaException {
        this.assertShop();
        ((TableClothBlockEntity)this.blockEntity).priceTag.count = argument.isPresent() ? Math.max(1, Math.min(100, argument.get().intValue())) : 1;
        ((TableClothBlockEntity)this.blockEntity).notifyUpdate();
    }

    @LuaFunction(mainThread=true)
    public final Map<Integer, Map<String, ?>> getWares() throws LuaException {
        this.assertShop();
        List<BigItemStack> wares = ((TableClothBlockEntity)this.blockEntity).requestData.encodedRequest().stacks();
        HashMap result = new HashMap();
        for (int i = 0; i < wares.size(); ++i) {
            ItemStack stack = wares.get((int)i).stack;
            HashMap<String, Integer> details = new HashMap<String, Integer>(VanillaDetailRegistries.ITEM_STACK.getDetails((Object)stack));
            details.put("count", wares.get((int)i).count);
            result.put(i + 1, details);
        }
        return result;
    }

    @LuaFunction(mainThread=true)
    public final void setWares(IArguments arguments) throws LuaException {
        if (!((TableClothBlockEntity)this.blockEntity).manuallyAddedItems.isEmpty()) {
            throw new LuaException("Tablecloth isn't empty.");
        }
        ArrayList<BigItemStack> list = new ArrayList<BigItemStack>();
        for (int i = 0; i <= 8; ++i) {
            ResourceLocation resourceLocation;
            ItemLike item;
            ItemStack itemStack;
            if (arguments.get(i) == null) continue;
            Map itemData = arguments.getTable(i);
            if (!(itemData instanceof Map)) {
                throw new LuaException("Table or nil expected for each item entry");
            }
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
            if ((itemStack = new ItemStack(item = (ItemLike)BuiltInRegistries.ITEM.get(resourceLocation = ResourceLocation.tryParse((String)itemName)))).isEmpty()) {
                throw new LuaException("Invalid item at index: " + (i + 1));
            }
            list.add(new BigItemStack(itemStack, count));
        }
        AutoRequestData.Mutable mutable = new AutoRequestData.Mutable(((TableClothBlockEntity)this.blockEntity).requestData);
        mutable.encodedRequest = PackageOrderWithCrafts.simple(list);
        ((TableClothBlockEntity)this.blockEntity).requestData = mutable.toImmutable();
        ((TableClothBlockEntity)this.blockEntity).notifyUpdate();
        ((TableClothBlockEntity)this.blockEntity).notifyShopUpdate();
    }

    public String getType() {
        return "Create_TableClothShop";
    }
}
