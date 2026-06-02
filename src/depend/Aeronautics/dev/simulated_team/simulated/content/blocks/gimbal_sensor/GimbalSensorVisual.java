/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.AbstractInstance
 *  dev.engine_room.flywheel.lib.instance.ColoredLitInstance
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.OrientedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package dev.simulated_team.simulated.content.blocks.gimbal_sensor;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.ColoredLitInstance;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.SimDirectionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class GimbalSensorVisual
extends AbstractBlockEntityVisual<GimbalSensorBlockEntity>
implements SimpleDynamicVisual {
    private final List<OrientedInstance> indicators = new ArrayList<OrientedInstance>();
    private final OrientedInstance gimbal;
    private final OrientedInstance compass;
    private final OrientedInstance needle;
    private final List<ColoredLitInstance> allInstances = new ArrayList<ColoredLitInstance>();

    public GimbalSensorVisual(VisualizationContext ctx, GimbalSensorBlockEntity blockEntity, float partialTick) {
        super(ctx, (BlockEntity)blockEntity, partialTick);
        for (Direction dir : SimDirectionUtil.Y_AXIS_PLANE) {
            OrientedInstance inst = (OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)SimPartialModels.GIMBAL_SENSOR_INDICATOR)).createInstance();
            inst.position((Vec3i)this.getVisualPosition()).translatePivot(-0.5f, 0.0f, 0.0f).translatePosition(0.5f, 0.0f, 0.0f).rotateToFace(dir);
            this.indicators.add(inst);
        }
        this.gimbal = ((OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)SimPartialModels.GIMBAL_SENSOR_GIMBAL)).createInstance()).position((Vec3i)this.getVisualPosition()).translatePosition(0.5f, 0.5f, 0.5f).translatePivot(-0.5f, -0.5f, -0.5f);
        this.compass = ((OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)SimPartialModels.GIMBAL_SENSOR_COMPASS)).createInstance()).position((Vec3i)this.getVisualPosition()).translatePosition(0.5f, 0.5f, 0.5f).translatePivot(-0.5f, -0.5f, -0.5f);
        this.needle = ((OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)SimPartialModels.GIMBAL_SENSOR_NEEDLE)).createInstance()).position((Vec3i)this.getVisualPosition()).translatePosition(0.5f, 0.5f, 0.5f).translatePivot(-0.5f, -0.5f, -0.5f);
        this.allInstances.addAll(this.indicators);
        this.allInstances.add((ColoredLitInstance)this.gimbal);
        this.allInstances.add((ColoredLitInstance)this.compass);
        this.allInstances.add((ColoredLitInstance)this.needle);
    }

    public void beginFrame(DynamicVisual.Context context) {
        for (int i = 0; i < SimDirectionUtil.Y_AXIS_PLANE.length; ++i) {
            Direction dir = SimDirectionUtil.Y_AXIS_PLANE[i];
            OrientedInstance associatedInst = this.indicators.get(i);
            associatedInst.colorArgb(SimColors.redstone((float)Math.max(((GimbalSensorBlockEntity)this.blockEntity).getPower(dir), 0) / 15.0f)).setChanged();
        }
        this.handleRotations(context.partialTick());
    }

    private void handleRotations(float partialTicks) {
        this.gimbal.identityRotation();
        this.compass.identityRotation();
        this.needle.identityRotation();
        Quaternionf base = ((GimbalSensorBlockEntity)this.blockEntity).getBaseQuaternion();
        ((GimbalSensorBlockEntity)this.blockEntity).applyPrimaryQuaternion(base, partialTicks);
        this.gimbal.rotation((Quaternionfc)base);
        this.gimbal.setChanged();
        ((GimbalSensorBlockEntity)this.blockEntity).applySecondaryQuaternion(base, partialTicks);
        this.compass.rotation((Quaternionfc)base);
        this.compass.setChanged();
        ((GimbalSensorBlockEntity)this.blockEntity).applyCompassQuaternion(base, partialTicks);
        this.needle.rotation((Quaternionfc)base);
        this.needle.setChanged();
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        for (AbstractInstance abstractInstance : this.allInstances) {
            consumer.accept((Instance)abstractInstance);
        }
    }

    public void updateLight(float v) {
        for (ColoredLitInstance inst : this.allInstances) {
            this.relight(new FlatLit[]{inst});
        }
    }

    protected void _delete() {
        for (ColoredLitInstance inst : this.allInstances) {
            inst.delete();
        }
    }
}
