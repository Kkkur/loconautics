/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  org.apache.commons.lang3.tuple.MutablePair
 */
package com.simibubi.create.content.kinetics.deployer;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.mounted.MountedContraption;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import java.util.UUID;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;

public class DeployerMovingInteraction
extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor = contraptionEntity.getContraption().getActorAt(localPos);
        if (actor == null || actor.right == null) {
            return false;
        }
        MovementContext ctx = (MovementContext)actor.right;
        ItemStack heldStack = player.getItemInHand(activeHand);
        if (heldStack.getItem().equals(AllItems.WRENCH.get())) {
            DeployerBlockEntity.Mode mode = (DeployerBlockEntity.Mode)NBTHelper.readEnum((CompoundTag)ctx.blockEntityData, (String)"Mode", DeployerBlockEntity.Mode.class);
            NBTHelper.writeEnum((CompoundTag)ctx.blockEntityData, (String)"Mode", (Enum)(mode == DeployerBlockEntity.Mode.PUNCH ? DeployerBlockEntity.Mode.USE : DeployerBlockEntity.Mode.PUNCH));
        } else {
            if (ctx.world.isClientSide) {
                return true;
            }
            DeployerFakePlayer fake = null;
            if (!(ctx.temporaryData instanceof DeployerFakePlayer) && ctx.world instanceof ServerLevel) {
                UUID owner = ctx.blockEntityData.contains("Owner") ? ctx.blockEntityData.getUUID("Owner") : null;
                DeployerFakePlayer deployerFakePlayer = new DeployerFakePlayer((ServerLevel)ctx.world, owner);
                deployerFakePlayer.onMinecartContraption = ctx.contraption instanceof MountedContraption;
                deployerFakePlayer.getInventory().load(ctx.blockEntityData.getList("Inventory", 10));
                fake = deployerFakePlayer;
                ctx.temporaryData = fake;
                ctx.blockEntityData.remove("Inventory");
            } else {
                fake = (DeployerFakePlayer)((Object)ctx.temporaryData);
            }
            if (fake == null) {
                return false;
            }
            ItemStack deployerItem = fake.getMainHandItem();
            player.setItemInHand(activeHand, deployerItem.copy());
            fake.setItemInHand(InteractionHand.MAIN_HAND, heldStack.copy());
            ctx.blockEntityData.put("HeldItem", heldStack.saveOptional((HolderLookup.Provider)player.registryAccess()));
            ctx.data.put("HeldItem", heldStack.saveOptional((HolderLookup.Provider)player.registryAccess()));
        }
        return true;
    }
}
