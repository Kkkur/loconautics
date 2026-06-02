/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.HopperBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 */
package com.simibubi.create.content.kinetics.drill;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.drill.CobbleGenOptimisation;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class DrillBlockEntity
extends BlockBreakingKineticBlockEntity {
    private CobbleGenOptimisation.CobbleGenBlockConfiguration currentConfig;
    private BlockState currentOutput = Blocks.AIR.defaultBlockState();

    public DrillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected BlockPos getBreakingPos() {
        return this.getBlockPos().relative((Direction)this.getBlockState().getValue((Property)DrillBlock.FACING));
    }

    @Override
    public void onBlockBroken(BlockState stateToBreak) {
        if (!this.optimiseCobbleGen(stateToBreak)) {
            super.onBlockBroken(stateToBreak);
        }
    }

    public boolean optimiseCobbleGen(BlockState stateToBreak) {
        ChuteBlockEntity chute;
        ChuteBlockEntity chute2;
        DirectBeltInputBehaviour inv = BlockEntityBehaviour.get((BlockGetter)this.level, this.breakingPos.below(), DirectBeltInputBehaviour.TYPE);
        BlockEntity blockEntityBelow = this.level.getBlockEntity(this.breakingPos.below());
        BlockEntity blockEntityAbove = this.level.getBlockEntity(this.breakingPos.above());
        if (!(inv != null || blockEntityBelow instanceof HopperBlockEntity || blockEntityAbove instanceof ChuteBlockEntity && (chute2 = (ChuteBlockEntity)blockEntityAbove).getItemMotion() > 0.0f)) {
            return false;
        }
        CobbleGenOptimisation.CobbleGenBlockConfiguration config = CobbleGenOptimisation.getConfig((LevelAccessor)this.level, this.worldPosition, (Direction)this.getBlockState().getValue((Property)DrillBlock.FACING));
        if (config == null) {
            return false;
        }
        Level level = this.level;
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        ServerLevel sl = (ServerLevel)level;
        BlockPos breakingPos = this.getBreakingPos();
        if (!config.equals(this.currentConfig)) {
            this.currentConfig = config;
            this.currentOutput = CobbleGenOptimisation.determineOutput(sl, breakingPos, config);
        }
        if (this.currentOutput.isAir() || !this.currentOutput.equals(stateToBreak)) {
            return false;
        }
        if (inv != null) {
            for (ItemStack stack : Block.getDrops((BlockState)stateToBreak, (ServerLevel)sl, (BlockPos)breakingPos, null)) {
                inv.handleInsertion(stack, Direction.UP, false);
            }
        } else if (blockEntityBelow instanceof HopperBlockEntity) {
            HopperBlockEntity hbe = (HopperBlockEntity)blockEntityBelow;
            IItemHandler handler = (IItemHandler)this.level.getCapability(Capabilities.ItemHandler.BLOCK, hbe.getBlockPos(), null);
            if (handler != null) {
                for (ItemStack stack : Block.getDrops((BlockState)stateToBreak, (ServerLevel)sl, (BlockPos)breakingPos, null)) {
                    ItemHandlerHelper.insertItemStacked((IItemHandler)handler, (ItemStack)stack, (boolean)false);
                }
            }
        } else if (blockEntityAbove instanceof ChuteBlockEntity && (chute = (ChuteBlockEntity)blockEntityAbove).getItemMotion() > 0.0f) {
            for (ItemStack stack : Block.getDrops((BlockState)stateToBreak, (ServerLevel)sl, (BlockPos)breakingPos, null)) {
                if (!chute.getItem().isEmpty()) continue;
                chute.setItem(stack, 0.0f);
            }
        }
        this.level.levelEvent(2001, breakingPos, Block.getId((BlockState)stateToBreak));
        return true;
    }
}
