package com.lycoris.loconautics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerMovingInteraction;
import com.simibubi.create.content.contraptions.actors.seat.SeatInteractionBehaviour;
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Suppresses deployer, simple-block, and seat interaction behaviours on physics-train
 * carriage entities. Mirrors Create-Interactive's MixinMovingInteractionBehaviours.
 *
 * Note: seat is included here because on a physics train the player should interact
 * with the seat in the sub-level, not the phantom carriage entity.
 */
@Mixin(value = {
        DeployerMovingInteraction.class,
        SeatInteractionBehaviour.class,
        SimpleBlockMovingInteraction.class
})
public abstract class MovingInteractionBehaviourMixin {

    @WrapMethod(method = "handlePlayerInteraction")
    private boolean loconautics$suppressOnPhysicsTrain(Player player, InteractionHand hand,
                                                       BlockPos localPos,
                                                       AbstractContraptionEntity entity,
                                                       Operation<Boolean> original) {
        if (entity instanceof CarriageContraptionEntity cce && cce.trainId != null) {
            boolean isPhysics;
            if (entity.level().isClientSide()) {
                isPhysics = ClientPhysicsTrainRegistry.isPhysicsTrain(cce.trainId);
            } else {
                isPhysics = PhysicsTrainRegistry.get(((ServerLevel) entity.level()).getServer())
                        .isPhysicsTrain(cce.trainId);
            }
            if (isPhysics) return false;
        }
        return original.call(player, hand, localPos, entity);
    }
}