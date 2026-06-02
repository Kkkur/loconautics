/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions.actors.trainControls;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsRenderer;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import java.util.Collection;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ControlsMovementBehaviour
implements MovementBehaviour {
    @Override
    public ItemStack canBeDisabledVia(MovementContext context) {
        return null;
    }

    @Override
    public void stopMoving(MovementContext context) {
        context.contraption.entity.stopControlling(context.localPos);
        MovementBehaviour.super.stopMoving(context);
    }

    @Override
    public void tick(MovementContext context) {
        MovementBehaviour.super.tick(context);
        if (!context.world.isClientSide) {
            return;
        }
        if (!(context.temporaryData instanceof LeverAngles)) {
            context.temporaryData = new LeverAngles();
        }
        LeverAngles angles = (LeverAngles)context.temporaryData;
        angles.steering.tickChaser();
        angles.speed.tickChaser();
        angles.equipAnimation.tickChaser();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        Object object = context.temporaryData;
        if (!(object instanceof LeverAngles)) {
            return;
        }
        LeverAngles angles = (LeverAngles)object;
        AbstractContraptionEntity entity = context.contraption.entity;
        if (!(entity instanceof CarriageContraptionEntity)) {
            return;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)entity;
        StructureTemplate.StructureBlockInfo info = context.contraption.getBlocks().get(context.localPos);
        Direction initialOrientation = cce.getInitialOrientation().getCounterClockWise();
        boolean inverted = false;
        if (info != null && info.state().hasProperty((Property)ControlsBlock.FACING)) {
            boolean bl = inverted = !((Direction)info.state().getValue((Property)ControlsBlock.FACING)).equals((Object)initialOrientation);
        }
        if (ControlsHandler.getContraption() == entity && ControlsHandler.getControlsPos() != null && ControlsHandler.getControlsPos().equals((Object)context.localPos)) {
            Collection<Integer> pressed = ControlsHandler.currentlyPressed;
            angles.equipAnimation.chase(1.0, (double)0.2f, LerpedFloat.Chaser.EXP);
            angles.steering.chase((double)((pressed.contains(3) ? 1 : 0) + (pressed.contains(2) ? -1 : 0)), (double)0.2f, LerpedFloat.Chaser.EXP);
            float f = cce.movingBackwards ^ inverted ? -1.0f : 1.0f;
            angles.speed.chase(Math.min(context.motion.length(), 0.5) * (double)f, (double)0.2f, LerpedFloat.Chaser.EXP);
        } else {
            angles.equipAnimation.chase(0.0, (double)0.2f, LerpedFloat.Chaser.EXP);
            angles.steering.chase(0.0, 0.0, LerpedFloat.Chaser.EXP);
            angles.speed.chase(0.0, 0.0, LerpedFloat.Chaser.EXP);
        }
        float pt = AnimationTickHolder.getPartialTicks((LevelAccessor)context.world);
        ControlsRenderer.render(context, renderWorld, matrices, buffer, angles.equipAnimation.getValue(pt), angles.speed.getValue(pt), angles.steering.getValue(pt));
    }

    static class LeverAngles {
        LerpedFloat steering = LerpedFloat.linear();
        LerpedFloat speed = LerpedFloat.linear();
        LerpedFloat equipAnimation = LerpedFloat.linear();

        LeverAngles() {
        }
    }
}
