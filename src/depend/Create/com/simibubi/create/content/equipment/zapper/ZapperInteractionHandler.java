/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.StairsShape
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 */
package com.simibubi.create.content.equipment.zapper;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.zapper.ZapperItem;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class ZapperInteractionHandler {
    @SubscribeEvent
    public static void leftClickingBlocksWithTheZapperSelectsTheBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        ItemStack heldItem = event.getEntity().getMainHandItem();
        if (heldItem.getItem() instanceof ZapperItem && ZapperInteractionHandler.trySelect(heldItem, event.getEntity())) {
            event.setCanceled(true);
        }
    }

    public static boolean trySelect(ItemStack stack, Player player) {
        if (player.isShiftKeyDown()) {
            return false;
        }
        Vec3 start = player.position().add(0.0, (double)player.getEyeHeight(), 0.0);
        Vec3 range = player.getLookAngle().scale((double)ZapperInteractionHandler.getRange(stack));
        BlockHitResult raytrace = player.level().clip(new ClipContext(start, start.add(range), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)player));
        BlockPos pos = raytrace.getBlockPos();
        if (pos == null) {
            return false;
        }
        player.level().destroyBlockProgress(player.getId(), pos, -1);
        BlockState newState = player.level().getBlockState(pos);
        if (BlockHelper.getRequiredItem(newState).isEmpty()) {
            return false;
        }
        if (newState.hasBlockEntity() && !AllTags.AllBlockTags.SAFE_NBT.matches(newState)) {
            return false;
        }
        if (newState.hasProperty((Property)BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            return false;
        }
        if (newState.hasProperty((Property)BlockStateProperties.ATTACHED)) {
            return false;
        }
        if (newState.hasProperty((Property)BlockStateProperties.HANGING)) {
            return false;
        }
        if (newState.hasProperty((Property)BlockStateProperties.BED_PART)) {
            return false;
        }
        if (newState.hasProperty((Property)BlockStateProperties.STAIRS_SHAPE)) {
            newState = (BlockState)newState.setValue((Property)BlockStateProperties.STAIRS_SHAPE, (Comparable)StairsShape.STRAIGHT);
        }
        if (newState.hasProperty((Property)BlockStateProperties.PERSISTENT)) {
            newState = (BlockState)newState.setValue((Property)BlockStateProperties.PERSISTENT, (Comparable)Boolean.valueOf(true));
        }
        if (newState.hasProperty((Property)BlockStateProperties.WATERLOGGED)) {
            newState = (BlockState)newState.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false));
        }
        CompoundTag data = null;
        BlockEntity blockEntity = player.level().getBlockEntity(pos);
        if (blockEntity != null) {
            data = blockEntity.saveWithFullMetadata((HolderLookup.Provider)player.registryAccess());
            data.remove("x");
            data.remove("y");
            data.remove("z");
            data.remove("id");
        }
        if (stack.has(AllDataComponents.SHAPER_BLOCK_USED) && stack.get(AllDataComponents.SHAPER_BLOCK_USED) == newState && Objects.equals(data, stack.get(AllDataComponents.SHAPER_BLOCK_DATA))) {
            return false;
        }
        stack.set(AllDataComponents.SHAPER_BLOCK_USED, (Object)newState);
        if (data == null) {
            stack.remove(AllDataComponents.SHAPER_BLOCK_DATA);
        } else {
            stack.set(AllDataComponents.SHAPER_BLOCK_DATA, (Object)data);
        }
        AllSoundEvents.CONFIRM.playOnServer(player.level(), (Vec3i)player.blockPosition());
        return true;
    }

    public static int getRange(ItemStack stack) {
        if (stack.getItem() instanceof ZapperItem) {
            return ((ZapperItem)stack.getItem()).getZappingRange(stack);
        }
        return 0;
    }
}
