/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.chassis.StickerBlock
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.sticker;

import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.StickerBlockEntityExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={StickerBlock.class})
public class StickerBlockMixin
implements BlockSubLevelAssemblyListener {
    @Override
    public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        StickerBlockEntityExtension extension;
        BlockEntity blockEntity = originLevel.getBlockEntity(oldPos);
        if (blockEntity instanceof StickerBlockEntityExtension) {
            extension = (StickerBlockEntityExtension)blockEntity;
            extension.sable$removeConstraint();
        }
        if ((blockEntity = resultingLevel.getBlockEntity(newPos)) instanceof StickerBlockEntityExtension) {
            extension = (StickerBlockEntityExtension)blockEntity;
            extension.sable$removeConstraint();
        }
    }
}
