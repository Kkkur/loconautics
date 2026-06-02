/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblyException
extends Exception {
    private static final long serialVersionUID = 1L;
    public final Component component;
    private BlockPos position = null;

    public static void write(CompoundTag compound, HolderLookup.Provider registries, AssemblyException exception) {
        if (exception == null) {
            return;
        }
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Component", Component.Serializer.toJson((Component)exception.component, (HolderLookup.Provider)registries));
        if (exception.hasPosition()) {
            nbt.putLong("Position", exception.getPosition().asLong());
        }
        compound.put("LastException", (Tag)nbt);
    }

    public static AssemblyException read(CompoundTag compound, HolderLookup.Provider registries) {
        if (!compound.contains("LastException")) {
            return null;
        }
        CompoundTag nbt = compound.getCompound("LastException");
        String string = nbt.getString("Component");
        AssemblyException exception = new AssemblyException((Component)Component.Serializer.fromJson((String)string, (HolderLookup.Provider)registries));
        if (nbt.contains("Position")) {
            exception.position = BlockPos.of((long)nbt.getLong("Position"));
        }
        return exception;
    }

    public AssemblyException(Component component) {
        this.component = component;
    }

    public AssemblyException(String langKey, Object ... objects) {
        this((Component)CreateLang.translateDirect("gui.assembly.exception." + langKey, objects));
    }

    public static AssemblyException unmovableBlock(BlockPos pos, BlockState state) {
        AssemblyException e = new AssemblyException("unmovableBlock", pos.getX(), pos.getY(), pos.getZ(), state.getBlock().getName());
        e.position = pos;
        return e;
    }

    public static AssemblyException unloadedChunk(BlockPos pos) {
        AssemblyException e = new AssemblyException("chunkNotLoaded", pos.getX(), pos.getY(), pos.getZ());
        e.position = pos;
        return e;
    }

    public static AssemblyException structureTooLarge() {
        return new AssemblyException("structureTooLarge", AllConfigs.server().kinetics.maxBlocksMoved.get());
    }

    public static AssemblyException tooManyPistonPoles() {
        return new AssemblyException("tooManyPistonPoles", AllConfigs.server().kinetics.maxPistonPoles.get());
    }

    public static AssemblyException noPistonPoles() {
        return new AssemblyException("noPistonPoles", new Object[0]);
    }

    public static AssemblyException notEnoughSails(int sails) {
        return new AssemblyException("not_enough_sails", sails, AllConfigs.server().kinetics.minimumWindmillSails.get());
    }

    public boolean hasPosition() {
        return this.position != null;
    }

    public BlockPos getPosition() {
        return this.position;
    }
}
