/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.element.ParrotPose
 *  net.createmod.ponder.foundation.PonderScene
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.animal.Parrot
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.ponder;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.foundation.PonderScene;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public static class CreateSceneBuilder.SpecialInstructions.ParrotSpinOnComponentPose
extends ParrotPose {
    private final BlockPos componentPos;

    public CreateSceneBuilder.SpecialInstructions.ParrotSpinOnComponentPose(BlockPos componentPos) {
        this.componentPos = componentPos;
    }

    public void tick(PonderScene scene, Parrot entity, Vec3 location) {
        BlockEntity blockEntity = scene.getWorld().getBlockEntity(this.componentPos);
        if (!(blockEntity instanceof KineticBlockEntity)) {
            return;
        }
        float rpm = ((KineticBlockEntity)blockEntity).getSpeed();
        entity.yRotO = entity.getYRot();
        entity.setYRot(entity.getYRot() + rpm * 0.3f);
    }
}
