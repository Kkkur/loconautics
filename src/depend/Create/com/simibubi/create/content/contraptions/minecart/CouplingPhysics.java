/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.TickRateManager
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.common.extensions.IAbstractMinecartExtension
 */
package com.simibubi.create.content.contraptions.minecart;

import com.simibubi.create.content.contraptions.minecart.CouplingHandler;
import com.simibubi.create.content.contraptions.minecart.MinecartSim2020;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IAbstractMinecartExtension;

public class CouplingPhysics {
    public static void tick(Level world) {
        CouplingHandler.forEachLoadedCoupling(world, c -> CouplingPhysics.tickCoupling(world, (Couple<MinecartController>)c));
    }

    public static void tickCoupling(Level world, Couple<MinecartController> c) {
        Couple carts = c.map(MinecartController::cart);
        TickRateManager trm = world.tickRateManager();
        if (trm.isEntityFrozen((Entity)carts.getFirst()) && trm.isEntityFrozen((Entity)carts.getSecond())) {
            return;
        }
        float couplingLength = ((MinecartController)c.getFirst()).getCouplingLength(true);
        CouplingPhysics.softCollisionStep(world, (Couple<AbstractMinecart>)carts, couplingLength);
        if (world.isClientSide) {
            return;
        }
        CouplingPhysics.hardCollisionStep(world, (Couple<AbstractMinecart>)carts, couplingLength);
    }

    public static void hardCollisionStep(Level world, Couple<AbstractMinecart> carts, double couplingLength) {
        if (!MinecartSim2020.canAddMotion((AbstractMinecart)carts.get(false)) && MinecartSim2020.canAddMotion((AbstractMinecart)carts.get(true))) {
            carts = carts.swap();
        }
        Couple corrections = Couple.create(null, null);
        Couple maxSpeed = carts.map(IAbstractMinecartExtension::getMaxCartSpeedOnRail);
        boolean firstLoop = true;
        for (boolean current : new boolean[]{true, false, true}) {
            float correctionMagnitude;
            AbstractMinecart cart = (AbstractMinecart)carts.get(current);
            AbstractMinecart otherCart = (AbstractMinecart)carts.get(!current);
            float stress = (float)(couplingLength - cart.position().distanceTo(otherCart.position()));
            if (Math.abs(stress) < 0.125f) continue;
            RailShape shape = null;
            BlockPos railPosition = cart.getCurrentRailPosition();
            BlockState railState = world.getBlockState(railPosition.above());
            Block block = railState.getBlock();
            if (block instanceof BaseRailBlock) {
                BaseRailBlock block2 = (BaseRailBlock)block;
                shape = block2.getRailDirection(railState, (BlockGetter)world, railPosition, cart);
            }
            Vec3 correction = Vec3.ZERO;
            Vec3 pos = cart.position();
            Vec3 link = otherCart.position().subtract(pos);
            float f = correctionMagnitude = firstLoop ? -stress / 2.0f : -stress;
            if (!MinecartSim2020.canAddMotion(cart)) {
                correctionMagnitude /= 2.0f;
            }
            correction = shape != null ? CouplingPhysics.followLinkOnRail(link, pos, correctionMagnitude, MinecartSim2020.getRailVec(shape)).subtract(pos) : link.normalize().scale((double)correctionMagnitude);
            float maxResolveSpeed = 1.75f;
            correction = VecHelper.clamp((Vec3)correction, (float)Math.min(maxResolveSpeed, ((Float)maxSpeed.get(current)).floatValue()));
            if (corrections.get(current) == null) {
                corrections.set(current, (Object)correction);
            }
            if (shape != null) {
                MinecartSim2020.moveCartAlongTrack(cart, correction, railPosition, railState);
            } else {
                cart.move(MoverType.SELF, correction);
                cart.setDeltaMovement(cart.getDeltaMovement().scale((double)0.95f));
            }
            firstLoop = false;
        }
    }

