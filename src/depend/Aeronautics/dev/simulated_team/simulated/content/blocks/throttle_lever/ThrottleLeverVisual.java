/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock
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
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.throttle_lever;

import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
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
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.util.SimColors;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class ThrottleLeverVisual
extends AbstractBlockEntityVisual<ThrottleLeverBlockEntity>
implements SimpleDynamicVisual {
    private final TransformedInstance diode = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)SimPartialModels.THROTTLE_LEVER_DIODE)).createInstance();
    private final TransformedInstance handle = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)SimPartialModels.THROTTLE_LEVER_HANDLE)).createInstance();
    private final TransformedInstance button = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)SimPartialModels.THROTTLE_LEVER_BUTTON)).createInstance();
    private final AttachFace attached;
    private final Direction facing;

    public ThrottleLeverVisual(VisualizationContext ctx, ThrottleLeverBlockEntity blockEntity, float partialTick) {
        super(ctx, (BlockEntity)blockEntity, partialTick);
        this.attached = (AttachFace)blockEntity.getBlockState().getValue((Property)AnalogLeverBlock.FACE);
        this.facing = (Direction)blockEntity.getBlockState().getValue((Property)AnalogLeverBlock.FACING);
        this.diode.colorArgb(SimColors.redstone(Math.max(0.0f, (float)blockEntity.state / 15.0f)));
        this.transformAll(partialTick);
    }

    public void beginFrame(DynamicVisual.Context context) {
        this.diode.colorArgb(SimColors.redstone(Math.max(0.0f, (float)((ThrottleLeverBlockEntity)this.blockEntity).state / 15.0f)));
        this.transformAll(context.partialTick());
    }

    private void transformAll(float partialTicks) {
        this.diode.setIdentityTransform();
        this.handle.setIdentityTransform();
        this.button.setIdentityTransform();
        this.initialTransform(this.handle);
        this.initialTransform(this.button);
        this.initialTransform(this.diode);
        double buttonAngle = ((ThrottleLeverBlockEntity)this.blockEntity).clientPressedLerp.getValue(partialTicks) * -7.0f;
        float angle = (float)(((double)(((ThrottleLeverBlockEntity)this.blockEntity).clientAngle.getValue(partialTicks) / 15.0f) * 80.0 - 40.0) / 180.0 * Math.PI);
        if (this.attached == AttachFace.WALL) {
            angle = -angle;
        }
        this.transformHandle(this.handle, angle, this.attached);
        this.transformHandle(this.button, angle, this.attached);
        ((TransformedInstance)this.button.translate(0.0f, 0.875f, 0.5f).rotateXDegrees((float)buttonAngle)).translateBack(0.0f, 0.875f, 0.5f);
        this.diode.setChanged();
        this.handle.setChanged();
        this.button.setChanged();
    }

    private void initialTransform(TransformedInstance instance) {
        instance.translate((Vec3i)this.getVisualPosition());
        float rX = switch (this.attached) {
            case AttachFace.FLOOR -> 0.0f;
            case AttachFace.WALL -> 90.0f;
            default -> 180.0f;
        };
        float rY = AngleHelper.horizontalAngle((Direction)this.facing);
        instance.rotateCentered((float)((double)(rY / 180.0f) * Math.PI), Direction.UP);
        instance.rotateCentered((float)((double)(rX / 180.0f) * Math.PI), Direction.EAST);
        instance.rotateCentered(this.attached == AttachFace.CEILING ? (float)Math.PI : 0.0f, Direction.UP);
    }

    private void transformHandle(TransformedInstance instance, float angle, AttachFace face) {
        ((TransformedInstance)instance.translate(0.5f, 0.1875f, 0.5f).rotateX(angle).translateBack(0.5f, 0.1875f, 0.5f)).rotateCentered(face == AttachFace.WALL ? (float)Math.PI : 0.0f, Direction.UP);
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept((Instance)this.handle);
        consumer.accept((Instance)this.button);
        consumer.accept((Instance)this.diode);
    }

    public void updateLight(float v) {
        this.relight(new FlatLit[]{this.handle});
        this.relight(new FlatLit[]{this.button});
        this.relight(new FlatLit[]{this.diode});
    }

    protected void _delete() {
        this.handle.delete();
        this.button.delete();
        this.diode.delete();
    }
}
