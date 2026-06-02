/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaException
 */
package com.simibubi.create.compat.computercraft.implementation;

import com.simibubi.create.compat.computercraft.implementation.ComputerUtil;
import dan200.computercraft.api.lua.LuaException;
import java.util.List;
import java.util.Map;

private record ComputerUtil.Collection(ComputerUtil.MatchMode mode, List<?> list, Map<?, ?> map) {
    boolean isList() {
        return this.list != null;
    }

    boolean isMap() {
        return this.map != null;
    }

    static ComputerUtil.Collection of(Object o) throws LuaException {
        if (o instanceof Map) {
            Map m = (Map)o;
            ComputerUtil.MatchMode mode = ComputerUtil.MatchMode.parse(m.get("_mode"));
            m.remove("_mode");
            List<Object> lst = ComputerUtil.toOrderedList(m);
            return new ComputerUtil.Collection(mode, lst, m);
        }
        if (o instanceof List) {
            List raw = (List)o;
            return new ComputerUtil.Collection(ComputerUtil.MatchMode.CONTAINS, raw, null);
        }
        return null;
    }
}
