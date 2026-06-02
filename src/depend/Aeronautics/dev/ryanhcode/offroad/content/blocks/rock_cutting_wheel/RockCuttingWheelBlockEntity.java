/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 */
package dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;

public class RockCuttingWheelBlockEntity
extends SmartBlockEntity {
    private final LerpedFloat angle = LerpedFloat.angular();
    private int maxDuration;
    private int duration;
    private float manuallyAnimatedSpeed;

    public RockCuttingWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        super.tick();
        if (this.isVirtual()) {
            if (this.duration + 2 > this.maxDuration) {
                this.manuallyAnimatedSpeed = 0.0f;
            } else {
                ++this.duration;
            }
            this.angle.chase((double)(this.angle.getValue() + this.manuallyAnimatedSpeed), 1.0, LerpedFloat.Chaser.EXP);
            this.angle.tickChaser();
        }
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    protected AABB createRenderBoundingBox() {
        return AABB.encapsulatingFullBlocks((BlockPos)this.worldPosition, (BlockPos)this.worldPosition.offset(((Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING)).getNormal()));
    }

    public float getAnimatedSpeed(float partialTicks) {
        return this.angle.getValue(partialTicks);
    }

    public void setAnimatedSpeed(float speed) {
        this.manuallyAnimatedSpeed = speed;
    }

    public void setMaxDuration(int duration) {
        this.maxDuration = duration;
        this.duration = 0;
    }
}
