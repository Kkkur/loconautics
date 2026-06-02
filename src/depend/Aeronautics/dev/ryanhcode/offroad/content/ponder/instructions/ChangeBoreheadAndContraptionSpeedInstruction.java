/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.phys.Vec3
 */
package dev.ryanhcode.offroad.content.ponder.instructions;

import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import dev.ryanhcode.offroad.index.OffroadBlockEntityTypes;
import java.util.Optional;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;

public class ChangeBoreheadAndContraptionSpeedInstruction
extends PonderInstruction {
    private final BlockPos boreheadBearingPos;
    private final float targetSpeed;
    private final ElementLink<WorldSectionElement> contraption;
    private final RotationAxis axis;
    public boolean startSlowing;
    public boolean forcedStop;
    private boolean stopped;
    private boolean firstTick = true;

    public ChangeBoreheadAndContraptionSpeedInstruction(BlockPos boreheadBearingPos, ElementLink<WorldSectionElement> contraption, RotationAxis axis, float targetSpeed) {
        this.boreheadBearingPos = boreheadBearingPos;
        this.targetSpeed = targetSpeed;
        this.contraption = contraption;
        this.axis = axis;
    }

    public boolean isComplete() {
        if (this.forcedStop) {
            return true;
        }
        return this.stopped;
    }

    public void tick(PonderScene scene) {
        Optional be = scene.getWorld().getBlockEntity(this.boreheadBearingPos, (BlockEntityType)OffroadBlockEntityTypes.BOREHEAD_BEARING.get());
        if (be.isPresent()) {
            WorldSectionElement resolve;
            BoreheadBearingBlockEntity bhb = (BoreheadBearingBlockEntity)be.get();
            if (this.firstTick) {
                bhb.setSpeed(this.targetSpeed);
                this.firstTick = false;
            }
            if ((resolve = (WorldSectionElement)scene.resolve(this.contraption)) != null) {
                float currentRotationRate = bhb.getRotationSpeed();
                Vec3 vec3 = resolve.getAnimatedRotation();
                switch (this.axis.ordinal()) {
                    case 0: {
                        vec3 = vec3.add((double)currentRotationRate, 0.0, 0.0);
                        break;
                    }
                    case 1: {
                        vec3 = vec3.add(0.0, (double)currentRotationRate, 0.0);
                        break;
                    }
                    case 2: {
                        vec3 = vec3.add(0.0, 0.0, (double)currentRotationRate);
                        break;
                    }
                    default: {
                        vec3 = null;
                    }
                }
                resolve.setAnimatedRotation(vec3, false);
                if (this.startSlowing && resolve.getAnimatedRotation().length() < 0.1) {
                    this.stopped = true;
                }
            }
        }
    }

    public void reset(PonderScene scene) {
        super.reset(scene);
        this.firstTick = true;
        this.stopped = false;
        this.forcedStop = false;
        this.startSlowing = false;
    }

    public static enum RotationAxis {
        X,
        Y,
        Z;

    }
}
