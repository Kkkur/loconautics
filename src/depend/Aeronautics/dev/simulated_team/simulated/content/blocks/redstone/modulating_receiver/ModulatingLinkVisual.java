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
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3fc
 */
package dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver;

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
import dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver.ModulatingLinkedReceiverBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3fc;

public class ModulatingLinkVisual
extends AbstractBlockEntityVisual<ModulatingLinkedReceiverBlockEntity>
implements SimpleDynamicVisual {
    public static final float MAX_DISTANCE = 256.0f;
    public static final float SMOOTHING = 20.0f;
    private final Vector3d tempNormal = new Vector3d();
    private final TransformedInstance topPlate = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)SimPartialModels.MODULATING_RECEIVER_PLATE)).createInstance();
    private final TransformedInstance bottomPlate = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)SimPartialModels.MODULATING_RECEIVER_PLATE)).createInstance();
    private final Direction facing;

    public ModulatingLinkVisual(VisualizationContext ctx, ModulatingLinkedReceiverBlockEntity blockEntity, float partialTick) {
        super(ctx, (BlockEntity)blockEntity, partialTick);
        this.facing = (Direction)blockEntity.getBlockState().getValue((Property)BlockStateProperties.FACING);
        this.handleTransform();
    }

    public void beginFrame(DynamicVisual.Context context) {
        this.handleTransform();
    }

    private void handleTransform() {
        this.topPlate.setIdentityTransform();
        this.bottomPlate.setIdentityTransform();
        this.tempNormal.set((Vector3fc)this.facing.step()).mul(0.0625);
        float max = this.getMax();
        ((TransformedInstance)this.topPlate.translate((Vec3i)this.getVisualPosition())).translate(this.tempNormal.x() * (0.5 + (double)max), this.tempNormal.y() * (0.5 + (double)max), this.tempNormal.z() * (0.5 + (double)max));
        float min = this.getMin();
        ((TransformedInstance)this.bottomPlate.translate((Vec3i)this.getVisualPosition())).translate(this.tempNormal.x() * (double)min, this.tempNormal.y() * (double)min, this.tempNormal.z() * (double)min);
        if (this.facing.getAxis().isHorizontal()) {
            this.rotateInstanceHorizontally(this.topPlate, this.facing);
            this.rotateInstanceHorizontally(this.bottomPlate, this.facing);
        }
        this.rotateInstanceVertically(this.topPlate, this.facing);
        this.rotateInstanceVertically(this.bottomPlate, this.facing);
        this.topPlate.setChanged();
        this.bottomPlate.setChanged();
    }

    private void rotateInstanceHorizontally(TransformedInstance inst, Direction facing) {
        inst.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)facing.getOpposite())), Direction.UP);
    }

    private void rotateInstanceVertically(TransformedInstance inst, Direction facing) {
        inst.rotateCentered(AngleHelper.rad((double)(-90.0f - AngleHelper.verticalAngle((Direction)facing))), Direction.EAST);
    }

    private float getMin() {
        return 5.5f * ((float)(((ModulatingLinkedReceiverBlockEntity)this.blockEntity).minRange - 1) * 275.0f) / (255.0f * (20.0f + (float)((ModulatingLinkedReceiverBlockEntity)this.blockEntity).minRange - 1.0f));
    }

    private float getMax() {
        return 5.5f * ((float)(((ModulatingLinkedReceiverBlockEntity)this.blockEntity).maxRange - 1) * 275.0f) / (255.0f * (20.0f + (float)((ModulatingLinkedReceiverBlockEntity)this.blockEntity).maxRange - 1.0f));
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept((Instance)this.topPlate);
        consumer.accept((Instance)this.bottomPlate);
    }

    public void updateLight(float v) {
        this.relight(new FlatLit[]{this.topPlate});
        this.relight(new FlatLit[]{this.bottomPlate});
    }

    protected void _delete() {
        this.topPlate.delete();
        this.bottomPlate.delete();
    }
}
