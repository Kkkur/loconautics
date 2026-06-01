package com.lycoris.loconautics.core;

import org.joml.Quaterniond;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;

/**
 * Shared pose math for driving a carriage's Sable sub-level. Used both by the server tick driver (to
 * place the physics/collision body) and by the client render coupling (to draw the body exactly where
 * Create draws the carriage).
 */
public final class PhysicsTrainPose {

    private PhysicsTrainPose() {
    }

    /**
     * The carriage's world orientation as a JOML quaternion, taken from Create's own
     * {@link AbstractContraptionEntity.ContraptionRotationState} (the rotation it actually uses to draw
     * the carriage). NOTE: this is only populated on the <b>client</b> (server-side it stays zero), so
     * it is correct for rendering; the server body just stays axis-aligned, which is fine for collision.
     */
    public static Quaterniond orientationOf(CarriageContraptionEntity entity) {
        AbstractContraptionEntity.ContraptionRotationState rot = entity.getRotationState();
        return new Quaterniond()
                .rotateZYX(Math.toRadians(rot.zRotation), Math.toRadians(rot.yRotation), Math.toRadians(rot.xRotation))
                .rotateLocalY(Math.toRadians(rot.getYawOffset()))
                .normalize();
    }
}
