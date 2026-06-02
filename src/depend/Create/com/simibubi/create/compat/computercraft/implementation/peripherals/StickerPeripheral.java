/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.chassis.StickerBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public class StickerPeripheral
extends SyncedPeripheral<StickerBlockEntity> {
    public StickerPeripheral(StickerBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction
    public boolean isExtended() {
        return ((StickerBlockEntity)this.blockEntity).isBlockStateExtended();
    }

    @LuaFunction
    public boolean isAttachedToBlock() {
        return ((StickerBlockEntity)this.blockEntity).isBlockStateExtended() && ((StickerBlockEntity)this.blockEntity).isAttachedToBlock();
    }

    @LuaFunction(mainThread=true)
    public boolean extend() {
        BlockState state = ((StickerBlockEntity)this.blockEntity).getBlockState();
        if (!AllBlocks.STICKER.has(state) || ((Boolean)state.getValue((Property)StickerBlock.EXTENDED)).booleanValue()) {
            return false;
        }
        ((StickerBlockEntity)this.blockEntity).getLevel().setBlock(((StickerBlockEntity)this.blockEntity).getBlockPos(), (BlockState)state.setValue((Property)StickerBlock.EXTENDED, (Comparable)Boolean.valueOf(true)), 2);
        return true;
    }

    @LuaFunction(mainThread=true)
    public boolean retract() {
        BlockState state = ((StickerBlockEntity)this.blockEntity).getBlockState();
        if (!AllBlocks.STICKER.has(state) || !((Boolean)state.getValue((Property)StickerBlock.EXTENDED)).booleanValue()) {
            return false;
        }
        ((StickerBlockEntity)this.blockEntity).getLevel().setBlock(((StickerBlockEntity)this.blockEntity).getBlockPos(), (BlockState)state.setValue((Property)StickerBlock.EXTENDED, (Comparable)Boolean.valueOf(false)), 2);
        return true;
    }

    @LuaFunction(mainThread=true)
    public boolean toggle() {
        BlockState state = ((StickerBlockEntity)this.blockEntity).getBlockState();
        if (!AllBlocks.STICKER.has(state)) {
            return false;
        }
        boolean extended = (Boolean)state.getValue((Property)StickerBlock.EXTENDED);
        ((StickerBlockEntity)this.blockEntity).getLevel().setBlock(((StickerBlockEntity)this.blockEntity).getBlockPos(), (BlockState)state.setValue((Property)StickerBlock.EXTENDED, (Comparable)Boolean.valueOf(!extended)), 2);
        return true;
    }

    @NotNull
    public String getType() {
        return "Create_Sticker";
    }
}
