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
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.packager;

import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagerRenderer;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PackagerVisual<T extends PackagerBlockEntity>
extends AbstractBlockEntityVisual<T>
implements SimpleDynamicVisual {
    public final TransformedInstance hatch;
    public final TransformedInstance tray;
    public float lastTrayOffset = Float.NaN;
    public PartialModel lastHatchPartial;

    public PackagerVisual(VisualizationContext ctx, T blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);
        this.lastHatchPartial = PackagerRenderer.getHatchModel(blockEntity);
        this.hatch = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)this.lastHatchPartial)).createInstance();
        this.tray = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)PackagerRenderer.getTrayModel(this.blockState))).createInstance();
        Direction facing = ((Direction)this.blockState.getValue((Property)PackagerBlock.FACING)).getOpposite();
        Vec3 lowerCorner = Vec3.atLowerCornerOf((Vec3i)facing.getNormal());
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)((TransformedInstance)this.hatch.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translate(lowerCorner.scale((double)0.49999f))).rotateYCenteredDegrees(AngleHelper.horizontalAngle((Direction)facing))).rotateXCenteredDegrees(AngleHelper.verticalAngle((Direction)facing))).setChanged();
        this.animate(partialTick);
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        this.animate(ctx.partialTick());
    }

    public void animate(float partialTick) {
        float trayOffset;
        PartialModel hatchPartial = PackagerRenderer.getHatchModel((PackagerBlockEntity)this.blockEntity);
        if (hatchPartial != this.lastHatchPartial) {
            this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)hatchPartial)).stealInstance((Instance)this.hatch);
            this.lastHatchPartial = hatchPartial;
        }
        if ((trayOffset = ((PackagerBlockEntity)this.blockEntity).getTrayOffset(partialTick)) != this.lastTrayOffset) {
            Direction facing = ((Direction)this.blockState.getValue((Property)PackagerBlock.FACING)).getOpposite();
            Vec3 lowerCorner = Vec3.atLowerCornerOf((Vec3i)facing.getNormal());
            ((TransformedInstance)((TransformedInstance)((TransformedInstance)this.tray.setIdentityTransform().translate((Vec3i)this.getVisualPosition())).translate(lowerCorner.scale((double)trayOffset))).rotateYCenteredDegrees(facing.toYRot())).setChanged();
            this.lastTrayOffset = trayOffset;
        }
    }

    public void updateLight(float partialTick) {
        this.relight(new FlatLit[]{this.hatch, this.tray});
    }

    protected void _delete() {
        this.hatch.delete();
        this.tray.delete();
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
    }
}
