/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.HitResult
 *  org.joml.Vector4f
 */
package dev.simulated_team.simulated.content.blocks.lasers.optical_sensor;

import dev.simulated_team.simulated.content.blocks.lasers.AbstractLaserRenderer;
import dev.simulated_team.simulated.content.blocks.lasers.LaserBehaviour;
import dev.simulated_team.simulated.content.blocks.lasers.optical_sensor.OpticalSensorBlock;
import dev.simulated_team.simulated.content.blocks.lasers.optical_sensor.OpticalSensorBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector4f;

public class OpticalSensorRenderer
extends AbstractLaserRenderer<OpticalSensorBlockEntity> {
    public OpticalSensorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Vector4f getColors(OpticalSensorBlockEntity blockEntity, float partialTicks) {
        Vector4f laserColor = new Vector4f(0.75f, 0.15f, 0.15f, 0.4f * blockEntity.getOpacity());
        if (((Boolean)blockEntity.getBlockState().getValue((Property)OpticalSensorBlock.POWERED)).booleanValue()) {
            laserColor.set(0.0f, 0.05f, 0.8f, 0.4f * blockEntity.getOpacity());
        }
        return laserColor;
    }

    @Override
    public float getLaserScale(LaserBehaviour laser) {
        return 0.378f;
    }

    @Override
    public HitResult getRenderedHitResult(LaserBehaviour laser) {
        return laser.getBlockHitResult();
    }
}
