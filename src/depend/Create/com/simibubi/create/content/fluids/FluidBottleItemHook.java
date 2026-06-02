/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BottleItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickItem
 */
package com.simibubi.create.content.fluids;

import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class FluidBottleItemHook
extends Item {
    private FluidBottleItemHook(Item.Properties p) {
        super(p);
    }

    @SubscribeEvent
    public static void preventWaterBottlesFromCreatesFluids(PlayerInteractEvent.RightClickItem event) {
        Player player;
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) {
            return;
        }
        if (!(itemStack.getItem() instanceof BottleItem)) {
            return;
        }
        Level world = event.getLevel();
        BlockHitResult raytraceresult = FluidBottleItemHook.getPlayerPOVHitResult((Level)world, (Player)(player = event.getEntity()), (ClipContext.Fluid)ClipContext.Fluid.SOURCE_ONLY);
        if (raytraceresult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockPos blockpos = raytraceresult.getBlockPos();
        if (!world.mayInteract(player, blockpos)) {
            return;
        }
        FluidState fluidState = world.getFluidState(blockpos);
        if (fluidState.is(FluidTags.WATER) && RegisteredObjectsHelper.getKeyOrThrow((Fluid)fluidState.getType()).getNamespace().equals("create")) {
            event.setCancellationResult(InteractionResult.PASS);
            event.setCanceled(true);
            return;
        }
    }
}
