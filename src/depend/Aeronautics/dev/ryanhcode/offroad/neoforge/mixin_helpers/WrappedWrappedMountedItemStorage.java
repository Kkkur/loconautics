/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.Contraption
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 */
package dev.ryanhcode.offroad.neoforge.mixin_helpers;

import com.simibubi.create.content.contraptions.Contraption;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadAttachedStorage;
import dev.ryanhcode.offroad.content.contraptions.borehead_contraption.BoreheadBearingContraption;
import java.lang.ref.WeakReference;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class WrappedWrappedMountedItemStorage
implements IItemHandlerModifiable {
    private final WeakReference<Contraption> associatedContraption;
    private final IItemHandlerModifiable wrappedInv;

    public WrappedWrappedMountedItemStorage(WeakReference<Contraption> associatedContraption, IItemHandlerModifiable wrappedInv) {
        this.associatedContraption = associatedContraption;
        this.wrappedInv = wrappedInv;
    }

    public void setStackInSlot(int i, ItemStack itemStack) {
        Contraption contraption = (Contraption)this.associatedContraption.get();
        if (contraption instanceof BoreheadBearingContraption) {
            BoreheadBearingContraption bce = (BoreheadBearingContraption)contraption;
            if (itemStack.isEmpty()) {
                ((BoreheadAttachedStorage)bce.getStorage()).invokeUnstall();
            }
        }
        this.wrappedInv.setStackInSlot(i, itemStack);
    }

    public int getSlots() {
        return this.wrappedInv.getSlots();
    }

    public ItemStack getStackInSlot(int i) {
        return this.wrappedInv.getStackInSlot(i);
    }

    public ItemStack insertItem(int i, ItemStack itemStack, boolean b) {
        return this.wrappedInv.insertItem(i, itemStack, b);
    }

    public ItemStack extractItem(int i, int i1, boolean b) {
        ItemStack extracted = this.wrappedInv.extractItem(i, i1, b);
        Contraption contraption = (Contraption)this.associatedContraption.get();
        if (contraption instanceof BoreheadBearingContraption) {
            BoreheadBearingContraption bce = (BoreheadBearingContraption)contraption;
            if (!extracted.isEmpty()) {
                ((BoreheadAttachedStorage)bce.getStorage()).invokeUnstall();
            }
        }
        return extracted;
    }

    public int getSlotLimit(int i) {
        return this.wrappedInv.getSlotLimit(i);
    }

    public boolean isItemValid(int i, ItemStack itemStack) {
        return this.wrappedInv.isItemValid(i, itemStack);
    }
}
