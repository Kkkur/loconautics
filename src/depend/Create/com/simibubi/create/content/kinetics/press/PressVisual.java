/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.OrientedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.simibubi.create.content.kinetics.press;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class PressVisual
extends ShaftVisual<MechanicalPressBlockEntity>
implements SimpleDynamicVisual {
    private final OrientedInstance pressHead = (OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)AllPartialModels.MECHANICAL_PRESS_HEAD)).createInstance();

    public PressVisual(VisualizationContext context, MechanicalPressBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        Quaternionf q = Axis.YP.rotationDegrees(AngleHelper.horizontalAngle((Direction)((Direction)this.blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING))));
        this.pressHead.rotation((Quaternionfc)q);
        this.transformModels(partialTick);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.transformModels(ctx.partialTick());
    }

    private void transformModels(float pt) {
        float renderedHeadOffset = this.getRenderedHeadOffset(pt);
        this.pressHead.position((Vec3i)this.getVisualPosition()).translatePosition(0.0f, -renderedHeadOffset, 0.0f).setChanged();
    }

    private float getRenderedHeadOffset(float pt) {
        PressingBehaviour pressingBehaviour = ((MechanicalPressBlockEntity)this.blockEntity).getPressingBehaviour();
        return pressingBehaviour.getRenderedHeadOffset(pt) * pressingBehaviour.mode.headOffset;
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(new FlatLit[]{this.pressHead});
    }

    @Override
    protected void _delete() {
        super._delete();
        this.pressHead.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.pressHead);
    }
}
