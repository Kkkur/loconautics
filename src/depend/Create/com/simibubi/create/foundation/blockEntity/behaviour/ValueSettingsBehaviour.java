/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface ValueSettingsBehaviour
extends ClipboardCloneable {
    public boolean testHit(Vec3 var1);

    public boolean isActive();

    default public boolean onlyVisibleWithWrench() {
        return false;
    }

    default public void newSettingHovered(ValueSettings valueSetting) {
    }

    public ValueBoxTransform getSlotPositioning();

    public ValueSettingsBoard createBoard(Player var1, BlockHitResult var2);

    public void setValueSettings(Player var1, ValueSettings var2, boolean var3);

    public ValueSettings getValueSettings();

    default public boolean acceptsValueSettings() {
        return true;
    }

    @Override
    default public String getClipboardKey() {
        return "Settings";
    }

    @Override
    default public boolean writeToClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        if (!this.acceptsValueSettings()) {
            return false;
        }
        ValueSettings valueSettings = this.getValueSettings();
        tag.putInt("Value", valueSettings.value());
        tag.putInt("Row", valueSettings.row());
        return true;
    }

    @Override
    default public boolean readFromClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (!this.acceptsValueSettings()) {
            return false;
        }
        if (!tag.contains("Value") || !tag.contains("Row")) {
            return false;
        }
        if (simulate) {
            return true;
        }
        this.setValueSettings(player, new ValueSettings(tag.getInt("Row"), tag.getInt("Value")), false);
        return true;
    }

    default public void playFeedbackSound(BlockEntityBehaviour origin) {
        origin.getWorld().playSound(null, origin.getPos(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.25f, 2.0f);
        origin.getWorld().playSound(null, origin.getPos(), (SoundEvent)SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value(), SoundSource.BLOCKS, 0.03f, 1.125f);
    }

    default public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
    }

    default public boolean bypassesInput(ItemStack mainhandItem) {
        return false;
    }

    default public boolean mayInteract(Player player) {
        return true;
    }

    default public int netId() {
        return 0;
    }

    public record ValueSettings(int row, int value) {
        public MutableComponent format() {
            return CreateLang.number(this.value).component();
        }
    }
}
