/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.event.level.BlockEvent$BreakEvent
 */
package com.simibubi.create.content.equipment.wrench;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public interface IWrenchable {
    default public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState rotated = this.getRotatedBlockState(state, context.getClickedFace());
        if (!rotated.canSurvive((LevelReader)level, context.getClickedPos())) {
            return InteractionResult.PASS;
        }
        KineticBlockEntity.switchToBlockState(level, pos, this.updateAfterWrenched(rotated, context));
        if (level.getBlockState(pos) != state) {
            IWrenchable.playRotateSound(level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    default public BlockState updateAfterWrenched(BlockState newState, UseOnContext context) {
        return Block.updateFromNeighbourShapes((BlockState)newState, (LevelAccessor)context.getLevel(), (BlockPos)context.getClickedPos());
    }

    default public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (!(world instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel serverLevel = (ServerLevel)world;
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), player);
        NeoForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            return InteractionResult.SUCCESS;
        }
        if (player != null && !player.isCreative()) {
            Block.getDrops((BlockState)state, (ServerLevel)serverLevel, (BlockPos)pos, (BlockEntity)world.getBlockEntity(pos), (Entity)player, (ItemStack)context.getItemInHand()).forEach(itemStack -> player.getInventory().placeItemBackInInventory(itemStack));
        }
        state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
        world.destroyBlock(pos, false);
        IWrenchable.playRemoveSound(world, pos);
        return InteractionResult.SUCCESS;
    }

    public static void playRemoveSound(Level level, BlockPos pos) {
        AllSoundEvents.WRENCH_REMOVE.playOnServer(level, (Vec3i)pos, 1.0f, Create.RANDOM.nextFloat() * 0.5f + 0.5f);
    }

    public static void playRotateSound(Level level, BlockPos pos) {
        AllSoundEvents.WRENCH_ROTATE.playOnServer(level, (Vec3i)pos, 1.0f, Create.RANDOM.nextFloat() + 0.5f);
    }

    default public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        BlockState newState = originalState;
        if (targetedFace.getAxis() == Direction.Axis.Y) {
            if (originalState.hasProperty(HorizontalAxisKineticBlock.HORIZONTAL_AXIS)) {
                return (BlockState)originalState.setValue(HorizontalAxisKineticBlock.HORIZONTAL_AXIS, (Comparable)VoxelShaper.axisAsFace((Direction.Axis)((Direction.Axis)originalState.getValue(HorizontalAxisKineticBlock.HORIZONTAL_AXIS))).getClockWise(targetedFace.getAxis()).getAxis());
            }
            if (originalState.hasProperty(HorizontalKineticBlock.HORIZONTAL_FACING)) {
                return (BlockState)originalState.setValue(HorizontalKineticBlock.HORIZONTAL_FACING, (Comparable)((Direction)originalState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING)).getClockWise(targetedFace.getAxis()));
            }
        }
        if (originalState.hasProperty(RotatedPillarKineticBlock.AXIS)) {
            return (BlockState)originalState.setValue(RotatedPillarKineticBlock.AXIS, (Comparable)VoxelShaper.axisAsFace((Direction.Axis)((Direction.Axis)originalState.getValue(RotatedPillarKineticBlock.AXIS))).getClockWise(targetedFace.getAxis()).getAxis());
        }
        if (!originalState.hasProperty((Property)DirectionalKineticBlock.FACING)) {
            return originalState;
        }
        Direction stateFacing = (Direction)originalState.getValue((Property)DirectionalKineticBlock.FACING);
        if (stateFacing.getAxis().equals((Object)targetedFace.getAxis())) {
            if (originalState.hasProperty((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE)) {
                return (BlockState)originalState.cycle((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
            }
            return originalState;
        }
        do {
            newState = (BlockState)newState.setValue((Property)DirectionalKineticBlock.FACING, (Comparable)((Direction)newState.getValue((Property)DirectionalKineticBlock.FACING)).getClockWise(targetedFace.getAxis()));
            if (targetedFace.getAxis() != Direction.Axis.Y || !newState.hasProperty((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE)) continue;
            newState = (BlockState)newState.cycle((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
        } while (((Direction)newState.getValue((Property)DirectionalKineticBlock.FACING)).getAxis().equals((Object)targetedFace.getAxis()));
        return newState;
    }
}
