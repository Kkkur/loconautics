/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.hats;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlock;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class EntityHats {
    @Nullable
    public static PartialModel getHatFor(LivingEntity entity) {
        if (entity == null) {
            return null;
        }
        ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (!headItem.isEmpty()) {
            return null;
        }
        if (EntityHats.shouldRenderTrainHat(entity)) {
            return AllPartialModels.TRAIN_HAT;
        }
        return EntityHats.getLogisticsHatFor(entity);
    }

    public static PartialModel getLogisticsHatFor(LivingEntity entity) {
        if (!entity.isPassenger()) {
            return null;
        }
        Entity entity2 = entity.getVehicle();
        if (!(entity2 instanceof SeatEntity)) {
            return null;
        }
        SeatEntity cce = (SeatEntity)entity2;
        int stations = 0;
        Level level = entity.level();
        BlockPos pos = entity.blockPosition();
        PartialModel hat = null;
        for (Direction d : Iterate.horizontalDirections) {
            for (int y : Iterate.zeroAndOne) {
                StockTickerBlock lw;
                PartialModel hatOfStation;
                Block block = level.getBlockState(pos.relative(d).above(y)).getBlock();
                if (!(block instanceof StockTickerBlock) || (hatOfStation = (lw = (StockTickerBlock)block).getHat((LevelAccessor)level, pos, entity)) == null) continue;
                hat = hatOfStation;
                ++stations;
            }
        }
        if (stations == 1) {
            return hat;
        }
        return null;
    }

    public static boolean shouldRenderTrainHat(LivingEntity entity) {
        if (entity.getPersistentData().contains("TrainHat")) {
            return true;
        }
        if (!entity.isPassenger()) {
            return false;
        }
        Entity entity2 = entity.getVehicle();
        if (!(entity2 instanceof CarriageContraptionEntity)) {
            return false;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)entity2;
        if (!cce.hasSchedule() && !(entity instanceof Player)) {
            return false;
        }
        Contraption contraption = cce.getContraption();
        if (!(contraption instanceof CarriageContraption)) {
            return false;
        }
        CarriageContraption cc = (CarriageContraption)contraption;
        BlockPos seatOf = cc.getSeatOf(entity.getUUID());
        if (seatOf == null) {
            return false;
        }
        Couple<Boolean> validSides = cc.conductorSeats.get(seatOf);
        return validSides != null;
    }
}
