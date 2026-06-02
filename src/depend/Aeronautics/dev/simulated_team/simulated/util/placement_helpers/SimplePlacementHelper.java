/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.simulated_team.simulated.util.placement_helpers;

import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SimplePlacementHelper
implements IPlacementHelper {
    Predicate<ItemStack> itemPredicate;
    Predicate<BlockState> statePredicate;

    public SimplePlacementHelper(Predicate<ItemStack> itemPredicate, Predicate<BlockState> statePredicate) {
        this.itemPredicate = itemPredicate;
        this.statePredicate = statePredicate;
    }

    public Predicate<ItemStack> getItemPredicate() {
        return this.itemPredicate;
    }

    public Predicate<BlockState> getStatePredicate() {
        return this.statePredicate;
    }
}
