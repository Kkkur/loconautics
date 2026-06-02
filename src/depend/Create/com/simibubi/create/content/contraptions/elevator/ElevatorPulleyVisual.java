/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visual.SectionTrackedVisual$SectionCollector
 *  dev.engine_room.flywheel.api.visual.ShaderLightVisual
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  dev.engine_room.flywheel.lib.visual.util.InstanceRecycler
 *  it.unimi.dsi.fastutil.longs.LongArraySet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.simibubi.create.content.contraptions.elevator;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlock;
import com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyRenderer;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.simibubi.create.foundation.render.SpecialModels;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.SectionTrackedVisual;
import dev.engine_room.flywheel.api.visual.ShaderLightVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.util.InstanceRecycler;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class ElevatorPulleyVisual
extends ShaftVisual<ElevatorPulleyBlockEntity>
implements SimpleDynamicVisual,
ShaderLightVisual {
    private final InstanceRecycler<ScrollInstance> belt;
    private final ScrollInstance halfBelt;
    private final ScrollInstance coil;
    private final TransformedInstance magnet;
    private final Matrix4fc cachedMagnetTransform;
    private float lastOffset = Float.NaN;
    private final long topSection;
    private long lastBottomSection;

    public ElevatorPulleyVisual(VisualizationContext context, ElevatorPulleyBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        float blockStateAngle = AngleHelper.horizontalAngle((Direction)((Direction)this.blockState.getValue(ElevatorPulleyBlock.HORIZONTAL_FACING)));
        Quaternionf rotation = new Quaternionf().rotationY((float)Math.PI / 180 * blockStateAngle);
        this.topSection = SectionPos.of((BlockPos)this.pos).asLong();
        this.belt = new InstanceRecycler(() -> ElevatorPulleyVisual.lambda$new$0(context, (Quaternionfc)rotation));
        this.halfBelt = ((ScrollInstance)context.instancerProvider().instancer(AllInstanceTypes.SCROLLING, SpecialModels.flatLit(AllPartialModels.ELEVATOR_BELT_HALF)).createInstance()).rotation((Quaternionfc)rotation).setSpriteShift(AllSpriteShifts.ELEVATOR_BELT);
        this.coil = ((ScrollInstance)context.instancerProvider().instancer(AllInstanceTypes.SCROLLING, Models.partial((PartialModel)AllPartialModels.ELEVATOR_COIL)).createInstance()).position((Vec3i)this.getVisualPosition()).rotation((Quaternionfc)rotation).setSpriteShift(AllSpriteShifts.ELEVATOR_COIL);
        this.coil.setChanged();
        this.magnet = (TransformedInstance)context.instancerProvider().instancer(InstanceTypes.TRANSFORMED, SpecialModels.flatLit(AllPartialModels.ELEVATOR_MAGNET)).createInstance();
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)this.magnet.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).center()).rotateYDegrees(blockStateAngle)).uncenter();
        this.cachedMagnetTransform = new Matrix4f((Matrix4fc)this.magnet.pose);
        this.animate(PulleyRenderer.getBlockEntityOffset(partialTick, blockEntity));
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(new FlatLit[]{this.coil});
    }

    public void setSectionCollector(SectionTrackedVisual.SectionCollector sectionCollector) {
        super.setSectionCollector(sectionCollector);
        sectionCollector.sections(this.getLightSections(this.lastOffset));
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.animate(PulleyRenderer.getBlockEntityOffset(ctx.partialTick(), (PulleyBlockEntity)this.blockEntity));
    }

    @Override
    protected void _delete() {
        super._delete();
        this.belt.delete();
        this.halfBelt.delete();
        this.coil.delete();
        this.magnet.delete();
    }

    private void animate(float offset) {
        if (offset == this.lastOffset) {
            return;
        }
        this.lastOffset = offset;
        this.maybeUpdateSections(offset);
        this.animateCoil(offset);
        this.animateHalfBelt(offset);
        this.animateBelt(offset);
        this.animateMagnet(offset);
    }

    private void maybeUpdateSections(float offset) {
        if (this.lightSections == null) {
            return;
        }
        if (this.lastBottomSection == SectionPos.offset((long)this.topSection, (int)0, (int)(-ElevatorPulleyVisual.offset2SectionCount(offset)), (int)0)) {
            return;
        }
        this.lightSections.sections(this.getLightSections(offset));
    }

    private void animateMagnet(float offset) {
        ((TransformedInstance)this.magnet.setTransform(this.cachedMagnetTransform).translateY(-offset)).setChanged();
    }

    private void animateBelt(float offset) {
        this.belt.resetCount();
        int i = 0;
        while ((float)i < offset - 0.25f) {
            ScrollInstance segment = ((ScrollInstance)this.belt.get()).position((Vec3i)this.getVisualPosition()).shift(0.0f, -(offset - (float)i), 0.0f);
            segment.offsetV = offset;
            segment.setChanged();
            ++i;
        }
        this.belt.discardExtra();
    }

    private void animateHalfBelt(float offset) {
        float f = offset % 1.0f;
        if (f < 0.25f || f > 0.75f) {
            this.halfBelt.setVisible(true);
            this.halfBelt.position((Vec3i)this.getVisualPosition()).shift(0.0f, -(f > 0.75f ? f - 1.0f : f), 0.0f);
            this.halfBelt.offsetV = offset;
            this.halfBelt.setChanged();
        } else {
            this.halfBelt.setVisible(false);
        }
    }

    private void animateCoil(float offset) {
        this.coil.offsetV = -offset * 2.0f;
        this.coil.setChanged();
    }

    private LongSet getLightSections(float offset) {
        LongArraySet out = new LongArraySet();
        int sectionCount = ElevatorPulleyVisual.offset2SectionCount(offset);
        for (int i = 0; i < sectionCount; ++i) {
            out.add(SectionPos.offset((long)this.topSection, (int)0, (int)(-i), (int)0));
        }
        this.lastBottomSection = SectionPos.offset((long)this.topSection, (int)0, (int)(-sectionCount), (int)0);
        return out;
    }

    private static int offset2SectionCount(float offset) {
        return (int)Math.ceil((offset + 1.0f) / 16.0f);
    }

    private static /* synthetic */ ScrollInstance lambda$new$0(VisualizationContext context, Quaternionfc rotation) {
        return ((ScrollInstance)context.instancerProvider().instancer(AllInstanceTypes.SCROLLING, SpecialModels.flatLit(AllPartialModels.ELEVATOR_BELT)).createInstance()).rotation(rotation).setSpriteShift(AllSpriteShifts.ELEVATOR_BELT);
    }
}
