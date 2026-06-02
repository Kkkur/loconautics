/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType
 *  com.simibubi.create.api.registry.CreateRegistries
 *  com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem$Ammo
 *  dev.simulated_team.simulated.multiloader.inventory.ContainerSlot
 *  dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper
 *  dev.simulated_team.simulated.multiloader.inventory.SingleSlotContainer
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon;

import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon.MountedPotatoCannonBlockEntity;
import dev.simulated_team.simulated.multiloader.inventory.ContainerSlot;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import dev.simulated_team.simulated.multiloader.inventory.SingleSlotContainer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MountedPotatoCannonInventory
extends SingleSlotContainer {
    private final MountedPotatoCannonBlockEntity be;
    private PotatoCannonProjectileType cachedProjectileType;

    public MountedPotatoCannonInventory(MountedPotatoCannonBlockEntity be) {
        super(16);
        this.be = be;
    }

    public void setChanged() {
        this.be.notifyUpdate();
    }

    public void onStackItemChange(ContainerSlot slot, ItemStack oldSlotStack, ItemStack newSlotStack) {
        super.onStackItemChange(slot, oldSlotStack, newSlotStack);
        if (oldSlotStack.getItem() != newSlotStack.getItem()) {
            this.updateCachedType((HolderLookup.Provider)this.be.getLevel().registryAccess(), newSlotStack);
            this.be.resetAndUpdate();
        }
    }

    public void updateCachedType(HolderLookup.Provider registries, ItemStack itemStack) {
        this.cachedProjectileType = registries.lookupOrThrow(CreateRegistries.POTATO_PROJECTILE_TYPE).listElements().filter(ref -> ((PotatoCannonProjectileType)ref.value()).items().contains((Holder)itemStack.getItem().builtInRegistryHolder())).findFirst().map(Holder.Reference::value).orElse(null);
    }

    @Nullable
    public PotatoCannonItem.Ammo getAmmo() {
        ItemStack currentStack = this.getItem(0);
        if (this.cachedProjectileType != null) {
            return new PotatoCannonItem.Ammo(currentStack, this.cachedProjectileType);
        }
        return null;
    }

    public boolean canInsertItem(ItemInfoWrapper info) {
        return PotatoCannonProjectileType.getTypeForItem((RegistryAccess)this.be.getLevel().registryAccess(), (Item)info.type()).isPresent();
    }
}
