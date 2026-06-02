/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.tank;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidTankItem
extends BlockItem {
    public FluidTankItem(Block p_i48527_1_, Item.Properties p_i48527_2_) {
        super(p_i48527_1_, p_i48527_2_);
    }

    public InteractionResult place(BlockPlaceContext ctx) {
        InteractionResult initialResult = super.place(ctx);
        if (!initialResult.consumesAction()) {
            return initialResult;
        }
        this.tryMultiPlace(ctx);
        return initialResult;
    }

    protected boolean updateCustomBlockEntityTag(BlockPos blockPos, Level level, Player player, ItemStack itemStack, BlockState blockState) {
        MinecraftServer minecraftserver = level.getServer();
        if (minecraftserver == null) {
            return false;
        }
        CustomData blockEntityData = (CustomData)itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData != null) {
            FluidStack fluid;
            CompoundTag nbt = blockEntityData.copyTag();
            nbt.remove("Luminosity");
            nbt.remove("Size");
            nbt.remove("Height");
            nbt.remove("Controller");
            nbt.remove("LastKnownPos");
            if (nbt.contains("TankContent") && !(fluid = FluidStack.parseOptional((HolderLookup.Provider)minecraftserver.registryAccess(), (CompoundTag)nbt.getCompound("TankContent"))).isEmpty()) {
                fluid.setAmount(Math.min(FluidTankBlockEntity.getCapacityMultiplier(), fluid.getAmount()));
                nbt.put("TankContent", fluid.saveOptional((HolderLookup.Provider)minecraftserver.registryAccess()));
            }
            BlockEntity.addEntityType((CompoundTag)nbt, ((IBE)this.getBlock()).getBlockEntityType());
            itemStack.set(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.of((CompoundTag)nbt));
        }
        return super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState);
    }

    private void tryMultiPlace(BlockPlaceContext ctx) {
        BlockState blockState;
        BlockPos offsetPos;
        int zOffset;
        int xOffset;
        BlockPos startPos;
        BlockPos pos;
        BlockPos placedOnPos;
        Player player = ctx.getPlayer();
        if (player == null) {
            return;
        }
        if (player.isShiftKeyDown()) {
            return;
        }
        Direction face = ctx.getClickedFace();
        if (!face.getAxis().isVertical()) {
            return;
        }
        ItemStack stack = ctx.getItemInHand();
        Level world = ctx.getLevel();
        BlockState placedOnState = world.getBlockState(placedOnPos = (pos = ctx.getClickedPos()).relative(face.getOpposite()));
        if (!FluidTankBlock.isTank(placedOnState)) {
            return;
        }
        if (SymmetryWandItem.presentInHotbar(player)) {
            return;
        }
        boolean creative = this.getBlock().equals(AllBlocks.CREATIVE_FLUID_TANK.get());
        FluidTankBlockEntity tankAt = (FluidTankBlockEntity)ConnectivityHandler.partAt(creative ? (BlockEntityType)AllBlockEntityTypes.CREATIVE_FLUID_TANK.get() : (BlockEntityType)AllBlockEntityTypes.FLUID_TANK.get(), (BlockGetter)world, placedOnPos);
        if (tankAt == null) {
            return;
        }
        FluidTankBlockEntity controllerBE = tankAt.getControllerBE();
        if (controllerBE == null) {
            return;
        }
        int width = controllerBE.width;
        if (width == 1) {
            return;
        }
        int tanksToPlace = 0;
        BlockPos blockPos = startPos = face == Direction.DOWN ? controllerBE.getBlockPos().below() : controllerBE.getBlockPos().above(controllerBE.height);
        if (startPos.getY() != pos.getY()) {
            return;
        }
        for (xOffset = 0; xOffset < width; ++xOffset) {
            for (zOffset = 0; zOffset < width; ++zOffset) {
                offsetPos = startPos.offset(xOffset, 0, zOffset);
                blockState = world.getBlockState(offsetPos);
                if (FluidTankBlock.isTank(blockState)) continue;
                if (!blockState.canBeReplaced()) {
                    return;
                }
                ++tanksToPlace;
            }
        }
        if (!player.isCreative() && stack.getCount() < tanksToPlace) {
            return;
        }
        for (xOffset = 0; xOffset < width; ++xOffset) {
            for (zOffset = 0; zOffset < width; ++zOffset) {
                offsetPos = startPos.offset(xOffset, 0, zOffset);
                blockState = world.getBlockState(offsetPos);
                if (FluidTankBlock.isTank(blockState)) continue;
                BlockPlaceContext context = BlockPlaceContext.at((BlockPlaceContext)ctx, (BlockPos)offsetPos, (Direction)face);
                player.getPersistentData().putBoolean("SilenceTankSound", true);
                super.place(context);
                player.getPersistentData().remove("SilenceTankSound");
            }
        }
    }
}
