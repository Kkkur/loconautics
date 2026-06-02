/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.content.decoration.slidingDoor;

import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;

public class SlidingDoorBlockEntity
extends SmartBlockEntity {
    LerpedFloat animation;
    int bridgeTicks;
    boolean deferUpdate;

    public SlidingDoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.animation = LerpedFloat.linear().startWithValue(SlidingDoorBlockEntity.isOpen(state) ? 1.0 : 0.0);
    }

    @Override
    public void tick() {
        if (this.deferUpdate && !this.level.isClientSide()) {
            this.deferUpdate = false;
            BlockState blockState = this.getBlockState();
            blockState.handleNeighborChanged(this.level, this.worldPosition, Blocks.AIR, this.worldPosition, false);
        }
        super.tick();
        boolean open = SlidingDoorBlockEntity.isOpen(this.getBlockState());
        boolean wasSettled = this.animation.settled();
        this.animation.chase(open ? 1.0 : 0.0, (double)0.15f, LerpedFloat.Chaser.LINEAR);
        this.animation.tickChaser();
        if (this.level.isClientSide()) {
            if (this.bridgeTicks < 2 && open) {
                ++this.bridgeTicks;
            } else if (this.bridgeTicks > 0 && !open && this.isVisible(this.getBlockState())) {
                --this.bridgeTicks;
            }
            return;
        }
        if (!open && !wasSettled && this.animation.settled() && !this.isVisible(this.getBlockState())) {
            this.showBlockModel();
        }
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(1.0);
    }

    protected boolean isVisible(BlockState state) {
        return state.getOptionalValue((Property)SlidingDoorBlock.VISIBLE).orElse(true);
    }

    protected boolean shouldRenderSpecial(BlockState state) {
        return !this.isVisible(state) || this.bridgeTicks != 0;
    }

    protected void showBlockModel() {
        this.level.setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)SlidingDoorBlock.VISIBLE, (Comparable)Boolean.valueOf(true)), 3);
        this.level.playSound(null, this.worldPosition, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.5f, 1.0f);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public static boolean isOpen(BlockState state) {
        return state.getOptionalValue((Property)DoorBlock.OPEN).orElse(false);
    }
}
