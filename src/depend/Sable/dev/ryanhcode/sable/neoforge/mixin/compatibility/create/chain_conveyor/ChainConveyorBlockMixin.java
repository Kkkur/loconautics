/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlock
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.chain_conveyor;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlock;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={ChainConveyorBlock.class})
public class ChainConveyorBlockMixin
implements BlockSubLevelAssemblyListener {
    @Override
    public void beforeMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        BlockEntity blockEntity = originLevel.getBlockEntity(oldPos);
        if (blockEntity instanceof ChainConveyorBlockEntity) {
            ChainConveyorBlockEntity be = (ChainConveyorBlockEntity)blockEntity;
            be.notifyConnectedToValidate();
        }
    }

    @Override
    public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        BlockEntity blockEntity = resultingLevel.getBlockEntity(newPos);
        if (blockEntity instanceof ChainConveyorBlockEntity) {
            ChainConveyorBlockEntity be = (ChainConveyorBlockEntity)blockEntity;
            be.checkInvalid = true;
        }
    }
}
