/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.IArguments
 *  dan200.computercraft.api.lua.LuaException
 *  dan200.computercraft.api.lua.LuaFunction
 *  dan200.computercraft.api.lua.LuaValues
 *  dan200.computercraft.api.lua.ObjectLuaTable
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.lua.ObjectLuaTable;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

public class DisplayLinkPeripheral
extends SyncedPeripheral<DisplayLinkBlockEntity> {
    public static final String TAG_KEY = "ComputerSourceList";
    private final AtomicInteger cursorX = new AtomicInteger();
    private final AtomicInteger cursorY = new AtomicInteger();

    public DisplayLinkPeripheral(DisplayLinkBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction
    public final void setCursorPos(int x, int y) throws LuaException {
        if (x < 1 || y < 1) {
            throw new LuaException("cursor position must be larger then 0");
        }
        this.cursorX.set(x - 1);
        this.cursorY.set(y - 1);
    }

    @LuaFunction
    public final Object[] getCursorPos() {
        return new Object[]{this.cursorX.get() + 1, this.cursorY.get() + 1};
    }

    @LuaFunction(mainThread=true)
    public final Object[] getSize() {
        ((DisplayLinkBlockEntity)this.blockEntity).updateGatheredData();
        DisplayTargetStats stats = ((DisplayLinkBlockEntity)this.blockEntity).activeTarget.provideStats(new DisplayLinkContext(((DisplayLinkBlockEntity)this.blockEntity).getLevel(), (DisplayLinkBlockEntity)this.blockEntity));
        return new Object[]{stats.maxRows(), stats.maxColumns()};
    }

    @LuaFunction
    public final boolean isColor() {
        return false;
    }

    @LuaFunction
    public final boolean isColour() {
        return false;
    }

    @LuaFunction
    public final void write(String text) {
        this.writeImpl(text);
    }

    @LuaFunction
    public final void writeBytes(IArguments args) throws LuaException {
        byte[] bytes;
        Object data = args.get(0);
        if (data instanceof String) {
            String str = (String)data;
            bytes = str.getBytes(StandardCharsets.US_ASCII);
        } else if (data instanceof Map) {
            Map map = (Map)data;
            ObjectLuaTable table = new ObjectLuaTable(map);
            bytes = new byte[table.length()];
            for (int i = 0; i < bytes.length; ++i) {
                bytes[i] = (byte)(table.getInt(i + 1) & 0xFF);
            }
        } else {
            throw LuaValues.badArgumentOf((IArguments)args, (int)0, (String)"string or table");
        }
        this.writeImpl(new String(bytes, StandardCharsets.UTF_8));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void writeImpl(String text) {
        ListTag tag = ((DisplayLinkBlockEntity)this.blockEntity).getSourceConfig().getList(TAG_KEY, 8);
        int x = this.cursorX.get();
        int y = this.cursorY.get();
        for (int i = tag.size(); i <= y; ++i) {
            tag.add((Object)StringTag.valueOf((String)""));
        }
        StringBuilder builder = new StringBuilder(tag.getString(y));
        builder.append(" ".repeat(Math.max(0, x - builder.length())));
        builder.replace(x, x + text.length(), text);
        tag.set(y, (Tag)StringTag.valueOf((String)builder.toString()));
        DisplayLinkBlockEntity displayLinkBlockEntity = (DisplayLinkBlockEntity)this.blockEntity;
        synchronized (displayLinkBlockEntity) {
            ((DisplayLinkBlockEntity)this.blockEntity).getSourceConfig().put(TAG_KEY, (Tag)tag);
        }
        this.cursorX.set(x + text.length());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @LuaFunction
    public final void clearLine() {
        ListTag tag = ((DisplayLinkBlockEntity)this.blockEntity).getSourceConfig().getList(TAG_KEY, 8);
        if (tag.size() > this.cursorY.get()) {
            tag.set(this.cursorY.get(), (Tag)StringTag.valueOf((String)""));
        }
        DisplayLinkBlockEntity displayLinkBlockEntity = (DisplayLinkBlockEntity)this.blockEntity;
        synchronized (displayLinkBlockEntity) {
            ((DisplayLinkBlockEntity)this.blockEntity).getSourceConfig().put(TAG_KEY, (Tag)tag);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @LuaFunction
    public final void clear() {
        DisplayLinkBlockEntity displayLinkBlockEntity = (DisplayLinkBlockEntity)this.blockEntity;
        synchronized (displayLinkBlockEntity) {
            ((DisplayLinkBlockEntity)this.blockEntity).getSourceConfig().put(TAG_KEY, (Tag)new ListTag());
        }
    }

    @LuaFunction(mainThread=true)
    public final void update() {
        ((DisplayLinkBlockEntity)this.blockEntity).tickSource();
    }

    @NotNull
    public String getType() {
        return "Create_DisplayLink";
    }
}
