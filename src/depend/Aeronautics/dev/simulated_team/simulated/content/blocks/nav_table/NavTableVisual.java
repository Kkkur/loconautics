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
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package dev.simulated_team.simulated.content.blocks.nav_table;

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
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlock;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.SimDirectionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class NavTableVisual
extends AbstractBlockEntityVisual<NavTableBlockEntity>
implements SimpleDynamicVisual {
    private final Vector3f tempVec = new Vector3f();
    private final List<InstanceDirectionHolder> redstoneInstances = new ArrayList<InstanceDirectionHolder>();
    private final TransformedInstance pointer;

    public NavTableVisual(VisualizationContext ctx, NavTableBlockEntity navBE, float partialTick) {
        super(ctx, (BlockEntity)navBE, partialTick);
        Direction facing = (Direction)navBE.getBlockState().getValue((Property)NavTableBlock.FACING);
        Quaternionf facingRot = facing.getRotation();
        for (int i = 0; i < 4; ++i) {
            TransformedInstance inst = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)SimPartialModels.NAV_TABLE_INDICATOR)).createInstance();
            Direction dir = SimDirectionUtil.Y_AXIS_PLANE[i];
            ((TransformedInstance)((TransformedInstance)inst.translate((Vec3i)this.getVisualPosition())).center()).rotate((Quaternionfc)facingRot);
            inst.translate(0.0, -0.5, 0.0);
            inst.rotateToFace(dir);
            inst.translate(0.0, 0.0, 0.5);
            facingRot.transform((float)dir.getStepX(), (float)dir.getStepY(), (float)dir.getStepZ(), this.tempVec);
            Direction logicalDirection = Direction.getNearest((float)this.tempVec.x, (float)this.tempVec.y, (float)this.tempVec.z);
            inst.colorRgb(SimColors.redstone(navBE.isPowering ? (float)Math.max(navBE.getRedstoneStrength(logicalDirection), 0) / 15.0f : 0.0f));
            this.redstoneInstances.add(new InstanceDirectionHolder(inst, logicalDirection));
        }
        this.pointer = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial((PartialModel)SimPartialModels.NAV_TABLE_POINTER)).createInstance();
        this.translatePointer(partialTick);
    }

    private void translatePointer(float partialTick) {
        ((TransformedInstance)((TransformedInstance)((TransformedInstance)this.pointer.translate((Vec3i)this.getVisualPosition())).center()).rotate((Quaternionfc)((Direction)((NavTableBlockEntity)this.blockEntity).getBlockState().getValue((Property)BlockStateProperties.FACING)).getRotation()).translate(0.0, 0.3, 0.0)).rotateY((float)((double)((NavTableBlockEntity)this.blockEntity).getClientTargetAngle(partialTick) - 1.5707963267948966));
    }

    public void beginFrame(DynamicVisual.Context context) {
        for (InstanceDirectionHolder holder : this.redstoneInstances) {
            holder.instance().colorRgb(SimColors.redstone(((NavTableBlockEntity)this.blockEntity).isPowering ? (float)Math.max(((NavTableBlockEntity)this.blockEntity).getRedstoneStrength(holder.logicalDirection()), 0) / 15.0f : 0.0f)).setChanged();
        }
        this.pointer.setIdentityTransform();
        this.translatePointer(context.partialTick());
        this.pointer.setChanged();
    }

    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        for (InstanceDirectionHolder holder : this.redstoneInstances) {
            consumer.accept((Instance)holder.instance());
        }
        consumer.accept((Instance)this.pointer);
    }

    public void updateLight(float v) {
        for (InstanceDirectionHolder holder : this.redstoneInstances) {
            this.relight(new FlatLit[]{holder.instance()});
        }
        this.relight(new FlatLit[]{this.pointer});
    }

    protected void _delete() {
        for (InstanceDirectionHolder holder : this.redstoneInstances) {
            holder.instance().delete();
        }
        this.pointer.delete();
        this.redstoneInstances.clear();
    }

    private record InstanceDirectionHolder(TransformedInstance instance, Direction logicalDirection) {
    }
}
