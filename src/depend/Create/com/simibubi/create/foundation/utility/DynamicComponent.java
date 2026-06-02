/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonParser
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.commands.CommandSource
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.ComponentUtils
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec2
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class DynamicComponent {
    private JsonElement rawCustomText;
    private Component parsedCustomText;

    public void displayCustomText(Level level, BlockPos pos, String tagElement) {
        if (tagElement == null) {
            return;
        }
        this.rawCustomText = DynamicComponent.getJsonFromString(tagElement);
        this.parsedCustomText = DynamicComponent.parseCustomText(level, pos, this.rawCustomText);
    }

    public boolean sameAs(String tagElement) {
        return this.isValid() && this.rawCustomText.equals(DynamicComponent.getJsonFromString(tagElement));
    }

    public boolean isValid() {
        return this.parsedCustomText != null && this.rawCustomText != null;
    }

    public String resolve() {
        return this.parsedCustomText.getString();
    }

    public MutableComponent get() {
        return this.parsedCustomText == null ? Component.empty() : this.parsedCustomText.copy();
    }

    public void read(BlockPos pos, CompoundTag nbt, HolderLookup.Provider registries) {
        this.rawCustomText = DynamicComponent.getJsonFromString(nbt.getString("RawCustomText"));
        try {
            this.parsedCustomText = Component.Serializer.fromJson((String)nbt.getString("CustomText"), (HolderLookup.Provider)registries);
        }
        catch (JsonParseException e) {
            this.parsedCustomText = null;
        }
    }

    public void write(CompoundTag nbt, HolderLookup.Provider registries) {
        if (!this.isValid()) {
            return;
        }
        nbt.putString("RawCustomText", this.rawCustomText.toString());
        nbt.putString("CustomText", Component.Serializer.toJson((Component)this.parsedCustomText, (HolderLookup.Provider)registries));
    }

    public static JsonElement getJsonFromString(String string) {
        try {
            return JsonParser.parseString((String)string);
        }
        catch (JsonParseException e) {
            return null;
        }
    }

    public static Component parseCustomText(Level level, BlockPos pos, JsonElement customText) {
        if (!(level instanceof ServerLevel)) {
            return null;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        try {
            return ComponentUtils.updateForEntity((CommandSourceStack)DynamicComponent.getCommandSource(serverLevel, pos), (Component)Component.Serializer.fromJson((JsonElement)customText, (HolderLookup.Provider)level.registryAccess()), null, (int)0);
        }
        catch (JsonParseException | CommandSyntaxException e) {
            return null;
        }
    }

    public static Component parseCustomText(Level level, BlockPos pos, Component customText) {
        if (!(level instanceof ServerLevel)) {
            return null;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        try {
            return ComponentUtils.updateForEntity((CommandSourceStack)DynamicComponent.getCommandSource(serverLevel, pos), (Component)customText, null, (int)0);
        }
        catch (JsonParseException | CommandSyntaxException e) {
            return null;
        }
    }

    public static CommandSourceStack getCommandSource(ServerLevel level, BlockPos pos) {
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf((Vec3i)pos), Vec2.ZERO, level, 2, "create", (Component)Component.literal((String)"create"), level.getServer(), null);
    }
}
