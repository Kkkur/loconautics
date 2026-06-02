/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.tuple.MutablePair
 */
package com.simibubi.create.content.contraptions.actors.contraptionControls;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionDisableActorPacket;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.contraptions.elevator.ElevatorTargetFloorPacket;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import java.util.Iterator;
import java.util.List;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.MutablePair;

public class ContraptionControlsMovingInteraction
extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        Contraption contraption = contraptionEntity.getContraption();
        MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor = contraption.getActorAt(localPos);
        if (actor == null) {
            return false;
        }
        MovementContext ctx = (MovementContext)actor.right;
        if (ctx == null) {
            return false;
        }
        if (contraption instanceof ElevatorContraption) {
            ElevatorContraption ec = (ElevatorContraption)contraption;
            return this.elevatorInteraction(localPos, contraptionEntity, ec, ctx);
        }
        if (contraptionEntity.level().isClientSide()) {
            BlockEntity blockEntity = contraption.getBlockEntityClientSide(ctx.localPos);
            if (blockEntity instanceof ContraptionControlsBlockEntity) {
                ContraptionControlsBlockEntity cbe = (ContraptionControlsBlockEntity)blockEntity;
                cbe.pressButton();
            }
            return true;
        }
        ItemStack filter = ContraptionControlsMovement.getFilter(ctx);
        boolean disable = true;
        boolean invert = false;
        List<ItemStack> disabledActors = contraption.getDisabledActors();
        Iterator<ItemStack> iterator = disabledActors.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = iterator.next();
            boolean sameFilter = ContraptionControlsMovement.isSameFilter(itemStack, filter);
            if (itemStack.isEmpty()) {
                iterator.remove();
                disable = false;
                if (sameFilter) continue;
                invert = true;
                continue;
            }
            if (!sameFilter) continue;
            iterator.remove();
            disable = false;
            break;
        }
        if (invert) {
            for (MutablePair mutablePair : contraption.getActors()) {
                ItemStack behaviourStack;
                MovementBehaviour behaviour = MovementBehaviour.REGISTRY.get((StateHolder<Block, ?>)((StructureTemplate.StructureBlockInfo)mutablePair.left).state());
                if (behaviour == null || (behaviourStack = behaviour.canBeDisabledVia((MovementContext)mutablePair.right)) == null || ContraptionControlsMovement.isSameFilter(behaviourStack, filter) || contraption.isActorTypeDisabled(behaviourStack)) continue;
                disabledActors.add(behaviourStack);
                this.send(contraptionEntity, behaviourStack, true);
            }
        }
        if (filter.isEmpty()) {
            disabledActors.clear();
        }
        if (disable) {
            disabledActors.add(filter);
        }
        contraption.setActorsActive(filter, !disable);
        ContraptionControlsBlockEntity.sendStatus(player, filter, !disable);
        this.send(contraptionEntity, filter, disable);
        AllSoundEvents.CONTROLLER_CLICK.play(player.level(), null, (Vec3i)BlockPos.containing((Position)contraptionEntity.toGlobalVector(Vec3.atCenterOf((Vec3i)localPos), 1.0f)), 1.0f, disable ? 0.8f : 1.5f);
        if (!(contraptionEntity instanceof CarriageContraptionEntity)) {
            return true;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)contraptionEntity;
        if (!filter.is(ItemTags.DOORS)) {
            return true;
        }
        Carriage carriage = cce.getCarriage();
        Train train = carriage.train;
        for (Carriage c : train.carriages) {
            CarriageContraptionEntity anyAvailableEntity = c.anyAvailableEntity();
            if (anyAvailableEntity == null) continue;
            Contraption cpt = anyAvailableEntity.getContraption();
            cpt.setActorsActive(filter, !disable);
            ContraptionControlsBlockEntity.sendStatus(player, filter, !disable);
            this.send(anyAvailableEntity, filter, disable);
        }
        return true;
    }

    private void send(AbstractContraptionEntity contraptionEntity, ItemStack filter, boolean disable) {
        CatnipServices.NETWORK.sendToClientsTrackingEntity((Entity)contraptionEntity, (CustomPacketPayload)new ContraptionDisableActorPacket(contraptionEntity.getId(), filter, !disable));
    }

    private boolean elevatorInteraction(BlockPos localPos, AbstractContraptionEntity contraptionEntity, ElevatorContraption contraption, MovementContext ctx) {
        Level level = contraptionEntity.level();
        if (!level.isClientSide()) {
            BlockPos pos = BlockPos.containing((Position)contraptionEntity.toGlobalVector(Vec3.atCenterOf((Vec3i)localPos), 1.0f));
            AllSoundEvents.CONTROLLER_CLICK.play(level, null, (Vec3i)pos, 1.0f, 1.5f);
            AllSoundEvents.CONTRAPTION_ASSEMBLE.play(level, null, (Vec3i)pos, 0.75f, 0.8f);
            return true;
        }
        Object object = ctx.temporaryData;
        if (!(object instanceof ContraptionControlsMovement.ElevatorFloorSelection)) {
            return false;
        }
        ContraptionControlsMovement.ElevatorFloorSelection efs = (ContraptionControlsMovement.ElevatorFloorSelection)object;
        if (efs.currentTargetY == contraption.clientYTarget) {
            return true;
        }
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ElevatorTargetFloorPacket(contraptionEntity, efs.currentTargetY));
        BlockEntity blockEntity = contraption.getBlockEntityClientSide(ctx.localPos);
        if (blockEntity instanceof ContraptionControlsBlockEntity) {
            ContraptionControlsBlockEntity cbe = (ContraptionControlsBlockEntity)blockEntity;
            cbe.pressButton();
        }
        return true;
    }
}
