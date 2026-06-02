/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.DispenserBlock
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.wrapper.CombinedInvWrapper
 */
package com.simibubi.create.api.contraption.dispenser;

import com.simibubi.create.api.contraption.dispenser.DefaultMountedDispenseBehavior;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.impl.contraption.dispenser.DispenserBehaviorConverter;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

@FunctionalInterface
public interface MountedDispenseBehavior {
    public static final SimpleRegistry<Item, MountedDispenseBehavior> REGISTRY = (SimpleRegistry)Util.make(() -> {
        SimpleRegistry<Item, MountedDispenseBehavior> registry = SimpleRegistry.create();
        registry.registerProvider(DispenserBehaviorConverter.INSTANCE);
        return registry;
    });

    public ItemStack dispense(ItemStack var1, MovementContext var2, BlockPos var3);

    public static Vec3 getDispenserNormal(MovementContext ctx) {
        Direction facing = (Direction)ctx.state.getValue((Property)DispenserBlock.FACING);
        Vec3 normal = Vec3.atLowerCornerOf((Vec3i)facing.getNormal());
        return ((Vec3)ctx.rotation.apply(normal)).normalize();
    }

    public static Direction getClosestFacingDirection(Vec3 facing) {
        return Direction.getNearest((double)facing.x, (double)facing.y, (double)facing.z);
    }

    public static void placeItemInInventory(ItemStack stack, MovementContext context, BlockPos pos) {
        CombinedInvWrapper contraption;
        ItemStack newRemainder;
        ItemStack toInsert = stack.copy();
        ItemStack remainder = ItemHandlerHelper.insertItem((IItemHandler)context.getItemStorage(), (ItemStack)toInsert, (boolean)false);
        if (!remainder.isEmpty() && !(newRemainder = ItemHandlerHelper.insertItem((IItemHandler)(contraption = context.contraption.getStorage().getAllItems()), (ItemStack)remainder, (boolean)false)).isEmpty()) {
            DefaultMountedDispenseBehavior.INSTANCE.dispense(remainder, context, pos);
        }
    }
}
