/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaException
 *  dan200.computercraft.api.lua.LuaTable
 *  dan200.computercraft.api.lua.LuaValues
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.compat.computercraft.implementation;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.LuaValues;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateLuaTable
implements LuaTable<Object, Object> {
    private final Map<Object, Object> map;

    public CreateLuaTable() {
        this.map = new HashMap<Object, Object>();
    }

    public CreateLuaTable(Map<?, ?> map) {
        this.map = new HashMap(map);
    }

    public boolean getBoolean(String key) throws LuaException {
        Object value = this.get(key);
        if (!(value instanceof Boolean)) {
            throw LuaValues.badField((String)key, (String)"boolean", (String)LuaValues.getType((Object)value));
        }
        return (Boolean)value;
    }

    public String getString(String key) throws LuaException {
        Object value = this.get(key);
        if (!(value instanceof String)) {
            throw LuaValues.badField((String)key, (String)"string", (String)LuaValues.getType((Object)value));
        }
        return (String)value;
    }

    public CreateLuaTable getTable(String key) throws LuaException {
        Object value = this.get(key);
        if (!(value instanceof Map)) {
            throw LuaValues.badField((String)key, (String)"table", (String)LuaValues.getType((Object)value));
        }
        return new CreateLuaTable((Map)value);
    }

    public Optional<Boolean> getOptBoolean(String key) throws LuaException {
        Object value = this.get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (!(value instanceof Boolean)) {
            throw LuaValues.badField((String)key, (String)"boolean", (String)LuaValues.getType((Object)value));
        }
        return Optional.of((Boolean)value);
    }

    public Set<String> stringKeySet() throws LuaException {
        HashSet<String> stringSet = new HashSet<String>();
        for (Object key : this.keySet()) {
            if (!(key instanceof String)) {
                throw new LuaException("key " + String.valueOf(key) + " is not string (got " + LuaValues.getType((Object)key) + ")");
            }
            stringSet.add((String)key);
        }
        return Collections.unmodifiableSet(stringSet);
    }

    public Collection<CreateLuaTable> tableValues() throws LuaException {
        ArrayList<CreateLuaTable> tables = new ArrayList<CreateLuaTable>();
        for (int i = 1; i <= this.size(); ++i) {
            Object value = this.get(i);
            if (!(value instanceof Map)) {
                throw new LuaException("value " + String.valueOf(value) + " is not table (got " + LuaValues.getType((Object)value) + ")");
            }
            tables.add(new CreateLuaTable((Map)value));
        }
        return Collections.unmodifiableList(tables);
    }

    public Map<Object, Object> getMap() {
        return this.map;
    }

    @Nullable
    public Object put(Object key, Object value) {
        return this.map.put(key, value);
    }

    public void putBoolean(String key, boolean value) {
        this.map.put(key, value);
    }

    public void putDouble(String key, double value) {
        this.map.put(key, value);
    }

    public void putString(String key, String value) {
        this.map.put(key, value);
    }

    public void putTable(String key, CreateLuaTable value) {
        this.map.put(key, value);
    }

    public void putTable(int i, CreateLuaTable value) {
        this.map.put(i, value);
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(Object o) {
        return this.map.containsKey(o);
    }

    public boolean containsValue(Object o) {
        return this.map.containsValue(o);
    }

    public Object get(Object o) {
        return this.map.get(o);
    }

    @NotNull
    public Set<Object> keySet() {
        return this.map.keySet();
    }

    @NotNull
    public Collection<Object> values() {
        return this.map.values();
    }

    @NotNull
    public Set<Map.Entry<Object, Object>> entrySet() {
        return this.map.entrySet();
    }
}
