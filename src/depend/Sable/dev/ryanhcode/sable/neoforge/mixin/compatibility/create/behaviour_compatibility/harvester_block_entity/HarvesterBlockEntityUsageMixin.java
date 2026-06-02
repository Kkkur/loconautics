/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour
 *  com.simibubi.create.content.contraptions.behaviour.MovementContext
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.behaviour_compatibility.harvester_block_entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.behavior_compatibility.harvester_block_entity.DummyMovementContext;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.harvester.HarvesterTicker;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={HarvesterMovementBehaviour.class})
public class HarvesterBlockEntityUsageMixin {
    @WrapOperation(method={"lambda$visitNewPosition$0"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/actors/harvester/HarvesterMovementBehaviour;collectOrDropItem(Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;Lnet/minecraft/world/item/ItemStack;)V")})
    public void sable$replaceDropItem(HarvesterMovementBehaviour instance, MovementContext movementContext, ItemStack itemStack, Operation<Void> original) {
        if (movementContext instanceof DummyMovementContext) {
            HarvesterTicker.dropItem(movementContext.world, itemStack, movementContext.localPos);
        } else {
            original.call(new Object[]{instance, movementContext, itemStack});
        }
    }
}
