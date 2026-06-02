/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.TickableVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleTickableVisual
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.observer;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.observer.TrackObserver;
import com.simibubi.create.content.trains.observer.TrackObserverBlockEntity;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TrackObserverVisual
extends AbstractBlockEntityVisual<TrackObserverBlockEntity>
implements SimpleTickableVisual {
    private final TransformedInstance overlay;
    private BlockPos oldTargetPos;

    public TrackObserverVisual(VisualizationContext ctx, TrackObserverBlockEntity blockEntity, float partialTick) {
        super(ctx, (BlockEntity)blockEntity, partialTick);
        this.overlay = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.TRACK_OBSERVER_OVERLAY)).createInstance();
        this.setupVisual();
    }

    public void tick(TickableVisual.Context context) {
        this.setupVisual();
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.overlay});
    }

    protected void _delete() {
        this.overlay.delete();
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept((Instance)this.overlay);
    }

    private void setupVisual() {
        TrackTargetingBehaviour<TrackObserver> target = ((TrackObserverBlockEntity)this.blockEntity).edgePoint;
        BlockPos targetPosition = target.getGlobalPosition();
        Level level = ((TrackObserverBlockEntity)this.blockEntity).getLevel();
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();
        if (!(block instanceof ITrackBlock)) {
            this.overlay.setZeroTransform().setChanged();
            return;
        }
        ITrackBlock trackBlock = (ITrackBlock)block;
        if (!targetPosition.equals((Object)this.oldTargetPos)) {
            this.oldTargetPos = targetPosition;
            this.overlay.setIdentityTransform().translate((Vec3i)targetPosition.subtract(this.renderOrigin()));
            TrackTargetingBehaviour.RenderedTrackOverlayType type = TrackTargetingBehaviour.RenderedTrackOverlayType.OBSERVER;
            trackBlock.prepareTrackOverlay(this.overlay, (BlockGetter)level, targetPosition, trackState, target.getTargetBezier(), target.getTargetDirection(), type);
            this.overlay.setChanged();
        }
    }
}
