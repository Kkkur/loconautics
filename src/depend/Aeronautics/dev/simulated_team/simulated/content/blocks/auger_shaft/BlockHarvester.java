/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 */
package dev.simulated_team.simulated.content.blocks.auger_shaft;

import dev.simulated_team.simulated.content.blocks.auger_shaft.auger_groups.AugerDistributor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public interface BlockHarvester {
    public AugerDistributor simulated$getAssociatedDistributor();

    public void simulated$setDistributor(AugerDistributor var1);

    default public ItemStack depositItemStack(BlockPos fromPos, ItemStack stack) {
        if (stack.isEmpty()) {
            return stack;
        }
        AugerDistributor group = this.simulated$getAssociatedDistributor();
        if (group != null) {
            return group.distributeItem(stack, fromPos);
        }
        return stack;
    }
}
