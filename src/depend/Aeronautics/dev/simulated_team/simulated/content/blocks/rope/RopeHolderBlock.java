/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.simulated_team.simulated.content.blocks.rope;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachment;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachmentPoint;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerRopeStrand;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface RopeHolderBlock<T extends SmartBlockEntity>
extends BlockSubLevelAssemblyListener,
IBE<T> {
    public static <T extends SmartBlockEntity> ItemInteractionResult shearRope(RopeHolderBlock<T> block, Level level, BlockPos pos, ServerPlayer player) {
        return block.onBlockEntityUseItemOn((BlockGetter)level, pos, be -> {
            RopeStrandHolderBehavior ropeHolder = block.getHolder(be);
            ServerRopeStrand strand = ropeHolder.getAttachedStrand();
            if (strand == null) {
                return ItemInteractionResult.FAIL;
            }
            RopeAttachment ropeAttachment = strand.getAttachment(RopeAttachmentPoint.START);
            if (ropeAttachment == null) {
                return ItemInteractionResult.FAIL;
            }
            BlockPos attachment = ropeAttachment.blockAttachment();
            BlockEntity blockEntity = level.getBlockEntity(attachment);
            if (!(blockEntity instanceof SmartBlockEntity)) {
                return ItemInteractionResult.FAIL;
            }
            SmartBlockEntity smartBlockEntity = (SmartBlockEntity)blockEntity;
            RopeStrandHolderBehavior otherHolder = (RopeStrandHolderBehavior)smartBlockEntity.getBehaviour(RopeStrandHolderBehavior.TYPE);
            if (otherHolder == null) {
                return ItemInteractionResult.FAIL;
            }
            otherHolder.destroyRope(player, pos.getCenter());
            return ItemInteractionResult.SUCCESS;
        });
    }

    default public RopeStrandHolderBehavior getHolder(T blockEntity) {
        return (RopeStrandHolderBehavior)blockEntity.getBehaviour(RopeStrandHolderBehavior.TYPE);
    }

    default public void afterMove(ServerLevel originLevel, ServerLevel serverLevel, BlockState blockState, BlockPos oldPos, BlockPos newPos) {
        AtomicReference ownedStrand = new AtomicReference();
        this.withBlockEntityDo((BlockGetter)originLevel, oldPos, be -> {
            RopeStrandHolderBehavior holder = this.getHolder(be);
            ownedStrand.set(holder.getOwnedStrand());
            holder.detachRope();
        });
        this.withBlockEntityDo((BlockGetter)serverLevel, newPos, be -> {
            ServerRopeStrand strand;
            RopeStrandHolderBehavior holder = this.getHolder(be);
            if (ownedStrand.get() != null && holder.ownsRope()) {
                holder.takeOwnedStrand((ServerRopeStrand)((Object)((Object)ownedStrand.get())));
            }
            if ((strand = holder.getAttachedStrand()) != null) {
                strand.getTrackingPlayers().clear();
                SubLevel newSubLevel = Sable.HELPER.getContaining((Level)serverLevel, (Vec3i)newPos);
                UUID newSubLevelId = newSubLevel != null ? newSubLevel.getUniqueId() : null;
                RopeAttachmentPoint point = holder.ownsRope() ? RopeAttachmentPoint.START : RopeAttachmentPoint.END;
                RopeAttachment attachment = new RopeAttachment(point, newSubLevelId, newPos);
                strand.addAttachment(serverLevel, point, attachment);
            }
        });
    }
}
