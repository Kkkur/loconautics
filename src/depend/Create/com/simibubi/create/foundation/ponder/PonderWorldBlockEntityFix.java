/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.foundation.ponder;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PonderWorldBlockEntityFix {
    public static void fixControllerBlockEntities(PonderLevel world) {
        for (BlockEntity blockEntity : world.getBlockEntities()) {
            if (blockEntity instanceof BeltBlockEntity) {
                BeltBlockEntity beltBlockEntity = (BeltBlockEntity)blockEntity;
                if (!beltBlockEntity.isController()) continue;
                BlockPos controllerPos = blockEntity.getBlockPos();
                for (BlockPos blockPos : BeltBlock.getBeltChain((LevelAccessor)world, controllerPos)) {
                    BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
                    if (!(blockEntity2 instanceof BeltBlockEntity)) continue;
                    BeltBlockEntity belt2 = (BeltBlockEntity)blockEntity2;
                    belt2.setController(controllerPos);
                }
            }
            if (!(blockEntity instanceof IMultiBlockEntityContainer)) continue;
            IMultiBlockEntityContainer multiBlockEntity = (IMultiBlockEntityContainer)blockEntity;
            BlockPos lastKnown = multiBlockEntity.getLastKnownPos();
            BlockPos current = blockEntity.getBlockPos();
            if (lastKnown == null || current == null || multiBlockEntity.isController() || lastKnown.equals((Object)current)) continue;
            BlockPos newControllerPos = multiBlockEntity.getController().offset((Vec3i)current.subtract((Vec3i)lastKnown));
            multiBlockEntity.setController(newControllerPos);
        }
    }
}
