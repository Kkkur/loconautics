/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.Rotate
 *  dev.engine_room.flywheel.lib.transform.Translate
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone.analogLever;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Rotate;
import dev.engine_room.flywheel.lib.transform.Translate;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;

public class AnalogLeverVisual
extends AbstractBlockEntityVisual<AnalogLeverBlockEntity>
implements SimpleDynamicVisual {
    protected final TransformedInstance handle = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ANALOG_LEVER_HANDLE)).createInstance();
    protected final TransformedInstance indicator = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)AllPartialModels.ANALOG_LEVER_INDICATOR)).createInstance();
    final float rX;
    final float rY;

    public AnalogLeverVisual(VisualizationContext context, AnalogLeverBlockEntity blockEntity, float partialTick) {
        super(context, (BlockEntity)blockEntity, partialTick);
        AttachFace face = (AttachFace)this.blockState.getValue((Property)AnalogLeverBlock.FACE);
        this.rX = face == AttachFace.FLOOR ? 0.0f : (face == AttachFace.WALL ? 90.0f : 180.0f);
        this.rY = AngleHelper.horizontalAngle((Direction)((Direction)this.blockState.getValue((Property)AnalogLeverBlock.FACING)));
        this.transform(this.indicator.setIdentityTransform());
        this.animateLever(partialTick);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        if (!((AnalogLeverBlockEntity)this.blockEntity).clientState.settled()) {
            this.animateLever(ctx.partialTick());
        }
    }

    protected void animateLever(float pt) {
        float state = ((AnalogLeverBlockEntity)this.blockEntity).clientState.getValue(pt);
        this.indicator.colorRgb(Color.mixColors((int)2884352, (int)0xCD0000, (float)(state / 15.0f)));
        this.indicator.setChanged();
        float angle = (float)((double)(state / 15.0f * 90.0f / 180.0f) * Math.PI);
        ((TransformedInstance)this.transform(this.handle.setIdentityTransform()).translate(0.5f, 0.0625f, 0.5f).rotate(angle, Direction.EAST)).translate(-0.5f, -0.0625f, -0.5f).setChanged();
    }

    protected void _delete() {
        this.handle.delete();
        this.indicator.delete();
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.handle, this.indicator});
    }

    private <T extends Translate<T> & Rotate<T>> T transform(T msr) {
        return (T)((Translate)((Rotate)((Translate)((Rotate)msr.translate((Vec3i)this.getVisualPosition()).center()).rotate((float)((double)(this.rY / 180.0f) * Math.PI), Direction.UP))).rotate((float)((double)(this.rX / 180.0f) * Math.PI), Direction.EAST)).uncenter();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept((Instance)this.handle);
        consumer.accept((Instance)this.indicator);
    }
}
