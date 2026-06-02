/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.link.controller.LecternControllerBlock
 *  com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.lectern_controller;

import com.simibubi.create.content.redstone.link.controller.LecternControllerBlock;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.LecternControllerBlockEntityExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={LecternControllerBlock.class})
public class LecternControllerBlockMixin
implements BlockSubLevelAssemblyListener {
    @Override
    public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        BlockEntity blockEntity = originLevel.getBlockEntity(oldPos);
        if (blockEntity instanceof LecternControllerBlockEntity) {
            LecternControllerBlockEntity be = (LecternControllerBlockEntity)blockEntity;
            ((LecternControllerBlockEntityExtension)be).sable$setNoDrop();
        }
    }
}
