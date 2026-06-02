/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.SpriteShiftEntry
 */
package com.simibubi.create.content.contraptions.pulley;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.pulley.AbstractPulleyVisual;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyRenderer;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.SpriteShiftEntry;

public class RopePulleyVisual
extends AbstractPulleyVisual<PulleyBlockEntity> {
    public RopePulleyVisual(VisualizationContext context, PulleyBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }

    @Override
    protected Instancer<TransformedInstance> getRopeModel() {
        return this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ROPE));
    }

    @Override
    protected Instancer<TransformedInstance> getMagnetModel() {
        return this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.PULLEY_MAGNET));
    }

    @Override
    protected Instancer<TransformedInstance> getHalfMagnetModel() {
        return this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ROPE_HALF_MAGNET));
    }

    @Override
    protected Instancer<ScrollInstance> getCoilModel() {
        return this.instancerProvider().instancer(AllInstanceTypes.SCROLLING, Models.partial((PartialModel)AllPartialModels.ROPE_COIL));
    }

    @Override
    protected Instancer<TransformedInstance> getHalfRopeModel() {
        return this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ROPE_HALF));
    }

    @Override
    protected float getOffset(float pt) {
        return PulleyRenderer.getBlockEntityOffset(pt, (PulleyBlockEntity)this.blockEntity);
    }

    @Override
    protected boolean isRunning() {
        return PulleyRenderer.isPulleyRunning((PulleyBlockEntity)this.blockEntity);
    }

    @Override
    protected SpriteShiftEntry getCoilAnimation() {
        return AllSpriteShifts.ROPE_PULLEY_COIL;
    }
}
