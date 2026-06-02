/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.FunctionalHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.ParrotElement
 *  net.createmod.ponder.api.element.ParrotPose
 *  net.createmod.ponder.api.element.PonderElement
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.PonderSceneBuilder
 *  net.createmod.ponder.foundation.PonderSceneBuilder$PonderSpecialInstructions
 *  net.createmod.ponder.foundation.element.ElementLinkImpl
 *  net.createmod.ponder.foundation.instruction.CreateParrotInstruction
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.animal.Parrot
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.ponder;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.ponder.element.ExpandedParrotElement;
import java.util.function.Supplier;
import net.createmod.catnip.data.FunctionalHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.ParrotElement;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.element.PonderElement;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.createmod.ponder.foundation.element.ElementLinkImpl;
import net.createmod.ponder.foundation.instruction.CreateParrotInstruction;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class CreateSceneBuilder.SpecialInstructions
extends PonderSceneBuilder.PonderSpecialInstructions {
    public CreateSceneBuilder.SpecialInstructions() {
        super((PonderSceneBuilder)CreateSceneBuilder.this);
    }

    public ElementLink<ParrotElement> createBirb(Vec3 location, Supplier<? extends ParrotPose> pose) {
        ElementLinkImpl link = new ElementLinkImpl(ParrotElement.class);
        ParrotElement parrot = ExpandedParrotElement.create((Vec3)location, pose);
        CreateSceneBuilder.this.addInstruction((PonderInstruction)new CreateParrotInstruction(10, Direction.DOWN, parrot));
        CreateSceneBuilder.this.addInstruction(arg_0 -> CreateSceneBuilder.SpecialInstructions.lambda$createBirb$0(parrot, (ElementLink)link, arg_0));
        return link;
    }

    public ElementLink<ParrotElement> birbOnTurntable(BlockPos pos) {
        return this.createBirb(VecHelper.getCenterOf((Vec3i)pos), () -> new ParrotSpinOnComponentPose(pos));
    }

    public ElementLink<ParrotElement> birbOnSpinnyShaft(BlockPos pos) {
        return this.createBirb(VecHelper.getCenterOf((Vec3i)pos).add(0.0, 0.5, 0.0), () -> new ParrotSpinOnComponentPose(pos));
    }

    public void conductorBirb(ElementLink<ParrotElement> birb, boolean conductor) {
        CreateSceneBuilder.this.addInstruction(scene -> scene.resolveOptional(birb).map(FunctionalHelper.filterAndCast(ExpandedParrotElement.class)).ifPresent(expandedBirb -> expandedBirb.setConductor(conductor)));
    }

    private static /* synthetic */ void lambda$createBirb$0(ParrotElement parrot, ElementLink link, PonderScene scene) {
        scene.linkElement((PonderElement)parrot, link);
    }

    public static class ParrotSpinOnComponentPose
    extends ParrotPose {
        private final BlockPos componentPos;

        public ParrotSpinOnComponentPose(BlockPos componentPos) {
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
}
