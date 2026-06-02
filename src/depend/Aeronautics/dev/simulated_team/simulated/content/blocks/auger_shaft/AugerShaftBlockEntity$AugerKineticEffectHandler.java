/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.IRotate$SpeedLevel
 *  com.simibubi.create.content.kinetics.base.KineticBlock
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticEffectHandler
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.auger_shaft;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticEffectHandler;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerShaftBlockEntity;
import dev.simulated_team.simulated.content.particle.AugerIndicatorParticleData;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AugerShaftBlockEntity.AugerKineticEffectHandler
extends KineticEffectHandler {
    public AugerShaftBlockEntity.AugerKineticEffectHandler(KineticBlockEntity kte) {
        super(kte);
    }

    public void spawnRotationIndicators() {
        AugerShaftBlockEntity auger = AugerShaftBlockEntity.this;
        float speed = auger.getSpeed();
        if (speed == 0.0f) {
            return;
        }
        BlockState state = auger.getBlockState();
        Block block = state.getBlock();
        if (!(block instanceof KineticBlock)) {
            return;
        }
        KineticBlock kb = (KineticBlock)block;
        float radius1 = kb.getParticleInitialRadius();
        float radius2 = kb.getParticleTargetRadius();
        Direction direction = auger.flowDirection;
        BlockPos pos = auger.getBlockPos();
        Level level = auger.getLevel();
        if (direction == null || auger.speed == 0.0f) {
            return;
        }
        if (level == null) {
            return;
        }
        Vec3 vec = VecHelper.getCenterOf((Vec3i)pos);
        IRotate.SpeedLevel speedLevel = IRotate.SpeedLevel.of((float)speed);
        int color = speedLevel.getColor();
        int particleSpeed = speedLevel.getParticleSpeed();
        particleSpeed *= (int)Math.signum(speed);
        for (int i = 0; i < 3; ++i) {
            AugerIndicatorParticleData particleData = new AugerIndicatorParticleData(color, particleSpeed, radius1, radius2, (float)i / 3.0f, 10, direction);
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                serverLevel.sendParticles((ParticleOptions)particleData, vec.x, vec.y, vec.z, 20, 0.0, 0.0, 0.0, 1.0);
                continue;
            }
            for (int j = 0; j < 20; ++j) {
                level.addParticle((ParticleOptions)particleData, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
            }
        }
    }
}
