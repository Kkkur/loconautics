/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaException
 */
package com.simibubi.create.compat.computercraft.implementation;

import dan200.computercraft.api.lua.LuaException;

private static enum ComputerUtil.MatchMode {
    EXACT,
    CONTAINS,
    CONTAINED;


    static ComputerUtil.MatchMode parse(Object t) throws LuaException {
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
