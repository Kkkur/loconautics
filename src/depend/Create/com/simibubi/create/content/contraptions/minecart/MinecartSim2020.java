/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.MinecartFurnace
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.minecart;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllAttachmentTypes;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import java.util.Map;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public class MinecartSim2020 {
    private static final Map<RailShape, Pair<Vec3i, Vec3i>> MATRIX = (Map)Util.make((Object)Maps.newEnumMap(RailShape.class), map -> {
        Vec3i west = Direction.WEST.getNormal();
        Vec3i east = Direction.EAST.getNormal();
        Vec3i north = Direction.NORTH.getNormal();
        Vec3i south = Direction.SOUTH.getNormal();
        map.put(RailShape.NORTH_SOUTH, Pair.of((Object)north, (Object)south));
        map.put(RailShape.EAST_WEST, Pair.of((Object)west, (Object)east));
        map.put(RailShape.ASCENDING_EAST, Pair.of((Object)west.below(), (Object)east));
        map.put(RailShape.ASCENDING_WEST, Pair.of((Object)west, (Object)east.below()));
        map.put(RailShape.ASCENDING_NORTH, Pair.of((Object)north, (Object)south.below()));
        map.put(RailShape.ASCENDING_SOUTH, Pair.of((Object)north.below(), (Object)south));
        map.put(RailShape.SOUTH_EAST, Pair.of((Object)south, (Object)east));
        map.put(RailShape.SOUTH_WEST, Pair.of((Object)south, (Object)west));
        map.put(RailShape.NORTH_WEST, Pair.of((Object)north, (Object)west));
        map.put(RailShape.NORTH_EAST, Pair.of((Object)north, (Object)east));
    });

    public static Vec3 predictNextPositionOf(AbstractMinecart cart) {
        Vec3 position = cart.position();
        Vec3 motion = VecHelper.clamp((Vec3)cart.getDeltaMovement(), (float)1.0f);
        return position.add(motion);
    }

    public static boolean canAddMotion(AbstractMinecart c) {
        if (c instanceof MinecartFurnace) {
            MinecartFurnace furnace = (MinecartFurnace)c;
            return Mth.equal((double)furnace.xPush, (double)0.0) && Mth.equal((double)furnace.zPush, (double)0.0);
        }
        MinecartController controller = (MinecartController)c.getData(AllAttachmentTypes.MINECART_CONTROLLER);
        if (controller.isPresent()) {
            return !controller.isStalled();
        }
        return true;
    }

    public static void moveCartAlongTrack(AbstractMinecart cart, Vec3 forcedMovement, BlockPos cartPos, BlockState trackState) {
        double d14;
        if (forcedMovement.equals((Object)Vec3.ZERO)) {
            return;
        }
        Vec3 previousMotion = cart.getDeltaMovement();
        cart.fallDistance = 0.0f;
        double x = cart.getX();
        double y = cart.getY();
        double z = cart.getZ();
        double actualX = x;
        double actualY = y;
        double actualZ = z;
        Vec3 actualVec = cart.getPos(actualX, actualY, actualZ);
        actualY = cartPos.getY() + 1;
        BaseRailBlock abstractrailblock = (BaseRailBlock)trackState.getBlock();
        RailShape railshape = abstractrailblock.getRailDirection(trackState, (BlockGetter)cart.level(), cartPos, cart);
        switch (railshape) {
            case ASCENDING_EAST: {
                forcedMovement = forcedMovement.add(-1.0 * cart.getSlopeAdjustment(), 0.0, 0.0);
                actualY += 1.0;
                break;
            }
            case ASCENDING_WEST: {
                forcedMovement = forcedMovement.add(cart.getSlopeAdjustment(), 0.0, 0.0);
                actualY += 1.0;
                break;
            }
            case ASCENDING_NORTH: {
                forcedMovement = forcedMovement.add(0.0, 0.0, cart.getSlopeAdjustment());
                actualY += 1.0;
                break;
            }
            case ASCENDING_SOUTH: {
                forcedMovement = forcedMovement.add(0.0, 0.0, -1.0 * cart.getSlopeAdjustment());
                actualY += 1.0;
            }
        }
        Pair<Vec3i, Vec3i> pair = MATRIX.get(railshape);
        Vec3i Vector3i = (Vec3i)pair.getFirst();
        Vec3i Vector3i1 = (Vec3i)pair.getSecond();
        double d4 = Vector3i1.getX() - Vector3i.getX();
        double d5 = Vector3i1.getZ() - Vector3i.getZ();
        double d7 = forcedMovement.x * d4 + forcedMovement.z * d5;
        if (d7 < 0.0) {
            d4 = -d4;
            d5 = -d5;
        }
        double d23 = (double)cartPos.getX() + 0.5 + (double)Vector3i.getX() * 0.5;
        double d10 = (double)cartPos.getZ() + 0.5 + (double)Vector3i.getZ() * 0.5;
        double d12 = (double)cartPos.getX() + 0.5 + (double)Vector3i1.getX() * 0.5;
        double d13 = (double)cartPos.getZ() + 0.5 + (double)Vector3i1.getZ() * 0.5;
        d4 = d12 - d23;
        d5 = d13 - d10;
        if (d4 == 0.0) {
            d14 = actualZ - (double)cartPos.getZ();
        } else if (d5 == 0.0) {
            d14 = actualX - (double)cartPos.getX();
        } else {
            double d15 = actualX - d23;
            double d16 = actualZ - d10;
            d14 = (d15 * d4 + d16 * d5) * 2.0;
        }
        actualX = d23 + d4 * d14;
        actualZ = d10 + d5 * d14;
        cart.setPos(actualX, actualY, actualZ);
        cart.setDeltaMovement(forcedMovement);
        cart.moveMinecartOnRail(cartPos);
        x = cart.getX();
        y = cart.getY();
        z = cart.getZ();
        if (Vector3i.getY() != 0 && Mth.floor((double)x) - cartPos.getX() == Vector3i.getX() && Mth.floor((double)z) - cartPos.getZ() == Vector3i.getZ()) {
            cart.setPos(x, y + (double)Vector3i.getY(), z);
        } else if (Vector3i1.getY() != 0 && Mth.floor((double)x) - cartPos.getX() == Vector3i1.getX() && Mth.floor((double)z) - cartPos.getZ() == Vector3i1.getZ()) {
            cart.setPos(x, y + (double)Vector3i1.getY(), z);
        }
        x = cart.getX();
        y = cart.getY();
        z = cart.getZ();
        Vec3 Vector3d3 = cart.getPos(x, y, z);
        if (Vector3d3 != null && actualVec != null) {
            double d17 = (actualVec.y - Vector3d3.y) * 0.05;
            Vec3 Vector3d4 = cart.getDeltaMovement();
            double d18 = Math.sqrt(Vector3d4.horizontalDistanceSqr());
            if (d18 > 0.0) {
                cart.setDeltaMovement(Vector3d4.multiply((d18 + d17) / d18, 1.0, (d18 + d17) / d18));
            }
            cart.setPos(x, Vector3d3.y, z);
        }
        x = cart.getX();
        y = cart.getY();
        z = cart.getZ();
        int j = Mth.floor((double)x);
        int i = Mth.floor((double)z);
        if (j != cartPos.getX() || i != cartPos.getZ()) {
            Vec3 Vector3d5 = cart.getDeltaMovement();
            double d26 = Math.sqrt(Vector3d5.horizontalDistanceSqr());
            cart.setDeltaMovement(d26 * (double)(j - cartPos.getX()), Vector3d5.y, d26 * (double)(i - cartPos.getZ()));
        }
        cart.setDeltaMovement(previousMotion);
    }

    public static Vec3 getRailVec(RailShape shape) {
        switch (shape) {
            case ASCENDING_NORTH: 
            case ASCENDING_SOUTH: 
            case NORTH_SOUTH: {
                return new Vec3(0.0, 0.0, 1.0);
            }
            case ASCENDING_EAST: 
            case ASCENDING_WEST: 
            case EAST_WEST: {
                return new Vec3(1.0, 0.0, 0.0);
            }
            case NORTH_EAST: 
            case SOUTH_WEST: {
                return new Vec3(1.0, 0.0, 1.0).normalize();
            }
            case NORTH_WEST: 
            case SOUTH_EAST: {
                return new Vec3(1.0, 0.0, -1.0).normalize();
            }
        }
        return new Vec3(0.0, 1.0, 0.0);
    }
}
