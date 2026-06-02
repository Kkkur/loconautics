/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.detail.VanillaDetailRegistries
 *  dan200.computercraft.api.lua.LuaException
 *  net.createmod.catnip.data.Glob
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.compat.computercraft.implementation;

import com.simibubi.create.compat.computercraft.implementation.luaObjects.LuaComparable;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.LuaException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.createmod.catnip.data.Glob;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class ComputerUtil {
    public static int bigItemStackToLuaTableFilter(BigItemStack entry, Map<?, ?> filter) throws LuaException {
        String name;
        Object obj;
        Map details = VanillaDetailRegistries.ITEM_STACK.getDetails((Object)entry.stack);
        details.put("count", entry.count);
        if (filter.containsKey("name") && (obj = filter.get("name")) instanceof String && !(name = (String)obj).contains(":")) {
            details.put("name", "minecraft:" + name);
        }
        if (!ComputerUtil.deepEquals(new HashMap(filter), details)) {
            return 0;
        }
        return entry.count;
    }

    private static boolean deepEquals(Object fVal, Object iVal) throws LuaException {
        Map fMap;
        Object v;
        if (Objects.equals(iVal, fVal)) {
            return true;
        }
        if (iVal instanceof LuaComparable) {
            LuaComparable iStack = (LuaComparable)iVal;
            return ComputerUtil.deepEquals(fVal, iStack.getTableRepresentation());
        }
        if (fVal instanceof Number) {
            Number fn = (Number)fVal;
            if (iVal instanceof Number) {
                Number in = (Number)iVal;
                return Double.compare(fn.doubleValue(), in.doubleValue()) == 0;
            }
        }
        if (fVal instanceof Map && (v = (fMap = (Map)fVal).get("_op")) instanceof String) {
            String op = (String)v;
            if (fMap.get("value") != null) {
                Object fValue = fMap.get("value");
                switch (op) {
                    case "not": {
                        return !ComputerUtil.deepEquals(fValue, iVal);
                    }
                    case "any": 
                    case "all": {
                        String errorMsg = op + " operator requires a list of values";
                        if (!(fValue instanceof Map)) {
                            throw new LuaException(errorMsg);
                        }
                        Map valueMap = (Map)fValue;
                        List<Object> values = ComputerUtil.toOrderedList(valueMap);
                        if (values == null) {
                            throw new LuaException(errorMsg);
                        }
                        boolean isAll = op.equals("all");
                        for (Object v2 : values) {
                            boolean match = ComputerUtil.deepEquals(v2, iVal);
                            if (isAll) {
                                if (match) continue;
                                return false;
                            }
                            if (!match) continue;
                            return true;
                        }
                        return isAll;
                    }
                    case "type": {
                        if (!(fValue instanceof String)) {
                            throw new LuaException("Type operator requires a string value");
                        }
                        String type = (String)fValue;
                        if (iVal == null) {
                            return type.equals("nil");
                        }
                        return switch (type) {
                            case "nil" -> {
                                if (iVal == null) {
                                    yield true;
                                }
                                yield false;
                            }
                            case "number" -> iVal instanceof Number;
                            case "string" -> iVal instanceof String;
                            case "boolean" -> iVal instanceof Boolean;
                            case "table" -> {
                                if (iVal instanceof Map || iVal instanceof List) {
                                    yield true;
                                }
                                yield false;
                            }
                            case "list" -> iVal instanceof List;
                            case "map" -> iVal instanceof Map;
                            case "object" -> iVal instanceof LuaComparable;
                            default -> throw new LuaException("Unknown type: " + type);
                        };
                    }
                }
                if (iVal instanceof Number) {
                    Number in = (Number)iVal;
                    if (fValue instanceof Number) {
                        Number val = (Number)fValue;
                        return switch (op) {
                            case ">" -> {
                                if (in.doubleValue() > val.doubleValue()) {
                                    yield true;
                                }
                                yield false;
                            }
                            case ">=" -> {
                                if (in.doubleValue() >= val.doubleValue()) {
                                    yield true;
                                }
                                yield false;
                            }
                            case "<" -> {
                                if (in.doubleValue() < val.doubleValue()) {
                                    yield true;
                                }
                                yield false;
                            }
                            case "<=" -> {
                                if (in.doubleValue() <= val.doubleValue()) {
                                    yield true;
                                }
                                yield false;
                            }
                            case "==" -> {
                                if (in.doubleValue() == val.doubleValue()) {
                                    yield true;
                                }
                                yield false;
                            }
                            case "~=" -> {
                                if (in.doubleValue() != val.doubleValue()) {
                                    yield true;
                                }
                                yield false;
                            }
                            default -> throw new LuaException("Unknown operator: " + op);
                        };
                    }
                }
                if (iVal instanceof String) {
                    String inStr = (String)iVal;
                    if (fValue instanceof String) {
                        String fStr = (String)fValue;
                        return switch (op) {
                            case "glob" -> inStr.matches(Glob.toRegexPattern((String)fStr, (String)""));
                            case "regex" -> inStr.matches(fStr);
                            default -> throw new LuaException("Unknown operator: " + op);
                        };
                    }
                }
                throw new LuaException("Operator " + op + " not supported for type " + (fValue == null ? "null" : fValue.getClass().getSimpleName()));
            }
        }
        Collection fColl = Collection.of(fVal);
        Collection iColl = Collection.of(iVal);
        if (fColl == null || iColl == null) {
            return false;
        }
        if (iColl.isList() && fColl.isList()) {
            return ComputerUtil.matchList(fColl, iColl);
        }
        if (iColl.isMap() && fColl.isMap()) {
            return ComputerUtil.matchMap(fColl, iColl);
        }
        return false;
    }

    private static boolean matchList(Collection f, Collection i) throws LuaException {
        switch (f.mode.ordinal()) {
            case 0: {
                if (f.list.size() != i.list.size()) {
                    return false;
                }
                for (int k = 0; k < f.list.size(); ++k) {
                    if (ComputerUtil.deepEquals(f.list.get(k), i.list.get(k))) continue;
                    return false;
                }
                return true;
            }
            case 1: {
                block6: for (Object fVal : f.list) {
                    Iterator<?> it = i.list.iterator();
                    while (it.hasNext()) {
                        Object iVal = it.next();
                        if (!ComputerUtil.deepEquals(fVal, iVal)) continue;
                        it.remove();
                        continue block6;
                    }
                    return false;
                }
                return true;
            }
            case 2: {
                block8: for (Object iVal : i.list) {
                    Iterator<?> it = f.list.iterator();
                    while (it.hasNext()) {
                        Object fVal = it.next();
                        if (!ComputerUtil.deepEquals(fVal, iVal)) continue;
                        it.remove();
                        continue block8;
                    }
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private static boolean matchMap(Collection f, Collection i) throws LuaException {
        switch (f.mode.ordinal()) {
            case 0: {
                if (!f.map.keySet().equals(i.map.keySet())) {
                    return false;
                }
                for (Map.Entry<?, ?> e : f.map.entrySet()) {
                    if (ComputerUtil.deepEquals(e.getValue(), i.map.get(e.getKey()))) continue;
                    return false;
                }
                return true;
            }
            case 1: {
                for (Map.Entry<?, ?> e : f.map.entrySet()) {
                    if (i.map.containsKey(e.getKey()) && ComputerUtil.deepEquals(e.getValue(), i.map.get(e.getKey()))) continue;
                    return false;
                }
                return true;
            }
            case 2: {
                for (Map.Entry<?, ?> e : i.map.entrySet()) {
                    if (f.map.containsKey(e.getKey()) && ComputerUtil.deepEquals(f.map.get(e.getKey()), e.getValue())) continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isArrayLike(Map<?, ?> map) {
        int n = map.size();
        if (n == 0) {
            return true;
        }
        boolean[] seen = new boolean[n];
        for (Object keyObj : map.keySet()) {
            if (!(keyObj instanceof Number)) {
                return false;
            }
            int k = ((Number)keyObj).intValue() - 1;
            if ((double)k != (double)k) {
                return false;
            }
            if (k < 0 || k >= n || seen[k]) {
                return false;
            }
            seen[k] = true;
        }
        for (Object ok : (Object)seen) {
            if (ok != false) continue;
            return false;
        }
        return true;
    }

    private static List<Object> toOrderedList(Map<?, ?> m) {
        if (!ComputerUtil.isArrayLike(m)) {
            return null;
        }
        int n = m.size();
        ArrayList<Object> out = new ArrayList<Object>(Collections.nCopies(n, null));
        for (Map.Entry<?, ?> e : m.entrySet()) {
            out.set(((Number)e.getKey()).intValue() - 1, e.getValue());
        }
        return out;
    }

    public static Map<Integer, Map<String, ?>> list(IItemHandler inventory) {
        HashMap result = new HashMap();
        int size = inventory.getSlots();
        for (int i = 0; i < size; ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            result.put(i + 1, VanillaDetailRegistries.ITEM_STACK.getBasicDetails((Object)stack));
        }
        return result;
    }

    public static Map<String, ?> getItemDetail(IItemHandler inventory, int slot) throws LuaException {
        int maxSlots = inventory.getSlots();
        if (slot < 1 || slot > maxSlots) {
            throw new LuaException(String.format("Slot " + slot + " out of range, available slots between 1 and " + maxSlots, new Object[0]));
        }
        ItemStack stack = inventory.getStackInSlot(slot - 1);
        return stack.isEmpty() ? null : VanillaDetailRegistries.ITEM_STACK.getDetails((Object)stack);
    }

    public static Map<String, ?> getItemDetail(InventorySummary inventorySummary, int slot) throws LuaException {
        List<BigItemStack> stacks = inventorySummary.getStacks();
        int maxSlots = stacks.size();
        if (slot < 1 || slot > maxSlots) {
            throw new LuaException(String.format("Slot " + slot + " out of range, available slots between 1 and " + maxSlots, new Object[0]));
        }
        BigItemStack entry = stacks.get(slot - 1);
        HashMap<String, Integer> details = new HashMap<String, Integer>(VanillaDetailRegistries.ITEM_STACK.getDetails((Object)entry.stack));
        details.put("count", entry.count);
        return entry.stack.isEmpty() ? null : details;
    }

    private record Collection(MatchMode mode, List<?> list, Map<?, ?> map) {
        boolean isList() {
            return this.list != null;
        }

        boolean isMap() {
            return this.map != null;
        }

        static Collection of(Object o) throws LuaException {
            if (o instanceof Map) {
                Map m = (Map)o;
                MatchMode mode = MatchMode.parse(m.get("_mode"));
                m.remove("_mode");
                List<Object> lst = ComputerUtil.toOrderedList(m);
                return new Collection(mode, lst, m);
            }
            if (o instanceof List) {
                List raw = (List)o;
                return new Collection(MatchMode.CONTAINS, raw, null);
            }
            return null;
        }
    }

    private static enum MatchMode {
        EXACT,
        CONTAINS,
        CONTAINED;


        static MatchMode parse(Object t) throws LuaException {
            if (!(t instanceof String)) {
                return CONTAINS;
            }
            String s = (String)t;
            return switch (s.toLowerCase()) {
                case "exact" -> EXACT;
                case "contains" -> CONTAINS;
                case "contained" -> CONTAINED;
                default -> throw new LuaException("Invalid match mode: " + s + ", expected 'exact', 'contained' or 'contains'");
            };
        }
    }
}
