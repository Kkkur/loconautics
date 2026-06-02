/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.common.util.TriState
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.content.contraptions.glue;

import com.simibubi.create.content.contraptions.chassis.AbstractChassisBlock;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class SuperGlueItem
extends Item {
    @SubscribeEvent
    public static void glueItemAlwaysPlacesWhenUsed(PlayerInteractEvent.RightClickBlock event) {
        AbstractChassisBlock cb;
        BlockState blockState;
        Block block;
        if (event.getHitVec() != null && (block = (blockState = event.getLevel().getBlockState(event.getHitVec().getBlockPos())).getBlock()) instanceof AbstractChassisBlock && (cb = (AbstractChassisBlock)block).getGlueableSide(blockState, event.getFace()) != null) {
            return;
        }
        if (event.getItemStack().getItem() instanceof SuperGlueItem) {
            event.setUseBlock(TriState.FALSE);
        }
    }

    public SuperGlueItem(Item.Properties properties) {
        super(properties);
    }

    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return false;
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void spawnParticles(Level world, BlockPos pos, Direction direction, boolean fullBlock) {
        int i;
        Vec3 vec = Vec3.atLowerCornerOf((Vec3i)direction.getNormal());
        Vec3 plane = VecHelper.axisAlingedPlaneOf((Vec3)vec);
        Vec3 facePos = VecHelper.getCenterOf((Vec3i)pos).add(vec.scale(0.5));
        float distance = fullBlock ? 1.0f : 0.25f + 0.25f * (world.random.nextFloat() - 0.5f);
        plane = plane.scale((double)distance);
        ItemStack stack = new ItemStack((ItemLike)Items.SLIME_BALL);
        int n = i = fullBlock ? 40 : 15;
        while (i > 0) {
            Vec3 offset = VecHelper.rotate((Vec3)plane, (double)(360.0f * world.random.nextFloat()), (Direction.Axis)direction.getAxis());
            Vec3 motion = offset.normalize().scale(0.0625);
            if (fullBlock) {
                offset = new Vec3(Mth.clamp((double)offset.x, (double)-0.5, (double)0.5), Mth.clamp((double)offset.y, (double)-0.5, (double)0.5), Mth.clamp((double)offset.z, (double)-0.5, (double)0.5));
            }
            Vec3 particlePos = facePos.add(offset);
            world.addParticle((ParticleOptions)new ItemParticleOption(ParticleTypes.ITEM, stack), particlePos.x, particlePos.y, particlePos.z, motion.x, motion.y, motion.z);
            --i;
        }
    }
}
