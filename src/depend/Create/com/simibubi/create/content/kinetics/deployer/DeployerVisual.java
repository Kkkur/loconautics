/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visual.TickableVisual$Context
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.instance.FlatLit
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.OrientedInstance
 *  dev.engine_room.flywheel.lib.model.Models
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual
 *  dev.engine_room.flywheel.lib.visual.SimpleTickableVisual
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.simibubi.create.content.kinetics.deployer;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import java.util.function.Consumer;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class DeployerVisual
extends ShaftVisual<DeployerBlockEntity>
implements SimpleDynamicVisual,
SimpleTickableVisual {
    final Direction facing;
    final float yRot;
    final float xRot;
    final float zRot;
    protected final OrientedInstance pole;
    protected OrientedInstance hand;
    PartialModel currentHand;
    float progress;

    public DeployerVisual(VisualizationContext context, DeployerBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        this.facing = (Direction)this.blockState.getValue((Property)DirectionalKineticBlock.FACING);
        boolean rotatePole = (Boolean)this.blockState.getValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE) ^ this.facing.getAxis() == Direction.Axis.Z;
        this.yRot = AngleHelper.horizontalAngle((Direction)this.facing);
        this.xRot = this.facing == Direction.UP ? 270.0f : (this.facing == Direction.DOWN ? 90.0f : 0.0f);
        this.zRot = rotatePole ? 90.0f : 0.0f;
        this.pole = (OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)AllPartialModels.DEPLOYER_POLE)).createInstance();
        this.currentHand = ((DeployerBlockEntity)this.blockEntity).getHandPose();
        this.hand = (OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)this.currentHand)).createInstance();
        this.progress = this.getProgress(partialTick);
        DeployerVisual.updateRotation(this.pole, this.hand, this.yRot, this.xRot, this.zRot);
        this.updatePosition();
    }

    @Override
    public void tick(TickableVisual.Context context) {
        PartialModel handPose = ((DeployerBlockEntity)this.blockEntity).getHandPose();
        if (this.currentHand != handPose) {
            this.currentHand = handPose;
            this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial((PartialModel)this.currentHand)).stealInstance((Instance)this.hand);
        }
    }

    public void beginFrame(DynamicVisual.Context ctx) {
        float newProgress = this.getProgress(ctx.partialTick());
        if (Mth.equal((float)newProgress, (float)this.progress)) {
            return;
        }
        this.progress = newProgress;
        this.updatePosition();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        this.relight(new FlatLit[]{this.hand, this.pole});
    }

    @Override
    protected void _delete() {
        super._delete();
        this.hand.delete();
        this.pole.delete();
    }

    private float getProgress(float partialTicks) {
        if (((DeployerBlockEntity)this.blockEntity).state == DeployerBlockEntity.State.EXPANDING) {
            float f = 1.0f - ((float)((DeployerBlockEntity)this.blockEntity).timer - partialTicks * (float)((DeployerBlockEntity)this.blockEntity).getTimerSpeed()) / 1000.0f;
            if (((DeployerBlockEntity)this.blockEntity).fistBump) {
                f *= f;
            }
            return f;
        }
        if (((DeployerBlockEntity)this.blockEntity).state == DeployerBlockEntity.State.RETRACTING) {
            return ((float)((DeployerBlockEntity)this.blockEntity).timer - partialTicks * (float)((DeployerBlockEntity)this.blockEntity).getTimerSpeed()) / 1000.0f;
        }
        return 0.0f;
    }

    private void updatePosition() {
        float handLength = this.currentHand == AllPartialModels.DEPLOYER_HAND_POINTING ? 0.0f : (this.currentHand == AllPartialModels.DEPLOYER_HAND_HOLDING ? 0.25f : 0.1875f);
        float distance = Math.min(Mth.clamp((float)this.progress, (float)0.0f, (float)1.0f) * (((DeployerBlockEntity)this.blockEntity).reach + handLength), 1.3125f);
        Vec3i facingVec = this.facing.getNormal();
        BlockPos blockPos = this.getVisualPosition();
        float x = (float)blockPos.getX() + (float)facingVec.getX() * distance;
        float y = (float)blockPos.getY() + (float)facingVec.getY() * distance;
        float z = (float)blockPos.getZ() + (float)facingVec.getZ() * distance;
        this.pole.position(x, y, z).setChanged();
        this.hand.position(x, y, z).setChanged();
    }

    static void updateRotation(OrientedInstance pole, OrientedInstance hand, float yRot, float xRot, float zRot) {
        Quaternionf q = Axis.YP.rotationDegrees(yRot);
        q.mul((Quaternionfc)Axis.XP.rotationDegrees(xRot));
        hand.rotation((Quaternionfc)q).setChanged();
        q.mul((Quaternionfc)Axis.ZP.rotationDegrees(zRot));
        pole.rotation((Quaternionfc)q).setChanged();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept((Instance)this.pole);
        consumer.accept((Instance)this.hand);
    }
}
