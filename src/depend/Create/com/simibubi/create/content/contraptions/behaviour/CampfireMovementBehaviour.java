/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.CampfireBlock
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.behaviour;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.properties.Property;

public class CampfireMovementBehaviour
implements MovementBehaviour {
    @Override
    public void tick(MovementContext context) {
        if (context.world == null || !context.world.isClientSide || context.position == null || !((Boolean)context.state.getValue((Property)CampfireBlock.LIT)).booleanValue() || context.disabled) {
            return;
        }
        RandomSource random = context.world.random;
        if (random.nextFloat() < 0.11f) {
            for (int i = 0; i < random.nextInt(2) + 2; ++i) {
                context.world.addAlwaysVisibleParticle((ParticleOptions)((Boolean)context.state.getValue((Property)CampfireBlock.SIGNAL_FIRE) != false ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE), true, context.position.x() + random.nextDouble() / (random.nextBoolean() ? 3.0 : -3.0), context.position.y() + random.nextDouble() + random.nextDouble(), context.position.z() + random.nextDouble() / (random.nextBoolean() ? 3.0 : -3.0), 0.0, 0.07, 0.0);
            }
        }
    }
}
