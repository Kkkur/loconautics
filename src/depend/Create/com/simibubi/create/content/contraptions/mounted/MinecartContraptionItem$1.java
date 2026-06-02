/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.dispenser.BlockSource
 *  net.minecraft.core.dispenser.DefaultDispenseItemBehavior
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.AbstractMinecart$Type
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.DispenserBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.mounted;

import com.simibubi.create.content.contraptions.mounted.MinecartContraptionItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

class MinecartContraptionItem.1
extends DefaultDispenseItemBehavior {
    private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

    MinecartContraptionItem.1() {
    }

    public ItemStack execute(BlockSource source, ItemStack stack) {
        double d3;
        RailShape railshape;
        Direction direction = (Direction)source.state().getValue((Property)DispenserBlock.FACING);
        ServerLevel world = source.level();
        Vec3 vec3 = source.center();
        double d0 = vec3.x() + (double)direction.getStepX() * 1.125;
        double d1 = Math.floor(vec3.y()) + (double)direction.getStepY();
        double d2 = vec3.z() + (double)direction.getStepZ() * 1.125;
        BlockPos blockpos = source.pos().relative(direction);
        BlockState blockstate = world.getBlockState(blockpos);
        RailShape railShape = railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, (BlockGetter)world, blockpos, null) : RailShape.NORTH_SOUTH;
        if (blockstate.is(BlockTags.RAILS)) {
            d3 = railshape.isAscending() ? 0.6 : 0.1;
        } else {
            if (!blockstate.isAir() || !world.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
                return this.behaviourDefaultDispenseItem.dispense(source, stack);
            }
            BlockState blockstate1 = world.getBlockState(blockpos.below());
            RailShape railshape1 = blockstate1.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate1.getBlock()).getRailDirection(blockstate1, (BlockGetter)world, blockpos.below(), null) : RailShape.NORTH_SOUTH;
            d3 = direction != Direction.DOWN && railshape1.isAscending() ? -0.4 : -0.9;
        }
        AbstractMinecart abstractminecartentity = AbstractMinecart.createMinecart((ServerLevel)world, (double)d0, (double)(d1 + d3), (double)d2, (AbstractMinecart.Type)((MinecartContraptionItem)stack.getItem()).minecartType, (ItemStack)stack, null);
        if (stack.has(DataComponents.CUSTOM_NAME)) {
            abstractminecartentity.setCustomName(stack.getHoverName());
        }
        world.addFreshEntity((Entity)abstractminecartentity);
        MinecartContraptionItem.addContraptionToMinecart((Level)world, stack, abstractminecartentity, direction);
        stack.shrink(1);
        return stack;
    }

    protected void playSound(BlockSource source) {
        source.level().levelEvent(1000, source.pos(), 0);
    }
}
