/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.bell.AbstractBellBlock;
import com.simibubi.create.content.equipment.bell.HauntedBellBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class HauntedBellBlock
extends AbstractBellBlock<HauntedBellBlockEntity> {
    public HauntedBellBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends HauntedBellBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.HAUNTED_BELL.get();
    }

    @Override
    protected boolean ring(Level world, BlockPos pos, Direction direction, Player player) {
        boolean ring = super.ring(world, pos, direction, player);
        if (ring) {
            AllAdvancements.HAUNTED_BELL.awardTo(player);
        }
        return ring;
    }

    @Override
    public Class<HauntedBellBlockEntity> getBlockEntityClass() {
        return HauntedBellBlockEntity.class;
    }

    @Override
    public void playSound(Level world, BlockPos pos) {
        AllSoundEvents.HAUNTED_BELL_USE.playOnServer(world, (Vec3i)pos, 4.0f, 1.0f);
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != this && !world.isClientSide) {
            this.withBlockEntityDo((BlockGetter)world, pos, hbte -> {
                hbte.effectTicks = 20;
                hbte.sendData();
            });
        }
    }
}
