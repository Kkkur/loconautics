/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.display_link;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={DisplayLinkBlock.class})
public class DisplayLinkBlockMixin
implements BlockSubLevelAssemblyListener {
    @Override
    public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        BlockEntity blockEntity = originLevel.getBlockEntity(oldPos);
        if (blockEntity instanceof DisplayLinkBlockEntity) {
            DisplayLinkBlockEntity be = (DisplayLinkBlockEntity)blockEntity;
            blockEntity = resultingLevel.getBlockEntity(newPos);
            if (blockEntity instanceof DisplayLinkBlockEntity) {
                DisplayLinkBlockEntity newBe = (DisplayLinkBlockEntity)blockEntity;
                newBe.target(be.getTargetPosition());
            }
        }
    }
}
