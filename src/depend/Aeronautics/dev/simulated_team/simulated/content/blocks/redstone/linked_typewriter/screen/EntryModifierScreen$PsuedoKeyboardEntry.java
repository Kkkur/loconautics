/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler$Frequency
 *  net.minecraft.world.item.ItemStack
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen;

import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.LinkedTypewriterMenuCommon;
import net.minecraft.world.item.ItemStack;

public class EntryModifierScreen.PsuedoKeyboardEntry {
    public int glfwKeyCode = -1;
    private RedstoneLinkNetworkHandler.Frequency first;
    private RedstoneLinkNetworkHandler.Frequency second;

    public EntryModifierScreen.PsuedoKeyboardEntry keyCode(int newCode) {
        this.glfwKeyCode = newCode;
        return this;
    }

    public EntryModifierScreen.PsuedoKeyboardEntry first(RedstoneLinkNetworkHandler.Frequency newFrequency) {
        this.first = newFrequency;
        return this;
    }

    public EntryModifierScreen.PsuedoKeyboardEntry second(RedstoneLinkNetworkHandler.Frequency newFrequency) {
        this.second = newFrequency;
        return this;
    }

    public void finishModifications() {
        if (this.glfwKeyCode != -1) {
            this.first(RedstoneLinkNetworkHandler.Frequency.of((ItemStack)((LinkedTypewriterMenuCommon)EntryModifierScreen.this.parentScreen.getMenu()).ghostInventory.getStackInSlot(0)));
            this.second(RedstoneLinkNetworkHandler.Frequency.of((ItemStack)((LinkedTypewriterMenuCommon)EntryModifierScreen.this.parentScreen.getMenu()).ghostInventory.getStackInSlot(1)));
            LinkedTypewriterEntries.KeyboardEntry entry = new LinkedTypewriterEntries.KeyboardEntry(EntryModifierScreen.this.psuedoEntry.first, EntryModifierScreen.this.psuedoEntry.second, EntryModifierScreen.this.psuedoEntry.glfwKeyCode, EntryModifierScreen.this.parentScreen.clientBe.getBlockPos());
            EntryModifierScreen.this.finishedEntryCallback.accept(entry);
        } else {
            EntryModifierScreen.this.finishWithoutEntry();
        }
        EntryModifierScreen.this.disable();
    }
}
