/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.kinetics.crafter;

import com.simibubi.create.content.kinetics.crafter.ConnectedInputHandler;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CrafterHelper {
    public static MechanicalCrafterBlockEntity getCrafter(BlockAndTintGetter reader, BlockPos pos) {
        BlockEntity blockEntity = reader.getBlockEntity(pos);
        if (!(blockEntity instanceof MechanicalCrafterBlockEntity)) {
            return null;
        }
        return (MechanicalCrafterBlockEntity)blockEntity;
    }

    public static ConnectedInputHandler.ConnectedInput getInput(BlockAndTintGetter reader, BlockPos pos) {
        MechanicalCrafterBlockEntity crafter = CrafterHelper.getCrafter(reader, pos);
        return crafter == null ? null : crafter.input;
    }

    public static boolean areCraftersConnected(BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos) {
        ConnectedInputHandler.ConnectedInput input1 = CrafterHelper.getInput(reader, pos);
        ConnectedInputHandler.ConnectedInput input2 = CrafterHelper.getInput(reader, otherPos);
        if (input1 == null || input2 == null) {
            return false;
        }
        if (input1.data.isEmpty() || input2.data.isEmpty()) {
            return false;
        }
        try {
            if (pos.offset((Vec3i)input1.data.get(0)).equals((Object)otherPos.offset((Vec3i)input2.data.get(0)))) {
                return true;
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            // empty catch block
        }
        return false;
    }
}
