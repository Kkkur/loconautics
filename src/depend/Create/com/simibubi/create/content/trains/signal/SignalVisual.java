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
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.signal;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBoundary;
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
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SignalVisual
extends AbstractBlockEntityVisual<SignalBlockEntity>
implements SimpleTickableVisual {
    private final TransformedInstance signalLight;
    private final TransformedInstance signalOverlay;
    private boolean previousIsRedLight;
    private SignalBlockEntity.OverlayState previousOverlayState;

    public SignalVisual(VisualizationContext ctx, SignalBlockEntity blockEntity, float partialTick) {
        super(ctx, (BlockEntity)blockEntity, partialTick);
        this.signalLight = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.SIGNAL_OFF)).createInstance();
        this.signalOverlay = (TransformedInstance)ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.TRACK_SIGNAL_OVERLAY)).createInstance();
        this.setupVisual();
    }

    public void tick(TickableVisual.Context context) {
        this.setupVisual();
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.signalLight, this.signalOverlay});
    }

    protected void _delete() {
        this.signalLight.delete();
        this.signalOverlay.delete();
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept((Instance)this.signalLight);
    }

    private void setupVisual() {
        ITrackBlock trackBlock;
        BlockState trackState;
        Level level;
        BlockPos targetPosition;
        TrackTargetingBehaviour<SignalBoundary> target;
        SignalBlockEntity.OverlayState overlayState;
        block9: {
            block8: {
                float renderTime;
                SignalBlockEntity.SignalState signalState = ((SignalBlockEntity)this.blockEntity).getState();
                boolean isRedLight = signalState.isRedLight(renderTime = AnimationTickHolder.getRenderTime((LevelAccessor)((SignalBlockEntity)this.blockEntity).getLevel()));
                if (isRedLight != this.previousIsRedLight) {
                    PartialModel partial = isRedLight ? AllPartialModels.SIGNAL_ON : AllPartialModels.SIGNAL_OFF;
                    this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)partial)).stealInstance((Instance)this.signalLight);
                }
                this.signalLight.setIdentityTransform().translate((Vec3i)this.getVisualPosition());
                if (isRedLight) {
                    this.signalLight.light(240);
                }
                this.signalLight.setChanged();
                this.previousIsRedLight = isRedLight;
                overlayState = ((SignalBlockEntity)this.blockEntity).getOverlay();
                target = ((SignalBlockEntity)this.blockEntity).edgePoint;
                targetPosition = target.getGlobalPosition();
                level = ((SignalBlockEntity)this.blockEntity).getLevel();
                trackState = level.getBlockState(targetPosition);
                Block block = trackState.getBlock();
                if (!(block instanceof ITrackBlock)) break block8;
                trackBlock = (ITrackBlock)block;
                if (overlayState != SignalBlockEntity.OverlayState.SKIP) break block9;
            }
            this.previousOverlayState = null;
            this.signalOverlay.setZeroTransform().setChanged();
            return;
        }
        if (overlayState != this.previousOverlayState) {
            PartialModel partial;
            TrackTargetingBehaviour.RenderedTrackOverlayType type;
            this.previousOverlayState = overlayState;
            if (overlayState == SignalBlockEntity.OverlayState.DUAL) {
                type = TrackTargetingBehaviour.RenderedTrackOverlayType.DUAL_SIGNAL;
                partial = AllPartialModels.TRACK_SIGNAL_DUAL_OVERLAY;
            } else {
                type = TrackTargetingBehaviour.RenderedTrackOverlayType.SIGNAL;
                partial = AllPartialModels.TRACK_SIGNAL_OVERLAY;
            }
            this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)partial)).stealInstance((Instance)this.signalOverlay);
            this.signalOverlay.setIdentityTransform().translate((Vec3i)targetPosition.subtract(this.renderOrigin()));
            trackBlock.prepareTrackOverlay(this.signalOverlay, (BlockGetter)level, targetPosition, trackState, target.getTargetBezier(), target.getTargetDirection(), type);
            this.signalOverlay.setChanged();
        }
    }
}
