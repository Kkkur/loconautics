/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.ponder.foundation.PonderSceneBuilder
 *  net.createmod.ponder.foundation.PonderSceneBuilder$PonderEffectInstructions
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.ponder;

import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotationIndicatorParticleData;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CreateSceneBuilder.EffectInstructions
extends PonderSceneBuilder.PonderEffectInstructions {
    public CreateSceneBuilder.EffectInstructions() {
        super((PonderSceneBuilder)CreateSceneBuilder.this);
    }

    public void superGlue(BlockPos pos, Direction side, boolean fullBlock) {
        CreateSceneBuilder.this.addInstruction(scene -> SuperGlueItem.spawnParticles((Level)scene.getWorld(), pos, side, fullBlock));
    }

    private void rotationIndicator(BlockPos pos, boolean direction, BlockPos displayPos) {
        CreateSceneBuilder.this.addInstruction(scene -> {
            BlockState blockState = scene.getWorld().getBlockState(pos);
            BlockEntity blockEntity = scene.getWorld().getBlockEntity(pos);
            Block patt0$temp = blockState.getBlock();
            if (!(patt0$temp instanceof KineticBlock)) {
                return;
            }
            KineticBlock kb = (KineticBlock)patt0$temp;
            if (!(blockEntity instanceof KineticBlockEntity)) {
                return;
            }
            KineticBlockEntity kbe = (KineticBlockEntity)blockEntity;
            Direction.Axis rotationAxis = kb.getRotationAxis(blockState);
            float speed = kbe.getTheoreticalSpeed();
            IRotate.SpeedLevel speedLevel = IRotate.SpeedLevel.of(speed);
            int color = direction ? (speed > 0.0f ? 15425035 : 1476519) : speedLevel.getColor();
            int particleSpeed = speedLevel.getParticleSpeed();
            particleSpeed = (int)((float)particleSpeed * Math.signum(speed));
            Vec3 location = VecHelper.getCenterOf((Vec3i)displayPos);
            RotationIndicatorParticleData particleData = new RotationIndicatorParticleData(color, particleSpeed, kb.getParticleInitialRadius(), kb.getParticleTargetRadius(), 20, rotationAxis);
            for (int i = 0; i < 20; ++i) {
                scene.getWorld().addParticle((ParticleOptions)particleData, location.x, location.y, location.z, 0.0, 0.0, 0.0);
            }
        });
    }

    public void rotationSpeedIndicator(BlockPos pos) {
        this.rotationIndicator(pos, false, pos);
    }

    public void rotationDirectionIndicator(BlockPos pos) {
        this.rotationIndicator(pos, true, pos);
    }
}
