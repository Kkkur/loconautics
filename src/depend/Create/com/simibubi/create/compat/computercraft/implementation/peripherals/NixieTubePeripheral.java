/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.IArguments
 *  dan200.computercraft.api.lua.LuaException
 *  dan200.computercraft.api.lua.LuaFunction
 *  dan200.computercraft.api.lua.LuaValues
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NixieTubePeripheral
extends SyncedPeripheral<NixieTubeBlockEntity> {
    public NixieTubePeripheral(NixieTubeBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    protected void onFirstAttach() {
        super.onFirstAttach();
        Level world = ((NixieTubeBlockEntity)this.blockEntity).getLevel();
        if (world == null) {
            return;
        }
        NixieTubeBlock.walkNixies((LevelAccessor)world, ((NixieTubeBlockEntity)this.blockEntity).getBlockPos(), true, (currentPos, rowPosition) -> {
            BlockEntity patt0$temp = world.getBlockEntity(currentPos);
            if (patt0$temp instanceof NixieTubeBlockEntity) {
                NixieTubeBlockEntity ntbe = (NixieTubeBlockEntity)patt0$temp;
                ntbe.displayEmptyText((int)rowPosition);
            }
        });
    }

    @Override
    protected void onLastDetach() {
        super.onLastDetach();
        Level world = ((NixieTubeBlockEntity)this.blockEntity).getLevel();
        if (world == null) {
            return;
        }
        BlockState state = world.getBlockState(((NixieTubeBlockEntity)this.blockEntity).getBlockPos());
        if (!(state.getBlock() instanceof NixieTubeBlock)) {
            return;
        }
        NixieTubeBlock.walkNixies((LevelAccessor)world, ((NixieTubeBlockEntity)this.blockEntity).getBlockPos(), false, (currentPos, rowPosition) -> {
            BlockEntity patt0$temp = world.getBlockEntity(currentPos);
            if (patt0$temp instanceof NixieTubeBlockEntity) {
                NixieTubeBlockEntity ntbe = (NixieTubeBlockEntity)patt0$temp;
                NixieTubeBlock.updateDisplayedRedstoneValue(ntbe, state, true);
            }
        });
    }

    @LuaFunction(mainThread=true)
    public void setText(IArguments arguments) throws LuaException {
        Level level = ((NixieTubeBlockEntity)this.blockEntity).getLevel();
        if (level == null) {
            return;
        }
        ((NixieTubeBlockEntity)this.blockEntity).computerSignal = null;
        String tagElement = Component.Serializer.toJson((Component)Component.literal((String)arguments.getString(0)), (HolderLookup.Provider)level.registryAccess());
        @Nullable String colour = arguments.optString(1, null);
        BlockState state = null;
        DyeColor dye = null;
        if (colour != null) {
            state = level.getBlockState(((NixieTubeBlockEntity)this.blockEntity).getBlockPos());
            dye = (DyeColor)LuaValues.checkEnum((int)1, DyeColor.class, (String)(colour.equals("grey") ? "gray" : colour));
        }
        this.changeTextNixie(tagElement, state, dye);
    }

    @LuaFunction(mainThread=true)
    public void setTextColour(String colour) throws LuaException {
        Level world = ((NixieTubeBlockEntity)this.blockEntity).getLevel();
        if (world == null) {
            return;
        }
        BlockState state = ((NixieTubeBlockEntity)this.blockEntity).getLevel().getBlockState(((NixieTubeBlockEntity)this.blockEntity).getBlockPos());
        DyeColor dye = (DyeColor)LuaValues.checkEnum((int)1, DyeColor.class, (String)(colour.equals("grey") ? "gray" : colour));
        this.changeTextNixie(null, state, dye);
    }

    @LuaFunction(mainThread=true)
    public void setTextColor(String color) throws LuaException {
        this.setTextColour(color);
    }

    private void changeTextNixie(@Nullable String tagElement, @Nullable BlockState state, @Nullable DyeColor dye) {
        Level world = ((NixieTubeBlockEntity)this.blockEntity).getLevel();
        if (world == null) {
            return;
        }
        NixieTubeBlock.walkNixies((LevelAccessor)world, ((NixieTubeBlockEntity)this.blockEntity).getBlockPos(), true, (currentPos, rowPosition) -> {
            if (tagElement != null) {
                ((NixieTubeBlock)((NixieTubeBlockEntity)this.blockEntity).getBlockState().getBlock()).withBlockEntityDo((BlockGetter)world, (BlockPos)currentPos, be -> be.displayCustomText(tagElement, (int)rowPosition));
            }
            if (state != null && dye != null) {
                world.setBlockAndUpdate(currentPos, NixieTubeBlock.withColor(state, dye));
            }
        });
    }

    @LuaFunction(mainThread=true)
    public void setSignal(IArguments arguments) throws LuaException {
        if (arguments.optTable(0).isPresent()) {
            this.setSignal(this.signal().first, arguments.getTable(0));
        }
        if (arguments.optTable(1).isPresent()) {
            this.setSignal(this.signal().second, arguments.getTable(1));
        }
    }

    private void setSignal(NixieTubeBlockEntity.ComputerSignal.TubeDisplay display, @NotNull Map<?, ?> attrs) throws LuaException {
        if (attrs.containsKey("r")) {
            display.r = this.constrainByte("r", 0, 255, attrs.get("r"));
        }
        if (attrs.containsKey("g")) {
            display.g = this.constrainByte("g", 0, 255, attrs.get("g"));
        }
        if (attrs.containsKey("b")) {
            display.b = this.constrainByte("r", 0, 255, attrs.get("b"));
        }
        if (attrs.containsKey("glowWidth")) {
            display.glowWidth = this.constrainByte("glowWidth", 1, 4, attrs.get("glowWidth"));
        }
        if (attrs.containsKey("glowHeight")) {
            display.glowHeight = this.constrainByte("glowHeight", 1, 4, attrs.get("glowHeight"));
        }
        if (attrs.containsKey("blinkPeriod")) {
            display.blinkPeriod = this.constrainByte("blinkPeriod", 0, 255, attrs.get("blinkPeriod"));
        }
        if (attrs.containsKey("blinkOffTime")) {
            display.blinkOffTime = this.constrainByte("blinkOffTime", 0, 255, attrs.get("blinkOffTime"));
        }
        if (display.r == 0 && display.g == 0 && display.b == 0) {
            display.blinkPeriod = 0;
            display.blinkOffTime = 0;
        } else if (display.blinkPeriod == 0) {
            display.blinkPeriod = 1;
            display.blinkOffTime = 0;
        }
        ((NixieTubeBlockEntity)this.blockEntity).notifyUpdate();
    }

    private byte constrainByte(String name, int min, int max, Object rawValue) throws LuaException {
        if (!(rawValue instanceof Number)) {
            throw LuaValues.badField((String)name, (String)"number", (String)LuaValues.getType((Object)rawValue));
        }
        int value = ((Number)rawValue).intValue();
        if (value < min || value > max) {
            throw new LuaException("field " + name + " must be in range " + min + "-" + max);
        }
        return (byte)value;
    }

    private NixieTubeBlockEntity.ComputerSignal signal() {
        if (((NixieTubeBlockEntity)this.blockEntity).computerSignal == null) {
            ((NixieTubeBlockEntity)this.blockEntity).computerSignal = new NixieTubeBlockEntity.ComputerSignal();
        }
        return ((NixieTubeBlockEntity)this.blockEntity).computerSignal;
    }

    @NotNull
    public String getType() {
        return "Create_NixieTube";
    }
}
