/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.contraptions.piston;

import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalPistonRenderer
extends KineticBlockEntityRenderer<MechanicalPistonBlockEntity> {
    public MechanicalPistonRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected BlockState getRenderedBlockState(MechanicalPistonBlockEntity be) {
        return MechanicalPistonRenderer.shaft(MechanicalPistonRenderer.getRotationAxisOf(be));
    }
}
