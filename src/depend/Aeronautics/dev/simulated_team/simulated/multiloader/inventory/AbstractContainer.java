/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.multiloader.inventory;

import dev.simulated_team.simulated.multiloader.inventory.ContainerSlot;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import dev.simulated_team.simulated.multiloader.inventory.NBTSerializable;
import java.util.List;
import java.util.Set;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface AbstractContainer
extends NBTSerializable,
Container {
    default public int commonInsert(ItemInfoWrapper info, ContainerSlot slot, int insertAmount, boolean simulate) {
        return slot.insertStack(info, insertAmount, simulate);
    }

    default public int commonExtract(ItemInfoWrapper info, ContainerSlot slot, int extractAmount, boolean simulate) {
        return slot.extractStack(info, extractAmount, simulate);
    }

    public int insertGeneral(ItemInfoWrapper var1, int var2, boolean var3);

    public ItemStack insertSlot(ItemStack var1, int var2, boolean var3);

    public int extractGeneral(ItemInfoWrapper var1, int var2, boolean var3);

    public ItemStack extractSlot(int var1, int var2, boolean var3);

    default public boolean canInsertItem(ItemInfoWrapper info, ContainerSlot slot) {
        return true;
    }

    default public boolean canExtractFromSlot(ContainerSlot slot) {
        return true;
    }

    default public void populateFields(ContainerSlot containerSlot) {
    }

    default public void onStackItemChange(ContainerSlot slot, ItemStack oldSlotStack, ItemStack newSlotStack) {
    }

    @NotNull
    default public ItemStack removeItem(int slot, int amount) {
        ItemStack item = this.getItem(slot);
        return item.split(amount);
    }

    @NotNull
    default public ItemStack removeItemNoUpdate(int slot) {
        ItemStack item = this.getItem(slot);
        this.setItem(slot, ItemStack.EMPTY);
        return item;
    }

    default public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public int getContainerSize();

    public int getMaxStackSize();

    public boolean isEmpty();

    @NotNull
    public ItemStack getItem(int var1);

    public void setItem(int var1, @NotNull ItemStack var2);

    public List<ContainerSlot> getInventoryAsList();

    public Set<ContainerSlot> getPopulatedSlots();

    public void clearContent();

    public void setChanged();
}
