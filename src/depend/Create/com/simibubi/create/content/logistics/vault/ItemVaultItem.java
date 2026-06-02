/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
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
 */
package com.simibubi.create.content.logistics.vault;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem;
import com.simibubi.create.content.logistics.vault.ItemVaultBlock;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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

public class ItemVaultItem
extends BlockItem {
    public ItemVaultItem(Block p_i48527_1_, Item.Properties p_i48527_2_) {
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
            CompoundTag nbt = blockEntityData.copyTag();
            nbt.remove("Length");
            nbt.remove("Size");
            nbt.remove("Controller");
            nbt.remove("LastKnownPos");
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
        ItemStack stack = ctx.getItemInHand();
        Level world = ctx.getLevel();
        BlockState placedOnState = world.getBlockState(placedOnPos = (pos = ctx.getClickedPos()).relative(face.getOpposite()));
        if (!ItemVaultBlock.isVault(placedOnState)) {
            return;
        }
        if (SymmetryWandItem.presentInHotbar(player)) {
            return;
        }
        ItemVaultBlockEntity tankAt = (ItemVaultBlockEntity)ConnectivityHandler.partAt((BlockEntityType)AllBlockEntityTypes.ITEM_VAULT.get(), (BlockGetter)world, placedOnPos);
        if (tankAt == null) {
            return;
        }
        ItemVaultBlockEntity controllerBE = tankAt.getControllerBE();
        if (controllerBE == null) {
            return;
        }
        int width = controllerBE.radius;
        if (width == 1) {
            return;
        }
        int tanksToPlace = 0;
        Direction.Axis vaultBlockAxis = ItemVaultBlock.getVaultBlockAxis(placedOnState);
        if (vaultBlockAxis == null) {
            return;
        }
        if (face.getAxis() != vaultBlockAxis) {
            return;
        }
        Direction vaultFacing = Direction.fromAxisAndDirection((Direction.Axis)vaultBlockAxis, (Direction.AxisDirection)Direction.AxisDirection.POSITIVE);
        BlockPos blockPos = startPos = face == vaultFacing.getOpposite() ? controllerBE.getBlockPos().relative(vaultFacing.getOpposite()) : controllerBE.getBlockPos().relative(vaultFacing, controllerBE.length);
        if (VecHelper.getCoordinate((Vec3i)startPos, (Direction.Axis)vaultBlockAxis) != VecHelper.getCoordinate((Vec3i)pos, (Direction.Axis)vaultBlockAxis)) {
            return;
        }
        for (xOffset = 0; xOffset < width; ++xOffset) {
            for (zOffset = 0; zOffset < width; ++zOffset) {
                offsetPos = vaultBlockAxis == Direction.Axis.X ? startPos.offset(0, xOffset, zOffset) : startPos.offset(xOffset, zOffset, 0);
                blockState = world.getBlockState(offsetPos);
                if (ItemVaultBlock.isVault(blockState)) continue;
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
                offsetPos = vaultBlockAxis == Direction.Axis.X ? startPos.offset(0, xOffset, zOffset) : startPos.offset(xOffset, zOffset, 0);
                blockState = world.getBlockState(offsetPos);
                if (ItemVaultBlock.isVault(blockState)) continue;
                BlockPlaceContext context = BlockPlaceContext.at((BlockPlaceContext)ctx, (BlockPos)offsetPos, (Direction)face);
                player.getPersistentData().putBoolean("SilenceVaultSound", true);
                super.place(context);
                player.getPersistentData().remove("SilenceVaultSound");
            }
        }
    }
}
