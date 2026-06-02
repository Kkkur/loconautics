/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.FenceGateBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.behaviour;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class FenceGateMovingInteraction
extends SimpleBlockMovingInteraction {
    @Override
    protected BlockState handle(Player player, Contraption contraption, BlockPos pos, BlockState currentState) {
        SoundEvent sound = (Boolean)currentState.getValue((Property)FenceGateBlock.OPEN) != false ? SoundEvents.FENCE_GATE_CLOSE : SoundEvents.FENCE_GATE_OPEN;
        float pitch = player.level().random.nextFloat() * 0.1f + 0.9f;
        this.playSound(player, sound, pitch);
        return (BlockState)currentState.cycle((Property)FenceGateBlock.OPEN);
    }

    @Override
    protected boolean updateColliders() {
        return true;
    }
}
