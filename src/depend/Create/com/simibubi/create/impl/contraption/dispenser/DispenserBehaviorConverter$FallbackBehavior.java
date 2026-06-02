/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.dispenser.BlockSource
 *  net.minecraft.core.dispenser.DispenseItemBehavior
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.impl.contraption.dispenser;

import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.dispenser.DefaultMountedDispenseBehavior;
import com.simibubi.create.api.contraption.dispenser.MountedDispenseBehavior;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

private static final class DispenserBehaviorConverter.FallbackBehavior
extends DefaultMountedDispenseBehavior {
    private final Item item;
    private final DispenseItemBehavior wrapped;
    private boolean hasErrored;

    private DispenserBehaviorConverter.FallbackBehavior(Item item, DispenseItemBehavior wrapped) {
        this.item = item;
        this.wrapped = wrapped;
    }

    @Override
    protected ItemStack execute(ItemStack stack, MovementContext context, BlockPos pos, Vec3 facing) {
        if (this.hasErrored) {
            return stack;
        }
        MinecraftServer server = context.world.getServer();
        ServerLevel serverLevel = server != null ? server.getLevel(context.world.dimension()) : null;
        Direction nearestFacing = MountedDispenseBehavior.getClosestFacingDirection(facing);
        BlockState state = context.state;
        if (state.hasProperty((Property)BlockStateProperties.FACING)) {
            state = (BlockState)state.setValue((Property)BlockStateProperties.FACING, (Comparable)nearestFacing);
        }
        BlockSource source = new BlockSource(serverLevel, pos, state, null);
        try {
            return this.wrapped.dispense(source, stack.copy());
        }
        catch (NullPointerException e) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey((Object)this.item);
            String message = "Error dispensing item '" + String.valueOf(itemId) + "' from contraption, not doing that anymore";
            Create.LOGGER.error(message, (Throwable)e);
            this.hasErrored = true;
            return stack;
        }
    }
}
