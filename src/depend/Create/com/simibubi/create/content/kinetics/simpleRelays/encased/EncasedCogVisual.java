/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.model.Model
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.simpleRelays.encased;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.function.Consumer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class EncasedCogVisual
extends KineticBlockEntityVisual<KineticBlockEntity> {
    private final boolean large;
    protected final RotatingInstance rotatingModel;
    @Nullable
    protected final RotatingInstance rotatingTopShaft;
    @Nullable
    protected final RotatingInstance rotatingBottomShaft;

    public static EncasedCogVisual small(VisualizationContext modelManager, KineticBlockEntity blockEntity, float partialTick) {
        return new EncasedCogVisual(modelManager, blockEntity, false, partialTick, Models.partial((PartialModel)AllPartialModels.SHAFTLESS_COGWHEEL));
    }

    public static EncasedCogVisual large(VisualizationContext modelManager, KineticBlockEntity blockEntity, float partialTick) {
        return new EncasedCogVisual(modelManager, blockEntity, true, partialTick, Models.partial((PartialModel)AllPartialModels.SHAFTLESS_LARGE_COGWHEEL));
    }

    public EncasedCogVisual(VisualizationContext modelManager, KineticBlockEntity blockEntity, boolean large, float partialTick, Model model) {
        super(modelManager, blockEntity, partialTick);
        this.large = large;
        this.rotatingModel = (RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, model).createInstance();
        this.rotatingModel.setup(blockEntity).setPosition((Vec3i)this.getVisualPosition()).rotateToFace(this.rotationAxis()).setChanged();
        RotatingInstance rotatingTopShaft = null;
        RotatingInstance rotatingBottomShaft = null;
        Block block = this.blockState.getBlock();
        if (block instanceof IRotate) {
            IRotate def = (IRotate)block;
            for (Direction d : Iterate.directionsInAxis((Direction.Axis)this.rotationAxis())) {
                if (!def.hasShaftTowards((LevelReader)blockEntity.getLevel(), blockEntity.getBlockPos(), this.blockState, d)) continue;
                RotatingInstance instance = (RotatingInstance)this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial((PartialModel)AllPartialModels.SHAFT_HALF)).createInstance();
                instance.setup(blockEntity).setPosition((Vec3i)this.getVisualPosition()).rotateToFace(Direction.SOUTH, d).setChanged();
                if (large) {
                    instance.setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(this.rotationAxis(), this.pos));
                }
                if (d.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                    rotatingTopShaft = instance;
                    continue;
                }
                rotatingBottomShaft = instance;
            }
        }
        this.rotatingTopShaft = rotatingTopShaft;
        this.rotatingBottomShaft = rotatingBottomShaft;
    }

    public void update(float pt) {
        this.rotatingModel.setup((KineticBlockEntity)this.blockEntity).setChanged();
        if (this.rotatingTopShaft != null) {
            this.rotatingTopShaft.setup((KineticBlockEntity)this.blockEntity).setChanged();
        }
        if (this.rotatingBottomShaft != null) {
            this.rotatingBottomShaft.setup((KineticBlockEntity)this.blockEntity).setChanged();
        }
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.rotatingModel, this.rotatingTopShaft, this.rotatingBottomShaft});
    }

    protected void _delete() {
        this.rotatingModel.delete();
        if (this.rotatingTopShaft != null) {
            this.rotatingTopShaft.delete();
        }
        if (this.rotatingBottomShaft != null) {
            this.rotatingBottomShaft.delete();
        }
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept((Instance)this.rotatingModel);
        consumer.accept((Instance)this.rotatingTopShaft);
        consumer.accept((Instance)this.rotatingBottomShaft);
    }
}
