/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.dispenser.DispenseItemBehavior
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.Equipable
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.DispenserBlock
 */
package com.simibubi.create.content.equipment.goggles;

import com.simibubi.create.AllItems;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class GogglesItem
extends Item
implements Equipable {
    private static final List<Predicate<Player>> IS_WEARING_PREDICATES = new ArrayList<Predicate<Player>>();

    public GogglesItem(Item.Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior((ItemLike)this, (DispenseItemBehavior)ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        return this.swapWithEquipmentSlot(this, worldIn, playerIn, handIn);
    }

    public static boolean isWearingGoggles(Player player) {
        for (Predicate<Player> predicate : IS_WEARING_PREDICATES) {
            if (!predicate.test(player)) continue;
            return true;
        }
        return false;
    }

    public static synchronized void addIsWearingPredicate(Predicate<Player> predicate) {
        IS_WEARING_PREDICATES.add(predicate);
    }

    static {
        GogglesItem.addIsWearingPredicate(player -> AllItems.GOGGLES.isIn(player.getItemBySlot(EquipmentSlot.HEAD)));
    }
}
