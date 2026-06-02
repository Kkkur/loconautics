/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.equipment.clipboard;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public interface ClipboardCloneable {
    public String getClipboardKey();

    public boolean writeToClipboard(@NotNull HolderLookup.Provider var1, CompoundTag var2, Direction var3);

    public boolean readFromClipboard(@NotNull HolderLookup.Provider var1, CompoundTag var2, Player var3, Direction var4, boolean var5);
}
