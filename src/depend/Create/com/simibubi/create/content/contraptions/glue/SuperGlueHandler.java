/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.levelWrappers.RayTraceLevel
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.level.BlockEvent$EntityPlaceEvent
 */
package com.simibubi.create.content.contraptions.glue;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.contraptions.glue.GlueEffectPacket;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import java.util.HashSet;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.levelWrappers.RayTraceLevel;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class SuperGlueHandler {
    @SubscribeEvent
    public static void glueListensForBlockPlacement(BlockEvent.EntityPlaceEvent event) {
        LevelAccessor world = event.getLevel();
        Entity entity = event.getEntity();
        BlockPos pos = event.getPos();
        if (entity == null || world == null || pos == null) {
            return;
        }
        if (world.isClientSide()) {
            return;
        }
        HashSet<SuperGlueEntity> cached = new HashSet<SuperGlueEntity>();
        for (Direction direction : Iterate.directions) {
            BlockPos relative = pos.relative(direction);
            if (!SuperGlueEntity.isGlued(world, pos, direction, cached) || !BlockMovementChecks.isMovementNecessary(world.getBlockState(relative), entity.level(), relative)) continue;
            CatnipServices.NETWORK.sendToClientsTrackingAndSelf(entity, (CustomPacketPayload)new GlueEffectPacket(pos, direction, true));
        }
        if (entity instanceof Player) {
            SuperGlueHandler.glueInOffHandAppliesOnBlockPlace(event, pos, (Player)entity);
        }
    }

    public static void glueInOffHandAppliesOnBlockPlace(BlockEvent.EntityPlaceEvent event, BlockPos pos, Player placer) {
        ItemStack itemstack = placer.getOffhandItem();
        AttributeInstance reachAttribute = placer.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (!AllItems.SUPER_GLUE.isIn(itemstack) || reachAttribute == null) {
            return;
        }
        if (AllItems.WRENCH.isIn(placer.getMainHandItem())) {
            return;
        }
        if (event.getPlacedAgainst() == IPlacementHelper.ID) {
            return;
        }
        double distance = reachAttribute.getValue();
        Vec3 start = placer.getEyePosition(1.0f);
        Vec3 look = placer.getViewVector(1.0f);
        Vec3 end = start.add(look.x * distance, look.y * distance, look.z * distance);
        Level world = placer.level();
        RayTraceLevel rayTraceLevel = new RayTraceLevel((LevelAccessor)world, (p, state) -> p.equals((Object)pos) ? Blocks.AIR.defaultBlockState() : state);
        BlockHitResult ray = rayTraceLevel.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)placer));
        Direction face = ray.getDirection();
        if (face == null || ray.getType() == HitResult.Type.MISS) {
            return;
        }
        BlockPos gluePos = ray.getBlockPos();
        if (!gluePos.relative(face).equals((Object)pos)) {
            event.setCanceled(true);
            return;
        }
        if (SuperGlueEntity.isGlued((LevelAccessor)world, gluePos, face, null)) {
            return;
        }
        SuperGlueEntity entity = new SuperGlueEntity(world, SuperGlueEntity.span(gluePos, gluePos.relative(face)));
        CustomData customData = (CustomData)itemstack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            EntityType.updateCustomEntityTag((Level)world, (Player)placer, (Entity)entity, (CustomData)customData);
        }
        if (SuperGlueEntity.isValidFace(world, gluePos, face)) {
            if (!world.isClientSide) {
                world.addFreshEntity((Entity)entity);
                CatnipServices.NETWORK.sendToClientsTrackingEntity((Entity)entity, (CustomPacketPayload)new GlueEffectPacket(gluePos, face, true));
            }
            itemstack.hurtAndBreak(1, (LivingEntity)placer, EquipmentSlot.MAINHAND);
        }
    }
}