    public static void softCollisionStep(Level world, Couple<AbstractMinecart> carts, double couplingLength) {
        Couple maxSpeed = carts.map(IAbstractMinecartExtension::getMaxCartSpeedOnRail);
        Couple canAddmotion = carts.map(MinecartSim2020::canAddMotion);
        Couple motions = carts.map(Entity::getDeltaMovement);
        motions.replaceWithParams(VecHelper::clamp, Couple.create((Object)Float.valueOf(1.0f), (Object)Float.valueOf(1.0f)));
        Couple nextPositions = carts.map(MinecartSim2020::predictNextPositionOf);
        Couple shapes = carts.mapWithContext((minecart, current) -> {
            BlockPos railPosition;
            BlockState railState;
            Block patt0$temp;
            Vec3 vec = (Vec3)nextPositions.get(current.booleanValue());
            int x = Mth.floor((double)vec.x());
            int y = Mth.floor((double)vec.y());
            int z = Mth.floor((double)vec.z());
            BlockPos pos = new BlockPos(x, y - 1, z);
            if (minecart.level().getBlockState(pos).is(BlockTags.RAILS)) {
                pos = pos.below();
            }
            if (!((patt0$temp = (railState = world.getBlockState((railPosition = pos).above())).getBlock()) instanceof BaseRailBlock)) {
                return null;
            }
            BaseRailBlock block = (BaseRailBlock)patt0$temp;
            return block.getRailDirection(railState, (BlockGetter)world, railPosition, minecart);
        });
        float futureStress = (float)(couplingLength - ((Vec3)nextPositions.getFirst()).distanceTo((Vec3)nextPositions.getSecond()));
        if (Mth.equal((double)futureStress, (double)0.0)) {
            return;
        }
        for (boolean current2 : Iterate.trueAndFalse) {
            Vec3 correction = Vec3.ZERO;
            Vec3 pos = (Vec3)nextPositions.get(current2);
            Vec3 link = ((Vec3)nextPositions.get(!current2)).subtract(pos);
            float correctionMagnitude = -futureStress / 2.0f;
            if (canAddmotion.get(current2) != canAddmotion.get(!current2)) {
                float f = correctionMagnitude = (Boolean)canAddmotion.get(current2) == false ? 0.0f : correctionMagnitude * 2.0f;
            }
            if (!((Boolean)canAddmotion.get(current2)).booleanValue()) continue;
            RailShape shape = (RailShape)shapes.get(current2);
            if (shape != null) {
                Vec3 railVec = MinecartSim2020.getRailVec(shape);
                correction = CouplingPhysics.followLinkOnRail(link, pos, correctionMagnitude, railVec).subtract(pos);
            } else {
                correction = link.normalize().scale((double)correctionMagnitude);
            }
            correction = VecHelper.clamp((Vec3)correction, (float)((Float)maxSpeed.get(current2)).floatValue());
            motions.set(current2, (Object)((Vec3)motions.get(current2)).add(correction));
        }
        motions.replaceWithParams(VecHelper::clamp, maxSpeed);
        carts.forEachWithParams(Entity::setDeltaMovement, motions);
    }

    public static Vec3 followLinkOnRail(Vec3 link, Vec3 cart, float diffToReduce, Vec3 railAxis) {
        double radius;
        Vec3 center;
        double dotProduct = railAxis.dot(link);
        if (Double.isNaN(dotProduct) || dotProduct == 0.0 || diffToReduce == 0.0f) {
            return cart;
        }
        Vec3 axis = railAxis.scale(-Math.signum(dotProduct));
        Vec3 intersectSphere = VecHelper.intersectSphere((Vec3)cart, (Vec3)axis, (Vec3)(center = cart.add(link)), (double)(radius = link.length() - (double)diffToReduce));
        if (intersectSphere == null) {
            return cart.add(VecHelper.project((Vec3)link, (Vec3)axis));
        }
        return intersectSphere;
    }
}
